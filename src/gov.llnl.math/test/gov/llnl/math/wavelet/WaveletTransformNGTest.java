/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.wavelet;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixOps;
import gov.llnl.utility.ArrayEncoding;
import java.text.ParseException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for WaveletTransform.
 */
public class WaveletTransformNGTest
{

  @Test
  public void testForward() throws WaveletNotFoundException, ParseException
  {
    double[] x = new double[16];
    x[5] = 1;
    WaveletTransform instance = WaveletTransformFactory.newTransform("daub4");

    double[] expResult = ArrayEncoding.decodeDoubles(
            "eJyLdrFmYGBwsClgAIM9cyD0/gMTag9OunZ7/5kN+7mzo0XtXx3Ze+3xx5b9917cd"
            + "fc4JbCnBaLOpgeqD6rfpgPKb4DyoTQMwPg2FVB1CVD7dm+rehH+oX3/wYk7Ylri"
            + "T+w/etu2REd0lv21JZKL8pJy7Z8H8Vw0l5iyf3nO1wsbk8r2P0jJE1cUEALri3D"
            + "0sV//HwjWu8HdAaMnMGAFe6Di+yH6rPcfYCozmr30vf2BH2Vn7tcttr8SNUlj2Z"
            + "wc+9szT+qGvbhv/4ibyfOxQ4b9IZHWV2evLt8PVpf6cv+F2rL6vX1l+29zH5wX3"
            + "zxr/0mHrWtXXDi6f/OMPhFO5Rn714HCK+vF/gmg8Fx8ASLOzrV/94MzXy41ue3f"
            + "8+eO0EETA/u7969m6xfn2l/yaCooMTGyPyozzdZFM8d+Fyjcv5y0nwOMh5zIBPs"
            + "Oh6bJGx037J/KsMvS10dh/8Y1PfcbS+6AzTlwr8Z+Y0L+DR25AvsTKsv6d/kZ2J"
            + "+/d/dhT62F/ZWrk0pPlFnYX703K/DhxgP21z+s4hIRmGAPdvdMVgAIBtv7");
    Matrix result = instance.forward(x, 5);
    assertTrue(DoubleArray.equivalent(result.flatten(), expResult));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testForwardFail() throws WaveletNotFoundException
  {
    WaveletTransform instance = WaveletTransformFactory.newTransform("daub4");
    instance.forward(new double[10], 0);
  }

  /**
   * Test of forward and reconstruct method, of class WaveletTransform.
   */
  @Test
  public void testLoopback() throws WaveletNotFoundException
  {

    double[] in =
    {
      1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    double tolerance = 2e-11;
    for (int i = 0; i < in.length; ++i)
    {
      double[] x = WaveletUtilities.circShift1D(in, i);

      WaveletTransform wt = WaveletTransformFactory.newTransform("daub6");
      Matrix wc = wt.forward(x, 20);
      Matrix rc = MatrixOps.sumOfEachRow(wt.reconstruct(wc));
      Matrix err = MatrixOps.subtract(rc, MatrixFactory.newColumnMatrix(x));
      assertTrue(DoubleArray.findMaximumAbsolute(err.flatten()) < tolerance);
    }
  }

  /**
   * Test of smooth method, of class WaveletTransform.
   */
  @Test
  public void testSmooth() throws WaveletNotFoundException, ParseException
  {
    double[] in =
    {
      1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    WaveletTransform instance = WaveletTransformFactory.newTransform("daub4");
    {
      double[] expResult = ArrayEncoding.decodeDoubles(
              "eJyLdrFmYGAQsL//Hwhen7O/CKJ/fbF1MXwAFGfYPx/EX2/BAAV7"
              + "aqB0GwNWYNMAlQ/ALg41T9HWxXQBiA+zDwAH6SoD");
      double[] result = instance.smooth(in, 1);
      assertTrue(DoubleArray.equivalent(result, expResult));
    }

    {
      double[] expResult = ArrayEncoding.decodeDoubles(
              "eJyLdrFmYGAQsD//Hwge/bI/BaLfFNofBNE/N9hvaABKc8fb2vmAGA"
              + "z7J4LEF0js7wfRswX214PowwV7WTQYwPIQvgNEftYLqHoWWzt/sH6o"
              + "eSlQ87dC7SsCAIXDTnU=");
      double[] result = instance.smooth(in, 2);
      assertTrue(DoubleArray.equivalent(result, expResult));
    };

  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testSmoothFail() throws WaveletNotFoundException
  {
    WaveletTransform instance = WaveletTransformFactory.newTransform("daub4");
    instance.smooth(new double[10], 0);
  }

}
