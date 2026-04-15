/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import gov.llnl.math.matrix.MatrixTransposeView;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixColumnArray;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixOps;
import gov.llnl.math.matrix.MatrixRowArray;
import support.MatrixTestGenerator;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 * Test code for MatrixTransposeView.
 */
public class MatrixTransposeViewNGTest
{
  MatrixTestGenerator tg = new MatrixTestGenerator();

  public MatrixTransposeViewNGTest()
  {
  }

  /**
   * Test of rows method, of class MatrixTransposeView.
   */
  @Test
  public void testRows()
  {
    assertEquals(new MatrixTransposeView(tg.newMatrix(2, 3)).rows(), 3);
  }

  /**
   * Test of columns method, of class MatrixTransposeView.
   */
  @Test
  public void testColumns()
  {
    assertEquals(new MatrixTransposeView(tg.newMatrix(3, 2)).columns(), 3);
  }

  /**
   * Test of set method, of class MatrixTransposeView.
   */
  @Test
  public void testSet() throws Exception
  {
    MatrixRowArray m = new MatrixRowArray(3, 3);
    MatrixTransposeView mt = new MatrixTransposeView(m);
    {
      int i = 0;
      for (int c = 0; c < 3; ++c)
      {
        for (int r = 0; r < 3; ++r)
        {
          mt.set(r, c, i);
          assertEquals(m.get(c, r), (double) i, 0.0);
          ++i;
        }
      }
    }
  }

  /**
   * Test of get method, of class MatrixTransposeView.
   */
  @Test
  public void testGet() throws Exception
  {
    Matrix m = MatrixFactory.newColumnMatrix(3, 3);
    Matrix mt = new MatrixTransposeView(m);
    int i = 0;
    for (int c = 0; c < 3; ++c)
    {
      for (int r = 0; r < 3; ++r)
      {
        m.set(r, c, i);
        assertEquals(mt.get(c, r), (double) i, 0.0);
        ++i;
      }
    }
  }

  /**
   * Test of mutable method, of class MatrixTransposeView.
   */
  @Test
  public void testMutable() throws Exception
  {
    MatrixColumnArray m = new MatrixColumnArray()
    {
      @Override
      public void mutable()
      {
        rows = 123;
      }
    };
    Matrix mt = new MatrixTransposeView(m);
    mt.mutable();
    assertEquals(m.rows(), 123);
  }

  /**
   * Test of sync method, of class MatrixTransposeView.
   */
  @Test
  public void testSync()
  {
    Matrix m = MatrixFactory.newColumnMatrix(3, 3);
    Matrix mt = new MatrixTransposeView(m);
    assertEquals(mt.sync(), m);
  }

  /**
   * Test of resize method, of class MatrixTransposeView.
   */
  @Test
  public void testResize() throws Exception
  {
    Matrix m = MatrixFactory.newMatrix(2, 2);
    Matrix mt = new MatrixTransposeView(m);
    mt.resize(4, 1);
    assertEquals(m.rows(), 1);
    assertEquals(m.columns(), 4);
  }

  /**
   * Test of assign method, of class MatrixTransposeView.
   */
  @Test
  public void testAssign() throws Exception
  {
    Matrix m = MatrixFactory.newMatrix(3, 3);
    Matrix mt = new MatrixTransposeView(m);
    Matrix u = tg.newMatrix(4, 2);
    mt.assign(u);
    assertEquals(m.rows(), 2);
    assertEquals(m.columns(), 4);
    for (int r = 0; r < u.rows(); ++r)
    {
      for (int c = 0; c < u.columns(); ++c)
      {
        assertEquals(u.get(r, c), m.get(c, r), 0.0);
      }
    }
  }

  /**
   * Test of assignRow method, of class MatrixTransposeView.
   */
  @Test
  public void testAssignRow() throws Exception
  {
    Matrix m = MatrixFactory.newMatrix(4, 2);
    Matrix.WriteAccess mt = new MatrixTransposeView(m);

    double[] d = new double[4];
    for (int i = 0; i < 4; ++i)
    {
      d[i] = i + 1;
    }
    mt.assignRow(d, 0);
    for (int i = 0; i < 4; ++i)
    {
      assertEquals(m.get(i, 0), (double) i + 1);
    }
  }

  /**
   * Test of assignColumn method, of class MatrixTransposeView.
   */
  @Test
  public void testAssignColumn() throws Exception
  {
    Matrix m = MatrixFactory.newMatrix(2, 4);
    Matrix.WriteAccess mt = new MatrixTransposeView(m);

    double[] d = new double[4];
    for (int i = 0; i < 4; ++i)
    {
      d[i] = i + 1;
    }
    mt.assignColumn(d, 0);
    for (int i = 0; i < 4; ++i)
    {
      assertEquals(m.get(0, i), (double) i + 1);
    }
  }

  /**
   * Test of copyRowTo method, of class MatrixTransposeView.
   */
  @Test
  public void testCopyRow()
  {
    Matrix m = tg.newMatrix(4, 3);
    Matrix mt = new MatrixTransposeView(m);
    for (int j = 0; j < 3; j++)
    {
      double[] c = mt.copyRowTo(new double[4], 0, j);
      for (int i = 0; i < 4; ++i)
      {
        assertEquals(m.get(i, j), c[i], 0.0);
      }
    }
  }

  /**
   * Test of copyColumnTo method, of class MatrixTransposeView.
   */
  @Test
  public void testCopyColumn()
  {
    Matrix m = tg.newMatrix(3, 4);
    Matrix mt = new MatrixTransposeView(m);
    for (int j = 0; j < 3; j++)
    {
      double[] c = mt.copyColumnTo(new double[4], 0, j);
      for (int i = 0; i < 4; ++i)
      {
        assertEquals(m.get(j, i), c[i]);
      }
    }
  }

  /**
   * Test of transpose method, of class MatrixTransposeView.
   */
  @Test
  public void testTranspose()
  {
    Matrix m = tg.newMatrix(4, 2);
    MatrixTransposeView mt = new MatrixTransposeView(m);
    assertEquals(m, mt.transpose());
  }

  /**
   * Test of copyOf method, of class MatrixTransposeView.
   */
  @Test
  public void testCopyOf()
  {
  }

  @Test
  public void testAddAssign() throws Exception
  {
    Matrix m1 = tg.newMatrix(3, 3);
    Matrix mt = new MatrixTransposeView(m1);
    Matrix m2 = m1.copyOf();
    MatrixOps.addAssign(m2, mt);
    MatrixOps.addAssign(m1, mt);
    assertTrue(MatrixOps.equivalent(m1, m2));
  }

  @Test
  public void testSubtractAssign() throws Exception
  {
    Matrix m1 = tg.newMatrix(3, 3);
    Matrix mt = new MatrixTransposeView(m1);
    Matrix m2 = m1.copyOf();
    MatrixOps.subtractAssign(m2, mt);
    MatrixOps.subtractAssign(m1, mt);
    assertTrue(MatrixOps.equivalent(m1, m2));
  }

  @Test
  public void testMultiplyAssignElements() throws Exception
  {
    Matrix m1 = tg.newMatrix(3, 3);
    Matrix mt = new MatrixTransposeView(m1);
    Matrix m2 = m1.copyOf();
    MatrixOps.multiplyAssignElements(m2, mt);
    MatrixOps.multiplyAssignElements(m1, mt);
    assertTrue(MatrixOps.equivalent(m1, m2));
  }

}
