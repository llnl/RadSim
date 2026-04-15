// --- file: gov/llnl/rtk/data/EnergyScaleWriter.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
@Internal
public class EnergyScaleWriter extends ObjectWriter<EnergyScale>
{
  public EnergyScaleWriter()
  {
    super(Options.REFERENCEABLE, "energyScale", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, EnergyScale object) throws WriterException
  {
  }

  @Override
  public void contents(EnergyScale object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    if (object==null)
       return;
    if (object instanceof EnergyPairsScale)
    {
      wb.element("pairs")
              .writer(new EnergyPairsScaleWriter())
              .put((EnergyPairsScale) object);
    }
    else
    {
      wb.element("values").putContents(object.getEdges());
    }

  }

}
