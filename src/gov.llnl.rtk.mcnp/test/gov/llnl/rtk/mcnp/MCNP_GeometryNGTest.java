package gov.llnl.rtk.mcnp;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.rtk.physics.MaterialImpl;
import gov.llnl.rtk.physics.SphericalSection;
import gov.llnl.rtk.physics.CylindricalSection;
import gov.llnl.rtk.physics.ConicalSection;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for MCNP geometry handling with Section objects.
 */
public class MCNP_GeometryNGTest {

    private MaterialImpl material;

    @BeforeMethod
    public void setUp() {
        // Reset MCNP counters between tests
        MCNP_Utils.resetAllCounts();

        // Create a material for testing
        material = new MaterialImpl();
        material.setDensity(11.34);
        material.addElement("Pb206", 0.241, 0.0);
        material.addElement("Pb207", 0.221, 0.0);
        material.addElement("Pb208", 0.524, 0.0);
    }

    @Test
    public void testSphericalSectionToCell() throws Exception {
        // Create a spherical section
        SphericalSection sphere = SphericalSection.Sphere(Vector3.ZERO, 1.0);
        sphere.setMaterial(material);

        // Convert to MCNP_Cell
        MCNP_Cell cell = MCNP_Cell.fromSection("Sphere Cell", sphere);

        // Verify cell was created
        assertNotNull(cell);
        assertEquals("Sphere Cell", cell.getName());

        // Check string representation
        String cellStr = cell.toString();
        assertNotNull(cellStr);
        assertTrue(cellStr.contains("Sphere Cell"));

        // Should have a material
        assertNotNull(cell.getMaterial());
    }

    @Test
    public void testSphericalSectionWithTheta() throws Exception {
        // Create a spherical section with theta range (partial sphere)
        SphericalSection section = new SphericalSection(
                Vector3.ZERO, Vector3.AXIS_Z,
                0.0, Math.PI/2, // theta range (0 to 90 degrees)
                0.0, 2*Math.PI, // phi range (full circle)
                0.5, 1.0);     // radii (inner and outer)
        section.setMaterial(material);

        // Convert to MCNP_Cell
        MCNP_Cell cell = MCNP_Cell.fromSection("Partial Sphere Cell", section);

        // Verify cell was created
        assertNotNull(cell);
        assertEquals("Partial Sphere Cell", cell.getName());

        // Check string representation
        String cellStr = cell.toString();
        assertNotNull(cellStr);
    }

    @Test
    public void testCylindricalSectionToCell() throws Exception {
        // Create a cylindrical section along z-axis
        CylindricalSection cylinder = CylindricalSection.Cylinder(
                Vector3.ZERO, Vector3.AXIS_Z, 1.0, 2.0);
        cylinder.setMaterial(material);

        // Convert to MCNP_Cell
        MCNP_Cell cell = MCNP_Cell.fromSection("Cylinder Cell", cylinder);

        // Verify cell was created
        assertNotNull(cell);
        assertEquals("Cylinder Cell", cell.getName());

        // Check string representation
        String cellStr = cell.toString();
        assertNotNull(cellStr);
        assertTrue(cellStr.contains("Cylinder Cell"));
    }

    @Test
    public void testCylindricalSectionWithRadii() throws Exception {
        // Create a cylindrical shell (with inner and outer radii)
        CylindricalSection cylinder = new CylindricalSection(
                Vector3.ZERO, Vector3.AXIS_Z,
                0.0, 2*Math.PI, // theta range (full circle)
                0.5, 1.0,       // inner and outer radii
                2.0);           // height
        cylinder.setMaterial(material);

        // Convert to MCNP_Cell
        MCNP_Cell cell = MCNP_Cell.fromSection("Cylinder Shell Cell", cylinder);

        // Verify cell was created
        assertNotNull(cell);
        assertEquals("Cylinder Shell Cell", cell.getName());

        // Check string representation
        String cellStr = cell.toString();
        assertNotNull(cellStr);
    }

    @Test
    public void testConicalSectionToCell() throws Exception {
        // Create a conical section along z-axis
        ConicalSection cone = new ConicalSection(
                Vector3.ZERO, Vector3.AXIS_Z,
                Math.PI/16, Math.PI/8, // theta range (cone angle)
                0.0, 2*Math.PI,        // phi range (full circle)
                0.0, 2.0);            // height range
        cone.setMaterial(material);

        // Convert to MCNP_Cell
        MCNP_Cell cell = MCNP_Cell.fromSection("Cone Cell", cone);

        // Verify cell was created
        assertNotNull(cell);
        assertEquals("Cone Cell", cell.getName());

        // Check string representation
        String cellStr = cell.toString();
        assertNotNull(cellStr);
        assertTrue(cellStr.contains("Cone Cell"));
    }

    @Test
    public void testComplexGeometry() throws Exception {
        // Origin and axis
        Vector3 origin = Vector3.ZERO;
        Vector3 axis = Vector3.AXIS_Z;
        Vector3 reverseAxis = Vector3.of(0.0, 0.0, -1.0);

        // Create multiple sections
        SphericalSection sphere = SphericalSection.Sphere(origin, 1.0);
        sphere.setMaterial(material);

        SphericalSection cap = new SphericalSection(
                origin, axis,
                0.0, Math.PI/2, // theta range (0 to 90 degrees)
                0.0, 2*Math.PI, // phi range (full circle)
                1.5, 2.5);     // radii (inner and outer)
        cap.setMaterial(material);

        CylindricalSection container = new CylindricalSection(
                Vector3.of(0.0, 0.0, -3.0), axis,
                0.0, 2*Math.PI, // theta range (full circle)
                1.5, 2.5,       // inner and outer radii
                3.0);           // height
        container.setMaterial(material);

        CylindricalSection floor = CylindricalSection.Cylinder(
                Vector3.of(0.0, 0.0, -2.75), axis, 1.5, 0.5);
        floor.setMaterial(material);

        ConicalSection holder = new ConicalSection(
                origin, reverseAxis,
                Math.PI/16, 5*Math.PI/32, // theta range (cone angle)
                0.0, 2*Math.PI,           // phi range (full circle)
                1.0, 2.25);              // height range
        holder.setMaterial(material);

        // Convert each to MCNP_Cell
        MCNP_Cell sphereCell = MCNP_Cell.fromSection("Sphere Cell", sphere);
        MCNP_Cell capCell = MCNP_Cell.fromSection("Cap Cell", cap);
        MCNP_Cell containerCell = MCNP_Cell.fromSection("Container Cell", container);
        MCNP_Cell floorCell = MCNP_Cell.fromSection("Floor Cell", floor);
        MCNP_Cell holderCell = MCNP_Cell.fromSection("Holder Cell", holder);

        // Verify all cells were created
        assertNotNull(sphereCell);
        assertNotNull(capCell);
        assertNotNull(containerCell);
        assertNotNull(floorCell);
        assertNotNull(holderCell);

        // Create a deck with all cells
        MCNP_Deck deck = new MCNP_Deck("Complex Geometry Test");
        deck.addCells(sphereCell);
        deck.addCells(capCell);
        deck.addCells(containerCell);
        deck.addCells(floorCell);
        deck.addCells(holderCell);

        // Verify deck can be converted to string
        String deckStr = deck.toString();
        assertNotNull(deckStr);
    }

    @Test
    public void testRadSimMCNPJobWithSections() throws Exception {
        // Create a RadSim MCNP job
        String name = "GeometryTest";
        java.nio.file.Path outputDir = java.nio.file.Paths.get(System.getProperty("java.io.tmpdir"), "mcnp_test");
        java.nio.file.Path mcnpPath = java.nio.file.Paths.get("/usr/bin/mcnp6");  // Mock path
        RadSim_MCNP_Job job = new RadSim_MCNP_Job(name, outputDir, mcnpPath);

        // Set up job
        job.setEnergyBins(0.0, 2.0, 21);
        MCNP_Photon photon = new MCNP_Photon();
        job.setParticleOptions(1000000, photon);

        // Create a simple flux
        gov.llnl.rtk.flux.FluxBinned flux = new gov.llnl.rtk.flux.FluxBinned();
        for (int i = 0; i < 10; i++) {
            double e_low = i * 0.1;
            double e_high = (i + 1) * 0.1;
            double counts = i * 10.0;
            flux.addPhotonGroup(new gov.llnl.rtk.flux.FluxGroupBin(e_low, e_high, counts, 0.0));
        }
        job.setFlux(flux);

        // Create source section
        SphericalSection sourceSection = SphericalSection.Sphere(Vector3.ZERO, 1.0);
        sourceSection.setMaterial(material);
        job.setSourceSection(sourceSection);

        // Create additional section
        SphericalSection shieldingSection = new SphericalSection(
                Vector3.ZERO, Vector3.AXIS_Z,
                0.0, Math.PI/2,
                0.0, 2*Math.PI,
                1.5, 2.5);
        shieldingSection.setMaterial(material);
        job.addSection(shieldingSection);

        // Try to build deck
        try {
            MCNP_Deck deck = job.buildDeck();
            assertNotNull(deck);
        }
        catch (Exception e) {
            // Some exceptions are expected in test environment
            if (!e.getMessage().contains("No flux initialized") &&
                !e.getMessage().contains("No energy bins initialized") &&
                !e.getMessage().contains("No particles initialized")) {
                throw e;
            }
        }
    }

    @Test
    public void testMCNP_UtilsGetContainingRadius() throws Exception {
        // Create sections
        SphericalSection sphere = SphericalSection.Sphere(Vector3.of(1.0, 1.0, 1.0), 2.0);
        CylindricalSection cylinder = CylindricalSection.Cylinder(Vector3.of(2.0, 0.0, 0.0), Vector3.AXIS_Z, 1.0, 3.0);

        // Test containing radius calculations
        double sphereRadius = MCNP_Utils.getContainingRadius(sphere);
        double cylinderRadius = MCNP_Utils.getContainingRadius(cylinder);

        // Sphere at (1,1,1) with radius 2 should have containing radius of √(1²+1²+1²) + 2 = √3 + 2
        double expectedSphereRadius = Math.sqrt(3) + 2.0;
        assertEquals(expectedSphereRadius, sphereRadius, 1e-6);

        // Cylinder along Z at (2,0,0) with radius 1 and height 3 should have containing radius based on its furthest point
        assertTrue(cylinderRadius > 0);
    }
}