// --- file: gov/llnl/rtk/quality/QualityControlException.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

//  Class<Type> getObjectType();

public class QualityControlException extends Exception
{
  public QualityControlException(String desc)
  {
    super(desc);
  }

}
