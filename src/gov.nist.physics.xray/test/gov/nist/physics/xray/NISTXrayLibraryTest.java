/*
 * Copyright 2024, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xray;

import gov.llnl.rtk.physics.Element;
import gov.llnl.rtk.physics.Elements;
import gov.llnl.rtk.physics.XrayData;
import gov.llnl.rtk.physics.XrayEdge;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the NISTXrayLibrary class.
 */
public class NISTXrayLibraryTest {

    public NISTXrayLibraryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getInstance method, of class NISTXrayLibrary.
     */
    @Test
    public void testGetInstance() {
        NISTXrayLibrary result = NISTXrayLibrary.getInstance();
        assertNotNull(result, "NISTXrayLibrary singleton instance should not be null");

        // getInstance should always return the same instance
        NISTXrayLibrary result2 = NISTXrayLibrary.getInstance();
        assertSame(result, result2, "Multiple calls to getInstance should return the same instance");
    }

    /**
     * Test of get method for an existing element.
     */
    @Test
    public void testGetExistingElement() {
        Element element = Elements.getElement("Fe"); // Iron
        NISTXrayLibrary instance = NISTXrayLibrary.getInstance();

        XrayData result = instance.get(element);

        assertNotNull(result, "XrayData for Iron should not be null");
        assertEquals(result.getElement().getAtomicNumber(), element.getAtomicNumber(),
                "XrayData should be for the correct element");

        // Check that the result has edges
        List<XrayEdge> edges = result.getEdges();
        assertNotNull(edges, "Edges list should not be null");
        assertFalse(edges.isEmpty(), "Edges list should not be empty for Iron");

        // Test an edge has expected properties
        XrayEdge kEdge = null;
        for (XrayEdge edge : edges) {
            if (edge.getName().equals("K")) {
                kEdge = edge;
                break;
            }
        }
        assertNotNull(kEdge, "Iron should have a K edge");
        assertTrue(kEdge.getFluorescenceYield() > 0, "K edge fluorescence yield should be positive");
        assertNotNull(kEdge.getXrays(), "K edge should have xrays");
        assertFalse(kEdge.getXrays().isEmpty(), "K edge should have non-empty xrays list");
    }

    /**
     * Test of get method for null input.
     */
    @Test
    public void testGetNullElement() {
        NISTXrayLibrary instance = NISTXrayLibrary.getInstance();

        XrayData result = instance.get(null);

        assertNull(result, "XrayData for null element should be null");
    }

    /**
     * Test that all elements from 1-98 can be accessed.
     */
    @Test
    public void testGetAllElements() {
        NISTXrayLibrary instance = NISTXrayLibrary.getInstance();

        for (int z = 1; z <= 98; z++) {
            Element element = Elements.getElement(z);
            XrayData result = instance.get(element);

            assertNotNull(result, "XrayData for element " + element.getSymbol() +
                    " (Z=" + z + ") should not be null");
            assertEquals(result.getElement().getAtomicNumber(), z,
                    "XrayData should be for the correct element");
        }
    }

    /**
     * Test internal structure of the library contains all expected elements.
     */
    @Test
    public void testInternalMaps() {
        NISTXrayLibrary instance = NISTXrayLibrary.getInstance();

        // Use reflection to access the private maps
        Map<String, XrayDataImpl> bySymbol = instance.bySymbol;
        Map<Integer, XrayDataImpl> byNumber = instance.byNumber;

        assertNotNull(bySymbol, "bySymbol map should not be null");
        assertNotNull(byNumber, "byNumber map should not be null");

        assertEquals(bySymbol.size(), 98, "bySymbol map should contain 98 elements");
        assertEquals(byNumber.size(), 98, "byNumber map should contain 98 elements");

        // Check some specific elements
        assertTrue(bySymbol.containsKey("H"), "bySymbol should contain Hydrogen");
        assertTrue(bySymbol.containsKey("Fe"), "bySymbol should contain Iron");
        assertTrue(bySymbol.containsKey("U"), "bySymbol should contain Uranium");

        assertTrue(byNumber.containsKey(1), "byNumber should contain Z=1 (Hydrogen)");
        assertTrue(byNumber.containsKey(26), "byNumber should contain Z=26 (Iron)");
        assertTrue(byNumber.containsKey(92), "byNumber should contain Z=92 (Uranium)");
    }
}