/*
 * Copyright 2024, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xray;

import gov.llnl.rtk.physics.Xray;
import gov.llnl.rtk.physics.Quantity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the XrayEdgeImpl class.
 */
public class XrayEdgeImplTest {

    private XrayEdgeImpl instance;

    public XrayEdgeImplTest() {
    }

    @BeforeMethod
    public void setUp() {
        instance = new XrayEdgeImpl();
        instance.name = "K";
        instance.energy = 7.112;
        instance.fluorescence_yield = 0.34;
        instance.ratio_jump = 8.0;

        // Create some sample xray lines
        XrayImpl ka1 = new XrayImpl();
        ka1.symbolIUPAC = "K-L3";
        ka1.symbolSiegbahn = "Ka1";
        ka1.energy = Quantity.of(6.403, "keV");
        ka1.intensity = Quantity.scalar(1.0);

        XrayImpl ka2 = new XrayImpl();
        ka2.symbolIUPAC = "K-L2";
        ka2.symbolSiegbahn = "Ka2";
        ka2.energy = Quantity.of(6.390, "keV");
        ka2.intensity = Quantity.scalar(0.5);

        instance.lines = new ArrayList<>();
        instance.lines.add(ka1);
        instance.lines.add(ka2);

        // Add Coster-Kronig transitions
        instance.CK = new HashMap<>();
        instance.CK.put("L1-L3", 0.23);
        instance.CK.put("L2-L3", 0.17);
    }

    /**
     * Test of getName method.
     */
    @Test
    public void testGetName() {
        String expResult = "K";
        String result = instance.getName();
        assertEquals(result, expResult, "Edge name should be K");
    }

    /**
     * Test of getFluorescenceYield method.
     */
    @Test
    public void testGetFlorencenceYield() {
        double expResult = 0.34;
        double result = instance.getFluorescenceYield();
        assertEquals(result, expResult, 0.001, "Fluorescence yield should be 0.34");
    }

    /**
     * Test of getXrays method.
     */
    @Test
    public void testGetXrays() {
        List<Xray> result = instance.getXrays();
        assertNotNull(result, "Xrays list should not be null");
        assertEquals(result.size(), 2, "Should have 2 xray lines");

        Xray ka1 = result.get(0);
        assertEquals(ka1.getName(), "K-L3", "First xray should be K-L3");
        assertEquals(ka1.getEnergy().getValue(), 6.403, 0.001, "K-L3 energy should be correct");
        assertEquals(ka1.getIntensity().getValue(), 1.0, 0.001, "K-L3 intensity should be correct");

        Xray ka2 = result.get(1);
        assertEquals(ka2.getName(), "K-L2", "Second xray should be K-L2");
    }

    /**
     * Test that the returned xray list is unmodifiable.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testUnmodifiableXrays() {
        List<Xray> result = instance.getXrays();
        result.add(new XrayImpl()); // Should throw UnsupportedOperationException
    }

    /**
     * Test of getCosterKronig method.
     */
    @Test
    public void testGetCosterKronig() {
        Map<String, Double> result = instance.getCosterKronig();
        assertNotNull(result, "Coster-Kronig map should not be null");
        assertEquals(result.size(), 2, "Should have 2 Coster-Kronig transitions");

        assertTrue(result.containsKey("L1-L3"), "Should contain L1-L3 transition");
        assertEquals(result.get("L1-L3"), 0.23, 0.001, "L1-L3 transition probability should be correct");

        assertTrue(result.containsKey("L2-L3"), "Should contain L2-L3 transition");
        assertEquals(result.get("L2-L3"), 0.17, 0.001, "L2-L3 transition probability should be correct");
    }

    /**
     * Test interaction with real data from the library.
     */
    @Test
    public void testWithRealData() {
        // Get a real K edge from the library's iron data
        NISTXrayLibrary lib = NISTXrayLibrary.getInstance();
        XrayDataImpl ironData = (XrayDataImpl) lib.get(gov.llnl.rtk.physics.Elements.getElement("Fe"));

        XrayEdgeImpl kEdge = null;
        for (XrayEdgeImpl edge : ironData.edges) {
            if (edge.name.equals("K")) {
                kEdge = edge;
                break;
            }
        }

        assertNotNull(kEdge, "K edge should exist for iron");
        assertTrue(kEdge.energy > 0, "K edge energy should be positive");
        assertTrue(kEdge.fluorescence_yield > 0, "K edge fluorescence yield should be positive");
        assertTrue(kEdge.ratio_jump > 1, "K edge ratio jump should be > 1");

        // Check the xray lines
        assertNotNull(kEdge.lines, "K edge lines should not be null");
        assertFalse(kEdge.lines.isEmpty(), "K edge should have xray lines");

        // Check for common K lines
        boolean hasKa1 = false;
        for (XrayImpl line : kEdge.lines) {
            if (line.symbolSiegbahn != null && line.symbolSiegbahn.equals("Ka1")) {
                hasKa1 = true;
                break;
            }
        }

        assertTrue(hasKa1, "K edge should have Ka1 line");
    }
}