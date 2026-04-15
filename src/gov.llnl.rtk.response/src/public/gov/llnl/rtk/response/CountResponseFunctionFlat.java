// --- file: gov/llnl/rtk/response/CountResponseFunctionFlat.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

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
public class CountResponseFunctionFlat implements CountResponseFunction
{
  double area; // area in square meters
  double efficiency;  // absolute efficiency

  @Override
  public String getVendor()
  {
    return "generic";
  }

  @Override
  public String getModel()
  {
    return "generic";
  }

  @Override
  public CountResponseEvaluator newEvaluator()
  {
    return new CountResponseEvaluatorFlat();
  }

  @Override
  public double getGeometryFactor(Vector3 sourceCoordinate, Vector3 globalCoordinate, Versor globalOrientation, Vector3 sensorCoordinate, Versor sensorOrientation)
  {
    Vector3 delta = Vector3Ops.subtract(sourceCoordinate, globalCoordinate);
    return 1 / delta.norm2();
  }

  private class CountResponseEvaluatorFlat implements CountResponseEvaluator
  {
    private Vector3 globalCoordinate;
    private Vector3 sourceCoordinate;
    private double geometryFactor = 1;

    public CountResponseEvaluatorFlat()
    {
    }

    @Override
    public CountResponseFunction getResponseFunction()
    {
      return CountResponseFunctionFlat.this;
    }

    public double getGeometryFactor()
    {
      if (this.geometryFactor < 0)
      {
        geometryFactor = CountResponseFunctionFlat.this.getGeometryFactor(
                this.sourceCoordinate,
                this.globalCoordinate,
                null,
                null,
                null);
      }
      return this.geometryFactor;
    }

    @Override
    public double apply(Flux flux)
    {
      double geometricFactor = this.getGeometryFactor();
      double counts = 0;
      for (FluxGroup group : flux.getPhotonGroups())
      {
        counts += group.getCounts();
      }
      for (FluxLine line : flux.getPhotonLines())
      {
        counts += line.getIntensity();
      }
      return counts * area * efficiency * geometricFactor / 4 / Math.PI;
    }

    @Override
    public void setSourceCoordinate(Vector3 global)
    {
      this.sourceCoordinate = global;
      geometryFactor = -1;
    }

    @Override
    public void setInstrumentCoordinate(Vector3 global)
    {
      this.globalCoordinate = global;
      geometryFactor = -1;
    }

    @Override
    public void setInstrumentOrientation(Versor versor)
    {
      // Not used
    }

    @Override
    public Vector3 getSourceCoordinate()
    {
      return this.sourceCoordinate;
    }

    @Override
    public Vector3 getInstrumentCoordinate()
    {
      return this.globalCoordinate;
    }

    @Override
    public Versor getInstrumentOrientation()
    {
      return null;
    }

  }

}
