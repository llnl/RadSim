/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.matrix;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.DoubleComparator;
import gov.llnl.math.MathExceptions.MathException;
import gov.llnl.math.MathExceptions.ResizeException;
import gov.llnl.math.MathExceptions.SingularException;
import gov.llnl.math.MathExceptions.SizeException;
import gov.llnl.math.MathExceptions.WriteAccessException;
import gov.llnl.math.matrix.Matrix.ColumnAccess;
import gov.llnl.math.matrix.Matrix.ColumnReadAccess;
import gov.llnl.math.matrix.Matrix.DivideOperation;
import gov.llnl.math.matrix.Matrix.ElementwiseOperations;
import gov.llnl.math.matrix.Matrix.RowAccess;
import gov.llnl.math.matrix.Matrix.RowReadAccess;
import gov.llnl.math.matrix.Matrix.ScalarOperations;
import gov.llnl.math.matrix.MatrixIterators.VectorIterator;
import gov.llnl.utility.annotation.Internal;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.function.DoubleSupplier;

/**
 * Collection of matrix operations supported for all matrix types.
 */
public class MatrixOps
{

  public static <MatrixType extends Matrix>
          boolean isNaN(MatrixType operand)
  {
    if (determineVectorAccess1(operand))
      return isNanIterator(MatrixIterators.newColumnDualIterator(operand));
    else
      return isNanIterator(MatrixIterators.newRowDualIterator(operand));
  }

  private static boolean isNanIterator(VectorIterator iter)
  {
    while (iter.advance())
    {
      if (DoubleArray.isNaNRange(iter.access(), iter.begin(), iter.end()))
        return true;
    }
    return false;
  }

//<editor-fold desc="add" defaultstate="collapsed">
  /**
   * Adds a value to every element in a matrix.
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param operand is target matrix to be operated on.
   * @param scalar is the quantity to add.
   * @return the target matrix.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  @SuppressWarnings("unchecked")
  public static <MatrixType extends Matrix>
          MatrixType addAssign(MatrixType operand, double scalar)
          throws WriteAccessException
  {
    if (scalar == 0)
      return operand;
    if (operand instanceof Matrix.ScalarOperations)
      return (MatrixType) ((ScalarOperations) operand).addAssign(scalar);
    if (determineVectorAccess1(operand))
      addAssignIterator(MatrixIterators.newColumnDualIterator(operand), scalar);
    else
      addAssignIterator(MatrixIterators.newRowDualIterator(operand), scalar);
    return operand;
  }

  /**
   * Add one matrix to another in an element-wise fashion.
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param a is target which will be added to.
   * @param b is the matrix to add.
   * @return the target matrix.
   * @throws SizeException if the matrices are the same size.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  @SuppressWarnings("unchecked")
  static public <MatrixType extends Matrix>
          MatrixType addAssign(MatrixType a, Matrix b)
          throws SizeException, WriteAccessException
  {
    MatrixAssert.assertEqualSize(a, b);
    if (a instanceof Matrix.ElementwiseOperations)
      return (MatrixType) ((ElementwiseOperations) a).addAssign(b);
    if (a != b && a.sync() == b.sync())
      b = b.copyOf();
    if (determineVectorAccess2(a, b))
      addAssignIterator(MatrixIterators.newColumnDualIterator(a),
              MatrixIterators.newColumnReadIterator(b));
    else
      addAssignIterator(MatrixIterators.newRowDualIterator(a),
              MatrixIterators.newRowReadIterator(b));
    return a;
  }

  /**
   * Add one matrix to another in an element-wise fashion times a scaling
   * factor.
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param a is target which will be added to.
   * @param b is the matrix to add.
   * @param scalar is the scalar to multiply to b prior to adding.
   * @return the target matrix.
   * @throws SizeException if the matrices are the same size.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  static public <MatrixType extends Matrix>
          MatrixType addAssignScaled(MatrixType a, Matrix b, double scalar)
          throws SizeException, WriteAccessException
  {
    MatrixAssert.assertEqualSize(a, b);
    if (a != b && a.sync() == b.sync())
      b = b.copyOf();
    if (determineVectorAccess2(a, b))
      addAssignScaledIterator(MatrixIterators.newColumnDualIterator(a),
              MatrixIterators.newColumnReadIterator(b), scalar);
    else
      addAssignScaledIterator(MatrixIterators.newRowDualIterator(a),
              MatrixIterators.newRowReadIterator(b), scalar);
    return a;
  }

  /**
   * Add two matrices in an element-wise fashion.
   *
   * @param a is the first matrix operand.
   * @param b is the second matrix operand.
   * @return the matrix element-wise sum.
   * @throws SizeException if the matrices not are the same size.
   */
  static public Matrix add(Matrix a, Matrix b)
          throws SizeException
  {
    MatrixAssert.assertEqualSize(a, b);
    try
    {
      if (determineVectorAccess2(a, b))
      {
        ColumnAccess r = MatrixFactory.newColumnMatrix(a);
        addAssignIterator(MatrixIterators.newColumnDualIterator(r),
                MatrixIterators.newColumnReadIterator(b));
        return r;
      }
      else
      {
        RowAccess r = MatrixFactory.newRowMatrix(a);
        addAssignIterator(MatrixIterators.newRowDualIterator(r),
                MatrixIterators.newRowReadIterator(b));
        return r;
      }
    }
    catch (WriteAccessException ex)
    {
      throw new RuntimeException(ex);
    }
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  private static void addAssignIterator(VectorIterator iter, double value)
  {
    while (iter.advance())
    {
      DoubleArray.addAssignRange(iter.access(), iter.begin(), iter.end(), value);
    }
  }

  private static void addAssignIterator(VectorIterator iter1, VectorIterator iter2)
  {
    int n = iter1.length();
    while (iter1.advance() && iter2.advance())
    {
      DoubleArray.addAssign(iter1.access(), iter1.begin(),
              iter2.access(), iter2.begin(), n);
    }
  }

  private static void addAssignScaledIterator(VectorIterator iter1, VectorIterator iter2, double scale)
  {
    int n = iter1.length();
    while (iter1.advance() && iter2.advance())
    {
      DoubleArray.addAssignScaled(iter1.access(), iter1.begin(),
              iter2.access(), iter2.begin(), n, scale);
    }
  }

//</editor-fold>
//</editor-fold>
//<editor-fold desc="subtract" defaultstate="collapsed">
  /**
   * Add one matrix to another in an element-wise fashion.
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param a is target which will be subtracted to.
   * @param b is the matrix to subtract.
   * @return the target matrix.
   * @throws SizeException if the matrices are the same size.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  static public <MatrixType extends Matrix>
          MatrixType subtractAssign(MatrixType a, Matrix b)
          throws SizeException, WriteAccessException
  {
    MatrixAssert.assertEqualSize(a, b);
    if (a != b && a.sync() == b.sync())
      b = b.copyOf();
    if (determineVectorAccess2(a, b))
      subtractAssignIterator(MatrixIterators.newColumnDualIterator(a),
              MatrixIterators.newColumnReadIterator(b));
    else
      subtractAssignIterator(MatrixIterators.newRowDualIterator(a),
              MatrixIterators.newRowReadIterator(b));
    return a;
  }

  /**
   * Add two matrices in an element-wise fashion.
   *
   * @param a is the first matrix operand.
   * @param b is the second matrix operand.
   * @return the matrix element-wise sum.
   * @throws SizeException if the matrices are the same size.
   */
  static public Matrix subtract(Matrix a, Matrix b)
          throws SizeException
  {
    MatrixAssert.assertEqualSize(a, b);
    try
    {
      if (determineVectorAccess2(a, b))
      {
        ColumnAccess r = MatrixFactory.newColumnMatrix(a);
        return subtractAssign(r, b);
      }
      else
      {
        RowAccess r = MatrixFactory.newRowMatrix(a);
        return subtractAssign(r, b);
      }
    }
    catch (WriteAccessException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Negate all values in a matrix
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param operand is the target matrix.
   * @return the target matrix.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  public static <MatrixType extends Matrix>
          MatrixType negateAssign(MatrixType operand)
          throws WriteAccessException
  {
    if (determineVectorAccess1(operand))
      negateAssignVector(MatrixIterators.newColumnDualIterator(operand));
    else
      negateAssignVector(MatrixIterators.newRowDualIterator(operand));
    return operand;
  }

  /**
   * Create a matrix which is the negation of a matrix.
   *
   * @param operand is the operand.
   * @return the target matrix.
   */
  public static Matrix negate(Matrix operand)
  {
    try
    {
      return negateAssign(operand.copyOf());
    }
    catch (WriteAccessException ex)
    {
      throw new RuntimeException(ex);
    }
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  private static void subtractAssignIterator(VectorIterator iter, double value)
  {
    while (iter.advance())
    {
      DoubleArray.addAssignRange(iter.access(), iter.begin(), iter.end(), -value);
    }
  }

  private static void subtractAssignIterator(VectorIterator iter1, VectorIterator iter2)
  {
    int n = iter1.length();
    while (iter1.advance() && iter2.advance())
    {
      DoubleArray.subtractAssign(iter1.access(), iter1.begin(),
              iter2.access(), iter2.begin(), n);
    }
  }

  private static void negateAssignVector(VectorIterator iter)
  {
    while (iter.advance())
    {
      DoubleArray.negateAssignRange(iter.access(), iter.begin(), iter.end());
    }
  }

//</editor-fold>
//</editor-fold>
//<editor-fold desc="multiply" defaultstate="collapsed">
  /**
   * Multiplies every element in a matrix by a value.
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param operand is the matrix to be operated on.
   * @param scalar is the quantity to multiply.
   * @return the target matrix.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  @SuppressWarnings("unchecked")
  public static <MatrixType extends Matrix>
          MatrixType multiplyAssign(MatrixType operand, double scalar)
          throws WriteAccessException
  {
    if (scalar == 1)
      return operand;
    if (operand instanceof Matrix.ScalarOperations)
      return (MatrixType) ((ScalarOperations) operand).multiplyAssign(scalar);

    if (determineVectorAccess1(operand))
      multiplyAssignIterator(MatrixIterators.newColumnDualIterator(operand), scalar);
    else
      multiplyAssignIterator(MatrixIterators.newRowDualIterator(operand), scalar);
    return operand;
  }

  /**
   * Multiply one matrix to another in an element-wise fashion.
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param a is target which will be multiply to.
   * @param b is the matrix to multiply by.
   * @return the target matrix.
   * @throws SizeException if the matrices are the same size.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  @SuppressWarnings("unchecked")
  static public <MatrixType extends Matrix>
          MatrixType multiplyAssignElements(MatrixType a, Matrix b)
          throws SizeException, WriteAccessException
  {
    MatrixAssert.assertEqualSize(a, b);
    if (a instanceof Matrix.ElementwiseOperations)
      return (MatrixType) ((ElementwiseOperations) a).multiplyAssignElements(b);
    if (a != b && a.sync() == b.sync())
      b = b.copyOf();
    if (determineVectorAccess2(a, b))
      multiplyAssignIterator(MatrixIterators.newColumnDualIterator(a),
              MatrixIterators.newColumnReadIterator(b));
    else
      multiplyAssignIterator(MatrixIterators.newRowDualIterator(a),
              MatrixIterators.newRowReadIterator(b));
    return a;
  }

  static public double[] multiply(double[] b, Matrix a)
          throws SizeException
  {
    Matrix c = multiply(MatrixFactory.newRowMatrix(b), a);
    return unwrapArray(c);
  }

  static public double[] multiply(Matrix a, double[] b)
          throws SizeException
  {
    Matrix c = multiply(a, MatrixFactory.newColumnMatrix(b));
    return unwrapArray(c);
  }

  static private double[] unwrapArray(Matrix c)
  {
    if (c instanceof MatrixArrayBase)
      return ((MatrixArrayBase) c).toArray();
    return c.flatten();
  }

  /**
   * Multiply two matrices together. The result will be in the preferred
   * implementation for multiplication.
   *
   * @param a is the first operand.
   * @param b is the second operand.
   * @return is the matrix product of the operands.
   * @throws SizeException is the inner dimension does not match.
   */
  static public Matrix multiply(Matrix a, Matrix b)
          throws SizeException
  {
    try
    {
      MatrixAssert.assertColumnsEqualsRows(a, b);
      int d1 = a.rows();
      int d3 = b.columns();

      Matrix r;

      // Select the best output type
      switch (MatrixOpMultiply.determinePolicy(a, b))
      {
        case COLUMN_ACCUMULATE:
        case ROW_INNER:
          if (d1 == 1 || d3 == 1)
            r = new MatrixColumnArray(d1, d3);
          else
            r = new MatrixColumnTable(d1, d3);
          break;
        case ROW_ACCUMULATE:
        case COLUMN_INNER:
          if (d1 == 1 || d3 == 1)
            r = new MatrixRowArray(d1, d3);
          else
            r = new MatrixRowTable(d1, d3);
          break;
        default:
          throw new UnsupportedOperationException();
      }

      // Perform multiplication
      switch (MatrixOpMultiply.determinePolicy(a, b))
      {
        case COLUMN_ACCUMULATE:
          return MatrixOpMultiply.multiplyColumnAccumulate(r, MatrixFactory.asColumnMatrix(a), b);
        case ROW_INNER:
          return MatrixOpMultiply.multiplyRowInner(r, MatrixFactory.asRowMatrix(a), b);
        case ROW_ACCUMULATE:
          return MatrixOpMultiply.multiplyRowAccumulate(r, a, MatrixFactory.asRowMatrix(b));
        case COLUMN_INNER:
          return MatrixOpMultiply.multiplyColumnInner(r, a, MatrixFactory.asColumnMatrix(b));
        default:
          throw new UnsupportedOperationException();
      }
    }
    catch (ResizeException | WriteAccessException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Multiply each row by a value from a vector.
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param operand is a matrix to be operated on.
   * @param vector is a vector with elements equal to operand rows.
   * @return the target matrix.
   * @throws SizeException if the vector length does not match the operand.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  static public <MatrixType extends Matrix>
          MatrixType multiplyAssignRows(MatrixType operand, Matrix vector)
          throws MathException
  {
    VectorView va  = new VectorView(vector);
    MatrixAssert.assertRowsEqual(operand, va.size());
    if (determineVectorAccess1(operand))
      multiplyAssignIterator2(MatrixIterators.newColumnDualIterator(operand), va);
    else
      multiplyAssignIterator1(MatrixIterators.newRowDualIterator(operand), va);
    return operand;
  }

  /**
   * Multiply each column by a value from a vector.
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param operand is a matrix to be operated on.
   * @param vector is a vector with elements equal to operand columns.
   * @return the target matrix.
   * @throws SizeException if the vector length does not match the operand.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  static public <MatrixType extends Matrix>
          MatrixType multiplyAssignColumns(MatrixType operand, Matrix vector)
          throws MathException
  {
    VectorView va  = new VectorView(vector);
    MatrixAssert.assertColumnsEqual(operand, va.size());
    if (determineVectorAccess1(operand))
      multiplyAssignIterator1(MatrixIterators.newColumnDualIterator(operand), va);
    else
      multiplyAssignIterator2(MatrixIterators.newRowDualIterator(operand), va);
    return operand;
  }

  public static Matrix multiplyVectorOuter(Matrix a, Matrix b)
          throws SizeException
  {
    // Convert to vectors
    VectorView va  = new VectorView(a);
    VectorView vb = new VectorView(b);
    double[] da = va.access();
    double[] db = vb.access();
    int oa = va.offset();
    int ob = vb.offset();
    int n = va.size();
    int m = vb.size();
    Matrix.ColumnAccess out = MatrixFactory.newColumnMatrix(n, m);
    for (int i = 0; i < m; i++)
    {
      DoubleArray.addAssignScaled(out.accessColumn(i), out.addressColumn(i), da, oa, n, db[i + ob]);
    }
    return out;
  }

  /**
   * Computes the product of two vectors to produce a matrix.
   *
   * @param a is a vector length n
   * @param b is a vector of length m
   * @return a matrix of (n,m) whose elements e_ij=a_i*b_j
   *
   */
  public static Matrix multiplyVectorOuter(double[] a, double[] b)
  {
    int n = a.length;
    int m = b.length;
    Matrix.ColumnAccess out = MatrixFactory.newColumnMatrix(n, m);
    for (int i = 0; i < m; i++)
    {
      DoubleArray.addAssignScaled(out.accessColumn(i), out.addressColumn(i), a, 0, n, b[i]);
    }
    return out;
  }

  /**
   * Compute the hermitian product R=X'*H*Y.
   *
   * @param x is a double array representing a row vector with length N.
   * @param h is a matrix with the dimensions NxM.
   * @param y is a double array representing a column vector with length M.
   * @return the hermitian product.
   * @throws SizeException if the dimensions are incorrect.
   */
  public static double multiplyHermitian(double[] x, Matrix h, double[] y) throws SizeException
  {
    VectorView vx = new VectorView(x);
    VectorView vy = new VectorView(y);
    return MatrixOps.multiplyHermitian(vx, h, vy);
  }

  /**
   * Compute the hermitian product R=X'*H*Y.
   *
   * @param x is a vector with length N.
   * @param h is a matrix with the dimensions NxM.
   * @param y is a vector with length M.
   * @return the hermitian product.
   * @throws SizeException if the dimensions are incorrect.
   */
  public static double multiplyHermitian(Matrix x, Matrix h, Matrix y) throws SizeException
  {
    VectorView vx = new VectorView(x);
    VectorView vy = new VectorView(y);
    return MatrixOps.multiplyHermitian(vx, h, vy);
  }

  @Internal
  public static double multiplyHermitian(VectorView vx, Matrix h, VectorView vy) throws SizeException
  {
    MatrixAssert.assertRowsEqual(h, vx.size());
    MatrixAssert.assertColumnsEqual(h, vy.size());

    if (determineVectorAccess1(h))
      return hermitianIterator(MatrixIterators.newColumnReadIterator(h), vy, vx);
    else
      return hermitianIterator(MatrixIterators.newRowReadIterator(h), vx, vy);
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  private static void multiplyAssignIterator(VectorIterator iter, double value)
  {
    while (iter.advance())
    {
      DoubleArray.multiplyAssignRange(iter.access(), iter.begin(), iter.end(), value);
    }
  }

  static private int getMajorAxis(Matrix m)
  {
    if (m instanceof ColumnAccess)
      return m.rows();
    if (m instanceof RowAccess)
      return m.columns();
    return 1;
  }

  private static void multiplyAssignIterator(VectorIterator a, VectorIterator b)
  {
    while (a.advance() && b.advance())
    {
      DoubleArray.multiplyAssign(a.access(), a.begin(), b.access(), b.begin(), a.length());
    }
  }

  private static void multiplyAssignIterator1(VectorIterator in, VectorView va)
  
  {
    double[] values = va.access();
    int offset = va.offset();
    while (in.advance())
    {
      DoubleArray.multiplyAssignRange(in.access(), in.begin(), in.end(), values[in.index() + offset]);
    }
  }

  private static void multiplyAssignIterator2(VectorIterator in, VectorView va)
  
  {
    double[] values = va.access();
    int offset = va.offset();
    while (in.advance())
    {
      DoubleArray.multiplyAssign(in.access(), in.begin(), values, offset, in.length());
    }
  }

  private static double hermitianIterator(VectorIterator iter, VectorView y, VectorView x)
  {
    double sum = 0;
    double[] dx = x.access();
    int ix = x.offset();
    while (iter.advance())
    {
      sum += dx[ix++] * DoubleArray.multiplyInner(iter.access(),
              iter.begin(), y.access(), y.offset(), iter.length());
    }
    return sum;
  }

//</editor-fold>
//</editor-fold>
//<editor-fold desc="divide" defaultstate="collapsed">
  /**
   * Divide a matrix by a scalar.
   *
   * @param <MatrixType> is the type of matrix both input and returned.
   * @param operand is target matrix to be operated on.
   * @param scalar is the quantity to add.
   * @return the target matrix.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  @SuppressWarnings("unchecked")
  public static <MatrixType extends Matrix>
          MatrixType divideAssign(MatrixType operand, double scalar)
          throws WriteAccessException
  {
    if (operand instanceof Matrix.ScalarOperations)
      return (MatrixType) ((ScalarOperations) operand).divideAssign(scalar);

    if (determineVectorAccess1(operand))
      divideAssignIterator(MatrixIterators.newColumnDualIterator(operand), scalar);
    else
      divideAssignIterator(MatrixIterators.newRowDualIterator(operand), scalar);
    return operand;
  }

  /**
   * Compute the inverse of a matrix.
   *
   * @param matrix is a square matrix.
   * @return the inverse of the matrix.
   * @throws SizeException if the matrix is not square.
   * @throws SingularException if the matrix is not invertible.
   */
  public static Matrix invert(Matrix matrix) throws SizeException, SingularException
  {
    try
    {
      int n = matrix.rows();
      MatrixAssert.assertSquare(matrix);
      Matrix.RowAccess in = new MatrixRowTable(matrix);
      Matrix.RowAccess out = MatrixFactory.newRowMatrix(n, n);
      addAssign(MatrixViews.diagonal(out), 1);
      MatrixOpDivide.solveDestructive(out, in);
      return out;
    }
    catch (WriteAccessException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Compute the inverse of a matrix.
   *
   * This is cheapest if the result is a Row matrix.
   *
   * @param <MatrixType>
   * @param result is the output of the matrix.
   * @param in is a square matrix.
   * @return the inverse of the matrix.
   * @throws SizeException if the matrix is not square.
   * @throws ResizeException if the result matrix cannot be resized.
   * @throws WriteAccessException if the result matrix cannot be written to.
   * @throws SingularException if the matrix is singular within working
   * precision.
   */
  public static <MatrixType extends Matrix>
          MatrixType invert(MatrixType result, Matrix in)
          throws SizeException, ResizeException, WriteAccessException, SingularException
  {
    int n = in.rows();
    MatrixAssert.assertSquare(in);
    if (!result.resize(n, n))
      fill(result, 0);
    // We need a copy because we will be destroying
    // the original with gauss elimination
    Matrix.RowAccess copy = new MatrixRowTable(in);
    fill(MatrixViews.diagonal(result), 1);
    MatrixOpDivide.solveDestructive(MatrixFactory.createRowOperations(result), copy);
    return result;
  }

  /**
   * Divide a matrix by another to solve A*x=B. Method of calculation depends on
   * the shape of the matrix. This is computed directly if A is square and
   * computed in the least squares sense if the number of rows exceeds the
   * number of columns. This is equivalent to x=A\B in Matlab.
   *
   * @param A is the denominator.
   * @param B is the numerator.
   * @return a new matrix that when multiplied by A results in B.
   * @throws SizeException if the number of rows in A and B differ.
   * @throws SingularException if the matrix is singular within working
   * precision.
   */
  static public Matrix divideLeft(Matrix A, Matrix B)
          throws SizeException, SingularException
  {
    // Special handling if the matrix supports divideLeft
    if (A instanceof Matrix.DivideOperation)
    {
      Matrix R = MatrixFactory.newMatrix(B);
      MatrixRowOperations RO = MatrixFactory.createRowOperations(R);
      ((DivideOperation) A).divideLeft(RO);
      return R;
    }

    try
    {
      MatrixAssert.assertRowsEqual(A, B);
      int m = A.rows();
      int n = A.columns();
      Matrix.RowAccess Q = MatrixFactory.newRowMatrix(n, n);

      // FIXME what if X is a column vector.  We need to allocate based on the needs.
      Matrix.RowAccess X = MatrixFactory.newRowMatrix(n, B.columns());

      if (n == m)
      { // Square case, easy copy the matrices and invert
        Q.assign(A);
        X.assign(B);
      }
      else if (n < m)
      {
        // Otherwise, compute the least squares solution
        // This is poor because we should solve using the symmetric method
        Matrix At = A.transpose();
        Q = MatrixFactory.asRowMatrix(multiply(At, A));
        X = MatrixFactory.asRowMatrix(multiply(At, B));
      }
      else
      {
        throw new UnsupportedOperationException("Not implemented yet.");
      }

      MatrixOpDivide.solveDestructive(X, Q);
      return X;
    }
    catch (ResizeException | WriteAccessException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Divide a matrix by another to compute x*B=A. The number of columns must be
   * equal. This is a short cut to call divideLeft with B'*x'=A'. This is the
   * equivalent of x=A/B in Matlab.
   *
   * @param A is the numerator.
   * @param B is the denominator.
   * @return a new matrix such that the product with B equivalent A.
   * @throws SizeException if the number of columns in a A and B differ.
   */
  static public Matrix divideRight(Matrix A, Matrix B)
          throws SizeException
  {
    Matrix X = divideLeft(B.transpose(), A.transpose());
    return X.transpose();
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  private static void divideAssignIterator(VectorIterator iter, double scalar)
  {
    while (iter.advance())
    {
      DoubleArray.divideAssignRange(iter.access(), iter.begin(), iter.end(), scalar);
    }
  }

//</editor-fold>
//</editor-fold>
//<editor-fold desc="norm" defaultstate="collapsed">
  /**
   * Compute the norm1 for a matrix. Likely legacy code for setting tolerances.
   * Move to the location that uses this. Adds up each row with absolute values
   * then takes the largest.
   *
   * @param operand
   * @return the norm1 of the matrix.
   */
  static public double norm1(Matrix operand)
  {
    double n = -Double.MAX_VALUE;
    for (int i0 = 0; i0 < operand.columns(); ++i0)
    {
      double sum = 0;
      for (int i1 = 0; i1 < operand.rows(); ++i1)
      {
        sum += Math.abs(operand.get(i1, i0));
      }
      if (sum > n)
        n = sum;
    }
    return n;
  }

  /**
   * Divides each column by the sum of the absolute values.
   *
   * @param matrix
   */
  static public void normColumns1(Matrix matrix)
  {
    double[] d = VectorOps.evaluateEachColumn(matrix, new VectorOps.SumAbsolute()).toArray();
    for (int i = 0; i < d.length; ++i)
    {
      if (d[i] > 0)
        d[i] = 1.0 / d[i];
    }
    MatrixOps.multiplyAssignColumns(matrix, MatrixFactory.wrapRowVector(d));
  }

  /**
   * Divides each column by the column norm2.
   *
   * @param matrix
   */
  static public void normColumns2(Matrix matrix)
  {
    double[] d = VectorOps.evaluateEachColumn(matrix, new VectorOps.SumSquare())
            .toArray();
    for (int i = 0; i < d.length; ++i)
    {
      if (d[i] > 0)
        d[i] = 1.0 / Math.sqrt(d[i]);
      else
        d[i] = 0.0;
    }
    MatrixOps.multiplyAssignColumns(matrix, MatrixFactory.newRowMatrix(d));
  }

//</editor-fold>
//<editor-fold desc="sum/mean" defaultstate="collapsed">
  /**
   * Compute the sum of each column in a matrix.
   *
   * @param matrix
   * @return a new vector with the sum of each column.
   */
  public static MatrixRowArray sumOfEachColumn(Matrix matrix)
  {
    return VectorOps.evaluateEachColumn(matrix, new VectorOps.Sum());
  }

  public static MatrixColumnArray sumOfEachRow(Matrix matrix)
  {
    return VectorOps.evaluateEachRow(matrix, new VectorOps.Sum());
  }

  public static MatrixRowArray meanOfEachColumn(Matrix matrix)
  {
    MatrixRowArray out = sumOfEachColumn(matrix);
    return MatrixOps.divideAssign(out, matrix.rows());
  }

  public static MatrixColumnArray meanOfEachRow(Matrix matrix)
  {
    MatrixColumnArray out = sumOfEachRow(matrix);
    return MatrixOps.divideAssign(out, matrix.columns());
  }

//</editor-fold>
//<editor-fold desc="fill"  defaultstate="collapsed">
  /**
   * Fill a matrix with a value.
   *
   * @param operand is the target matrix.
   * @param scalar is the quantity to be filled.
   * @return the target matrix.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  public static <MatrixType extends Matrix> MatrixType fill(MatrixType operand, double scalar) throws WriteAccessException
  {
    if (determineVectorAccess1(operand))
      fillIterator(MatrixIterators.newColumnDualIterator(operand), scalar);
    else
      fillIterator(MatrixIterators.newRowDualIterator(operand), scalar);
    return operand;
  }

  /**
   * Fill a matrix with a value.
   *
   * The traversal direction depends on the orientation of the matrix. Some are
   * filled rowwise and others are columnwise depending on what was the minor
   * direction of the matrix.
   *
   * @param operand is the target matrix.
   * @param scalar is the quantity to be filled.
   * @return the target matrix.
   * @throws WriteAccessException if the matrix cannot be written to.
   */
  public static <MatrixType extends Matrix> MatrixType fill(MatrixType operand, DoubleSupplier scalar) throws WriteAccessException
  {
    if (determineVectorAccess1(operand))
      fillIterator(MatrixIterators.newColumnDualIterator(operand), scalar);
    else
      fillIterator(MatrixIterators.newRowDualIterator(operand), scalar);
    return operand;
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  private static void fillIterator(VectorIterator iter, double value)
  {
    while (iter.advance())
    {
      DoubleArray.fillRange(iter.access(), iter.begin(), iter.end(), value);
    }
  }

  private static void fillIterator(VectorIterator iter, DoubleSupplier value)
  {
    while (iter.advance())
    {
      DoubleArray.fillRange(iter.access(), iter.begin(), iter.end(), value);
    }
  }

//</editor-fold>
//</editor-fold>
//<editor-fold desc="comparison" defaultstate="collapsed">
  /**
   * Compares two matrices to see if they are equal within limits of precision.
   *
   * @param a
   * @param b
   * @return
   */
  public static boolean equals(Matrix a, Matrix b)
  {
    // Must be the same size
    if (a.rows() != b.rows() || a.columns() != b.columns())
      return false;

    // Handle trivial case
    if (a == b)
      return true;

    if (determineVectorAccess2(a, b))
      return equals(MatrixIterators.newColumnReadIterator(a),
              MatrixIterators.newColumnReadIterator(b));
    else
      return equals(MatrixIterators.newRowReadIterator(a),
              MatrixIterators.newRowReadIterator(b));
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  static private boolean equals(VectorIterator a, VectorIterator b)
  {
    int d = a.length();
    while (a.advance() && b.advance())
    {
      if (!DoubleArray.equals(a.access(), a.begin(), b.access(), b.begin(), d))
        return false;
    }
    return true;
  }

  /**
   * Compares two matrices to see if they are equal within limits of precision.
   *
   * @param a
   * @param b
   * @return
   */
  public static boolean equivalent(Matrix a, Matrix b)
  {
    // Must be the same size
    if (a.rows() != b.rows() || a.columns() != b.columns())
      return false;

    // Handle trivial case
    if (a == b)
      return true;

    if (determineVectorAccess2(a, b))
      return equivalent(MatrixIterators.newColumnReadIterator(a),
              MatrixIterators.newColumnReadIterator(b));
    else
      return equivalent(MatrixIterators.newRowReadIterator(a),
              MatrixIterators.newRowReadIterator(b));
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  static private boolean equivalent(VectorIterator a, VectorIterator b)
  {
    int d = a.length();
    while (a.advance() && b.advance())
    {
      if (!DoubleArray.equivalent(a.access(), a.begin(), b.access(), b.begin(), d))
        return false;
    }
    return true;
  }
//</editor-fold>
//</editor-fold>
//<editor-fold desc="misc" defaultstate="collapsed">

  public static void dump(PrintStream out, Matrix m)
  {
    for (int r = 0; r < m.rows(); ++r)
    {
      for (int c = 0; c < m.columns(); ++c)
        out.print(m.get(r, c) + " ");
      out.println();
    }
  }

  public static void dumpIterator(PrintStream out, MatrixIterators.VectorIterator iter)
  {
    while (iter.advance())
    {
      dumpIteratorContent(out, iter);
    }
  }

  public static void dumpIteratorContent(PrintStream out, MatrixIterators.VectorIterator iter)
  {
    out.print("[");
    for (int i = iter.begin(); i < iter.end(); ++i)
      out.print(iter.access()[i] + " ");
    out.println("]");
  }

  static private double sumOfElementsSqr(VectorIterator iter)
  {
    double out = 0;
    while (iter.advance())
    {
      out += DoubleArray.sumSqrRange(iter.access(), iter.begin(), iter.end());
    }
    return out;
  }

  static public double sumOfElementsSqr(Matrix m)
  {
    if (determineVectorAccess1(m))
      return sumOfElementsSqr(MatrixIterators.newColumnReadIterator(m));
    else
      return sumOfElementsSqr(MatrixIterators.newRowReadIterator(m));
  }

  static private double sumOfElements(VectorIterator iter)
  {
    double out = 0;
    while (iter.advance())
    {
      out += DoubleArray.sumRange(iter.access(), iter.begin(), iter.end());
    }
    return out;
  }

  static public double sumOfElements(Matrix m)
  {
    if (determineVectorAccess1(m))
      return sumOfElements(MatrixIterators.newColumnReadIterator(m));
    else
      return sumOfElements(MatrixIterators.newRowReadIterator(m));
  }

  /**
   * Create a copy of an array with a different shape. The size of the reshaped
   * matrix must be the same as the original. Matrix is flattened into column
   * major order before reshaping. This function is to support Matlab
   * translation.
   *
   * @param matrix is the matrix to copy from.
   * @param rows is the requested number of rows.
   * @param columns is the requested number of columns.
   * @return is a copy of the matrix with the requested size.
   * @throws SizeException if the size requested is different than the original.
   */
  static public Matrix reshape(Matrix matrix, int rows, int columns)
          throws SizeException
  {
    MatrixAssert.assertSizeEquals(matrix, rows * columns);
    MatrixColumnArray mca = new MatrixColumnArray(matrix);
    mca.reshape(rows, columns);
    return mca;
  }

//</editor-fold>
//<editor-fold desc="utility" defaultstate="collapsed">
  /**
   * Identify the preferred direction for vector iteration of two matrices.
   *
   * @param a is the first operand.
   * @return true if the best direction is columnwise, otherwise return false.
   */
  public static boolean determineVectorAccess1(Matrix a)
  {
    if (a instanceof ColumnReadAccess)
      return true;
    if (a instanceof RowReadAccess)
      return false;
    return (a.rows() > a.columns());
  }

  /**
   * Identify the preferred direction for vector iteration of two matrices. Both
   * matrices are assumed to be the same size.
   *
   * @param a is the first operand.
   * @param b is the second operand.
   * @return true if the best direction is columnwise, otherwise return false.
   */
  public static boolean determineVectorAccess2(Matrix a, Matrix b)
  {
    if (a instanceof ColumnReadAccess)
    {
      // Both have the same acess abilities
      if (b instanceof ColumnReadAccess)
        return true;

      // A and B have different, so select the one with the most efficient step
      if (b instanceof RowReadAccess)
        return b.columns() < a.rows();

      // Use access policy of A
      return true;
    }

    if (a instanceof RowReadAccess)
    {
      // Both have the same access abilities.
      if (b instanceof RowReadAccess)
        return false;

      if (b instanceof ColumnReadAccess)
        return b.rows() >= a.columns();

      // Use access policy of A
      return false;
    }

    // a does not have an access policy, so how about b
    if (b instanceof RowReadAccess)
      return false;

    // Neither has an access policy.
    return true;
  }

  public static double[][] toArray(Matrix m)
  {
    return new MatrixRowTable(m).asRows();
  }
//</editor-fold>
//<editor-fold desc="cat" defaultstate="collapsed">

  public static Matrix hcat(Matrix... m)
  {
    int columns = 0;
    int rows = m[0].rows();
    for (Matrix v : m)
    {
      MatrixAssert.assertRowsEqual(v, rows);
      columns += v.columns();
    }

    Matrix out = MatrixFactory.newMatrix(rows, columns);
    int i = 0;
    for (Matrix v : m)
    {

      int c = v.columns();
      MatrixViews.selectColumnRange(out, i, i + c).assign(v);
      i += c;
    }
    return out;
  }

  /**
   * Stack two matrix on top of each other
   *
   * @param m
   * @return
   */
  public static Matrix vcat(Matrix... m)
  {
    if (m.length == 0)
      return MatrixFactory.newMatrix(0, 0);
    int rows = 0;
    int columns = m[0].columns();
    for (Matrix v : m)
    {
      MatrixAssert.assertColumnsEqual(v, columns);
      rows += v.rows();
    }

    Matrix out = MatrixFactory.newMatrix(rows, columns);
    int i = 0;
    for (Matrix v : m)
    {
      int r = v.rows();
      Matrix mv = MatrixViews.selectRowRange(out, i, i + r);
      mv.assign(v);
      i += r;
    }
    return out;
  }
//</editor-fold>
//<editor-fold desc="clip" defaultstate="collapsed">

  /**
   * Creates a matrix that is the element-wise maximum of the matrix elements
   * and a value.
   *
   * @param m
   * @param value
   * @return
   */
  public static Matrix maximum(Matrix m, double value)
  {
    return assignMaximumOf(m.copyOf(), value);
  }

  public static Matrix assignMaximumOf(Matrix m2, double value)
  {
    VectorIterator mi;
    if (determineVectorAccess1(m2))
    {
      mi = MatrixIterators.newColumnDualIterator(m2);
    }
    else
    {
      mi = MatrixIterators.newColumnDualIterator(m2);
    }
    while (mi.advance())
    {
      double[] memory = mi.access();
      for (int i = mi.begin(); i < mi.end(); ++i)
      {
        if (memory[i] < value)
          memory[i] = value;
      }
    }
    return m2;
  }

  /**
   * Creates a matrix that is the element-wise minimum of the matrix elements
   * and a value.
   *
   * @param m
   * @param value
   * @return
   */
  public static Matrix minimum(Matrix m, double value)
  {
    return assignMinimumOf(m.copyOf(), value);
  }

  public static Matrix assignMinimumOf(Matrix m2, double value)
  {
    VectorIterator mi;
    if (determineVectorAccess1(m2))
    {
      mi = MatrixIterators.newColumnDualIterator(m2);
    }
    else
    {
      mi = MatrixIterators.newColumnDualIterator(m2);
    }
    while (mi.advance())
    {
      double[] memory = mi.access();
      for (int i = mi.begin(); i < mi.end(); ++i)
      {
        if (memory[i] > value)
          memory[i] = value;
      }
    }
    return m2;
  }

  public static Matrix clip(Matrix m, double min, double max)
  {
    Matrix m2 = m.copyOf();
    VectorIterator mi;
    if (determineVectorAccess1(m2))
    {
      mi = MatrixIterators.newColumnDualIterator(m2);

    }
    else
    {
      mi = MatrixIterators.newColumnDualIterator(m2);
    }
    while (mi.advance())
    {
      double[] memory = mi.access();
      for (int i = mi.begin(); i < mi.end(); ++i)
      {
        if (memory[i] < min)
          memory[i] = min;
        if (memory[i] > max)
          memory[i] = max;
      }
    }
    return m2;
  }
//</editor-fold>
//<editor-fold desc="maximum" defaultstate="collapsed">

  /**
   * Take the maximum of each position in a set of vectors.
   *
   * @param iter
   * @return
   */
  private static double[] maximumVector1(VectorIterator iter)
  {
    iter.advance();
    double[] d = Arrays.copyOfRange(iter.access(), iter.begin(), iter.length());
    while (iter.advance())
    {
      double[] d2 = iter.access();
      int j = iter.begin();
      for (int i = 0; i < iter.length(); i++, j++)
      {
        if (d[i] < d2[j])
          d[i] = d2[j];
      }
    }
    return d;
  }

  /**
   * Take the maximum of each vector and store in an array.
   *
   * @param iter
   * @return
   */
  private static double[] maximumVector2(VectorIterator iter)
  {
    double[] d = new double[iter.length()];
    while (iter.advance())
    {
      double[] d2 = iter.access();
      int j = iter.begin();
      double mx = d2[j];
      j++;
      for (; j < iter.end(); j++)
        if (mx < d2[j])
          mx = d2[j];
      d[iter.index()] = mx;
    }
    return d;
  }

  public static Matrix.RowAccess maximumOfEachColumn(Matrix m)
  {
    if (determineVectorAccess1(m))
    {
      return MatrixFactory.newRowMatrix(maximumVector1(MatrixIterators.newRowReadIterator(m)));
    }
    else
    {
      return MatrixFactory.newRowMatrix(maximumVector2(MatrixIterators.newColumnReadIterator(m)));
    }
  }

  public static Matrix.ColumnAccess maximumOfEachRow(Matrix m)
  {
    if (determineVectorAccess1(m))
    {
      return MatrixFactory.newColumnMatrix(maximumVector1(MatrixIterators.newColumnReadIterator(m)));
    }
    else
    {
      return MatrixFactory.newColumnMatrix(maximumVector2(MatrixIterators.newRowReadIterator(m)));
    }
  }

  public static double maximumOfAll(Matrix m)
  {
    if (determineVectorAccess1(m))
    {
      return maximumVectorAll(MatrixIterators.newColumnReadIterator(m));
    }
    else
    {
      return maximumVectorAll(MatrixIterators.newRowReadIterator(m));
    }
  }

  private static double maximumVectorAll(VectorIterator iter)
  {
    double d = -Double.MAX_VALUE;
    while (iter.advance())
    {
      double[] d2 = iter.access();
      int j = iter.begin();
      for (int i = 0; i < iter.length(); i++, j++)
      {
        if (d < d2[j])
          d = d2[j];
      }
    }
    return d;
  }

//</editor-fold>
//<editor-fold desc="extrema" defaultstate="collapsed">
  public static class Extrema
  {
    public int row;
    public int column;
    public double value;
  }

  public static Extrema findExtrema(Matrix m, DoubleComparator comparator)
  {
    if (determineVectorAccess1(m))
    {
      Extrema extrema = findMaximumVector(MatrixIterators.newColumnReadIterator(m), comparator);
      return extrema;
    }
    else
    {
      Extrema extrema = findMaximumVector(MatrixIterators.newColumnReadIterator(m), comparator);
      int r = extrema.row;
      int c = extrema.column;
      extrema.row = c;
      extrema.column = r;
      return extrema;
    }
  }

  public static Extrema findMaximumElement(Matrix m)
  {
    return findExtrema(m, new DoubleComparator.Positive());
  }

  public static Extrema findMinimumElement(Matrix m)
  {
    return findExtrema(m, new DoubleComparator.Negative());
  }

//<editor-fold desc="internal">
  private static Extrema findMaximumVector(VectorIterator iter, DoubleComparator comparator)
  {
    Extrema extrema = null;
    while (iter.advance())
    {
      int i = DoubleArray.findIndexOfRange(iter.access(), iter.begin(), iter.end(), comparator);
      if (extrema == null)
      {
        extrema = new Extrema();
      }
      else if (comparator.compare(iter.get(i), extrema.value) != 1)
      {
        continue;
      }
      extrema.row = i;
      extrema.column = iter.index();
      extrema.value = iter.get(i);
    }
    return extrema;
  }
//</editor-fold>
//</editor-fold>
}
