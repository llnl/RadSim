/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.FileNotFoundException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for Try.
 */
public class TryNGTest
{

  public TryNGTest()
  {
  }

  Object obj;

  public void call(Object i) throws FileNotFoundException
  {
    obj = i;
  }

  /**
   * Test of promote method, of class Try.
   */
  @Test
  public void testPromote1()
  {
    BiConsumer<TryNGTest, Object> result = Try.promote(TryNGTest::call);
    result.accept(this, 1);
    assertEquals(this.obj, 1);
  }

  /**
   * Test of promote method, of class Try.
   */
  @Test
  public void testPromote2()
  {
    Consumer<Object> result = Try.promote(this::call);
    result.accept(2);
    assertEquals(this.obj, 2);
  }

}
