// --- file: gov/llnl/rtk/io/Gr135Spectrum.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

//  private Object Gr135packTemperature(byte[] temp)
import gov.llnl.rtk.data.IntegerSpectrum;
import static gov.llnl.rtk.data.SpectrumAttributes.ENERGY_POLYNOMIAL;
import static gov.llnl.rtk.data.SpectrumAttributes.NEUTRONS;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

//  {
@Internal
class Gr135Spectrum
{
  public byte[] synchro = new byte[4];
  public byte[] instrument_type = new byte[4];
  public byte record_type; // 'A' 'S' 'Z'
  public int sequence;
  public byte year;
  public byte month;
  public byte day;
  public byte hour;
  public byte min;
  public byte sec;
  public short nch;
  public int real_time_ms; //Clock_time
  public short gain;
  public short peak;
  public short FW;
  public byte dose_unit;
  public int geiger;
  public short neutron;
  public short pileup;
  public short serial;
  public short version;
  public float[] ecal = new float[3];
  public byte[] temp = new byte[3];
  public byte[] spare = new byte[16];
  public int live_time_ms;
  public int[] spec = new int[1024];
  public byte checksum;

  public static void convert(Gr135Spectrum data, IntegerSpectrum spec)
  {
    try
    {
      spec.setLiveTime((float) ((data.live_time_ms) * 0.001));
      spec.setRealTime((float) ((data.real_time_ms) * 0.001));
      int[] v = new int[data.nch];
      for (int i = 2; i < data.nch; i++)
      {
        v[i] = data.spec[i - 2];
      }
      v[0] = 0;
      v[1] = 1;
      v[data.nch - 1] = 0;
      spec.assign(v);
      double[] energyPoly = new double[3];
      energyPoly[0] = data.ecal[0];
      energyPoly[1] = data.ecal[1];
      energyPoly[2] = data.ecal[2];
      DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      String timeString = String.format("%4d-%02d-%02d %02d:%02d:%02d",
              2000 + data.year, data.month, data.day, data.hour, data.min, data.sec);
      spec.setStartTime(df.parse(timeString).toInstant());
      spec.setAttribute(NEUTRONS, (double)(data.neutron));
      spec.setAttribute(ENERGY_POLYNOMIAL, energyPoly);
      spec.setAttribute("gr135.serial", data.serial);
      spec.setAttribute("gr135.version", data.version);
      spec.setAttribute("gr135.gain", data.gain);
      spec.setAttribute("gr135.peak", data.peak);
      spec.setAttribute("gr135.FW", data.FW);
      spec.setAttribute("gr135.temperature", Gr135packTemperature(data.temp));
    }
    catch (ParseException ex)
    {
      throw new RuntimeException(ex);
    }
  }

//  static int Gr135packTemperature(byte temp[])
//  {
//    int i = 0;
//    i = temp[0];
//    i <<= 8;
//    i |= temp[1];
//    i <<= 8;
//    i |= temp[2];
//    return i;
//  }
//
//  static void Gr135unpackTemperature(byte temp[], int i)
//  {
//    temp[0] = (byte) ((i & 0xFF0000) >> 16);
//    temp[1] = (byte) ((i & 0xFF00) >> 8);
//    temp[2] = (byte) (i & 0xff);
//  }
  public static int convert(IntegerSpectrum spec, Gr135Spectrum data)
          throws WriterException
  {
    data.live_time_ms = (int) (1000 * spec.getLiveTime());
    data.real_time_ms = (int) (1000 * spec.getRealTime());
    data.nch = (short) spec.size();
    if (data.nch > 1024)
      throw new WriterException("Size of channels exceeded");
    int[] v = spec.toArray();
    for (int i = 2; i < data.nch; i++)
    {
      if (v[i] < 0)
      {
        data.spec[i - 2] = 0;
      }
      else if (v[i] > 0x10000)
      {
        data.spec[i - 2] = 0xffff;
      }
      else
      {
        data.spec[i - 2] = (int) (v[i]);
      }
    }
    double[] energyPoly = spec.getAttribute("energy_poly", double[].class);
    // FIXME check length/existance
    data.ecal[0] = (float) energyPoly[0];
    data.ecal[1] = (float) energyPoly[1];
    data.ecal[2] = (float) energyPoly[2];
    data.neutron = (short) ((int) (Integer) spec.getAttribute("neutrons"));
    Instant tm = spec.getStartTime();
    // FIXME convert to Java 8
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(Date.from(tm));
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH) + 1;
    int year = calendar.get(Calendar.YEAR);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int seconds = calendar.get(Calendar.SECOND); //, centseconds=tm.millisecond/10;
    data.year = dec2bcd((byte) year);
    data.month = dec2bcd((byte) month);
    data.day = dec2bcd((byte) day);
    data.hour = dec2bcd((byte) hour);// FIX THIS: somewhere an hour is added...
    data.min = dec2bcd((byte) minute);
    data.sec = dec2bcd((byte) seconds);
    data.synchro = "ZZZZ".getBytes();
    data.instrument_type = "1350".getBytes();
    data.record_type = 'A';
    data.sequence = (int) 1;
    data.gain = (short) (int) (Integer) spec.getAttribute("gr135.gain");
    data.peak = (short) (int) (Integer) spec.getAttribute("gr135.peak");
    data.FW = (short) (int) (Integer) spec.getAttribute("gr135.FW");
    data.dose_unit = (int) 'R';
    data.geiger = (int) 0;
    data.pileup = (int) 0;
    data.serial = (short) (int) (Integer) spec.getAttribute("gr135.serial");
    data.version = (short) (int) (Integer) spec.getAttribute("gr135.version");
    Gr135unpackTemperature(data.temp, (Integer) spec.getAttribute("gr135.temperature"));
    for (int k = 0; k < 0; k++)
    {
      data.spare[k] = 0;
    }
    data.checksum = (char) 0;
    return 0;
  }

  public Gr135Spectrum()
  {
  }

  static public class Gr135CheckSum
  {
    public Gr135CheckSum()
    {
      sum = 0;
    }

    public void proc(byte[] b, int sz)
    {
      for (int i = 0; i < sz; i++)
      {
        sum ^= b[i];
      }
    }
    public int sum;
  };

  public static byte dec2bcd(byte dec)
  {
    int bcd = (((((int) (dec % 100) / 10) << 4) & 0xf0) | (int) (dec % 10));
    return (byte) bcd;
  }

  public static byte bcd2dec(byte r)
  {
    int r1 = r & 0xf;
    int r2 = (r >> 4) & 0xf;
    return (byte) (r1 + r2 * 10);
  }

  public static int Gr135packTemperature(byte[] temperature)
  {
    int i;
    i = 0;
    i = temperature[0];
    i <<= 8;
    i |= temperature[1];
    i <<= 8;
    i |= temperature[2];
    return i;
  }

  public static void Gr135unpackTemperature(byte[] temperature, int value)
  {
    temperature[0] = (byte) ((value & 0xFF0000) >> 16);
    temperature[1] = (byte) ((value & 0xFF00) >> 8);
    temperature[2] = (byte) (value & 0xff);;
  }
}
