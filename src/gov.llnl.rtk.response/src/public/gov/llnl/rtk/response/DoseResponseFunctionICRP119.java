// --- file: gov/llnl/rtk/response/DoseResponseFunctionICRP119.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.interp.MultiInterpolator;
import gov.llnl.rtk.response.support.CsvReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author nelson85
 */
public class DoseResponseFunctionICRP119 implements DoseResponseFunction
{

  private static final DoseResponseFunctionICRP119 instance = new DoseResponseFunctionICRP119();

  // FIXME Exposed for testing.   Make provide once testing is complete.
  public final double[] e;
  public final double[] df;
  public final double[] ap;
  public final double[] pa;
  public final double[] rlat;
  public final double[] llat;
  public final double[] rot;
  public final double[] iso;
  public final MultiInterpolator tables;

  /**
   * Get the ICRP119 Dose tables.
   *
   * This is a singleton, thus only one copy gets loaded.
   *
   * @return
   */
  static public DoseResponseFunctionICRP119 getInstance()
  {
    return instance;
  }

  /**
   * Constructor for singleton.
   */
  private DoseResponseFunctionICRP119()
  {
    URL resource = DoseResponseFunctionICRP119.class.getClassLoader().
            getResource("gov/llnl/rtk/response/resources/ICRP119.tsv");
    try ( InputStream is = resource.openStream())
    {
      CsvReader reader = new CsvReader();
      reader.setSeparator("\t");
      HashMap<String, Object> table = reader.readStream(is);
      this.e = (double[]) table.get("E");
      this.df = (double[]) table.get("DF");
      this.ap = (double[]) table.get("AP");
      this.pa = (double[]) table.get("PA");
      this.rlat = (double[]) table.get("RLAT");
      this.llat = (double[]) table.get("LLAT");
      this.rot = (double[]) table.get("ROT");
      this.iso = (double[]) table.get("ISO");
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
    this.tables = MultiInterpolator.createLogLog(e, df, iso, ap, pa, rlat, llat);
  }

  @Override
  public String getModel()
  {
    return "119 - Compendium of Dose Coefficients based on ICRP Publication 60";
  }

  @Override
  public String getVendor()
  {
    return "ICRP";
  }

  public enum View
  {
    ISO,
    AP,
    PA,
    RLAT,
    LLAT
  }

  @Override
  public DoseEvaluatorICRP119 newEvaluator()
  {
    return new DoseEvaluatorICRP119(this);
  }

}
