// --- file: gov/llnl/rtk/impl/FaultImpl.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.impl;

import gov.llnl.rtk.quality.Fault;
import gov.llnl.rtk.quality.FaultLevel;
import gov.llnl.utility.UUIDUtilities;

/**
 *
 * @author nelson85
 */
public class FaultImpl implements Fault
{
  private static final long serialVersionUID = UUIDUtilities.createLong("Fault-v1");

  final private Object source;
  final private String name;
  final private boolean recoverable;
  private boolean state = false;
  final private FaultLevel level;
  private final String description;

  public FaultImpl(FaultLevel level, String name, String description, Object source, boolean recoverable)
  {
    this.source = source;
    this.name = name;
    this.description = description;
    this.level = level;
    this.recoverable = recoverable;
  }

  public void setUnRecoverable()
  {
  }

  /**
   * @return the source
   */
  @Override
  public Object getSource()
  {
    return source;
  }

  /**
   * @return the description
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * @return the recoverable
   */
  @Override
  public boolean isRecoverable()
  {
    return recoverable;
  }

  @Override
  public boolean isState()
  {
    return state;
  }

  public void setState(boolean state)
  {
    this.state = state;
  }

  @Override
  public int compareTo(Fault t)
  {
    if (t.getLevel() == this.level)
      return this.name.compareTo(t.getName());
    return this.level.compareTo(t.getLevel());
  }

  @Override
  public FaultLevel getLevel()
  {
    return level;
  }

  @Override
  public String getDescription()
  {
    if (description == null)
      return name;
    return description;
  }


}
