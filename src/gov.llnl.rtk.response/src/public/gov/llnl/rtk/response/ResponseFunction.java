// --- file: gov/llnl/rtk/response/ResponseFunction.java ---
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
interface ResponseFunction
{

  /**
   * Get the detector model name.
   *
   * @return
   */
  String getModel();

  /**
   * Get the detector vendor.
   *
   * @return
   */
  String getVendor();

  double getGeometryFactor(Vector3 sourceCoordinate, Vector3 globalCoordinate, Versor globalOrientation, Vector3 sensorCoordinate, Versor sensorOrientation);

}
