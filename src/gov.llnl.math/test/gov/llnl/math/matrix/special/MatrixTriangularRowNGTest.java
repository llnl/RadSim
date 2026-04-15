/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix.special;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.MathExceptions.MathException;
import gov.llnl.math.MathExceptions.WriteAccessException;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixOps;
import support.MatrixTestGenerator;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * Test code for MatrixTriangularRow.
 */
public class MatrixTriangularRowNGTest
{
  static final double[][] TRIANGULAR_CONTENTS =
  {
    {
      1, 0, 0, 0, 0
    },
    {
      2, 3, 0, 0, 0
    },
    {
      4, 5, 6, 0, 0
    },
    {
      7, 1, 9, 8, 0
    },
    {
      2, 5, 7, 3, 4
    }
  };
  MatrixTestGenerator tg=new MatrixTestGenerator();

  static MatrixTriangularRow makeUpper()
  {
    Matrix m1 = MatrixFactory.wrapFromArray(TRIANGULAR_CONTENTS, false);
    return new MatrixTriangularRow(m1);
  }

  static MatrixTriangularRow makeLower()
  {
    Matrix m1 = MatrixFactory.wrapFromArray(TRIANGULAR_CONTENTS, true);
    return new MatrixTriangularRow(m1);
  }

  public MatrixTriangularRowNGTest()
  {
  }

  @Test(expectedExceptions = MathException.class)
  public void testCtor()
  {
    Matrix m1 = MatrixFactory.newMatrix(5, 5);
    m1.set(0, 4, 1);
    m1.set(4, 0, 1);
    Matrix.Triangular tm1 = new MatrixTriangularRow(m1);
  }

  /**
   * Test of isUpper method, of class MatrixTriangularRow.
   */
  @Test
  public void testIsUpper()
  {
    Matrix m1 = MatrixFactory.newMatrix(5, 5);
    m1.set(0, 4, 1);
    Matrix.Triangular tm1 = new MatrixTriangularRow(m1);
    Assert.assertTrue(tm1.isUpper());

    Matrix m2 = MatrixFactory.newMatrix(5, 5);
    m2.set(4, 0, 1);
    Matrix.Triangular tm2 = new MatrixTriangularRow(m2);
    Assert.assertTrue(!tm2.isUpper());
  }

  /**
   * Test of rows method, of class MatrixTriangularRow.
   */
  @Test
  public void testRows()
  {
    Matrix m1 = MatrixFactory.newMatrix(5, 5);
    m1.set(0, 4, 1);
    Matrix.Triangular tm1 = new MatrixTriangularRow(m1);
    assertEquals(m1.rows(), 5);
  }

  /**
   * Test of columns method, of class MatrixTriangularRow.
   */
  @Test
  public void testColumns()
  {
    Matrix m1 = MatrixFactory.newMatrix(5, 5);
    m1.set(0, 4, 1);
    Matrix.Triangular tm1 = new MatrixTriangularRow(m1);
    assertEquals(m1.columns(), 5);
  }

  /**
   * Test of set method, of class MatrixTriangularRow.
   */
  @Test
  public void testSet()
  {
    { // Test upper 
      Matrix m1 = MatrixFactory.newMatrix(5, 5);
      m1.set(0, 4, 1);
      MatrixTriangularRow tm1 = new MatrixTriangularRow(m1);
      for (int i = 0; i < 5; i++)
        for (int j = 0; j < 5; j++)
          tm1.set(i, j, j >= i ? 1 : 0);
    }

    {
      // Test lower
      Matrix m1 = MatrixFactory.newMatrix(5, 5);
      m1.set(4, 0, 1);
      Matrix.Triangular tm1 = new MatrixTriangularRow(m1);
      for (int i = 0; i < 5; i++)
        for (int j = 0; j < 5; j++)
          tm1.set(i, j, i >= j ? 1 : 0);
    }
  }

  @Test(expectedExceptions = WriteAccessException.class)
  public void testSetException()
  {
    Matrix.Triangular tm1 = makeUpper();
    tm1.set(4, 0, 1);
  }

  /**
   * Test of get method, of class MatrixTriangularRow.
   */
  @Test
  public void testGet()
  {
    MatrixTriangularRow trig = makeUpper();
    for (int i = 0; i < 5; ++i)
    {
      for (int j = 0; j < 5; ++j)
      {
        Assert.assertEquals(trig.get(i, j), TRIANGULAR_CONTENTS[j][i], 0.0);
      }
    }
  }

  /**
   * Test of resize method, of class MatrixTriangularRow.
   */
  @Test
  public void testResize()
  {
    MatrixTriangularRow mtr = new MatrixTriangularRow(3, 3, true);
    mtr.resize(5, 5);
    assertEquals(mtr.rows(), 5);
    assertEquals(mtr.columns(), 5);
  }

  /**
   * Test of assign method, of class MatrixTriangularRow.
   */
  @Test
  public void testAssign()
  {
    MatrixTriangularRow mtr = new MatrixTriangularRow(5, 5, true);
    mtr.set(0, 4, 1);
    
    Matrix expResult = mtr;
    Matrix result = mtr.assign(MatrixFactory.newMatrix(1,1));
    assertEquals(result, expResult);
    
    result = mtr.assign(mtr);
    assertEquals(result, expResult);

  }

  /**
   * Test of copyOf method, of class MatrixTriangularRow.
   */
  @Test
  public void testCopyOf()
  {
    MatrixTriangularRow mtr = new MatrixTriangularRow(5, 5, true);
    mtr.set(0, 4, 1);
    Matrix mtr1 = mtr.copyOf();
    Assert.assertTrue(mtr1 instanceof Matrix.Triangular);
  }

  /**
   * Test of copyColumnTo method, of class MatrixTriangularRow.
   */
  @Test
  public void testCopyColumnTo()
  {
    MatrixTriangularRow trig = makeUpper();
    for (int i = 0; i < 5; i++)
    {
      double[] v = trig.copyColumnTo(new double[5], 0, i);
      Assert.assertTrue(DoubleArray.equivalent(v, TRIANGULAR_CONTENTS[i]));
    }
  }

  /**
   * Test of copyRowTo method, of class MatrixTriangularRow.
   */
  @Test
  public void testCopyRowTo()
  {
    MatrixTriangularRow trig = makeLower();
    for (int i = 0; i < 5; i++)
    {
      double[] v = trig.copyRowTo(new double[5], 0, i);
      Assert.assertTrue(DoubleArray.equivalent(v, TRIANGULAR_CONTENTS[i]));
    }
  }

  /**
   * Test of accessRow method, of class MatrixTriangularRow.
   */
  @Test
  public void testAccessRow()
  {
    MatrixTriangularRow trig = makeLower();
    for (int i = 0; i < 5; i++)
    {
      double[] v = trig.accessRow(i);
      int j = trig.addressRow(i);
      Assert.assertTrue(DoubleArray.equivalent(v, j, TRIANGULAR_CONTENTS[i], 0, 5));
    }
  }

  /**
   * Test of addressRow method, of class MatrixTriangularRow.
   */
  @Test
  public void testAddressRow()
  {
    MatrixTriangularRow trig = makeLower();
    for (int i = 0; i < 5; i++)
    {
      int j = trig.addressRow(i);
      Assert.assertEquals(j, 0);
    }
  }

  /**
   * Test of divideLeft method, of class MatrixTriangularRow.
   */
  @Test
  public void testDivideLeft()
  {
    { // Test upper
      MatrixTriangularRow trig = makeUpper();
      Matrix m1 = tg.newMatrix(5, 1);
      Matrix m2 = m1.copyOf();
      trig.divideLeft(MatrixFactory.createRowOperations(m2));
      Assert.assertTrue(MatrixOps.equivalent(m1, MatrixOps.multiply(trig, m2)));
    }

    { // Test lower
      MatrixTriangularRow trig = makeLower();
      Matrix m1 = tg.newMatrix(5, 1);
      Matrix m2 = m1.copyOf();
      trig.divideLeft(MatrixFactory.createRowOperations(m2));
      Assert.assertTrue(MatrixOps.equivalent(m1, MatrixOps.multiply(trig, m2)));
    }
  }

}
