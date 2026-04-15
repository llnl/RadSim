// --- file: gov/llnl/rtk/response/SpectralBuffer.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.Spectrum;
import java.util.Arrays;

/**
 * A SpectralBuffer collects the information from the rendering process.
 *
 * The SpectralBuffer may either be partial or finished. Processes like adding
 * annihilation and LLD will not take place until the buffer is in the finished
 * state.
 *
 * The renderer always adds contributions so multiple fluxes can be summed
 * together so long as the buffer is not finished.
 *
 * Buffers can be reused for multiple calculations so long as they are cleared
 * between use.
 *
 * @author nelson85
 */
public class SpectralBuffer
{
  boolean finished = false;
  SpectralResponseEvaluator evaluator;
  EnergyScale energyScale;
  double[] target;
  double[] edges;
  SpectralBufferDeferred deferred = null;

  /**
   * Convert the buffer to a Spectrum.
   *
   * This will finish the rendering.
   *
   * @return
   */
  public DoubleSpectrum toSpectrum()
  {
    // Make sure the LLD and annihilation were included.
    finish();
    return Spectrum.builder().scale(evaluator.getEnergyScale())
            .counts(target).time(1.0).asDouble();
  }

  /**
   * Convert the buffer to a counts.
   *
   * This will finish the rendering.
   *
   * @return
   */
  public double[] getCounts()
  {
    finish();
    return this.target;
  }

  /**
   * Assign the buffer to an evaluator for calculation.
   *
   * @param eval
   */
  public void set(SpectralResponseEvaluator eval)
  {
    if (finished)
      throw new RuntimeException("Buffer reused");

    if (this.evaluator!=eval)
    {
      this.evaluator = eval;
      this.deferred = eval.deferred();
    }

    EnergyScale scale = eval.getEnergyScale();
    if (scale != energyScale)
    {
      this.energyScale = scale;
      this.edges = scale.getEdges();
    }
    this.evaluator = eval;
    if (target == null)
      target = new double[energyScale.getChannels()];
  }

  /**
   * Force the evaluator to finish the buffer.
   *
   * When the buffer is finished things like deferred annihilation and LLD would
   * be applied. The buffer cannot add any addition flux once finished.
   */
  private void finish()
  {
    if (finished)
      return;
    this.evaluator.finish(this);
    finished = true;
  }

  /**
   * Reset the buffer for reuse.
   *
   */
  public void clear()
  {
    Arrays.fill(target, 0, target.length, 0);
    deferred.clear();
    finished = false;
  }

  void checkNaN()
  {
    for (int i = 0; i < this.target.length; ++i)
      if (Double.isNaN(target[i]))
        throw new ArithmeticException("NaN");
  }

  /**
   * @return the target
   */
  public double[] getTarget()
  {
    return target;
  }

  public void resize(int i)
  {
    this.target = new double[i];
  }

}
