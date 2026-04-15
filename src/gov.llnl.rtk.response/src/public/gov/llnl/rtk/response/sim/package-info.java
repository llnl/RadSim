// --- file: gov/llnl/rtk/response/sim/package-info.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.sim;

/**
 * Provides simulation tools for geometric chord length distributions, including
 * Monte Carlo engines and analytic evaluators for cuboids, cylinders, and related shapes.
 *
 * <p>
 * This package is designed for geometric validation, analytic modeling, and machine learning
 * applications in radiation transport and related fields. Simulations include random chord
 * sampling, ray tracing, and feature extraction for statistical or ML workflows.
 * </p>
 *
 * <h2>Thread Safety Warning</h2>
 * <p>
 * <b>These classes are <em>not</em> thread-safe.</b> Each simulation engine and shape instance
 * is intended to be used as a calculator or evaluator with a single thread. If you require
 * parallel processing, create a separate instance of each class per thread.
 * </p>
 */