// --- file: gov/llnl/rtk/io/SPESpectrum.java ---
/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.data.IntegerSpectrum;
import static gov.llnl.rtk.data.SpectrumAttributes.*;
import gov.llnl.utility.annotation.Internal;
import java.time.Instant;
import java.util.ArrayList;

/**
 *
 * @author nelson85
 */
@Internal
class SPESpectrum
{
  // Attributes
  static final String SPE_ATTRIBUTE_ROI = "SPE.ROI";
  static final String SPE_ATTRIBUTE_SHAPE = "SPE.shape";

  public static class RegionOfInterest
  {
    public int begin;
    public int end;

    public RegionOfInterest(int b, int e)
    {
      this.begin = b;
      this.end = e;
    }
  }
  public String title_;
  public ArrayList<String> comments_;
  public Instant timestamp_ = null;
  public double live_time_;
  public double real_time_;
  public int[] gamma_counts_;
  public ArrayList<RegionOfInterest> roi_;
  public double[] energy_calibration_;
  public double[] shape_calibration_;

  public SPESpectrum()
  {
    clear();
  }

  final void clear()
  {
    title_ = "";
    live_time_ = 0;
    real_time_ = 0;
    comments_ = new ArrayList<>();;
    gamma_counts_ = null;
    roi_ = new ArrayList<>();;
  }

  /**
   *
   * @param data
   * @param spec
   */
  public static void convert(SPESpectrum data, IntegerSpectrum spec)
  {
    spec.setTitle(data.title_);
    spec.setStartTime(data.timestamp_);
    spec.setLiveTime(data.live_time_);
    spec.setRealTime(data.real_time_);
    if (data.gamma_counts_==null)
      throw new NullPointerException("Spectrum counts is null");
    spec.resize(data.gamma_counts_.length);
    spec.assign(data.gamma_counts_);
    spec.setAttribute(SPE_ATTRIBUTE_ROI, data.roi_);
    spec.setAttribute(ENERGY_POLYNOMIAL, data.energy_calibration_);
    spec.setAttribute(SPE_ATTRIBUTE_SHAPE, data.shape_calibration_);
  }

  public static void convert(IntegerSpectrum spec, SPESpectrum data)
  {
    data.title_ = spec.getTitle();
    data.timestamp_ = spec.getStartTime();
    data.live_time_ = (double) spec.getLiveTime();
    data.real_time_ = spec.getRealTime();
    data.gamma_counts_ = new int[spec.size()];
    int[] v = spec.toArray();
    for (int i = 0; i < data.gamma_counts_.length; i++)
      data.gamma_counts_[i] = v[i];
    data.roi_ = (ArrayList<RegionOfInterest>) spec.getAttribute(SPE_ATTRIBUTE_ROI);
    data.energy_calibration_ = spec.getAttribute("energy_poly", double[].class);
    data.shape_calibration_ = (double[]) spec.getAttribute(SPE_ATTRIBUTE_SHAPE);
  }
}
