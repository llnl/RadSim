/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.function.Function;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for CharSequenceUtilities.
 */
public class CharSequenceUtilitiesNGTest
{

  public CharSequenceUtilitiesNGTest()
  {
  }

  /**
   * Test of substitute method, of class CharSequenceUtilities.
   */
  @Test
  public void testSubstitute()
  {
    CharSequence str = "I felt happy because I saw the others were happy and because I knew I should feel happy, but I wasn’t really happy. - Roberto Bolano, 2666";
    String from = "happy";
    String to = "joy";
    int occurrenceStart = 0;
    int occurrenceEnd = -1;
    StringBuilder expResult = new StringBuilder("I felt joy because I saw the others were joy and because I knew I should feel joy, but I wasn’t really joy. - Roberto Bolano, 2666");
    StringBuilder result = CharSequenceUtilities.substitute(str, from, to, occurrenceStart, occurrenceEnd);
    assertEquals(result.toString(), expResult.toString());
    assertNotEquals(result, expResult);
  }

  /**
   * Test of substituteFunction method, of class CharSequenceUtilities.
   */
  @Test
  public void testSubstituteFunction()
  {
    Function<String, String> to = s -> "joy";
    CharSequence str = "I felt happy because I saw the others were happy and because I knew I should feel happy, but I wasn’t really happy. - Roberto Bolano, 2666";
    String from = "happy";
    int occurrenceStart = 0;
    int occurrenceEnd = -1;
    StringBuilder expResult = new StringBuilder("I felt joy because I saw the others were joy and because I knew I should feel joy, but I wasn’t really joy. - Roberto Bolano, 2666");
    StringBuilder result = CharSequenceUtilities.substituteFunction(str, from, to, occurrenceStart, occurrenceEnd);
    assertEquals(result.toString(), expResult.toString());
  }

  /**
   * Test of substituteAssign method, of class CharSequenceUtilities.
   */
  @Test
  public void testSubstituteAssign()
  {
    StringBuilder str = new StringBuilder("I felt happy because I saw the others were happy and because I knew I should feel happy, but I wasn’t really happy. - Roberto Bolano, 2666");
    String from = "happy";
    String to = "joy";
    int occurrenceStart = 0;
    int occurrenceEnd = -1;
    StringBuilder expResult = new StringBuilder("I felt joy because I saw the others were joy and because I knew I should feel joy, but I wasn’t really joy. - Roberto Bolano, 2666");
    StringBuilder result = CharSequenceUtilities.substituteAssign(str, from, to, occurrenceStart, occurrenceEnd);
    assertEquals(result.toString(), expResult.toString());
    assertEquals(result, str);
    assertEquals(result.toString(), str.toString());

    result = CharSequenceUtilities.substituteAssign(str, "joy", null, occurrenceStart, occurrenceEnd);
    assertEquals(result, str);
    assertEquals(result.toString(), str.toString());
    assertEquals(result.toString(), "I felt  because I saw the others were  and because I knew I should feel , but I wasn’t really . - Roberto Bolano, 2666");
  }

  /**
   * Test of substituteFunctionAssign method, of class CharSequenceUtilities.
   */
  @Test
  public void testSubstituteFunctionAssign()
  {
    StringBuilder str = new StringBuilder("I felt happy because I saw the others were happy and because I knew I should feel happy, but I wasn’t really happy. - Roberto Bolano, 2666");
    String from = "happy";
    Function<String, String> to = s -> "joy";
    int occurrenceStart = 0;
    int occurrenceEnd = -1;
    StringBuilder expResult = new StringBuilder("I felt joy because I saw the others were joy and because I knew I should feel joy, but I wasn’t really joy. - Roberto Bolano, 2666");
    StringBuilder result = CharSequenceUtilities.substituteFunctionAssign(str, from, to, occurrenceStart, occurrenceEnd);
    assertEquals(result.toString(), expResult.toString());
    assertEquals(result, str);
    assertEquals(result.toString(), str.toString());
  }

  /**
   * Test of translateAll method, of class CharSequenceUtilities.
   */
  @Test(expectedExceptions =
  {
    RuntimeException.class
  })
  public void testTranslateAll()
  {
    CharSequence str = "I felt happy because I saw the others were happy and because I knew I should feel happy, but I wasn’t really happy. - Roberto Bolano, 2666";
    CharSequence from = "happy";
    CharSequence to = "HAPPY";
    StringBuilder expResult = new StringBuilder("I felt HAPPY becAuse I sAw tHe otHers were HAPPY And becAuse I knew I sHould feel HAPPY, but I wAsn’t reAllY HAPPY. - Roberto BolAno, 2666");
    StringBuilder result = CharSequenceUtilities.translateAll(str, from, to);
    assertEquals(result.toString(), expResult.toString());
    assertNotEquals(result, str);

    result = CharSequenceUtilities.translateAll(str, from, null);
    assertEquals(result.toString(), "I felt  becuse I sw te oters were  nd becuse I knew I sould feel , but I wsn’t rell . - Roberto Bolno, 2666");

    // Test RuntimeException
    CharSequenceUtilities.translateAll(str, "happy", "joy");

  }

  /**
   * Test of translateAllAssign method, of class CharSequenceUtilities.
   */
  @Test
  public void testTranslateAllAssign()
  {
    StringBuilder str = new StringBuilder("I felt happy because I saw the others were happy and because I knew I should feel happy, but I wasn’t really happy. - Roberto Bolano, 2666");
    CharSequence from = "happy";
    CharSequence to = "HAPPY";
    StringBuilder expResult = new StringBuilder("I felt HAPPY becAuse I sAw tHe otHers were HAPPY And becAuse I knew I sHould feel HAPPY, but I wAsn’t reAllY HAPPY. - Roberto BolAno, 2666");
    StringBuilder result = CharSequenceUtilities.translateAllAssign(str, from, to);
    assertEquals(result.toString(), expResult.toString());
    assertEquals(result, str);
    assertEquals(result.toString(), str.toString());

    str = new StringBuilder("I felt happy because I saw the others were happy and because I knew I should feel happy, but I wasn’t really happy. - Roberto Bolano, 2666");
    result = CharSequenceUtilities.translateAllAssign(str, from, null);
    assertEquals(result.toString(), "I felt  becuse I sw te oters were  nd becuse I knew I sould feel , but I wsn’t rell . - Roberto Bolno, 2666");
    assertEquals(result, str);
    assertEquals(result.toString(), str.toString());
  }

}
