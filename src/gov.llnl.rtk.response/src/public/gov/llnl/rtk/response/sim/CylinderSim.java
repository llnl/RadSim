// --- file: gov/llnl/rtk/response/sim/CylinderSim.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.sim;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.euclidean.MutableVector3;
import gov.llnl.math.euclidean.Vector3Ops;
import gov.llnl.math.random.NormalRandom;
import gov.llnl.math.random.UniformRandom;
import gov.llnl.rtk.response.deposition.AngularUtility;
import gov.llnl.rtk.response.deposition.CylinderUtility;
import java.util.NoSuchElementException;

/**
 * Simulation engine for ray tracing and geometric analysis in cylinder (right
 * circular cylinder) geometries.
 * <p>
 * {@code CylinderSim} generates random cylinder shapes and source positions,
 * then simulates the interaction of rays with the cylinder for various
 * scenarios:
 * <ul>
 * <li><b>Direct:</b> Simulates straight-line (unscattered) ray intersections
 * with the cylinder.</li>
 * <li><b>Scatter:</b> Simulates rays that scatter inside the cylinder, modeling
 * attenuation and angular distribution.</li>
 * <li><b>Chord:</b> Simulates random chords (line segments) through the
 * cylinder for analytic comparison or validation.</li>
 * </ul>
 * </p>
 * <p>
 * This class is intended for use in Monte Carlo simulations and machine
 * learning validation of geometric response functions. It provides methods to
 * generate random configurations, compute geometric features, and sample path
 * lengths according to the specified scenario.
 * </p>
 * <p>
 * <b>Usage:</b> Not thread-safe or reentrant. Each simulation or thread should
 * use its own {@code CylinderSim} instance. Fields are mutable and public for
 * performance in controlled, single-threaded contexts.
 * </p>
 * <p>
 * <b>Key components:</b>
 * <ul>
 * <li>{@link CylinderSolid} - the geometric shape being simulated</li>
 * <li>{@link Basis} - orthonormal basis for ray direction sampling</li>
 * <li>{@link MonteSim} - simulation strategies for different scenarios</li>
 * </ul>
 * </p>
 *
 * @author hangal1
 */
public class CylinderSim implements ShapeSimulation
{
  static final int MAX_ATTEMPTS = 100;
  // Random number generators
  UniformRandom ur = new UniformRandom();
  NormalRandom nr = new NormalRandom();

  // The current shape being evaluated
  public CylinderSolid cylinder = new CylinderSolid();

  // the location of the source
  public MutableVector3 source = new MutableVector3();

  // Direction from source to center of cylinder
  public MutableVector3 direction = new MutableVector3();

  // Direction for current ray
  MutableVector3 ray = new MutableVector3();

  // Basis for cone
  Basis cone = new Basis();
  Basis scatter = new Basis();

  public MutableVector3 tmp1 = new MutableVector3();
  public MutableVector3 tmp2 = new MutableVector3();
  public MutableVector3 tmp3 = new MutableVector3();
  public MutableVector3 tmp4 = new MutableVector3();

  double correlation;

  // Chord lengths simulated for random rays 
  double[] distances = new double[6];

  double path;
  double first;

  // Simulations 
  public MonteSim runDirect = new RunDirect();
  public MonteSim runScatter = new RunScatter();
  public MonteSim runChords = new RunChords();
  private double diag;

  public CylinderSim()
  {
    this.ur = new UniformRandom();
  }

  /**
   * Sets the random seed for the uniform random number generator used in this
   * simulation.
   *
   * <p>
   * This ensures reproducibility of the simulation by initializing the
   * underlying {@link UniformRandom} generator with the specified seed value.
   * All subsequent random draws for geometry, source placement, and ray
   * directions will be determined by this seed.
   * </p>
   *
   * @param seed the seed value to initialize the random number generator
   */
  public void setSeed(long seed)
  {
    ur.setSeed(seed);
  }

  /**
   * Randomizes the source location for the current cylinder simulation.
   *
   * <p>
   * This method generates a random point outside the cylinder to serve as the
   * emission (source) location for ray tracing simulations. The source is
   * selected in the following way:
   * <ol>
   * <li>Draws a random direction in the positive octant using a normal
   * distribution, then normalizes it.</li>
   * <li>Scales this direction by a random range (exponentially distributed),
   * ensuring the source is placed at varying distances from the origin.</li>
   * <li>Assigns the negative of this scaled direction to the source
   * coordinates, so the source points toward the cylinder.</li>
   * <li>Ensures the generated source is outside the current cylinder geometry;
   * if not, repeats the process.</li>
   * <li>Computes the normalized direction vector from the source toward the
   * origin (center of the cylinder).</li>
   * <li>Initializes the orthonormal basis ({@code cone}) for subsequent random
   * direction sampling, and computes the minimum cosine angle
   * ({@code correlation}) required to cover the cylinder from the chosen source
   * location.</li>
   * </ol>
   * </p>
   *
   * <p>
   * This setup ensures that subsequent rays cast from the source will be
   * directed toward the cylinder, and that the acceptance cone for ray sampling
   * is correctly configured.
   * </p>
   */
  @Override
  public void drawSourceLocation()
  {
    for (int i = 0; i < MAX_ATTEMPTS; ++i)
      while (true)
      {
        // Choose a random point in the first quadrent relative to the origin
        double x = Math.abs(nr.draw());
        double y = Math.abs(nr.draw());
        double z = Math.abs(nr.draw());
        double scale = Math.sqrt(x * x + y * y + z * z);

        // Reject samples that are too small 
        if (scale < 1e-9)
          continue;

        // Chose a range from origin
        double range = Math.pow(ur.draw(-1, 2), 10);
        this.setSourceLocation(-x * range / scale, -x * range / scale, -x * range / scale);

        // Make sure we are outside the shape
        if (cylinder.inside(source))
          continue;

        return;
      }

    // This should never happen as our cylinder is unit dimensions and the source location is large.
    throw new NoSuchElementException("Unable to find source location");
  }

  /**
   * Randomizes the cylinder geometry for the next simulation run.
   *
   * <p>
   * This method selects new random dimensions (radius and height) for the
   * cylinder using the configured uniform random number generator. The
   * resulting dimensions are normalized and stored in the {@code cylinder}
   * field, setting up the geometry for subsequent simulation calls.
   * </p>
   */
  @Override
  public void drawShape()
  {
    // Choose a new cuboid shape
    cylinder.drawDimensions(ur);
    diag = 1;
  }

  /**
   * Fills the provided array with random chord lengths sampled through the
   * current cylinder configuration.
   *
   * <p>
   * For each entry in the {@code chords} array, this method calls
   * {@link #drawChord()} to generate a random chord length according to the
   * current cylinder's geometry. This is typically used to generate a batch of
   * samples for statistical analysis or machine learning validation.
   * </p>
   *
   * @param chords Array to be filled with sampled chord lengths. Must be
   * preallocated to the desired batch size.
   */
  @Override
  public void simulateChord(double[] chords)
  {
    for (int i = 0; i < chords.length; ++i)
      chords[i] = drawChord();
  }

  public void setDimensions(double diameter, double height)
  {
    this.diag = Math.sqrt(diameter * diameter + height * height);
    this.cylinder.setDimensions(diameter, height);
  }

  public void setSourceLocation(double x, double y, double z)
  {
    // Always come from the lower quadant
    if (x > 0)
      x = -x;
    if (y > 0)
      y = -y;
    if (z > 0)
      z = -z;

    x /= diag;
    y /= diag;
    z /= diag;

    double scale = Math.sqrt(x * x + y * y + z * z);
    source.x = x;
    source.y = y;
    source.z = z;

    // The root direction to head towards the origin
    direction.x = -x / scale;
    direction.y = -y / scale;
    direction.z = -z / scale;

    // Compute orthogonal basis for casting rays on object
    cone.assign(direction);
    correlation = this.coverCylinder(cylinder, source);
  }

  boolean rethrow = false;
  double v;

  /**
   * Samples a random chord length through the current cylinder using uniform
   * randomization and rejection sampling.
   *
   * <p>
   * This method generates a random chord by:
   * <ol>
   * <li>Selecting a random starting point uniformly within the cylinder
   * volume.</li>
   * <li>Choosing a random direction (uniformly distributed on the sphere).</li>
   * <li>Computing the intersection points of the line with the cylinder surface
   * (including caps).</li>
   * <li>Calculating the distance between the two intersection points as the
   * chord length.</li>
   * </ol>
   * </p>
   *
   * <p>
   * To ensure the distribution of chord lengths is unbiased (avoiding the
   * Bertrand paradox), the method uses rejection sampling: a random sample is
   * accepted only if a uniform draw (scaled by the maximum possible chord
   * length) is less than the computed chord length. This corrects for the fact
   * that naive sampling would overrepresent longer chords.
   * </p>
   *
   * @return The length of a randomly sampled chord through the cylinder.
   */
  public double drawChord()
  {
    double R = cylinder.radius;
    double H = cylinder.height;

    if (rethrow)
    {
      rethrow = false;
      return ur.draw(0, v);
    }

    for (int i = 0; i < MAX_ATTEMPTS; ++i)
    {
      // Select random point inside cylinder
      double theta = 2 * Math.PI * Math.random();
      double r = R * Math.sqrt(Math.random()); // uniform distribution in circle
      double x = r * Math.cos(theta);
      double y = r * Math.sin(theta);
      double z = H * (Math.random() - 0.5);

      // Random direction
      double dx = Math.random() - 0.5;
      double dy = Math.random() - 0.5;
      double dz = Math.random() - 0.5;
      double norm = Math.sqrt(dx * dx + dy * dy + dz * dz);
      dx /= norm;
      dy /= norm;
      dz /= norm;

      // Quadratic equation to find intersections with cylinder (ignoring caps)
      double a = dx * dx + dy * dy;
      double b = 2 * (x * dx + y * dy);
      double c = x * x + y * y - R * R;

      double discriminant = b * b - 4 * a * c;
      if (discriminant < 0)
        continue; // No real intersection

      double sqrtDisc = Math.sqrt(discriminant);
      double t1 = (-b - sqrtDisc) / (2 * a);
      double t2 = (-b + sqrtDisc) / (2 * a);

      double z1 = z + dz * t1;
      double z2 = z + dz * t2;

      // Check intersections with cylinder caps
      if (Math.abs(z1) > H / 2)
      {
        t1 = (dz > 0 ? (H / 2 - z) : (-H / 2 - z)) / dz;
      }
      if (Math.abs(z2) > H / 2)
      {
        t2 = (dz > 0 ? (H / 2 - z) : (-H / 2 - z)) / dz;
      }

      // Ensure valid intersection points
      if (t1 >= t2)
        continue;

      double chordLength = Math.abs(t2 - t1) * Math.sqrt(dx * dx + dy * dy + dz * dz);

      double maxDimension = Math.max(2 * R, H);

      if (chordLength < maxDimension)
      {
        rethrow = true;
        v = chordLength;
        return v;
      }

      if (ur.draw(0, chordLength) > maxDimension)
        continue;

      return chordLength;
    }
    throw new NoSuchElementException("Unable to find acceptable chord");
  }

  /**
   * Generates a batch of direct (unscattered) ray samples for the current
   * cylinder configuration.
   *
   * <p>
   * This method fills the provided {@code chords} array with simulated path
   * lengths, each representing a straight-line (direct) ray that enters and
   * exits the cylinder. For each sample, it calls {@link #drawDirect()} to
   * generate a random direct chord length according to the current geometry and
   * source location.
   * </p>
   *
   * @param chords Array to be filled with sampled chord lengths. The length of
   * the array determines the number of samples generated.
   */
  @Override
  public void simulateDirect(double[] chords)
  {
    // Simulate the number of requested chords
    for (int i = 0; i < chords.length; i++)
    {
      chords[i] = drawDirect();
    }
  }

  /**
   * Simulates a single direct (unscattered) ray from the current source
   * position toward the cylinder, returning the length of the segment where the
   * ray passes through the cylinder.
   *
   * <p>
   * This method repeatedly generates random directions within a cone defined by
   * the current basis and correlation until a ray is found that intersects the
   * cylinder in exactly two points (entry and exit). The method then returns
   * the distance between these two intersection points, representing the direct
   * chord length through the cylinder.
   * </p>
   *
   * <p>
   * If the computed exit distance is less than the entry distance, a
   * {@link RuntimeException} is thrown, indicating an unexpected geometry or
   * intersection order.
   * </p>
   *
   * @return The length of the chord where the sampled ray passes through the
   * cylinder.
   * @throws RuntimeException If the exit distance is before the entry distance.
   */
  public double drawDirect()
  {
    while (true)
    {
      // Create a random vector 
      cone.drawConeVector(ray, ur, correlation, 1);

      // Determine if we hit the target
      if (cylinder.intercept(distances, source, ray) != 2)
      {
        distances[1] = -1;
        continue;
      }
      break;
    }

    if (distances[1] < distances[0])
      throw new RuntimeException();

    tmp1.assign(source);
    tmp1.addAssignScaled(ray, distances[0]);
    tmp2.assign(source);
    tmp2.addAssignScaled(ray, distances[1]);

    // We have a ray that hits the target
    return distances[1] - distances[0];
  }

  /**
   * Generates a batch of scattering samples for the current cylinder
   * configuration.
   *
   * <p>
   * This method fills the provided {@code chords} array with simulated path
   * lengths, each representing a ray that enters the cylinder, scatters at a
   * random location (according to the specified attenuation), and then exits
   * after being deflected by a fixed scattering angle. Each sample uses the
   * same scattering angle ({@code cosAngle}) and attenuation coefficient
   * ({@code attenuation}), representing a fixed physical scenario.
   * </p>
   *
   * <p>
   * For each entry in the {@code chords} array, this method calls
   * {@link #drawScatter(double, double)} to generate a scattering event and
   * record the resulting path length.
   * </p>
   *
   * @param chords Array to be filled with simulated scattering results. The
   * length of the array determines the number of samples generated.
   * @param cosAngle The cosine of the scattering angle for all samples in this
   * batch.
   * @param attenuation The attenuation coefficient for all samples in this
   * batch.
   */
  @Override
  public void simulateScatter(double[] chords, double cosAngle, double attenuation)
  {
    // Cast a set of rays into the target
    // Simulate the number of requested chords
    for (int i = 0; i < chords.length; i++)
      chords[i] = drawScatter(cosAngle, attenuation);
  }

  /**
   * Simulates a single scattering event within the cylinder, returning the
   * distance traveled after scattering.
   *
   * <p>
   * This method performs the following steps:
   * <ol>
   * <li>Generates a random incident ray direction within the acceptance cone
   * that covers the cylinder.</li>
   * <li>Checks if the ray intersects the cylinder (must have exactly two
   * intersection points).</li>
   * <li>Randomly selects an interaction point along the entry-to-exit path,
   * using either uniform or exponential (attenuated) sampling depending on the
   * attenuation parameter.</li>
   * <li>Verifies that the interaction point is inside the cylinder.</li>
   * <li>From the interaction point, generates a scattered ray with a fixed
   * scattering angle (cosAngle).</li>
   * <li>Finds the intersection of the scattered ray with the cylinder's surface
   * (should be exactly one hit).</li>
   * <li>Returns the distance from the interaction point to the exit surface
   * along the scattered direction.</li>
   * </ol>
   * If a valid event is not found after {@code MAX_ATTEMPTS} trials, a
   * {@link NoSuchElementException} is thrown.
   * </p>
   *
   * @param cosAngle The cosine of the scattering angle for the scattered ray.
   * @param attenuation The attenuation coefficient. If zero or negative,
   * uniform sampling is used; otherwise, exponential attenuation is applied to
   * the path length.
   * @return The distance from the interaction point to the cylinder's exit
   * surface along the scattered ray.
   * @throws NoSuchElementException If a valid scattering event cannot be found
   * after {@code MAX_ATTEMPTS} attempts.
   */
  public double drawScatter(double cosAngle, double attenuation)
  {
    int j = 0;
    for (; j < MAX_ATTEMPTS; j++)
    {
      // Create a random vector 
      cone.drawConeVector(ray, ur, correlation, 1);

      // Determine if we hit the target
      int hits = cylinder.intercept(distances, source, ray);
      if (hits != 2)
        continue;

      // Deterime how far along the path we interacted
      first = distances[0];  // Record the first for auditing
      if (attenuation <= 0)  // with no attenuation draw evenly on the entire line
        path = ur.draw(0, distances[1] - distances[0]);
      else  // otherwise use an exponential so we draw close to the surface
        path = Math.log(ur.draw(Math.exp(-attenuation * (distances[1] - distances[0])), 1)) / -attenuation;

      // Advance our position along the ray
      tmp1.assign(source);
      tmp1.addAssignScaled(ray, first + path);

      if (!cylinder.inside(tmp1))
      {
        System.out.println("Cuboid " + cylinder.radius + " , " + cylinder.height);
        System.out.println("Source " + source);
        System.out.println("Not inside " + distances[0] + " " + distances[1] + " " + path + "  " + attenuation);
        throw new RuntimeException("Outside");
      }

      tmp4.assign(source);
      tmp4.addAssignScaled(ray, first);

      // Construct a scattered ray relative to incoming ray
      scatter.assign(ray);
      scatter.drawConeVector(tmp2, ur, cosAngle, cosAngle);

      // Find the intersection of the raw with a surface.  (hopefully only one)
      hits = cylinder.intercept(distances, tmp1, tmp2);
      if (hits != 1)
      {
        System.out.println("hits " + hits + " " + cylinder.inside(tmp1));
        throw new RuntimeException("Double hit");
      }

      tmp3.assign(tmp1);
      tmp3.addAssignScaled(tmp2, distances[0]);

      // We have a ray that hits the target
      return distances[0];
    }
    throw new NoSuchElementException("Too Many");
  }

  /**
   * Computes the minimum cosine of the angle required for a cone originating at
   * the source to fully encompass the cylinder, ensuring that any ray cast
   * within this cone will intersect the cylinder.
   * <p>
   * This method iterates over the six extreme corners of the cylinder (as seen
   * from the source point) and computes the cosine of the angle between the
   * source vector and the vector to each corner. The smallest cosine (i.e., the
   * largest angle) is returned, which defines the acceptance cone. This is used
   * to efficiently generate random directions for ray tracing simulations: any
   * direction within this cone is guaranteed to intersect the cylinder,
   * avoiding wasted samples.
   * </p>
   *
   * @param cylinder The cylinder whose coverage cone is being computed.
   * @param source The source location (outside the cylinder) from which rays
   * originate.
   * @return The minimum cosine of the angle to the cylinder's corners,
   * corresponding to the cone's aperture.
   */
  public double coverCylinder(CylinderSolid cylinder, MutableVector3 source)
  {
    // Compute an acceptance angle that should cover the cuboid 
    // (so that we don't have to through vectors as random and miss.)
    double c2;

    // For a cylinder we can use ... to find the maximum cone angle.
    tmp1.assign(source);
    tmp1.x -= cylinder.radius;
    tmp1.z -= cylinder.height / 2;
    c2 = Vector3Ops.cos(tmp1, source);
    double c1 = c2;

    tmp1.x += 2 * cylinder.radius;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.x -= cylinder.radius;
    tmp1.y += cylinder.radius;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.y -= cylinder.radius;
    tmp1.x -= cylinder.radius;
    tmp1.z += cylinder.height;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.x += 2 * cylinder.radius;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.x -= cylinder.radius;
    tmp1.y += cylinder.radius;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    return c1;
  }

  // ... existing fields and methods ...
  // 1. Direct Monte Carlo simulation
  public class RunDirect implements MonteSim
  {
    @Override
    public double[] nextConfiguration()
    {
      drawShape();
      drawSourceLocation();
      double[] features = new double[10]; // adjust size as needed

      CylinderUtility.fillCylinderFeatures(features, 0, source.x, source.y, source.z, cylinder.radius * 2, cylinder.height, diag);

      if (DoubleArray.isNaN(features))
        throw new RuntimeException("NaN in features");
      return features;
    }

    @Override
    public void draw(double[] values)
    {
      simulateDirect(values);
    }

    @Override
    public void setSeed(long l)
    {
      CylinderSim.this.setSeed(l);
    }

    @Override
    public int getConditionSize()
    {
      return 10; // adjust to match features array length
    }

    @Override
    public int getDynamicsSize()
    {
      return 0;
    }
  }

  // 2. Scatter Monte Carlo simulation
  public class RunScatter implements MonteSim
  {
    double cosAngle;
    double attenuation;

    @Override
    public double[] nextConfiguration()
    {
      drawShape();
      drawSourceLocation();
      double[] features = new double[10 + 10]; // geometry + dynamics, adjust as needed
      CylinderUtility.fillCylinderFeatures(features, 0, source.x, source.y, source.z, cylinder.radius * 2, cylinder.height, diag);

      // Draw scattering parameters
      this.cosAngle = ur.draw(-1, 1);
      this.attenuation = Math.pow(10, ur.draw(-3, 3));

      AngularUtility.fillAngularFeatures(features, 10, cosAngle, this.attenuation);
      if (DoubleArray.isNaN(features))
        throw new RuntimeException("NaN in features");
      return features;
    }

    @Override
    public void draw(double[] values)
    {
      simulateScatter(values, this.cosAngle, this.attenuation);
    }

    @Override
    public void setSeed(long l)
    {
      CylinderSim.this.setSeed(l);
    }

    @Override
    public int getConditionSize()
    {
      return 10;
    }

    @Override
    public int getDynamicsSize()
    {
      return 10;
    }
  }

  // 3. Chord Monte Carlo simulation
  public class RunChords implements MonteSim
  {
    @Override
    public double[] nextConfiguration()
    {
      drawShape();
      double[] features = new double[2]; // e.g., radius and height
      features[0] = cylinder.radius;
      features[1] = cylinder.height;
      return features;
    }

    @Override
    public void draw(double[] values)
    {
      simulateChord(values);
    }

    @Override
    public void setSeed(long l)
    {
      CylinderSim.this.setSeed(l);
    }

    @Override
    public int getConditionSize()
    {
      return 2; // radius and height
    }

    @Override
    public int getDynamicsSize()
    {
      return 0;
    }
  }
}
