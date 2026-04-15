// --- file: gov/llnl/rtk/response/deposition/CuboidSpatialChordQF.java ---
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
public class CuboidSpatialChordQF implements SpatialChordQF
{
  final private ArchEPP model;
  final private double l, w, h, diag;

  private boolean dirty = true;
  private double x, y, z;

  private final ModuleState state;
  public final double[] xp;
  public final double[] xe;
  private final double[] dims = new double[3];
  private final double[] entry = new double[3];

  CuboidSpatialChordQF(ArchEPP model, double l, double w, double h)
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
      CuboidUtility.permute(dims, entry, x, y, z, l, w, h);
      CuboidUtility.fillCuboidFeatures(xe, 0, entry[0], entry[1], entry[2], dims[0], dims[1], dims[2], diag);
      model.encode(state, xe);
      dirty = false;
    }

    // ODE integration for FM model
    xp[0] = 0;      // t
    xp[1] = q;      // x (quantile)
    // If your model expects more predictor features, set them here

    int numSteps = 200;
    double dt = 1.0 / numSteps;
    // FIXME reversed
    return DepositionUtility.flow(model, state, xp, 1.0, -dt, numSteps) * diag;
  }

}
