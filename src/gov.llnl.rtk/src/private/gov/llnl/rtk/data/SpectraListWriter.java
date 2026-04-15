// --- file: gov/llnl/rtk/data/SpectraListWriter.java ---
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
import java.util.TreeMap;

/**
 *
 * @author nelson85
 * @param <T>
 */
public class SpectraListWriter<T extends SpectraList<? extends Spectrum>>
        extends ObjectWriter<T>
{
  TreeMap<Integer, EnergyScale> binMap = new TreeMap<>();

  public SpectraListWriter()
  {
    super(Options.NONE, "spectraList", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, T object) throws WriterException
  {
  }

  @Override
  @SuppressWarnings("unchecked")
  public void contents(T object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    WriteObject wos = wb.element("spectrum").writer(new SpectrumWriter());
    for (Spectrum spectrum : object)
    {
      wos.put(spectrum);
    }
  }
}
