/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import support.MatrixTestGenerator;
import gov.llnl.math.MathExceptions.MathException;
import gov.llnl.math.MathExceptions.ResizeException;
import gov.llnl.math.MathExceptions.SizeException;
import org.testng.annotations.Test;

/**
 * Test code for MatrixAssert.
 */
public class MatrixAssertNGTest
{
  MatrixTestGenerator tg = new MatrixTestGenerator();

  public MatrixAssertNGTest()
  {
  }
  
  /**
   * Test constructor of class MatrixAssert.
   */
  @Test
  public void testConstructor()
  {
    MatrixAssert instance = new MatrixAssert();
  }

  /**
   * Test of assertSquare method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertSquare() throws Exception
  {
    MatrixAssert.assertSquare(MatrixFactory.newColumnMatrix(2, 2));
  }

  /**
   * Test of assertSquare method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test(expectedExceptions = SizeException.class)
  public void testAssertSquare_SizeException() throws Exception
  {
    MatrixAssert.assertSquare(MatrixFactory.newColumnMatrix(2, 3));
  }

  /**
   * Test of assertSizeEquals method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertSizeEquals() throws Exception
  {
    MatrixAssert.assertSizeEquals(MatrixFactory.newColumnMatrix(2, 3), 6);
  }

  /**
   * Test of assertSizeEquals method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test(expectedExceptions = SizeException.class)
  public void testAssertSizeEquals_SizeException() throws Exception
  {
    MatrixAssert.assertSizeEquals(MatrixFactory.newColumnMatrix(2, 3), 9);
  }

  /**
   * Test of assertEqualSize method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertEqualSize() throws Exception
  {
    MatrixAssert.assertEqualSize(MatrixFactory.newColumnMatrix(2, 3),
            MatrixFactory.newColumnMatrix(2, 3));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAssertEqualSize_SizeException() throws Exception
  {
    MatrixAssert.assertEqualSize(MatrixFactory.newColumnMatrix(2, 3),
            MatrixFactory.newColumnMatrix(4, 1));
    MatrixAssert.assertEqualSize(MatrixFactory.newColumnMatrix(2, 3),
            MatrixFactory.newColumnMatrix(2, 1));
    MatrixAssert.assertEqualSize(MatrixFactory.newColumnMatrix(2, 3),
            MatrixFactory.newColumnMatrix(1, 3));
  }

  /**
   * Test of assertColumnsEqualsRows method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertColumnsEqualsRows() throws Exception
  {
    MatrixAssert.assertColumnsEqualsRows(MatrixFactory.newColumnMatrix(2, 3),
            MatrixFactory.newColumnMatrix(3, 2));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAssertColumnsEqualsRows_SizeException() throws Exception
  {
    MatrixAssert.assertColumnsEqualsRows(MatrixFactory.newColumnMatrix(2, 3),
            MatrixFactory.newColumnMatrix(1, 4));
  }

  /**
   * Test of assertRowsEqual method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertRowsEqual_Matrix_Matrix() throws Exception
  {
    MatrixAssert.assertRowsEqual(MatrixFactory.newColumnMatrix(4, 2),
            MatrixFactory.newColumnMatrix(4, 1));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAssertRowsEqual_Matrix_Matrix_SizeException() throws Exception
  {
    MatrixAssert.assertRowsEqual(MatrixFactory.newColumnMatrix(4, 2),
            MatrixFactory.newColumnMatrix(3, 1));
  }

  /**
   * Test of assertRowsEqual method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertRowsEqual_Matrix_int() throws Exception
  {
    MatrixAssert.assertRowsEqual(MatrixFactory.newColumnMatrix(4, 2), 4);
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAssertRowsEqual_Matrix_int_SizeException() throws Exception
  {
    MatrixAssert.assertRowsEqual(MatrixFactory.newColumnMatrix(4, 2), 3);
  }

  /**
   * Test of assertColumnsEqual method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertColumnsEqual_Matrix_int() throws Exception
  {
    MatrixAssert.assertColumnsEqual(MatrixFactory.newColumnMatrix(2, 4), 4);
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAssertColumnsEqual_Matrix_int_SizeException() throws Exception
  {
    MatrixAssert.assertColumnsEqual(MatrixFactory.newColumnMatrix(2, 4),
            MatrixFactory.newColumnMatrix(1, 3));
  }

  /**
   * Test of assertColumnsEqual method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertColumnsEqual_Matrix_Matrix() throws Exception
  {
    MatrixAssert.assertColumnsEqual(MatrixFactory.newColumnMatrix(2, 4),
            MatrixFactory.newColumnMatrix(1, 4));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAssertColumnsEqual_Matrix_Matrix_SizeException() throws Exception
  {
    MatrixAssert.assertColumnsEqual(MatrixFactory.newColumnMatrix(4, 2),
            MatrixFactory.newColumnMatrix(4, 1));
  }

  /**
   * Test of assertNotViewResize method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertNotViewResize() throws Exception
  {
    MatrixAssert.assertNotViewResize(MatrixFactory.newColumnMatrix(4, 2));
  }

  /**
   * Test of assertViewResize method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test (expectedExceptions = {
    ResizeException.class
  })
  public void testAssertViewResize() throws Exception
  {
    MatrixAssert.assertViewResize(MatrixFactory.newColumnMatrix(4, 2), 4, 2);
    MatrixAssert.assertViewResize(MatrixFactory.newColumnMatrix(4, 2), 4, 1);
    MatrixAssert.assertViewResize(MatrixFactory.newColumnMatrix(4, 2), 1, 2);
    MatrixAssert.assertViewResize(MatrixFactory.newColumnMatrix(4, 2), 0, 0);
  }

  /**
   * Test of assertSymmetric method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertSymmetric() throws Exception
  {
    Matrix.ColumnAccess m1 = tg.newMatrix(2, 2);
    MatrixOps.addAssign(m1, m1.transpose());
    MatrixAssert.assertSymmetric(m1);
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAssertSymmetric_SizeException() throws Exception
  {
    Matrix.ColumnAccess m1 = MatrixFactory.newColumnMatrix(2, 3);
    MatrixAssert.assertSymmetric(m1);
  }

  @Test(expectedExceptions = MathException.class)
  public void testAssertSymmetric_MathException() throws Exception
  {
    Matrix.ColumnAccess m1 = tg.newMatrix(3, 3);
    MatrixAssert.assertSymmetric(m1);
  }

  /**
   * Test of assertVector method, of class MatrixAssert.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAssertVector() throws Exception
  {
    MatrixAssert.assertVector(tg.newMatrix(5, 1));
    MatrixAssert.assertVector(tg.newMatrix(1, 5));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAssertVector_SizeException() throws Exception
  {
    MatrixAssert.assertVector(tg.newMatrix(3, 3));
  }

}
