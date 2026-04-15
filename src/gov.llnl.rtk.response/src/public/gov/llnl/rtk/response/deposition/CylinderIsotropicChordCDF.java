// --- file: gov/llnl/rtk/response/deposition/CylinderIsotropicChordCDF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

/**
 * Semi-analytic CDF/PDF for isotropic chord lengths through a right circular cylinder.
 *
 * The analytic expressions implemented here are based on the reduction
 * to Carlson symmetric elliptic integrals RF and RD for the right
 * circular cylinder chord distribution, as presented in:
 *
 *   Langworthy & Owens, *Chord Length Distribution for a Right Circular
 *   Cylinder*, DTIC Technical Report ADA198002, 1990,
 *   https://apps.dtic.mil/sti/tr/pdf/ADA198002.pdf
 *
 * This class follows the same conventions and functional decomposition
 * used in the reference, including the c1(s) and c0,2(s) decomposition,
 * special handling of endpoint behavior in R_x(u,m), and careful
 * numerical integration to produce a normalized CDF.
 */
public class CylinderIsotropicChordCDF implements IsotropicChordCDF
{
  // ------------------------------
  // Geometry parameters
  // ------------------------------

  /** Cylinder radius (half of full diameter). */
  private final double d;

  /** Cylinder half-height (half of full height). */
  private final double h;

  /** Maximum half-chord length: sqrt(d² + h²). */
  private final double maxS;

  /** Maximum full-chord length = 2 * maxS. */
  private final double maxChord;

  // ------------------------------
  // Numerical tolerances
  // ------------------------------

  /**
   * Small positive number used to avoid exact zeros in elliptic integrals.
   * Carlson RF and RD algorithms assume strictly positive arguments;
   * passing EPS in place of zero aligns with the standard implementation.
   */
  private static final double EPS = 1e-14;

  private static final int INTEGRATION_N = 4096;

  private static final double U1_EPS = 1e-12;

  private static final double M_EPS = 1e-14;

  /**
   * Constructor.
   *
   * @param diameter full cylinder diameter
   * @param height   full cylinder height
   */
  public CylinderIsotropicChordCDF(double diameter, double height)
  {
    if (diameter <= 0 || height <= 0)
      throw new IllegalArgumentException("Diameter and height must be positive.");

    this.d = 0.5 * diameter;
    this.h = 0.5 * height;
    this.maxS = Math.sqrt(d * d + h * h);
    this.maxChord = 2.0 * maxS;
  }

  @Override
  public double getMaxChord() { return maxChord; }

  // ---------------------------------------------------------------------
  // PDF / CDF interface
  // ---------------------------------------------------------------------

  /**
   * CDF of full chord length L = 2s.
   * Internally integrates the half-chord density on [0, L/2].
   */
  @Override
  public double eval(double x)
  {
    if (x <= 0.0) return 0.0;
    if (x >= maxChord) return 1.0;

    final double sMax = 0.5 * x;
    final double a = 1e-10 * maxS;
    final double b = Math.max(sMax, a);
    if (b <= a) return 0.0;

    double integral = simpsonHalfChordPdf(a, b);
    if (integral < 0.0) integral = 0.0;
    if (integral > 1.0) integral = 1.0;
    return integral;
  }

  /**
   * Half-chord length PDF p_s(s). Negative values due to
   * cancellation/roundoff are clamped to zero for integration stability.
   */
  public double pdfHalfChord(double s)
  {
    if (s <= 0.0 || s >= maxS) return 0.0;
    double val = c1(s) + c02(s);
    return (val < 0.0) ? 0.0 : val;
  }

  /** Full-chord PDF from half-chord PDF. */
  public double pdfFullChord(double x)
  {
    if (x <= 0.0 || x >= maxChord) return 0.0;
    return 0.5 * pdfHalfChord(0.5 * x);
  }

  // ---------------------------------------------------------------------
  // Analytic decomposition (c1 and c0,2 terms)
  // (Eqns. and definitions from ADA198002)
  // ---------------------------------------------------------------------

  private static double H(double x) { return (x < 0.0) ? 0.0 : 1.0; }
  private static double clamp(double x, double lo, double hi) { return Math.max(lo, Math.min(hi, x)); }

  private double aOf(double s)
  {
    if (s < h) return 0.0;
    double t = 1.0 - (h*h)/(s*s);
    return (t < 0) ? 0.0 : Math.sqrt(t);
  }

  private double mOf(double s)
  {
    return (s < d) ? ((s*s)/(d*d)) : ((d*d)/(s*s));
  }

  private double bOf(double s) { return (s < d) ? (s/d) : 1.0; }

  private static double RA(double f, double x)
  {
    x = clamp(x, -1.0, 1.0);
    double t = 1.0 - x*x;
    if (t < 0.0) t = 0.0;
    return x * Math.sqrt(t) * f - Math.asin(x);
  }

  /**
   * R_x(u,m) auxiliary function expressed via Carlson integrals.
   * Uses the endpoint identity: RD(1,0,1-m) = 3*(K(m)-E(m))/m
   * as given in ADA198002 for improved stability near u=1.
   */
  private static double Rx(double u, double m)
  {
    u = clamp(u, 0.0, 1.0);
    m = Math.max(0.0, m);
    final double u2 = u*u;

    final double rfz;
    final double rdz;

    if (1.0 - u < U1_EPS)
    {
      double K = completeK(m);
      double E = completeE(m);

      rfz = K;
      rdz = (m < M_EPS) ? (3.0 * Math.PI / 4.0) : (3.0 * (K - E) / m);
    }
    else
    {
      double z1 = 1.0;
      double z2 = Math.max(1.0 - u2, EPS);
      double z3 = Math.max(1.0 - m*u2, EPS);

      rfz = carlsonRF(z1, z2, z3);
      rdz = carlsonRD(z1, z2, z3);
    }

    double sqrtTerm = Math.sqrt(Math.max(0.0, (1.0 - u2)*(1.0 - m*u2)));
    return sqrtTerm + (2.0/3.0)*(m+1.0)*u2*rdz - rfz;
  }

  /**
   * c1(s) term.
   * Uses piecewise definition depending on s<d and s>=d for the parameter m(s).
   */
  private double c1(double s)
  {
    double mm = mOf(s);

    if (s < d)
    {
      return ((8.0*h)/(Math.PI*(d+2.0*h)*s)) *
             ((1.0/3.0)*Rx(1.0, mm) - (aOf(s)/3.0)*Rx(aOf(s), mm));
    }
    else
    {
      double u0 = clamp((s*aOf(s)/d), 0.0, 1.0);
      return ((8.0*h*d)/(Math.PI*(d+2.0*h)*s*s)) *
             ((1.0/3.0)*Rx(1.0, mm) - (u0/3.0)*Rx(u0, mm));
    }
  }

  /**
   * c0,2(s) term combining algebraic and elemental contributions.
   */
  private double c02(double s)
  {
    double bb = clamp(bOf(s), -1.0, 1.0);
    double u0 = clamp(s * aOf(s) / d, -1.0, 1.0);

    double term1 = (d*d)*(RA((6.0*bb*bb)+1.0, bb) - RA((6.0*u0*u0)+1.0, u0));
    double term2 = (4.0*h*h)*H(s-h)*(RA(-1.0, 1.0) - RA(-1.0, u0));

    return d/(Math.PI*(d+2.0*h)*(s*s*s))*(term1 - term2);
  }

  // ---------------------------------------------------------------------
  // Elliptic integral helpers
  // ---------------------------------------------------------------------

  private static double completeK(double m)
  {
    m = clamp(m, 0.0, 1.0);
    return carlsonRF(EPS, Math.max(1.0-m,EPS), 1.0);
  }

  private static double completeE(double m)
  {
    m = clamp(m, 0.0, 1.0);
    double y = Math.max(1.0-m, EPS);
    double rf = carlsonRF(EPS, y, 1.0);
    double rd = carlsonRD(EPS, y, 1.0);
    return rf - (m/3.0)*rd;
  }

  // ---------------------------------------------------------------------
  // Numerical integration (Simpson) for the CDF
  // ---------------------------------------------------------------------

  private double simpsonHalfChordPdf(double a, double b)
  {
    int n = INTEGRATION_N;
    if ((n & 1) == 1) n++;

    double hStep = (b-a)/n;
    double sum = pdfHalfChord(a) + pdfHalfChord(b);

    for (int i=1; i<n; i++)
    {
      double x = a + i*hStep;
      double fx = pdfHalfChord(x);
      sum += (i%2 == 0) ? 2.0*fx : 4.0*fx;
    }
    return sum * hStep / 3.0;
  }

  // ---------------------------------------------------------------------
  // Carlson symmetric elliptic integrals RF and RD
  // (Duplication algorithm)
  // ---------------------------------------------------------------------

  private static double carlsonRF(double x, double y, double z)
  {
    final double ERRTOL = 1e-10;

    double xn = x, yn = y, zn = z;
    double An = (xn + yn + zn)/3.0;

    double Q = Math.pow(3.0*ERRTOL, -1.0/6.0) *
               Math.max(Math.max(Math.abs(An-xn),Math.abs(An-yn)),
                        Math.abs(An-zn));

    int it = 0;
    while (Q > An && it++ < 10000)
    {
      double sx = Math.sqrt(xn);
      double sy = Math.sqrt(yn);
      double sz = Math.sqrt(zn);

      double lambda = sx*sy + sx*sz + sy*sz;
      xn = 0.25*(xn + lambda);
      yn = 0.25*(yn + lambda);
      zn = 0.25*(zn + lambda);

      An = (xn + yn + zn)/3.0;
      Q *= 0.25;
    }

    double X = (An-xn)/An;
    double Y = (An-yn)/An;
    double Z = (An-zn)/An;

    double E2 = X*Y - Z*Z;
    double E3 = X*Y*Z;

    return (1.0/Math.sqrt(An)) *
           (1.0 - (1.0/10.0)*E2 + (1.0/14.0)*E3
            + (1.0/24.0)*E2*E2 - (3.0/44.0)*E2*E3);
  }

  private static double carlsonRD(double x, double y, double z)
  {
    final double ERRTOL = 1e-10;

    double xn = x, yn = y, zn = z;
    double sigma = 0.0;
    double power4 = 1.0;

    double An = (xn + yn + 3.0*zn)/5.0;

    double Q = Math.pow(ERRTOL/4.0, -1.0/6.0) *
               Math.max(Math.max(Math.abs(An-xn),Math.abs(An-yn)),
                        Math.abs(An-zn));

    int it = 0;
    while (Q > An && it++ < 10000)
    {
      double sx = Math.sqrt(xn);
      double sy = Math.sqrt(yn);
      double sz = Math.sqrt(zn);

      double lambda = sx*sy + sx*sz + sy*sz;

      double mu = zn + lambda;
      sigma += power4 / (sz*mu);

      power4 *= 0.25;
      xn = 0.25*(xn+lambda);
      yn = 0.25*(yn+lambda);
      zn = 0.25*(zn+lambda);

      An = (xn+yn+3.0*zn)/5.0;
      Q *= 0.25;
    }

    double X = (An-xn)/An;
    double Y = (An-yn)/An;
    double Z = (An-zn)/An;

    double EA = X*Y;
    double EB = Z*Z;
    double EC = EA-EB;
    double ED = EA-6.0*EB;
    double EE = ED + EC + EC;

    double S1 = ED*(-3.0/14.0) + Z*EE*(1.0/6.0);
    double S2 = Z*(ED*(9.0/88.0) - Z*EE*(3.0/22.0));
    double S3 = EC*EC*(9.0/52.0) + Z*Z*ED*(3.0/26.0);

    return 3.0*sigma + power4*(1.0/(An*Math.sqrt(An))) *
           (1.0 + S1 + S2 + S3);
  }
}
