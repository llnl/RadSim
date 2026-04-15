package gov.bnl.nndc.ensdf.decay;

import gov.llnl.rtk.physics.Quantity;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for GammaImpl.
 */
public class GammaImplNGTest {

    public GammaImplNGTest() {
    }

    /**
     * Test constructor and basic getters.
     */
    @Test
    public void testConstructor() {
        double energy = 661.657;
        double energyUnc = 0.003;
        double intensity = 85.1;
        double intensityUnc = 0.2;
        String multipolarity = "M1+E2";

        GammaImpl gamma = new GammaImpl(energy, energyUnc, intensity, intensityUnc, multipolarity);

        assertEquals(energy, gamma.getEnergy().getValue(), 0.001);
        assertEquals(energyUnc, gamma.getEnergy().getUncertainty(), 0.001);
        assertEquals(intensity, gamma.getIntensity().getValue(), 0.001);
        assertEquals(intensityUnc, gamma.getIntensity().getUncertainty(), 0.001);
        assertEquals(multipolarity, gamma.getMultipolarity());
    }

    /**
     * Test getters and setters.
     */
    @Test
    public void testGettersSetters() {
        GammaImpl gamma = new GammaImpl(661.657, 0.003, 85.1, 0.2, "M1+E2");

        // Test setting/getting parent
        gamma.setParent("Ba137m");
        assertEquals("Ba137m", gamma.getParent());

        // Test setting/getting daughter
        gamma.setDaughter("Ba137");
        assertEquals("Ba137", gamma.getDaughter());

        // Test setting/getting intensity with new Quantity object
        Quantity newIntensity = new Quantity(90.0, 0.5);
        gamma.setIntensity(newIntensity);
        assertEquals(90.0, gamma.getIntensity().getValue(), 0.001);
        assertEquals(0.5, gamma.getIntensity().getUncertainty(), 0.001);

        // Test setting/getting multipolarity
        gamma.setMultipolarity("E2");
        assertEquals("E2", gamma.getMultipolarity());

        // Test setting/getting mixing ratio
        gamma.setMixingRatio(0.25);
        assertEquals(0.25, gamma.getMixingRatio(), 0.001);

        // Test setting/getting conversion coefficient
        gamma.setConversionCoefficient(0.05);
        assertEquals(0.05, gamma.getConversionCoefficient(), 0.001);
    }

    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        GammaImpl gamma = new GammaImpl(661.657, 0.003, 85.1, 0.2, "M1+E2");
        gamma.setParent("Ba137m");
        gamma.setDaughter("Ba137");
        gamma.setMixingRatio(0.25);
        gamma.setConversionCoefficient(0.05);

        String str = gamma.toString();

        assertTrue(str.contains("661.657"));
        assertTrue(str.contains("85.1"));
        assertTrue(str.contains("M1+E2"));
        assertTrue(str.contains("Ba137m"));
        assertTrue(str.contains("Ba137"));
    }

    /**
     * Test gamma with zero uncertainty.
     */
    @Test
    public void testZeroUncertainty() {
        GammaImpl gamma = new GammaImpl(661.657, 0.0, 85.1, 0.0, null);

        assertEquals(661.657, gamma.getEnergy().getValue(), 0.001);
        assertEquals(0.0, gamma.getEnergy().getUncertainty(), 0.001);
        assertEquals(85.1, gamma.getIntensity().getValue(), 0.001);
        assertEquals(0.0, gamma.getIntensity().getUncertainty(), 0.001);
        assertNull(gamma.getMultipolarity());
    }

    /**
     * Test equals and hashCode.
     */
    @Test
    public void testEqualsHashCode() {
        GammaImpl gamma1 = new GammaImpl(661.657, 0.003, 85.1, 0.2, "M1+E2");
        gamma1.setParent("Ba137m");
        gamma1.setDaughter("Ba137");

        GammaImpl gamma2 = new GammaImpl(661.657, 0.003, 85.1, 0.2, "M1+E2");
        gamma2.setParent("Ba137m");
        gamma2.setDaughter("Ba137");

        GammaImpl gamma3 = new GammaImpl(1173.228, 0.003, 99.85, 0.03, "E2");
        gamma3.setParent("Co60");
        gamma3.setDaughter("Ni60");

        // Test reflexivity
        assertTrue(gamma1.equals(gamma1));

        // Test symmetry
        assertTrue(gamma1.equals(gamma2));
        assertTrue(gamma2.equals(gamma1));

        // Test with different gamma
        assertFalse(gamma1.equals(gamma3));

        // Test with null and different object type
        assertFalse(gamma1.equals(null));
        assertFalse(gamma1.equals("not a gamma"));

        // Test hashCode consistency with equals
        assertEquals(gamma1.hashCode(), gamma2.hashCode());
    }
}