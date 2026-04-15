// --- file: gov/llnl/rtk/physics/ConicalSection.java ---
package gov.llnl.rtk.physics;

import gov.llnl.math.euclidean.Vector3;

/**
 * @author lahmann
 */
public class ConicalSection implements Section
{
  private static final long serialVersionUID = 0xA341_0000_0001_0000L;

  // FIXME use Quantity
  
  private Vector3 origin, axis;
  private Material material;
  private double theta1, theta2;
  private double phi1, phi2;
  private double startLength, endLength;

  public ConicalSection(Vector3 origin, Vector3 axis, double theta1, double theta2, double phi1, double phi2, double startLength, double endLength)
  {
    this.origin = origin;
    this.axis = axis;
    this.theta1 = theta1;
    this.theta2 = theta2;
    this.phi1 = phi1;
    this.phi2 = phi2;
    this.startLength = startLength;
    this.endLength = endLength;
  }

  public static ConicalSection Cone(Vector3 origin, Vector3 axis, double theta, double length)
  {
    return new ConicalSection(origin, axis, 0.0, theta, 0.0, 2. * Math.PI, 0.0, length);
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

  public double getInnerRadiusMinusDz()
  {
    return startLength * Math.sin(theta1);
  }

  public double getInnerRadiusPlusDz()
  {
    return endLength * Math.sin(theta1);
  }

  public double getOuterRadiusMinusDz()
  {
    return startLength * Math.sin(theta2);
  }

  public double getOuterRadiusPlusDz()
  {
    return endLength * Math.sin(theta2);
  }

  public double getStartLength()
  {
    return startLength;
  }

  public double getEndLength()
  {
    return endLength;
  }
}
