// --- file: gov/llnl/rtk/quality/QualityControlled.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import java.util.List;

/**
 *
 * @author nelson85
 */
public interface QualityControlled
{
  FaultSet getFaultSet();

  List<QualityCheck> getQualityChecks();

}
