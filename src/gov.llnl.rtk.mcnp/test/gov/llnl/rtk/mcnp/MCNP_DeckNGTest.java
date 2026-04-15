package gov.llnl.rtk.mcnp;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for MCNP_Deck class.
 */
public class MCNP_DeckNGTest {

    private MCNP_Deck deck;

    @BeforeMethod
    public void setUp() {
        // Reset MCNP counters between tests to ensure consistent behavior
        MCNP_Utils.resetAllCounts();
        // Create a new deck for each test
        deck = new MCNP_Deck("Test Deck");
    }

    @Test
    public void testDeckCreation() {
        assertNotNull(deck);
        assertEquals("Test Deck", deck.getTitle());
    }

    @Test
    public void testAddParticles() {
        // Add photon particle
        MCNP_Photon photon = new MCNP_Photon();
        deck.addParticles(photon);

        // Add electron particle
        MCNP_Electron electron = new MCNP_Electron();
        deck.addParticles(electron);

        // Verify deck can be converted to string with particles
        String deckStr = deck.toString();
        assertNotNull(deckStr);

        // Check if the string contains mode line with both particles
        assertTrue(deckStr.contains("mode p e"));
    }

    @Test
    public void testAddSource() {
        // Create a source
        MCNP_Photon photon = new MCNP_Photon();
        MCNP_Source source = new MCNP_Source("Test Source", photon, 1000000);

        // Set position
        source.setPosition(1.0, 2.0, 3.0);

        // Add source to deck
        deck.setSource(source);

        // Verify deck contains source information
        String deckStr = deck.toString();
        assertNotNull(deckStr);
        assertTrue(deckStr.contains("SDEF"));
    }

    @Test
    public void testAddCells() {
        // Create a sphere surface
        MCNP_Surface surface = MCNP_Surface.sphere("Test Sphere", 0.0, 0.0, 0.0, 1.0);

        // Create a volume using the surface
        MCNP_Volume volume = new MCNP_Volume(surface, MCNP_Volume.Orientation.NEGATIVE);

        // Create a cell from the volume
        MCNP_Cell cell = new MCNP_Cell("Test Cell", volume);

        // Create material
        MCNP_Material material = new MCNP_Material("Test Material");
        material.setDensity(1.0);
        material.addElement("H1", 2.0);
        material.addElement("O16", 1.0);

        // Assign material to cell
        cell.setMaterial(material);

        // Add cell to deck
        deck.addCells(cell);

        // Verify deck contains cell information
        String deckStr = deck.toString();
        assertNotNull(deckStr);
        assertTrue(deckStr.contains("Test Cell"));
    }

    @Test
    public void testAddTallys() {
        // Create a tally
        MCNP_Photon photon = new MCNP_Photon();
        MCNP_Tally tally = new MCNP_Tally("Test Tally", photon, MCNP_Tally.Type.SURFACE_CURRENT);

        // Create a surface for the tally
        MCNP_Surface surface = MCNP_Surface.sphere("Tally Sphere", 0.0, 0.0, 0.0, 5.0);
        tally.addSurfaces(surface);

        // Add energy bins
        double[] energyBins = new double[21];
        for (int i = 0; i < energyBins.length; i++) {
            energyBins[i] = i * 0.1;
        }
        tally.addEnergyBins(energyBins);

        // Add cosine bins
        tally.addCosineBins(0.0, 1.0);

        // Add tally to deck
        deck.addTallys(tally);

        // Verify deck contains tally information
        String deckStr = deck.toString();
        assertNotNull(deckStr);
        assertTrue(deckStr.contains("f1"));  // F1 is surface current tally
    }

    @Test
    public void testToString() {
        String deckStr = deck.toString();
        assertNotNull(deckStr);

        // Check if the title is in the deck string
        assertTrue(deckStr.contains("Test Deck"));

        // Check for standard sections
        assertTrue(deckStr.contains("c Cell Cards"));
        assertTrue(deckStr.contains("c Surface Cards"));
        assertTrue(deckStr.contains("c Data Cards"));
    }
}