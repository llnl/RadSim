/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.algebra;

import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixAssert;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixOpMultiply;
import gov.llnl.math.matrix.MatrixOps;
import gov.llnl.math.matrix.MatrixViews;
import gov.llnl.math.matrix.special.MatrixTriangularColumn;
import gov.llnl.math.matrix.special.MatrixTriangularRow;
import java.util.function.Consumer;

/**
 * Compute Matrix square root using Denman–Beavers iteration.
 *
 * This is not guaranteed to converge but works very well for our shielding
 * calculations.
 *
 * This is not a very highly efficient method as we do not make use of iterative
 * methods to solve the inverse in the later steps.
 *
 * This works best on upper or lower triagonal matrics.
 *
 * @author nelson85
 */
public class DenmanBeavers
{
  public Matrix.RowAccess Y;
  public Matrix.RowAccess Z;
  public Matrix nY;
  public Matrix nZ;
  public Matrix iY;
  public Matrix iZ;
  public Matrix m2;

  public Consumer<DenmanBeavers> audit = null;

  public boolean mask = true;
  public double error;

  /**
   * Compute the square root of a matrix if it exists.
   *
   * This will produce sqrt(M+lambda*I)
   *
   * @param m is the matrix to compute.
   * @param lambda is a number added to the diagonal to help with convergence.
   * @return
   */
  public Matrix compute(Matrix m, double lambda)
  {
    MatrixAssert.assertSquare(m);

    int n = m.columns();
    m2 = MatrixFactory.newRowMatrix(m);
    MatrixOps.addAssign(MatrixViews.diagonal(m2), lambda);

    // Allocate working memory
    Y = MatrixFactory.newRowMatrix(m2);
    Z = MatrixFactory.newRowMatrix(n, n);
    nY = MatrixFactory.newRowMatrix(n, n);
    nZ = MatrixFactory.newRowMatrix(n, n);
    iY = MatrixFactory.newRowMatrix(n, n);
    iZ = MatrixFactory.newRowMatrix(n, n);
    MatrixOps.fill(MatrixViews.diagonal(Z), 1);

    for (int i = 0; i < 40; i++)
    {
      nY.assign(Y);
      nZ.assign(Z);

      MatrixOps.invert(iY, nY);
      MatrixOps.invert(iZ, nZ);

      // Yn = 1/2 (Y + Z^-1)
      // Zn = 1/2 (Z + Y^-1)
      nY.assign(Y);
      nZ.assign(Z);

      MatrixOps.addAssign(nY, iZ);
      MatrixOps.addAssign(nZ, iY);

      MatrixOps.multiplyAssign(nY, 0.5);
      MatrixOps.multiplyAssign(nZ, 0.5);

      // This modification to the stock algorithm keeps our matrix valid for 
      // shielding calculations
      if (mask)
      {
        for (int i0 = 0; i0 < n; i0++)
          for (int i1 = 0; i1 < n; i1++)
            if (m2.get(i0, i1) == 0)
              nY.set(i0, i1, 0);
      }

      // Update the matrices      
      Y.assign(nY);
      Z.assign(nZ);

      MatrixOpMultiply.multiplyRowAccumulate(nZ, Y, Y);
      MatrixOps.subtractAssign(nZ, m2);
      this.error = MatrixOps.sumOfElementsSqr(nZ);
      if (error < 3e-15 * 3e-15)
        break;

      if (audit != null)
        audit.accept(this);
    }

    for (int i0 = 0; i0 < n; i0++)
      Y.set(i0, i0, Math.sqrt(m.get(i0, i0) + lambda));
    return Y;
  }

  /**
   * Correction step for triangular matrix sqrt to remove epsilon.
   *
   * Y = (2Q + Q^-1 P + P Q^-1)/4
   *
   * @param P is the matrix to compute sqrt (lower trigonal)
   * @param Q is the estimate of the sqrt
   * @return the corrected estimate.
   */
  public Matrix correct(Matrix P, Matrix.RowAccess Q)
  {
    // Right estimate
    MatrixTriangularColumn U = new MatrixTriangularColumn(Q.asRows(), true);
    Matrix P2 = MatrixFactory.newRowMatrix(P.transpose());
    U.divideLeft(MatrixFactory.createRowOperations(P2));
    P2 = MatrixFactory.newRowMatrix(P2.transpose());

    // Left estimate
    MatrixTriangularRow L = new MatrixTriangularRow(Q.asRows(), false);
    Matrix P3 = MatrixFactory.newRowMatrix(P);
    L.divideLeft(MatrixFactory.createRowOperations(P3));
    P3 = MatrixFactory.newRowMatrix(P3);

    // Average
    MatrixOps.addAssign(P2, P3);
    MatrixOps.multiplyAssign(P2, 0.5);
    
    // Make sure diagonal is correct
    for (int i = 0; i < P.columns(); ++i)
      P2.set(i, i, Math.sqrt(P.get(i, i)));
    
    MatrixOps.assignMaximumOf(P2, 0);
    return P2;
  }
}
