/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xcom;

import gov.llnl.math.Cursor;
import gov.llnl.rtk.physics.PhotonCrossSectionsEvaluator;
import gov.llnl.rtk.physics.PhysicalProperty;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.physics.Units;

/**
 * XCOM specific cross section evaluator implementation.
 *
 * @author nelson85
 */
final class CrossSectionsEvaluatorImpl implements PhotonCrossSectionsEvaluator
{
  private final CrossSectionsTable outer;
  private final Cursor cursor;
  static final Units BASE_INPUT_UNITS = Units.get("MeV");
  static final Units BASE_OUTPUT_UNITS = Units.get("cm2/g");
  private double input_conversion = 1.0;
  private double output_conversion = 1.0;
  private Units inputUnits = BASE_INPUT_UNITS;
  private Units outputUnits = BASE_OUTPUT_UNITS;
  private double elog;
  private final double PAIR = Math.log(1.022);
  
  public CrossSectionsEvaluatorImpl(final CrossSectionsTable outer, Units inputUnits, Units outputUnits)
  {
    this.outer = outer;
    cursor = new Cursor(outer.energies, 0, outer.energies.length);
    this.setInputUnits(inputUnits);
    this.setOutputUnits(outputUnits);
  }
  
  public CrossSectionsTable getTable()
  {
    return this.outer;
  }

  /**
   * @return the inputUnits
   */
  @Override
  public Units getInputUnits()
  {
    return inputUnits;
  }

  @Override
  public void setInputUnits(Units unit)
  {
    unit.require(PhysicalProperty.ENERGY);
    this.inputUnits = unit;
    this.input_conversion = unit.getValue() / BASE_INPUT_UNITS.getValue();
  }

  /**
   * @return the outputUnits
   */
  @Override
  public Units getOutputUnits()
  {
    return outputUnits;
  }

  @Override
  public void setOutputUnits(Units unit)
  {
    unit.require(PhysicalProperty.CROSS_SECTION);
    this.outputUnits = unit;
    this.output_conversion = BASE_OUTPUT_UNITS.getValue() / unit.getValue();
  }

  @Override
  public CrossSectionsEvaluatorImpl seek(double d)
  {
    this.seek_(d * input_conversion);
    return this;
  }

  @Override
  public CrossSectionsEvaluatorImpl seek(Quantity q)
  {
    q.require(PhysicalProperty.ENERGY);
    this.seek_(q.as(BASE_INPUT_UNITS));
    return this;
  }

  /**
   * Switch to a new energy.
   *
   * @param energy in BASE_INPUT_UNITS
   */
  void seek_(double energy)
  {
    this.elog = Math.log(energy);
    if (this.elog < outer.energies[0])
      this.elog = outer.energies[0];
    cursor.seek(this.elog);
  }

  @Override
  public double getPhotoelectric()
  {
    return interp(outer.photoelectric);
  }

  @Override
  public double getIncoherent()
  {
    return interp(outer.incoherent);
  }

  @Override
  public double getPairElectron()
  {
    if (elog<PAIR)
      return 0;
    return interp(outer.pairElectron);
  }

  @Override
  public double getPairNuclear()
  {
    if (elog<PAIR)
      return 0;
    return interp(outer.pairNuclear);
  }

  @Override
  public double getTotal()
  {
    return interp(outer.total);
  }

  double interp(double[] table)
  {
    int i = cursor.getIndex();
    double v0 = table[i];
    if (Double.isInfinite(v0))
      return 0;
    double v1 = table[i + 1];
    double f = cursor.getFraction();
    double out = Math.max(Math.exp((1 - f) * v0 + f * v1) - 9.095e-13, 0)* output_conversion;
    return out;
  }

  @Override
  public PhotonCrossSectionsEvaluator newEvaluator()
  {
    return new CrossSectionsEvaluatorImpl(outer, inputUnits, outputUnits);
  }

}
