// --- file: gov/llnl/rtk/model/DefaultResolutionModelWriter.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.model.DefaultResolutionModel;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
public class DefaultResolutionModelWriter extends ObjectWriter<DefaultResolutionModel>
{

  public DefaultResolutionModelWriter()
  {
    super(ObjectWriter.Options.NONE, "defaultResolutionModel", RtkPackage.getInstance());
  }

  @Override
  public void attributes(ObjectWriter.WriterAttributes attributes, DefaultResolutionModel object) throws WriterException
  {
    attributes.add("fwhm0", object.getFWHM0());
    attributes.add("fwhm", object.getFWHM());
    attributes.add("energy", object.getEnergy());
    attributes.add("power", object.getPower());
  }

  @Override
  public void contents(DefaultResolutionModel object) throws WriterException
  {
  }

}
