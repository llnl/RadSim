// --- file: gov/llnl/rtk/response/sim/BatchManager.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.sim;

import gov.llnl.math.random.NormalRandom;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Analytic cumulative distribution function (CDF) for chord lengths through a
 * right circular cylinder.
 * <p>
 * This class provides closed-form or semi-analytic expressions for the
 * cumulative distribution of chord lengths (the lengths of random straight-line
 * segments) through a cylinder. It is useful for Monte Carlo validation,
 * analytic modeling, and detector or shielding simulations involving random
 * penetrations of cylindrical volumes.
 * </p>
 *
 *
 * <h2>References</h2>
 * <ul>
 * <li>J. Santaló, "Integral Geometry and Geometric Probability,"
 * Addison-Wesley, 1976. (General background on chord length distributions)</li>
 * <li>J. S. Coleman, "Chord Lengths in Right Circular Cylinders," Mathematical
 * Gazette, Vol. 49, No. 369 (1965), pp. 364–367.</li>
 * <li>Wikipedia contributors. "Bertrand paradox (probability)", Wikipedia, The
 * Free Encyclopedia. (Disk case)</li>
 * </ul>
 *
 * <p>
 * <b>Usage:</b> Instantiate, call {@link #setSize(double, double)}, then call
 * {@link #eval(double)} for desired chord lengths.
 * </p>
 *
 * <h2>Limitations</h2>
 * <ul>
 * <li>Assumes a right circular cylinder with positive radius and height.</li>
 * <li>Not intended for degenerate or near-zero dimensions.</li>
 * </ul>
 *
 * @author nelson85
 */
public class BatchManager
{
  NormalRandom nd = new NormalRandom();
  Random rand = new Random();

  // Default is currently 8M samples in queue
  public int over = 20;
  public int samples = 400;
  public int batches = 800; // 8000
  int capacity;

  double[][] featurePool;
  double[] x0Pool;
  double[] x1Pool;
  int count = 0;

  public double[] tmp1;
  public double[] tmp0;

  // Outputs for draw
  public double[][] features;
  public double[] rank;  // rank
  public double[] length;  // length/diagonal

  public boolean linear = true;
  public boolean sort = true;

  private final MonteSim monte;
  public double[] batchFeatures;
  public double[] importance;
  public boolean useImportance = true;

  /**
   * Create a batch manager for the simulation.
   *
   * Current takes CuboidSim.runDirect, CuboidSim.runScatter, CuboidSim.runChord
   *
   * @param monte
   */
  public BatchManager(MonteSim monte)
  {
    this.monte = monte;
  }

  /**
   * Sets the random seed for all underlying random number generators used in
   * batch sampling.
   * <p>
   * This method ensures reproducibility of the simulation by initializing the
   * internal {@link Random}, {@link NormalRandom}, and the associated
   * {@link MonteSim} simulation engine with deterministic seeds derived from
   * the provided base value. Each generator receives a unique offset to avoid
   * correlation between their output streams.
   * </p>
   *
   * @param seed the base seed value to initialize all random number generators
   */
  public void seed(long seed)
  {
    this.rand.setSeed(seed);
    this.nd.setSeed(seed + 1000);
    this.monte.setSeed(seed + 2000);
  }

  /**
   * Initializes and fills the internal sample pools for the batch simulation.
   * <p>
   * This method allocates memory for all feature and sample arrays based on the
   * configured batch and sample sizes, and repeatedly fills the pools with
   * simulated data until the desired capacity is reached. It must be called
   * before drawing samples using {@link #fetch(int)}.
   * </p>
   *
   * <p>
   * The method is typically invoked at the start of a simulation or whenever
   * the configuration changes, ensuring that all sample buffers are populated
   * and ready for randomized retrieval.
   * </p>
   */
  public void initial()
  {
    capacity = samples * batches;
    featurePool = new double[capacity][];
    x0Pool = new double[capacity];
    x1Pool = new double[capacity];
    tmp1 = new double[samples * over];
    tmp0 = new double[samples * over];
  }

  /**
   * Requests a new simulation configuration and updates the current feature
   * set.
   * <p>
   * This method generates a new random configuration (e.g., shape geometry,
   * emission point, and scenario parameters) by invoking
   * {@link MonteSim#nextConfiguration()}. The resulting feature vector is
   * stored in {@link #batchFeatures} for use in subsequent sampling operations.
   * </p>
   */
  public void changeConfiguration()
  {
    if (capacity == 0)
      initial();

    // Generate a new shape with a set of parameters
    batchFeatures = this.monte.nextConfiguration();
  }

  /**
   * Draws a new set of sample values for the current batch configuration.
   * <p>
   * This method fills the temporary sample arrays with values drawn from either
   * a linear (uniform) or normal (Gaussian) distribution, depending on the
   * {@link #linear} flag. It then draws corresponding target values from the
   * simulation scenario via {@link MonteSim#draw(double[])}. Optionally, both
   * sets of samples can be sorted for improved statistical smoothness.
   * </p>
   * <p>
   * If the underlying simulation scenario cannot provide valid samples (e.g.,
   * due to a degenerate geometry), the method returns {@code false} to indicate
   * that the current configuration should be skipped.
   * </p>
   *
   * @return {@code true} if samples were successfully drawn; {@code false}
   * otherwise
   */
  public boolean drawSamples()
  {
    if (batchFeatures == null)
      throw new RuntimeException("We must have a configuration to draw samples from");
    // Draw from the gaussian distribution
    if (!linear)
    {
      // This is for norm -> length simulation.  
      // We won't be using this mode.
      for (int i = 0; i < tmp0.length; ++i)
        tmp0[i] = nd.draw();
      if (sort)
        Arrays.sort(tmp0);
    }
    else
    {
      // Fill with 0 to 1
      for (int i = 0; i < tmp0.length; ++i)
      {
        // Kai recommends always inserting slight amounts of noise into the process
        // FM does not like delta functions
        double v = (0.5 + i) / tmp0.length + 0.01 / this.samples * nd.draw();
        if (v < 0)
          v = 0;
        if (v > 1)
          v = 1;
        tmp0[i] = v;
      }
    }

    // Draw from the chord distibution
    try
    {
      this.monte.draw(tmp1);
    }
    catch (NoSuchElementException ex)
    {
      System.out.println("bad shape " + ex + " " + this.batchFeatures);
      // This shape has problems, skip it.
      return false;
    }

    // Line up all the samples to make for a smooth field
    if (sort)
      Arrays.sort(tmp1);

    if (useImportance)
    {
      // Now compute importance using pointwise linear interpolation
      int n = tmp1.length;
      if (importance == null || importance.length != n)
        importance = new double[n];
      int neighborCount = 5; // or configurable

      for (int i = 0; i < n; ++i)
      {
        int left = Math.max(i - neighborCount, 0);
        int right = Math.min(i + neighborCount, n - 1);

        // Only compute if we have both neighbors; otherwise, set to 0 or handle as needed
        double xL = tmp0[left], yL = tmp1[left];
        double xR = tmp0[right], yR = tmp1[right];
        double xC = tmp0[i];

        // Linear interpolation
        double f = (xC - xL) / (xR - xL);
        if (f < 0.25)
        {
          xL = tmp0[(left + right) / 2];
          yL = tmp1[(left + right) / 2];
          f = (xC - xL) / (xR - xL);
        }
        if (f > 0.75)
        {
          xR = tmp0[(left + right) / 2];
          yR = tmp1[(left + right) / 2];
          f = (xC - xL) / (xR - xL);
        }
        double interp = (1 - f) * yL + f * yR;
        importance[i] = Math.abs(tmp1[i] - interp);
      }
    }
    return true;
  }

  /**
   * Randomly selects a subset of samples from the temporary arrays and adds
   * them to the pool.
   * <p>
   * This method draws {@link #samples} entries without replacement from the
   * current temporary sample arrays ({@link #tmp0} and {@link #tmp1}), storing
   * the selected feature vectors and sample values in the main pools. Each
   * selected sample is removed from the temporary arrays to prevent
   * duplication.
   * </p>
   * <p>
   * The process continues until the pool reaches its configured
   * {@link #capacity}.
   * </p>
   */
  public void sampleSubSet()
  {
    int n = tmp0.length;
    // Build an array of available indices
    int[] available = new int[n];
    for (int i = 0; i < n; ++i)
      available[i] = i;
    int availableCount = n;

    double maxImportance = 0;
    if (useImportance)
    {
      // Find the maximum importance for rejection sampling
      maxImportance = importance[0];
      for (int i = 1; i < n; ++i)
        if (importance[i] > maxImportance)
          maxImportance = importance[i];
    }

    int selected = 0;
    while (selected < samples && availableCount > 0)
    {
      int idx = rand.nextInt(0, availableCount);
      int k = available[idx];
      double u = rand.nextDouble() * maxImportance;

      double imp = 1;
      if (useImportance)
        imp = importance[k];

      if (u >= imp)
        continue;
      // Accept this sample
      featurePool[count] = batchFeatures; // Everything in a sample batch has the same features
      x0Pool[count] = tmp0[k];
      x1Pool[count] = tmp1[k];
      count++;
      selected++;

      // Remove k from available (swap with last)
      available[idx] = available[availableCount - 1];
      availableCount--;

      // Stop if full
      if (count == capacity)
        break;
    }
    // If rejected, do nothing (try again)
  }

  double[] thresholds = null;

  /**
   * Performs weighted random sampling without replacement from the current batch.
   * <p>
   * This method selects up to {@code samples} items, each with an associated importance
   * weight, such that the probability of selection is proportional to the item's importance.
   * Sampling is performed without replacement: each item can be selected at most once per call.
   * <p>
   * The algorithm operates as follows:
   * <ul>
   *   <li>Computes the total importance across all items.</li>
   *   <li>Generates {@code samples} uniformly random thresholds in the range [0, totalImportance), and sorts them.</li>
   *   <li>For each threshold, advances through the items using a running cumulative sum of importance,
   *       and selects the first item whose cumulative sum meets or exceeds the threshold.</li>
   *   <li>If a threshold would select the same item as the previous threshold (a "repeat"), it is counted as a repeat and retried in a subsequent pass.</li>
   *   <li>The process is repeated for up to three passes, each time attempting to fill any remaining samples using new random thresholds.
   *       If fewer than 10 repeats remain after any pass, the process terminates early.</li>
   * </ul>
   * <p>
   * This approach is efficient (O(n + samples)), requires no extra cumulative sum array,
   * and works for both uniform and importance-weighted sampling.
   * <p>
   * <b>Note:</b> In rare pathological cases (e.g., highly skewed importance or nearly full sampling),
   * the method may select fewer than {@code samples} items. This is by design and indicates that
   * the remaining samples could not be filled without excessive retries. Underfilling is considered
   * acceptable for most operational use cases.
   * <p>
   * <b>References:</b>
   * <ul>
   *   <li>Efraimidis, P. S., & Spirakis, P. G. (2006). Weighted random sampling with a reservoir.
   *       Information Processing Letters, 97(5), 181-185.</li>
   * </ul>
   *
   * @implNote The method fills the {@code featurePool}, {@code x0Pool}, and {@code x1Pool}
   * arrays with the selected items, and updates {@code count} to reflect the number of samples chosen
   * (at most {@code samples}, {@code n}, or {@code capacity}). If {@code useImportance} is false,
   * uniform sampling is performed.
   *
   * @throws IllegalStateException if the input arrays are inconsistent in length.
   */
  public void sampleSubSet2()
  {
    int n = tmp0.length;
    double totalImportance = 0.0;

    // Compute total importance
    if (useImportance)
    {
      for (int i = 0; i < n; ++i)
        totalImportance += importance[i];
    }
    else
    {
      for (int i = 0; i < n; ++i)
        totalImportance += 1.0;
    }

    // Generate sorted random thresholds in [0, totalImportance)
    if (thresholds == null || thresholds.length != samples)
      thresholds = new double[samples];

    int draws = samples;
    for (int pass=0; pass<3; ++pass)
    {
      for (int i = 0; i < draws; ++i)
        thresholds[i] = rand.nextDouble() * totalImportance;
      Arrays.sort(thresholds, 0, draws);

      int cumsumIdx = 0;
      double cumsum = 0;
      int last = -1;
      int repeats = 0;
      for (int t = 0; t < draws && count < capacity && cumsumIdx < n; ++t)
      {
        double threshold = thresholds[t];
        // Advance cumsumIdx until we reach the threshold
        while (cumsumIdx < n && cumsum < threshold)
        {
          cumsum += importance[cumsumIdx++];
        }
        if (last == cumsumIdx)
        {
          repeats++;
          continue;
        }
        if (cumsumIdx == n)
          break; // Safety: no more items to select

        // Accept this sample
        featurePool[count] = batchFeatures;
        x0Pool[count] = tmp0[cumsumIdx];
        x1Pool[count] = tmp1[cumsumIdx];
        last = cumsumIdx;
        count++;
      }
      draws = repeats;
      if (draws<10)
        break;
    }
    // At this point, count == selected == number of samples chosen (≤ samples, ≤ n, ≤ capacity)
  }

  public boolean fill()
  {
    // Already full
    while (count != capacity)
    {
      // Generate a new shape with a set of parameters
      batchFeatures = this.monte.nextConfiguration();

      if (!drawSamples())
        continue;

      if (over != 1)
      {
        sampleSubSet2();
      }
      else
      {
        // Use all
        for (int i = 0; i < samples; i++)
        {
          featurePool[count] = batchFeatures; // Everything in a sample batch has the same features
          x0Pool[count] = tmp0[i];
          x1Pool[count] = tmp1[i];
          // Add to the pool
          count++;
          // Once we are full quit
          if (count == capacity)
            break;
        }
      }
      break;
    }

    return count == capacity;
  }

  /**
   * Fetches a batch of samples from the pool for downstream use.
   * <p>
   * This method randomly selects {@code batch} samples without replacement from
   * the current pool, storing the selected feature vectors, ranks, and lengths
   * in the corresponding public arrays
   * ({@link #features}, {@link #rank}, {@link #length}). The selected samples
   * are removed from the pool and replaced by the last entries to maintain pool
   * integrity.
   * </p>
   * <p>
   * If the pool does not contain enough samples, it is automatically refilled
   * by invoking {@link #initial()} as needed.
   * </p>
   *
   * @param batch the number of samples to fetch from the pool
   */
  public void fetch(int batch)
  {
    if (count < samples)
    {
      this.initial();
      while (capacity - count >= samples)
        fill();
    }

    if (features == null || features.length != batch)
    {
      features = new double[batch][];
      rank = new double[batch];
      length = new double[batch];
    }

    for (int i = 0; i < batch; ++i)
    {
      // Draw without replacement
      int n = rand.nextInt(0, count);
      count--;

      // Take the drawn sample
      features[i] = featurePool[n];
      rank[i] = x0Pool[n];
      length[i] = x1Pool[n];

      // Rotate out to fill the taken sample
      featurePool[n] = featurePool[count];
      x0Pool[n] = x0Pool[count];
      x1Pool[n] = x1Pool[count];

      // Refill when needed
      if (capacity - count >= samples)
        fill();
    }

  }

  /**
   * @return the monte
   */
  public MonteSim getMonte()
  {
    return monte;
  }

}
