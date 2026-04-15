// --- file: gov/llnl/rtk/physics/UnitImpl.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.rtk.RtkPackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nelson85
 */
class UnitImpl implements Units, Serializable
{
  // List of known units
  static final HashMap<String, Units> units;

  static Units get(String name)
  {
    name = name.replaceAll("\\^", "");
    Units unit = units.get(name);
    if (unit == null)
      throw new UnitsException("unknown unit " + name);
    return unit;
  }

  private final double value;
  private final String name;
  private final UnitType type;

  public UnitImpl(UnitType type, String part1, double value)
  {
    this.type = type;
    this.name = part1;
    this.value = value;
  }

  @Override
  public String getSymbol()
  {
    return name;
  }

  /**
   * Value relative to the SI unit.
   *
   * @return
   */
  @Override
  public double getValue()
  {
    return value;
  }

  @Override
  public UnitType getType()
  {
    return type;
  }

  @Override
  public String toString()
  {
    return this.name;
  }

  static
  {
    units = new HashMap<>();
    HashMap<String, PhysicalProperty> prop = new HashMap<>();

    for (PhysicalProperty v : PhysicalProperty.values())
    {
      prop.put(v.getMeasure(), v);
    }

    try (InputStream is = RtkPackage.getInstance().getResource("units.csv"))
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(is));

      while (true)
      {
        String buffer = br.readLine();
        if (buffer == null)
          break;
        if (buffer.startsWith("#"))
          continue; // skip comments
        if (buffer.isEmpty())
          continue;
        int i0 = buffer.indexOf(",");
        String desc = buffer.substring(0, i0).trim();
        double value = Double.parseDouble(buffer.substring(i0 + 1).trim());
        String[] parts = desc.split(":");
        PhysicalProperty measure = prop.get(parts[0]);
        if (measure == null)
        {
          System.err.println("bad measure " + parts[0]);
        }
        if (value == 1.0)
        {
          units.put(parts[1], measure);
          units.put(desc, measure);
        }
        else
        {
          UnitImpl u = new UnitImpl(measure, parts[1], value);
          units.put(parts[1], u);
          units.put(desc, u);
        }
      }
    }
    catch (IOException ex)
    {
      Logger.getLogger(UnitImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
