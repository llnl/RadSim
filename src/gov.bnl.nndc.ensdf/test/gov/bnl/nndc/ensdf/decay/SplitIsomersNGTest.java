package gov.bnl.nndc.ensdf.decay;

import gov.bnl.nndc.ensdf.*;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for SplitIsomers.
 */
public class SplitIsomersNGTest {

    private EnsdfDataSet dataSet;
    private SplitIsomers splitter;

    public SplitIsomersNGTest() {
    }

    @BeforeClass
    public void setUp() {
        // Create a test data set with ground state and isomeric state
        dataSet = new EnsdfDataSet();

        // Create Ba-137 ground state and metastable state
        EnsdfLevel groundState = new EnsdfLevel("137BA", new EnsdfQuantity(0.0, 0.0),
                "3/2+", new EnsdfTimeQuantity(0.0, 0.0, "STABLE"), null);

        EnsdfLevel isomer = new EnsdfLevel("137BA", new EnsdfQuantity(661.657, 0.003),
                "11/2-", new EnsdfTimeQuantity(2.552, 0.001, "M"), null);

        // Add levels to dataset
        dataSet.addLevel(groundState);
        dataSet.addLevel(isomer);

        // Add gamma from isomer to ground state
        EnsdfGamma gamma = new EnsdfGamma("137BA", new EnsdfQuantity(661.657, 0.003),
                new EnsdfQuantity(85.1, 0.2), "M4", null);
        dataSet.addGamma(isomer, gamma);

        // Initialize the splitter
        splitter = new SplitIsomers();
    }

    /**
     * Test the constructor.
     */
    @Test
    public void testConstructor() {
        SplitIsomers instance = new SplitIsomers();
        assertNotNull(instance);
    }

    /**
     * Test setting and getting options.
     */
    @Test
    public void testOptions() {
        SplitIsomers instance = new SplitIsomers();

        // Test default options
        assertFalse(instance.getOption(SplitIsomers.Option.REJECT_UNCERTAIN_LEVELS));
        assertTrue(instance.getOption(SplitIsomers.Option.DECAY_FROM_GROUND));

        // Test setting options
        instance.setOption(SplitIsomers.Option.REJECT_UNCERTAIN_LEVELS, true);
        instance.setOption(SplitIsomers.Option.DECAY_FROM_GROUND, false);

        assertTrue(instance.getOption(SplitIsomers.Option.REJECT_UNCERTAIN_LEVELS));
        assertFalse(instance.getOption(SplitIsomers.Option.DECAY_FROM_GROUND));
    }

    /**
     * Test identifying isomers in the dataset.
     */
    @Test
    public void testIdentifyIsomers() {
        List<String> isomers = splitter.identifyIsomers(dataSet, "137BA");

        assertNotNull(isomers);
        assertEquals(1, isomers.size());
        assertEquals("137BAm", isomers.get(0));
    }

    /**
     * Test splitting isomers.
     */
    @Test
    public void testSplitIsomer() {
        // Split the isomers
        List<DecayTransitionImpl> transitions = new ArrayList<>();
        splitter.splitIsomer(dataSet, "137BA", transitions);

        // Check transitions
        assertNotNull(transitions);
        assertEquals(1, transitions.size());

        DecayTransitionImpl transition = transitions.get(0);
        assertEquals("137BAm", transition.getParent());
        assertEquals("137BA", transition.getDaughter());
        assertEquals(1.0, transition.getBranchRatio(), 0.001);

        // Check emissions (gamma)
        List<EmissionCorrelationImpl> emissions = new ArrayList<>();
        splitter.getEmissions(dataSet, transition, emissions);

        assertNotNull(emissions);
        assertEquals(1, emissions.size());

        EmissionCorrelationImpl emission = emissions.get(0);
        assertEquals(661.657, emission.getEnergy(), 0.001);
        assertEquals(85.1, emission.getIntensity(), 0.1);
    }

    /**
     * Test isomer naming convention.
     */
    @Test
    public void testIsomerNaming() {
        // Create a test dataset with multiple isomeric states
        EnsdfDataSet testSet = new EnsdfDataSet();

        // Ground state
        EnsdfLevel ground = new EnsdfLevel("133BA", new EnsdfQuantity(0.0, 0.0),
                "1/2+", new EnsdfTimeQuantity(0.0, 0.0, "STABLE"), null);

        // First isomer
        EnsdfLevel isomer1 = new EnsdfLevel("133BA", new EnsdfQuantity(288.25, 0.05),
                "11/2-", new EnsdfTimeQuantity(38.9, 0.1, "H"), null);

        // Second isomer
        EnsdfLevel isomer2 = new EnsdfLevel("133BA", new EnsdfQuantity(1942.0, 0.5),
                "19/2-", new EnsdfTimeQuantity(17.5, 0.5, "NS"), null);

        testSet.addLevel(ground);
        testSet.addLevel(isomer1);
        testSet.addLevel(isomer2);

        // Test isomer identification
        List<String> isomers = splitter.identifyIsomers(testSet, "133BA");

        assertNotNull(isomers);
        assertEquals(2, isomers.size());
        assertEquals("133BAm", isomers.get(0));
        assertEquals("133BAn", isomers.get(1));
    }

    /**
     * Test the decay chain creation.
     */
    @Test
    public void testDecayChain() {
        // Create a test dataset with a decay chain: A -> B -> C
        EnsdfDataSet testSet = new EnsdfDataSet();

        // Create levels for three nuclides in a chain
        EnsdfLevel levelA = new EnsdfLevel("A", new EnsdfQuantity(0.0, 0.0),
                "0+", new EnsdfTimeQuantity(10.0, 0.1, "S"), null);

        EnsdfLevel levelB = new EnsdfLevel("B", new EnsdfQuantity(0.0, 0.0),
                "0+", new EnsdfTimeQuantity(5.0, 0.1, "S"), null);

        EnsdfLevel levelC = new EnsdfLevel("C", new EnsdfQuantity(0.0, 0.0),
                "0+", new EnsdfTimeQuantity(0.0, 0.0, "STABLE"), null);

        testSet.addLevel(levelA);
        testSet.addLevel(levelB);
        testSet.addLevel(levelC);

        // Create beta transitions
        EnsdfBeta betaAB = new EnsdfBeta("A", new EnsdfQuantity(1000.0, 1.0),
                new EnsdfQuantity(100.0, 0.0), "1U", null);

        EnsdfBeta betaBC = new EnsdfBeta("B", new EnsdfQuantity(500.0, 1.0),
                new EnsdfQuantity(100.0, 0.0), "1U", null);

        testSet.addBeta(levelA, betaAB);
        testSet.addBeta(levelB, betaBC);

        // Process the chain
        List<DecayTransitionImpl> transitions = new ArrayList<>();

        SplitIsomers instance = new SplitIsomers();
        instance.setOption(SplitIsomers.Option.DECAY_FROM_GROUND, true);

        instance.processChain(testSet, transitions);

        // Check results
        assertNotNull(transitions);
        assertEquals(2, transitions.size());

        // Check A -> B transition
        boolean foundAtoB = false;
        boolean foundBtoC = false;

        for (DecayTransitionImpl trans : transitions) {
            if (trans.getParent().equals("A") && trans.getDaughter().equals("B")) {
                foundAtoB = true;
                assertEquals(1.0, trans.getBranchRatio(), 0.001);
            }
            else if (trans.getParent().equals("B") && trans.getDaughter().equals("C")) {
                foundBtoC = true;
                assertEquals(1.0, trans.getBranchRatio(), 0.001);
            }
        }

        assertTrue(foundAtoB, "A -> B transition not found");
        assertTrue(foundBtoC, "B -> C transition not found");
    }
}