// --- file: gov/llnl/rtk/filter/SmoothingSpectralFilter.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.filter;

import gov.llnl.math.MathExceptions;
import gov.llnl.math.Smoothing;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixColumnArray;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;

/**
 *
 * @author nelson85
 */
@ReaderInfo(SmoothingSpectralFilterReader.class)
public class SmoothingSpectralFilter implements SpectralFilter
{
  DoubleUnaryOperator coef;

  public static SmoothingSpectralFilter create(double range)
  {
    SmoothingSpectralFilter out = new SmoothingSpectralFilter();
    out.coef = p -> range*p;
    return out;
  }

  @Override
  public Matrix apply(Matrix in) throws MathExceptions.SizeException
  {
    return Smoothing.smoothRange(in, 0, in.rows(), coef);
  }

  @Override
  public double[] apply(double[] in) throws MathExceptions.SizeException
  {
    MatrixColumnArray m = MatrixFactory.wrapColumnVector(in);
    return this.apply(m).copyColumn(0);
  }

}
