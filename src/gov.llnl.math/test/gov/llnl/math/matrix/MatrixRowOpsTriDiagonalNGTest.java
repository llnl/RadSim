/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import gov.llnl.math.MathExceptions.MathException;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixOps;
import gov.llnl.math.matrix.MatrixRowOperations;
import gov.llnl.math.matrix.special.MatrixTriDiagonal;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test code for MatrixRowOpsTriDiagonal.
 */
public class MatrixRowOpsTriDiagonalNGTest
{

  public MatrixRowOpsTriDiagonalNGTest()
  {
  }

  @Test
  public void testAddScaledRows()
  {

    double[] d = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] l = new double[]
    {
      1, 2, 3, 1
    };
    double[] u = new double[]
    {
      1, 2, 4, 5
    };

    MatrixTriDiagonal m = MatrixTriDiagonal.wrap(l, d, u);
    MatrixRowOperations ro = MatrixFactory.createRowOperations(m);

    ro.addScaledRows(1, 0, -1);
    Assert.assertEquals(l[0], 0.0);
    Assert.assertEquals(d[1], 1.0);

    ro.addScaledRows(2, 1, -1);
    Assert.assertEquals(l[1], 1.0);
    Assert.assertEquals(d[2], 1.0);

    try
    {
      ro.addScaledRows(3, 2, -1);
      Assert.fail();
    }
    catch (Exception ex)
    {
      Assert.assertTrue(ex instanceof MathException, "Got " + ex);
    }

    ro.addScaledRows(3, 4, -1);
    MatrixOps.dump(System.out, m);
    Assert.assertEquals(d[3], 3.0);
    Assert.assertEquals(u[3], 0.0);

    ro.addScaledRows(2, 3, -1);
    MatrixOps.dump(System.out, m);
    Assert.assertEquals(d[2], -2.0);
    Assert.assertEquals(u[2], 1.0);

    try
    {
      ro.addScaledRows(1, 2, -1);
      Assert.fail();
    }
    catch (Exception ex)
    {
      Assert.assertTrue(ex instanceof MathException, "Got " + ex);
    }

  }

  @Test
  public void testMultiplyAssignRow()
  {

    double[] d = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] l = new double[]
    {
      1, 2, 3, 1
    };
    double[] u = new double[]
    {
      1, 2, 4, 5
    };

    MatrixTriDiagonal m = MatrixTriDiagonal.wrap(l, d, u);
    MatrixRowOperations ro = MatrixFactory.createRowOperations(m);

    ro.multiplyAssignRow(0, 2);
    Assert.assertEquals(d[0], 2.0, 0.0);
    Assert.assertEquals(u[0], 2.0, 0.0);

    ro.multiplyAssignRow(1, 2);
    Assert.assertEquals(l[0], 2.0, 0.0);
    Assert.assertEquals(d[1], 4.0, 0.0);
    Assert.assertEquals(u[1], 4.0, 0.0);

    ro.multiplyAssignRow(4, 2);
    Assert.assertEquals(l[3], 2.0, 0.0);
    Assert.assertEquals(d[4], 10.0, 0.0);

    try
    {
      ro.multiplyAssignRow(5, 2);
      Assert.fail();
    }
    catch (Exception ex)
    {
      Assert.assertTrue(ex instanceof IndexOutOfBoundsException);
    }
  }

  @Test
  public void testDivideAssignRow()
  {
    double[] d = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] l = new double[]
    {
      1, 2, 3, 1
    };
    double[] u = new double[]
    {
      1, 2, 4, 5
    };

    MatrixTriDiagonal m = MatrixTriDiagonal.wrap(l, d, u);
    MatrixRowOperations ro = MatrixFactory.createRowOperations(m);

    ro.divideAssignRow(0, 2);
    Assert.assertEquals(d[0], 0.5);
    Assert.assertEquals(u[0], 0.5);

    ro.divideAssignRow(1, 2);
    Assert.assertEquals(l[0], 0.5);
    Assert.assertEquals(d[1], 1.0);
    Assert.assertEquals(u[1], 1.0);

    ro.divideAssignRow(4, 2);
    Assert.assertEquals(l[3], 0.5);
    Assert.assertEquals(d[4], 2.5);

    try
    {
      ro.divideAssignRow(5, 2);
      Assert.fail();
    }
    catch (Exception ex)
    {
      Assert.assertTrue(ex instanceof IndexOutOfBoundsException);
    }

  }

  @Test
  public void testSwapRows()
  {
    // not implemented
  }

  @Test
  public void testApply()
  {
    // does nothing
  }

}
