// --- file: gov/llnl/rtk/response/deposition/CylinderSpatialChordQF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.rtk.ml.ArchEPP;
import gov.llnl.rtk.ml.ModuleState;

/**
 * ML-based evaluator for the direct chord-length CDF (PositionalCDF).
 *
 * All geometry and position features are normalized to the unit cuboid. Output
 * is denormalized to physical units.
 */
public class CylinderSpatialChordQF implements SpatialChordQF
{
  final private ArchEPP model;
  final private double diameter, height, diag;

  private boolean dirty = true;
  private double x, y, z;

  private final ModuleState state;
  public final double[] xp;
  public final double[] xe;
 
  CylinderSpatialChordQF(ArchEPP model, double diameter, double height)
  {
    this.model = model;
    this.state = model.newState();
    this.xp = new double[model.getPredictorInputSize()];
    this.xe = new double[model.getEncoderInputSize()];
    this.diameter = diameter;
    this.height = height;
    this.diag = Math.sqrt(diameter * diameter + height * height);
  }

  @Override
  public void setPosition(double x, double y, double z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.dirty = true;
  }

  /**
   * Evaluate the chord length for a given quantile, using current position and
   * geometry.
   *
   * @param q Quantile (input to ODE)
   * @return Chord length (physical units)
   */
  @Override
  public double getChord(double q)
  {
    // Recompute geometry/position features if needed
    if (dirty)
    {
      CylinderUtility.fillCylinderFeatures(xe, 2, x,y,z, diameter, height, diag);
      model.encode(state, xe);
      dirty = false;
    }

    // ODE integration for FM model
    xp[0] = 0;      // t
    xp[1] = q;      // x (quantile)
    // If your model expects more predictor features, set them here

    int numSteps = 200;
    double dt = 1.0 / numSteps;
    return DepositionUtility.flow(model, state, xp, 1.0, -dt, numSteps) * diag;
  }

}
