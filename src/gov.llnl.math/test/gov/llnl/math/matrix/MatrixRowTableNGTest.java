/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for MatrixRowTable.
 */
public class MatrixRowTableNGTest
{
  
  public MatrixRowTableNGTest()
  {
  }

  /**
   * Test of asColumns method, of class MatrixRowTable.
   */
  @Test
  public void testAsRows()
  {
    MatrixRowTable instance = new MatrixRowTable(3,2);
    double[][] expResult = new double[3][2];
    double[][] result = instance.asRows();
    assertEquals(result.length, 3);
    assertEquals(result[0], expResult[0]);
    assertEquals(result[1], expResult[1]);
  }

  /**
   * Test of accessColumn method, of class MatrixRowTable.
   */
  @Test
  public void testAccessRow()
  {
    int c = 0;
    MatrixRowTable instance = new MatrixRowTable(3,2);
    double[] expResult = null;
    double[] result = instance.accessRow(c);
    assertSame(result, instance.toArray()[c]);
  }

  /**
   * Test of addressColumn method, of class MatrixRowTable.
   */
  @Test
  public void testAddressRow()
  {
    int c = 0;
    MatrixRowTable instance = new MatrixRowTable();
    int expResult = 0;
    int result = instance.addressRow(c);
    assertEquals(result, expResult);
  }

  /**
   * Test of rows method, of class MatrixRowTable.
   */
  @Test
  public void testRows()
  {
    MatrixRowTable instance = new MatrixRowTable(4, 3);
    int expResult = 4;
    int result = instance.rows();
    assertEquals(result, expResult);
  }

  /**
   * Test of columns method, of class MatrixRowTable.
   */
  @Test
  public void testColumns()
  {
    MatrixRowTable instance = new MatrixRowTable(4,3);
    int result = instance.columns();
    assertEquals(result, 3);
  }

  /**
   * Test of set method, of class MatrixRowTable.
   */
  @Test
  public void testSet()
  {
    MatrixRowTable instance = new MatrixRowTable(3,2);
    instance.set(0, 0, 5);
    assertEquals(instance.get(0, 0), 5.0);
  }

  /**
   * Test of get method, of class MatrixRowTable.
   */
  @Test
  public void testGet()
  {
    MatrixRowTable instance = new MatrixRowTable(3,2);
    instance.set(0, 0, 5);
    assertEquals(instance.get(0, 0), 5.0, 0.0);
  }

  /**
   * Test of resize method, of class MatrixRowTable.
   */
  @Test
  public void testResize()
  {
    int rows = 4;
    int columns = 3;
    MatrixRowTable instance = new MatrixRowTable();
    boolean expResult = true;
    boolean result = instance.resize(rows, columns);
    assertEquals(result, expResult);
    assertEquals(instance.rows(), rows);
    assertEquals(instance.columns(), columns);
  }

  /**
   * Test of assign method, of class MatrixRowTable.
   */
  @Test
  public void testAssign()
  {
    Matrix matrix = MatrixFactory.newMatrix(3, 2);
    MatrixRowTable instance = new MatrixRowTable();
    Matrix result = instance.assign(matrix);
    assertEquals(result.rows(), 3);
    assertEquals(result.columns(), 2);
  }

  /**
   * Test of assignRow method, of class MatrixRowTable.
   */
  @Test
  public void testAssignColumn()
  {
    double[] in = new double[]{1,2,3,4,5};
    int index = 0;
    MatrixRowTable instance = new MatrixRowTable(5,2);
    instance.assignColumn(in, index);
    assertEquals(instance.copyColumn(index), in);
  }

  /**
   * Test of assignColumn method, of class MatrixRowTable.
   */
  @Test
  public void testAssignRow()
  {
    double[] in = new double[]{1,2,3};
    int index = 0;
    MatrixRowTable instance = new MatrixRowTable(2,3);
    instance.assignRow(in, index);
    assertEquals(instance.accessRow(index), in);
    assertNotSame(instance.accessRow(index), in);
  }

  /**
   * Test of copyRowTo method, of class MatrixRowTable.
   */
  @Test
  public void testCopyColumnTo()
  {
    double[] in = new double[]{1,2,3,4};
    double[] in2 = new double[]{5,6,7,8};
    MatrixRowTable instance = MatrixRowTable.cat(in,in2);
    double[] dest = new double[6];
    int offset = 1;
    int index = 2;
    double[] expResult = new double[]{0,3,7,0,0,0};
    double[] result = instance.copyColumnTo(dest, offset, index);
    assertEquals(result, expResult);
  }

  /**
   * Test of copyColumnTo method, of class MatrixRowTable.
   */
  @Test
  public void testCopyRowTo()
  {
    double[] in = new double[]{1,2,3,4};
    double[] in2 = new double[]{5,6,7,8};
    MatrixRowTable instance = MatrixRowTable.cat(in,in2);
    double[] dest = new double[10];
    int offset = 1;
    int index = 1;
    double[] expResult = new double[]{0,5,6,7,8, 0,0,0,0,0};
    double[] result = instance.copyRowTo(dest, offset, index);
    assertEquals(result, expResult);
  }

  /**
   * Test of copyOf method, of class MatrixRowTable.
   */
  @Test
  public void testCopyOf()
  {
    double[] in = new double[]{1,2,3,4};
    double[] in2 = new double[]{5,6,7,8};
    MatrixRowTable instance = MatrixRowTable.cat(in,in2);
    Matrix expResult = instance;
    Matrix result = instance.copyOf();
    assertEquals(result, expResult);
    assertNotSame(result, expResult);
  }

  /**
   * Test of toArray method, of class MatrixRowTable.
   */
  @Test
  public void testToArray()
  {
    double[][] expResult = new double[5][6];
    MatrixRowTable instance = new MatrixRowTable(expResult, expResult, 6);
    double[][] result = instance.toArray();
    assertSame(result, expResult);
  }

  /**
   * Test of cat method, of class MatrixRowTable.
   */
  @Test
  public void testCat()
  {
    double[] A = new double[]{1,2,3,4};
    MatrixRowTable result = MatrixRowTable.cat(A,A,A);
    assertEquals(result.accessRow(0), A);
    assertEquals(result.accessRow(1), A);
    assertEquals(result.accessRow(2), A);
  }

  /**
   * Test of equals method, of class MatrixRowTable.
   */
  @Test
  public void testEquals()
  {
    double[] in1 = new double[]{1,2,3,4};
    double[] in2 = new double[]{5,6,7,8};
    MatrixRowTable instance = MatrixRowTable.cat(in1, in2);
    Object o = new MatrixRowTable(instance);
    assertEquals(instance.equals(new Object()), false);
    assertEquals(instance.equals(o), true);
  }
 }
