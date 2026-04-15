// --- file: gov/llnl/rtk/calibration/ChannelEnergyUtilities.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.MathAssert;
import gov.llnl.math.SpecialFunctions;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.rtk.data.EnergyRegionOfInterest;
import gov.llnl.rtk.data.ChannelEnergySetImpl;
import gov.llnl.rtk.model.ResolutionModel;
import gov.llnl.utility.annotation.Matlab;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Collection of extension methods to operate on a collection of channel energy
 * pairs.
 *
 * @author nelson85
 */
public class ChannelEnergyUtilities
{
  public static ChannelEnergySet newSet(ChannelEnergyPair.Origin type, double[] channels, double[] energies)
  {
    MathAssert.assertEqualLength(channels, energies);
    ChannelEnergyPair[] pairs = new ChannelEnergyPair[channels.length];
    for (int i = 0; i < channels.length; ++i)
      pairs[i] = new ChannelEnergyPair(channels[i], energies[i], type);
    return new ChannelEnergySetImpl(Arrays.asList(pairs));
  }

  /**
   * Get the channels corresponding to the pairs with the center of the bin as
   * the origin.
   *
   * @param pairs
   * @return a new array storing the pairs channels with an origin in the center
   * of the first channel.
   */
  public static double[] getChannelsCenter(Collection<ChannelEnergyPair> pairs)
  {
    double[] out = new double[pairs.size()];
    int i = 0;
    for (ChannelEnergyPair pair : pairs)
    {
      out[i++] = pair.getChannelCenter();
    }
    return out;
  }

  /**
   * Get the channels corresponding to the pairs with the edge of the bin as the
   * origin.
   *
   * @param pairs
   * @return a new array with the channel definition for each pair with an
   * origin at the edge.
   */
  public static double[] getChannelsEdge(ChannelEnergySet pairs)
  {
    double[] out = new double[pairs.size()];
    int i = 0;
    for (ChannelEnergyPair pair : pairs)
    {
      out[i++] = pair.getChannelEdge();
    }
    return out;
  }

  /**
   * Get the energies for the pairs.
   *
   * @param pairs
   * @return a new list with the energies for each of the channels.
   */
  public static double[] getEnergies(Collection<ChannelEnergyPair> pairs)
  {
    double[] out = new double[pairs.size()];
    int i = 0;
    for (ChannelEnergyPair pair : pairs)
    {
      out[i++] = pair.getEnergy();
    }
    return out;
  }

  /**
   * Get a list of all the labels on the pair. Primarily used for debugging in
   * Matlab.
   *
   * @param pairs
   * @return a new list with the labels for each pair if set.
   */
  @Matlab
  public static String[] getLabels(Collection<ChannelEnergyPair> pairs)
  {
    String[] out = new String[pairs.size()];
    int i = 0;
    for (ChannelEnergyPair pair : pairs)
    {
      out[i++] = pair.getLabel();
    }
    return out;
  }

  static public double[][] toArray(Collection<ChannelEnergyPair> pairs)
  {
    return new double[][]
    {
      getChannelsCenter(pairs), getEnergies(pairs)
    };
  }

  /**
   * Debugging method for use with Matlab.
   *
   * @param pairs
   * @return a new matrix storing the channel centers and the energies.
   */
  @Matlab
  static public Matrix toMatrix(Collection<ChannelEnergyPair> pairs)
  {
    return MatrixFactory.createFromArray(toArray(pairs), false);
  }

  public static ChannelEnergyPair scalePair(ChannelEnergyPair pair, double scale)
  {
    double energy = pair.getEnergy();
    double channel = (pair.getChannelEdge()) * scale;
    ChannelEnergyPair out = new ChannelEnergyPair(channel, energy);
    out.copyAttributes(pair);
    return out;
  }

  /**
   * Remove all pairs that fall within a specified region of interest.
   *
   * @param pairs
   * @param roi is the region of interest to remove.
   */
  public static void remove(Iterable<ChannelEnergyPair> pairs, EnergyRegionOfInterest roi)
  {
    Iterator<ChannelEnergyPair> iter = pairs.iterator();
    while (iter.hasNext())
    {
      ChannelEnergyPair pair = iter.next();
      if (roi.contains(pair.getEnergy()))
        iter.remove();
    }
  }

  /**
   * Change the number of bins in an energy scale. A number greater than 1 is
   * equal to splitting bins. A number less that 1 denotes combining bins.
   * Scales are applied with an origin relative to the left edge of the first
   * bin.
   *
   * @param pairs
   * @param scale
   */
  public static void scaleChannels(Collection<ChannelEnergyPair> pairs, double scale)
  {
    ArrayList<ChannelEnergyPair> tmp = new ArrayList<>(pairs);
    pairs.clear();
    for (ChannelEnergyPair pair : tmp)
    {
      pairs.add(scalePair(pair, scale));
    }
  }

  /**
   * Smooth a set of pairs in a collection. Alters the collection.
   *
   * @param in
   */
  public static void smoothEnergies(Collection<ChannelEnergyPair> in)
  {
    smoothEnergiesRange(in, 0, in.size());
  }

  /**
   * Smooth a set of pairs in a collection. Alters the collection.
   *
   * @param in
   * @param begin
   * @param end
   *
   */
  public static void smoothEnergiesRange(Collection<ChannelEnergyPair> in, int begin, int end)
  {
//    int n = in.size();
    int i = 1;
    //    ChannelEnergyPairs out = new ChannelEnergyPairs();
    //    out.setPairType(in.getPairType());
    Iterator<ChannelEnergyPair> iter = new ArrayList<>(in).iterator();
    in.clear();
    ChannelEnergyPair pair0;
    ChannelEnergyPair pair1;
    ChannelEnergyPair pair2;
    // Trust the first pair
    pair0 = iter.next();
    in.add(pair0);
    // Manipulate the pairs between
    pair1 = iter.next();
    pair2 = pair1;
    while (iter.hasNext())
    {
      pair2 = iter.next();
      double delta = pair2.getChannelEdge() - pair0.getChannelEdge();
      double f = (pair1.getChannelEdge() - pair0.getChannelEdge()) / delta;
      double w = Math.min(pair1.getChannelEdge() - pair0.getChannelEdge(), pair2.getChannelEdge() - pair1.getChannelEdge()) / pair1.getChannelEdge();
      double f2 = SpecialFunctions.logistic(w, 0, 15);
      // Restrict our action to a range
      if (i < begin || i >= end)
        f2 = 1;
      double ep = (1 - f) * pair0.getEnergy() + f * pair2.getEnergy();
      double en = ep * (1 - f2) + f2 * pair1.getEnergy();
      ChannelEnergyPair pair1p = new ChannelEnergyPair(pair1.getChannelEdge(), en, ChannelEnergyPair.Origin.EDGE);
      pair1p.copyAttributes(pair1);
      in.add(pair1p);
      pair0 = pair1;
      pair1 = pair2;
    }
    // Trust the last pair
    in.add(pair2);
  }

  /**
   * Quick and dirty method to remove closely spaced pairs that will cause
   * problems for our spline. Alters the collection.
   *
   * @param in
   * @param erf is the energy resolution model for this detector.
   */
  public static void removeRedundant(Collection<ChannelEnergyPair> in, ResolutionModel erf)
  {
    ArrayList<ChannelEnergyPair> tmp = new ArrayList<>(in);
    in.clear();
    double prev = -Double.MAX_VALUE;
    ChannelEnergyPair last = null;
    int weight = 1;
    for (ChannelEnergyPair pair : tmp)
    {
      double energy = pair.getEnergy();
      double width = erf.fwhm(energy);
      if (energy - prev > width)
      {
        if (last != null)
          in.add(last);
        last = new ChannelEnergyPair(pair);
        prev = energy;
        weight = 1;
      }
      else
      {
        // Handle the case that more than one measurement in a region needs to be averaged.
        double f = weight;
        double e0 = (f * last.getEnergy() + energy) / (1 + f);
        double c0 = (f * last.getChannelEdge() + pair.getChannelEdge()) / (1 + f);
        last = new ChannelEnergyPair(c0, e0);
        weight++;
      }
    }
    if (last != null)
      in.add(last);
  }
}
