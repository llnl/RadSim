/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import gov.llnl.math.MathExceptions;
import static org.testng.Assert.assertEquals;
import support.MatrixTestGenerator;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 * Test code for MatrixOpMultiply.
 */
public class MatrixOpMultiplyNGTest
{
  MatrixTestGenerator tg = new MatrixTestGenerator();

  public MatrixOpMultiplyNGTest()
  {
  }

  public static Matrix referenceMultiply(Matrix a, Matrix b)
  {
    try
    {
      Matrix result = MatrixFactory.newMatrix(a.rows(), b.columns());
      for (int i0 = 0; i0 < a.rows(); ++i0)
      {
        for (int i1 = 0; i1 < b.columns(); ++i1)
        {

          double sum = 0;
          for (int i2 = 0; i2 < a.columns(); ++i2)
            sum += a.get(i0, i2) * b.get(i2, i1);
          result.set(i0, i1, sum);

        }
      }
      return result;
    }
    catch (MathExceptions.WriteAccessException | IndexOutOfBoundsException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Test of multiplyColumnAccumulate method, of class MatrixOpMultiply.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMultiplyColumnAccumulate() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      Matrix.ColumnAccess m1 = new MatrixColumnTable(tg.newMatrix(1, 10, 1, 10));
      Matrix m2 = new MatrixColumnTable(tg.newMatrixFixedRows(m1.columns(), 1, 10));
      Matrix expected = referenceMultiply(m1, m2);

      Matrix.ColumnAccess out = new MatrixColumnTable();
      MatrixOpMultiply.multiplyColumnAccumulate(out, m1, m2);
      try
      {
        assertTrue(MatrixOps.equivalent(expected, out));
      }
      catch (AssertionError ex)
      {
        System.out.println("A:");
        MatrixOps.dump(System.out, m1);
        System.out.println("B:");
        MatrixOps.dump(System.out, m2);
        System.out.println("R:");
        MatrixOps.dump(System.out, out);
        System.out.println("E:");
        MatrixOps.dump(System.out, expected);
        throw ex;
      }
    }
  }

  /**
   * Test of multiplyRowAccumulate method, of class MatrixOpMultiply.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMultiplyRowAccumulate() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      Matrix m1 = new MatrixRowTable(tg.newMatrix(1, 10, 1, 10));
      Matrix.RowAccess m2 = new MatrixRowTable(tg.newMatrixFixedRows(m1.columns(), 1, 10));
      Matrix expected = referenceMultiply(m1, m2);

      Matrix.RowAccess out = new MatrixRowTable();
      MatrixOpMultiply.multiplyRowAccumulate(out, m1, m2);
      try
      {
        assertTrue(MatrixOps.equivalent(expected, out));
      }
      catch (AssertionError ex)
      {
        System.out.println("A:");
        MatrixOps.dump(System.out, m1);
        System.out.println("B:");
        MatrixOps.dump(System.out, m2);
        System.out.println("R:");
        MatrixOps.dump(System.out, out);
        System.out.println("E:");
        MatrixOps.dump(System.out, expected);
        throw ex;
      }
    }
  }

  /**
   * Test of multiplyRowInner method, of class MatrixOpMultiply.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMultiplyRowInner() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      Matrix.RowAccess m1 = new MatrixRowTable(tg.newMatrix(1, 10, 1, 10));
      Matrix.ColumnAccess m2 = new MatrixColumnTable(tg.newMatrixFixedRows(m1.columns(), 1, 10));
      Matrix expected = referenceMultiply(m1, m2);

      Matrix.ColumnAccess out = new MatrixColumnTable();
      MatrixOpMultiply.multiplyRowInner(out, m1, m2);
      try
      {
        assertTrue(MatrixOps.equivalent(expected, out));
      }
      catch (AssertionError ex)
      {
        System.out.println("A:");
        MatrixOps.dump(System.out, m1);
        System.out.println("B:");
        MatrixOps.dump(System.out, m2);
        System.out.println("R:");
        MatrixOps.dump(System.out, out);
        System.out.println("E:");
        MatrixOps.dump(System.out, expected);
        throw ex;
      }
    }
  }

  /**
   * Test of multiplyColumnInner method, of class MatrixOpMultiply.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMultiplyColumnInner() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      Matrix.RowAccess m1 = new MatrixRowTable(tg.newMatrix(1, 10, 1, 10));
      Matrix.ColumnAccess m2 = new MatrixColumnTable(tg.newMatrixFixedRows(m1.columns(), 1, 10));
      Matrix expected = referenceMultiply(m1, m2);

      Matrix.RowAccess out = new MatrixRowTable();
      MatrixOpMultiply.multiplyColumnInner(out, m1, m2);
      try
      {
        assertTrue(MatrixOps.equivalent(expected, out));
      }
      catch (AssertionError ex)
      {
        System.out.println("A:");
        MatrixOps.dump(System.out, m1);
        System.out.println("B:");
        MatrixOps.dump(System.out, m2);
        System.out.println("R:");
        MatrixOps.dump(System.out, out);
        System.out.println("E:");
        MatrixOps.dump(System.out, expected);
        throw ex;
      }
    }
  }
  
  /**
   * Test of determinePolicy method, of class MatrixOpMultiply.
   */
  @Test
  public void testDeterminePolicy()
  {
    Matrix a = MatrixFactory.newMatrix(1,3);
    Matrix b = MatrixFactory.newMatrix(3,1);
    MatrixOpMultiply.MultiplyPolicy expResult = MatrixOpMultiply.MultiplyPolicy.ROW_INNER;
    MatrixOpMultiply.MultiplyPolicy result = MatrixOpMultiply.determinePolicy(a,b);
    assertEquals(result, expResult);
    
    a = MatrixFactory.newMatrix(1,3);
    b = MatrixFactory.newMatrix(3,0);
    expResult = MatrixOpMultiply.MultiplyPolicy.COLUMN_INNER;
    result = MatrixOpMultiply.determinePolicy(a,b);
    assertEquals(result, expResult);
    
    a = MatrixFactory.newMatrix(1,0);
    b = MatrixFactory.newMatrix(3,1);
    expResult = MatrixOpMultiply.MultiplyPolicy.COLUMN_ACCUMULATE;
    result = MatrixOpMultiply.determinePolicy(a,b);
    assertEquals(result, expResult);
    
    a = MatrixFactory.newMatrix(4,1);
    b = MatrixFactory.newMatrix(2,3);
    expResult = MatrixOpMultiply.MultiplyPolicy.ROW_ACCUMULATE;
    result = MatrixOpMultiply.determinePolicy(a,b);
    assertEquals(result, expResult);
  }

}
