// --- file: gov/llnl/rtk/io/SpectraSpeOutputFile.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.utility.io.DataFileWriter;
import gov.llnl.utility.io.WriterException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author nelson85
 */
public class SpectraSpeOutputFile implements DataFileWriter<IntegerSpectrum>
{
  @Override
  public void saveFile(Path path, IntegerSpectrum object) throws IOException, WriterException
  {
    saveWriter(Files.newBufferedWriter(path, UTF_8), object);
  }

  @Override
  public void saveStream(OutputStream stream, IntegerSpectrum object) throws IOException, WriterException
  {
    saveWriter(new BufferedWriter(new OutputStreamWriter(stream, UTF_8)), object);
  }

  public void saveWriter(BufferedWriter writer, IntegerSpectrum spec) throws IOException
  {
    try (PrintWriter fso_ = new PrintWriter(writer))
    {
      SPESpectrum data = new SPESpectrum();
      SPESpectrum.convert(spec, data);

      fso_.println("$SPEC_ID:");
      fso_.println(data.title_);
      /*
     if (!data.comments_.isEmpty())
     {
     fso_.println("$SPEC_REM:");
     for (int i = 0; i < data.comments_.size(); ++i)
     {
     fso_.println(data.comments_.get(i));
     }
     }
       */
      fso_.println("$MEAS_TIM:");
      fso_.print((float) data.live_time_);
      fso_.print(" ");
      fso_.print((float) data.real_time_);
      fso_.println();

      fso_.println("$DATE_MEA");
      DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      fso_.printf(df.format(data.timestamp_));
      fso_.println();

      fso_.println("$DATA:");
      fso_.print(0); // why not print "1"?
      fso_.print(" ");
      fso_.print(data.gamma_counts_.length);
      fso_.println();

      for (int i = 0; i != data.gamma_counts_.length; ++i)
      {
        // System.out.printf(" write out spec: i = %d, count = %f \n", i, data.gamma_counts_[i]);
        fso_.println((int) data.gamma_counts_[i]);
      }

      /*
     if (!data.roi_.isEmpty())
     {
     fso_.println("$ROI:");
     fso_.println(data.roi_.size());

     for (int i = 0; i < data.roi_.size(); ++i)
     {
     fso_.print(data.roi_.get(i).begin);
     fso_.print(" ");
     fso_.println(data.roi_.get(i).end);
     }
     }
       */
      if (data.energy_calibration_ != null)
      {
//      double a = 0, b = 0, c = 0;
//      if (data.energy_calibration_.size() > 1)
//      {
//        a = data.energy_calibration_.get(0);
//        b = data.energy_calibration_.get(1);
//        c = data.energy_calibration_.get(2);
//      }

//      fso_.println("$MCA_CAL:");
//      fso_.println(data.energy_calibration_.size());
//
//      for (int i = 0; i < data.energy_calibration_.size(); ++i)
//      {
//        fso_.print(Float.toString(data.energy_calibration_.get(i)));
//        fso_.print(" ");
//      }
//      fso_.println();
        fso_.println("$ENER_FIT:");
        for (int i = 0; i < data.energy_calibration_.length; ++i)
        {
          fso_.print(data.energy_calibration_[i] + " ");
        }
//      fso_.print((float) a);
//      fso_.print(' ');
//      fso_.print((float) b);
//      fso_.print(' ');
//      fso_.print((float) c);
        fso_.println();

      }
    }
  }
}
