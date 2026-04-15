/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.algebra;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.DoubleUtilities;
import gov.llnl.math.IntegerArray;
import gov.llnl.math.MathExceptions;
import gov.llnl.math.MathExceptions.SizeException;
import gov.llnl.math.MathPackage;
import gov.llnl.math.matrix.special.MatrixSymmetric;
import gov.llnl.utility.CoreDump;
import gov.llnl.utility.LoggerStream;
import gov.llnl.utility.LoggerUtilities;
import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.annotation.Internal;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * Workspace is responsible for computation required to solve the problem.
 *
 */
class NnlsqImpl implements Nnlsq, Serializable
{
  private static final long serialVersionUID = UUIDUtilities.createLong("Nnlsq-v1");

  public static final double EPSILON = 1e-15;
  public static final double UNITIZE_THRESHOLD = 1e-6;
  
  Input input;
  // input holds
  //    regressors         A     [M,N]
  //    regressorsWeighted W^T*A [M,N]
  //    setUse (N1)
  double[] beta; // A_p*x   [M]
  double[] regressand; // B     [M]
  double[] regressandWeighted; // W.B    [M]
  double regressandScale; // 1/sqrt(B^T*W*B)
  double regressandLength; // sqrt(B^T*W*B)
  NnlsqRegressor lastAdd; // the old regressor for max demand

  // Sets
  NnlsqRegressorSet setIn;
  NnlsqRegressorSet setOut;
  NnlsqRegressorSet setCycled;
  NnlsqRegressorSet setDeferred;

  // Cycle check
  long cycleState = 0;
  boolean inCycle = false;

  // Options
  boolean useCache = true;
  boolean useDeferred = true;
  boolean useScaleDemand = true;
  boolean useUnitize = true;
  boolean useConstraints = false;
  double toleranceDemand;

  // Parallel control
//  @Debug public NnlsqOperations processor = null;
  // Product Matrix
  transient ProductMatrixCache productMatrixCache;
  MatrixSymmetric productMatrix;
  long cost = 0;
  Collection<? extends Constraint> constraints;

  /**
   * Set options must be called before initialize. Options cannot be changed
   * once the initialize routine has been called.
   *
   * @param useCache
   * @param useDeferred
   * @param useScaleDemand
   * @param useUnitize
   */
  public void setOptions(boolean useCache, boolean useDeferred, boolean useScaleDemand, boolean useUnitize)
  {
    this.useCache = useCache;
    this.useDeferred = useDeferred;
    this.useScaleDemand = useScaleDemand;
    this.useUnitize = useUnitize;
  }

  @Override
  public Output<? extends Datum> getSolution()
  {
    return setIn;
  }

//<editor-fold desc="initialize">
  @Override
  public void initialize(Input input2) throws SizeException
  {
    this.input = input2;
    this.constraints = input2.getConstraints();
    useConstraints = (constraints != null) && (constraints.size() > 0);

    // Allow the input to finish all prerequisite tasks.
    input.initialize();

    // Copy over regressand to working memory
    this.regressand = input.getRegressand();
    this.regressandWeighted = input.getRegressandWeighted();

    if (this.regressand.length != this.regressandWeighted.length)
      throw new MathExceptions.SizeException("Regressand size mismatch");

    // Rescale problem for length of regressand
    this.regressandLength = Math.sqrt(
            DoubleArray.multiplyInner(this.regressand, 0, this.regressandWeighted, 0, this.regressand.length));

    if (this.regressandLength == 0)
      return;

    // Set up memory for the problem
    initializeMemory(input.getRegressorSet());

    if (useConstraints)
    {
      // tag regressors that are constrained
      for (Constraint c : this.constraints)
      {
        for (int j = 0; j < c.points.size(); j++)
        {
          int c1 = c.points.get(j).regressorId;
          NnlsqRegressor regressor = setOut.findById(c1);
          if (regressor != null)
            regressor.constrainTag = 1;
        }
      }

      // need to modify regressandLength to include contribution from
      // regressand constraints, no room to simplify further here
      // add contribution from regressand constraints
      double value = this.regressandLength * this.regressandLength;

      for (Constraint c : this.constraints)
      {
        value += DoubleUtilities.sqr(c.rhs_);
      }

      // compute the length
      this.regressandLength = Math.sqrt(value);
    }

    this.regressandScale = 1.0 / regressandLength;

    computeRegressorsScale();

    // Rescale problem to a constant regressand length
    double factor = this.useUnitize ? regressandScale : 1;
    for (NnlsqRegressor entry : setOut)
    {
      // regressandProjection = (B'*W*A)
      double[] a = input.getRegressandWeighted();
      double[] b = entry.regressor;
      int rows = input.getNumVariables();
      int offset = entry.offset;
      double value = DoubleArray.multiplyInner(a, 0, b, offset, rows);

      if (useConstraints && entry.constrainTag == 1)
      {
        // this regressor is constrained
        int found = 0;
        // contribution from regressorConstraint dot regressandConstraints
        for (Constraint c : this.constraints)
        {
          for (int j = 0; j < c.points.size(); j++)
          {
            Constraint.ConstraintDatum cd = c.points.get(j);
            if (cd.regressorId == entry.id)
            {
              value += cd.coefficent * c.rhs_;
              found++;
            }
          }
        }

        if (found == 0)
          MathPackage.getInstance().getLogger().warning(" constraint is not found! found = " + found);
      }

      entry.regressandProjection = factor * value;
    }
    computeTolerance();
  }

  /**
   * Sizes all of the arrays for solving with a restriction on which regressors
   * to use.
   *
   * @param setUse is the list of regressors to use or null if all should be
   * used.
   */
  void initializeMemory(int[] setUse)
  {
    int m = this.input.getNumVariables();

    if (setUse == null)
      setUse = IntegerArray.colon(0, this.input.getNumRegressors());

    this.beta = new double[m];

    this.setOut = new NnlsqRegressorSet(this);
    this.setIn = new NnlsqRegressorSet(this);
    this.setDeferred = new NnlsqRegressorSet(this);
    this.setCycled = new NnlsqRegressorSet(this);

    // Copy the list of inputs to our internal structure.    
    for (int id : setUse)
    {
      NnlsqRegressor regressor = new NnlsqRegressor(id,
              input.getRegressor(id),
              input.getRegressorWeighted(id),
              input.getRegressorOffset(id));
      this.setOut.add(regressor);
    }

    if (useCache)
      this.productMatrixCache = new ProductMatrixCache();
  }

  void computeTolerance()
  {
    if (useScaleDemand)
    {
      toleranceDemand = 4e-16;
      if (!this.useUnitize)
        toleranceDemand *= this.regressandLength;
    }
    else
    {
      toleranceDemand = 0;
      for (NnlsqRegressor entry : setOut)
      {
        double sum = 0;
        double[] regressor = entry.regressor;
        int offset = entry.offset;
        for (int j = 0; j < input.getNumVariables(); j++)
        {
          sum += Math.abs(regressor[offset + j]);
        }
        sum *= 1e-14;
        if (toleranceDemand < sum)
          toleranceDemand = sum;
      }
    }
  }

  /**
   * Computes the length of each regressor to reduce issues with scaling. Used
   * only if useUnitize is set to true.
   */
  void computeRegressorsScale()
  {
    // used if either demand or regressors are being scaled
    if (!this.useScaleDemand && !this.useUnitize)
      return;

    // S_i=1/|A_i|
    int m = input.getNumVariables();

    // Compute regressorsScale for each regressor
    for (NnlsqRegressor entry : setOut)
    {
      double v = DoubleArray.multiplyInner(entry.regressor,
              entry.offset, entry.regressorWeighted,
              entry.offset, m); // does not occur
      // add contribution from regressor constraints at this slot
      if (useConstraints && entry.constrainTag == 1)
      {
        for (Constraint c : this.constraints)
        {
          for (int k = 0; k < c.points.size(); k++)
          {
            if (c.points.get(k).getId() == entry.id)
              v += DoubleUtilities.sqr(c.points.get(k).coefficent);
          }
        }
      }
      if (v == 0)
        v = 1;
      // regressorsScale= 1/sqrt(A'*W*A)
      entry.scale = 1.0 / Math.sqrt(v);
    }
  }
//</editor-fold>

  @Override
  public void solve(Input input) throws MathExceptions.ConvergenceException
  {
    this.initialize(input);
    solve();
  }

  @Override
  public void solve() throws MathExceptions.ConvergenceException
  {
    if (input == null)
      throw new RuntimeException("Input not set.");

    // Clear state variables
    this.inCycle = false;
    this.cycleState = 0;

    try
    {
      // System.out.println("Solve");
      int m = input.getNumVariables();
      int n = input.getNumRegressors();

      // Determine the scaling factor for the problem
      if (this.regressandLength == 0)
        return;

      // a number that measures difference between coefficients
      int zeroDiffCount = 0; // count number of zero diffs

      // The maximum iterations is a function of the problem size
      int iterationsMax = Math.max(5 * input.getNumVariables(), 3 * input.getNumRegressors()) + 10;
      int iterations = 0;

      outer:
      while (iterations < iterationsMax)
      {
        inner:
        while (iterations < iterationsMax)
        {
          iterations++;

          //  Step 3 - We must never chose more columns than rows
          // (without we may fail to realize convergence)
          // Terminate if all regressors used or no regressor can contribute
          if (this.setIn.size() >= m || this.setOut.size() == 0)
          {
            break;
          }

          // Compute the demand
          NnlsqRegressor max = this.computeDemand();

          // Step 4 find the max w corresponding to a regressor in Z
          lastAdd = max;

          if (max.demand <= toleranceDemand)
          {
            break inner;
          }

          // Step 5 move to P
          this.transferIn(max);

          // Deferred optimization
          if (useDeferred)
            this.defer();

          // Step 5 solve with positive constraints for the new set P
          this.fitPositive(max);

          // I don't see how this logic would ever be triggered.
          // quit if no difference between new and last solutions
          // this is much faster and not affect the solution little
          double diff = this.updateSolution();
          if (diff == 0.0)
          {
            zeroDiffCount++;
            if (zeroDiffCount > 6)
              break outer;
          }

          // Cycle buster logic
          if (this.inCycle)
          {
            // Okay here is where we should consider if we are making progress
            // toward the solution.  Unfortunately, we will need a cycling problem 
            // to be able to test this section.

            // The getMSE function is not valid at this point in time, as
            // the coefficients are still unitized.  Further, getMSE deals
            // with the unconstrained MSE.  It is quite possible for the
            // mse to be climbing while the constrained mse is falling.
            // Thus using the MSE would certainly lead to unwanted behavior.
            throw new UnsupportedOperationException("Cycle detected");
          }

        }

        // If any deferred then move back to consideration, otherwise solved
        if (this.setDeferred.size() != 0)
          this.restoreDeferred();
        else
          break;
      }

      if (this.useUnitize)
      {
        // Correct answer for the length of B
        // convert from u to x = u*sqrt(B'*W*B)/sqrt(A'*W*A)
        for (NnlsqRegressor entry : setIn)
        {
          entry.coef *= this.regressandLength * entry.scale;
        }
      }

      if (iterations == iterationsMax)
      {
        try (LoggerStream ls = LoggerStream.create(MathPackage.getInstance().getLogger(), Level.WARNING))
        {
          //System.out.printf(" iterations == %d reached maximum allowed number! \n", iterations);
          ls.printf(" \n quitting condition reached iterations == %d \n", iterations);
          ls.printf(" num small diff count %d \n", zeroDiffCount);
          //        ls.printf(" the maximum demand computed is %e \n", lastDemand);
          // ls.printf(" num positive demands = %d, setIn size = %d \n", positiveDemandCount, setIn.size());
          ls.printf(" num regressand M = %d, num regressors N = %d \n\n", m, n);
        }

        throw new MathExceptions.ConvergenceException("Maximum iterations reached");
      }
    }

    //Special logic to save bad inputs for evaluation
    catch (Exception ex)
    {
      CoreDump dump = new CoreDump("gov.llnl.math.Nnlsq", true);
      dump.add("nnlsq", this);
      dump.add("input", this.input);
      dump.add("exception", ex);
      dump.write("nnlsq");
      throw ex;
    }
  }

  /**
   * Compute the current estimate of the measurement as the product of
   * coefficients and regressors.
   */
  void updateBeta()
  {
    int m = this.input.getNumVariables();
    // \beta= A_P*x_P
    DoubleArray.fill(beta, 0);

    for (NnlsqRegressor entry : setIn)
    {
      // scale if the regressors are scaled
      double factor = this.useUnitize ? entry.scale : 1.0;
      // We want G*u,  we compute (A/sqrt(A'*W*A)*u) = (A*u(sqrt(A'*W*A))
      DoubleArray.addAssignScaled(beta, 0,
              entry.regressor, entry.offset, m, entry.coef * factor);
    }
  }

  /**
   * Computes the demand for all out regressors with respect for those
   * regressors that are already in.
   */
  NnlsqRegressor computeDemand()
  {
    // \beta = A_P*x_P/sqrt(B'*W*B)
    // \omega =  (B^T*W*A - A_Z*W*\beta) / sqrt(B'*W*B)             without scaling
    // \omega =  (B^T*W*A - A_Z*W*\beta) / sqrt(A'*W*A) / sqrt(B'*W*B)  with scaling
    // Step 1- Initialize demand as B^T*A
    {
      for (NnlsqRegressor entry : setOut)
      {
        // B^T*W*A /sqrt(B'*W*B) if unitized
        // B^T*W*A,              otherwise
        entry.demand = entry.regressandProjection;
      }
    }

    // Step 2 - compute w
    if (setIn.size() != 0)
    {
      // Profiling check
      cost += setOut.size() * input.getNumVariables();
      updateBeta(); // beta is scaled by sqrt(B^T*W*B)
      // compute bete equivalent for contribution from constraints
      // "sigma" is the constraint corresponding to "beta"
      int numConstraints = 0;
      if (useConstraints)
        numConstraints = this.constraints.size();
      double[] sigma = new double[numConstraints];
      if (useConstraints)
      {
        int rank = 0;
        // loop over constraints
        for (Constraint c : this.constraints)
        {
          for (NnlsqRegressor entry : setIn)
          {
            int slot = entry.id;
            for (int k = 0; k < c.points.size(); k++)
            {
              if (c.points.get(k).regressorId == slot)
                sigma[rank] += c.points.get(k).coefficent * entry.coef;
            }
          }
          rank++;
        }
      }

      // FIXME replace the parallel processing unit
//      if (processor != null)
//      {
//        // processor.projectDemands(demand, beta, input, setOut);
//        throw new UnsupportedOperationException("Multcore needs to be reimplemented");
//      }
//      else
      {
        // w_Z= A_Z'*W*B- A_Z'*W*\beta
        int m = input.getNumVariables();
        for (NnlsqRegressor entry : setOut)
        {
          double res = DoubleArray.multiplyInner(beta, 0, entry.regressorWeighted,
                  entry.offset, m);
          // add contribution from constraints
          if (useConstraints && entry.constrainTag == 1)
          {
            int rank = 0;
            for (Constraint c : this.constraints)
            {
              for (int k = 0; k < c.points.size(); k++)
              {
                if (c.points.get(k).regressorId == entry.id)
                  res += sigma[rank] * c.points.get(k).coefficent;
              }
              rank++;
            }
          }
          entry.demand -= res;

          // Deal with round-off errors
          if (entry.demand > 0.0 && entry.demand < EPSILON * Math.abs(res))
            entry.demand = 0.0;

          // If the regressors are scaled differently, we need to apply the
          // scale to determine the correct element to add.           
          else if (this.useScaleDemand)
            // Correct demand for length
            // G_Z*W*(H-G_P*u) have A_Z*W*(H-G_P*u)
            entry.demand *= entry.scale;

          // Apply an offset if a variable was kicked previously
          entry.demand += entry.demandOffset;
        }
      }
    }

    // Find the maximum demand
    NnlsqRegressor max = null;
    for (NnlsqRegressor entry : setOut)
    {
      if (max == null || entry.demand > max.demand)
        max = entry;
    }
    return max;
  }

//<editor-fold desc="deferred">
  /**
   * Transfers all regressors that have insufficient demand to deferred.
   */
  void defer()
  {
    // Set is modified while indexing so must use while loop
    Iterator<NnlsqRegressor> iter = setOut.iterator();
    while (iter.hasNext())
    {
      NnlsqRegressor entry = iter.next();
      if (entry.demand > 0.0)
        continue;

      // Transfer the item to set deferred for later consideration
      iter.remove();
      setDeferred.add(entry);
    }
  }

  /**
   * Transfers all deferred regressors back into consideration.
   */
  void restoreDeferred()
  {
    setOut.transferAll(setDeferred);
  }
//</editor-fold>
//<editor-fold desc="transfer">  

  /**
   * Moves an regressor from setOut to setIn and updates the cache accordingly.
   *
   * @param index_z is the index in setOut to add
   * @return is the regressor number that was added
   */
  void transferIn(NnlsqRegressor add)
  {
    // Handle the cycle catcher
    if (add.lastUsed == this.cycleState)
    {
//      System.out.println("Cycle suspected last=" + add.lastUsed + " cycleState=" + this.cycleState);
      inCycle = true;
    }
    add.lastUsed = cycleState;
    cycleState ^= add.hash;

    // zero previous state    
    add.coef = 0;  // zero the component

    // Transfer from set out to set in
    setOut.remove(add);
    setIn.add(add);
    add.convergenceTag++;
  }

//</editor-fold>
  /**
   * Commits revised solution to the current solution.
   *
   * @param coef solution at last iteration
   * @return difference between this and the last solutions
   */
  double updateSolution()
  {
    // compute difference between new solution and solution last cycle
    double diff = 0.0;
    for (NnlsqRegressor entry : setIn)
    {
      // Previous formula was min( |(Zp-Xp)/Xp|, |(Zp-Xp)| )
      double value = Math.abs((entry.coef - entry.update) / (entry.coef + entry.update));
      if (diff < value)
        diff = value;
    }

    for (NnlsqRegressor entry : setIn)
    {
      entry.coef = entry.update;
    }

    return diff;
  }

  /**
   * Identifies all regressors that do no contribute to current solution and
   * removes them.
   *
   * @param zp is the revised coefficients
   * @param x is the existing coefficients prior to removal
   * @param tol is the tolerance for removal
   */
  void resetActingSets(NnlsqRegressor lastAdd)
  {
    // Copy x into u
    for (NnlsqRegressor entry : setIn)
    {
      entry.revised = entry.coef;
    }

    NnlsqRegressor minimum = null;
    double alpha = Double.MAX_VALUE;
    for (NnlsqRegressor entry : setIn)
    {
      double zvar = entry.update;
      if (zvar <= 0)
      {
        double uvar = entry.revised;
        double y = uvar / (uvar - zvar);
        if (y < alpha)
        {
          minimum = entry;
          alpha = y;
        }
      }
    }
    if (minimum == null)
      alpha = 0;

    for (NnlsqRegressor entry : setIn)
    {
      entry.revised += alpha * (entry.update - entry.revised);
    }

    // Copy u into x
    for (NnlsqRegressor entry : setIn)
    {
      entry.coef = entry.revised;
    }

    // mark regressor that causes non-convergence by round-off
    for (NnlsqRegressor entry : setIn)
    {
      if (entry.coef <= 0.0 && entry.convergenceTag > 0)
      {
        // mark regressor to be tranferred out, but was just added
        entry.convergenceTag++;
      }
      else
      {
        entry.convergenceTag = 0;
      }
    }

    // The min alpha must always be removed (regardless of rounding errors)
    if (minimum != null)
      minimum.coef = 0;

    // Remove any other regressors that have been forced to zero
    // This is much more complicated then it appears.  When we remove elements 
    // we need to swap the rows in the cache.  Thus we can't just proceed 
    // through the list.   Running through the list backward is better because
    // the item we swap is guaranteed to have been checked. This prevents us
    // form having to hold position in the list.
    Iterator<NnlsqRegressor> iter = setIn.decendingIterator();
    while (iter.hasNext())
    {
      NnlsqRegressor next = iter.next();
      if (next.coef <= 0)
      {
        // Remove the pattern from the cycle state
        cycleState ^= next.hash;
        iter.remove();
        setOut.add(next);
        if (next == lastAdd)
        {
          next.demandOffset -= next.demand;
        }
      }
    }
  }

  void fitPositive(NnlsqRegressor lastAdd)
  {
    try
    {
      while (true)
      {
        // Step 6 solve problem for just the regressors in P
        // We will need to copy here for now (if using cache)
        // zp the local LS solution for setP
        int sz = setIn.size();
        double[] zP = new double[sz];
        {
          int i = 0;
          if (this.useUnitize)
          {
            for (NnlsqRegressor entry : setIn)
              zP[i++] = entry.regressandProjection * entry.scale;
          }
          else
          {
            for (NnlsqRegressor entry : setIn)
              zP[i++] = entry.regressandProjection;
          }

          productMatrix = computeProductMatrix();

          // I use SymmetricLUFactorization inverse with SVD treatment, not using cached values
          // Solve A_P'*W*A_P x_P= A_P'*W*Y or G_P'*W*G_P*u=G_P'*W*H
          SymmetricLUFactorization cf = new SymmetricLUFactorization();
          cf.decompose(productMatrix);
          double[] zPnew = cf.solve(zP);

//          if (cf.getNullity() > 0)
//            throw Exceptions.builder(new MathException("nullity violation"))
//                    .put("productMatrix", productMatrix)
//                    .put("zP", zP)
//                    .put("zPnew", zPnew)
//                    .get();

          // Copy the solution back to the solution list form
          i = 0;
          for (NnlsqRegressor entry : setIn)
          {
            entry.update = zPnew[i++];
          }
        }

        // If all greater than zero, x=z and proceed to Step 2
        double minzp = Double.MAX_VALUE;
        for (NnlsqRegressor entry : setIn)
          if (entry.update < minzp)
            minzp = entry.update;

        if (minzp > 0)
          break;

        this.resetActingSets(lastAdd);
      }
    }
    catch (MathExceptions.SizeException ex)
    {
      throw new RuntimeException(ex); // this cannot happen
    }
  }

  final double computeProductMatrixEntry(NnlsqRegressor entryI, NnlsqRegressor entryJ)
  {
    int m = input.getNumVariables();
    // Compute A_P'*W*A_P
    double v = DoubleArray.multiplyInner(entryI.regressor,
            entryI.offset, entryJ.regressorWeighted,
            entryJ.offset, m);
    if (useConstraints && entryI.constrainTag == 1 && entryJ.constrainTag == 1)
    {
      // if both regressors "slotI" and "slotJ" are constrained
      // add contribution from constraints
      double contribution = 0.0;
      // loop over all constraints
      for (Constraint c : this.constraints)
      {
        double vI = 0.0;
        double vJ = 0.0;

        for (int k = 0; k < c.points.size(); k++)
        {
          Constraint.ConstraintDatum cd = c.points.get(k);
          if (cd.regressorId == entryI.id)
            vI = cd.coefficent;
          if (cd.regressorId == entryJ.id)
            vJ = cd.coefficent;
        }
        contribution += vI * vJ;
      }
      v += contribution;
    }

    // Compute G_P'*W*G_P if unitize
    if (this.useUnitize)
      v *= entryJ.scale * entryI.scale;
    return v;
  }

  /**
   * Compute the product matrix for the current setIn.
   *
   * @return a symmetric matrix.
   */
  MatrixSymmetric computeProductMatrix()
  {
    int sz = setIn.size();
    productMatrix = new MatrixSymmetric(sz);
    int i = 0;
    for (NnlsqRegressor entryI : setIn)
    {
      int j = 0;
      for (NnlsqRegressor entryJ : setIn)
      {
        if (j > i)
          break;
        double value;
        if (this.useCache)
          value = productMatrixCache.get(entryI, entryJ);
        else
          value = this.computeProductMatrixEntry(entryI, entryJ);

        productMatrix.set(j, i, value);
        ++j;
      }
      ++i;
    }

    // check product matrix scaling if unitizer is used
    if (useUnitize)
    {
      for (int k = 0; k < productMatrix.rows(); k++)
      {
        double value = productMatrix.get(k, k);
        if (Math.abs(value - 1.0) > UNITIZE_THRESHOLD)
          LoggerUtilities.format(MathPackage.getInstance().getLogger(), Level.WARNING,
                  "%dth diagonal is %f", k, value);
      }
    }

    return productMatrix;
  }

  // the mean squared errorA
  // how do we get the constrainted MSE?
  @Override
  public double getMSE()
  {
    // Compute e1=Y-A*x
    // Compute e2=W*Y-W*A*x
    double[] e1 = this.regressand.clone(); // e1=Y
    double[] e2 = this.regressandWeighted.clone(); // e2=W*Y
    // for e1-=A_i*(-x_i) all i in setIn

    int n = this.regressand.length;
    for (NnlsqRegressor entry : setIn)
    {
      DoubleArray.addAssignScaled(e1, 0,
              entry.regressor, entry.offset,
              n, -entry.coef);
      DoubleArray.addAssignScaled(e2, 0,
              entry.regressorWeighted, entry.offset,
              n, -entry.coef);
    }
    // MSE=e1'*e2
    return DoubleArray.multiplyInner(e1, e2);
  }

  @Override
  public double[] getProjection()
  {
    double[] out = new double[this.regressand.length];
    for (NnlsqRegressor entry : setIn)
    {
      DoubleArray.addAssignScaled(
              out, 0,
              entry.regressor, entry.offset,
              this.regressand.length, entry.coef);
    }
    return out;
  }

//<editor-fold desc="productMatrix">
  @Internal
  static final class ProductMatrixEntry implements Serializable
  {
    private static final long serialVersionUID = UUIDUtilities.createLong("ProductMatrixEntry");
    final ProductMatrixEntry next;
    final int id1, id2;
    final double value;

    private ProductMatrixEntry(int id1, int id2, double value, ProductMatrixEntry next)
    {
      this.id1 = id1;
      this.id2 = id2;
      this.value = value;
      this.next = next;
    }
  }

  @Internal
  class ProductMatrixCache implements Serializable
  {
    private static final long serialVersionUID = 5845149207535894039l;
    ProductMatrixEntry[] cache = new ProductMatrixEntry[2048];

    double get(NnlsqRegressor r1, NnlsqRegressor r2)
    {
      int key = (int) ((r1.hash ^ r2.hash) & 0x3ff);
      int id1 = r1.id;
      int id2 = r2.id;
      if (id1 > id2)
      {
        id1 = r2.id;
        id2 = r1.id;
      }

      ProductMatrixEntry pme = this.cache[key];
      while (pme != null)
      {
        if (pme.id1 == id1 && pme.id2 == id2)
        {
          return pme.value;
        }
        pme = pme.next;
      }

      // Failed... need a new value
      double value = computeProductMatrixEntry(r1, r2);
      this.cache[key] = new ProductMatrixEntry(id1, id2, value, this.cache[key]);
      return value;
    }
  }
//</editor-fold>
}
