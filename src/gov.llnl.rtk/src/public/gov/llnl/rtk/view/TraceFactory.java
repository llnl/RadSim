// --- file: gov/llnl/rtk/view/TraceFactory.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Vector3Ops;
import gov.llnl.utility.TemporalUtilities;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author nelson85
 */
public class TraceFactory
{
  public static Trace linear(Instant startTime, Vector3 startPosition,
          Instant endTime, Vector3 endPosition)
  {
    Duration duration = Duration.between(startTime, endTime);
    double dt = TemporalUtilities.toSeconds(duration);
    long startSecond = startTime.getEpochSecond();
    long startNano = startTime.getNano();

    return new Trace()
    {
      @Override
      public Vector3 get(Instant time)
      {
        double delta = (time.getEpochSecond() - startSecond) + 1e-9 * (time.getNano() - startNano);
        delta /= dt;
        return Vector3Ops.interpolate(delta, startPosition, endPosition);
      }
    };
  }

  public static void main(String[] args)
  {
    Instant start = Instant.parse("2020-01-01T12:00:00Z");
    Instant end = Instant.parse("2020-01-01T12:00:05Z");
    Trace t = linear(start, Vector3.of(3, -10, 0), end, Vector3.of(3, 10, 0));
    System.out.println(t.get(start));
    System.out.println(t.get(end));
    System.out.println(t.get(start.plus(2500, ChronoUnit.MILLIS)));

  }
}
