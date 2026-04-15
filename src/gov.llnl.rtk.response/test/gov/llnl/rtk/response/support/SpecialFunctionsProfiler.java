/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import static gov.llnl.math.SpecialFunctions.exp;
import static gov.llnl.math.SpecialFunctions.erf;
import static gov.llnl.math.SpecialFunctions.erfc;
import static gov.llnl.math.SpecialFunctions.erfcx;

/**
 *
 * @author nelson85
 */
public class SpecialFunctionsProfiler
{
  static public int TRIALS = 100_000_000;

  public static double profileExp()
  {
    long startTime = System.nanoTime();
    double u = 0;
    for (int i = 0; i < TRIALS; ++i)
    {
      u += exp(-.165612);
      u -= exp(-.165612);
      u += exp(-.165612);
      u -= exp(-.165612);
      u += exp(-.165612);
      u -= exp(-.165612);
      u += exp(-.165612);
      u -= exp(-.165612);
      u += exp(-.165612);
      u -= exp(-.165612);
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Exp: " + duration * 1e-9);
    return u;
  }

  public static double profileExpNative()
  {
    long startTime = System.nanoTime();
    double u = 0;
    for (int i = 0; i < TRIALS; ++i)
    {
      u += Math.exp(-.165612);
      u -= Math.exp(-.165612);
      u += Math.exp(-.165612);
      u -= Math.exp(-.165612);
      u += Math.exp(-.165612);
      u -= Math.exp(-.165612);
      u += Math.exp(-.165612);
      u -= Math.exp(-.165612);
      u += Math.exp(-.165612);
      u -= Math.exp(-.165612);
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Exp (Native): " + duration * 1e-9);
    return u;
  }

  public static double profileExpStrict()
  {
    long startTime = System.nanoTime();
    double u = 0;
    for (int i = 0; i < TRIALS; ++i)
    {
      u += StrictMath.exp(-.165612);
      u -= StrictMath.exp(-.165612);
      u += StrictMath.exp(-.165612);
      u -= StrictMath.exp(-.165612);
      u += StrictMath.exp(-.165612);
      u -= StrictMath.exp(-.165612);
      u += StrictMath.exp(-.165612);
      u -= StrictMath.exp(-.165612);
      u += StrictMath.exp(-.165612);
      u -= StrictMath.exp(-.165612);
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Exp (Strict): " + duration * 1e-9);
    return u;
  }

  public static double profileErf()
  {
    long startTime = System.nanoTime();
    double u = 0;
    for (int i = 0; i < TRIALS; ++i)
    {
      u += erf(0.2);
      u -= erf(0.2);
      u += erf(0.2);
      u -= erf(0.2);
      u += erf(0.2);
      u -= erf(0.2);
      u += erf(0.2);
      u -= erf(0.2);
      u += erf(0.2);
      u -= erf(0.2);
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Erf: " + duration * 1e-9);
    return u;
  }

  public static double erfNative(double x)
  {
    // From https://en.wikipedia.org/wiki/Abramowitz_and_Stegun
    // constants
    final double a1 = 0.254829592;
    final double a2 = -0.284496736;
    final double a3 = 1.421413741;
    final double a4 = -1.453152027;
    final double a5 = 1.061405429;
    final double p = 0.3275911;
    // Save the sign of x
    int sign = 1;
    if (x < 0)
      sign = -1;
    x = Math.abs(x);
    double t = 1.0 / (1.0 + p * x);
    double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
    return sign * y;
  }

  public static double profileErfNative()
  {
    long startTime = System.nanoTime();
    double u = 0;
    for (int i = 0; i < TRIALS; ++i)
    {
      u += erfNative(0.2);
      u -= erfNative(0.2);
      u += erfNative(0.2);
      u -= erfNative(0.2);
      u += erfNative(0.2);
      u -= erfNative(0.2);
      u += erfNative(0.2);
      u -= erfNative(0.2);
      u += erfNative(0.2);
      u -= erfNative(0.2);
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Erf (native): " + duration * 1e-9);
    return u;
  }

  public static double profileErfc()
  {
    long startTime = System.nanoTime();
    double u = 0;
    for (int i = 0; i < TRIALS; ++i)
    {
      u += erfc(0.2);
      u -= erfc(0.2);
      u += erfc(0.2);
      u -= erfc(0.2);
      u += erfc(0.2);
      u -= erfc(0.2);
      u += erfc(0.2);
      u -= erfc(0.2);
      u += erfc(0.2);
      u -= erfc(0.2);
    }

    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Erfc: " + duration * 1e-9);
    return u;
  }

  public static double erfcNative(double x)
  {
    if (x < 0)
      return 1 + erfNative(-x);
    // constants
    final double a1 = 0.254829592;
    final double a2 = -0.284496736;
    final double a3 = 1.421413741;
    final double a4 = -1.453152027;
    final double a5 = 1.061405429;
    final double p = 0.3275911;

    // Save the sign of x
    double sign = Math.signum(x);
    double t = 1.0 / (1.0 + p * x);
    double y = (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
    return sign * y + (1 - sign);
  }

  public static double profileErfcNative()
  {
    long startTime = System.nanoTime();
    double u = 0;
    for (int i = 0; i < TRIALS; ++i)
    {
      u += erfcNative(1.65612);
      u -= erfcNative(1.65612);
      u += erfcNative(1.65612);
      u -= erfcNative(1.65612);
      u += erfcNative(1.65612);
      u -= erfcNative(1.65612);
      u += erfcNative(1.65612);
      u -= erfcNative(1.65612);
      u += erfcNative(1.65612);
      u -= erfcNative(1.65612);
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Erfc (native): " + duration * 1e-9);
    return u;
  }

  public static double profileErfcx()
  {
    long startTime = System.nanoTime();
    double u = 0;
    for (int i = 0; i < TRIALS; ++i)
    {
      u += erfcx(16.5612);
      u -= erfcx(16.5612);
      u += erfcx(16.5612);
      u -= erfcx(16.5612);
      u += erfcx(16.5612);
      u -= erfcx(16.5612);
      u += erfcx(16.5612);
      u -= erfcx(16.5612);
      u += erfcx(16.5612);
      u -= erfcx(16.5612);
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Erfcx: " + duration * 1e-9);
    return u;
  }

  public static double erfcxNative(double x)
  {
    return Math.exp(x * x + Math.log(erfc(x)));
  }

  public static double profileErfcxNative()
  {
    long startTime = System.nanoTime();
    double u = 0;
    for (int i = 0; i < TRIALS; ++i)
    {
      u += erfcxNative(16.5612);
      u -= erfcxNative(16.5612);
      u += erfcxNative(16.5612);
      u -= erfcxNative(16.5612);
      u += erfcxNative(16.5612);
      u -= erfcxNative(16.5612);
      u += erfcxNative(16.5612);
      u -= erfcxNative(16.5612);
      u += erfcxNative(16.5612);
      u -= erfcxNative(16.5612);
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Erfcx (Native): " + duration * 1e-9);
    return u;
  }

  public static void main(String[] s)
  {
    // Run our functions through their paces to figure out what is not 
    // getting properly optimized by Java.
    profileExp();
    profileExpNative();
    profileExpStrict();

    profileErf();
    profileErfNative();

    profileErfc();
    profileErfcNative();

    profileErfcx();
    profileErfcxNative();
  }

}
