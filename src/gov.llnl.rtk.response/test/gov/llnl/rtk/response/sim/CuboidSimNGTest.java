package gov.llnl.rtk.response.sim;

import gov.llnl.math.euclidean.MutableVector3;
import gov.llnl.math.euclidean.Vector3;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class CuboidSimNGTest
{
  public CuboidSimNGTest()
  {
  }


  @Test
  public void testSetSeed()
  {
    CuboidSim instance = new CuboidSim();
    instance.setSeed(123L);
    // Check that repeated runs with same seed are deterministic
    instance.drawShape();
    instance.drawSourceLocation();
    double[] chords1 = new double[5];
    instance.simulateDirect(chords1);

    CuboidSim instance2 = new CuboidSim();
    instance2.setSeed(123L);
    instance2.drawShape();
    instance2.drawSourceLocation();
    double[] chords2 = new double[5];
    instance2.simulateDirect(chords2);

    assertEquals(chords1, chords2, "Simulations with same seed should be deterministic");
  }

  @Test
  public void testDrawSourceLocation()
  {
    CuboidSim instance = new CuboidSim();
    instance.drawShape();
    instance.drawSourceLocation();
    // Source should be outside the cuboid
    assertFalse(instance.cuboid.inside(instance.source), "Source should be outside cuboid");
    // Direction should be normalized
    double norm = Math.sqrt(instance.direction.x * instance.direction.x + instance.direction.y * instance.direction.y + instance.direction.z * instance.direction.z);
    assertEquals(norm, 1.0, 1e-9, "Direction vector should be normalized");
  }

  @Test
  public void testDrawShape()
  {
    CuboidSim instance = new CuboidSim();
    instance.drawShape();
    // Should have valid normalized cuboid dimensions
    double norm = Math.sqrt(
            instance.cuboid.dimensions.x * instance.cuboid.dimensions.x
            + instance.cuboid.dimensions.y * instance.cuboid.dimensions.y
            + instance.cuboid.dimensions.z * instance.cuboid.dimensions.z);
    assertEquals(norm, 1.0, 1e-12, "Cuboid dimensions should be normalized");
    assertTrue(instance.cuboid.dimensions.x >= instance.cuboid.dimensions.y && instance.cuboid.dimensions.y >= instance.cuboid.dimensions.z);
  }

  @Test
  public void testSimulateDirect()
  {
    CuboidSim instance = new CuboidSim();
    instance.drawShape();
    instance.drawSourceLocation();
    double[] chords = new double[10];
    instance.simulateDirect(chords);
    // All chords should be positive and finite
    for (double c : chords)
    {
      assertTrue(c > 0 && Double.isFinite(c), "Chord should be positive and finite: " + c);
    }
  }

  @Test
  public void testDrawDirect()
  {
    CuboidSim instance = new CuboidSim();
    instance.drawShape();
    instance.drawSourceLocation();
    double result = instance.drawDirect();
    assertTrue(result > 0 && Double.isFinite(result), "Direct chord length should be positive and finite");
  }

  @Test
  public void testSimulateScatter()
  {
    CuboidSim instance = new CuboidSim();
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
    CuboidSim instance = new CuboidSim();
    instance.drawShape();
    instance.drawSourceLocation();
    double cosAngle = 0.5;
    double attenuation = 0.1;
    double result = instance.drawScatter(cosAngle, attenuation);
    assertTrue(result > 0 && Double.isFinite(result), "Scattered chord length should be positive and finite");
  }

  @Test
  public void testCoverCuboid()
  {
    CuboidSim instance = new CuboidSim();
    instance.drawShape();
    MutableVector3 source = new MutableVector3(Vector3.of(10, 10, 10));
    double coverage = instance.coverCuboid(instance.cuboid, source);
    // Should be in [-1, 1] and less than 1 (since source is outside)
    assertTrue(coverage < 1.0 && coverage > -1.0, "Coverage cosine should be less than 1 and greater than -1");
  }

  @Test
  public void testSimulateChord()
  {
    CuboidSim instance = new CuboidSim();
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
    CuboidSim instance = new CuboidSim();
    instance.drawShape();
    double result = instance.drawChord();
    assertTrue(result > 0 && Double.isFinite(result), "Random chord should be positive and finite");
  }
}
