// --- file: gov/llnl/rtk/SpectrumDoseReader.java ---
package gov.llnl.rtk;

/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.SpectrumDoseCalculator;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.model.PileupCorrectionReader;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Internal
@Reader.Declaration(pkg = RtkPackage.class, name = "spectrumDoseCalculator",
        cls = SpectrumDoseCalculator.class,
        document = true, order = Reader.Order.ALL, referenceable = true)
public class SpectrumDoseReader extends ObjectReader<SpectrumDoseCalculator>
{

  @Override
  public SpectrumDoseCalculator start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new SpectrumDoseCalculator();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<SpectrumDoseCalculator> rb = newBuilder();
    rb.element("bins").call(SpectrumDoseCalculator::setEnergyScale, EnergyScale.class);
    rb.element("dose").call(SpectrumDoseCalculator::setDoseTable, double[].class);
    rb.element("correction").reader(new PileupCorrectionReader())
            .call(SpectrumDoseCalculator::setPileupCorrection).optional();
    return rb.getHandlers();
  }
}
