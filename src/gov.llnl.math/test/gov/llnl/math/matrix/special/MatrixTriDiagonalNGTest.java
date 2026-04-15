/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix.special;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.MathExceptions.ResizeException;
import gov.llnl.math.MathExceptions.SizeException;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixOps;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;
import support.MatrixTestGenerator;

/**
 * Test code for MatrixTriDiagonal.
 */
public class MatrixTriDiagonalNGTest
{
  MatrixTestGenerator tg = new MatrixTestGenerator();

  public MatrixTriDiagonal makeMatrix(int size)
  {
    return MatrixTriDiagonal.wrap(tg.newArray(size - 1), tg.newArray(size), tg.newArray(size - 1));
  }

  public MatrixTriDiagonalNGTest()
  {
  }
  
  /**
   * Test constructor of class MatrixTriDiagonal.
   */
  @Test
  public void testConstructor()
  {
    MatrixTriDiagonal instance = new MatrixTriDiagonal(1);
  }
  
  /**
   * Test of wrap method, of class MatrixTriDiagonal.
   */
  @Test
  public void testWrap()
  {
    Matrix tri = MatrixTriDiagonal.wrap(tg.newArray(4), tg.newArray(5), tg.newArray(4));
  }

  /**
   * Test of wrap method, of class MatrixTriDiagonal.
   */
  @Test(expectedExceptions = SizeException.class)
  public void testWrapSizeException()
  {
    Matrix tri = MatrixTriDiagonal.wrap(tg.newArray(5), tg.newArray(5), tg.newArray(4));
  }

  /**
   * Test of rows method, of class MatrixTriDiagonal.
   */
  @Test
  public void testRows()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    Assert.assertEquals(m1.rows(), 5);
  }

  /**
   * Test of columns method, of class MatrixTriDiagonal.
   */
  @Test
  public void testColumns()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    Assert.assertEquals(m1.columns(), 5);
  }

  /**
   * Test of set method, of class MatrixTriDiagonal.
   */
  @Test
  public void testSet()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    for (int i = 0; i < 5; ++i)
    {
      for (int j = 0; j < 5; ++j)
      {
        m1.set(i, j, Math.abs(i - j) < 2 ? 1 : 0);
      }
    }
  }

  /**
   * Test of get method, of class MatrixTriDiagonal.
   */
  @Test
  public void testGet()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    for (int i = 0; i < 5; ++i)
    {
      for (int j = 0; j < 5; ++j)
      {
        double v = m1.get(i, j);
        Assert.assertEquals(v != 0, Math.abs(i - j) < 2);
      }
    }
  }

  /**
   * Test of mutable method, of class MatrixTriDiagonal.
   */
  @Test
  public void testMutable()
  {
    // Nothing to check
  }

  /**
   * Test of sync method, of class MatrixTriDiagonal.
   */
  @Test
  public void testSync()
  {
    Matrix m = makeMatrix(5);
    Assert.assertEquals(m, m.sync());
  }

  /**
   * Test of resize method, of class MatrixTriDiagonal.
   */
  @Test
  public void testResize()
  {
    MatrixTriDiagonal m = makeMatrix(5);
    m.resize(7, 7);
  }

  @Test(expectedExceptions = ResizeException.class)
  public void testResizeResizeException()
  {
    MatrixTriDiagonal m = makeMatrix(5);
    m.resize(7, 6);
  }

  /**
   * Test of assign method, of class MatrixTriDiagonal.
   */
  @Test
  public void testAssign()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    m1.assign(makeMatrix(7));
    
    Matrix expResult = m1;
    Matrix result = m1.assign(MatrixFactory.newMatrix(1,1));
    assertEquals(result, expResult);
    
    result = m1.assign(m1);
    assertEquals(result, expResult);
  }

  /**
   * Test of copyRowTo method, of class MatrixTriDiagonal.
   */
  @Test
  public void testCopyRowTo()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    double[] v = new double[5];
    double[] v2 = new double[5];
    for (int i = 0; i < 5; ++i)
    {
      v = m1.copyRowTo(v, 0, i);
      for (int j = 0; j < 5; ++j)
        v2[j] = m1.get(i, j);
      Assert.assertTrue(DoubleArray.equivalent(v, v2));
    }
  }

  /**
   * Test of copyColumnTo method, of class MatrixTriDiagonal.
   */
  @Test
  public void testCopyColumnTo()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    double[] v = new double[5];
    double[] v2 = new double[5];
    for (int i = 0; i < 5; ++i)
    {
      v = m1.copyColumnTo(v, 0, i);
      for (int j = 0; j < 5; ++j)
        v2[j] = m1.get(j, i);
      Assert.assertTrue(DoubleArray.equivalent(v, v2));
    }
  }

  /**
   * Test of copyOf method, of class MatrixTriDiagonal.
   */
  @Test
  public void testCopyOf()
  {
    Matrix m1 = makeMatrix(5);
    Assert.assertTrue(m1.copyOf() instanceof MatrixTriDiagonal);
  }

  /**
   * Test of divideLeft method, of class MatrixTriDiagonal.
   */
  @Test
  public void testDivideLeft_MatrixRowOperations()
  {
    MatrixTriDiagonal trig = makeMatrix(5);
    Matrix m1 = tg.newMatrix(5, 1);
    Matrix m2 = m1.copyOf();
    trig.divideLeft(MatrixFactory.createRowOperations(m2));
    Assert.assertTrue(MatrixOps.equivalent(m1, MatrixOps.multiply(trig, m2)));
  }

  /**
   * Test of divideLeft method, of class MatrixTriDiagonal.
   */
  @Test
  public void testDivideLeft_doubleArr()
  {
    MatrixTriDiagonal trig = makeMatrix(5);
    double[] m1 = tg.newArray(5);
    double[] m2 = trig.divideLeft(m1);
    Assert.assertTrue(DoubleArray.equivalent(m1, MatrixOps.multiply(trig, m2)));
  }

  /**
   * Test of divideRight method, of class MatrixTriDiagonal.
   */
  @Test
  public void testDivideRight()
  {
    MatrixTriDiagonal trig = makeMatrix(5);
    double[] m1 = tg.newArray(5);
    double[] m2 = trig.divideRight(m1);
    Assert.assertTrue(DoubleArray.equivalent(m1, MatrixOps.multiply(m2, trig)));
  }

  /**
   * Test of addAssign method, of class MatrixTriDiagonal.
   */
  @Test
  public void testAddAssign()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    m1.addAssign(0);
  }

  @Test(expectedExceptions = UnsupportedOperationException.class)
  public void testAddAssign_UnsupportedOperationException()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    m1.addAssign(1);
  }

  /**
   * Test of multiplyAssign method, of class MatrixTriDiagonal.
   */
  @Test
  public void testMultiplyAssign()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    Matrix m2 = m1.copyOf();
    m1.multiplyAssign(2);
    Assert.assertFalse(MatrixOps.equivalent(m1, m2));
    m1.multiplyAssign(0.5);
    Assert.assertTrue(MatrixOps.equivalent(m1, m2));
    m1.multiplyAssign(1);
    Assert.assertTrue(MatrixOps.equivalent(m1, m2));
  }

  /**
   * Test of divideAssign method, of class MatrixTriDiagonal.
   */
  @Test
  public void testDivideAssign()
  {
    MatrixTriDiagonal m1 = makeMatrix(5);
    Matrix m2 = m1.copyOf();
    m1.divideAssign(2);
    Assert.assertFalse(MatrixOps.equivalent(m1, m2));
    m1.divideAssign(0.5);
    Assert.assertTrue(MatrixOps.equivalent(m1, m2));
    m1.divideAssign(1);
    Assert.assertTrue(MatrixOps.equivalent(m1, m2));
  }

}
