// --- file: gov/llnl/rtk/physics/SphericalSection.java ---
package gov.llnl.rtk.physics;

import gov.llnl.math.euclidean.Vector3;

/**
 * @author lahmann
 */
public class SphericalSection implements Section
{

  private Vector3 origin, axis;
  private Material material;
  private double theta1, theta2;
  private double phi1, phi2;
  private double innerRadius, outerRadius;

  public SphericalSection(Vector3 origin, Vector3 axis, double theta1, double theta2, double phi1, double phi2, double innerRadius, double outerRadius)
  {

    if (outerRadius <= innerRadius)
    {
      throw new IllegalArgumentException("Outer radius must be strictly greater than the inner radius.");
    }
    if (innerRadius < 0.0)
    {
      throw new IllegalArgumentException(String.format("Invalid value %.2e for inner radius.", innerRadius));
    }
    if (outerRadius <= 0.0)
    {
      throw new IllegalArgumentException(String.format("Invalid value %.2e for outer radius.", outerRadius));
    }

    if (theta2 <= theta1)
    {
      throw new IllegalArgumentException("Theta2 must be strictly greater than theta1.");
    }
    if (theta1 < 0 || theta1 > Math.PI)
    {
      throw new IllegalArgumentException(String.format("Invalid value %.2e for theta1.", theta1));
    }
    if (theta2 < 0 || theta2 > Math.PI)
    {
      throw new IllegalArgumentException(String.format("Invalid value %.2e for theta2.", theta2));
    }

    if (phi2 <= phi1)
    {
      throw new IllegalArgumentException("Phi2 must be strictly greater than Phi1.");
    }
    if (phi1 < 0 || phi1 > 2. * Math.PI)
    {
      throw new IllegalArgumentException(String.format("Invalid value %.2e for phi1.", phi1));
    }
    if (phi2 < 0 || phi2 > 2. * Math.PI)
    {
      throw new IllegalArgumentException(String.format("Invalid value %.2e for phi2.", phi2));
    }

    this.origin = origin;
    this.axis = axis;
    this.theta1 = theta1;
    this.theta2 = theta2;
    this.phi1 = phi1;
    this.phi2 = phi2;
    this.innerRadius = innerRadius;
    this.outerRadius = outerRadius;
  }

  public static SphericalSection HollowSphere(Vector3 origin, double innerRadius, double outerRadius)
  {
    return new SphericalSection(origin, Vector3.AXIS_X, 0.0, Math.PI, 0.0, 2. * Math.PI, innerRadius, outerRadius);
  }

  public static SphericalSection Sphere(Vector3 origin, double radius)
  {
    return new SphericalSection(origin, Vector3.AXIS_X, 0.0, Math.PI, 0.0, 2. * Math.PI, 0.0, radius);
  }

  public static SphericalSection HalfHollowSphere(Vector3 origin, Vector3 axis, double innerRadius, double outerRadius)
  {
    return new SphericalSection(origin, axis, 0.0, Math.PI / 2.0, 0.0, 2. * Math.PI, innerRadius, outerRadius);
  }

  public static SphericalSection HalfSphere(Vector3 origin, Vector3 axis, double radius)
  {
    return new SphericalSection(origin, axis, 0.0, Math.PI / 2.0, 0.0, 2. * Math.PI, 0.0, radius);
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

  public double getTheta1()
  {
    return theta1;
  }

  public double getTheta2()
  {
    return theta2;
  }

  public double getDeltaTheta()
  {
    return theta2 - theta1;
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
}
