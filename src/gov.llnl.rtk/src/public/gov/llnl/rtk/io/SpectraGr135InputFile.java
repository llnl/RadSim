// --- file: gov/llnl/rtk/io/SpectraGr135InputFile.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.io.Gr135Spectrum.Gr135CheckSum;
import static gov.llnl.rtk.io.Gr135Spectrum.bcd2dec;
import static gov.llnl.rtk.io.Gr135Spectrum.convert;
import gov.llnl.utility.InputStreamUtilities;
import gov.llnl.utility.UnsignedUtilities;
import gov.llnl.utility.io.DataStreamReader;
import gov.llnl.utility.io.PropertyException;
import gov.llnl.utility.io.RandomAccessData;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.io.SequentialAccessData;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 *
 * @author yao2
 */
public class SpectraGr135InputFile implements DataStreamReader<IntegerSpectrum>,
        RandomAccessData, SequentialAccessData
{
  boolean mode_; // write = true
  ArrayList<Long> records_;
  private SeekableByteChannel fsi_;

  @Override
  public void openFile(Path path) throws FileNotFoundException, IOException, ReaderException
  {
    fsi_ = Files.newByteChannel(path);
    readHeader();
  }

  @Override
  public void openStream(InputStream stream) throws IOException, ReaderException
  {
    fsi_ = InputStreamUtilities.newByteChannel(InputStreamUtilities.readAllBytes(stream));
    readHeader();
  }

  public SpectraGr135InputFile()
  {
    this.records_ = new ArrayList<>();
    mode_ = false;
    fsi_ = null;
  }

  @Override
  public IntegerSpectrum getNext() throws IOException, ReaderException
  {
    Gr135Spectrum data = new Gr135Spectrum();
    Gr135CheckSum sum;
    sum = new Gr135CheckSum();

    ByteBuffer bb = ByteBuffer.allocate(75);
    int bytes = fsi_.read(bb);
    bb.rewind();
    if (bytes != 75)
      return null;

    sum.proc(bb.array(), 75);

    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.get(data.synchro);
    if (data.synchro[0] != 'Z'
            || data.synchro[1] != 'Z'
            || data.synchro[2] != 'Z'
            || data.synchro[3] != 'Z')
    {
      throw new ReaderException("Synchro not found");
    }

    bb.get(data.instrument_type);
    data.record_type = bb.get();
    data.sequence = bb.getInt();
    data.year = bb.get();
    data.month = bb.get();
    data.day = bb.get();
    data.hour = bb.get();
    data.min = bb.get();
    data.sec = bb.get();
    data.nch = bb.getShort();
    data.real_time_ms = bb.getInt();
    data.gain = bb.getShort();
    data.peak = bb.getShort();
    data.FW = bb.getShort();
    data.dose_unit = bb.get();
    data.geiger = bb.getInt();
    data.neutron = bb.getShort();
    data.pileup = bb.getShort();
    data.serial = bb.getShort();
    data.version = bb.getShort();

    for (int i = 0; i < 3; i++)
    {
      data.ecal[i] = bb.getFloat();
    }

    bb.get(data.temp);
    bb.get(data.spare);

    data.year = bcd2dec(data.year);
    data.month = bcd2dec(data.month);
    data.day = bcd2dec(data.day);
    data.hour = bcd2dec(data.hour);
    data.min = bcd2dec(data.min);
    data.sec = bcd2dec(data.sec);

    if (data.nch > 1024)
      throw new ReaderException("Number of channels exceeds limit");

    ByteBuffer bb2 = ByteBuffer.allocate(2 * data.nch + 1);
    bb2.order(ByteOrder.LITTLE_ENDIAN);
    this.fsi_.read(bb2);
    bb2.rewind();
    sum.proc(bb2.array(), 2 * data.nch);
    data.live_time_ms = bb2.getInt();
    // GR 135 format has a funny way of counting channels
    for (int i = 0; i < data.nch - 4; i++)
    {
      // Unsigned shorts are not something java does
      data.spec[i] = UnsignedUtilities.getUnsignedShort(bb2.getShort());
    }
    data.checksum = bb2.get();
    IntegerSpectrum spec = new IntegerSpectrum();
    convert(data, spec);
    return spec;
  }

  void readHeader() throws ReaderException, IOException
  {
    mode_ = false;
    ByteBuffer bb = ByteBuffer.allocate(75);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    byte magic[] = new byte[4];
    byte name[] = new byte[4];
    byte type[] = new byte[1];

    // Check the magic
    fsi_.read(bb);
    bb.rewind();
    bb.get(magic);
    if ((magic[0] != 'Z') || (magic[1] != 'Z') || (magic[2] != 'Z') || (magic[3] != 'Z'))
    {
      throw new ReaderException("Incorrect magic");
    }

    // Get the instrument type
    bb.get(name);
    // It should be 1350 but checking fines that I have
    // shows that this is rarely the case. Most have 135, but
    // the next digit varies greatly.

    // Walk through the records
    long size = fsi_.size();

    // Move pointer back to beginning of file
    fsi_.position(0);

    while (fsi_.position() < size)
    {
      long tell = fsi_.position();
      bb.rewind();
      fsi_.read(bb);
      bb.rewind();
      bb.get(magic);
      if ((magic[0] != 'Z') || (magic[1] != 'Z') || (magic[2] != 'Z') || (magic[3] != 'Z'))
        throw new ReaderException("Incorrect magic");
      bb.get(name);
      bb.get(type);
      bb.position(19);
      int numChannels = bb.getShort();

      // No length == BAD
      if (numChannels < 0)
        throw new ReaderException("Incorrect length");
      if (numChannels == 0)
        break;

      // Valid record found
      if (type[0] == 'A' && numChannels == 1024)
        records_.add(tell);

      // Seek to the end of the record
      fsi_.position(tell + 75 + 2 * numChannels + 1);
    }
    fsi_.position(0);
  }

  @Override
  public void close() throws IOException
  {
    if (fsi_ != null)
      fsi_.close();
  }

  @Override
  public void seek(int record) throws IOException
  {
    fsi_.position(this.records_.get(record));
  }

  @Override
  public void skip(int record) throws IOException
  {
    long current = fsi_.position();
    int i = 0;
    while (this.records_.get(i) < current)
    {
      i++;
    }
    fsi_.position(this.records_.get(i + record));
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
