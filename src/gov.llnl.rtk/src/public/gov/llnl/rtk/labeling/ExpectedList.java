// --- file: gov/llnl/rtk/labeling/ExpectedList.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.labeling;

import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.util.List;

/**
 *
 * @author nelson85
 */
@ReaderInfo(ExpectedListReader.class)
@WriterInfo(ExpectedListWriter.class)
public interface ExpectedList extends List<Expected>
{

}
