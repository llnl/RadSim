// --- file: gov/llnl/rtk/data/EnergyBinsImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.DoubleArray;
import gov.llnl.utility.ExpandableObject;
import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.annotation.Internal;
import java.util.Arrays;

/**
 *
 * @author nelson85
 */
@Internal
public class EnergyBinsImpl extends ExpandableObject implements EnergyScale
{
  private static final long serialVersionUID = UUIDUtilities.createLong("EnergyBins-v2");
  double[] values;

  public EnergyBinsImpl()
  {
  }

  public EnergyBinsImpl(double[] values)
  {
    this.values = values;
  }

  public EnergyBinsImpl(EnergyBinsImpl bins)
  {
    super(bins);
    this.values = DoubleArray.copyOf(bins.values);
  }

//<editor-fold desc="equivalent">
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final EnergyBinsImpl other = (EnergyBinsImpl) obj;
    if (!Arrays.equals(this.values, other.values))
      return false;
    return true;
  }

  /**
   * Used by the Writers to determine if the same energy bin structure is shared
   * with multiple detectors. Does not look at attributes currently.
   *
   * @return
   */
  @Override
  public int hashCode()
  {
    int hash = (int) serialVersionUID;
    hash = 19 * hash + Arrays.hashCode(this.values);
    return hash;
  }
//</editor-fold>
//<editor-fold desc="EnergyScale">

  @Override
  public int getChannels()
  {
    if (this.values == null)
      return 0;
    return values.length - 1;
  }

  @Override
  public double getEdge(int edge)
  {
    return values[edge];
  }

  @Override
  public double[] getCenters()
  {
    if (values == null)
      return null;
    double[] out = new double[this.values.length - 1];
    for (int i = 0; i < out.length; ++i)
      out[i] = (values[i] + values[i + 1]) / 2;
    return out;
  }

  @Override
  public double[] getEdges()
  {
    return this.values;
  }

  @Override
  public int findEdgeFloor(double energy)
  {
    if (energy < values[0])
      return -1;
    int index = Arrays.binarySearch(values, energy);
    if (index < 0)
      return -index - 2;
    return index;
  }

  @Override
  public int findEdgeCeiling(double energy)
  {
    if (energy < values[0])
      return 0;
    int index = Arrays.binarySearch(values, energy);
    if (index < 0)
      return -index - 1;
    return index;
  }

  @Override
  public double findBin(double energy)
  {
    int lower = findEdgeFloor(energy);
    if (lower < 0)
      return -0.5;
    if (lower >= values.length-1)
      return values.length + 0.5;
    double eLower = values[lower];
    double eUpper = values[lower + 1];

    double fraction = (energy - eLower) / (eUpper - eLower);
    return lower + fraction;
  }

//</editor-fold>
  @Override
  public double getEnergyOfEdge(double edge)
  {
    if (edge < 0)
      return values[0];
    int lower;
    int upper;
    if (edge < values.length - 2)
    {
      lower = (int) edge;
      upper = lower + 1;
    }
    else
    {
      lower = values.length - 2;
      upper = values.length - 1;
    }
    double E0 = values[lower];
    double E1 = values[upper];
    double f = edge - lower;
    return (1 - f) * E0 + f * E1;
  }
}
