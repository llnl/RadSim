// --- file: gov/llnl/rtk/calibration/CumulativePeakFinder.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.xml.bind.Reader;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Compute the location of a peak (K40) in an unclaibrated raw spectra.
 * Makes use of the slope of the inverse cumulative sum of a spectra to compute
 * the K40 peak. Ensure that the overflow bin has been removed to avoid issues
 * with this method. 
 * @author canion1
 * @author hangal1
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "cumulativePeakFinder",
        referenceable = true)
public class CumulativePeakFinder implements PeakFinder, Serializable
{

  // Default values for NaI detector (1024 channels from 0-2850 keV)
  private double expectedChannel = 525; //K40 peak channel for NaI detector (1024 channels from 0-2850 keV)
  private double empiricalChannel = 525; //K40 peak channel for NaI detector (1024 channels from 0-2850 keV)
  private double expectedCumulative = 0.0669; // Inverse cumulative sum at K40 peak/Inverse cumulative sum at 200 keV  (f(1460)/f(200))
  private double expectedLowerFaction = 0.137; //200./1461.
  
  // iterators values
  private int iterations = 15;
  private double mu = 0.5;

  //range of allowable channel values
  private int lowerChannel;
  private int upperChannel;

  //output
  private int peakChannel;

  //Constructor
  public CumulativePeakFinder()
  {
    //range of allowable channel values
    lowerChannel = (int) (expectedChannel * 0.8);
    upperChannel = (int) (expectedChannel * 1.2);
  }

  @Override
  public PeakFinderResult initialSearch(Spectrum spectrum)
  {
    double[] iCumulative = inverseCumulativeSum(spectrum);

    //Check that expectedLowerFraction should always be less than 1
    if (expectedLowerFaction > 1)
      throw new RuntimeException("expectedLowerFraction should be less than 1!");

    double lowerRange = expectedLowerFaction*expectedChannel;
    for (int i = 0; i < iterations; ++i)
    {
      double target = iCumulative[(int) lowerRange] * expectedCumulative;
      peakChannel = Arrays.binarySearch(iCumulative, target);
      if (peakChannel < 0)
      {
        peakChannel = -1 * (peakChannel + 1);
      }
      //System.out.println(lowerRange+ " " + peakChannel + " " + iCumulative[(int) peakChannel]/iCumulative[(int) lowerRange] );
      double newlower = peakChannel*expectedLowerFaction;
      lowerRange = ((1 - getMu()) * (lowerRange + 1) + getMu() * (newlower + 1));
      lowerRange = lowerRange - 1;
    }
    return new CumulativePeakFinderResult(peakChannel);
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  // ADD FAILURE MODES
  // Other methods
  public static double[] inverseCumulativeSum(Spectrum spectrum)
  {
    return inverseCumulativeSum(spectrum.toDoubles());
  }

  public static double[] inverseCumulativeSum(double[] specArray)
  {
    double[] cumuSum = new double[specArray.length];
    double prevCumul = 0;
    //Make the cumulative sum negative so that it is in ascending order for searching
    for (int i = (specArray.length - 1); i >= 0; --i)
    {
      //Warning : check that the overflow bin has been removed
      cumuSum[i] = prevCumul + -1 * specArray[i];
      prevCumul = cumuSum[i];
    }
    return cumuSum;
  }
//</editor-fold>
//<editor-fold desc="accessor" defaultstate="collapsed">
  // allowed getter and setter methods

  public void setIterations(int in)
  {
    iterations = in;
  }

  public void setLowerChannel(int in)
  {
    lowerChannel = in;
  }

  public void setUpperChannel(int in)
  {
    upperChannel = in;
  }

  public double getExpectedChannel()
  {
    return expectedChannel;
  }

  public double getExpectedCumulative()
  {
    return expectedCumulative;
  }

  public double getExpectedLowerFaction()
  {
    return expectedLowerFaction;
  }

  public int getIterations()
  {
    return iterations;
  }

  public int getLowerChannel()
  {
    return lowerChannel;
  }

  public int getUpperChannel()
  {
    return upperChannel;
  }

  /**
   * @param channel the expectedChannel to set
   */
  @Override
  public void setExpectedChannel(double channel)
  {
    this.expectedChannel = channel;
    this.peakChannel = (int) channel;
    lowerChannel = (int) (expectedChannel * 0.8);
    upperChannel = (int) (expectedChannel * 1.2);
  }

  /**
   * @param expectedCumulative the expectedCumulative to set
   */
  @Reader.Element(name = "expectedCumulative", required = true)
  public void setExpectedCumulative(double expectedCumulative)
  {
    this.expectedCumulative = expectedCumulative;
  }

  /**
   * @param expectedLowerFaction the expectedLowerRange to set
   */
  @Reader.Element(name = "expectedLowerFraction", required = true)
  public void setExpectedLowerFaction(double expectedLowerFaction)
  {
    this.expectedLowerFaction = expectedLowerFaction;
  }

  /**
   * @return the mu
   */
  public double getMu()
  {
    return mu;
  }

  /**
   * @param mu the mu to set
   */
  public void setMu(double mu)
  {
    this.mu = mu;
  }

  /**
   * @return the empiricalChannel
   */
  public double getEmpiricalChannel()
  {
    return empiricalChannel;
  }

  /** This is used to help set up the finder for use.
   *
   * @param spectrum
   * @param expected
   */
  public static void getParameters(Spectrum spectrum, double expected)
  {
    double lower = expected * 0.4;
    double[] iCumulative = inverseCumulativeSum(spectrum);
    double target = iCumulative[(int) expected] / iCumulative[(int) lower];
    System.out.println("<cumulativePeakFinder>");
    System.out.println("  <expectedCumulative>"+target+"</expectedCumulative>");
    System.out.println("  <expectedLowerFraction>"+0.4+"</expectedLowerFraction>");
    System.out.println("</cumulativePeakFinder>");
  }

  /**
   * @param empiricalChannel the empiricalChannel to set
   */
  @Reader.Element(name = "empiricalChannel", required = true)
  public void setEmpiricalChannel(double empiricalChannel)
  {
    this.empiricalChannel = empiricalChannel;
  }
//</editor-fold>

  public class CumulativePeakFinderResult implements PeakFinderResult
  {
    double peakChannel;

    public CumulativePeakFinderResult(double peakChannel)
    {
      this.peakChannel = peakChannel;
    }

    /**
     * Get peak channel gets the resulting channel predicted by the cumulative
     * sum. If it has not found a peak within range it will be equal to the
     * expectedChannel.
     *
     * @return
     */
    @Override
    public double getPeakLocation()
    {
      return peakChannel;
    }

  }
}
