package gov.bnl.nndc.ensdf.decay;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for EmissionCorrelationImpl.
 */
public class EmissionCorrelationImplNGTest {

    public EmissionCorrelationImplNGTest() {
    }

    /**
     * Test constructor and basic getters.
     */
    @Test
    public void testConstructor() {
        double energy = 661.657;
        double intensity = 85.1;
        String comment = "Test emission";

        EmissionCorrelationImpl emission = new EmissionCorrelationImpl(energy, intensity, comment);

        assertEquals(energy, emission.getEnergy(), 0.001);
        assertEquals(intensity, emission.getIntensity(), 0.001);
        assertEquals(comment, emission.getComment());
    }

    /**
     * Test getters and setters.
     */
    @Test
    public void testGettersSetters() {
        EmissionCorrelationImpl emission = new EmissionCorrelationImpl(661.657, 85.1, "Test");

        // Test setting/getting energy
        double newEnergy = 1332.5;
        emission.setEnergy(newEnergy);
        assertEquals(newEnergy, emission.getEnergy(), 0.001);

        // Test setting/getting intensity
        double newIntensity = 90.0;
        emission.setIntensity(newIntensity);
        assertEquals(newIntensity, emission.getIntensity(), 0.001);

        // Test setting/getting comment
        String newComment = "Updated comment";
        emission.setComment(newComment);
        assertEquals(newComment, emission.getComment());

        // Test setting/getting level energy
        double levelEnergy = 1460.0;
        emission.setLevelEnergy(levelEnergy);
        assertEquals(levelEnergy, emission.getLevelEnergy(), 0.001);
    }

    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        EmissionCorrelationImpl emission = new EmissionCorrelationImpl(661.657, 85.1, "Test emission");
        emission.setLevelEnergy(661.657);

        String str = emission.toString();

        assertTrue(str.contains("661.657"));
        assertTrue(str.contains("85.1"));
        assertTrue(str.contains("Test emission"));
    }

    /**
     * Test correlation energy calculation.
     */
    @Test
    public void testCorrelationEnergy() {
        EmissionCorrelationImpl emission = new EmissionCorrelationImpl(661.657, 85.1, "Test");

        // No correlation initially
        assertTrue(Double.isNaN(emission.getCorrelationEnergy()));

        // Set level energy and test correlation
        emission.setLevelEnergy(661.657);
        assertEquals(0.0, emission.getCorrelationEnergy(), 0.001);

        // Set level energy higher than emission energy
        emission.setLevelEnergy(1000.0);
        assertEquals(338.343, emission.getCorrelationEnergy(), 0.001);
    }

    /**
     * Test with null comment.
     */
    @Test
    public void testNullComment() {
        EmissionCorrelationImpl emission = new EmissionCorrelationImpl(661.657, 85.1, null);
        assertNull(emission.getComment());

        // Test that null comment doesn't cause issues in toString
        String str = emission.toString();
        assertNotNull(str);
    }

    /**
     * Test equals and hashCode.
     */
    @Test
    public void testEqualsHashCode() {
        EmissionCorrelationImpl emission1 = new EmissionCorrelationImpl(661.657, 85.1, "Test");
        emission1.setLevelEnergy(661.657);

        EmissionCorrelationImpl emission2 = new EmissionCorrelationImpl(661.657, 85.1, "Test");
        emission2.setLevelEnergy(661.657);

        EmissionCorrelationImpl emission3 = new EmissionCorrelationImpl(1332.5, 100.0, "Different");
        emission3.setLevelEnergy(1332.5);

        // Test reflexivity
        assertTrue(emission1.equals(emission1));

        // Test symmetry
        assertTrue(emission1.equals(emission2));
        assertTrue(emission2.equals(emission1));

        // Test with different emission
        assertFalse(emission1.equals(emission3));

        // Test with null and different object type
        assertFalse(emission1.equals(null));
        assertFalse(emission1.equals("not an emission"));

        // Test hashCode consistency with equals
        assertEquals(emission1.hashCode(), emission2.hashCode());
    }

    /**
     * Test with extreme values.
     */
    @Test
    public void testExtremeValues() {
        // Very small energy and intensity
        EmissionCorrelationImpl small = new EmissionCorrelationImpl(0.001, 0.001, "Small");
        assertEquals(0.001, small.getEnergy(), 0.0001);
        assertEquals(0.001, small.getIntensity(), 0.0001);

        // Very large energy and intensity
        EmissionCorrelationImpl large = new EmissionCorrelationImpl(10000.0, 999.999, "Large");
        assertEquals(10000.0, large.getEnergy(), 0.001);
        assertEquals(999.999, large.getIntensity(), 0.001);
    }
}