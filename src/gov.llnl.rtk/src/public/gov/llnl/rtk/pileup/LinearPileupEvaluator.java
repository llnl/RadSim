// --- file: gov/llnl/rtk/pileup/LinearPileupEvaluator.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.pileup;

import gov.llnl.math.IntegerArray;
import gov.llnl.math.random.Random48;
import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.IntegerSpectrum;
import java.util.Arrays;

/**
 *
 * @author nelson85
 */
public class LinearPileupEvaluator implements PileupEvaluator
{
  private final TriggerModel trigger;
  private final ShaperModel shaper;

  RandomGenerator random = new Random48();
  double[] working = new double[16000]; // the maximum we can draw in one pass
  int start;
  int end;

  int[] output;

  // These two must at most add up to 1.
  int[] N = new int[4];
  int overflow;
  private double realTime;
  private double liveTime;
  private EnergyScale energyScale;
//  private double average;

  public LinearPileupEvaluator(TriggerModel trigger, ShaperModel shaper)
  {
    this.trigger = trigger;
    this.shaper = shaper;
  }

  void setRandom(RandomGenerator random)
  {
    this.random = random;
  }

  /**
   * Create a draw from this spectrum.Assumes that the binning is uniform.This
   * implementation is good up to 30% deadtime and accuracy will fall off
   * afterwards.
   *
   *
   * @param spectrum
   * @param countScalar
   * @param timeScalar
   * @return an array holding the draw. This array will be destroyed the next
   * time this method is called.
   *
   */
  @Override
  public int[] draw(DoubleSpectrum spectrum, double countScalar, double timeScalar)
  {
    double time = spectrum.getRealTime();
    double[] distribution = spectrum.toDoubles();
    double total = spectrum.getCounts();
    this.realTime = time * timeScalar;
    this.overflow = 0;
    this.energyScale = spectrum.getEnergyScale();

    // reuse the output array if possible.
    if (output == null || output.length != distribution.length)
      output = new int[distribution.length];
    else
      IntegerArray.fill(output, 0);

    // Convert to a rate
    double rate = total * countScalar;

    // Distribute the counts by event type
    trigger.drawDistribution(N, random, rate, time * timeScalar);

    // This is only right for non-paralyzable (not usable for nonpara
    this.liveTime = this.realTime - trigger.getTau() * IntegerArray.sum(N);

    // Decide how many photons we need to draw
    int need = N[0] + N[1] * 2 + N[2] * 3 + N[3] * 4; // + N[4] + N[5]) * 4;
//    this.average = 0;
//    if (N[3] > 0)
//    {
//      double sm = 0;
//      for (int i = 0; i < distribution.length; ++i)
//      {
//        sm += i * distribution[i];
//      }
//      this.average = sm / total;
//    }

    // up the memory if we are doing huge draws
//    if (need > 10 * working.length)
//      working = new double[need / 8];
    // Draw in batches to save time
    while (need > working.length)
    {
      produce(working.length, distribution, total);
      consume();
      need -= working.length;
    }
    produce(need, distribution, total);
    consume();
    return output;
  }

  @Override
  public IntegerSpectrum toSpectrum()
  {
    IntegerSpectrum out = new IntegerSpectrum(this.output.clone(), this.liveTime, this.realTime);
    out.setOverRange(this.overflow);
    out.setEnergyScale(energyScale);
    return out;
  }

  @Override
  public double getLiveTime()
  {
    return this.liveTime;
  }

  @Override
  public double getRealTime()
  {
    return this.realTime;
  }

  /**
   * Block wise fill of photons.
   *
   * The photons are in sorted order.
   *
   * @param N
   * @param spectrum
   * @param total
   */
  private void produce(int N, double[] spectrum, double total)
  {
    // Fill with new randoms
    for (int i = 0; i < N; i++)
      working[i] = random.nextDouble() * total;

    // Sort for fast conversion
    Arrays.sort(working, 0, N);
    start = 0;
    end = N;

    // Convert to channels
    int j = 0;
    double sum = 0;
    for (int i = 0; i < spectrum.length; i++)
    {
      sum += spectrum[i];
      while (j < N && working[j] < sum)
      {
        working[j] = i;
        j++;
      }
      if (j == N)
        break;
    }
  }

  /**
   * Block wise consumption of photons.
   */
  private void consume()
  {

    // Consume pileup of 4
    while (end - start > 4 && N[3] > 0)
    {
      pileup(4);
      N[3]--;
    }
    // Consume pileup of 3
    while (end - start > 3 && N[2] > 0)
    {
      pileup(3);
      N[2]--;
    }
    // Consume pileup of 2
    while (end - start > 2 && N[1] > 0)
    {
      pileup(2);
      N[1]--;
    }
    // Rest are singles
    while (start != end && N[0] > 0)
    {
      this.output[(int) working[start]]++;
      start++;
      N[0]--;
    }
  }

  /**
   * Routine to simulate the effects of n photons.
   *
   * @param n
   */
  private void pileup(int n)
  {
    double total = 0;
//    while (n > 4)
//    {
//      total += random.nextDouble() * average;
//      n--;
//    }
    for (int i = 0; i < n; i++)
    {
      double f = 1;
      // last is full deposition (so we have a chance of rejecting earlier)
      if (i != n - 1)
        f = shaper.draw(random);
      // pileup in recovery
      if (f==0)
        continue;
      // Pileup rejected
      if (f < 0)
        return;

      // Randomly permute to get the next photon
      int j = (int) (start + (end - start) * random.nextDouble());
      double k = working[j];
      working[j] = working[start];
      start++;

      total += k * f;
    }
    if (total > this.output.length)
      this.overflow++;
    else
      this.output[(int) total]++;
  }

}
//  Speed test results
//  uniform is 16 to 25 times faster than Poisson
//    for less that 20000 counts in the spectrum it is faster to use uniform for 1024 channel NaI data.
//    for less that 150,000 counts in spectrum is is faster to use uniform for 8000 channel HPGE data.
//  Conclusion for anything but directly on a source to use sorted uniform draw.
//   (but this assumes the sort and shuffle operations are trivial which may not be true)
//  for low count data where the counts are less than 10x the total channels we should use uniform
//
//  public static void testPoisson(RandomGenerator random, long n)
//  {
//    for (long i = 0; i < n; ++i)
//    {
//      PoissonRandom.draw(random, 2);
//    }
//  }
//
//  public static void testUnform(RandomGenerator random, long n)
//  {
//    for (long i = 0; i < n; ++i)
//    {
//      random.nextDouble();
//    }
//  }
//
//  public static void main(String[] args)
//  {
//
//    // This test is to resolve the relative cost of drawing poisson values for channels independently
//    // vs converting into a cdf first than drawing uniform
//    RandomGenerator generator = new Random48();
//    testPoisson(generator, 10000000);
//    testUnform(generator, 102100000);
//  }
