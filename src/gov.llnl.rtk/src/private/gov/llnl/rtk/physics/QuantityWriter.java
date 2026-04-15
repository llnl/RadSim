// --- file: gov/llnl/rtk/physics/QuantityWriter.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
public class QuantityWriter extends ObjectWriter<Quantity>
{

  final String target;

  public QuantityWriter()
  {
    super(Options.NONE, "quantity", RtkPackage.getInstance());
    target = null;
  }

  public QuantityWriter(String target)
  {
    super(Options.NONE, "quantity", RtkPackage.getInstance());
    this.target = target;
  }

  @Override
  public void attributes(WriterAttributes attributes, Quantity object) throws WriterException
  {
    if (target != null)
      object = object.to(target);
    if (object.getUnits() != null)
      attributes.add("units", object.getUnits().getSymbol());
    if (object.hasUncertainty())
      attributes.add("unc", object.getUncertainty());
  }

  @Override
  public void contents(Quantity object) throws WriterException
  {
    if (target != null)
      object = object.to(target);
    this.addContents(Double.toString(object.getValue()));
  }

}
