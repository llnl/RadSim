package gov.llnl.rtk.mcnp;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for MCNP_Surface class.
 */
public class MCNP_SurfaceNGTest {

    @BeforeMethod
    public void setUp() {
        // Reset MCNP counters between tests
        MCNP_Utils.resetAllCounts();
    }

    @Test
    public void testSphereCreation() {
        // Create a sphere surface
        MCNP_Surface sphere = MCNP_Surface.sphere("Test Sphere", 0.0, 0.0, 0.0, 1.0);

        // Verify sphere was created
        assertNotNull(sphere);
        assertEquals("Test Sphere", sphere.getName());

        // Check string representation
        String sphereStr = sphere.toString();
        assertNotNull(sphereStr);
        assertTrue(sphereStr.contains("s"));  // s is the MCNP code for sphere
        assertTrue(sphereStr.contains("0.0 0.0 0.0 1.0"));  // Coordinates and radius
    }

    @Test
    public void testPlaneCreation() {
        // Create a plane surface (normal to x-axis)
        MCNP_Surface plane = MCNP_Surface.plane("Test Plane", 1.0, 0.0, 0.0, 5.0);

        // Verify plane was created
        assertNotNull(plane);
        assertEquals("Test Plane", plane.getName());

        // Check string representation
        String planeStr = plane.toString();
        assertNotNull(planeStr);
        assertTrue(planeStr.contains("p"));  // p is the MCNP code for plane
    }

    @Test
    public void testCylinderCreation() {
        // Create a cylinder surface along z-axis
        MCNP_Surface cylinder = MCNP_Surface.cylinder("Test Cylinder", 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0);

        // Verify cylinder was created
        assertNotNull(cylinder);
        assertEquals("Test Cylinder", cylinder.getName());

        // Check string representation
        String cylinderStr = cylinder.toString();
        assertNotNull(cylinderStr);
        assertTrue(cylinderStr.contains("c/z") || cylinderStr.contains("c/x") || cylinderStr.contains("c/y")); // Cylinder codes
    }

    @Test
    public void testConeCreation() {
        // Create a cone surface along z-axis
        MCNP_Surface cone = MCNP_Surface.cone("Test Cone", 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.5);

        // Verify cone was created
        assertNotNull(cone);
        assertEquals("Test Cone", cone.getName());

        // Check string representation
        String coneStr = cone.toString();
        assertNotNull(coneStr);
        assertTrue(coneStr.contains("k/"));  // k is the MCNP code for cone
    }

    @Test
    public void testInfinitePlaneCreationX() {
        // Create an infinite plane perpendicular to x-axis
        MCNP_Surface plane = MCNP_Surface.px("Test Plane X", 5.0);

        // Verify plane was created
        assertNotNull(plane);
        assertEquals("Test Plane X", plane.getName());

        // Check string representation
        String planeStr = plane.toString();
        assertNotNull(planeStr);
        assertTrue(planeStr.contains("px"));  // px is the MCNP code for plane perpendicular to x
        assertTrue(planeStr.contains("5.0"));  // Coordinate
    }

    @Test
    public void testInfinitePlaneCreationY() {
        // Create an infinite plane perpendicular to y-axis
        MCNP_Surface plane = MCNP_Surface.py("Test Plane Y", 5.0);

        // Verify plane was created
        assertNotNull(plane);
        assertEquals("Test Plane Y", plane.getName());

        // Check string representation
        String planeStr = plane.toString();
        assertNotNull(planeStr);
        assertTrue(planeStr.contains("py"));  // py is the MCNP code for plane perpendicular to y
        assertTrue(planeStr.contains("5.0"));  // Coordinate
    }

    @Test
    public void testInfinitePlaneCreationZ() {
        // Create an infinite plane perpendicular to z-axis
        MCNP_Surface plane = MCNP_Surface.pz("Test Plane Z", 5.0);

        // Verify plane was created
        assertNotNull(plane);
        assertEquals("Test Plane Z", plane.getName());

        // Check string representation
        String planeStr = plane.toString();
        assertNotNull(planeStr);
        assertTrue(planeStr.contains("pz"));  // pz is the MCNP code for plane perpendicular to z
        assertTrue(planeStr.contains("5.0"));  // Coordinate
    }
}