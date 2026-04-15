// --- file: gov/llnl/rtk/physics/XrayData.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.List;

/**
 * All xray data associated with a given element.
 * 
 * @author nelson85
 */
public interface XrayData
{
  /**
   * Get the element associated with the xray.
   *
   * @return
   */
  Element getElement();

  /**
   * Get the list of edges associated with this electron transition.
   *
   * @return
   */
  List<XrayEdge> getEdges();

  /**
   * Search the edges by name.
   *
   * @param name
   * @return the named edge or null if not found.
   */
  default XrayEdge findEdge(String name)
  {
    for (XrayEdge edge : getEdges())
    {
      if (edge.getName().equals(name))
        return edge;
    }
    return null;
  }
}
