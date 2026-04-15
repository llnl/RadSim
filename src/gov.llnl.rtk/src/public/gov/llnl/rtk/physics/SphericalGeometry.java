// --- file: gov/llnl/rtk/physics/SphericalGeometry.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.io.Serializable;

/**
 *
 * @author nelson85
 */
class SphericalGeometry implements Geometry, Serializable
{
  public SphericalGeometry()
  {
  }

  @Override
  public Quantity getExtent1()
  {
    return null;
  }

  @Override
  public Quantity getExtent2()
  {
    return null;
  }

  @Override
  public Type getType()
  {
    return Type.SPHERICAL;
  }

  @Override
  public Quantity computeVolume(Quantity inner, Quantity thickness)
  {
    double d0 = inner.get();
    double d1 = d0 + thickness.get();
    double v = 4.0 / 3 * Math.PI * (d1 * d1 * d1 - d0 * d0 * d0);
    return Quantity.of(v, PhysicalProperty.VOLUME);
  }

  @Override
  public Quantity computeThickness(Quantity volume, Quantity inner)
  {
    double d0 = inner.get();
    double v = volume.get() + 4.0 / 3 * Math.PI * d0 * d0 * d0;
    double d1 = Math.pow(v * 3.0 / 4.0 / Math.PI, 1 / 3.0);
    return Quantity.of(d1, PhysicalProperty.LENGTH);
  }

}
