// --- file: gov/llnl/rtk/physics/Materials.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public class Materials
{
  /**
   * Create a material from a chemical formula.
   *
   * Any standard chemical formula is acceptable.
   *
   * <ul>
   * <li> Accepts elements in the form of "W" or "Zn".</li>
   * <li> Accepts nuclide in the form of "C-14".</li>
   * <li> Accepts white-space between parts for readability</li>
   * <li> Accepts groupings using parentheses.</li>
   * <li> Multiple groups may be included.</li>
   * <li> Accepts an atom count after elements or groups.</li>
   * <li> Requires ":" between nuclides and atom count.</li>
   * <li> Atom counts may be fractional.</li>
   * </ul>
   *
   * @param specification
   * @return
   */
  public static Material chemical(String specification)
  {
    return MaterialParser.parse(specification);
  }

  /**
   * Create a material from a nuclide.
   *
   * @param nuclide
   * @return
   */
  public static Material pure(Nuclide nuclide)
  {
    MaterialImpl out = new MaterialImpl();
    MaterialComponentImpl component = new MaterialComponentImpl(nuclide, 1, 1, null);
    out.addEntry(component);
    return out;
  }

  /**
   * Create material from element.
   *
   * @param elem
   * @return
   */
  public static Material pure(Element elem)
  {
    MaterialImpl out = new MaterialImpl();
    // FIXME.  
    MaterialComponentImpl component = new MaterialComponentImpl(Nuclides.natural(elem), 1, 1, null);
    out.addEntry(component);
    out.setDensity(elem.getDensity());
    return out;
  }

}
