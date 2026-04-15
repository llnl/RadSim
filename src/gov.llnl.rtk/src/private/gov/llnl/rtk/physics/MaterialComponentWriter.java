// --- file: gov/llnl/rtk/physics/MaterialComponentWriter.java ---
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
public class MaterialComponentWriter extends ObjectWriter<MaterialComponent>
{
  public MaterialComponentWriter()
  {
    super(Options.NONE, "entry", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, MaterialComponent object) throws WriterException
  {
    attributes.add("nuclide", object.getNuclide().getName());
  }

  @Override
  public void contents(MaterialComponent object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    if (object.getDoseFraction() > 0)
      wb.element("doseFraction").putDouble(object.getDoseFraction());
    if (object.getMassFraction() > 0)
      wb.element("massFraction").putDouble(object.getMassFraction());
    //   if (getContext().getProperty(LibraryWriter.WRITE_ACTIVITY, Boolean.class, true))
    if (object.getActivity() != null)
      wb.element("activity").writer(new QuantityWriter(PhysicalProperty.ACTIVITY.getUnit())).put(object.getActivity());
  }

}
