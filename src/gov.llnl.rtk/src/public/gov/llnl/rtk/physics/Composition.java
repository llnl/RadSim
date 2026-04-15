// --- file: gov/llnl/rtk/physics/Composition.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.io.Serializable;
import java.util.Collection;

/**
 * Composition is a collection of nuclides that make up a source.
 *
 * @author nelson85
 */
@ReaderInfo(CompositionReader.class)
@WriterInfo(CompositionWriter.class)
public interface Composition extends Collection<MaterialComponent>, Serializable
{

  /**
   * Create a composition from a GADRAS description string.
   *
   * @param desc
   * @return
   */
  static Composition fromString(String desc)
  {
    return CompositionFactory.createFromString(desc);
  }

  /**
   * Compare two compositions. Currently this does not deal with
   *
   * @param composition
   * @return
   */
  default public boolean equals(Composition composition)
  {
    if (composition.size() != this.size())
      return false;

    for (MaterialComponent c : this)
    {
      MaterialComponent c2 = composition.getComponent(c.getNuclide());
      if (c2 == null)
        return false;
      if (c.getActivity() != c2.getActivity())
        return false;
    }
    return true;
  }

  /**
   * Get the name of this composition.
   *
   * @return
   */
  String getName();

  /**
   * Utility method for filtering out a nuclide from a Composition.
   *
   * @param nuclide
   * @return
   */
  default MaterialComponent getComponent(Nuclide nuclide)
  {
    for (MaterialComponent component : this)
    {
      if (component.getNuclide() == nuclide)
        return component;
    }
    return null;
  }

  default boolean containsNuclide(Nuclide nuclide)
  {
    return getComponent(nuclide) != null;
  }

  boolean hasName();
}
