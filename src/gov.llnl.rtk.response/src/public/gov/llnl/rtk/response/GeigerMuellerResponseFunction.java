// --- file: gov/llnl/rtk/response/GeigerMuellerResponseFunction.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.interp.SingleInterpolator;
import gov.llnl.rtk.response.support.CubicSplineExtrapolation;
import gov.llnl.rtk.response.support.CubicSplineBuilder;
import gov.llnl.rtk.response.support.CubicSpline;
import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Vector3Ops;
import gov.llnl.math.euclidean.Versor;
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxGroup;
import gov.llnl.rtk.flux.FluxLine;

/**
 *
 * @author nelson85
 */
public class GeigerMuellerResponseFunction implements CountResponseFunction
{
  // 1 over Counts of 662 into 4pi required produce 1 mSv/h at 1 m.
  final static double COUNT_FACTOR = 9.259259259259259e-11;
  CubicSpline efficiency;
  private double doseFactor = 0; // cps/ (mSv/hr)
  String vendor;
  String model;
  double endFactor;
  double sideFactor;
  double deadtime;

  @Override
  public String getVendor()
  {
    return this.vendor;
  }

  @Override
  public String getModel()
  {
    return this.model;
  }

  /**
   * @return the doseFactor
   */
  public double getDoseFactor()
  {
    return doseFactor;
  }

  /**
   * @return the endFactor
   */
  public double getEndFactor()
  {
    return endFactor;
  }

  /**
   * @return the sideFactor
   */
  public double getSideFactor()
  {
    return sideFactor;
  }

  public double getDeadtime()
  {
    return this.deadtime;
  }

  @Override
  public double getGeometryFactor(
          Vector3 sourceCoordinate,
          Vector3 globalCoordinate,
          Versor globalOrientation,
          Vector3 sensorCoordinate,
          Versor sensorOrientation)
  {
    Vector3 delta = Vector3Ops.subtract(sourceCoordinate, globalCoordinate);
    delta = globalOrientation.rotate(delta);
    delta = Vector3Ops.subtract(delta, sensorCoordinate);
    delta = sensorOrientation.rotate(delta);
    double distance = delta.norm();
    double cos2 = delta.getZ() * delta.getZ() / delta.norm2();
    double sin2 = 1 - cos2;
    double angularEfficiency = cos2 * endFactor + sin2 * sideFactor;
    return angularEfficiency / distance / distance;
  }

  @Override
  public CountResponseEvaluator newEvaluator()
  {
    return new GeigerMuellerEvaluator();
  }

  public class GeigerMuellerEvaluator implements CountResponseEvaluator
  {
    private final SingleInterpolator.Evaluator eval;
    private Vector3 sourceCoordinate;
    private Vector3 globalCoordinate;
    private Vector3 sensorCoordinate;
    private Versor localOrientation;
    private Versor globalOrientation;
    private boolean viewEnabled;

    GeigerMuellerEvaluator()
    {
      this.eval = GeigerMuellerResponseFunction.this.efficiency.get();
    }

    @Override
    public CountResponseFunction getResponseFunction()
    {
      return GeigerMuellerResponseFunction.this;
    }

    @Override
    public double apply(Flux flux)
    {
      double geometricEfficiency = getGeometryFactor(
              this.sourceCoordinate,
              this.globalCoordinate,
              this.globalOrientation,
              this.sensorCoordinate,
              this.localOrientation);
      if (!viewEnabled)
        geometricEfficiency = 1.0;
      double total = 0;
      for (FluxGroup fluxGroup : flux.getPhotonGroups())
      {
        double e0 = fluxGroup.getEnergyLower();
        double e1 = fluxGroup.getEnergyUpper();
        double c = fluxGroup.getCounts();
        double ef0 = eval.applyAsDouble(e0);
        double ef = eval.applyAsDouble((e0 + e1) / 2);
        double ef1 = eval.applyAsDouble(e1);
        total += c / 6 * (ef0 + 4 * ef + ef1);
      }

      for (FluxLine fluxLine : flux.getPhotonLines())
      {
        total += fluxLine.getIntensity()
                * eval.applyAsDouble(fluxLine.getEnergy());
      }
      return total * getDoseFactor() * COUNT_FACTOR * geometricEfficiency;
    }

    /**
     * @return the sourceCoordinate
     */
    @Override
    public Vector3 getSourceCoordinate()
    {
      return sourceCoordinate;
    }

    /**
     * @param sourceCoordinate the sourceCoordinate to set
     */
    @Override
    public void setSourceCoordinate(Vector3 sourceCoordinate)
    {
      this.sourceCoordinate = sourceCoordinate;
    }

    /**
     * @return the localOrientation
     */
    @Override
    public Versor getSensorRelativeOrientation()
    {
      return localOrientation;
    }

    /**
     * @param localOrientation the localOrientation to set
     */
    public void setLocalOrientation(Versor localOrientation)
    {
      this.localOrientation = localOrientation;
    }

    /**
     * @return the globalOrientation
     */
    @Override
    public Versor getInstrumentOrientation()
    {
      return globalOrientation;
    }

    /**
     * @param globalOrientation the globalOrientation to set
     */
    @Override
    public void setInstrumentOrientation(Versor globalOrientation)
    {
      this.globalOrientation = globalOrientation;
    }

    /**
     * @return the globalCoordinate
     */
    @Override
    public Vector3 getInstrumentCoordinate()
    {
      return globalCoordinate;
    }

    /**
     * @param globalCoordinate the globalCoordinate to set
     */
    @Override
    public void setInstrumentCoordinate(Vector3 globalCoordinate)
    {
      this.globalCoordinate = globalCoordinate;
    }

  }

//<editor-fold desc="builder" defaultstate="collapsed">
  public static Builder newBuilder()
  {
    return new Builder();
  }

  public static class Builder
  {
    GeigerMuellerResponseFunction output = new GeigerMuellerResponseFunction();
    double[] efficiencyX;
    double[] efficiencyY;

    public Builder vendor(String value)
    {
      output.vendor = value;
      return this;
    }

    public Builder model(String value)
    {
      output.model = value;
      return this;
    }

    public Builder efficiency(double[] energies, double[] values)
    {
      output.efficiency = new CubicSplineBuilder(energies, values)
              .extrapolation(CubicSplineExtrapolation.LINEAR)
              .create();
      return this;
    }

    public Builder doseFactor(double value)
    {
      output.doseFactor = value;
      return this;
    }

    public Builder sideFactor(double value)
    {
      output.sideFactor = value;
      return this;
    }

    public Builder endFactor(double value)
    {
      output.endFactor = value;
      return this;
    }

    public Builder deadtime(double value)
    {
      output.deadtime = value;
      return this;
    }

    public GeigerMuellerResponseFunction create()
    {
      // Added to support the encoding
      if (efficiencyX!=null && this.efficiencyY!=null)
        efficiency(efficiencyX, efficiencyY);
      return output;
    }
  }
//</editor-fold> 
}
