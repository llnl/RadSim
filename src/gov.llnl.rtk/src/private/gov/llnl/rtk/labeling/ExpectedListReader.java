// --- file: gov/llnl/rtk/labeling/ExpectedListReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.labeling;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.labeling.Expected;
import gov.llnl.rtk.labeling.ExpectedList;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author seilhan3
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "expectedList", cls = ExpectedList.class,
        order = Reader.Order.CHOICE, referenceable = true,
        document = true)
public class ExpectedListReader extends ObjectReader<ExpectedList>
{

  @Override
  public ExpectedList start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new ExpectedListImpl();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<ExpectedList> builder = this.newBuilder();
    builder.element("expected").call(ExpectedList::add, Expected.class);
    return builder.getHandlers();
  }

}
