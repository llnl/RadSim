// --- file: gov/llnl/rtk/calibration/ControlMapping.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.MathExceptions;
import gov.llnl.math.spline.CubicHermiteSpline;
import gov.llnl.math.spline.Spline;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.annotation.Debug;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.xml.bind.Reader;
import java.io.Serializable;

/**
 * This class stores the change in one peak as a function of some observable.
 * Knots are represented by position of fiducial (such as energy control in the
 * spectrum) Typically these are stored as the centroid locations with the
 * center of each channel indicated as 0 (as opposed to edge space in which the
 * center of then channel is 0.5). We have a nonlinear map as a function of some
 * observable, say temperature or total integrated power.
 */
@Internal
@Reader.Declaration(pkg = RtkPackage.class, name = "controlMapping")
public class ControlMapping implements Serializable
{
  // Original peak position channels
  double origin;
  double energy;
  // Observed peak position as a function of the observable in channels
  Spline spline;

  public double compute(double observable)
  {
    try
    {
      return spline.applyAsDouble(observable);
    }
    catch (MathExceptions.DomainException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @return the origin
   */
  public double getOrigin()
  {
    return origin;
  }

  /**
   * @param origin the origin to set
   */
  @Reader.Attribute(name = "origin", required = true)
  public void setOrigin(double origin)
  {
    this.origin = origin;
  }

  @Reader.Attribute(name = "energy")
  public void setEnergy(double energy)
  {
    this.energy = energy;
  }

  /**
   * @return the spline
   */
  public Spline getSpline()
  {
    return spline;
  }

  /**
   * @param spline the spline to set
   */
  @Reader.Element(name = "spline")
  public void setSpline(CubicHermiteSpline spline)
  {
    this.spline = spline;
  }

}
