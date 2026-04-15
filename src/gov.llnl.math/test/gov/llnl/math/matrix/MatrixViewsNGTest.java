/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import java.util.Random;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * Test code for MatrixViews.
 */
public class MatrixViewsNGTest
{
  /**
   * Test of selectColumn method, of class MatrixViews.
   */
  @Test
  public void testSelectColumn()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixRowTable(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    for (int i = 0; i < 5; i++)
    {
      // Create a view
      Matrix result = MatrixViews.selectColumn(matrix, i);
      
      // Verify that the view matches with read access
      Matrix expResult = MatrixFactory.newColumnMatrix(matrix.copyColumn(i));
      assertEquals(result, expResult);
      
      // Verify changes are applied to the original
      MatrixOps.fill(result, 0);
      assertEquals(matrix.copyColumn(i), new double[5]);
    }
  }

  /**
   * Test of selectColumns method, of class MatrixViews.
   */
  @Test
  public void testSelectColumns()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixRowTable(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix result = MatrixViews.selectColumns(matrix, 1, 2);
    assertEquals(result.copyColumn(0), matrix.copyColumn(1));
    assertEquals(result.copyColumn(1), matrix.copyColumn(2));
    MatrixOps.fill(result, 0);
    assertEquals(matrix.copyColumn(1), new double[5]);
    assertEquals(matrix.copyColumn(2), new double[5]);
  }

  /**
   * Test of selectColumnRange method, of class MatrixViews.
   */
  @Test
  public void testSelectColumnRange()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixRowTable(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix result = MatrixViews.selectColumnRange(matrix, 1, 3);
    assertEquals(result.copyColumn(0), matrix.copyColumn(1));
    assertEquals(result.copyColumn(1), matrix.copyColumn(2));
    MatrixOps.fill(result, 0.0);
    assertEquals(matrix.copyColumn(1), new double[5]);
    assertEquals(matrix.copyColumn(2), new double[5]);
  }

  /**
   * Test of selectRow method, of class MatrixViews.
   */
  @Test
  public void testSelectRow()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixRowTable(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    for (int i = 0; i < 5; i++)
    {
      // Create a view
      Matrix result = MatrixViews.selectRow(matrix, i);
      
      // Verify that the view matches with read access
      Matrix expResult = MatrixFactory.newRowMatrix(matrix.copyRow(i));
      assertEquals(result, expResult);
      
      // Verify changes are applied to the original
      MatrixOps.fill(result, 0.0);
      assertEquals(matrix.copyRow(i), new double[5]);
    }
  }

  /**
   * Test of selectRows method, of class MatrixViews.
   */
  @Test
  public void testSelectRows()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixRowTable(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix result = MatrixViews.selectRows(matrix, 1, 2);
    assertEquals(result.copyRow(0), matrix.copyRow(1));
    assertEquals(result.copyRow(1), matrix.copyRow(2));
    MatrixOps.fill(result, 0);
    assertEquals(matrix.copyRow(1), new double[5]);
    assertEquals(matrix.copyRow(2), new double[5]);
  }

  /**
   * Test of selectRowRange method, of class MatrixViews.
   */
  @Test
  public void testSelectRowRange()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixRowTable(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix result = MatrixViews.selectRowRange(matrix, 1, 3);
    assertEquals(result.copyRow(0), matrix.copyRow(1));
    assertEquals(result.copyRow(1), matrix.copyRow(2));
    MatrixOps.fill(result, 0);
    assertEquals(matrix.copyRow(1), new double[5]);
    assertEquals(matrix.copyRow(2), new double[5]);
  }

  /**
   * Test of select method, of class MatrixViews.
   */
  @Test
  public void testSelect()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixRowTable(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix result = MatrixViews.select(matrix, 1, 4, 1, 4);
    for (int i =0; i<3; ++i)
      for (int j =0; j<3; ++j)
          assertEquals(result.get(i,j), matrix.get(i+1, j+1));
    MatrixOps.fill(result, 0);
    for (int i =0; i<3; ++i)
      for (int j =0; j<3; ++j)
          assertEquals(0.0, matrix.get(i+1, j+1));
  }

  /**
   * Test of diagonal method, of class MatrixViews.
   */
  @Test
  public void testDiagonal()
  {
    Random random = new Random();
    random.setSeed(1);
    Matrix matrix = new MatrixRowTable(5, 5);
    MatrixOps.fill(matrix, random::nextDouble);
    Matrix result = MatrixViews.diagonal(matrix);
    assertEquals(result.rows(),5);
    assertEquals(result.columns(),1);
    for (int i=0;i<5;i++)
      assertEquals(matrix.get(i,i), result.get(i,0), 0.0);
    MatrixOps.fill(result, 0);
    for (int i=0;i<5;i++)
      assertEquals(matrix.get(i,i), 0.0);
  }

  /**
   * Test of duplicateRowVector method, of class MatrixViews.
   */
  @Test
  public void testDuplicateRowVector()
  {
    Matrix.RowAccess vector = MatrixFactory.newRowMatrix(new double[]{1,2,3,4});
    Matrix.RowReadAccess expResult = MatrixFactory.newRowMatrix(
            new double[]{1,2,3,4}, 
            new double[]{1,2,3,4}, 
            new double[]{1,2,3,4});
    Matrix.RowReadAccess result = MatrixViews.duplicateRowVector(vector, 3);
    assertEquals(result, expResult);
  }

}
