// --- file: gov/llnl/rtk/response/sim/CuboidIsotropicChordCDF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

/**
 * Analytic cumulative distribution function (CDF) for chord lengths through a
 * cuboid.
 *
 * <p>
 * This class implements exact, closed-form expressions for the cumulative
 * distribution of chord lengths (the lengths of random straight-line segments)
 * through a rectangular box (cuboid). It is based on results from geometric
 * probability theory and is useful for Monte Carlo validation, analytic
 * modeling, and detector or shielding simulations involving random penetrations
 * of cuboidal volumes.
 * </p>
 *
 * <h2>Usage</h2>
 * <pre>
 * CuboidIsotropicChordCDF cdf = new CuboidIsotropicChordCDF();
 * cdf.setSize(1.0, 2.0, 3.0);
 * double probability = cdf.eval(1.5);
 * </pre>
 *
 * <h2>References</h2>
 * <ul>
 * <li>Rodney Coleman, "Intercept Lengths of Random Probes through Boxes,"
 * <i>Journal of Applied Probability</i>, Vol. 18, No. 1 (Mar., 1981), pp.
 * 276-282.</li>
 * <li>Keung L. Luke, Martin G. Buehler, "An exact, closed-form expression of
 * the integral chord-length distribution for the calculation of single-event
 * upsets induced by cosmic rays," <i>Journal of Applied Physics</i>, 64,
 * 5132–5137 (1988).</li>
 * </ul>
 *
 * <p>
 * <b>Note:</b> The reference papers contain several typographical errors. The
 * equations implemented in this class have been corrected and supersede those
 * in the original publications.
 * </p>
 *
 * <h2>Limitations</h2>
 * <ul>
 * <li>Assumes axis-aligned, right rectangular cuboids with positive edge
 * lengths.</li>
 * <li>Not intended for degenerate or near-zero dimensions.</li>
 * </ul>
 *
 * @author nelson85
 */
public class CuboidIsotropicChordCDF implements IsotropicChordCDF
{

  final double a, b, c, V, w;
  final double va,
    vb, vc, S, k;
  final double q1, q2, q3, q4, q5, q6;
  final double z;

  /**
   * Sets the dimensions of the cuboid and precomputes all derived geometric
   * parameters.
   * <p>
   * This method accepts three edge lengths, sorts them in ascending order (so
   * that {@code a} is the shortest, {@code b} is the middle, and {@code c} is
   * the longest), and then computes all internal parameters needed for
   * evaluating the chord-length IsotropicChordCDF. These include the cuboid's
   * volume, surface area, maximum chord length, and several auxiliary values
   * used in the analytic formulas.
   * <br>
   * The method also precomputes cumulative terms ({@code q1} through
   * {@code q6}) for use in the piecewise definition of the IsotropicChordCDF.
   * </p>
   *
   * @param l the first edge length of the cuboid
   * @param w the second edge length of the cuboid
   * @param h the third edge length of the cuboid
   */
  CuboidIsotropicChordCDF(double l, double w, double h)
  {
    // Sort shortest to longest
    if (l > w)
    {
      double d = l;
      l = w;
      w = d;
    }
    if (l > h)
    {
      double d = l;
      l = h;
      h = d;
    }
    if (w > h)
    {
      double d = w;
      w = h;
      h = d;
    }

    this.a = l;
    this.b = w;
    this.c = h;

    this.V = a * b * c;
    this.w = sqrt(a * a + b * b + c * c); // max chord

    // Definitions for functions
    this.va  = sqrt(b * b + c * c); // max of each side
    this.vb = sqrt(a * a + c * c);
    this.vc = sqrt(a * a + b * b);
    this.S = 2.0 * (b * c + c * a + a * b); // surface area
    this.k = 2.0 / 3.0 / Math.PI / S; // normalization factor

    this.z = a + b + c;
    this.q1 = F0(a);
    this.q2 = q1 + Fa(b);

    if (vc < c)
    {
      this.q3 = q2 + Fb(vc);
      this.q4 = q3 + Fvc(c);
      this.q5 = q4 + Ft(vb);
    }
    else
    {
      this.q3 = q2 + Fb(c);
      this.q4 = q3 + Fc(vc);
      this.q5 = q4 + Ft(vb);
    }
    this.q6 = q5 + Fvb(va);
  }

  /**
   * Evaluates the cumulative distribution function (IsotropicChordCDF) for the
   * chord length distribution of a cuboid at the specified chord length
   * {@code x}.
   * <p>
   * This method returns the probability that a randomly chosen chord through
   * the cuboid has length less than or equal to {@code x}, using exact analytic
   * expressions. The calculation is performed using a piecewise formula, with
   * intervals and cumulative terms determined by the cuboid's sorted dimensions
   * and derived geometric properties.
   * <br>
   * The method assumes that {@link #setSize(double, double, double)} has been
   * called to initialize the cuboid's geometry and precompute all necessary
   * constants.
   * </p>
   *
   * @param x the chord length at which to evaluate the cumulative probability
   * @return the value of the IsotropicChordCDF at chord length {@code x}, in
   * the range [0, 1]
   */
  @Override
  public double eval(double x)
  {
    if (x <= a)
      return F0(x);

    if (x <= b && x > a)
      return Fa(x) + q1;

    // There are two possible orderings depending on vc<>c
    if (vc < c)
    {
      if (x <= vc && x > b)
        return Fb(x) + q2;

      if ((x <= c) && (x > vc))
        return Fvc(x) + q3;

      if ((x <= vb) && (x > c))
        return Ft(x) + q4;
    }
    else
    {
      if ((x <= c) && (x > b))
        return Fb(x) + q2;

      if ((x <= vc) && (x > c))
        return Fc(x) + q3;

      if ((x <= vb) && (x > vc))
        return Ft(x) + q4;
    }

    // Finish the long sides
    if ((x <= va) && (x > vb))
      return Fvb(x) + q5;

    if ((x <= w) && (x > va))
      return Fva(x) + q6;
    return 1.0;
  }

  @Override
  public double getMaxChord()
  {
    return this.w;
  }

  /**
   * Computes the integral G(x1, g, x0) as defined in Coleman (1981).
   * <p>
   * Represents the cumulative contribution of chords longer than {@code g},
   * integrated between {@code x0} and {@code x1}. This function is used as part
   * of the analytic chord-length IsotropicChordCDF for a cuboid.
   * </p>
   *
   * @param x1 the upper integration bound for the chord length
   * @param g the minimum chord length for the integration
   * @param x0 the lower integration bound for the chord length
   * @return the value of the G integral over the specified interval
   */
  final static double G(double x1, double g, double x0)
  {
    //   Integration between x and x0
    double s1 = sqrt(x1 * x1 - g * g);
    double s2 = sqrt(x0 * x0 - g * g);
    double v1 = 8 * s1 - 2 * g * g * s1 / x1 / x1 - 6 * g * Math.atan2(s1, g);
    double v2 = 8 * s2 - 2 * g * g * s2 / x0 / x0 - 6 * g * Math.atan2(s2, g);
    return (v1 - v2);
  }

  /**
   * Computes the function T(x1, g, h, V, x0) used in the analytic CDF for
   * cuboid chord lengths.
   * <p>
   * This function integrates the angular contribution of chords that span two
   * faces of the cuboid. It is based on the geometric parameters of the cuboid
   * and is used in the piecewise IsotropicChordCDF definition.
   * </p>
   *
   * @param x1 the upper integration bound for the chord length
   * @param g the length of one cuboid edge
   * @param h the length of another cuboid edge
   * @param V the volume of the cuboid
   * @param x0 the lower integration bound for the chord length
   * @return the value of the T function over the specified interval
   */
  final static double T(double x1, double g, double h, double V, double x0)
  {
    //   Integration between x and x0
    double vr = sqrt(g * g + h * h);
    double y1 = sqrt(x1 * x1 - vr * vr);
    double y2 = sqrt(x0 * x0 - vr * vr);
    double v1 = ((1 - g * g / x1 / x1) * h * atan2(y1, h) + (1 - h * h / x1 / x1) * g * atan2(y1, g) - vr * atan2(y1, vr));
    double v2 = ((1 - g * g / x0 / x0) * h * atan2(y2, h) + (1 - h * h / x0 / x0) * g * atan2(y2, g) - vr * atan2(y2, vr));
    return 6 * V * (v1 - v2) / g / h;
  }

  /**
   * Computes the function U0(x1, g, h, x0) as part of the analytic CDF for
   * cuboid chord lengths.
   * <p>
   * This function contributes to the normalization and scaling of the
   * IsotropicChordCDF in certain intervals.
   * </p>
   *
   * @param x1 the upper integration bound for the chord length
   * @param g the length of a cuboid edge
   * @param h a derived geometric parameter (typically a sum of squared edge
   * lengths)
   * @param x0 the lower integration bound for the chord length
   * @return the value of the U0 function over the specified interval
   */
  final double U0(double x1, double g, double h, double x0)
  {
    double y = 6.0 * V * Math.PI;
    return (h - g * y) / 2 * (1 / x1 / x1 - 1 / x0 / x0);
  }

  /**
   * Computes the function U1(x1, x0) as part of the analytic CDF for cuboid
   * chord lengths.
   * <p>
   * This function is a simple polynomial term used in the normalization of the
   * IsotropicChordCDF.
   * </p>
   *
   * @param x1 the upper integration bound for the chord length
   * @param x0 the lower integration bound for the chord length
   * @return the value of the U1 function over the specified interval
   */
  final static double U1(double x1, double x0)
  {
    return 9.0 / 2.0 * (x1 * x1 - x0 * x0);
  }

  /**
   * Computes the function U2(x1, x0, b) as part of the analytic CDF for cuboid
   * chord lengths.
   * <p>
   * This function contributes a linear scaling term to the normalization of the
   * IsotropicChordCDF.
   * </p>
   *
   * @param x1 the upper integration bound for the chord length
   * @param x0 the lower integration bound for the chord length
   * @param b a geometric parameter (typically a sum of cuboid edge lengths)
   * @return the value of the U2 function over the specified interval
   */
  final static double U2(double x1, double x0, double b)
  {
    return 8.0 * b * (x1 - x0);
  }

  // Subfunctions for each domain
  //  There were many removed terms in the paper where the term was equal to zero, but
  // adding it back made the equations more regular and readable
  final double F0(double x)  // 0-a
  {
    return k * (-U1(x, 0) + U2(x, 0, z));
  }

  final double Fa(double x)
  {// a-b
    double p = a * a * a * a;
    return k * (U0(x, a, p, a) + U2(x, a, b + c)
            - (b + c) * G(x, a, a));
  }

  final double Fb(double x)  // b-min(vc,c)
  {
    double p = a * a * a * a + b * b * b * b;
    return k * (U0(x, a + b, p, b) + U1(x, b) + U2(x, b, c)
            - (c + b) * G(x, a, b)
            - (c + a) * G(x, b, b));
  }

  final double Fvc(double x)  // vc-c
  {
    double p = 6.0 * a * a * b * b;
    return k * (U0(x, a + b, p, vc) + U2(x, vc, c)
            + c * (G(x, vc, vc) - G(x, a, vc) - G(x, b, vc))
            - T(x, a, b, V, vc));
  }

  final double Fc(double x)  // c-vc
  {
    double p = a * a * a * a + b * b * b * b + c * c * c * c;
    return k * (U0(x, z, p, c) + 2 * U1(x, c)
            - (b + c) * G(x, a, c)
            - (c + a) * G(x, b, c)
            - (a + b) * G(x, c, c));
  }

  final double Ft(double x) //  max(c,vc) - vb
  {
    double x0 = Math.max(c, vc);
    double p = c * c * c * c + 6.0 * a * a * b * b;
    return k * (U0(x, z, p, x0) + U1(x, x0)
            + c * (G(x, vc, x0) - G(x, a, x0) - G(x, b, x0))
            - (a + b) * G(x, c, x0)
            - T(x, a, b, V, x0));
  }

  final double Fvb(double x) //  vb - va
  {
    double p = a * a * (6.0 * va  * va  - a * a);
    return k * (U0(x, z, p, vb)
            + b * (G(x, vb, vb) - G(x, c, vb))
            + c * (G(x, vc, vb) - G(x, b, vb))
            - T(x, a, b, V, vb) - T(x, a, c, V, vb));
  }

  final double Fva(double x) // va - w
  {
    double r = b * b * c * c + c * c * a * a + a * a * b * b;
    double p = 8.0 * r - w * w * w * w;
    return k * (U0(x, z, p, va) - U1(x, va)
            + (a * G(x, va, va) + b * G(x, vb, va) + c * G(x, vc, va))
            - (T(x, b, c, V, va) + T(x, c, a, V, va) + T(x, a, b, V, va)));
  }

}
