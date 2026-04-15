/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.net.URI;
import java.net.URISyntaxException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for URIUtilities.
 */
public class URIUtilitiesNGTest
{

  public URIUtilitiesNGTest()
  {
  }

  @Test
  public void testConstructor()
  {
    URIUtilities instance = new URIUtilities();
  }

  /**
   * Test of resolve method, of class URIUtilities.
   */
  @Test(expectedExceptions =
  {
    RuntimeException.class
  })
  public void testResolve() throws URISyntaxException
  {
    URI uri = new URI("https://computing-int.llnl.gov/");
    URI expResult = new URI("https://computing-int.llnl.gov/about-our-organization");
    URI result = URIUtilities.resolve(uri, "about-our-organization");
    assertEquals(result, expResult);

    // jar scheme
    uri = new URI("jar:file:/../../jars/gov.llnl.utility.jar!/gov/llnl/utility/");
    expResult = new URI("jar:file:/../../jars/gov.llnl.utility.jar!/gov/llnl/utility/URIUtilities.class");
    result = URIUtilities.resolve(uri, "URIUtilities.class");
    assertEquals(result, expResult);

    // Test RuntimeException
    URIUtilities.resolve(new URI("jar:file:/it/runs/deep/share/it/with/me!"), "\\");
  }

  /**
   * Test of getFileName method, of class URIUtilities.
   */
  @Test(expectedExceptions =
  {
    RuntimeException.class
  })
  public void testGetFileName() throws URISyntaxException
  {
    URI uri = new URI("file:/src/public/gov/llnl/utility/URIUtilities.java");
    assertEquals(URIUtilities.getFileName(uri), "URIUtilities.java");

    // jar scheme
    uri = new URI("jar:file:/../../jars/gov.llnl.utility.jar!/gov/llnl/utility/URIUtilities.class");
    assertEquals(URIUtilities.getFileName(uri), "URIUtilities.class");

    // Test RuntimeException
    URIUtilities.getFileName(new URI("http://it/runs/deep/share/it/with/me!"));

  }

  /**
   * Test of exists method, of class URIUtilities.
   */
  @Test
  public void testExists() throws URISyntaxException
  {
    URI uri = new URI("file:./src/public/gov/llnl/utility/URIUtilities.java");
    assertEquals(URIUtilities.exists(uri), true);
    assertEquals(URIUtilities.exists(new URI("file:./src/happy/birthday")), false);
    assertEquals(URIUtilities.exists(new URI("jar:file:jar:file./rm/-rf")), false);
  }

}
