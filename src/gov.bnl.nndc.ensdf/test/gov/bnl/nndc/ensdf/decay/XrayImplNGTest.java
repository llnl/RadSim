package gov.bnl.nndc.ensdf.decay;

import gov.llnl.rtk.physics.Quantity;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for XrayImpl.
 */
public class XrayImplNGTest {

    public XrayImplNGTest() {
    }

    /**
     * Test constructor and basic getters.
     */
    @Test
    public void testConstructor() {
        double energy = 31.817;  // Ba K-L2 X-ray energy
        double energyUnc = 0.001;
        double intensity = 2.05;
        double intensityUnc = 0.05;
        String name = "K-L2";

        XrayImpl xray = new XrayImpl(energy, energyUnc, intensity, intensityUnc, name);

        assertEquals(energy, xray.getEnergy().getValue(), 0.001);
        assertEquals(energyUnc, xray.getEnergy().getUncertainty(), 0.001);
        assertEquals(intensity, xray.getIntensity().getValue(), 0.001);
        assertEquals(intensityUnc, xray.getIntensity().getUncertainty(), 0.001);
        assertEquals(name, xray.getName());
    }

    /**
     * Test getters and setters.
     */
    @Test
    public void testGettersSetters() {
        XrayImpl xray = new XrayImpl(31.817, 0.001, 2.05, 0.05, "K-L2");

        // Test setting/getting parent
        xray.setParent("Ba137m");
        assertEquals("Ba137m", xray.getParent());

        // Test setting/getting daughter
        xray.setDaughter("Ba137");
        assertEquals("Ba137", xray.getDaughter());

        // Test setting/getting intensity with new Quantity object
        Quantity newIntensity = new Quantity(2.1, 0.06);
        xray.setIntensity(newIntensity);
        assertEquals(2.1, xray.getIntensity().getValue(), 0.001);
        assertEquals(0.06, xray.getIntensity().getUncertainty(), 0.001);

        // Test setting/getting name
        xray.setName("K-L3");
        assertEquals("K-L3", xray.getName());
    }

    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        XrayImpl xray = new XrayImpl(31.817, 0.001, 2.05, 0.05, "K-L2");
        xray.setParent("Ba137m");
        xray.setDaughter("Ba137");

        String str = xray.toString();

        assertTrue(str.contains("31.817"));
        assertTrue(str.contains("2.05"));
        assertTrue(str.contains("K-L2"));
        assertTrue(str.contains("Ba137m"));
        assertTrue(str.contains("Ba137"));
    }

    /**
     * Test X-ray with zero uncertainty.
     */
    @Test
    public void testZeroUncertainty() {
        XrayImpl xray = new XrayImpl(31.817, 0.0, 2.05, 0.0, "K-L2");

        assertEquals(31.817, xray.getEnergy().getValue(), 0.001);
        assertEquals(0.0, xray.getEnergy().getUncertainty(), 0.001);
        assertEquals(2.05, xray.getIntensity().getValue(), 0.001);
        assertEquals(0.0, xray.getIntensity().getUncertainty(), 0.001);
    }

    /**
     * Test X-ray with null name.
     */
    @Test
    public void testNullName() {
        XrayImpl xray = new XrayImpl(31.817, 0.001, 2.05, 0.05, null);

        assertEquals(31.817, xray.getEnergy().getValue(), 0.001);
        assertNull(xray.getName());
    }

    /**
     * Test equals and hashCode.
     */
    @Test
    public void testEqualsHashCode() {
        XrayImpl xray1 = new XrayImpl(31.817, 0.001, 2.05, 0.05, "K-L2");
        xray1.setParent("Ba137m");
        xray1.setDaughter("Ba137");

        XrayImpl xray2 = new XrayImpl(31.817, 0.001, 2.05, 0.05, "K-L2");
        xray2.setParent("Ba137m");
        xray2.setDaughter("Ba137");

        XrayImpl xray3 = new XrayImpl(32.194, 0.001, 3.78, 0.05, "K-L3");
        xray3.setParent("Ba137m");
        xray3.setDaughter("Ba137");

        // Test reflexivity
        assertTrue(xray1.equals(xray1));

        // Test symmetry
        assertTrue(xray1.equals(xray2));
        assertTrue(xray2.equals(xray1));

        // Test with different xray
        assertFalse(xray1.equals(xray3));

        // Test with null and different object type
        assertFalse(xray1.equals(null));
        assertFalse(xray1.equals("not an xray"));

        // Test hashCode consistency with equals
        assertEquals(xray1.hashCode(), xray2.hashCode());
    }

    /**
     * Test parsing X-ray shell information.
     */
    @Test
    public void testShellParsing() {
        XrayImpl xray1 = new XrayImpl(31.817, 0.001, 2.05, 0.05, "K-L2");
        XrayImpl xray2 = new XrayImpl(32.194, 0.001, 3.78, 0.05, "K-L3");
        XrayImpl xray3 = new XrayImpl(36.378, 0.001, 0.69, 0.05, "K-M3");
        XrayImpl xray4 = new XrayImpl(37.348, 0.001, 0.003, 0.001, "K-N4,5");

        // Test initial shell
        assertEquals("K", xray1.getInitialShell());
        assertEquals("K", xray2.getInitialShell());
        assertEquals("K", xray3.getInitialShell());
        assertEquals("K", xray4.getInitialShell());

        // Test final shell
        assertEquals("L2", xray1.getFinalShell());
        assertEquals("L3", xray2.getFinalShell());
        assertEquals("M3", xray3.getFinalShell());
        assertEquals("N4,5", xray4.getFinalShell());
    }
}