// --- file: gov/llnl/rtk/quality/QualityChecks.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.utility.xml.bind.ReaderInfo;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Stub needed for the reader.
 *
 * @author nelson85
 */
@ReaderInfo(QualityChecksReader.class)
public class QualityChecks extends ArrayList<QualityCheck>
        implements Serializable
{
  public QualityChecks()
  {
  }

}
