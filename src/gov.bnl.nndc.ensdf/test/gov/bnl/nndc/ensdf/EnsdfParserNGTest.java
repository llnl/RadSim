package gov.bnl.nndc.ensdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * TestNG test class for EnsdfParser.
 */
public class EnsdfParserNGTest {

    public EnsdfParserNGTest() {
    }

    /**
     * Test parsing a simple ENSDF record.
     */
    @Test
    public void testParseSimpleRecord() throws IOException {
        String testData = "137CS    ADOPTED LEVELS, GAMMAS                                  19990717   \n" +
                         "137CS  H TYP=FUL$AUT=AGDA ARTNA-COHEN$CIT=NDS 88, 155 (1999)$    \n" +
                         "137CS2 H CUT=1-JUL-1999$                                          \n" +
                         "137CS  Q 1175.63   15 4155.57   17 6340.9    4              1997ZI04\n" +
                         "137CS  L 0.0         7/2+              30.08 Y    5              \n" +
                         "137CSX L XREF=ABCDEF                                             \n" +
                         "137CS  L 455.37     3 11/2-             8.7 PS    4              \n" +
                         "137CS  G 455.37     3 100      [M2]                              \n";

        InputStream stream = new ByteArrayInputStream(testData.getBytes(StandardCharsets.UTF_8));
        EnsdfParser parser = new EnsdfParser();
        EnsdfDataSet dataSet = parser.parse(stream);

        // Verify basic properties of the parsed data
        assertNotNull(dataSet);

        // Check identification record
        EnsdfIdentification id = dataSet.getIdentification();
        assertNotNull(id);
        assertEquals("137CS", id.getSymbol());
        assertEquals("ADOPTED LEVELS, GAMMAS", id.getDatasetType());

        // Check Q values
        EnsdfQValue qValue = dataSet.getQValue();
        assertNotNull(qValue);
        assertEquals(1175.63, qValue.getBetaMinus().getValue(), 0.01);
        assertEquals(0.15, qValue.getBetaMinus().getUncertainty(), 0.01);
        assertEquals(4155.57, qValue.getBetaPlus().getValue(), 0.01);

        // Check levels
        boolean foundGroundState = false;
        boolean foundExcitedState = false;

        for (EnsdfLevel level : dataSet.getLevels()) {
            if (Math.abs(level.getEnergy().getValue()) < 0.001) { // Ground state
                foundGroundState = true;
                assertEquals("7/2+", level.getJpi());
                assertEquals(30.08, level.getHalfLife().getValue(), 0.01);
                assertEquals("Y", level.getHalfLife().getUnit());
            }

            if (Math.abs(level.getEnergy().getValue() - 455.37) < 0.01) { // Excited state
                foundExcitedState = true;
                assertEquals("11/2-", level.getJpi());
                assertEquals(8.7, level.getHalfLife().getValue(), 0.01);
                assertEquals("PS", level.getHalfLife().getUnit());

                // Check gamma from this level
                boolean foundGamma = false;
                for (EnsdfGamma gamma : dataSet.getGammas(level)) {
                    if (Math.abs(gamma.getEnergy().getValue() - 455.37) < 0.01) {
                        foundGamma = true;
                        assertEquals(100.0, gamma.getIntensity().getValue(), 0.01);
                        assertEquals("[M2]", gamma.getMultipolarity());
                    }
                }
                assertTrue(foundGamma, "Expected gamma not found");
            }
        }

        assertTrue(foundGroundState, "Ground state level not found");
        assertTrue(foundExcitedState, "Excited state level not found");
    }

    /**
     * Test handling of malformed input.
     */
    @Test
    public void testMalformedInput() throws IOException {
        String malformedData = "Invalid ENSDF format";
        InputStream stream = new ByteArrayInputStream(malformedData.getBytes(StandardCharsets.UTF_8));
        EnsdfParser parser = new EnsdfParser();

        try {
            EnsdfDataSet dataSet = parser.parse(stream);
            // Should not throw an exception but might return empty dataset
            assertNotNull(dataSet);
        } catch (Exception e) {
            // The parser might throw an exception for invalid input
            // This is also acceptable behavior
            assertTrue(e instanceof RuntimeException || e instanceof IOException,
                    "Expected RuntimeException or IOException, got " + e.getClass().getName());
        }
    }

    /**
     * Test parsing multiple datasets.
     */
    @Test
    public void testMultipleDatasets() throws IOException {
        String testData =
                "137CS    ADOPTED LEVELS, GAMMAS                                  19990717   \n" +
                "137CS  L 0.0         7/2+              30.08 Y    5              \n" +
                "137CS  L 455.37     3 11/2-             8.7 PS    4              \n" +
                "137BA    ADOPTED LEVELS, GAMMAS                                  20050101   \n" +
                "137BA  L 0.0         3/2+              STABLE                    \n" +
                "137BA  L 661.659    3 11/2-             2.552 M   1              \n";

        InputStream stream = new ByteArrayInputStream(testData.getBytes(StandardCharsets.UTF_8));
        EnsdfParser parser = new EnsdfParser();
        EnsdfDataSet dataSet = parser.parse(stream);

        // Check that both Cs and Ba data are present
        boolean foundCs = false;
        boolean foundBa = false;

        // Check if both isotopes are present
        for (EnsdfLevel level : dataSet.getLevels()) {
            if (level.getSymbol().equals("137CS")) {
                foundCs = true;
            } else if (level.getSymbol().equals("137BA")) {
                foundBa = true;
            }
        }

        assertTrue(foundCs, "Cs-137 data not found");
        assertTrue(foundBa, "Ba-137 data not found");
    }
}