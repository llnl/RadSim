/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.UUID;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for UUIDUtilities.
 */
public class UUIDUtilitiesNGTest
{

  public UUIDUtilitiesNGTest()
  {
  }

  @Test
  public void testConstructor()
  {
    UUIDUtilities instance = new UUIDUtilities();
  }

  /**
   * Test of createUUID method, of class UUIDUtilities.
   */
  @Test
  public void testCreateUUID()
  {
    assertEquals(UUIDUtilities.createUUID("ToInfinityAndBeyond"), UUID.fromString("aae59d09-127e-a8e9-2862-ddbf9f2d6cde"));
  }

  /**
   * Test of createLong method, of class UUIDUtilities.
   */
  @Test
  public void testCreateLong()
  {
    assertEquals(UUIDUtilities.createLong("ToInfinityAndBeyond"), -9041186574123678665L);
  }

}
