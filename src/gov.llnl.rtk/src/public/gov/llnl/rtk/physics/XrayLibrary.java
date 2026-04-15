// --- file: gov/llnl/rtk/physics/XrayLibrary.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Standard interface for xray data.
 * 
 * @author nelson85
 */
public interface XrayLibrary
{
  /** 
   * Find the xray data by element.
   * 
   * @param element
   * @return the xray data or null if not found.
   */
  XrayData get(Element element);
}
