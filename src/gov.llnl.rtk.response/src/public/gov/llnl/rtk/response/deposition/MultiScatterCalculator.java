// --- file: gov/llnl/rtk/response/deposition/MultiScatterCalculator.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

/**
 * Interface for multi-scatter photon fate calculators.
 * <p>
 * Implementations compute the fate (escape, absorption, scatter spectrum) of
 * photons in a detector geometry, supporting both direct and pair-production
 * events.
 * </p>
 *
 * <p>
 * Usage:
 * <ul>
 * <li>Call {@link #computeResponse()} to initialize or update the
 * calculator.</li>
 * <li>Query photon fate for a specific incident energy using
 * {@link #getFate(double)}.</li>
 * <li>Query the fate for pair production events using {@link #getPair()}.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Implementations are not guaranteed to be thread-safe; use one instance per
 * thread if needed.
 * </p>
 */
public interface MultiScatterCalculator
{
  /**
   * Compute all photon fates for the current configuration. Must be called
   * before querying fates.
   */
  void computeResponse();

  /**
   * Query the fate for a photon of the specified incident energy. Units: keV.
   *
   * @param energy Incident photon energy (keV)
   * @return The fate nearest to the requested energy
   */
  PhotonFate getFate(double energy);

  /**
   * Get the fate for pair production events (1022 keV).
   *
   * @return The pair fate
   */
  PhotonFate getPair();
}
