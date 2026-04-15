// --- file: gov/llnl/rtk/response/SpectralResponseFunctionNull.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.Cursor;
import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Vector3Ops;
import gov.llnl.math.euclidean.Versor;
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxItem;
import gov.llnl.rtk.flux.FluxSpectrum;
import gov.llnl.rtk.flux.FluxUtilities;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author nelson85
 */
public class SpectralResponseFunctionNull extends SpectralResponseFunctionBase
{

  public SpectralResponseFunctionNull(EnergyScale energyScale)
  {
    this.energyScale = energyScale;
    this.model = "";
    this.vendor = "null";
  }

  @Override
  public SpectralResponseEvaluator newEvaluator()
  {
    return new SpectralNullEvaluator();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof SpectralResponseFunctionNull))
      return false;
    SpectralResponseFunctionNull rf = (SpectralResponseFunctionNull) obj;
    return Objects.equals(this.energyScale, rf.energyScale);
  }

  @Override
  public double getGeometryFactor(Vector3 sourceCoordinate,
          Vector3 globalCoordinate, Versor globalOrientation,
          Vector3 sensorCoordinate, Versor sensorOrientation)
  {
    Vector3 delta = Vector3Ops.subtract(sourceCoordinate, globalCoordinate);
    return 1 / delta.norm2();
  }

  @Override
  public Map<String, Double> getParameters()
  {
    return Collections.EMPTY_MAP;
  }

//<editor-fold desc="evaluator" defaultstate="collapsed">
  /**
   * Specialized evaluator for
   */
  class SpectralNullEvaluator extends SpectralResponseEvaluatorBase
  {

    SpectralNullEvaluator()
    {
      super(SpectralResponseFunctionNull.this,
              SpectralResponseFunctionNull.this.energyScale);
    }

    @Override
    public Spectrum apply(Flux flux)
    {
      double geometricFactor = this.getGeometryFactor();
      if (!viewEnabled)
        geometricFactor = 1.0;
      FluxSpectrum spectrum = FluxUtilities.toSpectrum(flux, energyScale, FluxItem.ALL);
      double[] counts = spectrum.getPhotonCounts();
      return Spectrum.builder().scale(energyScale).counts(DoubleArray.multiplyAssign(counts, geometricFactor)).asDouble();
    }

    @Override
    public void renderLine(SpectralBuffer buffer,
            double energy, double intensity)
    {
      buffer.set(this);
      double[] edges = energyScale.getEdges();
      if (energy < edges[0])
        return;
      if (energy > edges[edges.length - 1])
        return;
      int i = Cursor.findInterval(edges, 0, edges.length, energy);
      buffer.target[i] += intensity * getGeometryFactor();
    }

    @Override
    public void renderGroup(SpectralBuffer buffer,
            double energy0, double density0,
            double energy1, double density1)
    {
      throw new UnsupportedOperationException(); // not used
    }

    @Override
    public DoubleUnaryOperator getResolutionFunction()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void finish(SpectralBuffer buffer)
    {
    }

    @Override
    public Spectrum getInternal()
    {
      return null;
    }

    @Override
    public DoubleUnaryOperator getEfficiencyFunction()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public SpectralBufferDeferred deferred()
    {
      return null;
    }

    @Override
    public double getLower()
    {
      return 0;
    }

    @Override
    public void setRenderItems(Set<RenderItem> renderItems)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<RenderItem> getRenderItems()
    {
      return RenderItem.ALL;
    }

  }
//</editor-fold>
}
