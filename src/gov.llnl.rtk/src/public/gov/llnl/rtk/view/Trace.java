// --- file: gov/llnl/rtk/view/Trace.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

import gov.llnl.math.euclidean.Vector3;
import java.time.Instant;

/**
 * Represents a position as a function of time.
 *
 * @author nelson85
 */
@FunctionalInterface
public interface Trace
{
  Vector3 get(Instant time);
}
