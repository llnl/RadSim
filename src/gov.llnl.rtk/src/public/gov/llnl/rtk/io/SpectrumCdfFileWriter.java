// --- file: gov/llnl/rtk/io/SpectrumCdfFileWriter.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.utility.io.DataFileWriter;
import gov.llnl.utility.io.WriterException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author nelson85
 */
public class SpectrumCdfFileWriter implements DataFileWriter<DoubleSpectrum>
{
  @Override
  public void saveFile(Path path, DoubleSpectrum spec) throws IOException, WriterException
  {
    this.setWriter(Files.newBufferedWriter(path, UTF_8), spec);
  }

  @Override
  public void saveStream(OutputStream stream, DoubleSpectrum object) throws IOException, WriterException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  void setWriter(BufferedWriter writer, DoubleSpectrum spec) throws IOException, WriterException
  {
    try (BufferedWriter bw = writer)
    {
      PrintWriter fso_ = new PrintWriter(bw);

      fso_.println("::BEGINHEADER::");
      fso_.println("TITLE=");
      fso_.println(spec.getTitle());
      fso_.println("CHANNELS=");
      fso_.println(spec.size());
      fso_.println("::ENDHEADER::");

      EnergyScale energies = spec.getEnergyScale();

      if (energies != null)
      {
        if (energies.getChannels() != spec.size())
          throw new WriterException("Bad energy bin size");
      }

      if (energies == null)
        throw new WriterException("Energy bins must be set for cfd file");

      int i0;
      double[] b = energies.getEdges();
      double[] c = spec.toArray();
      fso_.print(b[0]);
      fso_.println(" 0");

      for (i0 = 0; i0 < spec.size(); i0++)
      {
        fso_.print(b[i0 + 1]);
        fso_.print(" ");
        fso_.println(c[i0]);
      }
    }
  }
}
