package gov.bnl.nndc.ensdf.decay;

import gov.llnl.rtk.physics.Quantity;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for BetaImpl.
 */
public class BetaImplNGTest {

    public BetaImplNGTest() {
    }

    /**
     * Test constructor and basic getters.
     */
    @Test
    public void testConstructor() {
        double energy = 514.03;  // Cs-137 endpoint energy
        double energyUnc = 0.05;
        double intensity = 94.7;
        double intensityUnc = 0.2;
        String forbiddenness = "1U";

        BetaImpl beta = new BetaImpl(energy, energyUnc, intensity, intensityUnc, forbiddenness);

        assertEquals(energy, beta.getEnergy().getValue(), 0.001);
        assertEquals(energyUnc, beta.getEnergy().getUncertainty(), 0.001);
        assertEquals(intensity, beta.getIntensity().getValue(), 0.001);
        assertEquals(intensityUnc, beta.getIntensity().getUncertainty(), 0.001);
        assertEquals(forbiddenness, beta.getForbiddenness());
    }

    /**
     * Test getters and setters.
     */
    @Test
    public void testGettersSetters() {
        BetaImpl beta = new BetaImpl(514.03, 0.05, 94.7, 0.2, "1U");

        // Test setting/getting parent
        beta.setParent("Cs137");
        assertEquals("Cs137", beta.getParent());

        // Test setting/getting daughter
        beta.setDaughter("Ba137");
        assertEquals("Ba137", beta.getDaughter());

        // Test setting/getting intensity with new Quantity object
        Quantity newIntensity = new Quantity(95.0, 0.3);
        beta.setIntensity(newIntensity);
        assertEquals(95.0, beta.getIntensity().getValue(), 0.001);
        assertEquals(0.3, beta.getIntensity().getUncertainty(), 0.001);

        // Test setting/getting forbiddenness
        beta.setForbiddenness("2U");
        assertEquals("2U", beta.getForbiddenness());

        // Test setting/getting log ft
        beta.setLogFt(9.5);
        assertEquals(9.5, beta.getLogFt(), 0.001);

        // Test setting/getting shape factor
        beta.setShapeFactor("shape");
        assertEquals("shape", beta.getShapeFactor());
    }

    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        BetaImpl beta = new BetaImpl(514.03, 0.05, 94.7, 0.2, "1U");
        beta.setParent("Cs137");
        beta.setDaughter("Ba137");
        beta.setLogFt(9.5);
        beta.setShapeFactor("shape");

        String str = beta.toString();

        assertTrue(str.contains("514.03"));
        assertTrue(str.contains("94.7"));
        assertTrue(str.contains("1U"));
        assertTrue(str.contains("Cs137"));
        assertTrue(str.contains("Ba137"));
        assertTrue(str.contains("9.5"));
    }

    /**
     * Test beta with zero uncertainty.
     */
    @Test
    public void testZeroUncertainty() {
        BetaImpl beta = new BetaImpl(514.03, 0.0, 94.7, 0.0, null);

        assertEquals(514.03, beta.getEnergy().getValue(), 0.001);
        assertEquals(0.0, beta.getEnergy().getUncertainty(), 0.001);
        assertEquals(94.7, beta.getIntensity().getValue(), 0.001);
        assertEquals(0.0, beta.getIntensity().getUncertainty(), 0.001);
        assertNull(beta.getForbiddenness());
    }

    /**
     * Test forbidden transition parsing.
     */
    @Test
    public void testForbiddenness() {
        BetaImpl beta1 = new BetaImpl(514.03, 0.05, 94.7, 0.2, "1U");
        BetaImpl beta2 = new BetaImpl(514.03, 0.05, 94.7, 0.2, "2");
        BetaImpl beta3 = new BetaImpl(514.03, 0.05, 94.7, 0.2, "3F");

        assertEquals("1U", beta1.getForbiddenness());
        assertEquals("2", beta2.getForbiddenness());
        assertEquals("3F", beta3.getForbiddenness());

        // Test parsing degree
        assertEquals(1, beta1.getForbiddennessDegree());
        assertEquals(2, beta2.getForbiddennessDegree());
        assertEquals(3, beta3.getForbiddennessDegree());

        // Test null forbiddenness
        BetaImpl beta4 = new BetaImpl(514.03, 0.05, 94.7, 0.2, null);
        assertEquals(0, beta4.getForbiddennessDegree());
    }

    /**
     * Test equals and hashCode.
     */
    @Test
    public void testEqualsHashCode() {
        BetaImpl beta1 = new BetaImpl(514.03, 0.05, 94.7, 0.2, "1U");
        beta1.setParent("Cs137");
        beta1.setDaughter("Ba137");

        BetaImpl beta2 = new BetaImpl(514.03, 0.05, 94.7, 0.2, "1U");
        beta2.setParent("Cs137");
        beta2.setDaughter("Ba137");

        BetaImpl beta3 = new BetaImpl(1175.63, 0.15, 5.3, 0.1, null);
        beta3.setParent("Cs137");
        beta3.setDaughter("Ba137");

        // Test reflexivity
        assertTrue(beta1.equals(beta1));

        // Test symmetry
        assertTrue(beta1.equals(beta2));
        assertTrue(beta2.equals(beta1));

        // Test with different beta
        assertFalse(beta1.equals(beta3));

        // Test with null and different object type
        assertFalse(beta1.equals(null));
        assertFalse(beta1.equals("not a beta"));

        // Test hashCode consistency with equals
        assertEquals(beta1.hashCode(), beta2.hashCode());
    }
}