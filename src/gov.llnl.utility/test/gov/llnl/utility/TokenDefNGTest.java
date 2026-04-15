/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.regex.Pattern;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 * Test code for TokenDef.
 */
public class TokenDefNGTest
{

  public TokenDefNGTest()
  {
  }

  @Test
  public void testClass()
  {
    TokenDef instance = new TokenDef("Hello");
    assertTrue(instance.pattern.matcher("Hello").matches());
  }

  @Test
  public void testEquals()
  {
    TokenDef instance_1 = new TokenDef("Hello");
    TokenDef instance_2 = new TokenDef("Hello");
    TokenDef instance_3 = new TokenDef("World");
    Object o = "";

    assertEquals(instance_1.equals(instance_2), true);
    assertEquals(instance_1.equals(o), false);
    assertEquals(instance_1.equals(instance_3), false);
    instance_2.pattern = Pattern.compile("");
    assertEquals(instance_1.equals(instance_2), false);
  }

}
