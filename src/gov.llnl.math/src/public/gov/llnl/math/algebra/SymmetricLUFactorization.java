/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.algebra;

import gov.llnl.math.MathAssert;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixRowOperations;
import gov.llnl.math.matrix.special.MatrixTriangularColumn;
import gov.llnl.math.matrix.special.MatrixTriangularRow;

/**
   * Factorize a positive semidefinite matrix for solving.
   *
   * This implementation performs a Symmetric Crout LU decomposition 
   * (mathematically equivalent to $LDL^T$) using diagonal pivoting. 
   * Unlike a Cholesky decomposition, this approach does not require 
   * the matrix to be positive-definite and can handle semi-definite 
   * or indefinite matrices.
   */
public class SymmetricLUFactorization
{

  public final static double DEFAULT_TOLERANCE = 1e-15;

  double tolerance = DEFAULT_TOLERANCE;
  int size;
  Matrix.Triangular L;
  Matrix.Triangular U;
  int[] permutation;
  boolean permuted;
  int nullity;

  /**
   * Solve the problem of Ax=c where A is a symmetric matrix.
   *
   * This is a divide left operation. 
   *
   * @param c
   * @return
   */
  public double[] solve(double[] c)
  {
    // Check sizes
    MathAssert.assertLengthEqual(c, size, "vector size incorrect");

    // Copy to preserve original
    double[] out = permuteFrom(c, permutation);

    // Use backsubstitution to solve
    MatrixRowOperations ro = MatrixFactory.createRowOperations(out);
    L.divideLeft(ro);
    U.divideLeft(ro);

    if (permuted)
      return permuteTo(out, permutation);
    return out;
  }

  // Destructive
  public SymmetricLUFactorization decompose(Matrix.SymmetricAccess A)
  {
    int n = A.rows();

    this.permuted = false;
    this.nullity = 0;
    this.size = n;

    int[] perm = new int[n];
    double[][] Lv = new double[n][n];
    double[][] Uv = new double[n][n];
    // Enhancement keep only on LUv matrix and then split into views.

    for (int j = 0; j < n; ++j)
      perm[j] = j;

    // Decompose A into L*U
    // Because the diagonal of U is 1, we can store both L and U in the same
    // matrix.
    for (int j = 0; j < n; ++j)
    {
      double[] Lrj = Lv[j];
      double[] Ucj = Uv[j];
      double[] Acj = A.accessColumn(perm[j]);

      double Acjj = Acj[perm[j]];
      double s = 0;
      {
        for (int k = 0; k < j; ++k)
          s += Lrj[k] * Ucj[k];                                               // L[j,k]*U[k,j]
      }
      double alpha = Acjj - s;                                                // A[j,j]

      int jb = j;
      if (Math.abs(alpha) < Math.abs(Acjj * 1e-5))
      {
        // Attempt a pivot to find a better solution
        double quality = Math.abs(alpha);
        for (int q = j + 1; q < n; ++q)
        {
          double[] Lrq = Lv[q];
          double[] Ucq = Uv[q];
          double[] Acq = A.accessColumn(perm[q]);

          double s2 = 0;
          {
            for (int k = 0; k < j; ++k)
              s2 += Lrq[k] * Ucq[k];                                          // L[j,k]*U[k,j]
          }
          double alpha2 = Acq[perm[q]] - s2;
          double quality2 = Math.abs(alpha2);

          if (quality2 > quality)
          {
            jb = q;
            quality = quality2;
            alpha = alpha2;
          }
        }

        if (j != jb)
        {
          // swap from this point forward
          int pj = perm[j];
          perm[j] = perm[jb];
          perm[jb] = pj;
          permuted = true;

          double[] u = Lv[j];
          Lv[j] = Lv[jb];
          Lv[jb] = u;

          u = Uv[j];
          Uv[j] = Uv[jb];
          Uv[jb] = u;

          // Operate on permuted result
          Lrj = Lv[j];
          Ucj = Uv[j];
          Acj = A.accessColumn(perm[j]);
        }
      }

      // If we are still 0 then we have hit the end of our calculation.
      if (Math.abs(alpha) < Math.abs(Acj[perm[j]] * tolerance))
      {
        nullity++;
        Lrj[j] = 0;                                                           // L[j,j]
        Ucj[j] = 1;                                                           // U[j,j]

        for (int i = j + 1; i < n; ++i)
        {
          Lv[i][j] = 0;                                                       // L[i,j]
          Uv[i][j] = 0;                                                       // U[j,i]
        }
        continue;
      }

      Ucj[j] = 1;                                                             // U[j,j]
      Lrj[j] = alpha;                                                         // L[j,j]

      for (int i = j + 1; i < n; ++i)
      {
        double s2 = 0;
        double[] Uvi = Uv[i];
        double[] Lri = Lv[i];
        for (int k = 0; k < j; ++k)
          s2 += Lri[k] * Ucj[k];                                              // L[i,k]*U[k,j]

        double beta = Acj[perm[i]] - s2;                                      // A[i,j]
        Lri[j] = beta / Ucj[j];                                               // L[i,j]
        Uvi[j] = beta / Lrj[j];                                               // U[j,i]
      }
    }

    this.L = new MatrixTriangularRow(Lv, false);
    this.U = new MatrixTriangularColumn(Uv, true);
    this.permutation = perm;
    return this;
  }

//<editor-fold desc="accessors" defaultstate="collapsed">
  /**
   * @return the size
   */
  public int getSize()
  {
    return size;
  }

  /**
   * @return the L
   */
  public Matrix.Triangular getL()
  {
    return L;
  }

  /**
   * @return the U
   */
  public Matrix.Triangular getU()
  {
    return U;
  }

  /**
   * @return the permutation
   */
  public int[] getPermutation()
  {
    return permutation;
  }

  /**
   * @return the permuted
   */
  public boolean isPermuted()
  {
    return permuted;
  }

  /**
   * @return the nullity
   */
  public int getNullity()
  {
    return nullity;
  }

  /**
   * @return the tolerance
   */
  public double getTolerance()
  {
    return tolerance;
  }

  /**
   * @param tolerance the tolerance to set
   */
  public void setTolerance(double tolerance)
  {
    this.tolerance = tolerance;
  }
//</editor-fold>
//<editor-fold desc="internal" defaultstate="collapsed">

  static double[] permuteFrom(double[] c, int[] perm)
  {
    int n = c.length;
    double[] out = new double[c.length];
    for (int i = 0; i < n; ++i)
      out[i] = c[perm[i]];
    return out;
  }

  static double[] permuteTo(double[] c, int[] perm)
  {
    int n = c.length;
    double[] out = new double[c.length];
    for (int i = 0; i < n; ++i)
      out[perm[i]] = c[i];
    return out;
  }
//</editor-fold>
}
