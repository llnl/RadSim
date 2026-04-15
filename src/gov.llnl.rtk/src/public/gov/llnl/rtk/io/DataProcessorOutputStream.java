// --- file: gov/llnl/rtk/io/DataProcessorOutputStream.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import java.io.Closeable;
import java.io.IOException;
import gov.llnl.rtk.data.RadiationProcessorInput;

/**
 *
 * @author nelson85
 */
public interface DataProcessorOutputStream extends Closeable
{
  public void put(RadiationProcessorInput input) throws IOException;
}
