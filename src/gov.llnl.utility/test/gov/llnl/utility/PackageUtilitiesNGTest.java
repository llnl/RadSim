/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

/**
 * Test code for PackageUtilities.
 */
public class PackageUtilitiesNGTest
{
  PackageUtilities pu;

  public PackageUtilitiesNGTest()
  {
    pu = new PackageUtilities();
  }

  /**
   * Test of getClassURL method, of class PackageUtilities.
   */
  @Test
  public void testGetClassURL()
  {
    Class<?> klass = pu.getClass();
    URL expResult = pu.getClass().getClassLoader()
            .getResource("gov/llnl/utility/PackageUtilities.class");
    URL result = PackageUtilities.getClassURL(klass);
    assertEquals(result, expResult);
  }

  /**
   * Test of getJarURL method, of class PackageUtilities.
   */
  @Test
  public void testGetJarURL()
  {
    Class<?> klass = Test.class;
    URL result = PackageUtilities.getJarURL(klass);
    assertNotNull(result);
    assertEquals(result.getClass(), URL.class);
  }

  /**
   * Test of getJarMd5Checksum method, of class PackageUtilities.
   */
  @Test
  public void testGetJarMd5Checksum() throws Exception
  {
    Class<?> klass = pu.getClass();
    String result = PackageUtilities.getJarMd5Checksum(klass);
    assertNotNull(result);
    assertNotEquals(result, "none");
    assertEquals(result.length(), 32);
  }

  /**
   * Test of getManifestURL method, of class PackageUtilities.
   */
  @Test
  public void testGetManifestURL() throws IOException
  {
    Class<?> klass = pu.getClass();
    URL result = PackageUtilities.getManifestURL(klass);
    try (InputStream is = result.openStream())
    {
    }
  }

  /**
   * Test of getManifest method, of class PackageUtilities.
   */
  @Test
  public void testGetManifest()
  {
    Class<?> cl = pu.getClass();
    Manifest result = PackageUtilities.getManifest(cl);
    assertNotNull(result);
    assertEquals(result.getClass(), Manifest.class);
    assertNotNull(result.getMainAttributes().entrySet());
  }
}
