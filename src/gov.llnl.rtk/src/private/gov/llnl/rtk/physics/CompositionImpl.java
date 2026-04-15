// --- file: gov/llnl/rtk/physics/CompositionImpl.java ---
/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.annotation.Internal;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author nelson85
 */
@Internal
public class CompositionImpl extends ArrayList<MaterialComponent> implements Composition, Serializable
{
  private static final long serialVersionUID = UUIDUtilities.createLong("CompositionImpl");
  private final String name;

  public CompositionImpl()
  {
    this.name = null;
  }
  
  public CompositionImpl(String name)
  {
    this.name = name;
  }
  
  @Override
  public boolean hasName()
  {
    return name != null;
  }
  
  //ArrayList<EntryImpl> content = new ArrayList<>();
  // FIXME: check for uniqueness
  public MaterialComponentImpl add(Nuclide nuc, double doseFraction, Quantity activity)
  {
    MaterialComponentImpl entry = new MaterialComponentImpl();
    entry.nuclide = nuc;
    entry.doseFraction = doseFraction;
    entry.activity = activity;
    this.add(entry);
    return entry;
  }

  @Override
  public String getName()
  {
    if (this.name != null)
      return this.name;
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (MaterialComponent entry : this)
    {
      if (!first)
      {
        builder.append("+");
      }
      first = false;
      builder.append(entry.getNuclide().getName());
    }
    return builder.toString();
  }

  public void scale(double d)
  {
    for (MaterialComponent entry : this)
    {
      ((MaterialComponentImpl) entry).activity = new ScaledQuantity(((MaterialComponentImpl) entry).activity, d);
    }
  }

}
