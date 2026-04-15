// --- file: gov/llnl/rtk/physics/CompositionWriter.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
@Internal
public class CompositionWriter extends ObjectWriter<Composition>
{
  public CompositionWriter()
  {
    super(Options.REFERENCEABLE, "composition", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, Composition object) throws WriterException
  {
    if (object.hasName())
      attributes.add("name", object.getName());
  }

  @Override
  public void contents(Composition object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    WriteObject<MaterialComponent> wo = wb.element("entry").writer(new MaterialComponentWriter());
    for (MaterialComponent entry : object)
      wo.put(entry);
  }

}
