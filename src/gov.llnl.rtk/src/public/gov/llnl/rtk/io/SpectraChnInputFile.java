// --- file: gov/llnl/rtk/io/SpectraChnInputFile.java ---
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
import static gov.llnl.rtk.io.ChnSpectrum.convert;
import gov.llnl.utility.InputStreamUtilities;
import static gov.llnl.utility.StringUtilities.getFixedString;
import gov.llnl.utility.io.DataFileReader;
import gov.llnl.utility.io.ReaderException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author yao2
 */
public class SpectraChnInputFile implements DataFileReader<IntegerSpectrum>
{

  private SeekableByteChannel bc;

  private IntegerSpectrum getNext() throws IOException, ReaderException
  {
    IntegerSpectrum spec = new IntegerSpectrum();
    CHNheader header = new CHNheader();
    CHNfooter footer = new CHNfooter();

    int M = 32;

    ByteBuffer bb = ByteBuffer.allocate(M);
    bc.read(bb);
    bb.rewind();
    bb.order(ByteOrder.LITTLE_ENDIAN);

    header.magic = bb.getShort();
    header.mca_number = bb.getShort();
    header.segment = bb.getShort();

    String start_second_ = getFixedString(bb, 2);

    header.realtime = bb.getInt();
    header.livetime = bb.getInt();

    String start_date_ = getFixedString(bb, 8);
    String start_minute_ = getFixedString(bb, 4);

    short start_channel_ = bb.getShort();
    short number_channels_ = bb.getShort();

    for (int i = 0; i < 2; i++)
    {
      header.ascii_starttime[i] = (byte) start_second_.charAt(i);
    }

    for (int i = 0; i < 7; i++)
    {
      header.ascii_date[i] = (byte) start_date_.charAt(i);
    }
//
    header.century = (byte) start_date_.charAt(7);
//
    for (int i = 0; i < 4; i++)
    {
      header.ascii_time[i] = (byte) start_minute_.charAt(i);
    }
//
    header.channel_offset = start_channel_;
    header.num_channel = number_channels_;
//

    int SIZE = 32 * number_channels_;
    bb = ByteBuffer.allocate(SIZE);

    bc.read(bb);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.rewind();

    int[] v = new int[number_channels_];
    for (int i = 0; i < number_channels_; i++)
    {
      v[i] = bb.getInt();
    }
    spec.assign(v);
    spec.setMaximumValidChannel(number_channels_);

    short recordtype_ = bb.getShort();
    short is_ = bb.getShort();

    float energy_zero_ = bb.getFloat();
    float energy_slope_ = bb.getFloat();
    float energy_quadratic_ = bb.getFloat();
    float fwhm_zero_ = bb.getFloat();
    float fwhm_slope_ = bb.getFloat();
    float fwhm_quadratic_ = bb.getFloat();

    footer.magic = recordtype_;
    footer.resv = is_;
    footer.energy_calibration_zero_intercept = energy_zero_;
    footer.energy_calibration_slope = energy_slope_;
    footer.energy_calibration_quadratic = energy_quadratic_;
    footer.peak_shape_calibration_zero_intercept = fwhm_zero_;
    footer.peak_shape_calibration_slope = fwhm_slope_;
    footer.peak_shape_calibration_quadratic = fwhm_quadratic_;

    // read in rest of the footer
    int sizeResv3 = 228;
    for (int i = 0;
            i < sizeResv3;
            i++)
    {
      footer.resv3[i] = bb.get();
    }

    footer.length_detector_description = bb.get();

    int sizeDesc = 64;
    for (int i = 0; i < sizeDesc; i++)
    {
      footer.detector_description[i] = bb.get();
    }

    footer.length_sample_description = bb.get();

    for (int i = 0; i < sizeDesc; i++)
    {
      footer.sample_description[i] = bb.get();
    }

    int sizeResv4 = 128;
    for (int i = 0; i < sizeResv4; i++)
    {
      footer.resv4[i] = bb.get();
    }

    convert(header, footer, spec);
    return spec;
  }

  @Override
  public IntegerSpectrum loadFile(Path path) throws FileNotFoundException, IOException, ReaderException
  {
    try (SeekableByteChannel bc = Files.newByteChannel(path))
    {
      this.bc = bc;
      return getNext();
    }
  }

  @Override
  public IntegerSpectrum loadStream(InputStream stream) throws IOException, ReaderException
  {
    try (SeekableByteChannel bc = InputStreamUtilities.newByteChannel(InputStreamUtilities.readAllBytes(stream)))
    {
      this.bc = bc;
      return getNext();
    }
  }

}
