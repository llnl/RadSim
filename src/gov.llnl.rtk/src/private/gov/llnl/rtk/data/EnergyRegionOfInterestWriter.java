// --- file: gov/llnl/rtk/data/EnergyRegionOfInterestWriter.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
public class EnergyRegionOfInterestWriter extends ObjectWriter<EnergyRegionOfInterest>
{
  public EnergyRegionOfInterestWriter()
  {
    super(Options.NONE, "energyRegionOfInterest", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, EnergyRegionOfInterest object) throws WriterException
  {
    attributes.add("lower", object.getLowerEnergy());
    attributes.add("upper", object.getUpperEnergy());
  }

  @Override
  public void contents(EnergyRegionOfInterest object) throws WriterException
  {
  }
}
