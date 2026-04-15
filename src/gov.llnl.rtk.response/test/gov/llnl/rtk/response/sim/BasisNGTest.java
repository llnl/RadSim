package gov.llnl.rtk.response.sim;

import gov.llnl.math.euclidean.MutableVector3;
import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.random.UniformRandom;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class BasisNGTest
{
  @Test
  public void testAssign_UnitDirection()
  {
    Basis basis = new Basis();
    MutableVector3 ref = new MutableVector3(Vector3.of(1, 0, 0));
    basis.assign(ref);

    // vz should be parallel to ref and normalized
    assertEquals(basis.vz.x, 1.0, 1e-12);
    assertEquals(basis.vz.y, 0.0, 1e-12);
    assertEquals(basis.vz.z, 0.0, 1e-12);

    // vx and vy should be orthonormal to vz and each other
    double dotVxVz = basis.vx.x * basis.vz.x + basis.vx.y * basis.vz.y + basis.vx.z * basis.vz.z;
    double dotVyVz = basis.vy.x * basis.vz.x + basis.vy.y * basis.vz.y + basis.vy.z * basis.vz.z;
    double dotVxVy = basis.vx.x * basis.vy.x + basis.vx.y * basis.vy.y + basis.vx.z * basis.vy.z;

    assertEquals(dotVxVz, 0.0, 1e-12, "vx orthogonal to vz");
    assertEquals(dotVyVz, 0.0, 1e-12, "vy orthogonal to vz");
    assertEquals(dotVxVy, 0.0, 1e-12, "vx orthogonal to vy");

    assertEquals(basis.vx.norm(), 1.0, 1e-12, "vx normalized");
    assertEquals(basis.vy.norm(), 1.0, 1e-12, "vy normalized");
  }

  @Test
  public void testAssign_NonAxisDirection()
  {
    Basis basis = new Basis();
    MutableVector3 ref = new MutableVector3(Vector3.of(1, 2, 3));
    basis.assign(ref);

    // vz should be parallel to ref and normalized
    double norm = Math.sqrt(1 * 1 + 2 * 2 + 3 * 3);
    assertEquals(basis.vz.x, 1.0, 1e-12);
    assertEquals(basis.vz.y, 2.0, 1e-12);
    assertEquals(basis.vz.z, 3.0, 1e-12);

    // vx, vy orthonormal
    double dotVxVz = basis.vx.dot(basis.vz);
    double dotVyVz = basis.vy.dot(basis.vz);
    double dotVxVy = basis.vx.dot(basis.vy);

    assertEquals(dotVxVz, 0.0, 1e-12, "vx orthogonal to vz");
    assertEquals(dotVyVz, 0.0, 1e-12, "vy orthogonal to vz");
    assertEquals(dotVxVy, 0.0, 1e-12, "vx orthogonal to vy");

    assertEquals(basis.vx.norm(), 1.0, 1e-12, "vx normalized");
    assertEquals(basis.vy.norm(), 1.0, 1e-12, "vy normalized");
  }

  @Test
  public void testDrawConeVector_FixedAngle()
  {
    Basis basis = new Basis();
    MutableVector3 ref = new MutableVector3(Vector3.of(0, 0, 1));
    basis.assign(ref);

    UniformRandom ur = new UniformRandom();
    ur.setSeed(12345L);

    for (int i = 0; i < 1000; ++i)
    {
      MutableVector3 out = new MutableVector3();
      // Fixed angle: f1 == f2 == 1.0 (should be along vz)
      basis.drawConeVector(out, ur, 1.0, 1.0);

      assertEquals(out.x, 0.0, 1e-12, "x should be zero");
      assertEquals(out.y, 0.0, 1e-12, "y should be zero");
      assertEquals(out.z, 1.0, 1e-12, "z should be one");
      assertEquals(out.norm(), 1.0, 1e-12, "should be normalized");
    }
  }

  @Test
  public void testDrawConeVector_RandomAngle()
  {
    Basis basis = new Basis();
    MutableVector3 ref = new MutableVector3(Vector3.of(0, 0, 1));
    basis.assign(ref);

    UniformRandom ur = new UniformRandom();
    ur.setSeed(12345L);

    for (int i = 0; i < 1000; ++i)
    {
      MutableVector3 out = new MutableVector3();
      // Random within cone: f1 < f2
      basis.drawConeVector(out, ur, 0.5, 1.0);

      assertTrue(out.z >= 0.5 && out.z <= 1.0, "z component within cone");
      assertEquals(out.norm(), 1.0, 1e-12, "output normalized");
    }
  }

  @Test
  public void testLargest()
  {
    MutableVector3 v = new MutableVector3(Vector3.of(5, -10, 3));
    int idx = Basis.largest(v);
    assertEquals(idx, 1, "largest component is y");

    v = new MutableVector3(-20, 2, 1);
    idx = Basis.largest(v);
    assertEquals(idx, 0, "largest component is x");

    v = new MutableVector3(1, 2, -5);
    idx = Basis.largest(v);
    assertEquals(idx, 2, "largest component is z");
  }
}
