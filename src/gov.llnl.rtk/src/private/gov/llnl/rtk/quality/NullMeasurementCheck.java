// --- file: gov/llnl/rtk/quality/NullMeasurementCheck.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.impl.FaultImpl;
import gov.llnl.rtk.quality.FaultCategory;
import gov.llnl.rtk.quality.FaultLevel;
import gov.llnl.rtk.quality.FaultSet;
import gov.llnl.rtk.quality.QualityCheck;
import gov.llnl.rtk.quality.QualityControlException;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author seilhan3
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "nullMeasurementCheck",
        referenceable = true)
@Internal
public class NullMeasurementCheck implements QualityCheck<Object>
{
  FaultImpl NULL_FAULT
          = new FaultImpl(FaultLevel.ERROR, FaultCategory.NULL_MEASUREMENT, null, this, true);
  boolean recoverable = true;

  @Override
  public boolean execute(FaultSet faultSet, Object measurement) throws QualityControlException
  {
    if (measurement != null)
      return true;
    faultSet.add(NULL_FAULT);
    throw new QualityControlException(NULL_FAULT.getName());
  }
}
