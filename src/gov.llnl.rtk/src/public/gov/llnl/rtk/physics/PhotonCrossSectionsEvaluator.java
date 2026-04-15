// --- file: gov/llnl/rtk/physics/PhotonCrossSectionsEvaluator.java ---
/* 
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author nelson85
 */
public interface PhotonCrossSectionsEvaluator
{

  /**
   * Get the current energy input unit.
   *
   * @return the inputUnits
   */
  Units getInputUnits();

  /**
   * Define the input unit for energy.
   *
   * @param unit
   */
  public void setInputUnits(Units unit);

  /**
   * Get the current output cross section unit.
   *
   * @return the outputUnits
   */
  Units getOutputUnits();

  /**
   * Set the output unit for cross section.
   *
   * @param unit
   */
  public void setOutputUnits(Units unit);

  /**
   * Seek a new energy in the lookup table.
   *
   * @param energy is the value in the desired input unit.
   * @return 
   */
  public PhotonCrossSectionsEvaluator seek(double energy);

  /**
   * Seek a new energy in the lookup table.
   *
   * @param energy is the quantity including the units.
   * @return 
   */
  public PhotonCrossSectionsEvaluator seek(Quantity energy);

  /**
   * Get the incoherent cross section.
   *
   * @return cross section in specified.
   */
  double getIncoherent();

  /**
   * Get the pair production electron cross section.
   *
   * @return cross section in specified.
   */
  double getPairElectron();

  /**
   * Get the pair production nuclear cross section.
   *
   * @return cross section in cm^2/g
   */
  double getPairNuclear();

  /**
   * Get the pair production total cross section.
   *
   * @return cross section in specified.
   */
  default double getPair()
  {
    return getPairElectron() + getPairNuclear();
  }

  /**
   * Get the photo electric cross section.
   *
   * @return cross section in specified.
   */
  double getPhotoelectric();

  /**
   * Get the total cross section excluding coherent.
   *
   * @return cross section in specified.
   */
  double getTotal();
  
  
  PhotonCrossSectionsEvaluator newEvaluator();
  
    /**
   * Get the pair cross section.
   *
   * Use {@link #setInputUnits(Units) setInputUnits} and
   * {@link #setOutputUnits(Units) setOutputUnits} prior to this call to select
   * the units for the function.
   *
   * This includes both electron and atomic.
   *
   * @return function in units specified by user.
   */
  default DoubleUnaryOperator getPairFunction()
  {
    PhotonCrossSectionsEvaluator eval = newEvaluator();
    return (double p) ->
    {
      eval.seek(p);
      return eval.getPair();
    };
  }

  /**
   * Get the photo electric section.
   *
   * Use {@link #setInputUnits(Units) setInputUnits} and
   * {@link #setOutputUnits(Units) setOutputUnits} prior to this call to select
   * the units for the function.
   * 
   * @return function in units specified by user.
   */
  default DoubleUnaryOperator getPhotoelectricFunction()
  {
    PhotonCrossSectionsEvaluator eval = newEvaluator();
    return (double p) ->
    {
      eval.seek(p);
      return eval.getPhotoelectric();
    };
  }

  /**
   * Get the total cross section excluding coherent.
   *
   * Use {@link #setInputUnits(Units) setInputUnits} and
   * {@link #setOutputUnits(Units) setOutputUnits} prior to this call to select
   * the units for the function.
   *
   * @return function in units specified by user.
   */
  default DoubleUnaryOperator getTotalFunction()
  {
    PhotonCrossSectionsEvaluator eval = newEvaluator();
    return (double p) ->
    {
      eval.seek(p);
      return eval.getTotal();
    };
  }
  /**
   * Get the incoherent scattering as a function.
   *
   * Use {@link #setInputUnits(Units) setInputUnits} and
   * {@link #setOutputUnits(Units) setOutputUnits} prior to this call to select
   * the units for the function.
   *
   * @return function in units specified by user.
   */
  default DoubleUnaryOperator getIncoherentFunction()
  {
    PhotonCrossSectionsEvaluator eval = newEvaluator();
    return (double p) ->
    {
      eval.seek(p);
      return eval.getIncoherent();
    };
  }


}
