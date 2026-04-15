package gov.bnl.nndc.ensdf;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for EnsdfQuantity.
 */
public class EnsdfQuantityNGTest {

    public EnsdfQuantityNGTest() {
    }

    /**
     * Test constructor and basic getters.
     */
    @Test
    public void testConstructor() {
        double value = 123.45;
        double uncertainty = 0.67;

        EnsdfQuantity quantity = new EnsdfQuantity(value, uncertainty);

        assertEquals(value, quantity.getValue(), 0.001);
        assertEquals(uncertainty, quantity.getUncertainty(), 0.001);
    }

    /**
     * Test equality.
     */
    @Test
    public void testEquals() {
        EnsdfQuantity q1 = new EnsdfQuantity(100.0, 1.0);
        EnsdfQuantity q2 = new EnsdfQuantity(100.0, 1.0);
        EnsdfQuantity q3 = new EnsdfQuantity(100.0, 2.0); // Different uncertainty
        EnsdfQuantity q4 = new EnsdfQuantity(101.0, 1.0); // Different value

        // Self equality
        assertTrue(q1.equals(q1));

        // Same value and uncertainty
        assertTrue(q1.equals(q2));
        assertTrue(q2.equals(q1));

        // Different uncertainty
        assertFalse(q1.equals(q3));

        // Different value
        assertFalse(q1.equals(q4));

        // Null and different type
        assertFalse(q1.equals(null));
        assertFalse(q1.equals("not a quantity"));
    }

    /**
     * Test hashCode consistency with equals.
     */
    @Test
    public void testHashCode() {
        EnsdfQuantity q1 = new EnsdfQuantity(100.0, 1.0);
        EnsdfQuantity q2 = new EnsdfQuantity(100.0, 1.0);

        // Equal objects must have equal hash codes
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    /**
     * Test comparison with uncertainty.
     */
    @Test
    public void testCompareWithUncertainty() {
        EnsdfQuantity q1 = new EnsdfQuantity(100.0, 1.0);
        EnsdfQuantity q2 = new EnsdfQuantity(100.0, 1.0);
        EnsdfQuantity q3 = new EnsdfQuantity(102.0, 1.0);
        EnsdfQuantity q4 = new EnsdfQuantity(98.0, 1.0);

        // Same value and uncertainty
        assertEquals(0, q1.compareWithUncertainty(q2));
        assertEquals(0, q2.compareWithUncertainty(q1));

        // Clearly greater (beyond uncertainty)
        assertTrue(q3.compareWithUncertainty(q1) > 0);
        assertTrue(q1.compareWithUncertainty(q3) < 0);

        // Clearly smaller (beyond uncertainty)
        assertTrue(q4.compareWithUncertainty(q1) < 0);
        assertTrue(q1.compareWithUncertainty(q4) > 0);

        // Within uncertainty (100.0 ± 1.0 vs 101.5 ± 1.0)
        EnsdfQuantity q5 = new EnsdfQuantity(101.5, 1.0);
        assertEquals(0, q1.compareWithUncertainty(q5));
    }

    /**
     * Test string representation.
     */
    @Test
    public void testToString() {
        EnsdfQuantity q1 = new EnsdfQuantity(123.45, 0.67);
        String str = q1.toString();

        assertTrue(str.contains("123.45"));
        assertTrue(str.contains("0.67"));
    }
}