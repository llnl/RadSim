package gov.bnl.nndc.ensdf.decay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for BNLDecayLibrary.
 */
public class BNLDecayLibraryNGTest {

    private static final String SAMPLE_DATA =
        "Cs137,Ba137m\n" +
        "Cs137 -> Ba137,0.9,None\n" +
        "Cs137 -> Ba137m,0.1,None\n" +
        "Ba137m -> Ba137,1.0,661.657,0.851\n";

    private Path tempFile;

    public BNLDecayLibraryNGTest() {
    }

    @BeforeClass
    public void setUp() throws IOException {
        // Create a temporary file with sample decay data
        tempFile = Files.createTempFile("bnl_decay_test", ".txt");
        Files.write(tempFile, SAMPLE_DATA.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Test loading the library from a file.
     */
    @Test
    public void testLoadFile() throws Exception {
        BNLDecayLibrary library = new BNLDecayLibrary();
        boolean loaded = library.loadFile(tempFile);

        assertTrue(loaded, "Failed to load decay library from file");
    }

    /**
     * Test loading the library from an input stream.
     */
    @Test
    public void testLoadInputStream() throws Exception {
        BNLDecayLibrary library = new BNLDecayLibrary();
        InputStream stream = new ByteArrayInputStream(SAMPLE_DATA.getBytes(StandardCharsets.UTF_8));
        boolean loaded = library.load(stream);

        assertTrue(loaded, "Failed to load decay library from input stream");
    }

    /**
     * Test retrieving decay transitions.
     */
    @Test
    public void testGetTransitions() throws Exception {
        BNLDecayLibrary library = new BNLDecayLibrary();
        InputStream stream = new ByteArrayInputStream(SAMPLE_DATA.getBytes(StandardCharsets.UTF_8));
        library.load(stream);

        // Get transitions from Cs137 to its daughters
        java.util.List<DecayTransitionImpl> transitions = library.getTransitions("Cs137");
        assertNotNull(transitions);
        assertFalse(transitions.isEmpty());
        assertEquals(2, transitions.size(), "Expected 2 decay transitions for Cs137");

        // Verify the transitions
        boolean foundToBa137 = false;
        boolean foundToBa137m = false;

        for (DecayTransitionImpl transition : transitions) {
            assertEquals("Cs137", transition.getParent());

            if (transition.getDaughter().equals("Ba137")) {
                foundToBa137 = true;
                assertEquals(0.9, transition.getBranchRatio(), 0.01);
            } else if (transition.getDaughter().equals("Ba137m")) {
                foundToBa137m = true;
                assertEquals(0.1, transition.getBranchRatio(), 0.01);
            }
        }

        assertTrue(foundToBa137, "Transition from Cs137 to Ba137 not found");
        assertTrue(foundToBa137m, "Transition from Cs137 to Ba137m not found");
    }

    /**
     * Test retrieving gamma emissions.
     */
    @Test
    public void testGetEmissions() throws Exception {
        BNLDecayLibrary library = new BNLDecayLibrary();
        InputStream stream = new ByteArrayInputStream(SAMPLE_DATA.getBytes(StandardCharsets.UTF_8));
        library.load(stream);

        // Get emissions from Ba137m to Ba137
        java.util.List<EmissionCorrelationImpl> emissions = library.getEmissions("Ba137m", "Ba137");
        assertNotNull(emissions);
        assertFalse(emissions.isEmpty());

        // Verify the gamma emission
        boolean foundGamma = false;
        for (EmissionCorrelationImpl emission : emissions) {
            if (Math.abs(emission.getEnergy() - 661.657) < 0.001) {
                foundGamma = true;
                assertEquals(0.851, emission.getIntensity(), 0.001);
            }
        }

        assertTrue(foundGamma, "661.657 keV gamma from Ba137m not found");
    }

    /**
     * Test handling of unknown nuclides.
     */
    @Test
    public void testUnknownNuclide() throws Exception {
        BNLDecayLibrary library = new BNLDecayLibrary();
        InputStream stream = new ByteArrayInputStream(SAMPLE_DATA.getBytes(StandardCharsets.UTF_8));
        library.load(stream);

        // Get transitions from an unknown nuclide
        java.util.List<DecayTransitionImpl> transitions = library.getTransitions("Unknown");
        assertNotNull(transitions);
        assertTrue(transitions.isEmpty(), "Expected no transitions for unknown nuclide");

        // Get emissions from an unknown transition
        java.util.List<EmissionCorrelationImpl> emissions = library.getEmissions("Unknown1", "Unknown2");
        assertNotNull(emissions);
        assertTrue(emissions.isEmpty(), "Expected no emissions for unknown transition");
    }
}