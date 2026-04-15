// --- file: gov/llnl/rtk/io/SpectraChnOutputFile.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.io.ChnSpectrum.CHNfooter;
import gov.llnl.rtk.io.ChnSpectrum.CHNheader;
import gov.llnl.utility.io.DataStreamWriter;
import gov.llnl.utility.io.PropertyException;
import gov.llnl.utility.io.WriterException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author nelson85
 */
public class SpectraChnOutputFile implements DataStreamWriter<IntegerSpectrum>
{
  private SeekableByteChannel bc;

  @Override
  public void openFile(Path path) throws IOException
  {
    bc = Files.newByteChannel(path,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
  }

  @Override
  public void close() throws IOException
  {
    if (bc != null)
      bc.close();
  }

  @Override
  public void put(IntegerSpectrum spec) throws IOException, WriterException
  {
    CHNheader header = new CHNheader();
    CHNfooter footer = new CHNfooter();
    convert(spec, header, footer);

    // FIXME this should be using ByteBuffer as it is a binary file format
    int SIZE = 4 * (spec.size() + 200);
    ByteBuffer bb = ByteBuffer.allocate(SIZE);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.putShort(header.magic);
    bb.putShort(header.mca_number);
    bb.putShort(header.segment);

    for (int i = 0; i < 2; i++)
      bb.put(header.ascii_starttime[i]);

    bb.putInt(header.realtime);
    bb.putInt(header.livetime);

    for (int i = 0; i < 7; i++)
      bb.put(header.ascii_date[i]);
    bb.put(header.century);

    for (int i = 0; i < 4; i++)
      bb.put(header.ascii_time[i]);

    bb.putInt(header.channel_offset);
    bb.putInt(header.num_channel);

    int[] v = spec.toArray();
    for (int i = 0; i < spec.size(); i++)
      bb.putInt((int) v[i]); // (double) to (int) FIXME

    bb.putShort(footer.magic);
    bb.putShort(footer.resv);

    bb.putFloat((float) footer.energy_calibration_zero_intercept);
    bb.putFloat((float) footer.energy_calibration_slope);
    bb.putFloat((float) footer.energy_calibration_quadratic);
    bb.putFloat((float) footer.peak_shape_calibration_zero_intercept);
    bb.putFloat((float) footer.peak_shape_calibration_slope);
    bb.putFloat((float) footer.peak_shape_calibration_quadratic);

    // read in rest of the footer
    int sizeResv3 = 228;
    for (int i = 0; i < sizeResv3; i++)
    {
      bb.put(footer.resv3[i]);
    }

    bb.put(footer.length_detector_description);
    System.out.printf(" desc length = %d \n", footer.length_detector_description);

    int sizeDesc = 64;
    for (int i = 0; i < sizeDesc; i++)
      bb.put(footer.detector_description[i]);

    bb.put(footer.length_sample_description);

    for (int i = 0; i < sizeDesc; i++)
      bb.put(footer.sample_description[i]);

    int sizeResv4 = 128;
    for (int i = 0; i < sizeResv4; i++)
      bb.put(footer.resv4[i]);

    bc.write(bb);
  }

  private void convert(IntegerSpectrum spec, CHNheader header, CHNfooter footer)
  {

    header.mca_number = ((Number) spec.getAttribute("CHN::mca_number")).shortValue();
    header.segment = ((Number) spec.getAttribute("CHN::segment")).shortValue();
    header.realtime = (int) (spec.getRealTime() / 0.02);
    header.livetime = (int) (spec.getLiveTime() / 0.02);

    footer.energy_calibration_zero_intercept
            = ((Number) spec.getAttribute("CHN::energy::K0")).doubleValue();
    footer.energy_calibration_slope
            = ((Number) spec.getAttribute("CHN::energy::K1")).doubleValue();

    if (footer.energy_calibration_slope == 0)
      footer.energy_calibration_slope = 1;

    footer.energy_calibration_quadratic
            = ((Number) spec.getAttribute("CHN::energy::K2")).doubleValue();

    footer.peak_shape_calibration_zero_intercept
            = ((Number) spec.getAttribute("CHN::shape::K0")).doubleValue();

    footer.peak_shape_calibration_slope
            = ((Number) spec.getAttribute("CHN::shape::K1")).doubleValue();

    if (footer.peak_shape_calibration_slope == 0)
    {
      footer.peak_shape_calibration_slope = 1;
    }

    footer.peak_shape_calibration_quadratic
            = ((Number) spec.getAttribute("CHN::shape::K2")).doubleValue();

    String sd = spec.getTitle().substring(0, 63) + " ";
    String dd = spec.getAttribute("CHN::detector_description").toString().substring(0, 63) + " ";
    //sd.reserve(64);
    //dd.reserve(64);

    footer.sample_description = sd.getBytes();
    footer.detector_description = dd.getBytes();

    //memcpy(footer.sample_description, sd.c_str(),63);
    //memcpy(footer.detector_description, dd.c_str(),63);
    footer.length_detector_description = (byte) footer.sample_description.length;
    footer.length_sample_description = (byte) footer.detector_description.length;
  }

  @Override
  public void setProperty(String key, Object property) throws PropertyException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Object getProperty(String key) throws PropertyException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
