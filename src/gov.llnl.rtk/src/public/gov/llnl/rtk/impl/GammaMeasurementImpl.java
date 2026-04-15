// --- file: gov/llnl/rtk/impl/GammaMeasurementImpl.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.impl;

import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.GammaMeasurement;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.UUIDUtilities;
import java.io.Serializable;
import gov.llnl.rtk.data.RadiationSensor;

/**
 *
 * @author seilhan3
 */
public class GammaMeasurementImpl
        extends RadiationMeasurementImpl<Spectrum>
        implements GammaMeasurement, Serializable
{
  private static final long serialVersionUID = UUIDUtilities.createLong("GammaMeasurement-v1");

  public GammaMeasurementImpl(RadiationSensor sensor, Spectrum spectrum)
  {
    super(sensor, spectrum);
  }

  public GammaMeasurementImpl(GammaMeasurement measurement)
  {
    super(measurement.getSensor(), duplicate(measurement.getSample()));
  }

  static private Spectrum duplicate(Spectrum spectrum)
  {
    if (spectrum instanceof IntegerSpectrum)
      return new IntegerSpectrum((IntegerSpectrum) spectrum);
    if (spectrum instanceof DoubleSpectrum)
      return new DoubleSpectrum((DoubleSpectrum) spectrum);
    throw new RuntimeException("Unknown spectrum type");
  }

}
