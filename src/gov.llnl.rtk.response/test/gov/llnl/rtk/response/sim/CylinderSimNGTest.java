package gov.llnl.rtk.response.sim;

import gov.llnl.math.euclidean.MutableVector3;
import gov.llnl.math.euclidean.Vector3;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class CylinderSimNGTest
{
  public CylinderSimNGTest()
  {
  }

  @Test
  public void testSetSeed()
  {
    CylinderSim instance1 = new CylinderSim();
    instance1.setSeed(42L);
    instance1.drawShape();
    instance1.drawSourceLocation();
    double[] chords1 = new double[5];
    instance1.simulateDirect(chords1);

    CylinderSim instance2 = new CylinderSim();
    instance2.setSeed(42L);
    instance2.drawShape();
    instance2.drawSourceLocation();
    double[] chords2 = new double[5];
    instance2.simulateDirect(chords2);

    assertEquals(chords1, chords2, "Simulations with same seed should be deterministic");
  }

  @Test
  public void testDrawSourceLocation()
  {
    CylinderSim instance = new CylinderSim();
    instance.drawShape();
    instance.drawSourceLocation();
    // Source should be outside the cylinder
    assertFalse(instance.cylinder.inside(instance.source), "Source should be outside the cylinder");
    // Direction should be normalized
    double norm = Math.sqrt(instance.direction.x * instance.direction.x + instance.direction.y * instance.direction.y + instance.direction.z * instance.direction.z);
    assertEquals(norm, 1.0, 1e-9, "Direction vector should be normalized");
  }

  @Test
  public void testDrawShape()
  {
    CylinderSim instance = new CylinderSim();
    instance.drawShape();
    double r = instance.cylinder.radius;
    double h = instance.cylinder.height;
    double norm = Math.sqrt(4 * r * r + h * h);
    assertEquals(norm, 1.0, 1e-12, "Cylinder dimensions should be normalized");
    assertTrue(r > 0 && h > 0, "Radius and height should be positive");
  }

  @Test
  public void testSimulateChord()
  {
    CylinderSim instance = new CylinderSim();
    instance.drawShape();
    double[] chords = new double[10];
    instance.simulateChord(chords);
    for (double c : chords)
    {
      assertTrue(c > 0 && Double.isFinite(c), "Chord should be positive and finite: " + c);
    }
  }

  @Test
  public void testDrawChord()
  {
    CylinderSim instance = new CylinderSim();
    instance.drawShape();
    double result = instance.drawChord();
    assertTrue(result > 0 && Double.isFinite(result), "Random chord should be positive and finite");
  }

  @Test
  public void testSimulateDirect()
  {
    CylinderSim instance = new CylinderSim();
    instance.drawShape();
    instance.drawSourceLocation();
    double[] chords = new double[10];
    instance.simulateDirect(chords);
    for (double c : chords)
    {
      assertTrue(c > 0 && Double.isFinite(c), "Direct chord should be positive and finite: " + c);
    }
  }

  @Test
  public void testDrawDirect()
  {
    CylinderSim instance = new CylinderSim();
    instance.drawShape();
    instance.drawSourceLocation();
    double result = instance.drawDirect();
    assertTrue(result > 0 && Double.isFinite(result), "Direct chord length should be positive and finite");
  }

  @Test
  public void testSimulateScatter()
  {
    CylinderSim instance = new CylinderSim();
    instance.drawShape();
    instance.drawSourceLocation();
    double[] chords = new double[10];
    double cosAngle = 0.5;
    double attenuation = 0.1;
    instance.simulateScatter(chords, cosAngle, attenuation);
    for (double c : chords)
    {
      assertTrue(c > 0 && Double.isFinite(c), "Scattered chord should be positive and finite: " + c);
    }
  }

  @Test
  public void testDrawScatter()
  {
    CylinderSim instance = new CylinderSim();
    instance.drawShape();
    instance.drawSourceLocation();
    double cosAngle = 0.5;
    double attenuation = 0.1;
    double result = instance.drawScatter(cosAngle, attenuation);
    assertTrue(result > 0 && Double.isFinite(result), "Scattered chord length should be positive and finite");
  }

  @Test
  public void testCoverCylinder()
  {
    CylinderSim instance = new CylinderSim();
    instance.drawShape();
    MutableVector3 source = new MutableVector3(Vector3.of(10, 10, 10));
    double coverage = instance.coverCylinder(instance.cylinder, source);
    // Should be in [-1, 1] and less than 1 (since source is outside)
    assertTrue(coverage < 1.0 && coverage > -1.0, "Coverage cosine should be less than 1 and greater than -1");
  }
}
