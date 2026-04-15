// --- file: gov/llnl/rtk/response/SpectralResponseEvaluatorBase.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Versor;
import gov.llnl.rtk.flux.Flux;

/**
 * Base code used for multiple
 * @author nelson85
 */
public abstract class SpectralResponseEvaluatorBase implements SpectralResponseEvaluator
{
  SpectralResponseFunction responseFunction;
  EnergyScale energyScale;
  Vector3 sourceCoordinate = Vector3.ZERO;
  Vector3 globalCoordinate = Vector3.of(0, 0, -1);
  Versor globalOrientation = Versor.ZERO;
  Vector3 sensorCoordinate = Vector3.ZERO;
  Versor sensorOrientation = Versor.ZERO;
  boolean viewEnabled;
  double geometryFactor = 1.0;

  protected SpectralResponseEvaluatorBase(SpectralResponseFunction responseFunction, EnergyScale energyScale)
  {
    this.responseFunction = responseFunction;
    this.energyScale = energyScale;
  }

  @Override
  final public SpectralResponseFunction getResponseFunction()
  {
    return this.responseFunction;
  }

//<editor-fold desc="parameters" defaultstate="collapsed">
  @Override
  final public EnergyScale getEnergyScale()
  {
    return this.energyScale;
  }

  @Override
  public void setEnergyScale(EnergyScale scale)
  {
    this.energyScale = scale;
  }
  @Override
  public void setSourceCoordinate(Vector3 global)
  {
    this.sourceCoordinate = global;
    this.geometryFactor = -1;
  }

  @Override
  public void setInstrumentCoordinate(Vector3 global)
  {
    this.globalCoordinate = global;
    this.geometryFactor = -1;
  }

  @Override
  public void setSensorRelativeCoordinate(Vector3 local)
  {
    this.sensorCoordinate = local;
    this.geometryFactor = -1;
  }

  @Override
  public void setInstrumentOrientation(Versor versor)
  {
    this.globalOrientation = versor;
  }

  @Override
  public void setSensorRelativeOrientation(Versor local)
  {
    this.sensorOrientation = local;
  }

  @Override
  public Vector3 getSourceCoordinate()
  {
    return this.sourceCoordinate;
  }

  @Override
  public Vector3 getInstrumentCoordinate()
  {
    return this.globalCoordinate;
  }

  @Override
  public Versor getInstrumentOrientation()
  {
    return this.globalOrientation;
  }

  /**
   * @return the sensorCoordinate
   */
  @Override
  public Vector3 getSensorRelativeCoordinate()
  {
    return sensorCoordinate;
  }

  /**
   * @return the sensorOrientation
   */
  @Override
  public Versor getSensorRelativeOrientation()
  {
    return sensorOrientation;
  }

  /**
   * Get the current geometry factor.
   *
   * Compute the geometry factor based on the coordinates of the source
   * and detector.
   *
   * @return the geometryFactor
   */
  @Override
  public double getGeometryFactor()
  {
    // If the geometry factor is not set explicitly, it must be computed
    if (geometryFactor <= 0)
    {
      // Call the response function to compute the geometry factor
      geometryFactor = this.getResponseFunction().getGeometryFactor(this.getSourceCoordinate(),
              this.getInstrumentCoordinate(),
              this.getInstrumentOrientation(),
              this.getSensorRelativeCoordinate(),
              this.getSensorRelativeOrientation()
      );
    }
    return geometryFactor;
  }

  /**
   * Set the geometry factor.
   *
   * This manually sets the geometry factor.   If set less then zero the
   * geometry factor will be computed from the coordinates.
   *
   * @param geometryFactor the geometryFactor to set
   */
  @Override
  public void setGeometryFactor(double geometryFactor)
  {
    this.geometryFactor = geometryFactor;
  }

//</editor-fold>
//<editor-fold desc="primitives" defaultstate="collapsed">
  /**
   * Generic rendering of a group using Simpson's rule.
   *
   * Evaluators should override this method an replace it with one that is
   * more efficient.
   *
   * @param buffer is the buffer to be updated.
   * @param energy0 is the lower energy of this region.
   * @param density0 is the density at energy0.
   * @param energy1 is the upper energy of this region.
   * @param density1 is the density at energy1.
   */
  @Override
  public void renderGroup(SpectralBuffer buffer,
          double energy0, double density0,
          double energy1, double density1)
  {
    buffer.set(this);
    double de = energy1 - energy0;
    double width = getResolutionFunction().applyAsDouble(energy0);
    int slices = (int) (de / width);

    // Simple integration if the number of slices are low
    if (slices < 2)
    {
      // Highest energy first so that we minimize the tail computations
      renderLine(buffer, energy1, density1 * de / 6);
      renderLine(buffer, (energy0 + energy1) / 2, 4 * (density0 + density1) * de / 12);
      renderLine(buffer, energy0, density0 * de / 6);
      return;
    }

    // Else Simpson's rule for multiple pieces
    slices++;
    if (slices % 2 == 1)
      slices++;
    double h = de / slices / 3;

    // FIXME This code is the largest speed issue when rendering a new spectrum
    // with a fresh group structure.  We need to represent the line contribution
    // across the group, but for a very high resolution detector that requires
    // many slices so that the flat top does not have ripples.
    //
    // The efficient answer would be to render the left and right tails for the
    // highest and lowest energies and connect the line peices inbetween based
    // on the efficiency, but the structure currently does not allow for that.
    // It gets more complicated once we have escape peaks.
    // Highest energy slice first
    renderLine(buffer, energy1, density1 * h);
    renderLine(buffer, energy0, density0 * h);
    double factor = 4;
    for (int i = 1; i < slices; ++i)
    {
      double f = i / (double) slices;
      double e = (1 - f) * energy0 + f * energy1;
      double u = (1 - f) * density0 + f * density1;
      renderLine(buffer, e, u * factor * h);
      if (factor == 4)
        factor = 2;
      else
        factor = 4;
    }
  }

//</editor-fold>
  /**
   * Default conversion from flux to response.
   *
   * This is slow method. Used primarily in testing.
   *
   * @param flux
   * @return
   */
  @Override
  public Spectrum apply(Flux flux)
  {
    return SpectralResponseRenderer.render(this, flux);
  }


}
