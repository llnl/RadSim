// --- file: gov/llnl/rtk/SpectrumDoseWriter.java ---
package gov.llnl.rtk;

/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
import gov.llnl.rtk.data.EnergyScaleWriter;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.SpectrumDoseCalculator;
import gov.llnl.rtk.model.PileupCorrectionWriter;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
@Internal
public class SpectrumDoseWriter extends ObjectWriter<SpectrumDoseCalculator>
{
  public SpectrumDoseWriter()
  {
    super(Options.NONE, "spectrumDoseCalculator", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, SpectrumDoseCalculator object) throws WriterException
  {
  }

  @Override
  public void contents(SpectrumDoseCalculator object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    if (object.getPileupCorrection() != null)
      wb.element("correction")
              .writer(new PileupCorrectionWriter())
              .put(object.getPileupCorrection());
    wb.element("bins").writer(new EnergyScaleWriter()).put(object.getEnergyScale());
    wb.element("dose").putContents(object.getDoseTable());
  }
}
