// --- file: gov/llnl/rtk/io/SpectrumSpcInputFile.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.Spectrum;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author nelson85
 */
public class SpectrumSpcInputFile
{
  public DoubleSpectrum loadFile(Path p) throws IOException
  {
    // Stored in blocks of 128 bytes.  First is the header
    ByteBuffer record = ByteBuffer.allocate(64 * 2);

    try ( SeekableByteChannel byteChannel = Files.newByteChannel(p))
    {
      byteChannel.read(record);
      record.rewind();
      record.order(ByteOrder.LITTLE_ENDIAN);
      short INFTYP = record.getShort(); // must be 1
      short FILTYP = record.getShort(); // must be 1
      if (INFTYP != 1 || FILTYP != 1)
        throw new RuntimeException("Bad header " + INFTYP);
      short Contents = record.getShort(); // (1)
      short RESV = record.getShort();
      short ACQIRP = record.getShort(); // (3) I*2 Acquisition information record pointer
      short SAMDRP = record.getShort(); // (4) I*2 Sample description record pointer
      short DETDRP = record.getShort(); // (5) I*2 Detector description record pointer
      short EBRDESC = record.getShort(); // (0) I*2 EBAR description record
      short ANARP1 = record.getShort(); // (0x0b) I*2 First analysis parameters record pointer
      short ANARP2 = record.getShort(); // (0x11) I*2 Second analysis parameters record pointer
      short ANARP3 = record.getShort(); // (0x1a) I*2 Third analysis parameters record pointer
      short ANARP4 = record.getShort(); // (0x2b) I*2 Fourth analysis parameters record pointer
      short SRPDES = record.getShort(); // (0) I*2 Absorption correction description record pointer
      short IEQDESC = record.getShort(); // (0) I*2 IEQ description record pointer
      short GEODES = record.getShort(); // (0) I*2 Geometry correction description record pointer
      short MPCDESC = record.getShort(); // (0) I*2 MPC description record pointer
      short CALDES = record.getShort(); // (0x0a) I*2 Calibration description record pointer
      short CALRP1 = record.getShort(); // (6) I*2 First calibration data record pointer
      short CALRP2 = record.getShort(); // (7) I*2 Second calibration data record pointer
      short EFFPRP = record.getShort(); // (0) I*2 Efficiency pairs record pointer (first record)
      short ROIRP1 = record.getShort(); // (36) I*2 Record number of the first of the two ROI records
      short Energy_pairs = record.getShort();  // (0) record pointer
      short NPAIRS = record.getShort(); // (0) pair records
      record.getShort(); // (0) 
      record.getShort(); // (0) Disable deconvolution of unknown peaks
      record.getShort(); // (0) True = microcuries, false = becquerels
      short PERPTR = record.getShort(); //  (0x2a) I*2 Laboratory and operator name record pointer
      short MAXRCS = record.getShort(); //  (0x138) I*2 Maximum record number ever used
      short LSTREC = record.getShort(); // (0x138) I*2 Maximum record number in use
      short EFFPNM = record.getShort(); // (0) I*2 Number of efficiency pairs records (See Word 20)
      short SPCTRP = record.getShort(); // (0x39) I*2 Spectrum record pointer (pointer to first record)
      short SPCRCN = record.getShort(); // (0x100) I*2 Number of records in the spectrum
      short SPCCHN = record.getShort(); // (8196) I*2 Number of channels in spectrum
      short ABSTCH = record.getShort(); // (0) I*2 Physical start channel for data
      float ACQTIM = record.getFloat(); //  (X) R*4 Date and time of acquisition start in DECDAY format
      double ACQTI8 = record.getDouble(); // (X) R*8 Date and time as double precision DECDAY
      short SEQNUM = record.getShort(); // (0) I*2 Sequence number
      short MCANU = record.getShort(); //  (1) I*2 MCA number as two ASCII characters (old) or Detector number as integer for systems with Connections
      short SEGNUM = record.getShort(); //  (1) I*2 Segment number as two ASCII characters (old)or as integer value 1 for systems with Connections
      short MCADVT = record.getShort(); // (0) I*2 MCA device type
      short CHNSRT = record.getShort(); // (0) I*2 Start channel number
      double realtime = record.getFloat(); // R*4 Real time in seconds
      double livetime = record.getFloat(); //R*4 Live time in seconds


      byteChannel.position(128 * (SPCTRP-1));
      double[] counts = new double[SPCCHN];
      int j = CHNSRT;
      for (int i = 0; i < SPCRCN; i++)
      {
        record.rewind();
        byteChannel.read(record);
        record.rewind();
        int k = Math.min(j + 32, SPCCHN);
        for (; j < k; ++j)
        {
          counts[j] = record.getInt();
        }
      }
      
      return Spectrum.builder().counts(counts).time(livetime, realtime).asDouble();
    }
  }
}
