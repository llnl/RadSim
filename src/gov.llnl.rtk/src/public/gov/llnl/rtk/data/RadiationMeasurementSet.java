// --- file: gov/llnl/rtk/data/RadiationMeasurementSet.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import java.io.Serializable;

/**
 *
 * @author nelson85
 */
public interface RadiationMeasurementSet extends Serializable
{
  Iterable<RadiationMeasurement> getMeasurements();
}
