/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.algebra;

import gov.llnl.math.DoubleArray;
import static gov.llnl.math.DoubleUtilities.sqr;
import gov.llnl.math.MathExceptions;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixColumnArray;
import gov.llnl.math.matrix.MatrixColumnTable;
import gov.llnl.math.matrix.MatrixOps;
import static gov.llnl.math.matrix.MatrixOps.fill;
import static gov.llnl.math.matrix.MatrixOps.subtractAssign;
import gov.llnl.math.matrix.MatrixViews;
import static gov.llnl.math.matrix.MatrixViews.diagonal;

/**
 *
 * @author nelson85
 */
public class HouseHolderTransform
{
  int limit = -1;

  public Matrix[] svd(Matrix a, int steps)
  {
    int m = a.rows();
    int n = a.columns();

    // Create an identity matrix
    Matrix u = new MatrixColumnTable(n, n);
    fill(u, 0);
    fill(diagonal(u), 1);

    Matrix v = new MatrixColumnTable(m, m);
    fill(v, 0);
    fill(diagonal(v), 1);

    // Initialize the upper triangular matrix to the input
    Matrix s = new MatrixColumnTable(a);

    for (int i0 = 0; i0 < steps; i0++)
    {
      // QR decompose
      State state = new State();
      for (int i = 0; i < m; i++)
      {
        reduceColumn(u, s, i, i, n, m, state);
        state.sign = -state.sign;
      }

      // LQ decompose
      state.bmax = 0;
      for (int i = 0; i < n; i++)
      {
        reduceRow(s, v, i, i, n, m, state);
        state.sign = -state.sign;
      }

      // Apply tolerance
      double tolerance = state.bmax * state.epsilon;
      int count = 0;
      for (int j = 0; j < m; j++)
      {
        for (int i = j + 1; i < n; ++i)
        {
          double qq = s.get(i, j);
          if (qq == 0)
            continue;
          if (qq > tolerance || qq < -tolerance)
          {
            count++;
          }
          else
          {
            s.set(i, j, 0);
          }
        }
      }
      if (count == 0)
        break;
    }

    int min = Math.min(m, n);
    for (int i = 0; i < min; ++i)
    {
      if (s.get(i, i) < 0)
      {
        MatrixOps.negateAssign(MatrixViews.selectColumn(u, i));
        MatrixOps.negateAssign(MatrixViews.selectRow(s, i));
      }
    }

    return new Matrix[]
    {
      u, s, v
    };
  }

  /**
   * Compute a partial QR decomposition.
   *
   * @param a is the matrix to decompose.
   * @return
   */
  public Matrix[] decomposeQR(Matrix a)
  {
    // limit is used to stop the decomposition at a specific point for use in
    // debugging.

    try
    {
      int n = a.rows();
      int m = a.columns();
      int limit = this.limit;
      if (limit == -1)
        limit = Math.min(m, n);

      // Create an identity matrix
      Matrix q = new MatrixColumnTable(n, n);

      // Create identity matrix
      fill(q, 0);
      fill(diagonal(q), 1);

      // Initialize the upper triangular matrix to the input
      Matrix r = new MatrixColumnTable(a);

      State state = new State();
      for (int i = 0; i < limit; i++)
      {
        reduceColumn(q, r, i, i, n, m, state);
        state.sign = -state.sign;
      }
      return new Matrix[]
      {
        q, r
      };
    }

    // Promote exceptions caused by internal errors to runtime exceptions
    catch (MathExceptions.SizeException ex)
    {
      throw new RuntimeException("size issue a=" + a.rows() + "," + a.columns() + " limit=" + limit, ex); // cannot occur
    }
    catch (MathExceptions.ResizeException | MathExceptions.WriteAccessException ex)
    {
      throw new RuntimeException(ex); // cannot occur
    }

  }

  /**
   * Compute a partial QR decomposition.
   *
   * @param a is the matrix to decompose.
   * @return
   */
  public Matrix[] decomposeLQ(Matrix a)
  {
    // limit is used to stop the decomposition at a specific point for use in
    // debugging.

    try
    {
      int n = a.rows();
      int m = a.columns();
      int limit = this.limit;
      if (limit == -1)
        limit = Math.min(m, n);

      // Create an identity matrix
      Matrix q = new MatrixColumnTable(m, m);

      // Create identity matrix
      fill(q, 0);
      fill(diagonal(q), 1);

      // Initialize the upper triangular matrix to the input
      Matrix l = new MatrixColumnTable(a);
      State state = new State();
      for (int i = 0; i < limit; i++)
      {
        reduceRow(l, q, i, i, n, m, state);
        state.sign = -state.sign;
      }
      return new Matrix[]
      {
        l, q
      };
    }

    // Promote exceptions caused by internal errors to runtime exceptions
    catch (MathExceptions.SizeException ex)
    {
      throw new RuntimeException("size issue a=" + a.rows() + "," + a.columns() + " limit=" + limit, ex); // cannot occur
    }
    catch (MathExceptions.ResizeException | MathExceptions.WriteAccessException ex)
    {
      throw new RuntimeException(ex); // cannot occur
    }

  }

//<editor-fold desc="internal" defaultstate="collapsed">
  /**
   * Eliminate a portion of a column using householder transformation.
   *
   * Reduce first column of R(i..n, j..m)
   *
   * Assumes all columns to the left are already 0.
   *
   * @param q
   * @param r
   * @param i
   * @param j
   * @param n
   * @param m
   * @param state
   */
  static public void reduceColumn(Matrix q, Matrix r, int i, int j, int n, int m, State state)
  {
    // Check for completion of factorization
    if (i + 1 == n)
      return;

    // Householder reflection to eliminate this vector
    Matrix ur = MatrixViews.select(r, i, n, j, j + 1);
    double b = Math.sqrt(MatrixOps.sumOfElementsSqr(ur));
    state.bmax = Math.max(state.bmax, b);
    Matrix.ColumnAccess u = new MatrixColumnArray(ur);
    double[] uv = u.accessColumn(0);

    // Short cut check for zero if a vector is linear dependent
    double umax = DoubleArray.findMaximumAbsolute(uv);
    double tolerance = state.bmax * state.epsilon;
    if (umax < tolerance)
    {
      // Clear the column of values smaller the tolerance
      fill(ur, 0);

      // Clear the row of values smaller then tolerance
      for (int k = i; k < m; k++)
      {
        if (Math.abs(r.get(i, k)) < tolerance)
          r.set(i, k, 0);
      }
      return;
    }

    // Experiment with alternative policy
    double u3 = DoubleArray.sumSqrRange(uv, 1, uv.length);

    // Form the reflection vector
    //  For numerical accuracy the default policy is to reverse household reflection at each step.
    b = b * state.sign;

    // There are some rare cases in which the opposite reflection will be numerically better.
    // Check for that case and catch it here.
    if ((u3 + sqr(uv[0] - b)) < 1e-5 * (u3 + sqr(uv[0] + b)))
      b = -b;
    uv[0] = uv[0] - b;

    // Scaling factor is 2/(u'*u)
    double u2 = (u3 + sqr(uv[0]));

    // If all of the elements in the row are already 0, no work is necessary
    if (u2 == 0)
    {
      fill(ur, 0);
      r.set(i, j, b);
      return;
    }
    double scale = 2.0 / u2;

    // Apply the householder reflection to the portion of R
    // MATLAB
    //   R(i:end,j+1:end)=(eye-2/(u'*u) * u*u')*R(i:end,j+1:end);
    if (j + 1 < m && i < n)
    {
      Matrix subR = MatrixViews.select(r, i, n, j + 1, m);
      Matrix r2 = new MatrixColumnTable(subR);
      Matrix r3 = MatrixOps.multiply(u.transpose(), r2);
      MatrixOps.multiplyAssign(r3, scale);
      Matrix r4 = MatrixOps.multiplyVectorOuter(u, r3);
      MatrixOps.subtractAssign(r2, r4);
      subR.assign(r2);
    }
    // The first column of the rotated was precomputed by the reflection,
    // so to save work, we will just fill it in.
    // MATLAB
    //   R(i,i)=b;
    //   R(i+1:end,i)=0;
    fill(ur, 0);
    r.set(i, j, b);

    if (q != null)
    {
      // Apply the householder reflection to the portion of Q
      // MATLAB
      //   Q(:,i:end)=Q(:,i:end)*(eye(m-i)-2/(u'*u) * u*u');
      Matrix subQ = MatrixViews.selectColumnRange(q, i, n);
      Matrix q2 = new MatrixColumnTable(subQ);
      Matrix q3 = MatrixOps.multiply(q2, u);
      MatrixOps.multiplyAssign(q3, scale);
      Matrix q4 = MatrixOps.multiplyVectorOuter(q3, u);
      subtractAssign(q2, q4);
      subQ.assign(q2);
    }
  }

  /**
   * Eliminate a portion of a column using householder transformation.
   *
   * Reduce first column of R(i..n, j..m)
   *
   * Assumes all columns to the left are already 0.
   *
   * @param l
   * @param q
   * @param i
   * @param j
   * @param m
   * @param n
   * @param state
   */
  static public void reduceRow(Matrix l, Matrix q, int i, int j, int n, int m, State state)
  {
    // Check for completion of factorization
    if (i + 1 >= m)
      return;

    // Householder reflection to eliminate this vector
    Matrix ur = MatrixViews.select(l, i, i + 1, j, m);
    double b = Math.sqrt(MatrixOps.sumOfElementsSqr(ur));
    state.bmax = Math.max(state.bmax, b);
    Matrix.ColumnAccess u = new MatrixColumnArray(ur);
    double[] uv = u.accessColumn(0);

    // Short cut check for zero if a vector is linear dependent
    double umax = DoubleArray.findMaximumAbsolute(uv);
    double tolerance = state.bmax * state.epsilon;
    if (umax < tolerance)
    {
      // Clear the row of values smaller the tolerance
      fill(ur, 0);

      // Clear the column of values smaller then tolerance
      for (int k = i; k < n; k++)
      {
        if (Math.abs(l.get(k, j)) < tolerance)
          l.set(k, j, 0);
      }
      return;
    }

    // Experiment with alternative policy
    double u3 = DoubleArray.sumSqrRange(uv, 1, uv.length);

    // Form the reflection vector
    //  For numerical accuracy the default policy is to reverse household reflection at each step.
    b = b * state.sign;

    // There are some rare cases in which the opposite reflection will be numerically better.
    // Check for that case and catch it here.
    if ((u3 + sqr(uv[0] - b)) < 1e-5 * (u3 + sqr(uv[0] + b)))
      b = -b;
    uv[0] = uv[0] - b;

    // Scaling factor is 2/(u'*u)
    double u2 = (u3 + sqr(uv[0]));

    // If all of the elements in the row are already 0, no work is necessary
    if (u2 == 0)
    {
      fill(ur, 0);
      l.set(i, j, b);
      return;
    }
    double scale = 2.0 / u2;

    // Apply the householder reflection to the portion of R
    // MATLAB
    //   R(i+1:end, j:end)=R(i+1:end, j:end)*(eye-2/(u'*u) * u*u');
    if (i + 1 < n && j < m)
    {
      Matrix subR = MatrixViews.select(l, i + 1, n, j, m);
      Matrix r2 = new MatrixColumnTable(subR);
      Matrix r3 = MatrixOps.multiply(r2, u.transpose());
      MatrixOps.multiplyAssign(r3, scale);
      Matrix r4 = MatrixOps.multiplyVectorOuter(r3, u);
      MatrixOps.subtractAssign(r2, r4);
      subR.assign(r2);
    }
    // The first column of the rotated was precomputed by the reflection,
    // so to save work, we will just fill it in.
    // MATLAB
    //   R(i,i)=b;
    //   R(i+1:end,i)=0;
    fill(ur, 0);
    l.set(i, j, b);

    if (q != null)
    {
      // Apply the householder reflection to the portion of Q
      // MATLAB
      //   Q(:,j:end)=Q(:,i:end)*(eye(m-i)-2/(u'*u) * u*u');
      Matrix subQ = MatrixViews.selectRowRange(q, j, m);
      Matrix q2 = new MatrixColumnTable(subQ);
      Matrix q3 = MatrixOps.multiply(u, q2);
      MatrixOps.multiplyAssign(q3, scale);
      Matrix q4 = MatrixOps.multiplyVectorOuter(u, q3);
      subtractAssign(q2, q4);
      subQ.assign(q2);
    }
  }

  static public class State
  {
    double bmax = 0;
    int sign = -1;
    public double epsilon = 1e-15;
  }

//</editor-fold>
//  public static void main(String[] args)
//  {
//    Random rand = new Random();
//    Matrix h = MatrixFactory.newMatrix(5, 5);
//    Matrix q = MatrixFactory.newMatrix(5, 5);
//    fill(diagonal(q), 1);
//    MatrixOps.fill(h, () -> rand.nextDouble());
//    h = MatrixOps.multiply(h.transpose(), h);
//    Matrix h0 = h.copyOf();
//
//    HouseHolderTransform hht = new HouseHolderTransform();
//    Matrix[] m = hht.decomposeQR(h, 5);
//
//    System.out.println("H");
//    System.out.println("Q");
//    MatrixOps.dump(System.out, m[0]);
//    System.out.println("R");
//    MatrixOps.dump(System.out, m[1]);
////    MatrixOps.dump(System.out, h);
//    MatrixOps.dump(System.out, MatrixOps.subtract(h0, MatrixOps.multiply(m[0], m[1])));
//
//    m = hht.decomposeLQ(h, 5);
//
//    System.out.println("Q");
//    MatrixOps.dump(System.out, m[0]);
//    System.out.println("R");
//    MatrixOps.dump(System.out, m[1]);
////    MatrixOps.dump(System.out, h);
//    MatrixOps.dump(System.out, MatrixOps.subtract(h0, MatrixOps.multiply(m[0], m[1])));
//
//    {
//      Matrix[] m1 = hht.svd(h, 2);
//      Matrix[] m2 = hht.svd(h, 100);
//      System.out.println("U");
//      MatrixOps.dump(System.out, m1[0]);
//      System.out.println("S");
//      MatrixOps.dump(System.out, m1[1]);
//      System.out.println("V");
//      MatrixOps.dump(System.out, m1[2]);
//      System.out.println("Err");
//      MatrixOps.dump(System.out, MatrixOps.subtract(h0, MatrixOps.multiply(m1[0], MatrixOps.multiply(m1[1], m1[2]))));
//      MatrixOps.dump(System.out, MatrixOps.subtract(m1[0], m1[2].transpose()));
//      MatrixOps.dump(System.out, MatrixOps.multiplyAssign(
//              MatrixOps.add(MatrixViews.selectColumn(m1[0], 0), MatrixViews.selectColumn(m1[2].transpose(), 0)),0.5));
//      MatrixOps.dump(System.out, MatrixViews.selectColumn(m2[0], 0));
//
//    }
////    reduceColumn(q, h, 1, 0, 5, 5, state);
////    System.out.println("Q");
////    MatrixOps.dump(System.out, q);
////    System.out.println("R");
////    MatrixOps.dump(System.out, h);
////    System.out.println("Check");
////    MatrixOps.dump(System.out, MatrixOps.subtract(h0, MatrixOps.multiply(q, h)));
////
////    reduceRow( h, q, 0, 0, 5, 5, state);
////    reduceRow( h, q, 1, 1, 5, 5, state);
////    reduceRow( h, q, 2, 2, 5, 5, state);
////    reduceRow( h, q, 3, 3, 5, 5, state);
//////    reduceRow( h, q, 4, 4, 5, 5, state);
//////    reduceRow( h, q, 5, 5, 5, 5, state);
////    System.out.println("Q");
////    MatrixOps.dump(System.out, q);
////    System.out.println("R");
////    MatrixOps.dump(System.out, h);
////    System.out.println("Check");
////    MatrixOps.dump(System.out, MatrixOps.subtract(h0, MatrixOps.multiply( h, q)));
//  }
}
