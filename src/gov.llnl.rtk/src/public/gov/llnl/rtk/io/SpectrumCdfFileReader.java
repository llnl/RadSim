// --- file: gov/llnl/rtk/io/SpectrumCdfFileReader.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.utility.io.DataFileReader;
import gov.llnl.utility.io.ReaderException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author yao2
 */
public class SpectrumCdfFileReader implements DataFileReader<DoubleSpectrum>
{
  @Override
  public DoubleSpectrum loadFile(Path path)
          throws FileNotFoundException, IOException, ReaderException
  {
    return loadStream(Files.newInputStream(path));
  }

  @Override
  public DoubleSpectrum loadStream(InputStream stream)
          throws ReaderException, IOException
  {
    try (InputStream fsi_ = stream)
    {
      DoubleSpectrum spec = new DoubleSpectrum();
      String buff;
      int channels = -1;

      Scanner out = new Scanner(fsi_);

      // Magic should be ::BEGINHEADER::
      buff = out.nextLine();

      if (!buff.equals("::BEGINHEADER::"))
        throw new ReaderException("Bad magic");

      while (fsi_.available() > 0)
      {
        buff = out.nextLine();
        if (buff.equals("::ENDHEADER::"))
        {
          break;
        }

        if (!buff.equals("TITLE="))
        {
          spec.setTitle(buff.substring(6));
        }
        else if (!buff.equals("CHANNELS="))
        {
          //channels=atoi(buff.substr(9).c_str());
          channels = Integer.parseInt(buff.substring(9).toString());
        }
        // all others fields ignored for now.
      }

      if (fsi_.available() == 0)
        throw new ReaderException("End header not found.");

      if (channels == -1)
        throw new ReaderException("Channels attribute not found");

      ArrayList<Double> energies = new ArrayList<>();
      ArrayList<Double> counts = new ArrayList<>();

      while (true)
      {
        float energy = Float.parseFloat(out.nextLine());
        float count = Float.parseFloat(out.nextLine());
        if (fsi_.available() == 0)
        {
          break;
        }

        // first energy value has a bogus count field
        if (!energies.isEmpty())
        {
          counts.add((double) count);
        }

        energies.add((double) energy);
      }

      // just attach the energy map as an attribute
      EnergyScale scale = EnergyScaleFactory.newScale(DoubleArray.toPrimitives(energies));
      spec.setAttribute("energy", scale);
      spec.setGammaData(DoubleArray.toPrimitives(counts));

      return spec;
    }
  }
}
