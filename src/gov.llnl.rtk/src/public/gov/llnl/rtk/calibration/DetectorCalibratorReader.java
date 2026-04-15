// --- file: gov/llnl/rtk/calibration/DetectorCalibratorReader.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import static gov.llnl.rtk.calibration.DetectorCalibratorAttributes.DEFAULT_ENERGY_SCALE;
import static gov.llnl.rtk.calibration.DetectorCalibratorAttributes.ENERGY_SCALE_MAPPER;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleMapper;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.PolymorphicReader;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author seilhan3
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "calibrator",
        cls = DetectorCalibrator.class,
        referenceable = true, copyable = true)
public class DetectorCalibratorReader extends PolymorphicReader<DetectorCalibrator>
{

  @Override
  @SuppressWarnings("unchecked")
  public ObjectReader<? extends DetectorCalibrator>[] getReaders() throws ReaderException
  {
    return group(new SinglePeakGainStabilizerReader(),
            new NonlinearStabilizerReader(),
            ObjectReader.create(ExternalCalibration.class));
  }

  /**
   * Adds common handlers to the reader.
   *
   * This is called from getHandlers within readers that implement
   * DetectorCalibrator.
   *
   * @param <T>
   * @param builder
   * @throws ReaderException
   */
  public static <T extends DetectorCalibrator> void addHandlers(ReaderBuilder<T> builder)
          throws ReaderException
  {
    builder.element("energyScaleMapper")
            .contents(EnergyScaleMapper.class)
            .call((T dc, EnergyScaleMapper esm) -> dc.setAttribute(ENERGY_SCALE_MAPPER, esm))
            .optional();
    builder.element("energyScale")
            .contents(EnergyScale.class)
            .call((T dc, EnergyScale es) -> dc.setAttribute(DEFAULT_ENERGY_SCALE, es))
            .optional();
  }
}
