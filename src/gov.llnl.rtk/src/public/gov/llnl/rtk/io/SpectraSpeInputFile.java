// --- file: gov/llnl/rtk/io/SpectraSpeInputFile.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.io.SPESpectrum.RegionOfInterest;
import static gov.llnl.rtk.io.SPESpectrum.convert;
import gov.llnl.utility.ArrayEncoding;
import gov.llnl.utility.io.DataFileReader;
import gov.llnl.utility.io.ReaderException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author yao2
 */
public class SpectraSpeInputFile implements DataFileReader<IntegerSpectrum>
{
  @Override
  public IntegerSpectrum loadFile(Path path) throws FileNotFoundException, IOException, ReaderException
  {
    return loadStream(Files.newInputStream(path));
  }

  @Override
  public IntegerSpectrum loadStream(InputStream stream) throws IOException, ReaderException
  {
    try (InputStream fsi_ = stream)
    {
      SPESpectrum data = new SPESpectrum();
      String buff = "";
      Scanner scanner = new Scanner(fsi_, UTF_8.name());

      if (fsi_.available() == 0)
        return null;

      handle_line:
      // $Tag:\r\n[Data...]
      // while (fsi_.available() > 0)
      while (scanner.hasNext())
      {
        // Read to the next command line
        while (scanner.hasNextLine() && !buff.startsWith("$"))
        {
          buff = scanner.nextLine();
        }

        // Process the command
        String cmd = buff.trim();
        buff = "";

        // Handle each valid command
        if (cmd.equals("$SPEC_ID:"))
        {
          buff = scanner.nextLine();
          data.title_ = buff.trim();
          continue;
        }

        if (cmd.equals("$SPEC_REM:"))
        {
          while (fsi_.available() > 0)
          {
            buff = scanner.nextLine();
            if (buff.startsWith(Character.toString('$')))
            {
              data.comments_.add(buff.trim());
              continue handle_line;
            }
          }
        }

        if (cmd.equals("$DATE_MEA:"))
        {
          try
          {
            buff = scanner.nextLine();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Instant u = df.parse(buff).toInstant();
            data.timestamp_ = u;
            continue;
          }
          catch (ParseException ex)
          {
            throw new RuntimeException(ex);
          }
        }

        if (cmd.equals("$MEAS_TIM:"))
        {
          String timeLine = scanner.nextLine();
          String[] parts = timeLine.split(" ");

          data.live_time_ = Double.parseDouble(parts[0]);
          data.real_time_ = Double.parseDouble(parts[1]);
          continue;
        }

        if (cmd.equals("$DATA:"))
        {
          String countLine = scanner.nextLine();
          String[] parts = countLine.split(" ");

          int start = Integer.parseInt(parts[0]);
          int num_channels = Integer.parseInt(parts[1]);

          num_channels = num_channels - start + 1;
          data.gamma_counts_ = new int[num_channels];

          for (int i = 0; i < num_channels; ++i)
            data.gamma_counts_[i] = Integer.parseInt(scanner.nextLine());
          continue;
        }

        if (cmd.equals("$ROI"))
        {
          int num = Integer.parseInt(scanner.nextLine());
          data.roi_ = new ArrayList<>();
          for (int i = 0; i < num; ++i)
          {
            int b = Integer.parseInt(scanner.nextLine());
            int e = Integer.parseInt(scanner.nextLine());

            data.roi_.add(new RegionOfInterest(b, e));
          }
          continue;
        }

        if (cmd.equals("$ENER_FIT:") || cmd.equals("$ENEG_FIT:"))
        {
          buff = scanner.nextLine();
          data.energy_calibration_ = ArrayEncoding.parseDoubles(buff);
          continue;
        }

        if (cmd.equals("$CAMBIO:"))
        {
          while (scanner.hasNextLine())
          {
            buff = scanner.nextLine();
            if (buff.startsWith("$"))
              continue handle_line;
            //        System.out.println("CAMBIO " + buff);
          }
        }

        if (cmd.equals("$MCA_CAL:"))
        {
          buff = scanner.nextLine();
          int size = Integer.parseInt(buff);
          buff = scanner.nextLine();
          data.energy_calibration_ = ArrayEncoding.parseDoubles(buff);
          continue;
        }

        if (buff.equals("$SHAPE_CAL:"))
        {
          int factors = Integer.parseInt(scanner.nextLine());
          if (data.shape_calibration_ == null && data.shape_calibration_.length < factors)
          {
            data.shape_calibration_ = new double[factors];
          }
          for (int i = 0; i != factors; i++)
          {
            data.shape_calibration_[i] = Float.parseFloat(scanner.nextLine());
          }
          scanner.nextLine();
        }

        if (buff.equals("$PRESETS:"))
        {           // ignore
          while (fsi_.available() > 0)
          {
            buff = scanner.nextLine();
            if (buff.startsWith(Character.toString('$')))
            {
              continue handle_line;
            }
          }
        }
      }

      IntegerSpectrum spec = new IntegerSpectrum();
      convert(data, spec);
      return spec;
    }
  }
}
