// --- file: gov/llnl/rtk/physics/Geometry.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public interface Geometry
{

  public enum Type
  {
    SPHERICAL,
    SPHERE_CAP,
    CYLINDER_FLAT,
    CYLINDER_ROUND,
    SLAB,
    CONE,
    BACKGROUND,
    MIXED
  }

  public static Geometry newSpherical()
  {
    return new SphericalGeometry();
  }

  public static Geometry of(Type type, Quantity extent1, Quantity extent2)
  {
    switch (type)
    {
      case SPHERICAL:
        return newSpherical();
      case CONE:
        return new GeometryImpl(type, extent1, extent2);
      default:
        throw new UnsupportedOperationException();
    }
  }

  /**
   * @return the extent1
   */
  Quantity getExtent1();

  /**
   * @return the extent2
   */
  Quantity getExtent2();

  /**
   * @return the type
   */
  Type getType();

  Quantity computeVolume(Quantity inner, Quantity thickness);
  
  Quantity computeThickness(Quantity volume, Quantity inner);

}
