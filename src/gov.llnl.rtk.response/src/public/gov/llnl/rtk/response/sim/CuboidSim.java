// --- file: gov/llnl/rtk/response/sim/CuboidSim.java ---
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
import gov.llnl.rtk.response.deposition.CuboidUtility;
import java.util.NoSuchElementException;

/**
 * Simulation engine for ray tracing and geometric analysis in cuboid
 * (rectangular box) geometries.
 * <p>
 * {@code CuboidSim} generates random cuboid shapes and source positions, then
 * simulates the interaction of rays with the cuboid for various scenarios:
 * <ul>
 * <li><b>Direct:</b> Simulates straight-line (unscattered) ray intersections
 * with the cuboid.</li>
 * <li><b>Scatter:</b> Simulates rays that scatter inside the cuboid, modeling
 * attenuation and angular distribution.</li>
 * <li><b>Chord:</b> Simulates random chords (line segments) through the cuboid
 * for analytic comparison or validation.</li>
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
 * use its own {@code CuboidSim} instance. Fields are mutable and public for
 * performance in controlled, single-threaded contexts.
 * </p>
 * <p>
 * <b>Key components:</b>
 * <ul>
 * <li>{@link CuboidSolid} - the geometric shape being simulated</li>
 * <li>{@link Basis} - orthonormal basis for ray direction sampling</li>
 * <li>{@link MonteSim} - simulation strategies for different scenarios</li>
 * </ul>
 * </p>
 *
 * @author nelson85
 */
public class CuboidSim implements ShapeSimulation
{
  final int MAX_ATTEMPTS = 100;
  // Random number generators
  UniformRandom ur = new UniformRandom();
  NormalRandom nr = new NormalRandom();

  // The current shape being evaluated
  public CuboidSolid cuboid = new CuboidSolid();

  // the location of the source
  MutableVector3 source = new MutableVector3();

  // Direction from source to center of cube
  MutableVector3 direction = new MutableVector3();

  // Direction for current ray
  MutableVector3 ray = new MutableVector3();

  // Basis for cone
  Basis cone = new Basis();
  Basis scatter = new Basis();

  MutableVector3 tmp1 = new MutableVector3();
  MutableVector3 tmp2 = new MutableVector3();
  MutableVector3 tmp3 = new MutableVector3();
  MutableVector3 tmp4 = new MutableVector3();

  double correlation;

  // Chord lengths simulated for random rays 
  double[] distances = new double[6];

  double path;
  double first;

  // Simulations 
  public MonteSim runDirect = new RunDirect();
  public MonteSim runScatter = new RunScatter();
  public MonteSim runChords = new RunChords();

  public CuboidSim()
  {
    this.ur = new UniformRandom();
  }

  public void setSeed(long seed)
  {
    ur.setSeed(seed);
  }

//<editor-fold desc="simulations" defaultstate="collapsed">
  /**
   * Monte Carlo simulation strategy for direct (unscattered) ray paths through
   * a cuboid.
   * <p>
   * This implementation of {@link MonteSim} generates random cuboid geometries
   * and source positions, then computes geometric features and simulates the
   * distribution of direct (straight-line) chord lengths for rays entering the
   * cuboid.
   * </p>
   * <p>
   * Used for validating analytic models and generating training data for
   * machine learning approaches to geometric response functions.
   * </p>
   * <p>
   * Each call to {@link #nextConfiguration()} randomizes the cuboid and source
   * location, returning the geometric features for this configuration. The
   * {@link #draw(double[])} method simulates a batch of direct chord lengths
   * for the current configuration.
   * </p>
   * <b>Thread safety:</b> Not thread-safe; intended for use within a
   * single-threaded simulation context.
   */
  public class RunDirect implements MonteSim
  {
    @Override
    public double[] nextConfiguration()
    {

      // Change the geometry of the object and source position
      drawShape();

      // Select an emission point for the projection
      drawSourceLocation();

      // Generate a new shape with a set of parameters
      double[] features = new double[15];
      CuboidUtility.fillCuboidFeatures(features, 0,
              source.x, source.y, source.z,
              cuboid.dimensions.x, cuboid.dimensions.y, cuboid.dimensions.z,
              1.0);
      
      if (DoubleArray.isNaN(features))
      {
        System.out.println("D: " + features[0] + " " + features[1] + " " + features[2]);
        System.out.println("E: " + features[3] + " " + features[4] + " " + features[5]);
        System.out.println("E0: " + source.x + " " + source.y + " " + source.z);
        throw new RuntimeException("Feature error");
      }
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
      CuboidSim.this.setSeed(l);
    }

    @Override
    public int getConditionSize()
    {
      return 15;
    }

    @Override
    public int getDynamicsSize()
    {
      return 0;
    }

  };

  /**
   * Monte Carlo simulation strategy for single-scatter ray paths through a
   * cuboid.
   * <p>
   * This implementation of {@link MonteSim} generates random cuboid geometries,
   * source positions, and scattering parameters (scattering angle and
   * attenuation), then computes geometric features and simulates the
   * distribution of chord lengths for rays that scatter once inside the cuboid.
   * </p>
   * <p>
   * Each call to {@link #nextConfiguration()} randomizes the cuboid, source
   * location, scattering angle, and attenuation, returning a feature vector
   * that includes both geometric and scattering parameters. The
   * {@link #draw(double[])} method simulates a batch of scattered chord lengths
   * for the current configuration.
   * </p>
   * <b>Thread safety:</b> Not thread-safe; intended for use within a
   * single-threaded simulation context.
   *
   * <ul>
   * <li>The first 15 features describe the cuboid geometry and emission
   * point.</li>
   * <li>Features 15–19 encode the scattering angle in several forms (cosine,
   * sine, and derived values).</li>
   * <li>Features 20–24 encode attenuation in various normalized forms.</li>
   * </ul>
   */
  public class RunScatter implements MonteSim
  {
    double cosAngle;
    double attenuation;

    /**
     * Generate a new simulation configuration and return its feature vector.
     *
     * <p>
     * This method draws a random cuboid shape and source location, then
     * computes a feature vector describing the simulation scenario. The feature
     * vector is organized as follows:
     * </p>
     *
     * <ul>
     * <li><b>features[0..14]</b>: Geometry and emission features (see
     * CuboidSolid.computeFeatures for details). These represent the "condition"
     * variables and should be passed as the encoder input (<code>xe</code>) to
     * downstream models such as ArchEPT.</li>
     * <li><b>features[15..24]</b>: Scattering parameters ("dynamics"
     * variables):
     * <ul>
     * <li><b>15</b>: cosAngle (scattering angle cosine, uniform in [-1,
     * 1])</li>
     * <li><b>16</b>: sqrt(1 - cosAngle^2) (sine of the scattering angle)</li>
     * <li><b>17</b>: (cosAngle - 1)^2 / 4 (derived feature)</li>
     * <li><b>18</b>: (cosAngle + 1)^2 / 4 (derived feature)</li>
     * <li><b>19</b>: acos(cosAngle) / PI (normalized scattering angle)</li>
     * <li><b>20..24</b>: Various transforms of attenuation (attenuation is
     * drawn log-uniformly from 10^-3 to 10^3):
     * <ul>
     * <li><b>20</b>: 1 - 0.01 / (attenuation + 0.01)</li>
     * <li><b>21</b>: 1 - 0.1 / (attenuation + 0.1)</li>
     * <li><b>22</b>: 1 - 1 / (attenuation + 1)</li>
     * <li><b>23</b>: 1 - 10 / (attenuation + 10)</li>
     * <li><b>24</b>: 1 - 100 / (attenuation + 100)</li>
     * </ul>
     * </li>
     * </ul>
     * These represent the "dynamics" variables and should be passed as the
     * state input (<code>xp</code>) to downstream models such as ArchEPT.
     * </li>
     * </ul>
     *
     * <p>
     * <b>Usage with Encoder/Predictor:</b><br>
     * When using this method to generate inputs for the Encoder/Predictor
     * model:
     * <ul>
     * <li>Pass <code>features[0..14]</code> as <code>xe</code> (encoder
     * input)</li>
     * <li>Pass <code>features[15..24]</code> as <code>xp</code> (predictor
     * input)</li>
     * </ul>
     * </p>
     *
     * @return A feature vector of length 25, partitioned as described above.
     */
    @Override
    public double[] nextConfiguration()
    {
      drawShape();

      // Select an emission point for the projection
      drawSourceLocation();

      double[] features = new double[25];
      CuboidUtility.fillCuboidFeatures(features, 0,
              source.x, source.y, source.z,
              cuboid.dimensions.x, cuboid.dimensions.y, cuboid.dimensions.z,
              1.0);

      this.cosAngle = ur.draw(-1, 1);
      this.attenuation = Math.pow(10, ur.draw(-3, 3));  // units of 1/diag len which is 1.

      AngularUtility.fillAngularFeatures(features, 15, cosAngle, this.attenuation);
      if (DoubleArray.isNaN(features))
      {
        System.out.println("D: " + features[0] + " " + features[1] + " " + features[2]);
        System.out.println("E: " + features[3] + " " + features[4] + " " + features[5]);
        System.out.println("E0: " + source.x + " " + source.y + " " + source.z);
        throw new RuntimeException("Feature error");
      }

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
      CuboidSim.this.setSeed(l);
    }

    @Override
    public int getConditionSize()
    {
      return 15;
    }

    @Override
    public int getDynamicsSize()
    {
      return 10;
    }

  };

  /**
   * Monte Carlo simulation strategy for generating random chord lengths through
   * a cuboid geometry.
   * <p>
   * The {@code RunChords} class implements the {@link MonteSim} interface,
   * providing methods to generate random cuboid configurations and sample
   * unbiased chord lengths through the cuboid. This simulation is typically
   * used for analytic validation and statistical analysis of chord-length
   * distributions in three-dimensional boxes.
   * </p>
   * <p>
   * Each call to {@link #nextConfiguration()} generates a new random cuboid and
   * returns its geometric dimensions as the feature vector. The
   * {@link #draw(double[])} method fills the provided array with random chord
   * lengths sampled through the current cuboid configuration.
   * </p>
   * <p>
   * <b>Thread safety:</b> This class is not thread-safe. Each thread or
   * simulation should use its own instance.
   * </p>
   *
   * @author nelson85
   */
  public class RunChords implements MonteSim
  {

    @Override
    public double[] nextConfiguration()
    {
      drawShape();

      double[] features = new double[3];
      features[0] = cuboid.dimensions.x;
      features[1] = cuboid.dimensions.y;
      features[2] = cuboid.dimensions.z;
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
      CuboidSim.this.setSeed(l);
    }

    @Override
    public int getConditionSize()
    {
      return 3;
    }

    @Override
    public int getDynamicsSize()
    {
      return 0;
    }

  };

//</editor-fold>
  /**
   * Create a random vector towards the cuboid.
   *
   */
  @Override
  public void drawSourceLocation()
  {
    for (int i = 0; i < MAX_ATTEMPTS; i++)
    {
      // Choose a random point in the first quadrant relative to the origin
      double x = Math.abs(nr.draw());
      double y = Math.abs(nr.draw());
      double z = Math.abs(nr.draw());
      double s = Math.sqrt(x * x + y * y + z * z);

      // Reject samples that are too small 
      if (s < 1e-9)
        continue;

      // Chose a range from origin
      double range = Math.pow(ur.draw(-1, 2), 10);
      source.x = -x * range / s;
      source.y = -y * range / s;
      source.z = -z * range / s;

      // Make sure we are outside the shape
      if (cuboid.inside(source))
        continue;

      // The root direction to head towards the origin
      direction.x = x / s;
      direction.y = y / s;
      direction.z = z / s;

      // Compute orthagonal basis for casting rays on object
      cone.assign(direction);
      correlation = this.coverCuboid(cuboid, source);
      return;
    }

    // This should never happen as our cuboid is unit dimensions and the source location is large.
    throw new NoSuchElementException("Unable to find source location");
  }

  public void setSourceLocation(double x, double y, double z)
  {
    double diag = cuboid.diag;
    x /= diag;
    y /= diag;
    z /= diag;

    double s = Math.sqrt(x * x + y * y + z * z);
    source.x = x;
    source.y = y;
    source.z = z;

    // The root direction to head towards the origin
    direction.x = -x / s;
    direction.y = -y / s;
    direction.z = -z / s;

    // Compute orthagonal basis for casting rays on object
    cone.assign(direction);
    correlation = this.coverCuboid(cuboid, source);
  }

  /**
   * Create a new simulation scenario by generating a random cuboid shape.
   *
   * <p>
   * The cuboid dimensions are drawn uniformly from [0.1, 1] for each axis, then
   * normalized so the cuboid's diagonal is always 1. This ensures:
   * <ul>
   * <li>No dimension is ever close to zero (minimum ≈ 0.0577 of the
   * diagonal).</li>
   * <li>All aspect ratios are bounded, avoiding degenerate or pathological
   * shapes.</li>
   * <li>Sampling logic in methods like {@code drawChord} is robust and
   * unbiased.</li>
   * </ul>
   * </p>
   */
  @Override
  public void drawShape()
  {
    // Choose a new cuboid shape
    cuboid.drawDimensions(ur);
  }

  public void setDimensions(double x, double y, double z)
  {
    this.cuboid.setDimensions(x, y, z);
  }

//<editor-fold desc="direct" defaultstate="collapsed">
  /**
   * Generate a set of samples for one configuration.
   *
   * @param chords
   */
  @Override
  public void simulateDirect(double[] chords)
  {
    // Simulate the number of requested chords
    for (int i = 0; i < chords.length; i++)
      chords[i] = drawDirect();
  }
  
  /**
   * Simulates a single direct ray from the current source position toward the
   * cuboid, returning the length of the segment where the ray passes through
   * the cuboid.
   * <p>
   * This method repeatedly generates random directions within a cone defined by
   * the current basis and correlation until a ray is found that intersects the
   * cuboid in exactly two points (entry and exit). The method then returns the
   * distance between these two intersection points.
   * </p>
   *
   * @return The length of the chord where the sampled ray passes through the
   * cuboid.
   * @throws RuntimeException if the computed exit distance is less than the
   * entry distance, indicating an unexpected geometry or intersection order.
   */
  public double drawDirect()
  {
    for (int i = 0; i < MAX_ATTEMPTS; ++i)
    {
      // Create a random vector 
      cone.drawConeVector(ray, ur, correlation, 1);

      // Determine if we hit the target
      if (cuboid.intercept(distances, source, ray) != 2)
      {
        distances[1] = -1;
        continue;
      }
      if (distances[1] < distances[0])
        throw new RuntimeException("drawDirect: Exit distance is before entry distance. distances[0]="
                + distances[0] + ", distances[1]=" + distances[1]);

      // We have a ray that hits the target
      return distances[1] - distances[0];
    }
    throw new NoSuchElementException("Unable to find acceptable incoming vector");
  }

  //</editor-fold>
//<editor-fold desc="scatter" defaultstate="collapsed">  
  /**
   * Generate a batch of scattering samples for the current cuboid
   * configuration.
   *
   * <p>
   * This method repeatedly calls {@link #drawScatter(double, double)} to fill
   * the provided array with simulated chord lengths (or other relevant
   * scattering metrics) for rays entering and scattering within the cuboid.
   * Each sample uses the same scattering angle (cosAngle) and attenuation
   * parameter, representing a fixed physical scenario.
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
   * Simulate a single scattering event within the cuboid, returning the
   * distance traveled after scattering.
   *
   * <p>
   * This method performs the following steps:
   * <ul>
   * <li>Generates a random incident ray direction within the acceptance cone
   * that covers the cuboid.</li>
   * <li>Checks if the ray intersects the cuboid (must have exactly two
   * intersection points).</li>
   * <li>Randomly selects an interaction point along the entry-to-exit path,
   * using either uniform or exponential (attenuated) sampling depending on the
   * attenuation parameter.</li>
   * <li>Verifies that the interaction point is inside the cuboid.</li>
   * <li>From the interaction point, generates a scattered ray with a fixed
   * scattering angle (cosAngle).</li>
   * <li>Finds the intersection of the scattered ray with the cuboid's surface
   * (should be exactly one hit).</li>
   * <li>Returns the distance from the interaction point to the exit surface
   * along the scattered direction.</li>
   * </ul>
   * If a valid event is not found after 100 trials, a
   * {@link NoSuchElementException} is thrown.
   * </p>
   *
   * @param cosAngle The cosine of the scattering angle for the scattered ray.
   * @param attenuation The attenuation coefficient. If zero or negative,
   * uniform sampling is used; otherwise, exponential attenuation is applied to
   * the path length.
   * @return The distance from the interaction point to the cuboid's exit
   * surface along the scattered ray.
   * @throws NoSuchElementException If a valid scattering event cannot be found
   * after 100 attempts.
   */
  public double drawScatter(double cosAngle, double attenuation)
  {
    int j = 0;
    for (; j < MAX_ATTEMPTS; j++)
    {
      // Create a random vector 
      cone.drawConeVector(ray, ur, correlation, 1);

      // Determine if we hit the target
      int hits = cuboid.intercept(distances, source, ray);
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
      if (!cuboid.inside(tmp1))
      {
        System.out.println("Cuboid " + cuboid.dimensions);
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
      hits = cuboid.intercept(distances, tmp1, tmp2);
      if (hits != 1)
      {
        System.out.println("hits " + hits + " " + cuboid.inside(tmp1));
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
   * Compute the minimum cosine of the angle required for a cone originating at
   * the source to fully encompass the cuboid, ensuring that any ray cast within
   * this cone will intersect the cuboid.
   *
   * <p>
   * This method iterates over all 8 corners of the cuboid, as seen from the
   * source point, and computes the cosine of the angle between the source
   * vector and the vector to each corner. The smallest cosine (i.e., the
   * largest angle) is returned, which defines the acceptance cone. This is used
   * to efficiently generate random directions for ray tracing simulations: any
   * direction within this cone is guaranteed to intersect the cuboid, avoiding
   * wasted samples.
   * </p>
   *
   * @param cuboid The cuboid whose coverage cone is being computed.
   * @param source The source location (outside the cuboid) from which rays
   * originate.
   * @return The minimum cosine of the angle to the cuboid's corners,
   * corresponding to the cone's aperture.
   */
  public double coverCuboid(CuboidSolid cuboid, MutableVector3 source)
  {
    // Compute an acceptance angle that should cover the cuboid 
    // (so that we don't have to through vectors as random and miss.)
    double c2;

    // For a cube we can use the corners to find the maximum cone angle.
    tmp1.assign(source);
    tmp1.x -= cuboid.dimensions.x / 2;
    tmp1.y -= cuboid.dimensions.y / 2;
    tmp1.z -= cuboid.dimensions.z / 2;
    c2 = Vector3Ops.cos(tmp1, source);
    double c1 = c2;

    tmp1.x += cuboid.dimensions.x;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.y += cuboid.dimensions.y;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.x -= cuboid.dimensions.x;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.y -= cuboid.dimensions.y;
    tmp1.z += cuboid.dimensions.z;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.x += cuboid.dimensions.x;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.y += cuboid.dimensions.y;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    tmp1.x -= cuboid.dimensions.x;
    c2 = Vector3Ops.cos(tmp1, source);
    c1 = Math.min(c1, c2);

    return c1;
  }
//</editor-fold>
//<editor-fold desc="chords" defaultstate="collapsed">

  /**
   * Fill the provided array with random chord lengths sampled through the
   * current cuboid configuration.
   *
   * <p>
   * For each entry in the <code>chords</code> array, this method calls
   * {@link #drawChord()} to generate a random chord length according to the
   * current cuboid's geometry and the unbiased sampling procedure. This is
   * typically used to generate a batch of samples for statistical analysis or
   * machine learning.
   * </p>
   *
   * @param chords Array to be filled with sampled chord lengths. Must be
   * preallocated.
   */
  @Override
  public void simulateChord(double[] chords)
  {
    for (int i = 0; i < chords.length; ++i)
      chords[i] = drawChord();
  }

  boolean rethrow = false;
  double v;

  /**
   * Sample a random chord length through the cuboid using uniform randomization
   * and rejection sampling.
   *
   * <p>
   * This method generates a random chord by:
   * <ol>
   * <li>Selecting a random starting point uniformly within the cuboid.</li>
   * <li>Choosing a random direction (uniformly distributed on the sphere).</li>
   * <li>Computing the distances to the cuboid surface in both the chosen
   * direction and its opposite.</li>
   * <li>Summing these distances to obtain the total chord length through the
   * starting point.</li>
   * </ol>
   *
   * <b>Statistical Correction (Bertrand Paradox):</b><br>
   * To ensure the distribution of chord lengths is truly uniform (and not
   * biased toward longer chords, which are more likely to be selected by naive
   * sampling), this method implements rejection sampling:
   * <ul>
   * <li>
   * If the sampled chord length <code>d3</code> is less than the cuboid's
   * smallest dimension (<code>z</code>), the method uses a "rethrow" mechanism:
   * on the next call, it returns a value uniformly drawn from
   * <code>[0, d3]</code>. This adjustment helps ensure that very short chords,
   * which are rare, are not underrepresented in the output distribution.
   * </li>
   * <li>
   * Otherwise, the method accepts the sample only if a uniform random draw in
   * <code>[0, d3]</code> exceeds <code>cuboid.dimensions.z</code>. This step
   * ensures that the probability of accepting a chord is proportional to its
   * length, correcting for the bias toward longer chords (the Bertrand
   * paradox).
   * </li>
   * </ul>
   *
   * <p>
   * This approach ensures that the returned distribution of chord lengths is
   * approximately uniform and suitable for statistical analysis or machine
   * learning.
   * <p>
   * Note: The "rethrow" mechanism is a practical adjustment to improve coverage
   * of rare, short chords and is not part of the standard rejection sampling
   * algorithm.
   *
   * @return The length of a randomly sampled chord through the cuboid.
   */
  public double drawChord()
  {
    if (rethrow)
    {
      rethrow = false;
      return ur.draw(0, v);
    }
    for (int i = 0; i < MAX_ATTEMPTS; ++i)
    {
      // Select a random starting point
      tmp1.x = ur.draw(-cuboid.dimensions.x / 2, cuboid.dimensions.x / 2);
      tmp1.y = ur.draw(-cuboid.dimensions.y / 2, cuboid.dimensions.y / 2);
      tmp1.z = ur.draw(-cuboid.dimensions.z / 2, cuboid.dimensions.z / 2);

      // Select a random direction
      tmp2.x = nr.draw();
      tmp2.y = nr.draw();
      tmp2.z = nr.draw();
      tmp2.normalize();

      // Find the distance to surface in each direction
      if (cuboid.intercept(distances, tmp1, tmp2) != 1)
        continue;
      double d1 = distances[0];

      tmp2.negateAssign();
      if (cuboid.intercept(distances, tmp1, tmp2) != 1)
        continue;
      double d2 = distances[0];

      // Use rejection sampling to avoid the fact longer chords are more likely 
      // to get selected.  Bertrand paradox
      double d3 = d1 + d2;

      // If we are less than the rate of rejection sampling, the fake it
      if (d3 < cuboid.dimensions.z)
      {
        rethrow = true;
        v = d3;
        return v;
      }

      // Use rejection sampling 
      if (ur.draw(0, d3) > cuboid.dimensions.z)
        continue;
      return d3;
    }
    throw new NoSuchElementException("Unable to find acceptable chord");
  }
//</editor-fold>
}
