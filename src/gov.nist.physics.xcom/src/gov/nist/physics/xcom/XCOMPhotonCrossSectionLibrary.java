/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xcom;

import gov.llnl.rtk.physics.Element;
import gov.llnl.rtk.physics.Material;
import gov.llnl.rtk.physics.MaterialComponent;
import gov.llnl.utility.proto.ProtoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.ToDoubleFunction;
import gov.llnl.rtk.physics.PhotonCrossSectionLibrary;
import gov.llnl.rtk.physics.PhotonCrossSections;
import gov.llnl.rtk.physics.PhotonCrossSectionsEvaluator;

/**
 * <p>
 * This library uses the <b>NIST XCOM</b> database as its default source for
 * photon cross sections (photoelectric, incoherent, coherent, pair production,
 * total attenuation) for elements and compounds.
 * </p>
 *
 * <h2>Overview</h2>
 * <ul>
 * <li>Comprehensive: Covers elements Z=1–100 and arbitrary
 * mixtures/compounds.</li>
 * <li>Evaluated: Based on theoretical calculations and experimental data,
 * curated by NIST.</li>
 * <li>Wide Energy Range: 1 keV to 100 GeV.</li>
 * <li>Community Standard: Widely used in radiation transport, shielding, and
 * detector simulation.</li>
 * </ul>
 *
 * <h2>Nature of the Data</h2>
 * <ul>
 * <li>XCOM is an <b>evaluated database</b>, not a primary measurement
 * source.</li>
 * <li>Combines theoretical calculations (e.g., Scofield, Hubbell) and
 * experimental data.</li>
 * <li>Data is provided as mass attenuation coefficients (cm²/g) and cross
 * sections.</li>
 * </ul>
 *
 * <h2>Provenance and Citation</h2>
 * <p>
 * If you use this data in published work, please cite:
 * </p>
 * <blockquote>
 * Berger, M.J., Hubbell, J.H., Seltzer, S.M., Chang, J., Coursey, J.S.,
 * Sukumar, R., Zucker, D.S., & Olsen, K. (2010).<br>
 * XCOM: Photon Cross Section Database (version 1.5).<br>
 * National Institute of Standards and Technology, Gaithersburg, MD.<br>
 * <a href="https://www.nist.gov/pml/xcom-photon-cross-sections-database">https://www.nist.gov/pml/xcom-photon-cross-sections-database</a>
 * </blockquote>
 *
 * <h2>Limitations</h2>
 * <ul>
 * <li>Not primary data: For traceability, consult the original references in
 * the XCOM documentation.</li>
 * <li>Mixture rules: Compound/mixture cross sections are calculated using the
 * mixture rule (mass fractions).</li>
 * <li>Updates: Database reflects the state as of 2010. For the latest values,
 * check for updates or new releases.</li>
 * </ul>
 *
 * <h2>Alternatives</h2>
 * <ul>
 * <li>NIST X-ray: For atomic line energies and yields.</li>
 * <li>NIST Compton Profile Database: For electron momentum profiles.</li>
 * <li>EPDL, Storm & Israel, Henke: For specific energy ranges or
 * materials.</li>
 * </ul>
 *
 * <h2>Implementation Note</h2>
 * <p>
 * The library is accessed via the <code>nist.xcom</code> interface in this
 * codebase. If you wish to substitute a different cross-section database,
 * ensure it conforms to the same API and data structure.
 * </p>
 *
 * <h2>Summary Table</h2>
 * <table border="1">
 * <caption>Summary of Supported Libraries</caption>
 * <tr>
 * <th>Library/API</th>
 * <th>Backing Data Source</th>
 * <th>Contents</th>
 * <th>Notes</th>
 * </tr>
 * <tr>
 * <td>nist.xray</td>
 * <td>ElamDB12 (Elam et al)</td>
 * <td>X-ray lines, yields</td>
 * <td>Comprehensive, curated</td>
 * </tr>
 * <tr>
 * <td>nist.xcom</td>
 * <td>NIST XCOM</td>
 * <td>Photon cross sections</td>
 * <td>Evaluated, ab initio</td>
 * </tr>
 * <tr>
 * <td>nist.compton</td>
 * <td>NIST Compton Profile</td>
 * <td>Electron profiles</td>
 * <td>For Doppler broadening</td>
 * </tr>
 * </table>
 *
 * @author nelson85
 */
public class XCOMPhotonCrossSectionLibrary implements PhotonCrossSectionLibrary
{

  private static final XCOMPhotonCrossSectionLibrary INSTANCE = new XCOMPhotonCrossSectionLibrary();
  private final HashMap<String, CrossSectionsTable> cache = new HashMap<>();
  private final double EPS = 1e-9;

  public static XCOMPhotonCrossSectionLibrary getInstance()
  {
    return INSTANCE;
  }

  private XCOMPhotonCrossSectionLibrary()
  {
  }

  @Override
  synchronized public PhotonCrossSections get(Element element)
  {
    String descriptor = Integer.toString(element.getAtomicNumber());
    if (cache.get(descriptor) != null)
    {
      return new CrossSectionsImpl(cache.get(descriptor));
    }

    // Open the stored resource file.
    ClassLoader cl = this.getClass().getClassLoader();
    URL resource = cl.getResource(String.format("gov/nist/physics/xcom/resources/%s.bin", descriptor));
    if (resource == null)
    {
      return null;
    }

    // Read all the data from the resource file into memory
    try (InputStream is = resource.openStream())
    {
      byte[] bytes = is.readAllBytes();
      CrossSectionsTable cs = CrossSectionsEncoding.getInstance().parseBytes(bytes);
      cache.put(descriptor, cs);
      return new CrossSectionsImpl(cs);
    }
    catch (IOException | ProtoException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  @Override
  synchronized public PhotonCrossSections get(Material material)
  {
    // Data for combining cross-sections
    ArrayList<Double> fraction = new ArrayList<>();
    ArrayList<PhotonCrossSectionsEvaluator> sections = new ArrayList<>();
    HashSet<Double> edges = new HashSet<>();
    HashSet<Double> bins = new HashSet<>();

    // Collect all of the evaluators for each element
    for (MaterialComponent c : material)
    {
      // FIXME check this do we need atom fraction or mass fraction here
      double mf = c.getMassFraction();
      Element elem = c.getNuclide().getElement();
      fraction.add(mf);
      CrossSectionsImpl cs = (CrossSectionsImpl) this.get(elem);
      PhotonCrossSectionsEvaluator evaluator = cs.newEvaluator();
      evaluator.setInputUnits(CrossSectionsEvaluatorImpl.BASE_INPUT_UNITS);
      evaluator.setOutputUnits(CrossSectionsEvaluatorImpl.BASE_OUTPUT_UNITS);
      sections.add(evaluator);

      // Collect all energies and find edges
      double last = -1;
      for (double energy : cs.table.energies)
      {
        // Edges are stored with duplicate values in the energy range
        energy = Math.exp(energy);
        if (energy == last)
        {
          edges.add(energy);
        }
        last = energy;

        // Always add the energy to the bins
        bins.add(energy);
      }
    }

    // Define the new bins for the table
    double[] v = bins.stream().mapToDouble(p -> p).toArray();
    Arrays.sort(v);
    int n = v.length + edges.size();

    CrossSectionsTable out = new CrossSectionsTable();
    out.energies = new double[n];
    out.incoherent = new double[n];
    out.pairElectron = new double[n];
    out.pairNuclear = new double[n];
    out.photoelectric = new double[n];
    out.total = new double[n];

    int i = 0;
    for (double energy : v)
    {
      // If it isn't an edge we can just build create the entry directly
      if (!edges.contains(energy))
      {
        out.energies[i] = Math.log(energy);
        out.incoherent[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getIncoherent);
        out.pairElectron[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getPairElectron);
        out.pairNuclear[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getPairNuclear);
        out.photoelectric[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getPhotoelectric);
        out.total[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getTotal);
        i++;
        continue;
      }

      // If we are on an edge, then we must evaluate below and above the edge by a small amount.
      out.energies[i] = Math.log(energy);
      out.energies[i + 1] = Math.log(energy);

      energy -= EPS;
      out.incoherent[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getIncoherent);
      out.pairElectron[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getPairElectron);
      out.pairNuclear[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getPairNuclear);
      out.photoelectric[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getPhotoelectric);
      out.total[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getTotal);
      i++;

      energy += EPS;
      out.incoherent[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getIncoherent);
      out.pairElectron[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getPairElectron);
      out.pairNuclear[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getPairNuclear);
      out.photoelectric[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getPhotoelectric);
      out.total[i] = merge(energy, fraction, sections, PhotonCrossSectionsEvaluator::getTotal);
      i++;
    }

    return new CrossSectionsImpl(out);
  }

  /**
   * Combine the cross section using mass fractions.
   *
   * @param energy
   * @param fraction
   * @param sections
   * @param s
   * @return
   */
  private static double merge(double energy,
          ArrayList<Double> fraction,
          ArrayList<PhotonCrossSectionsEvaluator> sections,
          ToDoubleFunction<PhotonCrossSectionsEvaluator> s)
  {
    double v = 0;
    for (int i = 0; i < fraction.size(); i++)
    {
      PhotonCrossSectionsEvaluator section = sections.get(i);
      section.seek(energy);
      v += fraction.get(i) * s.applyAsDouble(section);
    }
    return Math.log(v);
  }
}
