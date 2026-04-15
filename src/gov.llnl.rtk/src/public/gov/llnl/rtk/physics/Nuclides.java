// --- file: gov/llnl/rtk/physics/Nuclides.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.ArrayList;
import java.util.List;

/**
 * Library holding all nuclides.
 *
 * @author nelson85
 */
public interface Nuclides
{
  /**
   * Get a nuclide by name.
   *
   * It can also accept under specified nuclides such as elements. This accepts
   * a few formats. "Tc-99m", "Tc99m", "99TCM", etc
   *
   * @return the nuclide requested or null if no element was found.
   */
  static Nuclide get(String name)
  {
    return NuclidesImpl.INSTANCE.getByName(name);
  }

  /**
   * Get by specifications.
   *
   * @param z is the atomic number.
   * @param mass is the atomic mass.
   * @param meta is the isomer number.
   * @return
   */
  static Nuclide get(int z, int mass, int meta)
  {
    return NuclidesImpl.INSTANCE.getById(z * 10000 + mass * 10 + meta);
  }

  static List<Nuclide> getIsomers(int z, int mass)
  {
    ArrayList<Nuclide> out = new ArrayList<>();
    for (int i = 0; i < 9; i++)
    {
      Nuclide n = get(z, mass, i);
      if (n != null)
        out.add(n);
    }
    return out;
  }

  /**
   * Creates a natural nuclide.
   *
   * This is a bit abusive as the nuclide will in most cases be mixture rather
   * than a single nuclide. It also won't get the isRadioactive correct as there
   * is no defined halflife for mixtures.
   *
   * @param element
   * @return
   */
  public static Nuclide natural(Element element)
  {
    return NuclidesImpl.INSTANCE.natural(element);
  }

}
