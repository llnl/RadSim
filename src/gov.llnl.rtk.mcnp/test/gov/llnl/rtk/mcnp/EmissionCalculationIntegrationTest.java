package gov.llnl.rtk.mcnp;

import gov.llnl.rtk.physics.Nuclides;
import gov.llnl.rtk.physics.SourceImpl;
import gov.llnl.rtk.physics.EmissionCalculator;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.flux.FluxBinned;
import gov.llnl.rtk.flux.FluxGroupBin;
import gov.llnl.rtk.flux.FluxLineStep;
import gov.llnl.rtk.flux.FluxTrapezoid;
import gov.llnl.rtk.flux.FluxGroupTrapezoid;
import gov.llnl.ensdf.decay.BNLDecayLibrary;
import gov.llnl.math.euclidean.Vector3;
import gov.nist.physics.xray.NISTXrayLibrary;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Integration test for emission calculation workflow.
 * Verifies consistency between the MCNP package and the sourcegen_example approach.
 */
public class EmissionCalculationIntegrationTest {

    private BNLDecayLibrary bnllib;
    private EmissionCalculator emcal;

    @BeforeClass
    public void setUp() throws Exception {
        // Initialize decay library
        bnllib = new BNLDecayLibrary();

        // Set the X-ray library
        bnllib.setXrayLibrary(NISTXrayLibrary.getInstance());

        // Try to find the BNL2023.txt file - look in various possible locations
        List<String> possiblePaths = Arrays.asList(
            "BNL2023.txt",
            "../data/BNL2023.txt",
            "../../data/BNL2023.txt",
            "../../../data/BNL2023.txt"
        );

        boolean fileFound = false;
        for (String pathStr : possiblePaths) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path)) {
                bnllib.loadFile(path);
                fileFound = true;
                break;
            }
        }

        if (!fileFound) {
            // For testing purposes, we'll skip tests if the file isn't available
            System.out.println("Warning: BNL2023.txt not found. Tests will be skipped or run with limited functionality.");
        }

        // Initialize the emission calculator
        emcal = new EmissionCalculator();
        emcal.setDecayLibrary(bnllib);
    }

    /**
     * Test creating a source using the approach from sourcegen_example.ipynb.
     */
    @Test
    public void testSourceCreation() {
        // Define activity using Quantity
        Quantity activity_100Bq = Quantity.of(100, "Bq");
        Quantity activity_94_7Bq = Quantity.of(94.7, "Bq");

        // Create sources with specified activity
        // SourceImpl.fromActivity(nuclide, activity) creates a source with specified activity
        SourceImpl cs137 = SourceImpl.fromActivity(Nuclides.get("Cs137"), activity_100Bq);
        SourceImpl ba137m = SourceImpl.fromActivity(Nuclides.get("Ba137m"), activity_94_7Bq);

        // Verify sources were created
        assertNotNull(cs137);
        assertNotNull(ba137m);
        assertEquals("Cs137", cs137.getNuclide().getId());
        assertEquals("Ba137m", ba137m.getNuclide().getId());

        // Verify activity
        assertEquals(100.0, cs137.getActivity().getValue(), 1e-6);
        assertEquals(94.7, ba137m.getActivity().getValue(), 1e-6);
    }

    /**
     * Test calculating emissions using the approach from sourcegen_example.ipynb.
     */
    @Test
    public void testEmissionCalculation() {
        // Skip test if BNL2023.txt was not found
        if (bnllib == null || !bnllib.isLoaded()) {
            System.out.println("Skipping testEmissionCalculation - decay library not loaded");
            return;
        }

        try {
            // Create sources
            Quantity activity_100Bq = Quantity.of(100, "Bq");
            Quantity activity_94_7Bq = Quantity.of(94.7, "Bq");
            SourceImpl cs137 = SourceImpl.fromActivity(Nuclides.get("Cs137"), activity_100Bq);
            SourceImpl ba137m = SourceImpl.fromActivity(Nuclides.get("Ba137m"), activity_94_7Bq);

            // Create a source list
            ArrayList<SourceImpl> sourceList = new ArrayList<>();
            sourceList.add(cs137);
            sourceList.add(ba137m);

            // Calculate emissions
            gov.llnl.rtk.physics.EmissionResults out = emcal.apply(sourceList);

            // Verify that we got emissions
            assertNotNull(out);
            assertFalse(out.getBetas().isEmpty());
            assertFalse(out.getGammas().isEmpty());

            // Print emission results for visibility in test output
            System.out.println("Beta emissions:");
            for (gov.llnl.rtk.physics.Emission emission : out.getBetas()) {
                double energy = emission.getEnergy().getValue();
                double intensity = emission.getIntensity().getValue();
                System.out.printf("  Energy: %.3f keV, Intensity: %.6f%%\n", energy, intensity);
            }

            System.out.println("Gamma emissions:");
            for (gov.llnl.rtk.physics.Emission emission : out.getGammas()) {
                double energy = emission.getEnergy().getValue();
                double intensity = emission.getIntensity().getValue();
                System.out.printf("  Energy: %.3f keV, Intensity: %.6f%%\n", energy, intensity);
            }

            // Check for specific known emission lines
            boolean foundCs137Beta = false;
            boolean foundBa137mGamma = false;

            for (gov.llnl.rtk.physics.Emission emission : out.getBetas()) {
                double energy = emission.getEnergy().getValue();
                double intensity = emission.getIntensity().getValue();
                if (Math.abs(energy - 514.0) < 1.0 && Math.abs(intensity - 94.7) < 1.0) {
                    foundCs137Beta = true;
                }
            }

            for (gov.llnl.rtk.physics.Emission emission : out.getGammas()) {
                double energy = emission.getEnergy().getValue();
                double intensity = emission.getIntensity().getValue();
                if (Math.abs(energy - 661.7) < 1.0 && Math.abs(intensity - 85.1) < 1.0) {
                    foundBa137mGamma = true;
                }
            }

            assertTrue(foundCs137Beta, "Did not find the expected Cs-137 beta emission (~514 keV)");
            assertTrue(foundBa137mGamma, "Did not find the expected Ba-137m gamma emission (~662 keV)");
        } catch (Exception e) {
            fail("Exception occurred during emission calculation: " + e.getMessage());
        }
    }

    /**
     * Test converting emissions to flux for MCNP calculations.
     */
    @Test
    public void testEmissionToFlux() {
        // Skip test if BNL2023.txt was not found
        if (bnllib == null || !bnllib.isLoaded()) {
            System.out.println("Skipping testEmissionToFlux - decay library not loaded");
            return;
        }

        try {
            // Create sources
            Quantity activity_100Bq = Quantity.of(100, "Bq");
            Quantity activity_94_7Bq = Quantity.of(94.7, "Bq");
            SourceImpl cs137 = SourceImpl.fromActivity(Nuclides.get("Cs137"), activity_100Bq);
            SourceImpl ba137m = SourceImpl.fromActivity(Nuclides.get("Ba137m"), activity_94_7Bq);

            // Create a source list
            ArrayList<SourceImpl> sourceList = new ArrayList<>();
            sourceList.add(cs137);
            sourceList.add(ba137m);

            // Calculate emissions
            gov.llnl.rtk.physics.EmissionResults out = emcal.apply(sourceList);

            // Convert gamma emissions to flux for MCNP
            FluxBinned flux = new FluxBinned();
            for (gov.llnl.rtk.physics.Emission gamma : out.getGammas()) {
                double energy = gamma.getEnergy().getValue();
                double intensity = gamma.getIntensity().getValue();
                flux.addPhotonLine(new FluxLineStep(energy, intensity, 0.0));
            }

            // Verify flux was created with emissions
            assertNotNull(flux);
            assertFalse(flux.getPhotonGroups().isEmpty());

            // Check the flux contains the expected gamma line
            boolean found662keV = false;
            for (gov.llnl.rtk.flux.FluxGroup group : flux.getPhotonGroups()) {
                double energy = (group.getEnergyLower() + group.getEnergyUpper()) / 2.0;
                if (Math.abs(energy - 661.7) < 1.0) {
                    found662keV = true;
                    break;
                }
            }
            assertTrue(found662keV, "Did not find the expected 662 keV gamma line in the flux");

            // Test that the flux can be used with RadSim_MCNP_Job
            RadSim_MCNP_Job job = new RadSim_MCNP_Job("TestJob",
                    Paths.get(System.getProperty("java.io.tmpdir")),
                    Paths.get("/usr/bin/mcnp6")); // Mock path

            // Set the flux
            job.setFlux(flux);

            // Set other required parameters
            job.setEnergyBins(0.0, 2.0, 21);
            job.setParticleOptions(1000000, new MCNP_Photon());

            // No exception should be thrown
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception occurred during flux conversion: " + e.getMessage());
        }
    }

    /**
     * Test creating and using a beta spectrum from a file.
     * This simulates the approach used in mcnp_example.ipynb.
     */
    @Test
    public void testBetaSpectrumFromFile() {
        // This test mocks reading from a file since we might not have the actual beta spectrum file
        try {
            // Create a mock beta spectrum
            FluxTrapezoid flux = new FluxTrapezoid();

            // Add some energy bins (simulating file content)
            double[] energies = {0.0, 100.0, 200.0, 300.0, 400.0, 500.0, 600.0};
            double[] densities = {0.0, 0.2, 0.5, 0.8, 0.5, 0.2, 0.0};

            for (int i = 0; i < energies.length - 1; i++) {
                flux.addPhotonGroup(new FluxGroupTrapezoid(
                        energies[i], energies[i+1],
                        densities[i], densities[i+1]));
            }

            // Verify the flux was created
            assertNotNull(flux);
            assertEquals(energies.length - 1, flux.getPhotonGroups().size());

            // Test that this flux can be used with RadSim_MCNP_Job
            RadSim_MCNP_Job job = new RadSim_MCNP_Job("TestBetaJob",
                    Paths.get(System.getProperty("java.io.tmpdir")),
                    Paths.get("/usr/bin/mcnp6")); // Mock path

            // Set the flux
            job.setFlux(flux);

            // Set other required parameters
            job.setEnergyBins(0.0, 2.0, 21);
            job.setParticleOptions(1000000, new MCNP_Electron(), new MCNP_Photon());

            // No exception should be thrown
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception occurred during beta spectrum test: " + e.getMessage());
        }
    }
}