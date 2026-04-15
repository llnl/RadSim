/*
 * Copyright 2024, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the XrayParser class.
 */
public class XrayParserTest {

    private XrayParser instance;
    private NISTXrayLibrary testLibrary;

    public XrayParserTest() {
    }

    @BeforeMethod
    public void setUp() {
        instance = new XrayParser();
        testLibrary = new NISTXrayLibrary();
    }

    /**
     * Test basic parsing functionality with a sample element.
     */
    @Test
    public void testBasicParsing() throws IOException {
        String sampleData = "Element  Fe  26  55.845  7.874\n"
                + "  Edge  K  7.112  0.34  8.0\n"
                + "    Lines  K-L3  Ka1  6.403  1.0\n"
                + "    Lines  K-L2  Ka2  6.39  0.5\n"
                + "  Edge  L1  0.846  0.05  2.0\n"
                + "    Lines  L1-M3  Lb3  0.702  0.3\n"
                + "  Scatter  1  2  3  4\n"
                + "    1  2  3  4\n"
                + "    5  6  7  8\n"
                + "  Photo  9  10  11  12\n"
                + "    13  14  15  16\n"
                + "EndElement\n";

        BufferedReader br = new BufferedReader(new StringReader(sampleData));
        instance.parse(testLibrary, br);

        // Check that the library contains the element
        assertEquals(testLibrary.bySymbol.size(), 1, "Library should contain 1 element");
        assertEquals(testLibrary.byNumber.size(), 1, "Library should contain 1 element");

        assertTrue(testLibrary.bySymbol.containsKey("Fe"), "Library should contain Fe");
        assertTrue(testLibrary.byNumber.containsKey(26), "Library should contain element with Z=26");

        // Get the parsed element
        XrayDataImpl iron = testLibrary.bySymbol.get("Fe");
        assertEquals(iron.name, "Fe", "Element name should be Fe");
        assertEquals(iron.atomic_number, 26, "Atomic number should be 26");
        assertEquals(iron.atomic_weigth, 55.845, 0.001, "Atomic weight should be correct");
        assertEquals(iron.density, 7.874, 0.001, "Density should be correct");

        // Check edges
        assertEquals(iron.edges.size(), 2, "Should have 2 edges");
        XrayEdgeImpl kEdge = iron.edges.get(0);
        assertEquals(kEdge.name, "K", "First edge should be K");
        assertEquals(kEdge.energy, 7.112, 0.001, "K edge energy should be correct");
        assertEquals(kEdge.fluorescence_yield, 0.34, 0.001, "K edge fluorescence yield should be correct");
        assertEquals(kEdge.ratio_jump, 8.0, 0.001, "K edge ratio jump should be correct");

        // Check lines
        assertEquals(kEdge.lines.size(), 2, "K edge should have 2 lines");
        XrayImpl ka1 = kEdge.lines.get(0);
        assertEquals(ka1.symbolIUPAC, "K-L3", "First line should have IUPAC symbol K-L3");
        assertEquals(ka1.symbolSiegbahn, "Ka1", "First line should have Siegbahn symbol Ka1");
        assertEquals(ka1.energy.getValue(), 6.403, 0.001, "First line energy should be correct");
        assertEquals(ka1.intensity.getValue(), 1.0, 0.001, "First line intensity should be correct");

        // Check scatter and photo data
        assertNotNull(iron.scatter, "Scatter data should be present");
        assertEquals(iron.scatter.length, 2, "Scatter data should have 2 rows");
        assertEquals(iron.scatter[0].length, 4, "Scatter data should have 4 columns");
        assertEquals(iron.scatter[0][0], 1.0, 0.001, "First scatter value should be correct");

        assertNotNull(iron.photo, "Photo data should be present");
        assertEquals(iron.photo.length, 1, "Photo data should have 1 row");
        assertEquals(iron.photo[0].length, 4, "Photo data should have 4 columns");
        assertEquals(iron.photo[0][0], 13.0, 0.001, "First photo value should be correct");
    }

    /**
     * Test parsing with comments.
     */
    @Test
    public void testParsingWithComments() throws IOException {
        String sampleData = "// This is a comment\n"
                + "Element  H  1  1.008  0.0708\n"
                + "// Another comment\n"
                + "  Edge  K  0.0136  0.0  0.0\n"
                + "EndElement\n";

        BufferedReader br = new BufferedReader(new StringReader(sampleData));
        instance.parse(testLibrary, br);

        // Check that the library contains the element
        assertEquals(testLibrary.bySymbol.size(), 1, "Library should contain 1 element");
        assertTrue(testLibrary.bySymbol.containsKey("H"), "Library should contain H");
    }

    /**
     * Test parsing with Coster-Kronig transitions.
     */
    @Test
    public void testParsingWithCosterKronig() throws IOException {
        String sampleData = "Element  Fe  26  55.845  7.874\n"
                + "  Edge  L1  0.846  0.05  2.0\n"
                + "    CK  L1-L3 0.23  L1-L2 0.17\n"
                + "    CKtotal 0.4\n"
                + "EndElement\n";

        BufferedReader br = new BufferedReader(new StringReader(sampleData));
        instance.parse(testLibrary, br);

        // Get the parsed element
        XrayDataImpl iron = testLibrary.bySymbol.get("Fe");
        XrayEdgeImpl l1Edge = iron.edges.get(0);

        // Check Coster-Kronig transitions
        assertEquals(l1Edge.CK.size(), 2, "Should have 2 Coster-Kronig transitions");
        assertTrue(l1Edge.CK.containsKey("L1-L3"), "Should contain L1-L3 transition");
        assertEquals(l1Edge.CK.get("L1-L3"), 0.23, 0.001, "L1-L3 transition probability should be correct");
        assertTrue(l1Edge.CK.containsKey("L1-L2"), "Should contain L1-L2 transition");
        assertEquals(l1Edge.CK.get("L1-L2"), 0.17, 0.001, "L1-L2 transition probability should be correct");
    }

    /**
     * Test parsing invalid data should throw exception.
     */
    @Test(expectedExceptions = RuntimeException.class)
    public void testParsingInvalidData() throws IOException {
        String sampleData = "Element  Fe  26  55.845  7.874\n"
                + "  InvalidTag  K  7.112  0.34  8.0\n"
                + "EndElement\n";

        BufferedReader br = new BufferedReader(new StringReader(sampleData));
        instance.parse(testLibrary, br); // Should throw RuntimeException
    }
}