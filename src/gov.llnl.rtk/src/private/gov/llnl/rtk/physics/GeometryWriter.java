// --- file: gov/llnl/rtk/physics/GeometryWriter.java ---
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
public class GeometryWriter extends ObjectWriter<Geometry>
{
  public GeometryWriter()
  {
    super(Options.NONE, "geometry", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, Geometry object) throws WriterException
  {
    if (object == null)
    {
      attributes.add("type", Geometry.Type.SPHERICAL.toString().toLowerCase());
      return;
    }
    attributes.add("type", object.getType().toString().toLowerCase());
  }

  @Override
  public void contents(Geometry object) throws WriterException
  {
    if (object == null)
      return;
    WriterBuilder wb = newBuilder();
    if (object.getExtent1() != null)
      wb.element("extent1").writer(new QuantityWriter("length:cm")).put(object.getExtent1());
    if (object.getExtent2() != null)
      wb.element("extent2").writer(new QuantityWriter("length:cm")).put(object.getExtent2());
  }

}
