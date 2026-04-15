package gov.llnl.rtk.geant4;

import gov.llnl.rtk.flux.FluxBinned;
import gov.llnl.rtk.flux.FluxGroupTrapezoid;
import gov.llnl.rtk.flux.FluxTrapezoid;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * TestNG test class for GEANT4Environment.
 */
public class GEANT4EnvironmentNGTest {

    private GEANT4Environment environment;
    private List<double[]> testDistribution;
    private String sourceParticle;
    private int numberOfParticles;
    private boolean isSpherical;
    private double sourceRadius;
    private List<Object> shieldingSections;

    @BeforeClass
    public void setUpClass() {
        // Create test distribution data
        testDistribution = new ArrayList<>();
        testDistribution.add(new double[]{0.0, 1.0, 0.0});
        testDistribution.add(new double[]{100.0, 2.0, 150.0});
        testDistribution.add(new double[]{200.0, 3.0, 400.0});
        testDistribution.add(new double[]{300.0, 4.0, 750.0});

        // Set test parameters
        sourceParticle = "gamma";
        numberOfParticles = 10000;
        isSpherical = true;
        sourceRadius = 1.5;
        shieldingSections = new ArrayList<>();

        // Create the environment object
        environment = new GEANT4Environment(testDistribution, sourceParticle,
                numberOfParticles, isSpherical, sourceRadius, new ArrayList<>());
    }

    /**
     * Test the constructor.
     */
    @Test
    public void testConstructor() {
        assertNotNull(environment, "Environment should not be null");

        assertEquals(environment.sourceParticle, sourceParticle,
                "Source particle should match constructor parameter");
        assertEquals(environment.numberOfParticles, numberOfParticles,
                "Number of particles should match constructor parameter");
        assertEquals(environment.isSpherical, isSpherical,
                "isSpherical should match constructor parameter");
        assertEquals(environment.sourceRadius, sourceRadius, 0.001,
                "Source radius should match constructor parameter");
        assertNotNull(environment.distribution, "Distribution should be initialized");
        assertNotNull(environment.defaultLines, "Default lines should be initialized");
        assertNotNull(environment.materialLines, "Material lines should be initialized");
        assertNotNull(environment.beamLines, "Beam lines should be initialized");
        assertNotNull(environment.results, "Results list should be initialized");
    }

    /**
     * Test the setSourceParticle method.
     */
    @Test
    public void testSetSourceParticle() {
        String newParticleType = "e-";
        environment.setSourceParticle(newParticleType);
        assertEquals(environment.sourceParticle, newParticleType,
                "Source particle should be updated");
    }

    /**
     * Test the setNumberOfParticle method.
     */
    @Test
    public void testSetNumberOfParticle() {
        int newNumber = 5000;
        environment.setNumberOfParticle(newNumber);
        assertEquals(environment.numberOfParticles, newNumber,
                "Number of particles should be updated");
    }

    /**
     * Test the setDistribution method.
     */
    @Test
    public void testSetDistribution() {
        List<double[]> newDistribution = new ArrayList<>();
        newDistribution.add(new double[]{0.0, 0.5, 0.0});
        newDistribution.add(new double[]{500.0, 1.5, 375.0});

        environment.setDistribution(newDistribution);
        assertSame(environment.distribution, newDistribution,
                "Distribution should be updated to the new list");
    }

    /**
     * Test the writeDistributionToFile method.
     */
    @Test
    public void testWriteDistributionToFile() {
        // Write distribution to file
        environment.writeDistributionToFile();

        // Verify the file was created
        File pdfCdfFile = new File("PdfCDF.csv");
        assertTrue(pdfCdfFile.exists(), "PdfCDF.csv file should be created");

        // Clean up
        pdfCdfFile.delete();
    }

    /**
     * Test the writeDefaultLines method.
     */
    @Test
    public void testWriteDefaultLines() {
        // The method is called in the constructor, so we just verify the result
        List<String> defaultLines = environment.defaultLines;

        assertNotNull(defaultLines, "Default lines should not be null");
        assertTrue(defaultLines.size() >= 4, "Should have at least 4 default lines");
        assertTrue(defaultLines.contains("/control/verbose 2"),
                "Should contain control/verbose line");
        assertTrue(defaultLines.contains("/run/numberOfThreads 8"),
                "Should contain run/numberOfThreads line");
    }

    /**
     * Test the addSphericalObject method.
     */
    @Test
    public void testAddSphericalObject() {
        // Create test parameters
        ArrayList<String> elements = new ArrayList<>();
        elements.add("C");
        elements.add("H");

        ArrayList<Integer> multipliers = new ArrayList<>();
        multipliers.add(1);
        multipliers.add(4);

        double density = 0.8;

        ArrayList<Double> geometries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            geometries.add((double)i);
        }

        // Clear material lines before test
        environment.materialLines.clear();

        // Add spherical object
        environment.addSphericalObject(elements, multipliers, density, geometries);

        // Verify material lines are created
        List<String> materialLines = environment.materialLines;
        assertFalse(materialLines.isEmpty(), "Material lines should not be empty");

        // Should have elements, multipliers, density, geometry, and endMaterial lines
        assertTrue(materialLines.contains("/source/mat/addElement C"),
                "Should contain element C");
        assertTrue(materialLines.contains("/source/mat/addMultiplier 1"),
                "Should contain multiplier 1");
        assertTrue(materialLines.contains("/source/mat/setDensity 0.8 g/cm3"),
                "Should contain density");
        assertTrue(materialLines.contains("/source/mat/setGeometry spherical"),
                "Should specify spherical geometry");
        assertTrue(materialLines.contains("/source/mat/endMaterial"),
                "Should end material definition");
    }

    /**
     * Test the addCylindricalObject method.
     */
    @Test
    public void testAddCylindricalObject() {
        // Create test parameters
        ArrayList<String> elements = new ArrayList<>();
        elements.add("Fe");

        ArrayList<Integer> multipliers = new ArrayList<>();
        multipliers.add(1);

        double density = 7.8;

        ArrayList<Double> geometries = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            geometries.add((double)i);
        }

        // Clear material lines before test
        environment.materialLines.clear();

        // Add cylindrical object
        environment.addCylindricalObject(elements, multipliers, density, geometries);

        // Verify material lines are created
        List<String> materialLines = environment.materialLines;
        assertFalse(materialLines.isEmpty(), "Material lines should not be empty");

        assertTrue(materialLines.contains("/source/mat/addElement Fe"),
                "Should contain element Fe");
        assertTrue(materialLines.contains("/source/mat/setDensity 7.8 g/cm3"),
                "Should contain density");
        assertTrue(materialLines.contains("/source/mat/setGeometry cylindrical"),
                "Should specify cylindrical geometry");
        assertTrue(materialLines.contains("/source/mat/endMaterial"),
                "Should end material definition");
    }

    /**
     * Test the addConicalObject method.
     */
    @Test
    public void testAddConicalObject() {
        // Create test parameters
        ArrayList<String> elements = new ArrayList<>();
        elements.add("Al");

        ArrayList<Integer> multipliers = new ArrayList<>();
        multipliers.add(1);

        double density = 2.7;

        ArrayList<Double> geometries = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            geometries.add((double)i);
        }

        // Clear material lines before test
        environment.materialLines.clear();

        // Add conical object
        environment.addConicalObject(elements, multipliers, density, geometries);

        // Verify material lines are created
        List<String> materialLines = environment.materialLines;
        assertFalse(materialLines.isEmpty(), "Material lines should not be empty");

        assertTrue(materialLines.contains("/source/mat/addElement Al"),
                "Should contain element Al");
        assertTrue(materialLines.contains("/source/mat/setDensity 2.7 g/cm3"),
                "Should contain density");
        assertTrue(materialLines.contains("/source/mat/setGeometry conical"),
                "Should specify conical geometry");
        assertTrue(materialLines.contains("/source/mat/endMaterial"),
                "Should end material definition");
    }

    /**
     * Test the prepareBeamLines method.
     */
    @Test
    public void testPrepareBeamLines() {
        // Clear beam lines before test
        environment.beamLines.clear();

        // Prepare beam lines
        environment.prepareBeamLines();

        // Verify beam lines are created
        List<String> beamLines = environment.beamLines;
        assertFalse(beamLines.isEmpty(), "Beam lines should not be empty");

        assertTrue(beamLines.contains("/run/initialize"),
                "Should include run/initialize");
        assertTrue(beamLines.contains("/generation/beamDef/setBeamType " + environment.sourceParticle),
                "Should include beam type definition");
        assertTrue(beamLines.contains("/run/beamOn " + environment.numberOfParticles),
                "Should include beamOn command with particle count");
    }

    /**
     * Test the writeSettingsToMacro method.
     */
    @Test
    public void testWriteSettingsToMacro() {
        // Execute the method
        environment.writeSettingsToMacro();

        // Verify that the macro files are created
        File cliMacro = new File("rtk.mac");
        File guiMacro = new File("init_vis.mac");

        assertTrue(cliMacro.exists(), "rtk.mac file should be created");
        assertTrue(guiMacro.exists(), "init_vis.mac file should be created");

        // Clean up
        cliMacro.delete();
        guiMacro.delete();
    }

    /**
     * Test the parseHistogramFromFile static method.
     */
    @Test
    public void testParseHistogramFromFile() throws IOException {
        // Create a test histogram file
        Path testFilePath = Paths.get("test_histogram.csv");
        try (FileWriter writer = new FileWriter(testFilePath.toFile())) {
            writer.write("Test header line 1\n");
            writer.write("Test header line 2\n");
            writer.write("Test header line 3\n");
            writer.write("# Bins= 5 Emin= 0.0 Emax= 500.0\n");
            writer.write("Test header line 5\n");
            writer.write("Test header line 6\n");
            writer.write("Test header line 7\n");
            writer.write("100, 0, 0, 0, 0\n");
            writer.write("200, 0, 0, 0, 0\n");
            writer.write("300, 0, 0, 0, 0\n");
            writer.write("400, 0, 0, 0, 0\n");
            writer.write("500, 0, 0, 0, 0\n");
            writer.write("600, 0, 0, 0, 0\n");
            writer.write("700, 0, 0, 0, 0\n");
        }

        // Use reflection to call the private static method
        try {
            java.lang.reflect.Method method = GEANT4Environment.class.getDeclaredMethod("parseHistogramFromFile", String.class);
            method.setAccessible(true);
            FluxBinned spectrum = (FluxBinned) method.invoke(null, testFilePath.toString());

            assertNotNull(spectrum, "Parsed spectrum should not be null");
            // Verify the spectrum content
            assertTrue(spectrum.getPhotonGroups().size() > 0,
                    "Spectrum should have photon groups");

        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        } finally {
            // Clean up
            Files.deleteIfExists(testFilePath);
        }
    }
}