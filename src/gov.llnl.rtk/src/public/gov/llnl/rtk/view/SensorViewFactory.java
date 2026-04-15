// --- file: gov/llnl/rtk/view/SensorViewFactory.java ---
/*
 * Copyright (c) 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Versor;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class SensorViewFactory
{
  /**
   * Create a rectangular cuboid.
   *
   * Cuboid is centered on the front face which faces in along the getX axis.
   *
   * @param width is the width in the getY direction.
   * @param height is the height in the getZ direction.
   * @param depth is the depth in the getX direction.
   * @param origin is the
   * @param orientation
   * @return
   */
  public static SensorView createCuboid(double width, double height, double depth,
          Vector3 origin, Versor orientation)
  {
    SensorView[] face = new SensorFace[6];
    face[0] = new SensorFaceRectangular(width, height,
            Vector3.ZERO, Versor.ZERO);
    face[1] = new SensorFaceRectangular(depth, height,
            Vector3.of(-depth/2, width / 2, 0), Versor.of(Vector3.AXIS_Z, Math.PI / 2));
    face[2] = new SensorFaceRectangular(width, height,
            Vector3.of(-depth, 0, 0), Versor.of(Vector3.AXIS_Z, Math.PI));
    face[3] = new SensorFaceRectangular(depth, height,
            Vector3.of(-depth/2, -width / 2, 0), Versor.of(Vector3.AXIS_Z, -Math.PI / 2));
    face[4] = new SensorFaceRectangular(width, depth,
            Vector3.of(-depth/2, 0, height / 2), Versor.of(Vector3.AXIS_Y, -Math.PI / 2));
    face[5] = new SensorFaceRectangular(width, depth,
            Vector3.of(-depth/2, 0, -height / 2), Versor.of(Vector3.AXIS_Y, Math.PI / 2));
    return new SensorViewComposite(Arrays.asList(face), origin, orientation);
  }

  public static SensorView createRectangular(double width, double height, Vector3 origin, Versor orientation)
  {
    return new SensorFaceRectangular(width, height,
            origin, orientation);
  }

  public static SensorView createRectangularCollimated(double width, double height,
          Vector3 origin, Versor orientation, double side, double top)
  {
    return new SensorFaceRectangularCollimated(width, height,
            origin, orientation, side, top);
  }

  public static SensorView createSmall(double area, Vector3 origin, Versor orientation)
  {
    return new SensorFaceSmall(area,
            origin, orientation);
  }

  public static SensorView createComposite(List<SensorView> faces, Vector3 origin, Versor orientation)
  {
    return new SensorViewComposite(faces, origin, orientation);
  }
}
