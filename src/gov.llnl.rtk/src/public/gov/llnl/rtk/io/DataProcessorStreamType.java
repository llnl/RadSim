// --- file: gov/llnl/rtk/io/DataProcessorStreamType.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author nelson85
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DataProcessorStreamType
{
  String value();
}
