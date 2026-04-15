package gov.llnl.rtk.mcnp;

import gov.llnl.math.euclidean.Vector3;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for MCNP_Source class.
 */
public class MCNP_SourceNGTest {

    private MCNP_Source source;
    private MCNP_Photon photon;

    @BeforeMethod
    public void setUp() {
        // Reset MCNP counters between tests
        MCNP_Utils.resetAllCounts();

        // Create a photon particle
        photon = new MCNP_Photon();

        // Create a basic source for testing
        source = new MCNP_Source("Test Source", photon, 1000000);
    }

    @Test
    public void testSourceCreation() {
        assertNotNull(source);
        assertEquals(1000000, source.getNumParticles());
    }

    @Test
    public void testSetPosition() {
        // Set position
        source.setPosition(1.0, 2.0, 3.0);

        // Get position
        Vector3 pos = source.getPosition();

        // Verify position
        assertNotNull(pos);
        assertEquals(1.0, pos.getX(), 1e-6);
        assertEquals(2.0, pos.getY(), 1e-6);
        assertEquals(3.0, pos.getZ(), 1e-6);
    }

    @Test
    public void testSetUniformSphericalDistribution() {
        // Create origin and radius
        Vector3 origin = Vector3.of(0.0, 0.0, 0.0);
        double radius = 2.0;

        // Set spherical distribution
        source.setUniformSphericalDistribution(origin, radius);

        // Verify source configuration
        String sourceStr = source.toString();
        assertNotNull(sourceStr);

        // Should contain spherical distribution parameters
        assertTrue(sourceStr.contains("SP"));
    }

    @Test
    public void testSetEnergyDistribution() throws Exception {
        // Create a distribution
        MCNP_Distribution distribution = new MCNP_Distribution("Test Distribution");
        distribution.addPoint(0.0, 0.0);
        distribution.addPoint(1.0, 1.0);

        // Set energy distribution
        source.setEnergyDistribution(distribution);

        // Verify source configuration
        String sourceStr = source.toString();
        assertNotNull(sourceStr);

        // Should contain energy distribution
        assertTrue(sourceStr.contains("ERG"));
    }

    @Test
    public void testSetCell() {
        // Create a sphere surface
        MCNP_Surface surface = MCNP_Surface.sphere("Test Sphere", 0.0, 0.0, 0.0, 1.0);

        // Create a volume using the surface
        MCNP_Volume volume = new MCNP_Volume(surface, MCNP_Volume.Orientation.NEGATIVE);

        // Create a cell from the volume
        MCNP_Cell cell = new MCNP_Cell("Test Cell", volume);

        // Set cell for source
        source.setCell(cell);

        // Verify source configuration
        String sourceStr = source.toString();
        assertNotNull(sourceStr);

        // Should contain cell information
        assertTrue(sourceStr.contains("CEL"));
    }

    @Test
    public void testToString() {
        // Set position
        source.setPosition(1.0, 2.0, 3.0);

        // Convert source to string
        String sourceStr = source.toString();
        assertNotNull(sourceStr);

        // Should contain SDEF card and position
        assertTrue(sourceStr.contains("SDEF"));
        assertTrue(sourceStr.contains("POS"));
    }
}