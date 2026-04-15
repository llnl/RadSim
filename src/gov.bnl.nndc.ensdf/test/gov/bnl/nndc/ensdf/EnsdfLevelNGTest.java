package gov.bnl.nndc.ensdf;

import static org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for EnsdfLevel.
 */
public class EnsdfLevelNGTest {

    public EnsdfLevelNGTest() {
    }

    /**
     * Test the constructor and basic getters.
     */
    @Test
    public void testConstructor() {
        String symbol = "137CS";
        EnsdfQuantity energy = new EnsdfQuantity(661.657, 0.003);
        String jpi = "11/2-";
        EnsdfTimeQuantity halfLife = new EnsdfTimeQuantity(2.552, 0.001, "M");
        EnsdfQuantity crossRef = null;

        EnsdfLevel level = new EnsdfLevel(symbol, energy, jpi, halfLife, crossRef);

        assertEquals(symbol, level.getSymbol());
        assertEquals(energy.getValue(), level.getEnergy().getValue(), 0.001);
        assertEquals(energy.getUncertainty(), level.getEnergy().getUncertainty(), 0.001);
        assertEquals(jpi, level.getJpi());
        assertEquals(halfLife.getValue(), level.getHalfLife().getValue(), 0.001);
        assertEquals(halfLife.getUncertainty(), level.getHalfLife().getUncertainty(), 0.001);
        assertEquals(halfLife.getUnit(), level.getHalfLife().getUnit());
    }

    /**
     * Test setting and getting cross-reference.
     */
    @Test
    public void testCrossRef() {
        String symbol = "137CS";
        EnsdfQuantity energy = new EnsdfQuantity(661.657, 0.003);
        String jpi = "11/2-";
        EnsdfTimeQuantity halfLife = new EnsdfTimeQuantity(2.552, 0.001, "M");
        EnsdfQuantity crossRef = new EnsdfQuantity(100.0, 0.0);

        EnsdfLevel level = new EnsdfLevel(symbol, energy, jpi, halfLife, crossRef);

        assertEquals(crossRef.getValue(), level.getCrossRef().getValue(), 0.001);
        assertEquals(crossRef.getUncertainty(), level.getCrossRef().getUncertainty(), 0.001);
    }

    /**
     * Test parsing of spin-parity.
     */
    @Test
    public void testJpi() {
        EnsdfLevel level1 = createTestLevel("1/2+");
        EnsdfLevel level2 = createTestLevel("3-");
        EnsdfLevel level3 = createTestLevel("0");
        EnsdfLevel level4 = createTestLevel("(5/2-)");
        EnsdfLevel level5 = createTestLevel("7/2(+)");

        assertEquals("1/2+", level1.getJpi());
        assertEquals("3-", level2.getJpi());
        assertEquals("0", level3.getJpi());
        assertEquals("(5/2-)", level4.getJpi());
        assertEquals("7/2(+)", level5.getJpi());
    }

    /**
     * Test extension mechanism.
     */
    @Test
    public void testExtensions() {
        EnsdfLevel level = createTestLevel("1/2+");

        // Add and retrieve a string extension
        String key1 = "comment";
        String value1 = "This is a test level";
        level.setExtension(key1, value1);
        assertEquals(value1, level.getExtension(key1));

        // Add and retrieve an object extension
        String key2 = "data";
        Integer value2 = 42;
        level.setExtension(key2, value2);
        assertEquals(value2, level.getExtension(key2));

        // Test getExtensionKeys
        java.util.Set<String> keys = level.getExtensionKeys();
        assertTrue(keys.contains(key1));
        assertTrue(keys.contains(key2));
        assertEquals(2, keys.size());

        // Test removing an extension
        level.removeExtension(key1);
        assertNull(level.getExtension(key1));
        assertEquals(1, level.getExtensionKeys().size());
    }

    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        String symbol = "137CS";
        EnsdfQuantity energy = new EnsdfQuantity(661.657, 0.003);
        String jpi = "11/2-";
        EnsdfTimeQuantity halfLife = new EnsdfTimeQuantity(2.552, 0.001, "M");
        EnsdfQuantity crossRef = new EnsdfQuantity(100.0, 0.0);

        EnsdfLevel level = new EnsdfLevel(symbol, energy, jpi, halfLife, crossRef);

        String str = level.toString();

        assertTrue(str.contains(symbol));
        assertTrue(str.contains(String.valueOf(energy.getValue())));
        assertTrue(str.contains(jpi));
        assertTrue(str.contains(String.valueOf(halfLife.getValue())));
        assertTrue(str.contains(halfLife.getUnit()));
    }

    // Helper method to create a test level
    private EnsdfLevel createTestLevel(String jpi) {
        String symbol = "137CS";
        EnsdfQuantity energy = new EnsdfQuantity(661.657, 0.003);
        EnsdfTimeQuantity halfLife = new EnsdfTimeQuantity(2.552, 0.001, "M");
        EnsdfQuantity crossRef = null;

        return new EnsdfLevel(symbol, energy, jpi, halfLife, crossRef);
    }
}