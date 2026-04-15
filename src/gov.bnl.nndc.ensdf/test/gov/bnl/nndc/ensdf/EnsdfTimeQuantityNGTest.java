package gov.bnl.nndc.ensdf;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for EnsdfTimeQuantity.
 */
public class EnsdfTimeQuantityNGTest {

    public EnsdfTimeQuantityNGTest() {
    }

    /**
     * Test constructor and basic getters.
     */
    @Test
    public void testConstructor() {
        double value = 30.08;
        double uncertainty = 0.05;
        String unit = "Y";

        EnsdfTimeQuantity time = new EnsdfTimeQuantity(value, uncertainty, unit);

        assertEquals(value, time.getValue(), 0.001);
        assertEquals(uncertainty, time.getUncertainty(), 0.001);
        assertEquals(unit, time.getUnit());
    }

    /**
     * Test getters and setters.
     */
    @Test
    public void testGettersSetters() {
        EnsdfTimeQuantity time = new EnsdfTimeQuantity(30.08, 0.05, "Y");

        // Test setting/getting unit
        String newUnit = "D";
        time.setUnit(newUnit);
        assertEquals(newUnit, time.getUnit());

        // Test setting/getting value and uncertainty through parent class
        double newValue = 10958.0;  // ~30 years in days
        double newUncertainty = 18.25;
        time.setValue(newValue);
        time.setUncertainty(newUncertainty);
        assertEquals(newValue, time.getValue(), 0.001);
        assertEquals(newUncertainty, time.getUncertainty(), 0.001);
    }

    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        EnsdfTimeQuantity time = new EnsdfTimeQuantity(30.08, 0.05, "Y");

        String str = time.toString();

        assertTrue(str.contains("30.08"));
        assertTrue(str.contains("0.05"));
        assertTrue(str.contains("Y"));
    }

    /**
     * Test time unit parsing.
     */
    @Test
    public void testTimeUnitConversions() {
        // Test various time units
        EnsdfTimeQuantity fs = new EnsdfTimeQuantity(100.0, 1.0, "FS");
        EnsdfTimeQuantity ps = new EnsdfTimeQuantity(10.0, 0.1, "PS");
        EnsdfTimeQuantity ns = new EnsdfTimeQuantity(1.0, 0.01, "NS");
        EnsdfTimeQuantity us = new EnsdfTimeQuantity(0.1, 0.001, "US");
        EnsdfTimeQuantity ms = new EnsdfTimeQuantity(0.01, 0.0001, "MS");
        EnsdfTimeQuantity s = new EnsdfTimeQuantity(1.0, 0.01, "S");
        EnsdfTimeQuantity m = new EnsdfTimeQuantity(2.552, 0.001, "M");
        EnsdfTimeQuantity h = new EnsdfTimeQuantity(12.0, 0.1, "H");
        EnsdfTimeQuantity d = new EnsdfTimeQuantity(3.0, 0.1, "D");
        EnsdfTimeQuantity y = new EnsdfTimeQuantity(30.08, 0.05, "Y");
        EnsdfTimeQuantity stable = new EnsdfTimeQuantity(0.0, 0.0, "STABLE");

        // Check units
        assertEquals("FS", fs.getUnit());
        assertEquals("PS", ps.getUnit());
        assertEquals("NS", ns.getUnit());
        assertEquals("US", us.getUnit());
        assertEquals("MS", ms.getUnit());
        assertEquals("S", s.getUnit());
        assertEquals("M", m.getUnit());
        assertEquals("H", h.getUnit());
        assertEquals("D", d.getUnit());
        assertEquals("Y", y.getUnit());
        assertEquals("STABLE", stable.getUnit());
    }

    /**
     * Test conversion to seconds.
     */
    @Test
    public void testConversionToSeconds() {
        // Create time quantities with different units
        EnsdfTimeQuantity fs = new EnsdfTimeQuantity(1000.0, 10.0, "FS");
        EnsdfTimeQuantity ps = new EnsdfTimeQuantity(1000.0, 10.0, "PS");
        EnsdfTimeQuantity ns = new EnsdfTimeQuantity(1000.0, 10.0, "NS");
        EnsdfTimeQuantity us = new EnsdfTimeQuantity(1000.0, 10.0, "US");
        EnsdfTimeQuantity ms = new EnsdfTimeQuantity(1000.0, 10.0, "MS");
        EnsdfTimeQuantity s = new EnsdfTimeQuantity(1.0, 0.01, "S");
        EnsdfTimeQuantity m = new EnsdfTimeQuantity(1.0, 0.01, "M");
        EnsdfTimeQuantity h = new EnsdfTimeQuantity(1.0, 0.01, "H");
        EnsdfTimeQuantity d = new EnsdfTimeQuantity(1.0, 0.01, "D");
        EnsdfTimeQuantity y = new EnsdfTimeQuantity(1.0, 0.01, "Y");

        // Convert to seconds and check
        assertEquals(1.0E-12, fs.getSeconds(), 1.0E-14);  // 1000 fs = 10^-12 s
        assertEquals(1.0E-9, ps.getSeconds(), 1.0E-11);   // 1000 ps = 10^-9 s
        assertEquals(1.0E-6, ns.getSeconds(), 1.0E-8);    // 1000 ns = 10^-6 s
        assertEquals(1.0E-3, us.getSeconds(), 1.0E-5);    // 1000 us = 10^-3 s
        assertEquals(1.0, ms.getSeconds(), 1.0E-2);       // 1000 ms = 1 s
        assertEquals(1.0, s.getSeconds(), 1.0E-2);        // 1 s = 1 s
        assertEquals(60.0, m.getSeconds(), 1.0);          // 1 m = 60 s
        assertEquals(3600.0, h.getSeconds(), 40.0);       // 1 h = 3600 s
        assertEquals(86400.0, d.getSeconds(), 900.0);     // 1 d = 86400 s
        assertEquals(31556952.0, y.getSeconds(), 400000.0); // 1 y ≈ 31556952 s (365.2425 days)
    }

    /**
     * Test stable and unknown time.
     */
    @Test
    public void testSpecialTimeValues() {
        EnsdfTimeQuantity stable = new EnsdfTimeQuantity(0.0, 0.0, "STABLE");
        EnsdfTimeQuantity unknown = new EnsdfTimeQuantity(0.0, 0.0, "");

        // Stable should return infinity
        assertEquals(Double.POSITIVE_INFINITY, stable.getSeconds());

        // Unknown should return NaN
        assertTrue(Double.isNaN(unknown.getSeconds()));
    }

    /**
     * Test equality.
     */
    @Test
    public void testEquals() {
        EnsdfTimeQuantity time1 = new EnsdfTimeQuantity(30.08, 0.05, "Y");
        EnsdfTimeQuantity time2 = new EnsdfTimeQuantity(30.08, 0.05, "Y");
        EnsdfTimeQuantity time3 = new EnsdfTimeQuantity(30.08, 0.1, "Y");  // Different uncertainty
        EnsdfTimeQuantity time4 = new EnsdfTimeQuantity(25.0, 0.05, "Y");  // Different value
        EnsdfTimeQuantity time5 = new EnsdfTimeQuantity(30.08, 0.05, "D"); // Different unit

        // Self equality
        assertTrue(time1.equals(time1));

        // Same value, uncertainty, and unit
        assertTrue(time1.equals(time2));
        assertTrue(time2.equals(time1));

        // Different uncertainty
        assertFalse(time1.equals(time3));

        // Different value
        assertFalse(time1.equals(time4));

        // Different unit
        assertFalse(time1.equals(time5));

        // Null and different type
        assertFalse(time1.equals(null));
        assertFalse(time1.equals("not a time quantity"));
    }

    /**
     * Test hashCode.
     */
    @Test
    public void testHashCode() {
        EnsdfTimeQuantity time1 = new EnsdfTimeQuantity(30.08, 0.05, "Y");
        EnsdfTimeQuantity time2 = new EnsdfTimeQuantity(30.08, 0.05, "Y");

        // Equal objects must have equal hash codes
        assertEquals(time1.hashCode(), time2.hashCode());
    }
}