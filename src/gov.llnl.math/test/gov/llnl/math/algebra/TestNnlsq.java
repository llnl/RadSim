/*
 * Copyright 2021, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.algebra;

import gov.llnl.math.IntegerArray;
import gov.llnl.math.MathExceptions;
import gov.llnl.math.MathExceptions.SizeException;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixColumnArray;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixOps;
import gov.llnl.math.matrix.MatrixViews;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author nelson85
 */
public class TestNnlsq
{

  /**
   * The driver for testing the NNLSQ coding with test small problems that has
   * rational scaling
   *
   * @param args
   * @throws SizeException
   */
  public static void main(String[] args) throws SizeException
  {
    NnlsqFactory factory = new NnlsqFactory();

    // Create an input
    Nnlsq.InputDoubleMatrix input = new Nnlsq.InputDoubleMatrix()
    {
      ArrayList<Constraint> constraints = new ArrayList<>();

      @Override
      public Collection<Constraint> getConstraints()
      {
        return constraints;
      }
    };

    // Flags for testing
    boolean useConstraints = true;
    boolean useDeferred = true;
    boolean useParallel = false;
    boolean useUnitize = false;
    boolean rational = true;

    // Pass flags to algorithm
    factory.setUseDeferred(useDeferred);
    factory.setParallel(useParallel);
    factory.setUseUnitize(useUnitize);
    Nnlsq nnlsq = factory.createSolver();

    // (M, N) arbitrarily specified here
    int M = 0;
    int N = 0;

    Matrix.ColumnAccess A = null;
    double[] RHS = null;

    if (rational == true)
    {
      M = 3;
      N = 3;

      if (useUnitize)
      {
        A = new MatrixColumnArray(
                new double[]
                {
                  3.0, 4.0, 0,
                  0, -5.0, 12.0,
                  -8.0, 0, 6.0
                }, 3, 3);
        RHS = new double[]
        {
          -3.0, -2.0, 6.0
        };
      }
      else
      {
        // We will unitize the problem ourselves
        A = new MatrixColumnArray(
                new double[]
                {
                  3.0 / 5.0, 4.0 / 5.0, 0,
                  0, -5.0 / 13.0, 12.0 / 13.0,
                  -4.0 / 5.0, 0, 3.0 / 5.0
                }, 3, 3);
        RHS = new double[]
        {
          -3.0 / 7.0, -2.0 / 7.0, 6.0 / 7.0
        };
      }
    }

    Matrix W = MatrixFactory.newMatrix(M, M);
    MatrixOps.fill(W, 0);
    MatrixOps.fill(MatrixViews.diagonal(W), 1);

    Matrix WA = MatrixOps.multiply(W, A);
    if (useConstraints)
    {
      Collection<Constraint> constraints = (Collection<Constraint>)input.getConstraints();
      double lambda = 1;
      if (useUnitize)
      {
        Constraint c = new Constraint(6 * lambda);
        c.add(1, 12 * lambda);
        c.add(2, 6 * lambda);
        constraints.add(c);
      }
      else
      {
        Constraint c = new Constraint(6 * lambda / 7.0);
        c.add(1, 12 * lambda / 13.0);
        c.add(2, 6 * lambda / 10.0);
         constraints.add(c);
      }
      M -= input.getConstraints().size();
    }

    int rows = M;
    int cols = N;
    input.use = IntegerArray.colon(0, N);
    input.regressand = new double[rows];
    input.regressors = MatrixFactory.newColumnMatrix(rows, cols);
    input.regressorsWeighted = MatrixFactory.newColumnMatrix(rows, cols);

    int length = input.regressand.length;
    for (int i = 0; i < length; i++)
    {
      input.regressand[i] = RHS[i];
    }
    if (useConstraints)
    {
      A = MatrixFactory.newColumnMatrix(M, N);
      if (!useUnitize)
      {
        A.set(0, 0, 3.0 / 5.0);
        A.set(0, 1, 0);
        A.set(0, 2, -8 / 10.0);
        A.set(1, 0, 4.0 / 5.0);
        A.set(1, 1, -5 / 13.0);
        A.set(1, 2, 0);
        RHS = new double[M];
        RHS[0] = -3.0 / 7.0;
        RHS[1] = -2.0 / 7.0;
      }
      else
      {
        A.set(0, 0, 3);
        A.set(0, 1, 0);
        A.set(0, 2, -8);
        A.set(1, 0, 4);
        A.set(1, 1, -5);
        A.set(1, 2, 0);
        RHS = new double[M];
        RHS[0] = -3;
        RHS[1] = -2;
      }
    }
    for (int i = 0; i < input.regressors.rows(); i++)
    {
      for (int j = 0; j < input.regressors.columns(); j++)
      {
        input.regressors.set(i, j, A.get(i, j));
        input.regressorsWeighted.set(i, j, WA.get(i, j));
      }
    }
    for (int k = 0; k < 1; k++)
    {
      try
      {
        double[] U = new double[N];
        double[] X = new double[N];
        nnlsq.initialize(input);
        nnlsq.solve();
        int i = 0;
        for (Nnlsq.Datum entry : nnlsq.getSolution())
        {
          if (useUnitize)
          {
            System.out.printf("i, slot, X = %d %d %f \n", i, entry.getId(), entry.getCoef());
          }
          else
          {
            int slot = entry.getId();
            double var = 7.0 / 10.0;
            if (slot == 1)
            {
              var = 7.0 / 13.0;
            }
            var *= entry.getCoef();
            System.out.printf("i, slot, coefficient = %d %d %f \n", i, entry.getId(), entry.getCoef());
            System.out.printf("i, slot, X = %d %d %f \n", i, entry.getId(), var);
          }
          i++;
        }
        double mse = nnlsq.getMSE();
        System.out.println(" MSE = " + mse);
      }
      catch (MathExceptions.ConvergenceException | MathExceptions.SizeException ex)
      {
        throw new RuntimeException(ex);
      }
    }
    System.out.println("cost=" + ((NnlsqImpl)nnlsq).cost);
    factory.dispose();
  }

}
