// --- file: gov/llnl/rtk/data/EnergyPairsScaleImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.MathExceptions;
import gov.llnl.math.MathExceptions.DomainException;
import gov.llnl.math.spline.CubicHermiteSplineUtilities;
import gov.llnl.math.spline.CubicHermiteSpline;
import gov.llnl.math.spline.CubicHermiteSplineFactory;
import gov.llnl.math.spline.EndBehavior;
import gov.llnl.math.spline.Spline;
import gov.llnl.rtk.calibration.ChannelEnergyPair;
import gov.llnl.rtk.calibration.ChannelEnergyUtilities;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.util.Arrays;
import java.util.Collection;

/**
 * Functional description for mapping channels to energies in a detector. See
 * EnergyBins for a fixed bin edge definition.
 *
 * @author nelson85
 */
@Internal
@ReaderInfo(EnergyScaleReader.class)
final public class EnergyPairsScaleImpl
        extends ChannelEnergySetImpl
        implements EnergyPairsScale
{
  private int channels = 0;
  transient private CubicHermiteSpline forward = null;
  transient private CubicHermiteSpline inverse = null;
  transient private double[] values;

  public EnergyPairsScaleImpl()
  {
  }

  public EnergyPairsScaleImpl(int channels, Collection<ChannelEnergyPair> pairs)
  {
    super(pairs);
    this.channels = channels;
    getForwardModel();
    CubicHermiteSplineUtilities.verifyMonotonic(forward);
//    if (!CubicHermiteSplineUtilities.verifyMonotonic(forward))
//      throw new RuntimeException("Energy spline is not monotonic");
  }

  /**
   * Copy constructor.
   *
   * @param scale
   */
  public EnergyPairsScaleImpl(EnergyPairsScale scale)
  {
    super(scale);
    this.channels = scale.getChannels();
  }

  @Override
  public int getChannels()
  {
    return channels;
  }

  public void setChannels(int channels)
  {
    this.channels = channels;
  }

  @Override
  public double getEdge(int edge)
  {
    return getForwardModel().applyAsDouble(edge);
  }

  @Override
  public double[] getCenters()
  {
    getForwardModel();
    double[] in = new double[this.channels];
    for (int i = 0; i < this.channels; ++i)
      in[i] = i + 0.5;
    return this.getForwardModel().evaluateRangeOrdered(in, 0, channels);
  }

  @Override
  public double[] getEdges()
  {
    if (values != null)
      return values;
    double[] in = new double[this.channels + 1];
    for (int i = 0; i < this.channels + 1; ++i)
      in[i] = i;
    values = this.getForwardModel().evaluateRangeOrdered(in, 0, in.length);
    return values;
  }

  @Override
  public int findEdgeFloor(double energy)
  {
    getEdges();
    if (energy < values[0])
      return -1;
    int index = Arrays.binarySearch(values, energy);
    if (index < 0)
      return -index - 2;
    return index;
  }

  @Override
  public int findEdgeCeiling(double energy)
  {
    getEdges();
    if (energy < values[0])
      return 0;
    int index = Arrays.binarySearch(values, energy);
    if (index < 0)
      return -index - 1;
    return index;
  }

  /**
   * Must be called if the pairs are manipulated
   */
  @Override
  protected void clearCache()
  {
    forward = null;
    inverse = null;
    values = null;
  }

  @Override
  public Spline getForwardModel()
  {
    if (forward == null)
    {
      double[] edges = ChannelEnergyUtilities.getChannelsEdge(this);
      double[] energies = ChannelEnergyUtilities.getEnergies(this);

      // Create a cubic spline
      forward = CubicHermiteSplineFactory.createNatural(edges, energies);
      // Define how to handle the deges
      forward.setEndBehavior(EndBehavior.LINEAR);
    }
    return forward;
  }

  @Override
  public Spline getInverseModel()
  {
    if (inverse == null)
    {
      double[] edges = ChannelEnergyUtilities.getChannelsEdge(this);
      double[] energies = ChannelEnergyUtilities.getEnergies(this);
      inverse = CubicHermiteSplineFactory.createNatural(energies, edges);
    }
    return inverse;
  }

  /**
   * Convert an energy into a bin. This is the position with an origin at the
   * lower edge of the first channel.
   *
   * @param energy
   * @return the bin corresponding the the energy.
   * @throws DomainException
   */
  @Override
  public double convertEnergyToEdge(double energy) throws DomainException
  {
    // Cubic splines to do not have an exact inverse.
    // Thus we need to find an initial guess and then refine it through iteration.
    double eps = 1e-9;
    Spline cs = getForwardModel();
    Spline cs2 = getInverseModel();

    // Use the inverse model to guess the answer
    double edge = cs2.applyAsDouble(energy);

    // Iterate on to refine the solution.
    for (int i = 0; i < 10; i++)
    {
      double energy2 = cs.applyAsDouble(edge);
      double energy3 = cs.applyAsDouble(edge + eps);
      double err = energy - energy2;
      if (Math.abs(err) < 1e-12)
        break;
      double delta = (energy3 - energy2) / eps;
      edge += err / delta;
    }
    return edge;
  }

  @Override
  public double[] convertEnergyToEdge(double[] energies) throws MathExceptions.DomainException
  {
    double[] out = new double[energies.length];
    for (int i = 0; i < energies.length; ++i)
    {
      out[i] = this.convertEnergyToEdge(energies[i]);
    }
    return out;
  }

  @Override
  public double findBin(double energy)
  {
    return convertEnergyToEdge(energy);
  }

  @Override
  public double getEnergyOfEdge(double edge)
  {
    return getForwardModel().applyAsDouble(edge);
  }  
}
