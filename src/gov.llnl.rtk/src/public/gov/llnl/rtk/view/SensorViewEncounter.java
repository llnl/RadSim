// --- file: gov/llnl/rtk/view/SensorViewEncounter.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Vector3Ops;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class SensorViewEncounter
{
  public boolean dynamic = true;

  public static class Output
  {
    public SensorView sensor;
    public Collection<Instant> times;
    public double[] solidAngle;  // in solid angle  (includes dt factor)
    public double[] sensorBearing; // direction to the sensor from prospective of source.
  }

  public boolean computeBearing = false;

  /**
   *
   * @param sensor is the sensor under simulation.
   * @param times is the boundaries for the times of the encounter
   * @param trace is the function of motion over the passby.
   * @return
   */
  public Output simulate(SensorView sensor, Collection<Instant> times, Trace trace)
  {
    int n = times.size();

    // Create an output structure
    Output output = new Output();
    output.sensor = sensor;
    output.solidAngle = new double[n - 1];
    output.times = times;
    if (computeBearing)
    {
      output.sensorBearing = new double[n - 1];
    }

    // Initialize state variables
    Iterator<Instant> iter = times.iterator();
    Instant time0 = iter.next();
    Vector3 pos0 = trace.get(time0);

    // For each interval
    int i = 1;
    for (; iter.hasNext();)
    {
      Instant time1 = iter.next();
      Vector3 pos1 = trace.get(time1);

      // FIXME this should depend on the amount of angular change for the sensor
      double dt = Duration.between(time0, time1).toNanos() * 1e-9;
      if (dynamic)
      {
        Vector3 v0 = Vector3Ops.subtract(pos0, sensor.getOrigin());
        Vector3 v1 = Vector3Ops.subtract(pos1, sensor.getOrigin());
        double correlation = Vector3Ops.correlation(v0, v1);
        int parts2 = 2;
        if (correlation < 0.99995)
          parts2 = 3;
        if (correlation < 0.9995)
          parts2 = 7;
        if (correlation < 0.995)
          parts2 = 13;
        output.solidAngle[i - 1] = dt * integrate2(sensor, pos0, pos1, parts2);
      }
      else
      {
        output.solidAngle[i - 1] = dt * integrate2(sensor, pos0, pos1, 9);
      }

      // Compute bearing if requested
      if (computeBearing)
      {
        Vector3 dv = Vector3Ops.subtract(pos1, pos0);
        output.sensorBearing[i - 1] = Bearing.compute(dv,
                Vector3Ops.subtract(sensor.getOrigin(), pos0));
      }

      // Update our position
      pos0 = pos1;
      time0 = time1;
      i++;
    }
    return output;
  }

  public List<Output> simulateCollection(Iterable<SensorView> sensors, Collection<Instant> times, Trace trace)
  {
    ArrayList<Output> out = new ArrayList<>();
    for (SensorView sensor : sensors)
    {
      out.add(this.simulate(sensor, times, trace));
    }
    return out;
  }

  double integrate(SensorView sensor, Vector3 pos0, Vector3 pos1, int parts)
  {
    if (parts == 0)
    {
      Vector3 mid = Vector3Ops.multiply(Vector3Ops.add(pos0, pos1), 0.5);
      return sensor.computeSolidAngle(mid);
    }

    parts *= 2;
    Vector3 dv = Vector3Ops.subtract(pos1, pos0);

    // Integrate with Simpson's rule
    double sum = sensor.computeSolidAngle(pos0);
    sum += sensor.computeSolidAngle(pos1);
    for (int j = 1; j < parts; j++)
    {
      double f = ((double) j) / (parts + 1);
      Vector3 v = Vector3Ops.add(pos0, Vector3Ops.multiply(dv, f));
      int coef = 2 << (j & 1);
      sum += coef * sensor.computeSolidAngle(v);
    }
    // (b-a)/3/n
    return sum / 3.0 / parts;
  }

  double integrate2(SensorView sensor, Vector3 pos0, Vector3 pos1, int parts)
  {
    Vector3 dv = Vector3Ops.subtract(pos1, pos0);

    // Integrate with Simpson's rule
    double sum = 0;
    for (int j = 0; j < parts; j++)
    {
      double f = (j + 0.5) / parts;
      Vector3 v = Vector3Ops.add(pos0, Vector3Ops.multiply(dv, f));
      sum += sensor.computeSolidAngle(v);
    }
    return sum / parts;
  }

}
