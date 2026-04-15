// --- file: gov/llnl/rtk/response/SpectralResponseEvaluator.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.flux.Flux;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;

/**
 * Calculator for SpectralResponseFunction.
 *
 * Evaluators cache previous results for speed. Evaluators are not reentrant so
 * each thread should use different evaluators. Using the same evaluator for
 * different shielding materials with the same source will have the best
 * performance.
 *
 * @author nelson85
 */
public interface SpectralResponseEvaluator extends ResponseEvaluator
{

  /**
   * Set the desired energy scale for spectrum.
   *
   * @param scale
   */
  void setEnergyScale(EnergyScale scale);

  /**
   * Convert a flux into a spectrum.
   *
   * @param flux
   * @return
   */
  Spectrum apply(Flux flux);

  /**
   * Get the internal source for this detector.
   *
   * @return internal source with this energy scale or null if not available.
   */
  Spectrum getInternal();

//<editor-fold desc="getters" defaultstate="collasped">
  /**
   * Get the response function for this evaluator.
   *
   * @return the response function.
   */
  SpectralResponseFunction getResponseFunction();

  /**
   * Get the energy scale for this evaluator.
   *
   * @return the energy scale for this evaluator.
   */
  EnergyScale getEnergyScale();

  /**
   * Get the resolution function for photopeaks in the detector.
   *
   * Resolution is defined in terms of sigmas.
   *
   * @return
   */
  DoubleUnaryOperator getResolutionFunction();

  /**
   * Get the efficiency function for the photopeaks in the detector.
   *
   * @return
   */
  DoubleUnaryOperator getEfficiencyFunction();

  /**
   * @param renderItems the renderItems to set
   */
  void setRenderItems(Set<RenderItem> renderItems);

  Set<RenderItem> getRenderItems();
    
  default void setRenderItems(RenderItem... items)
  {
    if (items == null)
      this.setRenderItems(Collections.EMPTY_SET);
    else
      this.setRenderItems(EnumSet.of(items[0], items));
  }

//</editor-fold>
//<editor-fold desc="primitives" defaultstate="collapsed">
  double getGeometryFactor();

  void setGeometryFactor(double d);

  /**
   * Add a mono-energetic line to a buffer.This is a primitive command used to
   * build up spectral.
   *
   *
   * @param buffer is the buffer to be updated.
   * @param energy is the energy for the spectral line..
   * @param intensity is the intensity of the line.
   */
  void renderLine(SpectralBuffer buffer, double energy, double intensity);

  /**
   * Add a spectral continuum to a buffer.
   *
   * @param buffer is the buffer to be updated.
   * @param energy0 is the starting energy of this region.
   * @param density0 is the density at energy0.
   * @param energy1 is the ending energy of this region.
   * @param density1 is the density at energy1.
   */
  void renderGroup(SpectralBuffer buffer,
          double energy0, double density0,
          double energy1, double density1
  );

  SpectralBufferDeferred deferred();

  void finish(SpectralBuffer buffer);

  /**
   * Get the lowest energy rendered
   *
   * @return
   */
  double getLower();

//</editor-fold>
}
