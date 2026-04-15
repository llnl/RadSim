// --- file: gov/llnl/rtk/physics/Elements.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.ReflectionUtilities;

/**
 * Library holding all elements.
 *
 * This is a singleton.
 *
 * @author nelson85
 */
public interface Elements
{
  /**
   * Get a element by symbol.
   *
   * This accepts elemental symbols and named nuclides.   
   * The first character of the symbol should be capitalized.
   * The second character may be either case.
   * 
   * @return the element from the symbol or null if no such element is found.
   */
  static Element get(String name)
  {
    return NuclidesImpl.INSTANCE.getElement(name);
  }

  /**
   * 
   * @param z
   * @return 
   */
  static Element getElement(int z)
  {
    return NuclidesImpl.INSTANCE.getElement(z);
  }

}
