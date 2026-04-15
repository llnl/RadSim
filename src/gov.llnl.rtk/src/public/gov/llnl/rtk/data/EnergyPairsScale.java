// --- file: gov/llnl/rtk/data/EnergyPairsScale.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.calibration.ChannelEnergySet;
import gov.llnl.math.MathExceptions;
import gov.llnl.math.MathExceptions.DomainException;
import gov.llnl.math.spline.Spline;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.io.Serializable;

/**
 * Functional description for mapping channels to energies in a detector. See
 * EnergyBins for a fixed bin edge definition.
 *
 * @author nelson85
 */
@ReaderInfo(EnergyPairsScaleReader.class)
public interface EnergyPairsScale extends EnergyScale, Serializable, ChannelEnergySet
{
  /**
   * Convert an energy into a bin. This is the position with an origin at the
   * lower edge of the first channel.
   *
   * @param energy
   * @return the bin corresponding the the energy.
   * @throws DomainException
   */
  double convertEnergyToEdge(double energy) throws MathExceptions.DomainException;

  double[] convertEnergyToEdge(double[] energies) throws MathExceptions.DomainException;

  Spline getForwardModel();

  Spline getInverseModel();

}
