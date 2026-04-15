// --- file: gov/llnl/rtk/response/SpectralResponseFunctionCalculated.java ---
/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Vector3Ops;
import gov.llnl.math.euclidean.Versor;
import gov.llnl.math.interp.SingleInterpolator;
import java.util.HashMap;
import java.util.Map;
import gov.llnl.rtk.physics.ElectronShell;
import gov.llnl.rtk.physics.Quantity;

import gov.llnl.rtk.physics.Material;
import gov.llnl.rtk.physics.PhotonCrossSectionLibrary;
import gov.llnl.rtk.physics.XrayLibrary;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author nelson85
 */
public class SpectralResponseFunctionCalculated extends SpectralResponseFunctionBase
{

  // Global peakShapeParameters parameters
  ShapeParameters peakShapeParameters = new ShapeParameters();

  // Impulse reponses sorted by energy
//  CalculatedResponseEntry[] entries;
  LLDFunction lld = new LLDFunction();

  // Caches created from the entries.
  double[] intervals;              // collected from continuum.energy

  // Model for incomplete capture in CZT
  SpectralResponseIncomplete incomplete = new SpectralResponseIncomplete();

  // New data for calculated (will come from the Builder class)
  List<ElectronShell> shells;// FIXME builder
  Material material;// FIXME builder
  Quantity length, width, height;  // FIXME builder
  double[] resolutionEnergy;
  double[] resolutionWidth2;
  SingleInterpolator resolutionModel;
  XrayLibrary xrayLibrary;
  PhotonCrossSectionLibrary photonLibrary;

  void initialize()
  {
    this.incomplete.cache();
    this.lld.initialize();
  }

  public void setXrayLibrary(XrayLibrary library)
  {
    this.xrayLibrary = library;
  }

  public void setPhotonCrossSectionLibrary(PhotonCrossSectionLibrary library)
  {
    this.photonLibrary = library;
  }

  @Override
  public SpectralResponseEvaluator newEvaluator()
  {
    return new CalculatedResponseEvaluator(this);
  }

  @Override
  public double getGeometryFactor(Vector3 sourceCoordinate,
          Vector3 globalCoordinate, Versor globalOrientation,
          Vector3 sensorCoordinate, Versor sensorOrientation)
  {
    Vector3 delta = Vector3Ops.subtract(sourceCoordinate, globalCoordinate);
    delta = globalOrientation.rotate(delta);
    double distance2 = Vector3Ops.sqrDistance(delta, sensorCoordinate);
    // Correct formula for near field is
    //   4 * Math.atan2(2 * distance * Math.sqrt(4 * distance * distance + w * w + h * h), w * h)/w/h;
    // But we don't have the area of the detector
    return 1 / distance2;
  }

  @Override
  public Map<String, Double> getParameters()
  {
    HashMap<String, Double> map = new HashMap<>();
    map.put(EMG_THETA, this.peakShapeParameters.theta);
    map.put(EMG_NEGATIVE_TAIL, this.peakShapeParameters.negativeTail);
    map.put(EMG_POSITIVE_TAIL, this.peakShapeParameters.positiveTail);
    return map;
  }

//<editor-fold desc="boilerplate" defaultstate="collapsed">
  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 79 * hash + this.peakShapeParameters.hashCode();
    hash = 79 * hash + this.incomplete.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final SpectralResponseFunctionCalculated other = (SpectralResponseFunctionCalculated) obj;
    if (!Objects.equals(this.peakShapeParameters, other.peakShapeParameters))
      return false;
    return Objects.equals(this.incomplete, other.incomplete);
  }
//</editor-fold>

  /**
   * @return the lld
   */
  public LLDFunction getLld()
  {
    return lld;
  }

  /**
   * @return the peakShapeParameters
   */
  public ShapeParameters getPeakShapeParameters()
  {
    return peakShapeParameters;
  }
}


/* ENHANCEMENT - The energy scales for deponsition are not as simple as it seems.
 we typically define the scale based on the photoelectric, BUT in practice 
 the photoelectric being a mixture of multiple interaction types is the most
 complete of all.   The double escape is the actual true single interacton curve
 while the single escape is the photoelectric for 511 plus the true.  
 
 If we want perfect accuracy, we first compute the ideal energies for all
 interactions then using the material spread function to get the photoelectric
 loss function.  We then use this loss function to boost all the other energies
 into the correct location relative to the photoelectric.

 For now we will go with the simple energy function as getting the integrated product
 of all the efficiencys right requires a heavy lift to the deposition calculator.
 */

 /* ENHANCEMENT - tracking internal sources in this system is different than 
   the simple model because we properly need to deal with the fact internal sources
   have very diffenent depositions and you need to deconvolve if you want to 
   change the parameters.   As this is LaBr concern we can safely punt.
 */
