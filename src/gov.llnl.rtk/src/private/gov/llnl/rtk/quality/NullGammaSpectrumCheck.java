// --- file: gov/llnl/rtk/quality/NullGammaSpectrumCheck.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.GammaMeasurement;
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
@Internal

@Reader.Declaration(pkg = RtkPackage.class, name = "nullGammaSpectrumCheck",
        referenceable=true)
public class NullGammaSpectrumCheck implements QualityCheck<GammaMeasurement>
{
  final FaultImpl NULL_FAULT = new FaultImpl(FaultLevel.ERROR, FaultCategory.NULL_MEASUREMENT, null, this, true);

  @Override
  public boolean execute(FaultSet faultSet, GammaMeasurement measurement) throws QualityControlException
  {
    if (measurement != null)
      return true;
    faultSet.add(NULL_FAULT);
    throw new QualityControlException("Null Measurement");
  }

  public boolean isRecoverable()
  {
    return NULL_FAULT.isRecoverable();
  }

}
