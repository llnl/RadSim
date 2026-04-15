// --- file: gov/llnl/rtk/io/ChnSpectrum.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.utility.annotation.Internal;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author nelson85
 */
@Internal
class ChnSpectrum
{
  private int encodeString(byte[] data, String value)
  {
    try
    {
      byte[] out = value.getBytes("UTF-8");
      for (int i = 0; i < data.length; ++i)
      {
        data[i] = 0;
      }
      int sz = Math.min(data.length - 1, out.length);
      for (int i = 0; i < sz; ++i)
      {
        data[i] = out[i];
      }
      return sz;
    }
    catch (UnsupportedEncodingException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  static public class CHNheader // Ortec - Integer Data Files
  {
    public short magic; // must be -1
    public short mca_number;
    public short segment;
    public byte ascii_starttime[] = new byte[2];
    public int realtime;  // units 20 ms
    public int livetime;  // units 20 ms
    public byte ascii_date[] = new byte[7]; // DDMMMYY
    public byte century; // 1=2000, 1900=otherwise
    public byte ascii_time[] = new byte[4]; // HHMM
    public int channel_offset;
    public int num_channel;

    public CHNheader()
    {
      magic = -1;
      mca_number = 0;
      segment = 0;
      ascii_starttime[0] = ascii_starttime[1] = '0';
      realtime = 0;
      livetime = 0;
      ascii_date = ("00Jan00").getBytes();
      century = '1';
      ascii_time = ("0000").getBytes();
      channel_offset = 0;
      num_channel = 0;
    }
  };

  static public class CHNfooter
  {
    public short magic; // -101 - old version, -102 - new version
    public short resv;
    public double energy_calibration_zero_intercept;     // default 0.0
    public double energy_calibration_slope;              // default 1.0
    public double energy_calibration_quadratic;          // resv, default 0.0
    public double peak_shape_calibration_zero_intercept; // default 1.0
    public double peak_shape_calibration_slope;          // default 0.0
    public double peak_shape_calibration_quadratic;      // resv, default 0.0
    public byte length_detector_description;
    public byte detector_description[] = new byte[64]; // length=63
    public byte length_sample_description;
    public byte sample_description[] = new byte[64]; // length=63
    public byte resv3[] = new byte[228];
    public byte resv4[] = new byte[128];

    public CHNfooter()
    {
      magic = -102; // -101 - old version, -102 - new version
      resv = 0;
      energy_calibration_zero_intercept = 0;     // default 0.0
      energy_calibration_slope = 0;              // default 1.0
      energy_calibration_quadratic = 0;          // resv, default 0.0
      peak_shape_calibration_zero_intercept = 0; // default 1.0
      peak_shape_calibration_slope = 0;          // default 0.0
      peak_shape_calibration_quadratic = 0;      // resv, default 0.0

      length_detector_description = 0;
      length_sample_description = 0;

      for (int i = 0; i < 64; i++)
      {
        detector_description[i] = 0;
        sample_description[i] = 0;
      }

      for (int i = 0; i < 228; i++)
      {
        resv3[i] = 0;
      }

      for (int i = 0; i < 128; i++)
      {
        resv4[i] = 0;
      }
    }
  };

  static public void convert(CHNheader header, CHNfooter footer, IntegerSpectrum spec)
  {
    try
    {
      spec.setAttribute("CHN::magic", (int)header.magic);
      spec.setAttribute("CHN::mca_number", (int) header.mca_number);
      spec.setAttribute("CHN::segment", (int) (header.segment));
      spec.setRealTime((header.realtime * 0.02));
      spec.setLiveTime((header.livetime * 0.02));

      // FIXME Date
      //char ascii_starttime[2];
      //char ascii_date[7]; // DDMMMYY
      //char century; // 1=2000, 1900=otherwise
      //char ascii_time[4]; // HHMM
      //unsigned short channel_offset;
      //unsigned short num_channel;
      spec.setAttribute("CHN::energy::K0", footer.energy_calibration_zero_intercept);
      spec.setAttribute("CHN::energy::K1", footer.energy_calibration_slope);
      spec.setAttribute("CHN::energy::K2", footer.energy_calibration_quadratic);
      spec.setAttribute("CHN::shape::K0", footer.peak_shape_calibration_zero_intercept);
      spec.setAttribute("CHN::shape::K1", footer.peak_shape_calibration_slope);
      spec.setAttribute("CHN::shape::K2", footer.peak_shape_calibration_quadratic);

      footer.sample_description[63] = 0;
      footer.detector_description[63] = 0;
      spec.setTitle(new String(footer.sample_description, "UTF-8"));
      spec.setAttribute("CHN::detector_description",
              new String(footer.detector_description, "UTF-8"));
    }
    catch (UnsupportedEncodingException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  void convert(DoubleSpectrum spec, CHNheader header, CHNfooter footer)
  {
    header.mca_number = (Short) spec.getAttribute("CHN::mca_number");
    header.segment = (Short) spec.getAttribute("CHN::segment");
    header.realtime = (int) (spec.getRealTime() / 0.02);
    header.livetime = (int) (spec.getLiveTime() / 0.02);
    // FIXME Date
    //char ascii_starttime[2];
    //char ascii_date[7]; // DDMMMYY
    //char century; // 1=2000, 1900=otherwise
    //char ascii_time[4]; // HHMM
    //unsigned short channel_offset;
    //unsigned short num_channel;
    footer.energy_calibration_zero_intercept = spec.getAttribute("CHN::energy::K0", Double.class);
    footer.energy_calibration_slope = spec.getAttribute("CHN::energy::K1", Double.class);
    if (footer.energy_calibration_slope == 0)
    {
      footer.energy_calibration_slope = 1;
    }
    footer.energy_calibration_quadratic = spec.getAttribute("CHN::energy::K2", Double.class);
    footer.peak_shape_calibration_zero_intercept = spec.getAttribute("CHN::shape::K0", Double.class);
    footer.peak_shape_calibration_slope = spec.getAttribute("CHN::shape::K1", Double.class);
    if (footer.peak_shape_calibration_slope == 0)
    {
      footer.peak_shape_calibration_slope = 1;
    }
    footer.peak_shape_calibration_quadratic = (Double) spec.getAttribute("CHN::shape::K2");
    String sd = spec.getTitle().substring(0, 63);
    String dd = ((String) spec.getAttribute("CHN::detector_description")).substring(0, 63);
    //sd.reserve(64);
    //dd.reserve(64);
    footer.length_sample_description = (byte) encodeString(footer.sample_description, sd);
    footer.length_detector_description = (byte) encodeString(footer.detector_description, dd);
  }
}
