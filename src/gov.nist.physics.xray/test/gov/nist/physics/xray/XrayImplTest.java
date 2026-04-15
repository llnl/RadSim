/*
 * Copyright 2024, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xray;

import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.physics.Transition;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the XrayImpl class.
 */
public class XrayImplTest {

    private XrayImpl instance;

    public XrayImplTest() {
    }

    @BeforeMethod
    public void setUp() {
        instance = new XrayImpl();
        instance.symbolIUPAC = "K-L3";
        instance.symbolSiegbahn = "Ka1";
        instance.energy = Quantity.of(6.403, "keV");
        instance.intensity = Quantity.scalar(1.0);
    }

    /**
     * Test of getName method.
     */
    @Test
    public void testGetName() {
        String expResult = "K-L3";
        String result = instance.getName();
        assertEquals(result, expResult, "Xray name should be K-L3");
    }

    /**
     * Test of getEnergy method.
     */
    @Test
    public void testGetEnergy() {
        Quantity expResult = Quantity.of(6.403, "keV");
        Quantity result = instance.getEnergy();
        assertEquals(result.getValue(), expResult.getValue(), 0.001,
                "Energy value should be 6.403");
        assertEquals(result.getUnit(), expResult.getUnit(),
                "Energy unit should be keV");
    }

    /**
     * Test of getIntensity method.
     */
    @Test
    public void testGetIntensity() {
        Quantity expResult = Quantity.scalar(1.0);
        Quantity result = instance.getIntensity();
        assertEquals(result.getValue(), expResult.getValue(), 0.001,
                "Intensity value should be 1.0");
        assertTrue(result.isScalar(), "Intensity should be scalar");
    }

    /**
     * Test of getOrigin method.
     */
    @Test
    public void testGetOrigin() {
        Transition result = instance.getOrigin();
        assertNull(result, "Origin should be null");
    }

    /**
     * Test with real data from the library.
     */
    @Test
    public void testWithRealData() {
        // Get a real xray from the library's iron data
        NISTXrayLibrary lib = NISTXrayLibrary.getInstance();
        XrayDataImpl ironData = (XrayDataImpl) lib.get(gov.llnl.rtk.physics.Elements.getElement("Fe"));

        XrayImpl xray = null;
        for (XrayEdgeImpl edge : ironData.edges) {
            if (edge.name.equals("K")) {
                for (XrayImpl line : edge.lines) {
                    if (line.symbolSiegbahn != null && line.symbolSiegbahn.equals("Ka1")) {
                        xray = line;
                        break;
                    }
                }
                if (xray != null) {
                    break;
                }
            }
        }

        assertNotNull(xray, "Ka1 line should exist for iron");
        assertEquals(xray.symbolIUPAC, "K-L3", "IUPAC symbol should be K-L3");
        assertEquals(xray.symbolSiegbahn, "Ka1", "Siegbahn symbol should be Ka1");

        Quantity energy = xray.getEnergy();
        assertNotNull(energy, "Energy should not be null");
        assertTrue(energy.getValue() > 0, "Energy should be positive");
        assertEquals(energy.getUnit(), "keV", "Energy unit should be keV");

        Quantity intensity = xray.getIntensity();
        assertNotNull(intensity, "Intensity should not be null");
        assertTrue(intensity.getValue() > 0, "Intensity should be positive");
        assertTrue(intensity.isScalar(), "Intensity should be scalar");
    }
}