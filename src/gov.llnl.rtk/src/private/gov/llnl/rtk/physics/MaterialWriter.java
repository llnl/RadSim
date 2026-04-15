// --- file: gov/llnl/rtk/physics/MaterialWriter.java ---
/*
 * Copyright 2017, Lawrence Livermore National Security, LLC.
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
public class MaterialWriter extends ObjectWriter<Material>
{
  public MaterialWriter()
  {
    super(Options.NONE, "material", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, Material object) throws WriterException
  {
  }

  @Override
  public void contents(Material object) throws WriterException
  {
    if (object == null)
      throw new RuntimeException("Null material");
    WriterBuilder wb = newBuilder();
    if (object.getAge() != null && object.getAge().getValue() != 0)
      wb.element("age")
              .writer(new QuantityWriter("time:a"))
              .put(object.getAge());
    wb.element("density")
            .writer(new QuantityWriter("density:g/cm3"))
            .put(object.getDensity());
    WriteObject<MaterialComponent> wo = wb.element("component")
            .writer(new MaterialComponentWriter());
    for (MaterialComponent entry : object)
      wo.put(entry);
  }

}
