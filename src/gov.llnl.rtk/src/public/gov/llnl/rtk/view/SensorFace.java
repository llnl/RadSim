// --- file: gov/llnl/rtk/view/SensorFace.java ---
/*
 * Copyright (c) 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

/**
 * Represents one plane of a sensor.
 *
 * Combining multiple faces together as a composite forms a detector surface.
 *
 * @author nelson85
 */
public interface SensorFace extends SensorView
{
  double getArea();
}
