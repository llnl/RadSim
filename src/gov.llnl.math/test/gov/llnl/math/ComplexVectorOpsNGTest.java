/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ComplexVectorOps.
 */
public class ComplexVectorOpsNGTest
{

  public ComplexVectorOpsNGTest()
  {
  }

  /**
   * Test of multiply method of class ComplexVectorOps.
   */
  @Test
  public void testMultiply()
  {
    // Create two complex vectors for multiplication
    double[] real1 = {1.0, 2.0, 3.0};
    double[] imag1 = {4.0, 5.0, 6.0};
    double[] real2 = {7.0, 8.0, 9.0};
    double[] imag2 = {10.0, 11.0, 12.0};

    ComplexVector a = ComplexVector.create(real1, imag1);
    ComplexVector b = ComplexVector.create(real2, imag2);

    // Multiply them
    ComplexVector result = ComplexVectorOps.multiply(a, b);

    // Calculate expected results manually:
    // For complex numbers (a+bi) * (c+di) = (ac-bd) + (ad+bc)i
    double[] expectedReal = new double[3];
    double[] expectedImag = new double[3];
    for (int i = 0; i < 3; i++)
    {
      expectedReal[i] = real1[i] * real2[i] - imag1[i] * imag2[i];
      expectedImag[i] = real1[i] * imag2[i] + imag1[i] * real2[i];
    }

    // Verify results
    double[] resultReal = result.getReal();
    double[] resultImag = result.getImag();

    assertEquals(resultReal.length, expectedReal.length);
    assertEquals(resultImag.length, expectedImag.length);

    for (int i = 0; i < 3; i++)
    {
      assertEquals(resultReal[i], expectedReal[i], 1e-12);
      assertEquals(resultImag[i], expectedImag[i], 1e-12);
    }
  }

  /**
   * Test of multiplyAssign method of class ComplexVectorOps.
   */
  @Test
  public void testMultiplyAssign()
  {
    // Create a complex vector
    double[] real = {1.0, 2.0, 3.0};
    double[] imag = {4.0, 5.0, 6.0};
    double[] realo = {1.0, 2.0, 3.0};
    double[] imago = {4.0, 5.0, 6.0};
    ComplexVector a = ComplexVector.create(real, imag);

    // Scale by a factor
    double scale = 2.5;
    ComplexVector result = ComplexVectorOps.multiplyAssign(a, scale);

    // Verify that result is same object as input (in-place operation)
    assertSame(result, a);

    // Verify scaling
    double[] resultReal = result.getReal();
    double[] resultImag = result.getImag();

    for (int i = 0; i < 3; i++)
    {
      assertEquals(resultReal[i], realo[i] * scale, 1e-12);
      assertEquals(resultImag[i], imago[i] * scale, 1e-12);
    }
  }

  /**
   * Test of divideAssign method of class ComplexVectorOps.
   */
  @Test
  public void testDivideAssign() throws Exception
  {
    // Create a ComplexVectorImpl instance using reflection
    double[] real = {1.0, 2.0, 3.0};
    double[] imag = {4.0, 5.0, 6.0};
    double[] realo = {1.0, 2.0, 3.0};
    double[] imago = {4.0, 5.0, 6.0};
    ComplexVector a = ComplexVector.create(real, imag);
    
    // Divide by a scalar
    double divisor = 2.0;
    ComplexVector result = ComplexVectorOps.divideAssign(a, divisor);

    // Verify that result is same object as input (in-place operation)
    assertSame(result, a);

    // Verify division
    double[] resultReal = result.getReal();
    double[] resultImag = result.getImag();

    for (int i = 0; i < 3; i++)
    {
      assertEquals(resultReal[i], realo[i] / divisor, 1e-12);
      assertEquals(resultImag[i], imago[i] / divisor, 1e-12);
    }
  }

  /**
   * Test of copyOfRange method of class ComplexVectorOps.
   */
  @Test
  public void testCopyOfRange()
  {
    // Create a complex vector
    double[] real = {1.0, 2.0, 3.0, 4.0, 5.0};
    double[] imag = {6.0, 7.0, 8.0, 9.0, 10.0};
    ComplexVector a = ComplexVector.create(real, imag);

    // Copy a range
    int start = 1;
    int end = 4;
    ComplexVector result = ComplexVectorOps.copyOfRange(a, start, end);

    // Verify size
    assertEquals(result.size(), end - start);

    // Verify contents
    double[] resultReal = result.getReal();
    double[] resultImag = result.getImag();

    for (int i = 0; i < end - start; i++)
    {
      assertEquals(resultReal[i], real[i + start], 1e-12);
      assertEquals(resultImag[i], imag[i + start], 1e-12);
    }
  }

  /**
   * Test of addAssign method of class ComplexVectorOps.
   */
  @Test
  public void testAddAssign() throws Exception
  {
    // Create two complex vectors
    double[] real1 = {1.0, 2.0, 3.0};
    double[] imag1 = {4.0, 5.0, 6.0};
    double[] real1o = {1.0, 2.0, 3.0};
    double[] imag1o = {4.0, 5.0, 6.0};
    double[] real2 = {7.0, 8.0, 9.0};
    double[] imag2 = {10.0, 11.0, 12.0};

    // Create ComplexVectorImpl instance for the first vector using reflection
    ComplexVector a = ComplexVector.create(real1, imag1);
    ComplexVector b = ComplexVector.create(real2, imag2);

    // Add b to a in-place
    ComplexVector result = ComplexVectorOps.addAssign(a, b);

    // Verify that result is same object as a (in-place operation)
    assertSame(result, a);

    // Verify addition
    double[] resultReal = result.getReal();
    double[] resultImag = result.getImag();

    for (int i = 0; i < 3; i++)
    {
      assertEquals(resultReal[i], real1o[i] + real2[i], 1e-12);
      assertEquals(resultImag[i], imag1o[i] + imag2[i], 1e-12);
    }
  }

  /**
   * Test of addAssignScaled method of class ComplexVectorOps.
   */
  @Test
  public void testAddAssignScaled() throws Exception
  {
    // Create two complex vectors
    double[] real1 = {1.0, 2.0, 3.0};
    double[] real1o = {1.0, 2.0, 3.0};
    double[] imag1 = {4.0, 5.0, 6.0};
    double[] imag1o = {4.0, 5.0, 6.0};
    double[] real2 = {7.0, 8.0, 9.0};
    double[] imag2 = {10.0, 11.0, 12.0};

    ComplexVector a = ComplexVector.create(real1, imag1);
    ComplexVector b = ComplexVector.create(real2, imag2);

    // Add scaled b to a in-place
    double scale = 0.5;
    ComplexVector result = ComplexVectorOps.addAssignScaled(a, b, scale);

    // Verify that result is same object as a (in-place operation)
    assertSame(result, a);

    // Verify scaled addition
    double[] resultReal = result.getReal();
    double[] resultImag = result.getImag();

    for (int i = 0; i < 3; i++)
    {
      assertEquals(resultReal[i], real1o[i] + real2[i] * scale, 1e-12);
      assertEquals(resultImag[i], imag1o[i] + imag2[i] * scale, 1e-12);
    }
  }
}