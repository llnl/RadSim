// --- file: gov/llnl/rtk/response/deposition/CylinderAngularChordQF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.rtk.ml.ArchEPP;
import gov.llnl.rtk.ml.ModuleState;

public class CylinderAngularChordQF implements AngularChordQF
{
  private final ArchEPP model;
  
  private final double diameter;
  private final double height;
  private final double diagonal;

  private double x;
  private double y;
  private double z;
  private boolean dirty;
  
  // Matchine learning
  private final ModuleState state;
  public final double[] xp;
  public final double[] xe;

  CylinderAngularChordQF(ArchEPP model, double diameter, double height)
  {
    this.model = model;
    this.state = model.newState();
    this.xp = new double[model.getPredictorInputSize()];
    this.xe = new double[model.getEncoderInputSize()];
    this.diameter = diameter;
    this.height = height;
    this.diagonal = Math.sqrt(diameter * diameter + height * height);
    this.dirty = true;
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

  public void prepare(double cdf, double cosAngle, double attenuation)
  {

    // Convert to features
    if (dirty)
    {
      dirty = false;
      CylinderUtility.fillCylinderFeatures(xe, 2, x, y, z, diameter, height, diagonal);
      model.encode(state, xe);
    }

    // Scale the crossection down to the unit cuboid
    AngularUtility.fillAngularFeatures(xp, 2, cosAngle, attenuation * diagonal);

    xp[0] = 0;
    xp[1] = cdf;
  }

  // Quantile to chord (apply scale here)
  @Override
  public double getChord(double cdf, double cosAngle, double attenuation)
  {
    prepare(cdf, cosAngle, attenuation);

    int numSteps = 100;
    double dt = 1.0 / numSteps;

    return DepositionUtility.flow(model, state, xp, 0.0, dt, numSteps)*diagonal;
  }

}

