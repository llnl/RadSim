/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

/**
 * Operations for complex vectors.
 *
 * This is a minimal set of operations required for Fourier transform
 * operations. It may expand to addition operations if we require it for wavelet
 * transforms.
 *
 * @author nelson85
 */
public class ComplexVectorOps
{

  static public ComplexVector multiply(ComplexVector a, ComplexVector b)
  {
    double[] ra = a.getReal();
    double[] ia = a.getImag();
    double[] rb = b.getReal();
    double[] ib = b.getImag();
    MathAssert.assertEqualLength(ra, rb);
    
    int n = ra.length;
    double[] ro = new double[n];
    double[] io = new double[n];
    for (int i=0;i<n;++i)
    {
      ro[i]=ra[i]*rb[i]-ia[i]*ib[i];
      io[i]=ra[i]*ib[i]+ia[i]*rb[i];
    }
    return ComplexVector.create(ro, io);
  }
  
  static public ComplexVector multiplyAssign(ComplexVector a, double scale)
  {
    DoubleArray.multiplyAssign(a.getReal(), scale);
    DoubleArray.multiplyAssign(a.getImag(), scale);
    return a;
  }

  static public ComplexVector divideAssign(ComplexVector a, double v)
  {
    DoubleArray.divideAssign(a.getReal(), v);
    DoubleArray.divideAssign(a.getImag(), v);
    return a;
  }

  static public ComplexVector copyOfRange(ComplexVector x, int start, int end)
  {
    return new ComplexVectorImpl(DoubleArray.copyOfRange(x.getReal(), start, end),
            DoubleArray.copyOfRange(x.getImag(), start, end));
  }
  
  static public ComplexVector addAssign(ComplexVector x, ComplexVector y)
  {
    DoubleArray.addAssign(x.getReal(), y.getReal());
    DoubleArray.addAssign(x.getImag(), y.getImag());
    return x;
  }

    static public ComplexVector addAssignScaled(ComplexVector x, ComplexVector y, double scale)
  {
    DoubleArray.addAssignScaled(x.getReal(), y.getReal(), scale);
    DoubleArray.addAssignScaled(x.getImag(), y.getImag(), scale);
    return x;
  }

}
