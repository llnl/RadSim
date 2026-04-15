// --- file: gov/llnl/rtk/io/DataProcessorInputStream.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.data.RadiationProcessorInput;
import gov.llnl.utility.Configurable;
import java.io.Closeable;
import java.util.Iterator;

/**
 * Generic base type for reading detector data for calibration or detection.
 *
 * TODO Consider making the input stream type an Iterator, but that has
 * implementations for exception handling as iterators cannot issue an
 * exception.
 *
 * @author nelson85
 */
public interface DataProcessorInputStream<Type extends RadiationProcessorInput>
        extends Closeable, Configurable, Iterator<Type>
{
  @Override
  Type next();
}
