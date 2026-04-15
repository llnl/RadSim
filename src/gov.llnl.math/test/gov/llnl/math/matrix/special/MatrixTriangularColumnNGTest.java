/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix.special;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.MathExceptions;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixOps;
import support.MatrixTestGenerator;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * Test code for MatrixTriangularColumn.
 */
public class MatrixTriangularColumnNGTest
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
  MatrixTestGenerator tg = new MatrixTestGenerator();

  static MatrixTriangularColumn makeUpper()
  {
    Matrix m1 = MatrixFactory.wrapFromArray(TRIANGULAR_CONTENTS, false);
    return new MatrixTriangularColumn(m1);
  }

  static MatrixTriangularColumn makeLower()
  {
    Matrix m1 = MatrixFactory.wrapFromArray(TRIANGULAR_CONTENTS, true);
    return new MatrixTriangularColumn(m1);
  }

  @Test
  public void mm()
  {
    Matrix m1 = MatrixFactory.wrapFromArray(TRIANGULAR_CONTENTS, false);
    MatrixTriangularColumn trig = makeUpper();
  }

  public MatrixTriangularColumnNGTest()
  {
  }

  @Test(expectedExceptions = MathExceptions.MathException.class)
  public void testCtor()
  {
    Matrix m1 = MatrixFactory.newMatrix(5, 5);
    m1.set(0, 4, 1);
    m1.set(4, 0, 1);
    Matrix.Triangular tm1 = new MatrixTriangularColumn(m1);
  }

  /**
   * Test of isUpper method, of class MatrixTriangularColumn.
   */
  @Test
  public void testIsUpper()
  {
    Matrix m1 = MatrixFactory.newMatrix(5, 5);
    m1.set(0, 4, 1);
    Matrix.Triangular tm1 = new MatrixTriangularColumn(m1);
    Assert.assertTrue(tm1.isUpper());

    Matrix m2 = MatrixFactory.newMatrix(5, 5);
    m2.set(4, 0, 1);
    Matrix.Triangular tm2 = new MatrixTriangularColumn(m2);
    Assert.assertTrue(!tm2.isUpper());
  }

  /**
   * Test of rows method, of class MatrixTriangularColumn.
   */
  @Test
  public void testRows()
  {
    Matrix m1 = MatrixFactory.newMatrix(5, 5);
    m1.set(0, 4, 1);
    Matrix.Triangular tm1 = new MatrixTriangularColumn(m1);
    assertEquals(m1.rows(), 5);
  }

  /**
   * Test of columns method, of class MatrixTriangularColumn.
   */
  @Test
  public void testColumns()
  {
    Matrix m1 = MatrixFactory.newMatrix(5, 5);
    m1.set(0, 4, 1);
    Matrix.Triangular tm1 = new MatrixTriangularColumn(m1);
    assertEquals(m1.columns(), 5);
  }

  /**
   * Test of set method, of class MatrixTriangularColumn.
   */
  @Test
  public void testSet()
  {
    { // Test upper 
      Matrix m1 = MatrixFactory.newMatrix(5, 5);
      m1.set(0, 4, 1);
      MatrixTriangularColumn tm1 = new MatrixTriangularColumn(m1);
      for (int i = 0; i < 5; i++)
        for (int j = 0; j < 5; j++)
          tm1.set(i, j, j >= i ? 1 : 0);
    }

    {
      // Test lower
      Matrix m1 = MatrixFactory.newMatrix(5, 5);
      m1.set(4, 0, 1);
      Matrix.Triangular tm1 = new MatrixTriangularColumn(m1);
      for (int i = 0; i < 5; i++)
        for (int j = 0; j < 5; j++)
          tm1.set(i, j, i >= j ? 1 : 0);
    }
  }

  @Test(expectedExceptions = MathExceptions.WriteAccessException.class)
  public void testSetException()
  {
    Matrix.Triangular tm1 = makeUpper();
    tm1.set(4, 0, 1);
  }

  /**
   * Test of get method, of class MatrixTriangularColumn.
   */
  @Test
  public void testGet()
  {
    MatrixTriangularColumn trig = makeUpper();
    for (int i = 0; i < 5; ++i)
    {
      for (int j = 0; j < 5; ++j)
      {
        Assert.assertEquals(trig.get(i, j), TRIANGULAR_CONTENTS[j][i], 0.0);
      }
    }
  }

  /**
   * Test of resize method, of class MatrixTriangularColumn.
   */
  @Test
  public void testResize()
  {
    MatrixTriangularColumn mtr = new MatrixTriangularColumn(3, 3, true);
    mtr.resize(5, 5);
    assertEquals(mtr.rows(), 5);
    assertEquals(mtr.columns(), 5);
  }

  /**
   * Test of assign method, of class MatrixTriangularColumn.
   */
  @Test
  public void testAssign()
  {
    MatrixTriangularColumn mtr = new MatrixTriangularColumn(5, 5, true);
    mtr.set(0, 4, 1);
    Matrix expResult = mtr;
    Matrix result = mtr.assign(MatrixFactory.newMatrix(1,1));
    assertEquals(result, expResult);
    
    result = mtr.assign(mtr);
    assertEquals(result, expResult);
  }

  /**
   * Test of copyOf method, of class MatrixTriangularColumn.
   */
  @Test
  public void testCopyOf()
  {
    MatrixTriangularColumn mtr = new MatrixTriangularColumn(5, 5, true);
    mtr.set(0, 4, 1);
    Matrix mtr1 = mtr.copyOf();
    Assert.assertTrue(mtr1 instanceof Matrix.Triangular);
  }

  /**
   * Test of copyColumnTo method, of class MatrixTriangularColumn.
   */
  @Test
  public void testCopyColumnTo()
  {
    MatrixTriangularColumn trig = makeUpper();
    for (int i = 0; i < 5; i++)
    {
      double[] v = trig.copyColumnTo(new double[5], 0, i);
      Assert.assertTrue(DoubleArray.equivalent(v, TRIANGULAR_CONTENTS[i]));
    }
  }

  /**
   * Test of copyRowTo method, of class MatrixTriangularColumn.
   */
  @Test
  public void testCopyRowTo()
  {
    MatrixTriangularColumn trig = makeLower();
    for (int i = 0; i < 5; i++)
    {
      double[] v = trig.copyRowTo(new double[5], 0, i);
      Assert.assertTrue(DoubleArray.equivalent(v, TRIANGULAR_CONTENTS[i]));
    }
  }

  /**
   * Test of accessRow method, of class MatrixTriangularColumn.
   */
  @Test
  public void testAccessColumn()
  {
    MatrixTriangularColumn trig = makeUpper();
    for (int i = 0; i < 5; i++)
    {
      double[] v = trig.accessColumn(i);
      int j = trig.addressColumn(i);
      Assert.assertTrue(DoubleArray.equivalent(v, j, TRIANGULAR_CONTENTS[i], 0, 5));
    }
  }

  /**
   * Test of addressRow method, of class MatrixTriangularColumn.
   */
  @Test
  public void testAddressColumn()
  {
    MatrixTriangularColumn trig = makeUpper();
    for (int i = 0; i < 5; i++)
    {
      int j = trig.addressColumn(i);
      Assert.assertEquals(j, 0);
    }
  }

  /**
   * Test of divideLeft method, of class MatrixTriangularColumn.
   */
  @Test
  public void testDivideLeft()
  {
    {
      MatrixTriangularColumn trig = makeLower();
      Matrix m1 = MatrixFactory.newMatrix(trig);
      trig.divideLeft(MatrixFactory.createRowOperations(m1));
      MatrixOps.dump(System.out, m1);
    }

    { // Test upper
      MatrixTriangularColumn trig = makeUpper();
      Matrix m1 = tg.newMatrix(5, 1);
      Matrix m2 = m1.copyOf();
      trig.divideLeft(MatrixFactory.createRowOperations(m2));
      Assert.assertTrue(MatrixOps.equivalent(m1, MatrixOps.multiply(trig, m2)));
    }

    { // Test lower
      MatrixTriangularColumn trig = makeLower();
      Matrix m1 = tg.newMatrix(5, 1);
      Matrix m2 = m1.copyOf();
      trig.divideLeft(MatrixFactory.createRowOperations(m2));
      Assert.assertTrue(MatrixOps.equivalent(m1, MatrixOps.multiply(trig, m2)));
    }
  }

}
