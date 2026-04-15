// --- file: gov/llnl/rtk/quality/FaultSetEncoding.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author seilhan3
 */
public class FaultSetEncoding implements Serializable
{
  transient HashMap<String, Integer> trackedFaults = new HashMap<>();
  private boolean autoExtending = false;

  public int addTrackedFault(Fault fault)
  {
    String name = fault.getName();

    if (!trackedFaults.containsKey(name))
    {
      trackedFaults.put(name, 1 << trackedFaults.size());
    }
    return trackedFaults.get(name);
  }

  public int getCode(Fault fault)
  {
    // if we are autoExtending, add unconditionally then return code.
    //   add method ensures we don't add a duplicate value.
    if (autoExtending)
      return addTrackedFault(fault);

    Integer code = trackedFaults.get(fault.getName());
    if (code == null)
      return 0;

    return code;
  }

  public int encode(FaultSet faults)
  {
    int code = 0;
    for (Fault f : faults)
    {
      code |= getCode(f);
    }
    return code;
  }

  public boolean isAutoExtending()
  {
    return autoExtending;
  }

  public void setAutoExtending(boolean autoExtending)
  {
    this.autoExtending = autoExtending;
  }

  public String[] getDescriptions()
  {
    return trackedFaults.keySet().toArray(new String[0]);
  }

  public String dump()
  {
    StringBuilder sb = new StringBuilder();

    ArrayList<Map.Entry<String, Integer>> set = new ArrayList<>(trackedFaults.entrySet());
    Collections.sort(set, new Comparator<Map.Entry<String, Integer>>()
    {
      @Override
      public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
      {
        return o1.getValue().compareTo(o2.getValue());
      }
    });
    for (Map.Entry<String, Integer> k : set)
    {
      sb.append(String.format("%2d %s", k.getValue(), k.getKey())).append('\n');
    }
    if (sb.length() > 0)
      sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

}
