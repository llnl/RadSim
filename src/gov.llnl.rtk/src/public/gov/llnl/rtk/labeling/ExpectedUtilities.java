// --- file: gov/llnl/rtk/labeling/ExpectedUtilities.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.labeling;

import gov.llnl.utility.ReflectionUtilities;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author seilhan3
 */
public class ExpectedUtilities
{
  public static ExpectedList filterByDetector(Collection<Expected> el, UUID uuid)
  {
    Supplier<ExpectedList> ctor = ReflectionUtilities.getConstructor("gov.llnl.rtk.labeling.ExpectedListImpl", ExpectedList.class);
    return el.stream().filter(e -> e.getDetectorID().equals(uuid)).collect(Collectors.toCollection(ctor));
  }

  public static ExpectedList addTimeSlop(Collection<Expected> el, int pre, int post)
  {
    ExpectedList out = new ExpectedListImpl();
    for (Expected e : el)
    {
      ExpectedImpl o = new ExpectedImpl(e);
      o.addTimeSlop(pre, post);
      out.add(o);
    }
    return out;
  }

  public static boolean contains(Expected expected, Instant startTime)
  {
    return startTime.isAfter(expected.getStartTime()) && startTime.isBefore(expected.getEndTime());
  }

}
