// --- file: gov/llnl/rtk/physics/TransformedQuantity.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

class TransformedQuantity implements Quantity
{
  private final Quantity original;
  private final Units units; // Units for the transformed value
  private final DoubleTransformer transformer;
  private final DoubleTransformer derivative;

  public TransformedQuantity(Quantity original, Units units, DoubleTransformer transformer, DoubleTransformer derivative)
  {
    this.original = original;
    this.units = units;
    this.transformer = transformer;
    this.derivative = (derivative == null) ? this::numericalDerivative : derivative;
  }

  @Override
  public double getValue()
  {
    double transformedSIValue = transformer.apply(original.get());
    return units.fromSI(transformedSIValue); 
  }

  @Override
  public double getUncertainty()
  {
    double value = original.getValue();
    double uncertainty = original.getUncertainty();
    double localDerivative = derivative.apply(value); // Use explicit or numerical derivative
    return Math.abs(localDerivative) * uncertainty; // Propagate uncertainty
  }

  @Override
  public boolean isSpecified()
  {
    return this.original.isSpecified();
  }

  @Override
  public boolean hasUncertainty()
  {
    return this.original.hasUncertainty();
  }

  @Override
  public Units getUnits()
  {
    return units;
  }

  @Override
  public Quantity to(Units desiredUnits)
  {
    if (desiredUnits == null)
    {
      throw new UnitsException("Null units requested");
    }

    // Ensure the desired units are compatible with the current units
    if (!this.units.getType().equals(desiredUnits.getType()))
    {
      throw new UnitsException("Incompatible unit types for transformation");
    }

    // Compute the scale factor for unit conversion
    double scaleFactor = this.units.getValue() / desiredUnits.getValue();

    // Create a scaled quantity linked to the transformed quantity
    return new ScaledQuantity(this, scaleFactor).to(desiredUnits);
  }

  /**
   * Compute the numerical derivative of the transformation function at a given
   * point using central differences.
   *
   * @param value the point at which to compute the derivative
   * @return the numerical derivative
   */
  private double numericalDerivative(double value)
  {
    double h = 1e-6; // Step size for finite differences
    double forward = transformer.apply(value + h);
    double backward = transformer.apply(value - h);
    return (forward - backward) / (2 * h);
  }

  @Override
  public double get()
  {
    return transformer.apply(original.get());
  }
}
