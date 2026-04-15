// --- file: gov/llnl/rtk/physics/CylindricalSection.java ---
package gov.llnl.rtk.physics;

import gov.llnl.math.euclidean.Vector3;

/**
 * @author lahmann
 */
public class CylindricalSection implements Section
{
  private static final long serialVersionUID = 0xA341_0000_0001_0000L;
  
  private Vector3 origin, axis;
  private Material material;
  private double phi1, phi2;
  private double innerRadius, outerRadius;
  private double depth;

  public CylindricalSection(Vector3 origin, Vector3 axis, double phi1, double phi2, double innerRadius, double outerRadius, double depth)
  {
    this.origin = origin;
    this.axis = axis;
    this.phi1 = phi1;
    this.phi2 = phi2;
    this.innerRadius = innerRadius;
    this.outerRadius = outerRadius;
    this.depth = depth;
  }

  public static CylindricalSection Cylinder(Vector3 origin, Vector3 axis, double radius, double depth)
  {
    return new CylindricalSection(origin, axis, 0.0, 2. * Math.PI, 0.0, radius, depth);
  }

  @Override
  public void setMaterial(Material material)
  {
    this.material = material;
  }

  @Override
  public Vector3 getOrigin()
  {
    return origin;
  }

  @Override
  public Vector3 getAxis()
  {
    return axis;
  }

  @Override
  public Material getMaterial()
  {
    return material;
  }

  public double getPhi1()
  {
    return phi1;
  }

  public double getPhi2()
  {
    return phi2;
  }

  public double getDeltaPhi()
  {
    return phi2 - phi1;
  }

  public double getInnerRadius()
  {
    return innerRadius;
  }

  public double getOuterRadius()
  {
    return outerRadius;
  }

  public double getDepth()
  {
    return depth;
  }

  public double getThickness()
  {
    return outerRadius - innerRadius;
  }
}
