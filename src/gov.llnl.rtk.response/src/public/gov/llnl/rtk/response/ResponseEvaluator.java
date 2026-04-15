// --- file: gov/llnl/rtk/response/ResponseEvaluator.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Versor;

/**
 *
 * @author nelson85
 */
public interface ResponseEvaluator
{

  /**
   * Set the global coordinate for the source.
   *
   * @param global
   */
  void setSourceCoordinate(Vector3 global);

  /**
   * Get the location of the source in global coordinates.
   *
   * @return
   */
  Vector3 getSourceCoordinate();

  /**
   * Set the global coordinate for the instrument.
   *
   * @param global
   */
  void setInstrumentCoordinate(Vector3 global);

  /**
   * Set the orientation of the instrument.
   *
   * @param versor
   */
  void setInstrumentOrientation(Versor versor);

  Vector3 getInstrumentCoordinate();

  Versor getInstrumentOrientation();

  /**
   * Set the location of the sensor within the instrument.
   *
   * @param local
   */
  default void setSensorRelativeCoordinate(Vector3 local)
  {
  }

  /**
   * Set the orientation of the sensor within the instrument.
   *
   * @param local
   */
  default void setSensorRelativeOrientation(Versor local)
  {
  }

  default Vector3 getSensorRelativeCoordinate()
  {
    return null;
  }

  default Versor getSensorRelativeOrientation()
  {
    return null;
  }

}
