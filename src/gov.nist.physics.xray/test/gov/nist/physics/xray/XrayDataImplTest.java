/*
 * Copyright 2024, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xray;

import gov.llnl.rtk.physics.Element;
import gov.llnl.rtk.physics.Elements;
import gov.llnl.rtk.physics.XrayEdge;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the XrayDataImpl class.
 */
public class XrayDataImplTest {

    private XrayDataImpl instance;

    public XrayDataImplTest() {
    }

    @BeforeMethod
    public void setUp() {
        instance = new XrayDataImpl();
        instance.name = "Fe";
        instance.atomic_number = 26;
        instance.atomic_weigth = 55.845;
        instance.density = 7.874;

        // Create some sample edges
        XrayEdgeImpl kEdge = new XrayEdgeImpl();
        kEdge.name = "K";
        kEdge.energy = 7.112;
        kEdge.fluorescence_yield = 0.34;
        kEdge.ratio_jump = 8.0;

        XrayEdgeImpl l1Edge = new XrayEdgeImpl();
        l1Edge.name = "L1";
        l1Edge.energy = 0.846;
        l1Edge.fluorescence_yield = 0.05;
        l1Edge.ratio_jump = 2.0;

        instance.edges = new ArrayList<>();
        instance.edges.add(kEdge);
        instance.edges.add(l1Edge);
    }

    /**
     * Test of getElement method.
     */
    @Test
    public void testGetElement() {
        Element result = instance.getElement();
        assertNotNull(result, "Element should not be null");
        assertEquals(result.getAtomicNumber(), 26, "Should return element with atomic number 26");
        assertEquals(result.getSymbol(), "Fe", "Should return element with symbol Fe");
    }

    /**
     * Test of getEdges method.
     */
    @Test
    public void testGetEdges() {
        List<XrayEdge> result = instance.getEdges();
        assertNotNull(result, "Edges list should not be null");
        assertEquals(result.size(), 2, "Should have 2 edges");

        XrayEdge kEdge = result.get(0);
        assertEquals(kEdge.getName(), "K", "First edge should be K");
        assertEquals(((XrayEdgeImpl)kEdge).energy, 7.112, 0.001, "K edge energy should be correct");
        assertEquals(kEdge.getFluorescenceYield(), 0.34, 0.001, "K edge fluorescence yield should be correct");

        XrayEdge l1Edge = result.get(1);
        assertEquals(l1Edge.getName(), "L1", "Second edge should be L1");
    }

    /**
     * Test that the returned edge list is unmodifiable.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testUnmodifiableEdges() {
        List<XrayEdge> result = instance.getEdges();
        result.add(new XrayEdgeImpl()); // Should throw UnsupportedOperationException
    }

    /**
     * Test interaction with real data from the library.
     */
    @Test
    public void testWithRealData() {
        // Get real data for iron from the library
        NISTXrayLibrary lib = NISTXrayLibrary.getInstance();
        XrayDataImpl ironData = (XrayDataImpl) lib.get(Elements.getElement("Fe"));

        assertNotNull(ironData, "Iron data should not be null");
        assertEquals(ironData.name, "Fe", "Element name should be Fe");
        assertEquals(ironData.atomic_number, 26, "Atomic number should be 26");

        List<XrayEdge> edges = ironData.getEdges();
        assertNotNull(edges, "Edges list should not be null");
        assertFalse(edges.isEmpty(), "Edges list should not be empty");

        // Check for photo and scatter data
        assertNotNull(ironData.photo, "Photo absorption data should be present");
        assertNotNull(ironData.scatter, "Scatter data should be present");

        // Check dimensions of data arrays
        assertTrue(ironData.photo.length > 0, "Photo data should have rows");
        assertTrue(ironData.scatter.length > 0, "Scatter data should have rows");
    }
}