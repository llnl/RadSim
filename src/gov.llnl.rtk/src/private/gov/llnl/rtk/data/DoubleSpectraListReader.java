// --- file: gov/llnl/rtk/data/DoubleSpectraListReader.java ---
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
@Reader.Declaration(pkg = RtkPackage.class, name = "doubleSpectraList",
        cls = DoubleSpectraList.class,
        referenceable = true, document = true,
        order = Reader.Order.FREE)
public class DoubleSpectraListReader extends SpectraListReader<DoubleSpectraList>
{
  @SuppressWarnings("unchecked")
  public DoubleSpectraListReader()
  {
    super(DoubleSpectraList.class);
  }

  @SuppressWarnings("unchecked")
  public DoubleSpectraListReader(DoubleSpectraList list)
  {
    super(list);
  }

}
