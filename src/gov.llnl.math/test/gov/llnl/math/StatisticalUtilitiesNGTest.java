/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import java.util.OptionalInt;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for StatisticalUtilities.
 */
public class StatisticalUtilitiesNGTest
{

  /**
   * Test of poissonAnscombeTransform method, of class StatisticalUtilities.
   */
  @Test
  public void testPoissonAnscombeTransform()
  {
    assertEquals(StatisticalUtilities.poissonAnscombeTransform(0), 1.224744871391589);
    assertEquals(StatisticalUtilities.poissonAnscombeTransform(1), 2.345207879911715);
    assertEquals(StatisticalUtilities.poissonAnscombeTransform(2), 3.082207001484488);
    assertEquals(StatisticalUtilities.poissonAnscombeTransform(3), 3.6742346141747673);
    assertEquals(StatisticalUtilities.poissonAnscombeTransform(4), 4.183300132670378);
  }

  /**
   * Test of binomialAnscombeTransform method, of class StatisticalUtilities.
   */
  @Test
  public void testBinomialAnscombeTransform()
  {
    assertEquals(StatisticalUtilities.binomialAnscombeTransform(0), 0.4812753739423435, 0.0);
    assertEquals(StatisticalUtilities.binomialAnscombeTransform(.1), 0.5480081266146937, 0.0);
    assertEquals(StatisticalUtilities.binomialAnscombeTransform(.2), 0.6104198077759172, 0.0);
    assertEquals(StatisticalUtilities.binomialAnscombeTransform(.3), 0.6700931577543916, 0.0);
    assertEquals(StatisticalUtilities.binomialAnscombeTransform(.4), 0.7281301767812984, 0.0);
  }

  /**
   * Test of poissonFreemanTukeyTransform method, of class StatisticalUtilities.
   */
  @Test
  public void testPoissonFreemanTukeyTransform()
  {
    assertEquals(StatisticalUtilities.poissonFreemanTukeyTransform(0), 1.0);
    assertEquals(StatisticalUtilities.poissonFreemanTukeyTransform(1), 2.414213562373095);
    assertEquals(StatisticalUtilities.poissonFreemanTukeyTransform(2), 3.1462643699419726);
    assertEquals(StatisticalUtilities.poissonFreemanTukeyTransform(3), 3.732050807568877);
    assertEquals(StatisticalUtilities.poissonFreemanTukeyTransform(4), 4.23606797749979);
  }

  /**
   * Test of boxCoxTransform1 method, of class StatisticalUtilities.
   */
  @Test
  public void testBoxCoxTransform1()
  {
    assertEquals(StatisticalUtilities.boxCoxTransform1(1, 0), 0.0);
    assertEquals(StatisticalUtilities.boxCoxTransform1(2, 0), 0.6931471805599453);
    assertEquals(StatisticalUtilities.boxCoxTransform1(2, 1), 1.0);
    assertEquals(StatisticalUtilities.boxCoxTransform1(2, 2), 1.5);
    assertEquals(StatisticalUtilities.boxCoxTransform1(2, 3), 2.3333333333333335);
  }

  /**
   * Test of chiSquaredTransform method, of class StatisticalUtilities.
   */
  @Test
  public void testChiSquaredTransform()
  {
    assertEquals(StatisticalUtilities.chiSquaredTransform(3, 3), 0.27370588642799965);
    assertEquals(StatisticalUtilities.chiSquaredTransform(4, 3), 0.6382281376221891);
    assertEquals(StatisticalUtilities.chiSquaredTransform(5, 3), 0.9472193156792008);
    assertEquals(StatisticalUtilities.chiSquaredTransform(3, 5), -0.5251237687742896);
    assertEquals(StatisticalUtilities.chiSquaredTransform(4, 5), -0.12488352502730142);
    assertEquals(StatisticalUtilities.chiSquaredTransform(5, 5), 0.2119476867014256);
  }

  /**
   * Test of gammaTransform method, of class StatisticalUtilities.
   */
  @Test
  public void testGammaTransform()
  {
    assertEquals(StatisticalUtilities.gammaTransform(5, 1, 3), 0.8812550318574018);
    assertEquals(StatisticalUtilities.gammaTransform(5, 5, 3), -1.9174967095400386);
    assertEquals(StatisticalUtilities.gammaTransform(5, 1, 5), 0.3341366670833411);
    assertEquals(StatisticalUtilities.gammaTransform(5, 1, 7), 0.021902365050830805);
  }

  /**
   * Test of mode method, of class StatisticalUtilities.
   */
  @Test
  public void testMode()
  {
    assertEquals(StatisticalUtilities.mode(10, 11, 10, 9), OptionalInt.of(10));
    assertEquals(StatisticalUtilities.mode(1, 2, 3, 4, 5), OptionalInt.of(3));
    assertEquals(StatisticalUtilities.mode(1, 1, 2, 3, 4, 4, 4, 5), OptionalInt.of(4));
    assertEquals(StatisticalUtilities.mode(1, 1, 2, 3, 4, 4, 5), OptionalInt.of(1));
    assertEquals(StatisticalUtilities.mode(null), OptionalInt.empty());
    assertEquals(StatisticalUtilities.mode(), OptionalInt.empty());
  }

}
