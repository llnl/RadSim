package gov.llnl.rtk.mcnp;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for MCNP_Tally class.
 */
public class MCNP_TallyNGTest {

    private MCNP_Tally tally;
    private MCNP_Photon photon;

    @BeforeMethod
    public void setUp() {
        // Reset MCNP counters between tests
        MCNP_Utils.resetAllCounts();

        // Create a photon particle
        photon = new MCNP_Photon();

        // Create a basic surface current tally
        tally = new MCNP_Tally("Test Tally", photon, MCNP_Tally.Type.SURFACE_CURRENT);
    }

    @Test
    public void testTallyCreation() {
        assertNotNull(tally);
        assertEquals(MCNP_Tally.Type.SURFACE_CURRENT, tally.getType());
    }

    @Test
    public void testAddSurfaces() {
        // Create a sphere surface
        MCNP_Surface surface = MCNP_Surface.sphere("Test Sphere", 0.0, 0.0, 0.0, 5.0);

        // Add surface to tally
        tally.addSurfaces(surface);

        // Verify tally configuration
        String tallyStr = tally.toString();
        assertNotNull(tallyStr);
        assertTrue(tallyStr.contains("f1"));  // F1 is surface current tally
    }

    @Test
    public void testAddCells() {
        // Create a sphere surface
        MCNP_Surface surface = MCNP_Surface.sphere("Test Sphere", 0.0, 0.0, 0.0, 1.0);

        // Create a volume using the surface
        MCNP_Volume volume = new MCNP_Volume(surface, MCNP_Volume.Orientation.NEGATIVE);

        // Create a cell from the volume
        MCNP_Cell cell = new MCNP_Cell("Test Cell", volume);

        // Add cell to tally
        tally = new MCNP_Tally("Test Cell Tally", photon, MCNP_Tally.Type.CELL_FLUX);
        tally.addCells(cell);

        // Verify tally configuration
        String tallyStr = tally.toString();
        assertNotNull(tallyStr);
        assertTrue(tallyStr.contains("f4"));  // F4 is cell flux tally
    }

    @Test
    public void testAddEnergyBins() {
        // Create energy bins
        double[] energyBins = new double[21];
        for (int i = 0; i < energyBins.length; i++) {
            energyBins[i] = i * 0.1;
        }

        // Add energy bins to tally
        tally.addEnergyBins(energyBins);

        // Verify tally configuration
        String tallyStr = tally.toString();
        assertNotNull(tallyStr);
        assertTrue(tallyStr.contains("e"));  // 'e' card for energy bins
    }

    @Test
    public void testAddCosineBins() {
        // Add cosine bins for current tally
        tally.addCosineBins(0.0, 1.0);

        // Verify tally configuration
        String tallyStr = tally.toString();
        assertNotNull(tallyStr);
        assertTrue(tallyStr.contains("c"));  // 'c' card for cosine bins
    }

    @Test
    public void testMultipleEnergyBins() {
        // Create a set of specific energy bins
        double[] energyBins = {0.0, 0.1, 0.5, 1.0, 2.0, 5.0, 10.0};

        // Add energy bins to tally
        tally.addEnergyBins(energyBins);

        // Verify tally configuration
        String tallyStr = tally.toString();
        assertNotNull(tallyStr);
        assertTrue(tallyStr.contains("e"));

        // Check if all energy values are present
        for (double energy : energyBins) {
            assertTrue(tallyStr.contains(String.valueOf(energy)) ||
                       tallyStr.contains(String.format("%.1f", energy)));
        }
    }

    @Test
    public void testDifferentTallyTypes() {
        // Test different tally types
        MCNP_Tally currentTally = new MCNP_Tally("Current Tally", photon, MCNP_Tally.Type.SURFACE_CURRENT);
        MCNP_Tally fluxTally = new MCNP_Tally("Flux Tally", photon, MCNP_Tally.Type.SURFACE_FLUX);
        MCNP_Tally cellFluxTally = new MCNP_Tally("Cell Flux Tally", photon, MCNP_Tally.Type.CELL_FLUX);
        MCNP_Tally cellEnergyTally = new MCNP_Tally("Cell Energy Tally", photon, MCNP_Tally.Type.CELL_ENERGY);

        // Verify different tally types generate appropriate MCNP cards
        assertTrue(currentTally.toString().contains("f1"));
        assertTrue(fluxTally.toString().contains("f2"));
        assertTrue(cellFluxTally.toString().contains("f4"));
        assertTrue(cellEnergyTally.toString().contains("f6"));
    }

    @Test
    public void testToString() {
        // Create a sphere surface
        MCNP_Surface surface = MCNP_Surface.sphere("Test Sphere", 0.0, 0.0, 0.0, 5.0);

        // Add surface to tally
        tally.addSurfaces(surface);

        // Add energy bins
        double[] energyBins = {0.0, 0.1, 0.5, 1.0};
        tally.addEnergyBins(energyBins);

        // Convert tally to string
        String tallyStr = tally.toString();
        assertNotNull(tallyStr);

        // Should contain tally type and surface
        assertTrue(tallyStr.contains("f1"));
        assertTrue(tallyStr.contains("e"));
    }
}