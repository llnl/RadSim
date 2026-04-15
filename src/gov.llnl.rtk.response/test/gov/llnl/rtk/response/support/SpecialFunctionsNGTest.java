/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;


import static gov.llnl.math.SpecialFunctions.erf;
import static gov.llnl.math.SpecialFunctions.erfc;
import static gov.llnl.math.SpecialFunctions.erfcx;
import static java.lang.Math.exp;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class SpecialFunctionsNGTest
{

  public SpecialFunctionsNGTest()
  {
  }

  @Test
  public void testErfinv()
  {
//    System.out.println("erfinv");
//    double x = 0.0;
//    double expResult = 0.0;
//    double result = SpecialFunctions.erfinv(x);
//    assertEquals(result, expResult, 0.0);
//    fail("The test case is a prototype.");
  }

  @Test
  public void testErf()
  {
    assertEquals(erf(-0.001), -0.0011283787909692363, 1e-15);
    assertEquals(erf(-0.01), -0.011283415555849616, 1e-15);
    assertEquals(erf(-0.1), -0.1124629160182849, 1e-15);
    assertEquals(erf(-1), -0.8427007929497148, 1e-15);
    assertEquals(erf(-2), -0.9953222650189527, 1e-15);
    assertEquals(erf(-5), -0.9999999999984626, 1e-15);
    assertEquals(erf(0), 0.0, 0.0);
    assertEquals(erf(0.001), 0.0011283787909692363, 1e-15);
    assertEquals(erf(0.01), 0.011283415555849616, 1e-15);
    assertEquals(erf(0.1), 0.1124629160182849, 1e-15);
    assertEquals(erf(1), 0.8427007929497148, 1e-15);
    assertEquals(erf(2), 0.9953222650189527, 1e-15);
    assertEquals(erf(5), 0.9999999999984626, 1e-15);
    assertEquals(erf(Double.NaN), Double.NaN, 0.0);
    assertEquals(erf(Double.NEGATIVE_INFINITY), -1.0, 0.0);
    assertEquals(erf(Double.POSITIVE_INFINITY), 1.0, 0.0);
    assertEquals(erf(1e-10), 1.1283791670955126e-10, 1e-24);
    assertEquals(erf(1e-20), 1.1283791670955125e-20, 1e-33);

  }

  @Test
  public void testErfc()
  {
    assertEquals(erfc(0), 1.0, 0.0);
    assertEquals(erfc(0.001), 0.9988716212090307, 1e-15);
    assertEquals(erfc(0.01), 0.98871658444415036, 1e-15);
    assertEquals(erfc(0.1), 0.8875370839817152, 1e-15);
    assertEquals(erfc(1), 0.15729920705028516, 1e-16);
    assertEquals(erfc(2), 0.004677734981047266, 1e-16);
    assertEquals(erfc(5), 1.5374597944280307e-12, 1e-27);
    assertEquals(erfc(10), 2.0884875837625256e-45, 1e-60);
    assertEquals(erfc(Double.NaN), Double.NaN, 0.0);
    assertEquals(erfc(Double.NEGATIVE_INFINITY), 2.0, 0.0);
    assertEquals(erfc(Double.POSITIVE_INFINITY), 0.0, 0.0);
  }

  @Test
  public void testErfcx()
  {
    assertEquals(erfcx(0), 1.0, 0.0);
    assertEquals(erfcx(Double.NaN), Double.NaN, 0.0);
    assertEquals(erfcx(Double.NEGATIVE_INFINITY), Double.POSITIVE_INFINITY, 0.0);
    assertEquals(erfcx(Double.POSITIVE_INFINITY), 0.0, 0.0);
    assertEquals(erfcx(0.001), 0.9988726200811509, 1e-15);
    assertEquals(erfcx(0.01), 0.9888154610463427, 1e-15);
    assertEquals(erfcx(0.1), 0.8964569799691268, 1e-15);
    assertEquals(erfcx(1), 0.4275835761558076, 1e-15);
    assertEquals(erfcx(10), 0.05614099274382259, 1e-15);
    assertEquals(erfcx(100), 0.005641613782989433, 1e-15);
  }

  @Test
  public void testExp()
  {
    assertEquals(exp(0), Math.exp(0), 0.0);
    assertEquals(exp(-0.002), Math.exp(-0.002), 1e-15);
    assertEquals(exp(-0.2), Math.exp(-0.2), 0.0);
    assertEquals(exp(-1), Math.exp(-1), 0.0);
    assertEquals(exp(-1.3), Math.exp(-1.3), 0.0);
    assertEquals(exp(-2), Math.exp(-2), 0.0);
    assertEquals(exp(-200), Math.exp(-200), 1e-95);
    assertEquals(exp(-745), Math.exp(-745), 0.0);
    assertEquals(exp(-Double.MIN_VALUE), Math.exp(-Double.MIN_VALUE), 0.0);

    assertEquals(exp(Double.NaN), Math.exp(Double.NaN), 0.0);
    assertEquals(exp(0.002), Math.exp(0.002), 1e-15);
    assertEquals(exp(0.2), Math.exp(0.2), 0.0);
    assertEquals(exp(1), Math.exp(1), 0.0);
    assertEquals(exp(1.3), Math.exp(1.3), 0.0);
    assertEquals(exp(2), Math.exp(2), 1e-7);
    assertEquals(exp(200), Math.exp(200), 1e+74);
    assertEquals(exp(745), Math.exp(745), 0.0);
  }


}
