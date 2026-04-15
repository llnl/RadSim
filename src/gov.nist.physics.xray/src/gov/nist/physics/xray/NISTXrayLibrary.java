/*
 * Copyright 2024, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xray;

import gov.llnl.rtk.physics.Element;
import gov.llnl.rtk.physics.XrayData;
import gov.llnl.rtk.physics.XrayLibrary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * <p>
 * This library uses the <b>Elam, Ravel, Sieber (ElamDB12)</b> database as its
 * default "NIST X-ray" library for atomic line energies, emission rates, and
 * related X-ray atomic data.
 * </p>
 *
 * <h2>Overview</h2>
 * <ul>
 * <li>Comprehensive: Covers elements Z=1–98 and a wide range of X-ray
 * transitions (energies, rates, yields).</li>
 * <li>Consistent: Provides a harmonized dataset, reducing risk of
 * inconsistencies when combining sources.</li>
 * <li>Parseable: Distributed in a machine-readable format, practical for
 * programmatic use.</li>
 * <li>Community Standard: Widely adopted in XRF and X-ray analysis software as
 * the de facto "NIST X-ray" data source.</li>
 * </ul>
 *
 * <h2>Nature of the Data</h2>
 * <ul>
 * <li>ElamDB12 is a <b>curated compilation</b>, not a primary measurement
 * database.</li>
 * <li>Aggregates data from:
 * <ul>
 * <li>Experimental measurements</li>
 * <li>Theoretical calculations</li>
 * <li>Prior compilations (e.g., Bearden &amp; Burr, Krause, Bambynek)
 * </li>
 * </ul>
 * </li>
 * <li>Authors exercised expert judgment in harmonizing values.</li>
 * </ul>
 *
 * <h2>Provenance and Citation</h2>
 * <p>
 * If you use this data in published work, please cite:
 * </p>
 * <blockquote>
 * Elam, W. T., Ravel, B., &amp; Sieber, J. R. (2002).<br>
 * A new atomic database for X-ray spectroscopic calculations.<br>
 * <i>Radiation Physics and Chemistry</i>, 63(2), 121–128.<br>
 * <a href="https://doi.org/10.1016/S0969-806X(01)00227-4">
 * https://doi.org/10.1016/S0969-806X(01)00227-4</a>
 * </blockquote>
 *
 * <h2>Limitations</h2>
 * <ul>
 * <li>Not primary data: For traceability to direct measurements or ab initio
 * calculations, consult the original sources referenced in ElamDB12.</li>
 * <li>Updates: Database reflects the state of knowledge as of 2001. For the
 * most current values, check for updates or newer compilations.</li>
 * </ul>
 *
 * <h2>Alternatives</h2>
 * <ul>
 * <li>NIST XCOM: For photon cross sections.</li>
 * <li>NIST Compton Profile Database: For electron momentum profiles.</li>
 * <li>EADL, Bearden &amp; Burr, Krause: For specific atomic parameters (may
 * require additional parsing or curation).</li>
 * </ul>
 *
 * <h2>Implementation Note</h2>
 * <p>
 * The library is accessed via the <code>nist.xray</code> interface in this
 * codebase. If you wish to substitute a different database, ensure it conforms
 * to the same API and data structure.
 * </p>
 *
 * <h2>Summary Table</h2>
 * <table border="1">
 * <caption>Summary of Supported Libraries</caption>
 * <tr>
 * <th>Library/API</th>
 * <th>Backing Data Source</th>
 * <th>Contents</th>
 * <th>Notes</th>
 * </tr>
 * <tr>
 * <td>nist.xray</td>
 * <td>ElamDB12 (Elam et al)</td>
 * <td>X-ray lines, yields</td>
 * <td>Comprehensive, curated</td>
 * </tr>
 * <tr>
 * <td>nist.xcom</td>
 * <td>NIST XCOM</td>
 * <td>Photon cross sections</td>
 * <td>Evaluated, ab initio</td>
 * </tr>
 * <tr>
 * <td>nist.compton</td>
 * <td>NIST Compton Profile</td>
 * <td>Electron profiles</td>
 * <td>For Doppler broadening</td>
 * </tr>
 * </table>
 *
 * @author nelson85
 */
public class NISTXrayLibrary implements XrayLibrary
{

  public HashMap<String, XrayDataImpl> bySymbol = new HashMap<>();
  public HashMap<Integer, XrayDataImpl> byNumber = new HashMap<>();

  static NISTXrayLibrary INSTANCE;

  public static NISTXrayLibrary getInstance()
  {
    if (INSTANCE == null)
    {
      INSTANCE = new NISTXrayLibrary();
      try (InputStream is = XrayParser.class.getClassLoader().getResourceAsStream("gov/nist/physics/xray/resources/ElamDB12.txt"))
      {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        XrayParser parser = new XrayParser();
        parser.parse(INSTANCE, reader);
      }
      catch (IOException ex)
      {
        throw new RuntimeException(ex);
      }
    }
    return INSTANCE;
  }

  void add(XrayDataImpl element)
  {
    bySymbol.put(element.name, element);
    byNumber.put(element.atomic_number, element);
  }

  @Override
  public XrayData get(Element element)
  {
    // not my problem if they request bad data.
    if (element == null)
    {
      return null;
    }

    // Get the data which is indexed by atomic number.
    return this.byNumber.get(element.getAtomicNumber());
  }

}
