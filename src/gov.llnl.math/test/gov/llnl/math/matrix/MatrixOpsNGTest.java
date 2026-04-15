/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.MathExceptions;
import gov.llnl.math.MathExceptions.SizeException;
import gov.llnl.math.MathExceptions.WriteAccessException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;
import support.MatrixTestGenerator;
import support.MatrixTestProxy;
import support.MatrixTestReadOnly;

/**
 * Test code for MatrixOps.
 */
public class MatrixOpsNGTest
{
  MatrixTestGenerator tg = new MatrixTestGenerator();
  static final int CASES = 10;

  public MatrixOpsNGTest()
  {
  }

//<editor-fold desc="add">
  /**
   * Test of addAssign method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAddAssign_GenericType_double() throws Exception
  {
    for (int i = 0; i < CASES; i++)
    {
      MatrixColumnArray m1 = tg.newMatrix(1, 10, 1, 10);
      double value = tg.rand.nextDouble();
      double[] v1 = DoubleArray.addAssign(m1.toArray().clone(), value);
      MatrixColumnArray expected = new MatrixColumnArray(v1, m1.rows(), m1.columns());

      Matrix[] cases1 = tg.eachType(m1);
      for (Matrix c1 : cases1)
      {
        Matrix target = c1.copyOf();
        MatrixOps.addAssign(target, value);
        Assert.assertTrue(MatrixOps.equivalent(expected, target));
      }
    }
  }

  @Test(expectedExceptions = WriteAccessException.class)
  public void testAddAssign_GenericType_double_WriteAccessException() throws Exception
  {
    MatrixTestReadOnly ro = new MatrixTestReadOnly(tg.newMatrix(2, 2));
    MatrixOps.addAssign(ro, 1);
  }

  /**
   * Test of addAssign method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAddAssign_GenericType_Matrix() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      MatrixColumnArray m1 = tg.newMatrix(1, 10, 1, 10);
      MatrixColumnArray m2 = tg.newMatrix(m1.rows(), m1.columns());
      double[] v1 = DoubleArray.addAssign(DoubleArray.copyOf(m1.toArray()), m2.toArray());
      MatrixColumnArray expected = new MatrixColumnArray(v1, m1.rows(), m1.columns());

      Matrix[] cases1 = tg.eachType(m1);
      Matrix[] cases2 = tg.eachType(m2);
      for (Matrix c1 : cases1)
      {
        for (Matrix c2 : cases2)
        {
          Matrix target = c1.copyOf();
          MatrixOps.addAssign(target, c2);
          Assert.assertTrue(MatrixOps.equivalent(expected, target));
        }
      }
    }
  }

  @Test(expectedExceptions = WriteAccessException.class)
  public void testAddAssign_GenericType_Matrix_WriteAccessException() throws Exception
  {
    MatrixTestReadOnly ro = new MatrixTestReadOnly(tg.newMatrix(2, 2));
    MatrixOps.addAssign(ro, tg.newMatrix(2, 2));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAddAssign_GenericType_Matrix_SizeException() throws Exception
  {
    MatrixOps.addAssign(tg.newMatrix(2, 2), tg.newMatrix(3, 3));
  }

  /**
   * Test of add method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAdd() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      MatrixColumnArray m1 = tg.newMatrix(1, 10, 1, 10);
      MatrixColumnArray m2 = tg.newMatrix(m1.rows(), m1.columns());
      double[] v1 = DoubleArray.addAssign(DoubleArray.copyOf(m1.toArray()),
              m2.toArray());
      MatrixColumnArray expected = new MatrixColumnArray(v1, m1.rows(), m1.columns());

      Matrix[] cases1 = tg.eachType(m1);
      Matrix[] cases2 = tg.eachType(m2);
      for (Matrix c1 : cases1)
      {
        Matrix m3 = null;
        Matrix b = null;
        try
        {
          for (Matrix c2 : cases2)
          {
            b = c2;
            m3 = MatrixOps.add(c1, c2);
            Assert.assertTrue(MatrixOps.equivalent(expected, m3));
            Assert.assertTrue(MatrixOps.equivalent(c1, m1));
            Assert.assertTrue(MatrixOps.equivalent(c2, m2));
          }
        }
        catch (AssertionError ex)
        {
          System.out.println("A:");
          MatrixOps.dump(System.out, c1);
          System.out.println("B:");
          MatrixOps.dump(System.out, b);
          System.out.println("R:");
          MatrixOps.dump(System.out, m3);
          System.out.println("E:");
          MatrixOps.dump(System.out, expected);
          throw ex;
        }
      }
    }
  }

  @Test(expectedExceptions = SizeException.class)
  public void testAdd_SizeException() throws Exception
  {
    MatrixOps.add(tg.newMatrix(2, 2), tg.newMatrix(3, 3));
  }

//</editor-fold>
//<editor-fold desc="subtract">   
  /**
   * Test of subtractAssign method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testSubtractAssign_GenericType_Matrix() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      MatrixColumnArray m1 = tg.newMatrix(1, 10, 1, 10);
      MatrixColumnArray m2 = tg.newMatrix(m1.rows(), m1.columns());
      double[] v1 = DoubleArray.subtractAssign(DoubleArray.copyOf(m1.toArray()), m2.toArray());
      MatrixColumnArray expected = new MatrixColumnArray(v1, m1.rows(), m1.columns());

      Matrix[] cases1 = tg.eachType(m1);
      Matrix[] cases2 = tg.eachType(m2);
      for (Matrix c1 : cases1)
      {
        for (Matrix c2 : cases2)
        {
          Matrix target = c1.copyOf();
          MatrixOps.subtractAssign(target, c2);
          Assert.assertTrue(MatrixOps.equivalent(expected, target));
        }
      }
    }
  }

  @Test(expectedExceptions = WriteAccessException.class)
  public void testSubtractAssign_GenericType_Matrix_WriteAccessException() throws Exception
  {
    MatrixTestReadOnly ro = new MatrixTestReadOnly(tg.newMatrix(2, 2));
    MatrixOps.subtractAssign(ro, tg.newMatrix(2, 2));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testSubtractAssign_GenericType_Matrix_SizeException() throws Exception
  {
    MatrixOps.subtractAssign(tg.newMatrix(2, 2), tg.newMatrix(3, 3));
  }

  /**
   * Test of subtract method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testSubtract() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      MatrixColumnArray m1 = tg.newMatrix(1, 10, 1, 10);
      MatrixColumnArray m2 = tg.newMatrix(m1.rows(), m1.columns());
      double[] v1 = DoubleArray.subtractAssign(DoubleArray.copyOf(m1.toArray()), m2.toArray());
      MatrixColumnArray expected = new MatrixColumnArray(v1, m1.rows(), m1.columns());

      Matrix[] cases1 = tg.eachType(m1);
      Matrix[] cases2 = tg.eachType(m2);
      for (Matrix c1 : cases1)
      {
        for (Matrix c2 : cases2)
        {
          Matrix m3 = MatrixOps.subtract(c1, c2);
          Assert.assertTrue(MatrixOps.equivalent(expected, m3));
          Assert.assertTrue(MatrixOps.equivalent(c1, m1));
          Assert.assertTrue(MatrixOps.equivalent(c2, m2));
        }
      }
    }
  }

  @Test(expectedExceptions = SizeException.class)
  public void testSubtract_SizeException() throws Exception
  {
    MatrixOps.subtract(tg.newMatrix(2, 2), tg.newMatrix(3, 3));
  }

  /**
   * Test of negateAssign method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testNegateAssign() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      MatrixColumnArray m1 = tg.newMatrix(1, 10, 1, 10);
      double[] v1 = DoubleArray.negateAssign(DoubleArray.copyOf(m1.toArray()));
      MatrixColumnArray expected = new MatrixColumnArray(v1, m1.rows(), m1.columns());

      Matrix[] cases1 = tg.eachType(m1);
      for (Matrix c1 : cases1)
      {
        Matrix m3 = MatrixOps.negateAssign(c1);
        Assert.assertTrue(MatrixOps.equivalent(expected, m3));
        Assert.assertTrue(MatrixOps.equivalent(expected, c1));
      }
    }
  }

  @Test(expectedExceptions = WriteAccessException.class)
  public void testNegateAssign_GenericType_Matrix_WriteAccessException() throws Exception
  {
    MatrixTestReadOnly ro = new MatrixTestReadOnly(tg.newMatrix(2, 2));
    MatrixOps.negateAssign(ro);
  }

  /**
   * Test of negateAssign method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testNegate() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      MatrixColumnArray m1 = tg.newMatrix(1, 10, 1, 10);
      double[] v1 = DoubleArray.negateAssign(DoubleArray.copyOf(m1.toArray()));
      MatrixColumnArray expected = new MatrixColumnArray(v1, m1.rows(), m1.columns());

      Matrix[] cases1 = tg.eachType(m1);
      for (Matrix c1 : cases1)
      {
        Matrix m3 = MatrixOps.negate(c1);
        Assert.assertTrue(MatrixOps.equivalent(expected, m3));
        Assert.assertTrue(MatrixOps.equivalent(m1, c1));
      }
    }
  }
//</editor-fold>
//<editor-fold desc="multiply"> 

  /**
   * Test of multiplyAssign method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMultiplyAssign() throws Exception
  {
    for (int i = 0; i < CASES; i++)
    {
      MatrixColumnArray m1 = tg.newMatrix(1, 10, 1, 10);
      double value = tg.rand.nextDouble();
      double[] v1 = DoubleArray.multiply(m1.toArray(), value);
      MatrixColumnArray expected = new MatrixColumnArray(v1, m1.rows(), m1.columns());

      Matrix[] cases1 = tg.eachType(m1);
      for (Matrix c1 : cases1)
      {
        Matrix target = c1.copyOf();
        MatrixOps.multiplyAssign(target, value);
        Assert.assertTrue(MatrixOps.equivalent(expected, target));
      }
    }
  }

  @Test(expectedExceptions = WriteAccessException.class)
  public void testMultiplyAssign_WriteAccessException() throws Exception
  {
    MatrixTestReadOnly ro = new MatrixTestReadOnly(tg.newMatrix(2, 2));
    MatrixOps.multiplyAssign(ro, 2);
  }

  /**
   * Test of multiplyAssignElements method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMultiplyAssignElements() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      MatrixColumnArray m1 = tg.newMatrix(1, 10, 1, 10);
      MatrixColumnArray m2 = tg.newMatrix(m1.rows(), m1.columns());
      double[] v1 = DoubleArray.multiplyAssign(DoubleArray.copyOf(m1.toArray()), m2.toArray());
      MatrixColumnArray expected = new MatrixColumnArray(v1, m1.rows(), m1.columns());

      Matrix[] cases1 = tg.eachType(m1);
      Matrix[] cases2 = tg.eachType(m2);
      for (Matrix c1 : cases1)
      {
        for (Matrix c2 : cases2)
        {
          Matrix target = c1.copyOf();
          MatrixOps.multiplyAssignElements(target, c2);
          Assert.assertTrue(MatrixOps.equivalent(expected, target));
        }
      }
    }
  }

  @Test(expectedExceptions = WriteAccessException.class)
  public void testMultiplyAssignElements_WriteAccessException() throws Exception
  {
    MatrixTestReadOnly ro = new MatrixTestReadOnly(tg.newMatrix(2, 2));
    MatrixOps.multiplyAssignElements(ro, tg.newMatrix(2, 2));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testMultiplyAssignElements_SizeException() throws Exception
  {
    MatrixOps.multiplyAssignElements(tg.newMatrix(2, 2), tg.newMatrix(3, 3));
  }

  /**
   * Test of multiply method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMultiply() throws Exception
  {
    //  rows*rows
    Matrix matrix1 = tg.newMatrix(5, 6, MatrixRowTable::new);
    Matrix matrix2 = tg.newMatrix(6, 5, MatrixRowTable::new);
    Matrix out = MatrixOps.multiply(matrix1, matrix2);
    for (int i = 0; i < matrix1.rows(); i++)
    {
      for (int j = 0; j < matrix2.columns(); j++)
      {
        assertEquals(out.get(i, j), DoubleArray.multiplyInner(matrix1.copyRow(i), matrix2.copyColumn(j)));
      }
    }

    // columns*rows    
    matrix1 = tg.newMatrix(5, 6, MatrixColumnTable::new);
    matrix2 = tg.newMatrix(6, 5, MatrixRowTable::new);
    out = MatrixOps.multiply(matrix1, matrix2);
    for (int i = 0; i < matrix1.rows(); i++)
    {
      for (int j = 0; j < matrix2.columns(); j++)
      {
        assertEquals(out.get(i, j), DoubleArray.multiplyInner(matrix1.copyRow(i), matrix2.copyColumn(j)));
      }
    }

    // column*row
    matrix1 = tg.newMatrix(5, 6, MatrixRowTable::new);
    matrix2 = tg.newMatrix(6, 5, MatrixColumnTable::new);
    out = MatrixOps.multiply(matrix1, matrix2);
    for (int i = 0; i < matrix1.rows(); i++)
    {
      for (int j = 0; j < matrix2.columns(); j++)
      {
        assertEquals(out.get(i, j), DoubleArray.multiplyInner(matrix1.copyRow(i), matrix2.copyColumn(j)));
      }
    }

    // column*column
    matrix1 = tg.newMatrix(5, 6, MatrixColumnTable::new);
    matrix2 = tg.newMatrix(6, 5, MatrixColumnTable::new);
    out = MatrixOps.multiply(matrix1, matrix2);
    for (int i = 0; i < matrix1.rows(); i++)
    {
      for (int j = 0; j < matrix2.columns(); j++)
      {
        assertEquals(out.get(i, j), DoubleArray.multiplyInner(matrix1.copyRow(i), matrix2.copyColumn(j)));
      }
    }

    matrix1 = tg.newMatrix(1, 5, MatrixRowTable::new);
    matrix2 = tg.newMatrix(5, 5, MatrixRowTable::new);
    out = MatrixOps.multiply(matrix1, matrix2);
    for (int i = 0; i < matrix1.rows(); i++)
    {
      for (int j = 0; j < matrix2.columns(); j++)
      {
        assertEquals(out.get(i, j), DoubleArray.multiplyInner(matrix1.copyRow(i), matrix2.copyColumn(j)));
      }
    }

    matrix1 = tg.newMatrix(5, 5, MatrixRowTable::new);
    matrix2 = tg.newMatrix(5, 1, MatrixRowTable::new);
    out = MatrixOps.multiply(matrix1, matrix2);
    for (int i = 0; i < matrix1.rows(); i++)
    {
      for (int j = 0; j < matrix2.columns(); j++)
      {
        assertEquals(out.get(i, j), DoubleArray.multiplyInner(matrix1.copyRow(i), matrix2.copyColumn(j)));
      }
    }

  }

  /**
   * Test of multiplyAssignRows method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMultiplyAssignRows() throws Exception
  {
  }

  @Test(expectedExceptions = WriteAccessException.class)
  public void testMultiplyAssignRows_WriteAccessException() throws Exception
  {
    MatrixTestReadOnly ro = new MatrixTestReadOnly(tg.newMatrix(2, 2));
    MatrixOps.multiplyAssignRows(ro, tg.newMatrix(2, 1));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testMultiplyAssignRows_SizeException() throws Exception
  {
    MatrixOps.multiplyAssignRows(tg.newMatrix(2, 2), tg.newMatrix(3, 1));
  }

  /**
   * Test of multiplyAssignColumns method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMultiplyAssignColumns() throws Exception
  {
  }

  @Test(expectedExceptions = WriteAccessException.class)
  public void testMultiplyAssignColumns_WriteAccessException() throws Exception
  {
    MatrixTestReadOnly ro = new MatrixTestReadOnly(tg.newMatrix(2, 2));
    MatrixOps.multiplyAssignColumns(ro, tg.newMatrix(2, 1));
  }

  @Test(expectedExceptions = SizeException.class)
  public void testMultiplyAssignColumns_SizeException() throws Exception
  {
    MatrixOps.multiplyAssignColumns(tg.newMatrix(2, 2), tg.newMatrix(3, 1));
  }

  /**
   * Test of multiplyVectorOuter method, of class MatrixOps.
   */
  @Test
  public void testMultiplyVectorTranspose2()
  {
  }

//</editor-fold>
//<editor-fold desc="divide">
  /**
   * Test of divideAssign method, of class MatrixOps.
   */
  @Test
  public void testDivideAssign_MatrixColumnAccess_double()
  {
  }

  /**
   * Test of divideAssign method, of class MatrixOps.
   */
  @Test
  public void testDivideAssign_MatrixRowAccess_double()
  {
  }

  /**
   * Test of divideAssign method, of class MatrixOps.
   */
  @Test
  public void testDivideAssign_Matrix_double()
  {
  }
//</editor-fold>
//<editor-fold desc="unsorted">  

  /**
   * Test of invert method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testInvert_Matrix() throws Exception
  {
  }

  /**
   * Test of invert method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testInvert_Matrix_Matrix() throws Exception
  {
  }

  /**
   * Test of norm1 method, of class MatrixOps.
   */
  @Test
  public void testNorm1()
  {
  }

  /**
   * Test of normColumns1 method, of class MatrixOps.
   */
  @Test
  public void testNormColumns1()
  {
  }

  /**
   * Test of normColumns2 method, of class MatrixOps.
   */
  @Test
  public void testNormColumns2()
  {
  }

  /**
   * Test of sumOfEachColumn method, of class MatrixOps.
   */
  @Test
  public void testSumOfEachColumn()
  {
    Matrix matrix = tg.newMatrix(1, 5, MatrixRowTable::new);
    Matrix out = MatrixOps.sumOfEachColumn(matrix);
    for (int i = 0; i < 5; ++i)
    {
      Assert.assertEquals(out.get(0, i), DoubleArray.sum(matrix.copyColumn(i)));
    }

    matrix = tg.newMatrix(1, 5, MatrixColumnTable::new);
    out = MatrixOps.sumOfEachColumn(matrix);
    for (int i = 0; i < 5; ++i)
    {
      Assert.assertEquals(out.get(0, i), DoubleArray.sum(matrix.copyColumn(i)));
    }
  }

  /**
   * Test of sumOfEachRow method, of class MatrixOps.
   */
  @Test
  public void testSumOfEachRow()
  {
    Matrix matrix = tg.newMatrix(5, 5, MatrixRowTable::new);
    Matrix out = MatrixOps.sumOfEachRow(matrix);
    for (int i = 0; i < 5; ++i)
    {
      Assert.assertEquals(out.get(i, 0), DoubleArray.sum(matrix.copyRow(i)), 0.0);
    }

    matrix = tg.newMatrix(5, 5, MatrixColumnTable::new);
    out = MatrixOps.sumOfEachRow(matrix);
    for (int i = 0; i < 5; ++i)
    {
      Assert.assertEquals(out.get(i, 0), DoubleArray.sum(matrix.copyRow(i)), 0.0);
    }
  }

  /**
   * Test of meanOfEachColumn method, of class MatrixOps.
   */
  @Test
  public void testMeanOfEachColumn()
  {
    Matrix matrix = tg.newMatrix(5, 5, MatrixRowTable::new);
    Matrix out = MatrixOps.meanOfEachColumn(matrix);
    for (int i = 0; i < 5; ++i)
    {
      Assert.assertEquals(out.get(0, i), DoubleArray.sum(matrix.copyColumn(i)) / 5);
    }

    matrix = tg.newMatrix(5, 5, MatrixColumnTable::new);
    out = MatrixOps.meanOfEachColumn(matrix);
    for (int i = 0; i < 5; ++i)
    {
      Assert.assertEquals(out.get(0, i), DoubleArray.sum(matrix.copyColumn(i)) / 5);
    }
  }

  /**
   * Test of meanOfEachRow method, of class MatrixOps.
   */
  @Test
  public void testMeanOfEachRow()
  {
    Matrix matrix = tg.newMatrix(5, 5, MatrixRowTable::new);
    Matrix out = MatrixOps.meanOfEachRow(matrix);
    for (int i = 0; i < 5; ++i)
    {
      Assert.assertEquals(out.get(i, 0), DoubleArray.sum(matrix.copyRow(i)) / 5);
    }

    matrix = tg.newMatrix(5, 5, MatrixColumnTable::new);
    out = MatrixOps.meanOfEachRow(matrix);
    for (int i = 0; i < 5; ++i)
    {
      Assert.assertEquals(out.get(i, 0), DoubleArray.sum(matrix.copyRow(i)) / 5);
    }
  }

  /**
   * Test of equivalent method, of class MatrixOps.
   */
  @Test
  public void testEquals()
  {
    MatrixColumnArray m1 = tg.newMatrix(5, 5);
    testEqualsCase(new MatrixColumnArray(m1), new MatrixColumnArray(m1));
    testEqualsCase(new MatrixColumnArray(m1), new MatrixColumnList(m1));
    testEqualsCase(new MatrixColumnArray(m1), new MatrixColumnTable(m1));
    testEqualsCase(new MatrixColumnArray(m1), new MatrixRowArray(m1));
    testEqualsCase(new MatrixColumnArray(m1), new MatrixRowList(m1));
    testEqualsCase(new MatrixColumnArray(m1), new MatrixRowTable(m1));
  }

  public void testEqualsCase(Matrix m1, Matrix m2)
  {
    try
    {
      Assert.assertTrue(MatrixOps.equivalent(m1, m2));
      int i = 1;
      for (int r = 0; r < m1.rows(); ++r)
      {
        for (int c = 0; c < m1.columns(); ++c)
        {
          m1.set(r, c, i);
          Assert.assertFalse(MatrixOps.equivalent(m1, m2));
          m2.set(r, c, i);
          Assert.assertTrue(MatrixOps.equivalent(m1, m2));
          i++;
        }
      }
    }
    catch (AssertionError ex)
    {
      System.out.println("M1:");
      MatrixOps.dump(System.out, m1);
      System.out.println("M2:");
      MatrixOps.dump(System.out, m2);
      throw ex;
    }
    catch (MathExceptions.WriteAccessException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Test of dump method, of class MatrixOps.
   */
  @Test
  public void testDump()
  {
    MatrixRowArray m1 = new MatrixRowArray(tg.newMatrix(3, 5));
    byte[] expected;
    {
      // Generate the correct answer
      double[] v1 = m1.toArray();
      ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
      PrintStream ps2 = new PrintStream(baos2);
      int k = 0;
      for (int i = 0; i < m1.rows(); ++i)
      {
        for (int j = 0; j < m1.columns(); ++j)
        {
          ps2.print(v1[k++] + " ");
        }
        ps2.println();
      }
      ps2.flush();
      expected = baos2.toByteArray();
    }

    testDumpCase(expected, new MatrixColumnArray(m1));
    testDumpCase(expected, new MatrixColumnList(m1));
    testDumpCase(expected, new MatrixColumnTable(m1));
    testDumpCase(expected, new MatrixRowArray(m1));
    testDumpCase(expected, new MatrixRowList(m1));
    testDumpCase(expected, new MatrixRowTable(m1));
  }

  @SuppressWarnings("UnusedAssignment")
  public static void testDumpCase(byte[] expected, Matrix m)
  {
    byte[] got = null;

    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      MatrixOps.dump(new PrintStream(baos), m);
      Assert.assertEquals(expected, got = baos.toByteArray());
    }
    catch (AssertionError ex)
    {
      System.out.println("Case: " + m.getClass().getSimpleName());
      System.out.println("Expected:");
      System.out.println(new String(expected));
      System.out.println("Got:");
      System.out.println(new String(got));
      System.out.println();
      throw ex;
    }
  }

  /**
   * Test of sumOfElementsSqr method, of class MatrixOps.
   */
  @Test
  public void testSumOfElementsSqr()
  {
  }

  /**
   * Test of sumOfElements method, of class MatrixOps.
   */
  @Test
  public void testSumOfElements()
  {
  }

  /**
   * Test of flatten method, of class MatrixOps.
   */
  @Test
  public void testFlatten()
  {
  }

  /**
   * Test of reshape method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testReshape() throws Exception
  {
  }

  /**
   * Test of determineVectorAccess1 method, of class MatrixOps.
   */
  @Test
  public void testDetermineElementAccess()
  {
  }
//</editor-fold>

  /**
   * Test of determineVectorAccess1 method, of class MatrixOps.
   */
  @Test
  public void testDetermineElementAccess_Matrix()
  {
  }

  /**
   * Test of determineVectorAccess1 method, of class MatrixOps.
   */
  @Test
  public void testDetermineElementAccess_Matrix_Matrix()
  {
  }

  /**
   * Test of divideAssign method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testDivideAssign_GenericType_double() throws Exception
  {
  }

  /**
   * Test of divideAssign method, of class MatrixOps.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testDivideAssign() throws Exception
  {
  }

  /**
   * Test of addAssignScaled method, of class MatrixOps.
   */
  @Test
  public void testAddAssignScaled()
  {
  }

  /**
   * Test of subtractAssign method, of class MatrixOps.
   */
  @Test
  public void testSubtractAssign()
  {
  }

  /**
   * Test of multiply method, of class MatrixOps.
   */
  @Test
  public void testMultiply_doubleArr_Matrix()
  {
    Matrix m1 = tg.newMatrix(5, 5);
    double[] v1 = tg.newArray(5);

    double[] a1 = MatrixOps.multiply(v1, new MatrixColumnTable(m1));
    double[] a2 = MatrixOps.multiply(v1, new MatrixRowTable(m1));
    double[] a3 = MatrixOps.multiply(v1, new MatrixTestProxy(m1));
    System.out.println(DoubleArray.toString(a1));
    System.out.println(DoubleArray.toString(a2));
    System.out.println(DoubleArray.toString(a3));
    Assert.assertTrue(DoubleArray.equivalent(a1, a2));
    Assert.assertTrue(DoubleArray.equivalent(a1, a3));
  }

  /**
   * Test of multiply method, of class MatrixOps.
   */
  @Test
  public void testMultiply_Matrix_doubleArr()
  {
    Matrix m1 = tg.newMatrix(5, 5);
    double[] v1 = tg.newArray(5);
    double[] a1 = MatrixOps.multiply(new MatrixColumnTable(m1), v1);
    double[] a2 = MatrixOps.multiply(new MatrixRowTable(m1), v1);
    double[] a3 = MatrixOps.multiply(new MatrixTestProxy(m1), v1);
    System.out.println(DoubleArray.toString(a1));
    System.out.println(DoubleArray.toString(a2));
    System.out.println(DoubleArray.toString(a3));
    Assert.assertTrue(DoubleArray.equivalent(a1, a2));
    Assert.assertTrue(DoubleArray.equivalent(a1, a3));
  }

  /**
   * Test of multiply method, of class MatrixOps.
   */
  @Test
  public void testMultiply_Matrix_Matrix()
  {
  }

  /**
   * Test of multiplyVectorOuter method, of class MatrixOps.
   */
  @Test
  public void testMultiplyVectorOuter_Matrix_Matrix()
  {
  }

  /**
   * Test of multiplyVectorOuter method, of class MatrixOps.
   */
  @Test
  public void testMultiplyVectorOuter_doubleArr_doubleArr()
  {
  }

  /**
   * Test of multiplyHermitian method, of class MatrixOps.
   */
  @Test
  public void testMultiplyHermitian_3args_1()
  {
  }

  /**
   * Test of multiplyHermitian method, of class MatrixOps.
   */
  @Test
  public void testMultiplyHermitian_3args_2()
  {
  }

  /**
   * Test of multiplyHermitian method, of class MatrixOps.
   */
  @Test
  public void testMultiplyHermitian_3args_3()
  {
  }

  /**
   * Test of invert method, of class MatrixOps.
   */
  @Test
  public void testInvert_GenericType_Matrix()
  {
  }

  /**
   * Test of divideLeft method, of class MatrixOps.
   */
  @Test
  public void testDivideLeft()
  {
  }

  /**
   * Test of divideRight method, of class MatrixOps.
   */
  @Test
  public void testDivideRight()
  {
  }

  /**
   * Test of dumpIterator method, of class MatrixOps.
   */
  @Test
  public void testDumpIterator()
  {
  }

  /**
   * Test of dumpIteratorContent method, of class MatrixOps.
   */
  @Test
  public void testDumpIteratorContent()
  {
  }

  /**
   * Test of determineVectorAccess1 method, of class MatrixOps.
   */
  @Test
  public void testDetermineVectorAccess1()
  {
  }

  /**
   * Test of deterimineVectorAccess2 method, of class MatrixOps.
   */
  @Test
  public void testDeterimineVectorAccess2()
  {
  }

  /**
   * Test of toArray method, of class MatrixOps.
   */
  @Test
  public void testToArray()
  {
  }

  /**
   * Test of findExtrema method, of class MatrixOps.
   */
  @Test
  public void testFindExtrema()
  {
  }

  /**
   * Test of findMaximumElement method, of class MatrixOps.
   */
  @Test
  public void testFindMaximumElement()
  {
  }

  /**
   * Test of findMinimumElement method, of class MatrixOps.
   */
  @Test
  public void testFindMinimumElement()
  {
  }

}
