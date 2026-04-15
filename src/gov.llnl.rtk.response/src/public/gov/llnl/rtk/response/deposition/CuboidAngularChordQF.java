// --- file: gov/llnl/rtk/response/deposition/CuboidAngularChordQF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.rtk.ml.ArchEPP;
import gov.llnl.rtk.ml.ModuleState;

public class CuboidAngularChordQF implements AngularChordQF
{
  final private ArchEPP model;
  final private double l;
  final private double w;
  final private double h;
  final private double diag;
  
  private double x;
  private double y;
  private double z;
  
  private boolean dirty;
  private final ModuleState state;
  public final double[] xp;
  public final double[] xe;
  private final double[] dims = new double[3];
  private final double[] entry = new double[3];

  CuboidAngularChordQF(ArchEPP model, double l, double w, double h)
  {
    this.model = model;
    this.state = model.newState();
    this.xp = new double[model.getPredictorInputSize()];
    this.xe = new double[model.getEncoderInputSize()];
    this.l = l;
    this.w = w;
    this.h = h;
    this.diag = Math.sqrt(l * l + w * w + h * h);
  }
  // Maybe this should handle permution and scale

  @Override
  public void setPosition(double x, double y, double z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.dirty = true;
  }

  public void prepare(double quantile, double cosAngle, double attenuation)
  {
    // Convert to features
    if (dirty)
    {
      dirty = false;
      CuboidUtility.permute(dims, entry, x, y, z, l, w, h);
      CuboidUtility.fillCuboidFeatures(xe, 0,
              entry[0], entry[1], entry[2],
              dims[0], dims[1], dims[2], diag);
      model.encode(state, xe);
    }

    // Scale the crossection down to the unit cuboid
    AngularUtility.fillAngularFeatures(xp, 2, cosAngle, attenuation * diag);

    xp[0] = 0;
    xp[1] = quantile;
  }

  // Quantile to chord (apply scale here)
  @Override
  public double getChord(double quantile, double cosAngle, double attenuation)
  {
    prepare(quantile, cosAngle, attenuation);
   
    int numSteps = 100;
    double dt = 1.0 / numSteps;

    // FIXME the model was built chord -> cdf rather than the preferred direction
    return DepositionUtility.flow(model, state, xp, 0.0, dt, numSteps)*diag;
  }



}
