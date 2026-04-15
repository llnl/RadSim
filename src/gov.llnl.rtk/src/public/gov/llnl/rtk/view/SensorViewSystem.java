// --- file: gov/llnl/rtk/view/SensorViewSystem.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

/**
 * Collection of sensor views representing a number of sensors.
 *
 * This will be the front end for simulating a detector encounter.
 *
 * @author nelson85
 */
public interface SensorViewSystem extends SensorView, Iterable<SensorView>
{
  }
