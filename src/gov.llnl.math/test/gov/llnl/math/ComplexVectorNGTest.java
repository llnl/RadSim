/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import gov.llnl.math.MathExceptions.SizeException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ComplexVector.
 */
public class ComplexVectorNGTest
{

  public ComplexVectorNGTest()
  {
  }

  /**
   * Test of getAbs method, of class ComplexVector.
   */
  @Test
  public void testGetAbs()
  {
    // Create a complex vector with real and imaginary parts
    double[] real = {3.0, 0.0, 4.0, 12.0};
    double[] imag = {4.0, 2.0, 3.0, 5.0};
    ComplexVector instance = ComplexVector.create(real, imag);

    // Calculate expected absolute values
    double[] expResult = {5.0, 2.0, 5.0, 13.0};

    // Get actual absolute values
    double[] result = instance.getAbs();

    // Verify results
    assertEquals(result.length, expResult.length);
    for (int i = 0; i < result.length; i++)
    {
      assertEquals(result[i], expResult[i], 1e-12);
    }
  }

  /**
   * Test of getImag method, of class ComplexVector.
   */
  @Test
  public void testGetImag()
  {
    // Create a complex vector with real and imaginary parts
    double[] real = {1.0, 2.0, 3.0};
    double[] imag = {4.0, 5.0, 6.0};
    ComplexVector instance = ComplexVector.create(real, imag);

    // Get imaginary part
    double[] result = instance.getImag();

    // Verify results
    assertEquals(result.length, imag.length);
    for (int i = 0; i < result.length; i++)
    {
      assertEquals(result[i], imag[i], 1e-12);
    }
  }

  /**
   * Test of getReal method, of class ComplexVector.
   */
  @Test
  public void testGetReal()
  {
    // Create a complex vector with real and imaginary parts
    double[] real = {1.0, 2.0, 3.0};
    double[] imag = {4.0, 5.0, 6.0};
    ComplexVector instance = ComplexVector.create(real, imag);

    // Get real part
    double[] result = instance.getReal();

    // Verify results
    assertEquals(result.length, real.length);
    for (int i = 0; i < result.length; i++)
    {
      assertEquals(result[i], real[i], 1e-12);
    }
  }

  /**
   * Test of size method, of class ComplexVector.
   */
  @Test
  public void testSize()
  {
    // Create a complex vector with real and imaginary parts
    double[] real = {1.0, 2.0, 3.0, 4.0, 5.0};
    double[] imag = {6.0, 7.0, 8.0, 9.0, 10.0};
    ComplexVector instance = ComplexVector.create(real, imag);

    // Check size
    int expResult = 5;
    int result = instance.size();
    assertEquals(result, expResult);
  }

  /**
   * Test of create method with valid inputs.
   */
  @Test
  public void testCreate_ValidInputs()
  {
    // Test create with both real and imaginary parts
    double[] real = {1.0, 2.0, 3.0};
    double[] imag = {4.0, 5.0, 6.0};
    ComplexVector result = ComplexVector.create(real, imag);
    assertNotNull(result);
    assertEquals(result.size(), 3);

    // Test create with only real part (imaginary part should be zeros)
    ComplexVector result2 = ComplexVector.create(real, null);
    assertNotNull(result2);
    assertEquals(result2.size(), 3);
    double[] imagPart = result2.getImag();
    for (int i = 0; i < imagPart.length; i++)
    {
      assertEquals(imagPart[i], 0.0, 1e-12);
    }

    // Test create with only imaginary part (real part should be zeros)
    ComplexVector result3 = ComplexVector.create(null, imag);
    assertNotNull(result3);
    assertEquals(result3.size(), 3);
    double[] realPart = result3.getReal();
    for (int i = 0; i < realPart.length; i++)
    {
      assertEquals(realPart[i], 0.0, 1e-12);
    }
  }

  /**
   * Test of create method with null inputs.
   */
  @Test(expectedExceptions = NullPointerException.class)
  public void testCreate_NullInputs()
  {
    // Should throw NullPointerException when both inputs are null
    ComplexVector.create(null, null);
  }

  /**
   * Test of create method with mismatched sizes.
   */
  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testCreate_MismatchedSizes()
  {
    // Should throw SizeException when sizes don't match
    double[] real = {1.0, 2.0, 3.0};
    double[] imag = {4.0, 5.0, 6.0, 7.0}; // Different length
    ComplexVector.create(real, imag);
  }
}