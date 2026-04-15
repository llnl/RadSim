/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class QuantityNGTest
{

  public QuantityNGTest()
  {
  }

  /**
   * Test of of method, of class Quantity.
   */
  @Test
  public void testOf_double_String()
  {
    System.out.println("of");
    double value = 2.4;
    String units = "km";
    Quantity result = Quantity.of(value, units);
    assertEquals(result.getValue(), 2.4, 0.00001);
    assertEquals(result.as(PhysicalProperty.LENGTH), 2400, 0.0);
    assertEquals(result.getUnits().getSymbol(), "km");
  }

  /**
   * Test of scalar method, of class Quantity.
   */
  @Test
  public void testScalar()
  {
    double value = 5.1;
    Quantity result = Quantity.scalar(value);
    assertEquals(result.getValue(), 5.1, 0.00001);
    assertEquals(result.getUnits(), null);
  }

  @Test(expectedExceptions = UnitsException.class)
  public void testScalar1()
  {
    double value = 5.1;
    Quantity result = Quantity.scalar(value);
    assertEquals(result.as(PhysicalProperty.LENGTH), 5.1, 0.0);
  }

  /**
   * Test of of method, of class Quantity.
   */
  @Test
  public void testOf_double_Units()
  {
    double value = 5.0;
    Units units = PhysicalProperty.LENGTH;
    Quantity result = Quantity.of(value, units);
    assertEquals(result.getValue(), 5.0, 0.0);
    assertEquals(result.as(PhysicalProperty.LENGTH), 5.0, 0.0);
    assertEquals(result.getUnits().getSymbol(), "m");
  }

  /**
   * Test of of method, of class Quantity.
   */
  @Test
  public void testOf_3args()
  {
    double value = 1.0;
    String units = "km";
    double uncertainty = 0.01;
    String expResult = "1.000000 km";
    Quantity result = Quantity.of(value, Units.get(units), uncertainty, true);
    assertEquals(result.toString(), expResult);
  }

  /**
   * Test of scaled method, of class Quantity.
   */
  @Test
  public void testScaled()
  {
    double scalar = 4.0;
    Quantity instance = Quantity.of(4, "cm");
    Quantity expResult = Quantity.of(16, "cm");
    Quantity result = instance.scaled(scalar);
    assertEquals(result.get(), expResult.get(), 0.0);
  }

  /**
   * Test of plus method, of class Quantity.
   */
  @Test
  public void testPlus()
  {
    Quantity value = Quantity.of(4, "cm");
    Quantity instance = Quantity.of(5, "m");
    Quantity expResult = Quantity.of(5.04, "m");
    Quantity result = instance.plus(value);
    assertEquals(result, expResult);
  }

  /**
   * Test of get method, of class Quantity.
   */
  @Test
  public void testGet()
  {
    Quantity instance = Quantity.of(5.1, "m");
    double expResult = 5.1;
    double result = instance.get();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of as method, of class Quantity.
   */
  @Test
  public void testAs_String()
  {
    String units = "km";
    Quantity instance = Quantity.of(1.23, "m");
    double expResult = 0.00123;
    double result = instance.as(units);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of as method, of class Quantity.
   */
  @Test
  public void testAs_Units()
  {
    Units prop = Units.get("km");
    Quantity instance = Quantity.of(4, "cm");
    double result = instance.as(prop);
    assertEquals(result, 0.00004, 0.0);
  }

  /**
   * Test of to method, of class Quantity.
   */
  @Test
  public void testTo_String()
  {
    String units = "cm";
    Quantity instance = Quantity.of(4.5, "km");
    Quantity expResult = Quantity.of(4500_00, "cm");
    Quantity result = instance.to(units);
    assertEquals(result, expResult);
  }

  /**
   * Test of to method, of class Quantity.
   */
  @Test
  public void testTo_Units()
  {
    Units desired = Units.get("cm");
    Quantity instance = Quantity.of(4, "km");
    Quantity expResult = Quantity.of(4000_00, "cm");
    Quantity result = instance.to(desired);
    assertEquals(result, expResult);
  }

  /**
   * Test of getUnits method, of class Quantity.
   */
  @Test
  public void testGetUnits()
  {
    Quantity instance = Quantity.of(4, "cm");
    Units expResult = Units.get("cm");
    Units result = instance.getUnits();
    assertEquals(result, expResult);
  }

  /**
   * Test of getSymbol method, of class Quantity.
   */
  @Test
  public void testGetSymbol()
  {
    Quantity instance = Quantity.of(4, "km");
    String expResult = "km";
    String result = instance.getUnits().getSymbol();
    assertEquals(result, expResult);
  }

  /**
   * Test of getValue method, of class Quantity.
   */
  @Test
  public void testGetValue()
  {
    Quantity instance = Quantity.of(4, "km");
    double expResult = 4.0;
    double result = instance.getValue();
    assertEquals(result, expResult, 0.0);

  }

  /**
   * Test of getUncertainty method, of class Quantity.
   */
  @Test
  public void testGetUncertainty()
  {
    Quantity instance = Quantity.of(1.4, Units.get("cm"), 0.1, true);
    double expResult = 0.1;
    double result = instance.getUncertainty();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of isSpecified method, of class Quantity.
   */
  @Test
  public void testIsSpecified()
  {
    Quantity instance = Quantity.of(1.2, Units.get("m"), 0.0, false);
    boolean expResult = false;
    boolean result = instance.isSpecified();
    assertEquals(result, expResult);
  }

  /**
   * Test of hasUncertainty method, of class Quantity.
   */
  @Test
  public void testHasUncertainty()
  {
    Quantity instance = Quantity.of(5.1, Units.get("km"), 0.1, true);
    boolean expResult = true;
    boolean result = instance.hasUncertainty();
    assertEquals(result, expResult);
  }

  /**
   * Test of toString method, of class Quantity.
   */
  @Test
  public void testToString()
  {
    Quantity instance = Quantity.of(1.23, "km");
    String expResult = "1.230000 km";
    String result = instance.toString();
    assertEquals(result, expResult);
  }

  /**
   * Test of require method, of class Quantity.
   */
  @Test
  public void testRequire()
  {
    PhysicalProperty physicalProperty = PhysicalProperty.LENGTH;
    Quantity instance = Quantity.of(1, "cm");
    instance.require(physicalProperty);
  }

}
