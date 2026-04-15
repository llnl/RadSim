// --- file: gov/llnl/rtk/filter/EnergyResolutionSpectralFilter.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.filter;

import gov.llnl.math.MathExceptions;
import static gov.llnl.math.SpecialFunctions.erf;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixColumnTable;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.annotation.Debug;
import gov.llnl.utility.xml.bind.Reader;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "energyResolutionSpectralFilter")
public class EnergyResolutionSpectralFilter implements SpectralFilter, Serializable
{
  double param = 0.05;
  ArrayList<Filter> filters = new ArrayList<>();
  private int channels;

  public EnergyResolutionSpectralFilter()
  {
  }

  public EnergyResolutionSpectralFilter(double param)
  {
    this.param = param;
  }

  @Reader.TextContents
  public void setFilterCoef(double param)
  {
    this.param = param;
  }

  public double getFilterCoef()
  {
    return param;
  }

  @Override
  public Matrix apply(Matrix in) throws MathExceptions.SizeException
  {
    if (filters.size() < in.rows())
      rebuildFilters(in.rows());

    Matrix.ColumnAccess out = new MatrixColumnTable(in.rows(), in.columns());
    for (int j = 0; j < in.columns(); ++j)
    {
      for (int i = 0; i < in.rows(); ++i)
      {
        filters.get(i).apply(out.accessColumn(i),
                out.addressColumn(j),
                in.rows(), in.get(i, j));
      }
    }
    return out;
  }

  @Override
  public double[] apply(double[] in) throws MathExceptions.SizeException
  {
    if (filters.size() < in.length)
      rebuildFilters(in.length);

    double[] out = new double[in.length];
    for (int i = 0; i < in.length; ++i)
    {
      filters.get(i).apply(out, 0, in.length, in[i]);
    }
    return out;
  }

  @Override
  public DoubleSpectrum apply(Spectrum<?> spectrum)
  {
    int minc = spectrum.getMinimumValidChannel();
    int maxc = spectrum.getMaximumValidChannel();
    double[] in = spectrum.toDoubles();

    // Rebuild the filters
    if (filters.size() < in.length)
      rebuildFilters(in.length);

    double[] out = new double[in.length];
    for (int i = minc; i < maxc; ++i)
    {
      filters.get(i).apply(out, 0, in.length, in[i]);
    }
    DoubleSpectrum outSpectrum = new DoubleSpectrum(spectrum);
    outSpectrum.setGammaData(out);
    return outSpectrum;
  }

  private void rebuildFilters(int channels)
  {
    this.channels = channels;
    filters.clear();
    filters.ensureCapacity(channels);
    for (int i = 0; i < channels; ++i)
    {
      filters.add(new Filter(param, i));
    }
  }

//<editor-fold desc="internal">
  public class Filter implements Serializable
  {

    Filter(double param, int center)
    {
      double sigma = Math.sqrt(2 * param * (center + 1));
      double delta = 3 * sigma;
      start = (int) Math.floor(center - delta);
      end = (int) Math.ceil(center + delta);
      if (start < 0)
        start = 0;
      if (end > channels)
        end = channels;
      values = new double[end - start];

      // Compute the fraction that is overhanging
      double q1 = 0.5 * (1 + erf((start - center + 0.5) / sigma));
      double q2 = 1 - 0.5 * (1 + erf((end - center + 0.5) / sigma));
      double f = 1 / (1 - q1 - q2);

      for (int i = 0; i < end - start; i++)
      {
        q2 = 0.5 * (1 + erf((start + i - center + 0.5) / sigma));
        values[i] = f * (q2 - q1);
        q1 = q2;
      }
    }

    int start;
    int end;
    double[] values;

    public void apply(double[] out, int index1, int length, double scale)
    {
      for (int i = 0; i < values.length; ++i)
      {
        if (i + start < 0)
          continue;
        if (i + start >= length)
          return;
        out[index1 + i + start] += scale * values[i];
      }
    }
  }
//</editor-fold>
}
