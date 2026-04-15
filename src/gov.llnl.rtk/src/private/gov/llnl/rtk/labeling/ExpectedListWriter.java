// --- file: gov/llnl/rtk/labeling/ExpectedListWriter.java ---
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
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author seilhan3
 */
public class ExpectedListWriter extends ObjectWriter<ExpectedList>
{
  public ExpectedListWriter()
  {
    super(Options.NONE, "expectedList", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, ExpectedList object) throws WriterException
  {
  }

  @Override
  public void contents(ExpectedList object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    WriteObject wos = wb.element("expected").writer(new ExpectedWriter());
    for (Expected expected : object)
    {
      wos.put(expected);
    }
  }
}
