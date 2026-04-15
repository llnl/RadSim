// --- file: gov/llnl/rtk/impl/RadiationSensorImpl.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.impl;

import gov.llnl.rtk.data.RadiationSensor;
import gov.llnl.rtk.data.SensorType;
import gov.llnl.utility.ExpandableObject;
import java.io.Serializable;

public class RadiationSensorImpl extends ExpandableObject implements RadiationSensor, Serializable
{
  private final SensorType type;
  private final int index;

  public RadiationSensorImpl(SensorType type, int index)
  {
    this.type =type;
    this.index = index;
  }

  @Override
  public int getSensorIndex()
  {
    return index;
  }

  @Override
  public SensorType getSensorType()
  {
    return type;
  }
}
