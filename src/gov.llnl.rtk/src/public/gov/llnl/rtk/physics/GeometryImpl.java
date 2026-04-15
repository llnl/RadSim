// --- file: gov/llnl/rtk/physics/GeometryImpl.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.io.Serializable;

/**
 *
 * @author nelson85
 */
public class GeometryImpl implements Serializable, Geometry
{
  
  private Type type;
  private Quantity extent1;
  private Quantity extent2;

  public GeometryImpl()
  {
  }

  GeometryImpl(Type type, Quantity e1, Quantity e2)
  {
    this.type = type;
    this.extent1 = e1;
    this.extent2 = e2;
  }

  /**
   * @return the type
   */
  @Override
  public Type getType()
  {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(Type type)
  {
    this.type = type;
  }

  /**
   * @return the extent1
   */
  @Override
  public Quantity getExtent1()
  {
    return extent1;
  }
  
  /**
   * @return the extent2
   */
  @Override
  public Quantity getExtent2()
  {
    return extent2;
  }

  /**
   * @param extent1 the extent1 to set
   */
  public void setExtent1(Quantity extent1)
  {
    this.extent1 = extent1;
  }


  /**
   * @param extent2 the extent2 to set
   */
  public void setExtent2(Quantity extent2)
  {
    this.extent2 = extent2;
  }
  
  @Override
  public Quantity computeVolume(Quantity inner, Quantity thickness)
  {
    throw new UnsupportedOperationException("Not supported yet."); 
  }

  @Override
  public Quantity computeThickness(Quantity volume, Quantity inner)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
