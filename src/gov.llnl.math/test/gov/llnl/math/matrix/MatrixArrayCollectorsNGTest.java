/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import java.util.stream.Stream;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for MatrixArrayCollectors.
 */
public class MatrixArrayCollectorsNGTest
{
  
  public MatrixArrayCollectorsNGTest()
  {
  }
  
  /**
   * Test constructor of class MatrixArrayCollectors
   */
  @Test
  public void testConstructor()
  {
    MatrixArrayCollectors instance = new MatrixArrayCollectors();
  }

  /**
   * Test of sum method, of class MatrixArrayCollectors.
   */
  @Test
  public void testSum_0args()
  {
    Matrix a = MatrixFactory.createFromArray(new double[]{0,1,2,3}, 2, 2);
    Matrix b = MatrixFactory.createFromArray(new double[]{3,2,1,0}, 2, 2);
    Matrix c = MatrixFactory.createFromArray(new double[]{3,3,3,3}, 2, 2);
    Matrix result = Stream.of(a,b).collect(MatrixArrayCollectors.sum());
    assertEquals(result, c);
  }

  /**
   * Test of sum method, of class MatrixArrayCollectors.
   */
  @Test
  public void testSum_Supplier()
  {
    Matrix a = MatrixFactory.createFromArray(new double[]{0,1,2,3}, 2, 2);
    Matrix b = MatrixFactory.createFromArray(new double[]{3,2,1,0}, 2, 2);
    Matrix c = MatrixFactory.createFromArray(new double[]{3,3,3,3}, 2, 2);
    MatrixRowTable result = Stream.of(a,b).collect(MatrixArrayCollectors.sum(MatrixRowTable::new));
    assertEquals(result, c);
  }

  /**
   * Test of hcat method, of class MatrixArrayCollectors.
   */
  @Test
  public void testHcat()
  {
    Matrix a = MatrixFactory.createFromArray(new double[]{0,1,2,3}, 2, 2);
    Matrix b = MatrixFactory.createFromArray(new double[]{3,2,1,0}, 2, 2);
    Matrix expResult = MatrixFactory.createFromArray(new double[]{0,1,2,3,3,2,1,0}, 2, 4);
    Matrix result = Stream.of(a,b).collect(MatrixArrayCollectors.hcat());
    assertEquals(result, expResult);
  }
  
}
