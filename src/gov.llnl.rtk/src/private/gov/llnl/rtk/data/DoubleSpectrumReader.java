// --- file: gov/llnl/rtk/data/DoubleSpectrumReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "spectrum",
        cls = DoubleSpectrum.class,
        order = Reader.Order.FREE,
        referenceable = true)
public class DoubleSpectrumReader extends SpectrumReader<DoubleSpectrum>
{
  public DoubleSpectrumReader()
  {
    super(DoubleSpectrum.class);
  }

}
