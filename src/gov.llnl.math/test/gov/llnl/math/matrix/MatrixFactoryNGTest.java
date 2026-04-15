/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.MathExceptions.SizeException;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for MatrixFactory.
 */
public class MatrixFactoryNGTest
{

  public MatrixFactoryNGTest()
  {
  }
  
  /**
   * Test constructor of class MatrixFactory.
   */
  @Test
  public void testConstructor()
  {
    MatrixFactory instance = new MatrixFactory();
  }

  /**
   * Test of newMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewMatrix_Matrix()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix expResult = new MatrixRowTable(4, 4);
    MatrixOps.fill(expResult, random::nextDouble);
    Matrix result = MatrixFactory.newMatrix(expResult);
    assertEquals(result, expResult);
    assertNotSame(result, expResult);

    // Make sure we can alter the copy
    result.set(0, 0, 1);
    assertNotEquals(result, expResult);
  }

  /**
   * Test of newMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewMatrix_int_int()
  {
    int rows = 10;
    int columns = 5;
    Matrix expResult = new MatrixColumnTable(rows, columns);
    Matrix result = MatrixFactory.newMatrix(rows, columns);
    assertEquals(result, expResult);
  }

  /**
   * Test of newColumnMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewColumnMatrix_int_int()
  {
    int rows = 5;
    int columns = 3;
    Matrix.ColumnAccess expResult = new MatrixColumnTable(rows, columns);
    Matrix.ColumnAccess result = MatrixFactory.newColumnMatrix(rows, columns);
    assertEquals(result, expResult);
  }

  /**
   * Test of newColumnMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewColumnMatrix_doubleArrArr()
  {
    Random random = new Random();
    random.setSeed(1);
    double[][] columns = new double[5][3];
    Stream.of(columns).forEach(p -> DoubleArray.fill(p, random.nextDouble()));
    Matrix.ColumnAccess expResult = new MatrixColumnTable(columns, columns, columns[0].length);
    Matrix.ColumnAccess result = MatrixFactory.newColumnMatrix(columns);
    assertEquals(result, expResult);
  }

  /**
   * Test of newColumnMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewColumnMatrix_Collection()
  {
    Random random = new Random();
    random.setSeed(1);
    double[][] columns = new double[5][3];
    Stream.of(columns).forEach(p -> DoubleArray.fill(p, random.nextDouble()));
    Matrix.ColumnAccess expResult = new MatrixColumnTable(columns, columns, columns[0].length);
    Matrix.ColumnAccess result = MatrixFactory.newColumnMatrix(Arrays.asList(columns));
    assertEquals(result, expResult);
  }

  /**
   * Test of newRowMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewRowMatrix_Collection()
  {
    Random random = new Random();
    random.setSeed(1);
    double[][] columns = new double[5][3];
    Stream.of(columns).forEach(p -> DoubleArray.fill(p, random.nextDouble()));
    Matrix.RowAccess expResult = new MatrixRowTable(columns, columns, columns[0].length);
    Matrix.RowAccess result = MatrixFactory.newRowMatrix(Arrays.asList(columns));
    assertEquals(result, expResult);
  }

  /**
   * Test of newRowMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewRowMatrix_doubleArrArr()
  {
    Random random = new Random();
    random.setSeed(1);
    double[][] rows = new double[6][5];
    Stream.of(rows).forEach(p -> DoubleArray.fill(p, random::nextDouble));
    Matrix expResult = new MatrixRowTable(6, 5);
    for (int r = 0; r < 6; ++r)
      for (int c = 0; c < 5; ++c)
        expResult.set(r, c, rows[r][c]);
    Matrix.RowAccess result = MatrixFactory.newRowMatrix(rows);
    assertEquals(result, expResult);
  }

  /**
   * Test of newColumnMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewColumnMatrix_Matrix()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixRowTable(5, 6);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix.ColumnAccess expResult = new MatrixColumnTable();
    expResult.assign(matrix);
    Matrix.ColumnAccess result = MatrixFactory.newColumnMatrix(matrix);
    assertEquals(result, expResult);
  }

  /**
   * Test of newRowMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewRowMatrix_int_int()
  {
    int rows = 4;
    int columns = 3;
    Matrix.RowAccess expResult = new MatrixRowTable(rows, columns);
    Matrix.RowAccess result = MatrixFactory.newRowMatrix(rows, columns);
    assertEquals(result, expResult);
    rows = 1;
    expResult = new MatrixRowArray(rows, columns);
    result = MatrixFactory.newRowMatrix(rows, columns);
    assertEquals(result, expResult);
  }

  /**
   * Test of newRowMatrix method, of class MatrixFactory.
   */
  @Test
  public void testNewRowMatrix_Matrix()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixColumnTable(5, 3);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix.RowAccess expResult = new MatrixRowTable(5, 3);
    expResult.assign(matrix);
    Matrix.RowAccess result = MatrixFactory.newRowMatrix(matrix);
    assertEquals(result, expResult);
  }

  /**
   * Test of createRowVector method, of class MatrixFactory.
   */
  @Test
  public void testCreateRowVector()
  {
    // No test for deprecated method.
  }

  /**
   * Test of wrapRowVector method, of class MatrixFactory.
   */
  @Test
  public void testWrapRowVector()
  {
    // No test for deprecated function
  }

  /**
   * Test of createColumnVector method, of class MatrixFactory.
   */
  @Test
  public void testCreateColumnVector()
  {
    // No test for deprecated function
  }

  /**
   * Test of wrapColumnVector method, of class MatrixFactory.
   */
  @Test
  public void testWrapColumnVector()
  {
    // No test for deprecated function
  }

  /**
   * Test of wrapArray method, of class MatrixFactory.
   */
  @Test (expectedExceptions = {
    SizeException.class
  })
  public void testWrapArray()
  {
    double[] v = new double[30];
    Random random = new Random();
    random.setSeed(1);
    DoubleArray.fill(v, random::nextDouble);
    Matrix expResult = new MatrixRowTable(6, 5);
    int l = 0;
    for (int c = 0; c < 5; c++)
    {
      for (int r = 0; r < 6; r++)
      {
        expResult.set(r, c, v[l++]);
      }
    }
    Matrix result = MatrixFactory.wrapArray(v, 6, 5);
    assertEquals(result, expResult);
    
    result = MatrixFactory.wrapArray(v, 1, 1);
  }

  /**
   * Test of createFromArray method, of class MatrixFactory.
   */
  @Test
  public void testCreateFromArray_3args()
  {
    // No test for deprecated method
  }

  /**
   * Test of createFromArray method, of class MatrixFactory.
   */
  @Test
  public void testCreateFromArray_4args()
  {
    Random random = new Random();
    random.setSeed(1);
    double[] v = new double[30];
    DoubleArray.fill(v, random::nextDouble);
    MatrixFactory.MatrixNew supplier = MatrixRowTable::new;
    Matrix expResult = new MatrixRowTable(6, 5);
    int l = 0;
    for (int c = 0; c < 5; c++)
      for (int r = 0; r < 6; r++)
        expResult.set(r, c, v[l++]);
    MatrixRowTable result = MatrixFactory.createFromArray(v, 6, 5, MatrixRowTable::new);
    assertEquals(result, expResult);
  }

  /**
   * Test of createFromArray method, of class MatrixFactory.
   */
  @Test
  public void testCreateFromArray_doubleArrArr_boolean()
  {
    // No test for deprecated function
  }

  /**
   * Test of wrapFromArray method, of class MatrixFactory.
   */
  @Test
  public void testWrapFromArray()
  {
    // Deprecated method not tested.
  }

  /**
   * Test of wrapRows method, of class MatrixFactory.
   */
  @Test
  public void testWrapRows()
  {
    // No test for deprecated methods.
  }

  /**
   * Test of wrapColumns method, of class MatrixFactory.
   */
  @Test
  public void testWrapColumns()
  {
    // No test for deprecated methods.
  }

  /**
   * Test of asRowMatrix method, of class MatrixFactory.
   */
  @Test
  public void testAsRowMatrix()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = MatrixFactory.newColumnMatrix(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix.RowAccess result = MatrixFactory.asRowMatrix(matrix);
    for (int i = 0; i < 5; i++)
      assertEquals(result.accessRow(i), matrix.copyRow(i));
  }

  /**
   * Test of asColumnMatrix method, of class MatrixFactory.
   */
  @Test
  public void testAsColumnMatrix()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = MatrixFactory.newRowMatrix(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix.ColumnAccess result = MatrixFactory.asColumnMatrix(matrix);
    for (int i = 0; i < 5; ++i)
      assertEquals(result.accessColumn(i), matrix.copyColumn(i));
  }

  /**
   * Test of createRowOperations method, of class MatrixFactory.
   */
  @Test
  public void testCreateRowOperations_Matrix()
  {
    assertNotNull(MatrixFactory.createRowOperations(new MatrixColumnArray(5, 5)));
    assertNotNull(MatrixFactory.createRowOperations(new MatrixColumnTable(5, 5)));
    assertNotNull(MatrixFactory.createRowOperations(new MatrixRowArray(5, 5)));
    assertNotNull(MatrixFactory.createRowOperations(new MatrixRowTable(5, 5)));
  }

  /**
   * Test of createRowOperations method, of class MatrixFactory.
   */
  @Test
  public void testCreateRowOperations_doubleArr()
  {
    double[] v = new double[5];
    MatrixRowOperations result = MatrixFactory.createRowOperations(v);
    assertNotNull(result);
  }

}
