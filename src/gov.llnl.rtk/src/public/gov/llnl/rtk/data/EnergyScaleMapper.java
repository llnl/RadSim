// --- file: gov/llnl/rtk/data/EnergyScaleMapper.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.calibration.ChannelEnergySet;
import gov.llnl.rtk.calibration.ChannelEnergyUtilities;
import gov.llnl.rtk.calibration.ChannelEnergyPair;
import gov.llnl.math.MathAssert;
import gov.llnl.math.MathExceptions;
import gov.llnl.math.spline.CubicHermiteSplineFactory;
import gov.llnl.math.spline.EndBehavior;
import gov.llnl.math.spline.Spline;
import gov.llnl.utility.annotation.Debug;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.Reader;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author nelson85
 */
@ReaderInfo(EnergyScaleMapperReader.class)
public class EnergyScaleMapper implements Serializable
{
  // Settings
  ArrayList<EnergyRegionOfInterest> ignore = new ArrayList<>();
  ChannelEnergySet reference = new ChannelEnergySetImpl();
  double target = 0;
  boolean useSmoothing = false;
  int channels = 512;

  // Cache for speed
  @Debug public double[] referenceChannelCenters;
  @Debug public double[] currentChannelCenters;
  @Debug public boolean constrainZero = false;

  public EnergyScaleMapper()
  {
  }

  // This does not set the channels, not currently used. If its needed we need
  //  combine with other constructor
  @SuppressWarnings("deprecation")
  public EnergyPairsScale remap(EnergyPairsScale fieldScale)
  {
    ChannelEnergySet fieldPairs = new ChannelEnergySetImpl(fieldScale);
    if (fieldScale.getChannels() != channels)
    {
      ChannelEnergyUtilities.scaleChannels(fieldPairs, ((double) channels) / fieldScale.getChannels());
    }

    if (useSmoothing)
    {
      ChannelEnergyUtilities.smoothEnergies(fieldPairs);
    }

    // Constrain spline, after any smoothing shifts have been made
    if (constrainZero)
    {
      // left edge of the first bin is 0.
      fieldPairs.add(new ChannelEnergyPair(-0.5, 0));
    }

    // Remove any pairs that are designated as bad in the configuration
    for (EnergyRegionOfInterest roi : ignore)
    {
      ChannelEnergyUtilities.remove(fieldPairs, roi);
    }

    // Map the reference points to the energies
    try
    {
      double[] referenceEnergies = ChannelEnergyUtilities.getEnergies(reference);
      // Reference spline
      Spline cs1
              = CubicHermiteSplineFactory.createNatural(referenceEnergies,
                      ChannelEnergyUtilities.getChannelsCenter(reference));

      cs1.setEndBehavior(EndBehavior.LINEAR);
      double[] fieldEnergies = ChannelEnergyUtilities.getEnergies(fieldPairs);
      referenceChannelCenters = cs1.interpolate(fieldEnergies);

      // Convert the reference from the reference to the current detector
      // map referenceCenters -> currenetCenters
      double[] fieldCenters = ChannelEnergyUtilities.getChannelsCenter(fieldPairs);
      MathAssert.assertSortedDoubleUnique(fieldCenters);

      Spline cs2
              = CubicHermiteSplineFactory.createNatural(
                      referenceChannelCenters,
                      fieldCenters);
      cs2.setEndBehavior(EndBehavior.LINEAR);
      currentChannelCenters = cs2.interpolate(ChannelEnergyUtilities.getChannelsCenter(reference));
      ChannelEnergySet ep
              = ChannelEnergySet.newSet(ChannelEnergyPair.Origin.CENTER, currentChannelCenters,
                      referenceEnergies);

      // Correct the scale to fit the target to match the reference
      if (target != 0)
      {
        double targetChannel = cs1.applyAsDouble(target);
        Spline cs3
                = CubicHermiteSplineFactory.createNatural(fieldEnergies, fieldCenters);
        double actualChannel = cs3.applyAsDouble(target);
        ChannelEnergyUtilities.scaleChannels(ep, targetChannel / actualChannel);
      }

      // Keep the number of channels
      if (fieldScale.getChannels() != channels)
      {
        ChannelEnergyUtilities.scaleChannels(ep, ((double) fieldScale.getChannels()) / channels);
      }

      EnergyPairsScale out = new EnergyPairsScaleImpl(fieldScale.getChannels(), ep);
      return out;
    }
    catch (MathExceptions.DomainException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public void addPair(ChannelEnergyPair pair)
  {
    reference.add(pair);
  }

  public void addPair(double channelCenter, double energyCenter)
  {
    this.addPair(new ChannelEnergyPair(channelCenter, energyCenter));
  }

  public void clear()
  {
    reference.clear();
  }

  public ChannelEnergySet getChannelEnergyPairs()
  {
    return reference;
  }

  /**
   * Set the target to fix the energy scale to. Target is in energy.
   *
   * @param target
   */
  @Reader.Attribute(name = "target")
  public void setTarget(double target)
  {
    this.target = target;
  }

  /**
   * Request a smoothing operation when interpreting pairs supplied by the user.
   *
   * @param target
   */
  @Reader.Attribute(name = "smoothing")
  public void setUseSmoother(boolean target)
  {
    this.useSmoothing = target;
  }

  /**
   * Request a zero pair be added to the spline.
   *
   * @param target
   */
  @Reader.Attribute(name = "constrainZero")
  public void setConstrainZero(boolean target)
  {
    this.constrainZero = target;
  }

  public void addIgnoreRoi(EnergyRegionOfInterest roi)
  {
    this.ignore.add(roi);
  }

  /**
   * @return the channels
   */
  public int getChannels()
  {
    return channels;
  }

  /**
   * @param channels the channels to set
   */
  @Reader.Attribute(name = "channels")
  public void setChannels(int channels)
  {
    this.channels = channels;
  }

}
