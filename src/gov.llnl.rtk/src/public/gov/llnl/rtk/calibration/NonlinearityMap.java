// --- file: gov/llnl/rtk/calibration/NonlinearityMap.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.spline.CubicHermiteSpline;
import gov.llnl.math.spline.CubicHermiteSplineFactory;
import gov.llnl.math.spline.EndBehavior;
import gov.llnl.math.spline.Spline;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.xml.bind.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "nonlinearMap", order = Reader.Order.FREE)
public class NonlinearityMap implements Serializable
{
  ArrayList<ControlMapping> control = new ArrayList<>();

  public Spline getCorrectionSpline(double observable)
  {
    double[] x = new double[control.size()];
    double[] y = new double[control.size()];
    int i = 0;
    for (ControlMapping point : control)
    {
      // -0.5 is to move from channel center to channel edge space
      // though the correction is usually insignificant if both
      // points are in the same reference system
      y[i] = point.getOrigin() - 0.5;
      x[i] = point.compute(observable) - 0.5;
      i++;
    }
    CubicHermiteSpline chs = CubicHermiteSplineFactory.createNatural(x, y);
    chs.setEndBehavior(EndBehavior.LINEAR);
    return chs;
  }

  public Spline getInverseCorrectionSpline(double observable)
  {
    double[] x = new double[control.size()];
    double[] y = new double[control.size()];
    int i = 0;
    for (ControlMapping point : control)
    {
      // -0.5 is to move from channel center to channel edge space
      // though the correction is usually insignificant if both
      // points are in the same reference system
      y[i] = point.getOrigin() - 0.5;
      x[i] = point.compute(observable) - 0.5;
      i++;
    }
    CubicHermiteSpline chs = CubicHermiteSplineFactory.createNatural(y, x);
    chs.setEndBehavior(EndBehavior.LINEAR);
    return chs;
  }

  @Reader.Element(name = "control")
  public void addControl(ControlMapping cm)
  {
    this.control.add(cm);
    // We need to keep this collection in order to make everything work
    Collections.sort(this.control, new Comparator<ControlMapping>()
    {
      @Override
      public int compare(ControlMapping t, ControlMapping t1)
      {
        return Double.compare(t.getOrigin(), t1.getOrigin());
      }
    });
  }

}

