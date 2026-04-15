/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for PathUtilities.
 */
public class PathUtilitiesNGTest
{

  public PathUtilitiesNGTest()
  {
  }

  /**
   * Test of joinStrings method, of class PathUtilities.
   */
  @Test
  public void testJoinStrings()
  {
    Collection<Path> collection = new ArrayList<>();
    collection.add(Paths.get("test1.txt"));
    collection.add(Paths.get("test2.txt"));
    collection.add(Paths.get("test3.txt"));
    String er = Arrays.toString(new String[]
    {
      "test1.txt", "test2.txt", "test3.txt"
    });
    String expResult = er.substring(1, er.length() - 1);
    String result = PathUtilities.joinStrings(collection);
    assertEquals(result, expResult);
  }

  /**
   * Test of getComponents method, of class PathUtilities.
   */
  @Test
  public void testGetComponents()
  {
    Path p = Paths.get("/a/b/c/test.txt");
    List expResult = Arrays.asList("a", "b", "c", "test.txt");
    List result = PathUtilities.getComponents(p);
    assertEquals(result, expResult);
  }

  /**
   * Test of resolve method, of class PathUtilities.
   */
  @Test
  public void testResolve() throws Exception
  {
    Path file = Paths.get("gov");
    Path expResult = Paths.get("test/gov");
    Path result = PathUtilities.resolve(file, Paths.get("build"), Paths.get("src"), Paths.get("test"));
    assertEquals(result, expResult);
  }

  /**
   * Test of isGzip method, of class PathUtilities.
   */
  @Test
  public void testIsGzip() throws Exception
  {
    Path file = Paths.get("test/gzipped");
    boolean expResult = true;
    boolean result = PathUtilities.isGzip(file);
    assertEquals(result, expResult);
  }

  /**
   * Test of getFileExtensionPosition method, of class PathUtilities.
   */
  @Test
  public void testGetFileExtensionPosition()
  {
    Path path = Paths.get("test.myname.tar.gz");
    int expResult = 11;
    int result = PathUtilities.getFileExtensionPosition(path);
    assertEquals(result, expResult);
  }

  /**
   * Test of getFileExtension method, of class PathUtilities.
   */
  @Test
  public void testGetFileExtension1()
  {
    Path path = Paths.get("test.txt");
    String expResult = "txt";
    String result = PathUtilities.getFileExtension(path);
    assertEquals(result, expResult);
  }

  /**
   * Test of getFileExtension method, of class PathUtilities.
   */
  @Test
  public void testGetFileExtension2()
  {
    Path path = Paths.get("test");
    String expResult = "";
    String result = PathUtilities.getFileExtension(path);
    assertEquals(result, expResult);
  }

  /**
   * Test of changeExtension method, of class PathUtilities.
   */
  @Test
  public void testChangeExtension()
  {
    Path path = Paths.get("test.ez");
    String ext = "txt";
    Path expResult = Paths.get("test.txt");
    Path result = PathUtilities.changeExtension(path, ext);
    assertEquals(result, expResult);
  }

  /**
   * Test of fileBasename method, of class PathUtilities.
   */
  @Test
  public void testFileBasename()
  {
    Path path = Paths.get("test.txt");;
    String expResult = "test";
    String result = PathUtilities.fileBasename(path);
    assertEquals(result, expResult);
  }

  /**
   * Test of md5Checksum method, of class PathUtilities.
   */
  @Test
  public void testMd5Checksum() throws Exception
  {
    Path file = Paths.get("test/md5sum.xml");
    String expResult = "861f96bc3c2955bb13759691d5b2ec4e";
    String result = PathUtilities.md5Checksum(file);
    assertEquals(result, expResult);
  }

  /**
   * Test of newPath method, of class PathUtilities.
   */
  @Test
  public void testNewPath_String()
  {
    String path = "test.txt";
    PathUtilities instance = new PathUtilities();
    Path expResult = Paths.get(path);
    Path result = instance.newPath(path);
    assertEquals(result, expResult);
  }

  /**
   * Test of newPath method, of class PathUtilities.
   */
  @Test
  public void testNewPath_String_String()
  {
    String directory = "test.dir";
    String filename = "test.txt";
    PathUtilities instance = new PathUtilities();
    Path expResult = Paths.get(directory, filename);
    Path result = instance.newPath(directory, filename);
    assertEquals(result, expResult);
  }

  /**
   * Test of chdir method, of class PathUtilities.
   */
  @Test
  public void testChdir()
  {
    String dir = "test.dir";
    PathUtilities.chdir(dir);
    assertEquals(Paths.get(".").toFile().getAbsolutePath()
            .equals(Paths.get(".").toAbsolutePath().toFile().toString()), true);
  }

  /**
   * Test of findPaths method, of class PathUtilities.
   */
  @Test
  public void testFindPaths_Path_String() throws Exception
  {
    Path directory = Paths.get("src");
    assertEquals(PathUtilities.findPaths(directory, "**/public"), Arrays.asList(Paths.get("src/public")));
    assertEquals(PathUtilities.findPaths(directory, "**/built"), Arrays.asList());
  }

  /**
   * Test of findPaths method, of class PathUtilities.
   */
  @Test
  public void testFindPaths_3args() throws Exception
  {
    Path directory = Paths.get("src");
    String pattern = "**/gov";
    boolean recursive = true;
    List<String> expResult = Arrays.asList(Paths.get("src/private/gov").toString(), Paths.get("src/public/gov").toString());
    Collections.sort(expResult);
    List<String> result = new ArrayList<>();
    for (Path p : PathUtilities.findPaths(directory, pattern, recursive))
    {
      result.add(p.toString());
    }
    Collections.sort(result);
    assertEquals(result, expResult);
  }

  /**
   * Test of findFileRecursive method, of class PathUtilities.
   */
  @Test
  public void testFindFileRecursive() throws Exception
  {
    Path directory = Paths.get(".");
    Path filename = Paths.get("ivy");
    Path expResult = Paths.get("./build/ivy");
    Path result = PathUtilities.findFileRecursive(directory, filename);
    assertEquals(result, expResult);
  }

}
