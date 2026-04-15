// --- file: gov/llnl/rtk/response/CalculatedResponseEvaluator.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.Spectrum;
import gov.llnl.math.Cursor;
import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Versor;
import gov.llnl.math.interp.SingleInterpolator;
import gov.llnl.rtk.physics.DBKNDistribution;
import gov.llnl.rtk.physics.PhysicalProperty;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.physics.Units;
import gov.llnl.rtk.response.deposition.CuboidChordGeometry;
import gov.llnl.rtk.response.deposition.Deposition;
import gov.llnl.rtk.response.deposition.DepositionCalculator;
import gov.llnl.rtk.view.SensorView;
import gov.llnl.rtk.view.SensorViewFactory;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

class CalculatedResponseEvaluator extends FunctionResponseEvaluator
{
  private static final double HALF_BIN = 0.1; // pointwise window half-width
  private static final double SRC_STEP = 1.0; // 1 keV between centers

  final DepositionCalculator depositionCalculator;
  final SpectralResponseFunctionCalculated outer;
  final Cursor intervalCursor;
  private final CuboidChordGeometry geometry;
  private final DBKNDistribution dbkn;

  // Impulse reponses sorted by energy
  SplineResponseEntry[] cache;
  private final SingleInterpolator.Evaluator resolution;
  private Units distanceUnits = PhysicalProperty.LENGTH;
  private SensorView view;
  private double solidAngle;

  CalculatedResponseEvaluator(final SpectralResponseFunctionCalculated outer)
  {
    super(outer, outer.energyScale, outer.peakShapeParameters, outer.lld);
    this.outer = outer;

    // Set up a cursor to navigate response curves
    intervalCursor = new Cursor(outer.intervals, 0, outer.intervals.length);

    if (outer.material == null)
      throw new NullPointerException("material is null");
    if (outer.photonLibrary == null)
      throw new NullPointerException("photonLibrary is null");

    double[] edges = energyScale.getEdges();
    this.edgesCursor = new Cursor(edges, 0, edges.length);
    this.geometry = new CuboidChordGeometry();
    this.geometry.setDimensions(outer.length, outer.width, outer.height);
    this.dbkn = new DBKNDistribution(outer.shells);
    this.depositionCalculator = new DepositionCalculator(
            outer.material,
            this.geometry,
            this.dbkn,
            200
    );
    this.depositionCalculator.setUnits(PhysicalProperty.LENGTH); // distances will be m
    this.depositionCalculator.setXrayLibrary(outer.xrayLibrary);
    this.depositionCalculator.setPhotonCrossSectionLibrary(outer.photonLibrary);
    this.resolution = outer.resolutionModel.get();

    // We need to tie the source position to the evaluator
    this.depositionCalculator.setPosition(1, 0, 0);

    this.view = SensorViewFactory.createCuboid(outer.width.get(), outer.height.get(), outer.length.get(),
            Vector3.ZERO, Versor.ZERO);
  }

  public void setUnits(Units units)
  {
    if (units.getType() == PhysicalProperty.LENGTH)
      this.distanceUnits = units;
    else
      throw new IllegalArgumentException("unexpected unit type");
  }

  public Units getDistanceUnits()
  {
    return distanceUnits;
  }

  public void setPosition(double x, double y, double z)
  {
    double cf = this.distanceUnits.getValue();
    this.depositionCalculator.setPosition(x * cf, y * cf, z * cf);
    this.cache = null;
    this.solidAngle = this.view.computeSolidAngle(Vector3.of(x * cf, y * cf, z * cf));
    System.out.println(this.solidAngle);
  }

  public void setPosition(Quantity x, Quantity y, Quantity z)
  {
    this.depositionCalculator.setPosition(x, y, z);
    this.cache = null;
    this.solidAngle = this.view.computeSolidAngle(Vector3.of(x.get(), y.get(), z.get()));
  }

  @Override
  public DoubleUnaryOperator getResolutionFunction()
  {
    return this.resolution;
  }

  @Override
  public DoubleUnaryOperator getEfficiencyFunction()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public double getLower()
  {
    return 10.0;
  }

  @Override
  public Spectrum getInternal()
  {
    // Not supported
    return null;
  }

  @Override
  public SpectralBufferDeferred deferred()
  {
    return new SpectralResponseDeferred();
  }

  @Override
  Cursor seek(double energy)
  {
    this.intervalCursor.seek(energy);
    return this.intervalCursor;
  }

  static int[] mesh1 = new int[]
  {
    20, 25, 25, 5
  };
  static int[] mesh2 = new int[]
  {
    30, 30, 30, 5
  }; //> 250
  static int[] mesh3 = new int[]
  {
    25, 50, 30, 5
  }; // >520

  @Override
  public SplineResponseEntry getEntry(int i)
  {
    if (i < 0)
      i = 0;
    if (i >= outer.intervals.length)
      i = outer.intervals.length - 1;

    geometryFactor = this.solidAngle / 4 / Math.PI;

    if (cache == null)
      cache = new SplineResponseEntry[outer.intervals.length];

    SplineResponseEntry entry = cache[i];
    if (entry != null)
      return entry;

    // 0) Incident energy for this entry (confirm intervals semantics)
    final double E0 = outer.intervals[i]; // keV
    Quantity Ein = Quantity.of(E0, "keV");
    Deposition dep = depositionCalculator.compute(Ein, 1.0);

    if (Double.isNaN(dep.totalPhoto))
      throw new ArithmeticException("NaN in deposition");

    // Decide the density of the mesh based on energy
    int[] meshPoints = mesh1;
    if (E0 >= 250)
      meshPoints = mesh2;
    if (E0 >= 520)
      meshPoints = mesh3;

    // We build all the values on the target mesh directly
    int kc = 1 + meshPoints[0] + meshPoints[1] + meshPoints[2] + meshPoints[3];
    double[] meshE = new double[kc];
    double[] meshV = new double[kc];
    SplineResponseContinuum revised = new SplineResponseContinuum();
    revised.energy = E0;
    revised.meshPoints = meshPoints;
    revised.createMesh(meshE, E0);

    smearContinuumToTarget(meshV, meshE,
            this.resolution,
            dep.scattered
    );

    // Convert to 4th power representation for Splines
    revised.values = DoubleStream.of(meshV).map(x -> Math.pow(geometryFactor * x, 0.25)).toArray();

    // ENHANCMENT this should use the tail value to find the correct width factor
    revised.widthFactor = 1.5 * this.resolution.applyAsDouble(E0);

    // 6) Construct entry
    entry = new SplineResponseEntry();
    entry.continuum = revised;

    // Photopeak at incident energy
    if (dep.totalPhoto > 0.0)
      entry.photoelectric = new SplineResponseLine(RenderItem.PHOTOELECTRIC,
              geometryFactor * dep.totalPhoto, E0, this.resolution.applyAsDouble(E0));

    // Pair escape peaks (energies already computed in DepositionCalculator)
    if (dep.singleEscape > 0.0)
      entry.singleEscape = new SplineResponseLine(RenderItem.ESCAPE_SINGLE,
              geometryFactor * dep.singleEscape, dep.singleEnergy, this.resolution.applyAsDouble(dep.singleEnergy));

    if (dep.doubleEscape > 0.0)
      entry.doubleEscape = new SplineResponseLine(RenderItem.ESCAPE_DOUBLE,
              geometryFactor * dep.doubleEscape, dep.doubleEnergy, this.resolution.applyAsDouble(dep.doubleEnergy));

    // Optional: incorporate additional line list from outer (fluorescence, etc.)
    // lines.addAll(outer.buildLinesForEnergy(E0, ...));
    cache[i] = entry;
    return entry;
  }

//<editor-fold desc="continuum smearing">
  void smearContinuumToTarget(
          double[] meshValues, double[] meshEnergies,
          DoubleUnaryOperator widthOfEnergy,
          double[] uniformValues
  )
  {
    // First compute the width for all points
    double[] widths = new double[uniformValues.length];
    for (int i = 0; i < uniformValues.length; ++i)
    {
      widths[i] = widthOfEnergy.applyAsDouble(i + 0.5);
    }
    int first = 0;
    while (first < uniformValues.length && uniformValues[first] == 0.0)
      first++;
    int last = uniformValues.length - 1;
    while (last >= 0 && uniformValues[last] == 0.0)
      last--;

    for (int j = 0; j < meshEnergies.length; ++j)
    {
      meshValues[j] = smearToMeshPoint(meshEnergies[j], uniformValues, widths, first, last);
    }
  }

  private double intervalMassStable(double e0, double e1, double eMid, double center, double width)
  {
    if (width <= 0.0)
      return (center >= e0 && center < e1) ? 1.0 : 0.0;

    final double mass;
    if (eMid < center)
      mass = cdf.apply(e1, center, width) - cdf.apply(e0, center, width);
    else
      mass = ccdf.apply(e0, center, width) - ccdf.apply(e1, center, width);

    // Clamp for floating error safety
    if (mass <= 0.0)
      return 0.0;
    if (mass >= 1.0)
      return 1.0;
    return mass;
  }

  private double smearToMeshPoint(double meshEnergy, double[] uniformValues, double[] widths, int first, int last)
  {
    final double e0 = meshEnergy - HALF_BIN;
    final double e1 = meshEnergy + HALF_BIN;
    final double outWidth = e1 - e0;

    if (first >= last)
      return 0.0;

    double total = 0.0;

    for (int i = first; i < last; ++i)
    {
      double v0 = uniformValues[i];
      double v1 = uniformValues[i + 1];
      if (v0 == 0.0 && v1 == 0.0)
        continue;

      double c0 = i + 0.5;
      double cm = i + 1.0;
      double c1 = i + 1.5;

      double w0 = Math.max(0.0, widths[i]);
      double w1 = Math.max(0.0, widths[i + 1]);
      double wm = 0.5 * (w0 + w1);
      double vm = 0.5 * (v0 + v1);

      double p0 = intervalMassStable(e0, e1, meshEnergy, c0, w0);
      double pm = intervalMassStable(e0, e1, meshEnergy, cm, wm);
      double p1 = intervalMassStable(e0, e1, meshEnergy, c1, w1);

      total += (SRC_STEP / 6.0) * (v0 * p0 + 4.0 * (vm * pm) + v1 * p1);
    }

    return total / outWidth;
  }
//</editor-fold> 
}
