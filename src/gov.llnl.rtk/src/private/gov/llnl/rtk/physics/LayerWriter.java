// --- file: gov/llnl/rtk/physics/LayerWriter.java ---
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
public class LayerWriter extends ObjectWriter<Layer>
{
  public LayerWriter()
  {
    super(Options.NONE, "layer", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, Layer object) throws WriterException
  {
    if (object.getLabel() != null)
      attributes.add("label", object.getLabel());
  }

  @Override
  public void contents(Layer object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    wb.element("thickness").writer(new QuantityWriter("length:cm")).put(object.getThickness());
    wb.element("geometry").writer(new GeometryWriter()).put(object.getGeometry());
    wb.element("material").writer(new MaterialWriter()).put(object.getMaterial());
  }

}
