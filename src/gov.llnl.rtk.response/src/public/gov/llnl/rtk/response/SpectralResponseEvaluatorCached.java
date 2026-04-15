// --- file: gov/llnl/rtk/response/SpectralResponseEvaluatorCached.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.Spectrum;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;

/**
 * Proxy to a SpectralResponseEvaluator with caching.
 *
 * This is an evaluator which is backed by a cache to speed up computations when
 * the same spectrum is being responsed with different shielding conditions.
 *
 * @author nelson85
 */
public class SpectralResponseEvaluatorCached extends SpectralResponseEvaluatorBase
{

  SpectralResponseEvaluator eval;
  LineCache<SpectrumCache> lineCache = new LineCache<>(2000);
  GroupCache<SpectrumCache[]> groupCache = new GroupCache<>(2000);
  private Set<RenderItem> renderItems;

  public SpectralResponseEvaluatorCached(SpectralResponseFunction drf)
  {
    super(drf, drf.getEnergyScale());
    this.eval = drf.newEvaluator();
    this.eval.setEnergyScale(this.getEnergyScale());
    this.eval.setGeometryFactor(1.0);
    this.renderItems = eval.getRenderItems();
  }

  public void setCacheSize(int size)
  {
    lineCache = new LineCache<>(size);
    groupCache = new GroupCache<>(size);
  }

  @Override
  public void setEnergyScale(EnergyScale scale)
  {
    // Short circuit if energy scale is the same
    if (this.energyScale.equals(scale))
      return;

    super.setEnergyScale(scale);

    // Update the evaluator
    eval.setEnergyScale(scale);

    // Dump the cache
    lineCache.clear();
    groupCache.clear();
  }

  @Override
  public void renderLine(SpectralBuffer buffer, double energy, double intensity)
  {
    buffer.set(eval);
    if (energy < this.getLower())
      return;

    // Consult the cache first
    SpectrumCache spec = lineCache.get(energy);
    if (spec == null)
    {
      // If not found, update the cache
      SpectralBuffer buffer0 = new SpectralBuffer();
      eval.setRenderItems(this.renderItems);
      eval.renderLine(buffer0, energy, 1.0);
      spec = new SpectrumCache(buffer0.target);
      lineCache.put(energy, spec);
    }

    // Scale for the required line intensity.
    DoubleArray.addAssignScaled(buffer.target, spec.range[0],
            spec.spectrum, spec.range[0],
            spec.range[1] - spec.range[0], intensity * getGeometryFactor());
  }

  @Override
  public void renderGroup(SpectralBuffer buffer,
          double energy0, double density0,
          double energy1, double density1)
  {
    buffer.set(eval);

    // Check the cache.
    SpectrumCache[] pair = groupCache.get(energy0, energy1);

    // If not found, update the cache
    if (pair == null)
      pair = computeGroup(energy0, energy1);

    // Scale for the required group intensities.
    double gf = getGeometryFactor();
    DoubleArray.addAssignScaled(buffer.target, pair[0].range[0],
            pair[0].spectrum, pair[0].range[0],
            pair[0].range[1] - pair[0].range[0], density0 * gf);
    DoubleArray.addAssignScaled(buffer.target, pair[1].range[0],
            pair[1].spectrum, pair[1].range[0],
            pair[1].range[1] - pair[1].range[0], density1 * gf);
  }

  /**
   * Compute a trapezoid front and back for an energy group.
   *
   * These are cached for speed.
   *
   * @param energy0
   * @param energy1
   * @return
   */
  synchronized SpectrumCache[] computeGroup(double energy0, double energy1)
  {
    double[][] pair = new double[2][];
    SpectralBuffer buffer0 = new SpectralBuffer();
    SpectralBuffer buffer1 = new SpectralBuffer();
    eval.setRenderItems(RenderItem.ALL);
    eval.renderGroup(buffer0, energy0, 1, energy1, 0);
    eval.renderGroup(buffer1, energy0, 0, energy1, 1);
    pair[0] = buffer0.target;
    pair[1] = buffer1.target;
    var result = new SpectrumCache[]
      {
        new SpectrumCache(pair[0]), new SpectrumCache(pair[1])
      };
    groupCache.put(energy0, energy1, result);
    return result;
  }

  @Override
  public DoubleUnaryOperator getResolutionFunction()
  {
    return this.eval.getResolutionFunction();
  }

  @Override
  public void finish(SpectralBuffer buffer)
  {
    buffer.set(this);
    eval.finish(buffer);
  }

  @Override
  public Spectrum getInternal()
  {
    return this.eval.getInternal();
  }

  @Override
  public DoubleUnaryOperator getEfficiencyFunction()
  {
    return this.eval.getEfficiencyFunction();
  }

  @Override
  public void setRenderItems(Set<RenderItem> renderItems)
  {
    this.renderItems = renderItems;
    this.groupCache.clear();
    this.lineCache.clear();
  }

  @Override
  public SpectralBufferDeferred deferred()
  {
    return this.eval.deferred();
  }

  @Override
  public double getLower()
  {
    return this.eval.getLower();
  }

  @Override
  public Set<RenderItem> getRenderItems()
  {
    return this.renderItems;
  }

  private class SpectrumCache
  {
    int[] range;
    double[] spectrum;

    SpectrumCache(double[] spectrum)
    {
      this.spectrum = spectrum;
      this.range = findNonZeroBounds(spectrum, 1e-15);
    }

    /**
     * Finds the first and last indices in the spectrum array where the value
     * exceeds epsilon. Returns [-1, -1] if no such bin is found.
     */
    public static int[] findNonZeroBounds(double[] spectrum, double epsilon)
    {
      int first = -1;
      int last = -1;

      for (int i = 0; i < spectrum.length; i++)
      {
        if (Math.abs(spectrum[i]) > epsilon)
        {
          first = i;
          break;
        }
      }
      if (first == -1)
      {
        // All bins are below threshold
        return new int[]
        {
          -1, -1
        };
      }
      for (int i = spectrum.length - 1; i >= first; i--)
      {
        if (Math.abs(spectrum[i]) > epsilon)
        {
          last = i;
          break;
        }
      }
      return new int[]
      {
        first, last
      };
    }
  }

}
