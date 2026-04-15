// --- file: gov/llnl/rtk/io/SpectraGr135OutputFile.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.io.Gr135Spectrum.Gr135CheckSum;
import gov.llnl.utility.io.DataStreamWriter;
import gov.llnl.utility.io.PropertyException;
import gov.llnl.utility.io.WriterException;
import java.io.FileOutputStream;
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
public class SpectraGr135OutputFile implements DataStreamWriter<IntegerSpectrum>
{
  private FileOutputStream fso_;

  @Override
  public void openFile(Path path) throws IOException
  {
    SeekableByteChannel bc = Files.newByteChannel(path,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
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

  @Override
  public void put(IntegerSpectrum spec) throws IOException, WriterException
  {
    Gr135Spectrum data;
    data = new Gr135Spectrum();
    Gr135Spectrum.convert(spec, data);

    if (data.nch != 1024)
      throw new WriterException("Unsupported number of channels");

    int SIZE = 75 - 19 - 2 + 2 * data.nch + 1;
    ByteBuffer bb = ByteBuffer.allocate(SIZE);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.put(data.synchro);
    bb.put(data.instrument_type);
    bb.put(data.record_type);

    bb.putInt(data.sequence);
    bb.put(data.year);
    bb.put(data.month);
    bb.put(data.day);
    bb.put(data.hour);
    bb.put(data.min);
    bb.put(data.sec);
    bb.putShort(data.nch);
    bb.putInt(data.real_time_ms);
    bb.putShort(data.gain);
    bb.putShort(data.peak);
    bb.putShort(data.FW);
    bb.put(data.dose_unit);
    bb.putInt(data.geiger);
    bb.putShort(data.neutron);
    bb.putShort(data.pileup);
    bb.putShort(data.serial);
    bb.putShort(data.version);

    for (int i = 0; i < 3; i++)
      bb.putFloat(data.ecal[i]);

    bb.put(data.temp);
    bb.put(data.spare);
    bb.putInt(data.live_time_ms);

    // GR 135 format has a funny way of counting channels
    for (int i = 0; i < data.nch - 2; i++)
      bb.putInt(data.spec[i]);

    Gr135CheckSum sum = new Gr135CheckSum();
    sum.proc(bb.array(), SIZE - 1);
    bb.put((byte) sum.sum);
    data.checksum = (byte) sum.sum;
  }

  @Override
  public void close() throws IOException
  {
    if (this.fso_ != null)
      this.fso_.close();
  }
}
