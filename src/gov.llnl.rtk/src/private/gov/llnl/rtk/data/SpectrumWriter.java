// --- file: gov/llnl/rtk/data/SpectrumWriter.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;
import java.util.function.Predicate;

/**
 *
 * @author nelson85
 * @param <T>
 */
public class SpectrumWriter<T extends Spectrum> extends ObjectWriter<T>
{
  public SpectrumWriter()
  {
    super(Options.NONE, "spectrum", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, T object) throws WriterException
  {

  }

  @Override
  @SuppressWarnings("unchecked")
  public void contents(T object) throws WriterException
  {
    Predicate<String> exclude = this.getContext()
            .getProperty(AttributesWriter.WRITER_EXCLUDE, Predicate.class, null);
    WriterBuilder wb = newBuilder();
    if (!object.getAttributes().isEmpty())
      wb.element("attributes").writer(new AttributesWriter()).put(object);
    if (exclude == null || !exclude.test("title"))
      wb.element("title").putString(object.getTitle());
    if (exclude == null || !exclude.test("realTime"))
      wb.element("realTime").putDouble(object.getRealTime());
    if (exclude == null || !exclude.test("liveTime"))
      wb.element("liveTime").putDouble(object.getLiveTime());
    if (object.getEnergyScale() != null)
      wb.element("gammaEnergyBins").writer(new EnergyScaleWriter()).put(object.getEnergyScale());
    if (object instanceof SpectrumBase)
    {
      wb.element("gammaCounts").putContents(((SpectrumBase) object).toArray());
    }
    else
    {
      wb.element("gammaCounts").putContents(object.toDoubles());
    }
    if (exclude == null || !exclude.test("minimumValidChannel"))
      wb.element("minimumValidChannel").putInteger(object.getMinimumValidChannel());
    if (exclude == null || !exclude.test("maximumValidChannel"))
      wb.element("maximumValidChannel").putInteger(object.getMaximumValidChannel());
  }

}
