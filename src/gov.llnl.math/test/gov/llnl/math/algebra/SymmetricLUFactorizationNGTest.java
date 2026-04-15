/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.algebra;

import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.Matrix.Triangular;
import gov.llnl.math.matrix.Matrix.SymmetricAccess;
import gov.llnl.math.matrix.MatrixOps;
import gov.llnl.math.matrix.special.MatrixSymmetric;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for SymmetricLUFactorization.
 */
public class SymmetricLUFactorizationNGTest
{

  public SymmetricLUFactorizationNGTest()
  {
  }

  /**
   * Create a symmetric positive definite matrix for testing.
   */
  private Matrix createSymmetricPositiveDefiniteMatrix(int n)
  {
    // Create a matrix with positive values on the diagonal
    Matrix A = MatrixFactory.newMatrix(n, n);

    // Fill with random values, then make it symmetric positive definite
    for (int i = 0; i < n; i++)
    {
      for (int j = 0; j < n; j++)
      {
        double val = (i+1.0)/(j+2.0); // Deterministic value for testing
        A.set(i, j, val);
      }
    }

    // Create a symmetric positive definite matrix by A^T*A + n*I
    Matrix result = MatrixOps.multiply(A.transpose(), A);
  
    // Add n*I to ensure positive definiteness
    for (int i = 0; i < n; i++)
    {
      result.set(i, i, result.get(i, i) + n);
    }

    return result;
  }

  /**
   * Test of decompose method.
   */
  @Test
  public void testDecompose()
  {
    // Create a symmetric positive definite matrix
    int n = 3;
    Matrix A = createSymmetricPositiveDefiniteMatrix(n);
    SymmetricAccess SA = new MatrixSymmetric(A);

    // Create Cholesky factorization
    SymmetricLUFactorization instance = new SymmetricLUFactorization();
    instance.decompose(SA);

    // Check dimensions of L and U
    Triangular L = instance.getL();
    Triangular U = instance.getU();
    assertEquals(L.rows(), n);
    assertEquals(L.columns(), n);
    assertEquals(U.rows(), n);
    assertEquals(U.columns(), n);

    // Check that L * U = A (approximately)
    Matrix LU = MatrixOps.multiply(L, U);
    for (int i = 0; i < n; i++)
    {
      for (int j = 0; j < n; j++)
      {
        assertEquals(LU.get(i, j), A.get(i, j), 1e-10);
      }
    }

    // Check that L is lower triangular
    for (int i = 0; i < n; i++)
    {
      for (int j = i+1; j < n; j++)
      {
        assertEquals(L.get(i, j), 0.0, 1e-12);
      }
    }

    // Check that U is upper triangular
    for (int i = 1; i < n; i++)
    {
      for (int j = 0; j < i; j++)
      {
        assertEquals(U.get(i, j), 0.0, 1e-12);
      }
    }

    // Check that L has non-zero diagonal elements
    for (int i = 0; i < n; i++)
    {
      assertTrue(Math.abs(L.get(i, i)) > 0.0);
    }

    // Check that U has ones on the diagonal (property of Cholesky L*U)
    for (int i = 0; i < n; i++)
    {
      assertEquals(U.get(i, i), 1.0, 1e-12);
    }
  }

  /**
   * Test of solve method.
   */
  @Test
  public void testSolve()
  {
    // Create a symmetric positive definite matrix
    int n = 3;
    Matrix A = createSymmetricPositiveDefiniteMatrix(n);
    SymmetricAccess SA = new MatrixSymmetric(A);

    // Create a known vector c
    double[] c = {1.0, 2.0, 3.0};

    // Calculate b = A*c so we know the expected solution
    double[] b = new double[n];
    for (int i = 0; i < n; i++)
    {
      double sum = 0.0;
      for (int j = 0; j < n; j++)
      {
        sum += A.get(i, j) * c[j];
      }
      b[i] = sum;
    }

    // Solve Ax = b using Cholesky factorization
    SymmetricLUFactorization instance = new SymmetricLUFactorization();
    instance.decompose(SA);
    double[] x = instance.solve(b);

    // Verify that x = c (approximately)
    for (int i = 0; i < n; i++)
    {
      assertEquals(x[i], c[i], 1e-10);
    }
  }

  /**
   * Test of accessors.
   */
  @Test
  public void testAccessors()
  {
    int n = 3;
    Matrix A = createSymmetricPositiveDefiniteMatrix(n);
    SymmetricAccess SA = new MatrixSymmetric(A);

    SymmetricLUFactorization instance = new SymmetricLUFactorization();
    instance.decompose(SA);

    // Test getSize
    assertEquals(instance.getSize(), n);

    // Test getPermutation and isPermuted
    int[] permutation = instance.getPermutation();
    assertNotNull(permutation);
    assertEquals(permutation.length, n);

    // Test getNullity
    assertEquals(instance.getNullity(), 0); // Should be 0 for a positive definite matrix

    // Test getTolerance and setTolerance
    double defaultTolerance = SymmetricLUFactorization.DEFAULT_TOLERANCE;
    assertEquals(instance.getTolerance(), defaultTolerance);

    double newTolerance = 1e-10;
    instance.setTolerance(newTolerance);
    assertEquals(instance.getTolerance(), newTolerance);
  }

  /**
   * Test with a nearly singular matrix.
   */
  @Test
  public void testNearlySingular()
  {
    int n = 3;
    Matrix A = MatrixFactory.newMatrix(n, n);

    // Create a nearly singular symmetric matrix
    A.set(0, 0, 1.0);
    A.set(0, 1, 0.0);
    A.set(0, 2, 0.0);
    A.set(1, 0, 0.0);
    A.set(1, 1, 1.0);
    A.set(1, 2, 0.9999999999999999); // High correlation
    A.set(2, 0, 0.0);
    A.set(2, 1, 0.9999999999999999);
    A.set(2, 2, 1.0);

    SymmetricAccess SA = new MatrixSymmetric(A);

    SymmetricLUFactorization instance = new SymmetricLUFactorization();
    instance.decompose(SA);

    // Either the nullity should be > 0 or the matrix should be permuted
    // depending on how the factorization handled the near-singularity
    assertTrue(instance.getNullity() > 0 || instance.isPermuted());
  }
}