// --- file: gov/llnl/rtk/response/deposition/DepositionCalculator.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.rtk.physics.Material;
import gov.llnl.rtk.physics.MaterialComponent;
import gov.llnl.rtk.physics.Nuclide;
import gov.llnl.rtk.physics.PhotonCrossSectionLibrary;
import gov.llnl.rtk.physics.PhotonCrossSections;
import gov.llnl.rtk.physics.PhotonCrossSectionsEvaluator;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.physics.ScatteringDistribution;
import gov.llnl.rtk.physics.Units;
import gov.llnl.rtk.physics.PhysicalProperty;
import gov.llnl.rtk.physics.Xray;
import gov.llnl.rtk.physics.XrayData;
import gov.llnl.rtk.physics.XrayEdge;
import gov.llnl.rtk.physics.XrayLibrary;
import static gov.llnl.rtk.response.deposition.DepositionUtility.simpsonEscapeProbability;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class DepositionCalculator
{

  final static int N_CHORDS = 101; //optimized for average mean free path lengths
  final static int N_CHORDS2 = 21; //optimized for average mean free path lengths
  final static int N_CHORDS3 = 51;

  // Using the 4-node Gauss-Legendre or mid-ring approach for the back hemisphere
  // Angles: 175, 160, 135, 105 (cosines: -0.996, -0.940, -0.707, -0.259)
  final static double[] escapeCosNodes =
  {
    -0.996, -0.940, -0.707, -0.259
  };
  final static double[] escapeWeights =
  {
    0.015, 0.118, 0.366, 0.500
  }; // d(cos theta) escapeWeights

  private final Material material;
  private final Quantity density;
  private final SpatialChordQF spatial;
  private final AngularChordQF angular;
  private final IsotropicChordQF isotropic;
  final ScatteringDistribution scatterDistribution;

  // External libraries
  private PhotonCrossSectionLibrary photonLibrary;
  private XrayLibrary xrayLibrary; // = NISTXrayLibrary.getInstance();

  public double[] chords1 = new double[N_CHORDS];  // first scatter 
  public double[] chords2 = new double[N_CHORDS2];  // second scatter
  final double[][] escapeChords = new double[escapeCosNodes.length][N_CHORDS3];

  final static int BINS = 6000;
  public double[] inflight = new double[BINS];
  public double[] deposited = new double[BINS];
//  public double[] partialPhoto = new double[BINS];
//  public double[] partialPair = new double[BINS];
//  public double[] partialScatter = new double[BINS];
//  public double[] partialEscape = new double[BINS];
  public List<double[]> chordAudit = new ArrayList<>();

  Units distanceUnits = PhysicalProperty.LENGTH;
  EnergyScale energyScale = EnergyScaleFactory.newLinearScale(0, BINS, BINS);
  boolean cache = false;

  MultiScatterCalculator multi;
  final ScatteringDistribution.Evaluator scatterEvaluator;

  PhotonCrossSections materialCrossSections;
  PhotonCrossSectionsEvaluator crossSectionsEvaluator;
  final HashMap<Nuclide, PhotonCrossSectionsEvaluator> nuclideCrossSections = new HashMap<>();

  /**
   * Creates a new DepositionCalculator for computing energy deposition in
   * detector materials.
   *
   * @param material Material to use for transport.
   * @param chords Chord geometry of the detector (cuboid, cylinder, etc.)
   * @param dbkn Scattering distribution (e.g., Klein-Nishina)
   * @param max Maximum energy in keV
   */
  public DepositionCalculator(
          Material material,
          ChordGeometry chords,
          ScatteringDistribution dbkn,
          double max)
  {
    this.material = material;
    this.density = material.getDensity();

    this.scatterDistribution = dbkn;
    this.spatial = chords.newSpatial();
    this.angular = chords.newAngular();
    this.isotropic = chords.newIsotropic();

    this.scatterEvaluator = dbkn.newEvaluator();
    this.scatterEvaluator.setInputUnits(Units.get("keV"));
  }

  public void setUnits(Units units)
  {
    if (units.getType() == PhysicalProperty.LENGTH)
      this.distanceUnits = units;
    else
      throw new IllegalArgumentException("unexpected unit type");
  }

  public void setXrayLibrary(XrayLibrary library)
  {
    this.xrayLibrary = library;
  }

  public void setPhotonCrossSectionLibrary(PhotonCrossSectionLibrary library)
  {
    this.photonLibrary = library;
    this.materialCrossSections = library.get(material);

    // Get evaluators for the material as a whole
    this.crossSectionsEvaluator = materialCrossSections.newEvaluator();
    this.crossSectionsEvaluator.setInputUnits(Units.get("keV"));
    this.crossSectionsEvaluator.setOutputUnits(Units.get("m2/kg")); // Standardize to SI units  }

    // Pull by nuclide
    for (MaterialComponent comp : material)
    {
      this.nuclideCrossSections.put(comp.getNuclide(),
              photonLibrary.get(comp.getNuclide().getElement()).newEvaluator());
    }

  }

  /**
   * Sets the source position relative to the detector.
   *
   * @param x X coordinate of source position in meters
   * @param y Y coordinate of source position in meters
   * @param z Z coordinate of source position in meters
   */
  public void setPosition(double x, double y, double z)
  {
    double cf = this.distanceUnits.getValue();
    this.spatial.setPosition(cf * x, cf * y, cf * z);
    this.angular.setPosition(cf * x, cf * y, cf * z);
    multi = null;
    cache = false;
  }

  public void setPosition(Quantity x, Quantity y, Quantity z)
  {
    x.require(PhysicalProperty.LENGTH);
    y.require(PhysicalProperty.LENGTH);
    z.require(PhysicalProperty.LENGTH);
    double cf = this.distanceUnits.getValue();
    this.setPosition(x.get() / cf, y.get() / cf, z.get() / cf);
  }

  /**
   * Computes the energy deposition in the detector for a given incident photon
   * energy.
   *
   * @param energy Incident photon energy (in keV)
   * @param intensity Incident photon intensity (relative value, no specific
   * units) Currently unused but retained for future implementation
   * @return Deposition object containing energy deposition information
   */
  public Deposition compute(Quantity energy, double intensity)
  {
    Deposition out = new Deposition();
    createMulti();

    // We have a new position
    createChords();
    clearBuffer();

    // Next we need to compute the cross sections for the original energy
    this.crossSectionsEvaluator.seek(energy);
    double crossSectionScatter = this.crossSectionsEvaluator.getIncoherent();
    double crossSectionPair = this.crossSectionsEvaluator.getPair();
    double crossSectionPhoto = this.crossSectionsEvaluator.getPhotoelectric();
    double crossSectionTotal = crossSectionScatter + crossSectionPair + crossSectionPhoto;
    
    System.out.println(energy+" " +crossSectionScatter+ " " + crossSectionPhoto+ " " +crossSectionPair + " " +crossSectionTotal );

    // We compute the total escape probability from the chord distributions
    // The probability of hitting the detector (solid angle) should be incorporated
    // TODO: Implement proper solid angle calculation based on detector geometry and position
    double probabilityHit = 1.0; // Currently assuming 100% hit probability

    // Calculate macroscopic cross-section (attenuation coefficient) in 1/m
    // crossSectionTotal is now in m²/kg (from setOutputUnits)
    // density is in kg/m³
    double macroscopicCrossSection = crossSectionTotal * density.get();

    // Calculate escape probability using macroscopic cross-section
    double probabilityEscape = simpsonEscapeProbability(chords1, macroscopicCrossSection);
    double probabilyInteract = probabilityHit * (1 - probabilityEscape);
    // Okay we know the chance of getting at least one interaction

    out.totalPair = probabilyInteract * crossSectionPair / crossSectionTotal;
    out.totalPhoto = probabilyInteract * crossSectionPhoto / crossSectionTotal;
    double scatter = probabilyInteract * crossSectionScatter / crossSectionTotal;
    
    System.out.println(probabilityEscape + " " + probabilyInteract + " "+ out.totalPhoto );
    
    if (Double.isNaN(out.totalPhoto))
              throw new ArithmeticException("NaN in initial calculation");

    double ei = energy.as("keV");
    out.energy = ei;
    int n = energyScale.findEdgeFloor(ei);
    computeScatter1(out, n, ei, scatter);
    out.initial = Arrays.copyOf(inflight, n);
    
    if (Double.isNaN(out.totalPhoto))
              throw new ArithmeticException("NaN in initial calculation");
    if (DoubleArray.isNaN(out.initial))
      throw new ArithmeticException("NaN in inflight");
    
    computeScatter2(out, n, ei);
    //computeScatterN(out, n, ei, crossSectionTotal);
    processPairs(out, ei, out.totalPair);
    out.scattered = Arrays.copyOf(deposited, n);

    if (intensity != 1)
      out.rescale(intensity);
    return out;
  }

  private void createChords()
  {
    if (cache)
      return;

    for (int i = 0; i < N_CHORDS; i++)
    {
      double quantile = ((double) i) / (N_CHORDS - 1);
      chords1[i] = spatial.getChord(quantile);
      if (Double.isNaN(chords1[i]))
        throw new ArithmeticException("NAN in chord calculation");
    }
    cache = true;
  }

  /**
   * Creates or retrieves the multi-scatter calculator. This pre-computes photon
   * fates for multiple scattering events.
   */
  private void createMulti()
  {
    // Note: The intensity is a scaling factor that should be applied to final results
    // rather than affecting the physics calculations themselves

    if (multi == null)
    {
      System.out.println("Create multi");
      // TODO: Add cache invalidation if detector size changes
      multi = new MultiScatterLinearPointwise(
              this.materialCrossSections,
              isotropic,
              this.scatterDistribution,
              density, // density in kg/m³
              3000 // maximum energy in keV
      );
      multi.computeResponse(); // Precompute all fates
    }
  }

  private void computeScatter1(Deposition out, int n, double ei, double scatter)
  {
    double total = 0;
    for (int i = 0; i < n; ++i)
    {
      double ed = i + 0.5;
      double ee = ei - ed;
      double f = this.scatterEvaluator.getCrossSection(ei, ee);
      total += f;
      this.inflight[i] = f;
    }
    DoubleArray.multiplyAssignRange(inflight, 0, n, scatter / total);
  }

  private void computeScatter2(Deposition out, int n, double ei)
  {
       if (Double.isNaN(out.totalPhoto))
      {
        throw new ArithmeticException("NaN in scatter 2");
      }

    // Only those that escape will be deposited, so next we need to use 
    for (int i = 0; i < n; ++i)
    {
      double ed = i + 0.5;
      double ee = ei - ed;

      // This is the total energy in flight
      this.crossSectionsEvaluator.seek(ee);
      double crossSectionScatter2 = this.crossSectionsEvaluator.getIncoherent();
      double crossSectionPair2 = this.crossSectionsEvaluator.getPair();
      double crossSectionPhoto2 = this.crossSectionsEvaluator.getPhotoelectric();
      double crossSectionTotal2 = crossSectionScatter2 + crossSectionPair2 + crossSectionPhoto2;

      // For chord length calculation, we need the macroscopic cross-section (1/m)
      double macroscopicCrossSection2 = crossSectionTotal2 * density.get();

      // We need to update the chord distribution based on the angles and the initial cross section
      // The initial macroscopic cross-section is used to determine how deep the first interaction was
      double angle = scatterEvaluator.getCosAngle(ei, ee);
      for (int j = 0; j < N_CHORDS2; j++)
        this.chords2[j] = this.angular.getChord(((double) j) / (N_CHORDS2 - 1), angle, macroscopicCrossSection2);
      this.chordAudit.add(this.chords2.clone());

      // ENHANCEMENT, we can get the total xray escapes by looking at distance to surface on the 180.
      // Convert to an escape probability using macroscopic cross section in 1/m
      double probabilityEscape2 = simpsonEscapeProbability(chords2, macroscopicCrossSection2);

      // Use the interaction proability to split the inflight
      double fractPhoto = crossSectionPhoto2 / crossSectionTotal2;
      double fractPair = crossSectionPair2 / crossSectionTotal2;
      double fractScatter = crossSectionScatter2 / crossSectionTotal2;
      double probabilityInteract = Math.max(1.0 - probabilityEscape2, 0);
      double q = this.inflight[i];
      
      double x1 = q * probabilityInteract * fractPhoto;
      double x2 = q * probabilityInteract * fractPair;
      double x3 = q * probabilityInteract * fractScatter;
      double x4 = q * probabilityEscape2;

      // If the second was a complete capture then life is easy
      out.totalPhoto += x1;
      if (Double.isNaN(out.totalPhoto))
        throw new ArithmeticException("NaN in scatter 2");

      // We have made a pair so all energy as absorbed, thus no different than an initial pair production
      out.totalPair += x2;

      // Those are now deposited as they have no further energy
      this.deposited[i] += x4;

      // Find the fate for this energy (e.g., using the isotropic fate matrix)
      PhotonFate fate = multi.getFate(ee); // Precomputed fate for this energy bin

      double x5 = x3 * fate.photoElectric;
      double x6 = x3 * fate.pair;
      double x7 = x3 * fate.escape;

      // Full absorption (photoelectric)
      out.totalPhoto += x5;

      // Pair production (full absorption)
      out.totalPair += x6;
      if (Double.isNaN(out.totalPhoto))
        throw new ArithmeticException("NaN in scatter 2 pair");

      // Escape probability (add to deposited or escaped as appropriate)
      deposited[i] += x7;

      // Multi-scatter spectrum: add to deposited spectrum using addScatter
      fate.addScatter(
              deposited, // output array
              i, // starting index in output array
              energyScale, // output energy grid (linear or user-supplied)
              energyScale.getCenter(i),
              n - i, // number of output bins to fill (to avoid overrun)
              x3 // scaling factor
      );
    }
  }

  private void processPairs(Deposition out, double ei, double totalPair)
  {
    double singleEscape = 0;
    double doubleEscape = 0;
    int base = energyScale.findEdgeFloor(ei - 1022);
    int last = energyScale.findEdgeFloor(ei);
    if (base > 0)
    {
      PhotonFate pair = this.multi.getPair();

      // Use addScatter to map the pair fate's scatter spectrum onto the output grid at the correct offset
      pair.addScatter(
              deposited, // output array
              base, // starting index in output array
              energyScale, // output energy grid (linear or user-supplied)
              ei - 1022,
              last - base, // number of output bins to fill
              totalPair // scaling factor
      );

      // NOTE: Do NOT add single/double escape or pair full absorption to the binned spectrum.
      // These are kept as delta functions for post-processing with the appropriate relativistic/detector broadening.
      // Only the scattered spectrum is added to the binned deposition array.
      singleEscape = totalPair * pair.singleEscape;
      doubleEscape = totalPair * pair.doubleEscape;
      out.totalPhoto += totalPair * pair.photoElectric;
    }
    // Store escape probabilities
    out.singleEscape = singleEscape;
    out.doubleEscape = doubleEscape;

    // Store escape peak energies in keV
    out.singleEnergy = Math.max(ei - 511, 0);
    out.doubleEnergy = Math.max(ei - 1022, 0);
  }

  /**
   * Clears all buffer arrays to prepare for a new calculation.
   */
  private void clearBuffer()
  {
    Arrays.fill(deposited, 0.0);
    Arrays.fill(inflight, 0.0);
//    Arrays.fill(partialPhoto, 0.0);
//    Arrays.fill(partialPair, 0.0);
//    Arrays.fill(partialScatter, 0.0);
//    Arrays.fill(partialEscape, 0.0);
    chordAudit.clear(); // Clear the audit trail to prevent unbounded growth
  }

//<editor-fold desc="xray" defaultstate="collapsed">
  private double computeXrayEscapeProbability(double muXray, double muPrimary)
  {
    double totalEscape = 0;
    // We will only need to do this once per position
    for (int i = 0; i < escapeCosNodes.length; i++)
    {
      for (int j = 0; j < N_CHORDS3; j++)
      {
        double quantile = (double) j / (N_CHORDS2 - 1);
        // getChord handles the depth-of-interaction weighting via muPrimary internally
        escapeChords[i][j] = this.angular.getChord(quantile, escapeCosNodes[i], muPrimary);
      }
    }

    // We can evaluate this per xray
    for (int i = 0; i < escapeCosNodes.length; i++)
    {
      double pEscapeAtAngle = simpsonEscapeProbability(escapeChords[i], muXray);
      totalEscape += pEscapeAtAngle * escapeWeights[i];
    }

    // We only integrate the back hemisphere (0.5 of 4pi)
    // because forward-directed X-rays are almost always absorbed in a thick detector.
    return 0.5 * totalEscape;
  }

  public double getVacancyYieldForElement(XrayData data, double ei)
  {
    for (XrayEdge edge : data.getEdges())
    {
      if ("K".equals(edge.getName()))
      {
        // Ensure we compare keV to keV
        if (ei > edge.getEnergy().as("keV"))
        {
          double jk = edge.getJumpRatio();
          // Jump ratio fraction: (J-1)/J
          return (jk > 1.0) ? (jk - 1.0) / jk : 0.0;
        }
      }
    }
    return 0.0;
  }

  public void processXrayEscapes(Deposition out, double ei, double probInteract, double crossSectionTotal)
  {
    double[] vacanciesPerElement = computeVacanciesPerElement(ei, material, probInteract);
    double muPrimary = crossSectionTotal * density.get();

    int index = 0;
    for (MaterialComponent component : material)
    {
      double v_i = vacanciesPerElement[index++];
      if (v_i <= 0)
        continue;

      XrayData data = xrayLibrary.get(component.getNuclide().getElement());
      for (XrayEdge edge : data.getEdges())
      {
        if (!"K".equals(edge.getName()) || ei <= edge.getEnergy().as("keV"))
          continue;

        double yield = edge.getFluorescenceYield();

        // The edge is a collection of possible transitions (the cascade)
        for (Xray line : edge.getXrays())
        {
          double eXray = line.getEnergy().as("keV");
          double relativeIntensity = line.getIntensity().get(); // Branching ratio within the edge

          // Seek to the specific energy of THIS line for the BULK material
          this.crossSectionsEvaluator.seek(eXray);
          double muXray = this.crossSectionsEvaluator.getTotal() * density.get();

          // Transport this specific photon
          double pEscape = computeXrayEscapeProbability(muXray, muPrimary);

          // Total escaped events for this specific spectral line
          double escapedEvents = v_i * yield * relativeIntensity * pEscape;

          // Adjust the deposition
          out.totalPhoto -= escapedEvents;

          int escapeBin = energyScale.findEdgeFloor(ei - eXray);
          if (escapeBin >= 0 && escapeBin < deposited.length)
          {
            this.deposited[escapeBin] += escapedEvents;
          }
        }
      }
    }
    // Restore evaluator to incident energy for any subsequent logic
    this.crossSectionsEvaluator.seek(ei);
  }

  public double[] computeVacanciesPerElement(double ei, Material material, double probInteract)
  {
    double[] vacancies = new double[material.size()];

    // Bulk material properties (denominator)
    this.crossSectionsEvaluator.seek(ei);
    double muTotalBulk = this.crossSectionsEvaluator.getTotal();

    int index = 0;
    for (MaterialComponent component : material)
    {
      Nuclide nuclide = component.getNuclide();
      XrayData xrayData = xrayLibrary.get(nuclide.getElement());
      PhotonCrossSectionsEvaluator nEval = nuclideCrossSections.get(nuclide);

      if (xrayData == null || nEval == null)
      {
        index++;
        continue;
      }

      nEval.seek(ei);
      double w_i = component.getMassFraction();

      // Get per-nuclide cross sections (m2/kg)
      double photo_i = nEval.getPhotoelectric();
      double scatter_i = nEval.getIncoherent();

      // Fraction of interactions hitting the K-shell
      double fK_photo = getVacancyYieldForElement(xrayData, ei);
      double fK_scatter = 2.0 / nuclide.getAtomicNumber();

      // Macroscopic vacancy production for this element (m2/kg)
      double muVacancy_i = w_i * (photo_i * fK_photo + scatter_i * fK_scatter);

      // Probability normalized to the bulk interaction chance
      vacancies[index] = probInteract * (muVacancy_i / muTotalBulk);
      index++;
    }
    return vacancies;
  }
//</editor-fold>
}
