// --- file: gov/llnl/rtk/response/SplineResponseEntry.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data collected to represent a mono-energetic line from the source.
 */
public class SplineResponseEntry
{

  // Continuum representation
  SplineResponseContinuum continuum = new SplineResponseContinuum();

  // Line representation
  SplineResponseLine photoelectric = null;
  SplineResponseLine annihilation = null;
  SplineResponseLine singleEscape = null;
  SplineResponseLine doubleEscape = null;
  SplineResponseLine[] peaks = null;


  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 53 * hash + Objects.hashCode(this.continuum);
    hash = 53 * hash + Objects.hashCode(this.photoelectric);
    hash = 53 * hash + Objects.hashCode(this.annihilation);
    hash = 53 * hash + Objects.hashCode(this.singleEscape);
    hash = 53 * hash + Objects.hashCode(this.doubleEscape);
    hash = 53 * hash + Arrays.deepHashCode(this.peaks);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final SplineResponseEntry other = (SplineResponseEntry) obj;
    if (!Objects.equals(this.continuum, other.continuum))
      return false;
    if (!Objects.equals(this.photoelectric, other.photoelectric))
      return false;
    if (!Objects.equals(this.annihilation, other.annihilation))
      return false;
    if (!Objects.equals(this.singleEscape, other.singleEscape))
      return false;
    if (!Objects.equals(this.doubleEscape, other.doubleEscape))
      return false;
    return Arrays.deepEquals(this.peaks, other.peaks);
  }

  /**
   * @return the continuum
   */
  public SplineResponseContinuum getContinuum()
  {
    return continuum;
  }
  
    /**
   * @return the photoelectric
   */
  public SplineResponseLine getPhotoelectric()
  {
    return photoelectric;
  }

  /**
   * @return the annihilation
   */
  public SplineResponseLine getAnnihilation()
  {
    return annihilation;
  }

  /**
   * @return the singleEscape
   */
  public SplineResponseLine getSingleEscape()
  {
    return singleEscape;
  }

  /**
   * @return the doubleEscape
   */
  public SplineResponseLine getDoubleEscape()
  {
    return doubleEscape;
  }

}
