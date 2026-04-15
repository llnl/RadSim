/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixFactory;
import java.io.Serializable;
import java.util.stream.IntStream;

/**
 *
 * @author nelson85
 */
public class RebinUtilities
{

  // Specialize exception so we can catch it
  static public class RebinException extends Exception implements Serializable
  {

    public RebinException(String s)
    {
      super(s);
    }
  };

  public interface BinEdges
  {
    int size();

    double get(int i);

    /**
     * Checks that the edges are increasing
     *
     * @throws gov.llnl.math.RebinUtilities.RebinException
     */
    void verifyEdges() throws RebinException;

  }

  /**
   * This class is a helper to implement the required frontends.
   */
  static public class ArrayBinEdges implements BinEdges, Serializable
  {

    double[] edges_;

    public ArrayBinEdges(double[] edges)
    {
      edges_ = edges;
    }

    @Override
    public int size()
    {
      return edges_.length;
    }

    @Override
    public double get(int i)
    {
      return edges_[i];
    }

    @Override
    public void verifyEdges() throws RebinException
    {
      for (int i = 0; i < edges_.length - 1; i++)
      {
        if (edges_[i] >= edges_[i + 1])
          throw new RebinException("Edges must monotonically increase");
      }
    }
  }

  static public class ScaledArrayBinEdges implements BinEdges, Serializable
  {

    double[] edges_;
    double scale_;

    public ScaledArrayBinEdges(double[] edges, double scale)
    {
      edges_ = edges;
      scale_ = scale;
    }

    @Override
    public int size()
    {
      return edges_.length;
    }

    @Override
    public double get(int i)
    {
      return edges_[i] * scale_;
    }

    @Override
    public void verifyEdges() throws RebinException
    {
      for (int i = 0; i < edges_.length - 1; i++)
      {
        if (edges_[i] >= edges_[i + 1])
          throw new RebinException("Edges must monotonically increase");
      }
    }
  }

  /**
   * This class is a helper to implement the required frontends.
   */
  static public class StepBinEdges implements BinEdges, Serializable
  {

    double start;
    double step;
    int length;

    public StepBinEdges(double start, double step, int length)
    {
      this.start = start;
      this.step = step;
      this.length = length;
    }

    /**
     * Helper for creating a linear spanning range.
     *
     * @param begin starting point for the linear range.
     * @param end ending point for the linear range (inclusive).
     * @param length is the total number of channels.
     * @return a set of bin edges
     */
    static public StepBinEdges createLinear(double begin, double end, int length)
    {
      return new StepBinEdges(begin, (end - begin) / (length), length);
    }

    @Override
    public int size()
    {
      return this.length + 1;
    }

    @Override
    public double get(int i)
    {
      return this.start + this.step * i;
    }

    @Override
    public void verifyEdges() throws RebinException
    {
      for (int i = 0; i < this.length - 1; i++)
      {
        if (this.get(i) >= this.get(i + 1))
          throw new RebinException("Edges must monotonically increase");
      }
    }

    public double[] toArray()
    {
      return IntStream.range(0, size()).mapToDouble(this::get).toArray();
    }
  }

  // Helper classes for abstracting the input and ouput
  public interface InputWrapper
  {

    void verifyDimensions(int columns) throws RebinException;

    int getColumns();

    int size();

    double get(int r, int c);
  }

  /**
   * Interface for wrapping different types of output targets for rebin. Use
   * either DoubleArrayOutputWrapper or MatrixOutputWrapper.
   */
  public interface OutputWrapper
  {

    void verifyDimensions(int rows, int columns) throws RebinException;

    int size();

    // These 6 methods define the memory handling for the computation
    /**
     * Starts the process of dividing up a bin by transferring a fraction of the
     * input into the remainder. The input should cached into current.
     *
     * @param fraction
     * @param in
     * @param index
     */
    void initialize(double fraction, InputWrapper in, int index);

    /**
     * Transfers the remainder into the accumulator.
     */
    void take();

    /**
     * Transfers a fraction of the current into the accumulator and reduces the
     * remainder by the same amount.
     *
     * @param fraction is how much of current to transfer.
     */
    void take(double fraction);

    /**
     * Takes a whole input bin and adds it to the accumulator.
     *
     * @param in is where to the input from.
     * @param index is the row to take the values from.
     */
    void accumulate(InputWrapper in, int index);

    /**
     * Stores the result of the accumulator in the output and clears the
     * accumulator.
     *
     * @param index is which row to store the accumulator in.
     */
    void apply(int index);
  }

  static public class IntegerArrayInputWrapper implements InputWrapper, Serializable
  {

    int values_[];

    public IntegerArrayInputWrapper(int values[])
    {
      this.values_ = values;
    }

    @Override
    public void verifyDimensions(int rows) throws RebinException
    {
      if (values_.length != rows)
      {
        throw new RebinException("Size mismatch.");
      }
    }

    @Override
    public int getColumns()
    {
      return 1;
    }

    @Override
    public int size()
    {
      return values_.length;
    }

    @Override
    public double get(int r, int c)
    {
      return values_[r];
    }
  }

  static public class DoubleArrayInputWrapper implements InputWrapper, Serializable
  {

    double values_[];

    public DoubleArrayInputWrapper(double values[])
    {
      this.values_ = values;
    }

    @Override
    public void verifyDimensions(int rows) throws RebinException
    {
      if (values_.length != rows)
      {
        throw new RebinException("Size mismatch " + values_.length + "!=" + rows);
      }
    }

    @Override
    public int getColumns()
    {
      return 1;
    }

    @Override
    public int size()
    {
      return values_.length;
    }

    @Override
    public double get(int r, int c)
    {
      if (r < 0)
      {
        System.out.printf(" values_length = %d, r = %d, c = %d \n",
                values_.length, r, c);
      }
      return values_[r];
    }
  }

  static public class DoubleArrayOutputWrapper implements OutputWrapper, Serializable
  {

    double values_[];
    double current = 0;
    double accumulator = 0;
    double remainder = 0;

    public DoubleArrayOutputWrapper(double values[])
    {
      this.values_ = values;
    }

    @Override
    public void verifyDimensions(int rows, int columns) throws RebinException
    {
      if (values_.length != rows || columns != 1)
      {
        throw new RebinException("Size mismatch " + values_.length + " " + rows);
      }
    }

    // These 4 methods define the memory handling for the computation
    @Override
    public void initialize(double fraction, InputWrapper in, int index)
    {
      current = in.get(index, 0);
      remainder = fraction * current;
    }

    @Override
    public void accumulate(InputWrapper in, int index)
    {
      accumulator += in.get(index, 0);
    }

    @Override
    public void take()
    {
      accumulator += remainder;
      remainder = 0;
    }

    @Override
    public void apply(int index)
    {
      values_[index] = accumulator;
      accumulator = 0;
    }

    @Override
    public int size()
    {
      return values_.length;
    }

    @Override
    public void take(double fraction)
    {
      double c = fraction * current;
      remainder -= c;
      accumulator += c;
    }
  }

  static public class MatrixInputWrapper implements InputWrapper, Serializable
  {

    Matrix values_;

    public MatrixInputWrapper(Matrix values)
    {
      this.values_ = values;
    }

    @Override
    public void verifyDimensions(int rows) throws RebinException
    {
      if (values_.rows() != rows)
      {
        throw new RebinException("Size mismatch (" + values_.rows() + "!=" + rows + ")");
      }
    }

    @Override
    public int getColumns()
    {
      return values_.columns();
    }

    @Override
    public int size()
    {
      return values_.rows();
    }

    @Override
    public double get(int r, int c)
    {
      return values_.get(r, c);
    }
  }

  static public class MatrixOutputWrapper implements OutputWrapper, Serializable
  {

    Matrix values_;
    double current[] = null;
    double accumulator[] = null;
    double remainder[] = null;

    public MatrixOutputWrapper(Matrix values)
    {
      this.values_ = values;
    }

    @Override
    public void verifyDimensions(int rows, int columns) throws RebinException
    {
      try
      {
        values_.resize(rows, columns);
      }
      catch (MathExceptions.ResizeException ex)
      {
        throw new RuntimeException(ex);
      }

      // Allocate space for results
      current = new double[values_.columns()];
      accumulator = new double[values_.columns()];
      remainder = new double[values_.columns()];
    }

    // These 4 methods define the memory handling for the computation
    @Override
    public void initialize(double fraction, InputWrapper in, int index)
    {
      for (int i = 0; i < values_.columns(); ++i)
      {
        current[i] = in.get(index, i);
        remainder[i] = fraction * current[i];
      }
    }

    @Override
    public void accumulate(InputWrapper in, int index)
    {
      for (int i = 0; i < values_.columns(); ++i)
      {
        accumulator[i] += in.get(index, i);
      }
    }

    @Override
    public void take()
    {
      for (int i = 0; i < values_.columns(); ++i)
      {
        accumulator[i] += remainder[i];
        remainder[i] = 0;
      }
    }

    @Override
    public void apply(int index)
    {
      for (int i = 0; i < values_.columns(); ++i)
      {
        values_.set(index, i, accumulator[i]);
        accumulator[i] = 0;
      }
    }

    @Override
    public int size()
    {
      return values_.rows();
    }

    @Override
    public void take(double fraction)
    {
      for (int i = 0; i < values_.columns(); ++i)
      {
        double c = fraction * current[i];
        remainder[i] -= c;
        accumulator[i] += c;
      }
    }
  }

  /**
   * This is the workhorse for rebinning. To use a wrapper class handles the
   * specifics of each type such as Matrix or double[]. BinEdges is a wrapper to
   * define a if the bin structure is stored in an array or computed on the fly.
   *
   * @param output
   * @param in
   * @param inputBins
   * @param outputBins
   * @throws gov.llnl.math.RebinUtilities.RebinException
   */
  static public void execute(OutputWrapper output, InputWrapper in,
          BinEdges inputBins, BinEdges outputBins)
          throws RebinException
  {
    // First we must assert the dimensions of the process are correct.
    in.verifyDimensions(inputBins.size() - 1);
    output.verifyDimensions(outputBins.size() - 1, in.getColumns());

    // verify the bins
    inputBins.verifyEdges();
    outputBins.verifyEdges();

    int inChannels = in.size();
    int outChannels = output.size();

    // Compute where the bins need to be by reading through
    // the list of energies and interpolation where the edge of
    // output energies fall in the input energies.
    int i1 = 0; // counter for in_energies
    int i0;   // counter for out_energies
    int n;    // the starting channel in the input

    // With the bin edges, we will walk through the input channels
    // and copy the fraction that corresponds to output.
    double bins0;
    double bins1 = 0;

    for (i0 = 0; i0 <= outChannels; ++i0)
    {
      bins0 = bins1;

      // advance until we find the corresponding point in the input energies
      while ((inputBins.get(i1) < outputBins.get(i0)) && i1 < inChannels)
      {
        i1++;
      }

      // if the input energies have not yet start the those output bins
      // map to the start of the list.
      if (i1 == 0)
      {
        bins1 = (double) (i1);
      } // else linearly interpolate
      else
      {
        double temp_div = (outputBins.get(i0) - inputBins.get(i1 - 1))
                / (inputBins.get(i1) - inputBins.get(i1 - 1));
        double temp_add = (double) i1 - 1.0;
        temp_div = temp_div + temp_add;
        bins1 = temp_div;
        if (bins1 < 0.0)
        {
          System.out.printf(" input, output Bin lengths = %d %d \n",
                  inputBins.size(), outputBins.size());
          System.out.printf(" curious numbers are %f %f %f \n",
                  outputBins.get(i0), inputBins.get(i1), inputBins.get(i1 - 1));
          System.out.printf(" i0, i1, bins1 = %d %d %f \n", i0, i1, bins1);
        }
      }

      // We need two bin edges to work, so just store the first
      if (i0 == 0)
      {
        n = (int) (bins1);

        output.initialize(((double) n + 1.0 - bins1), in, n);
        continue;
      }

      if (bins1 > i1)
      {
        bins1 = i1;
      }

      //     System.out.println(i0+" " +i1+" " +bins0 + " "+bins1);
      // if output channel is a fraction of an input bin.
      if ((int) (bins0) == (int) (bins1))
      {
        n = (int) (bins0);
        if (n == inChannels)
        {
          break;
        }
        output.take(bins1 - bins0);
      } // else take the remainder of the current bin, all the
      // bins inbetween and then the fraction of the ending bin.
      else
      {
        n = (int) (bins0);
        // remainder of current
        output.take();

        // all bins in between
        for (int i3 = n + 1; i3 < (int) (bins1); ++i3)
        {
          output.accumulate(in, i3);
        }

        // fraction of end
        n = (int) (bins1);

        if (n < inChannels)
        {
          output.initialize(1.0, in, n);
          output.take(bins1 - (double) n);
        }
      }

      // Assign the value
      output.apply(i0 - 1);
    }

    // zero the unassigned outputs
    for (i0--; i0 < outChannels; ++i0)
    {
      output.apply(i0);
    }
  }

  // Frontends for common tasks.
/**
   * Rebins a double array from one set of bin edges to another.
   * This method performs fractional binning to preserve the total integral of the input.
   *
   * @param input the data values associated with each input bin.
   * @param inputBins the edges defining the input bins (length must be input.length + 1).
   * @param outputBins the edges defining the desired output bins.
   * @return a new double array containing the rebinned data.
   * @throws RebinException if bin edges are not monotonic or dimensions mismatch.
   */
  static public double[] rebin(double input[], double inputBins[], double outputBins[]) throws RebinException
  {
    double out[] = new double[outputBins.length - 1];
    execute(new DoubleArrayOutputWrapper(out),
            new DoubleArrayInputWrapper(input),
            new ArrayBinEdges(inputBins),
            new ArrayBinEdges(outputBins));
    return out;
  }

  /**
   * Scales the x-axis of the input data.
   * This effectively stretches or compresses the spectrum by mapping the input 
   * range [0, value] onto the normalized range [0, 1].
   *
   * @param input the input data array.
   * @param value the maximum value of the original scale to be mapped to 1.
   * @return a scaled double array of the same length as the input.
   * @throws RebinException if the operation fails due to invalid dimensions or scale.
   */
  static public double[] scale(double input[], double value) throws RebinException
  {
    int channels = input.length;
    double out[] = new double[channels];
    execute(new DoubleArrayOutputWrapper(out),
            new DoubleArrayInputWrapper(input),
            StepBinEdges.createLinear(0, value, channels),
            StepBinEdges.createLinear(0, 1, channels));
    return out;
  }

  /**
   * Resamples the input data to a different number of channels.
   * Useful for upsampling or downsampling a spectrum while maintaining the same 
   * relative range [0, 1].
   *
   * @param input the input data array.
   * @param channels the desired number of bins in the output array.
   * @return a new double array with the specified number of channels.
   * @throws RebinException if dimensions are inconsistent.
   */
  static public double[] rescale(double input[], int channels) throws RebinException
  {
    double out[] = new double[channels];
    execute(new DoubleArrayOutputWrapper(out),
            new DoubleArrayInputWrapper(input),
            StepBinEdges.createLinear(0, 1, input.length ),
            StepBinEdges.createLinear(0, 1, channels));
    return out;
  }

  /**
   * Shift a histogram forward or back.
   *
   * @param input the input data array.
   * @param channels the number of channels to shift. Positive values shift toward 
   * higher indices; negative values shift toward lower indices.
   * @return a shifted double array of the same length as the input.
   * @throws RebinException if dimensions mismatch or calculation fails.
   */
  static public double[] shift(double[] input, double channels) throws RebinException
  {
    int n = input.length;
    double out[] = new double[n];
    execute(new DoubleArrayOutputWrapper(out),
            new DoubleArrayInputWrapper(input),
            StepBinEdges.createLinear(0, n, n),
            StepBinEdges.createLinear(0 - channels, n - channels, n));
    return out;
  }

  /**
   * Rebins a Matrix where each column is treated as a separate dataset.
   * Preserves the total sum of each column across the new binning structure.
   *
   * @param input the input Matrix.
   * @param inputBins the edges defining the input rows.
   * @param outputBins the edges defining the output rows.
   * @return a new Matrix with (outputBins.length - 1) rows and the same number of columns.
   * @throws RebinException if bin edges are invalid.
   */
  static public Matrix rebin(Matrix input, double inputBins[], double outputBins[]) throws RebinException
  {
    Matrix out = MatrixFactory.newColumnMatrix(outputBins.length - 1, input.columns());
    execute(new MatrixOutputWrapper(out),
            new MatrixInputWrapper(input),
            new ArrayBinEdges(inputBins),
            new ArrayBinEdges(outputBins));
    return out;
  }

  /**
   * Rescales a Matrix to a new number of rows (channels).
   * Operates column-wise to adjust the resolution of the data.
   *
   * @param input the input Matrix.
   * @param channels the desired number of rows in the output Matrix.
   * @return a new Matrix with the specified number of rows.
   * @throws RebinException if dimensions are inconsistent.
   */
  static public Matrix rescale(Matrix input, int channels) throws RebinException
  {
    Matrix out = MatrixFactory.newColumnMatrix(channels, input.columns());
    execute(new MatrixOutputWrapper(out),
            new MatrixInputWrapper(input),
            StepBinEdges.createLinear(0, 1, input.rows()),
            StepBinEdges.createLinear(0, 1, channels));
    return out;
  }

  /**
   * Aggregates input integer data into coarser bins based on specific edge indices.
   * Unlike {@code rebin}, this performs simple summation over discrete channel ranges.
   *
   * @param data the input integer array.
   * @param channelEdges indices in the data array that define the start/end of new bins.
   * @return an integer array where each element is the sum of the specified range.
   */
  static public int[] collect(int[] data, int[] channelEdges)
  {
    int[] out = new int[channelEdges.length - 1];
    for (int i = 0; i < channelEdges.length - 1; ++i)
    {
      for (int j = channelEdges[i]; j < channelEdges[i + 1]; ++j)
      {
        out[i] += data[j];
      }
    }
    return out;
  }

  /**
   * Aggregates input double data into coarser bins based on specific edge indices.
   *
   * @param data the input double array.
   * @param channelEdges indices in the data array that define the start/end of new bins.
   * @return a double array where each element is the sum of the specified range.
   */
  static public double[] collect(double[] data, int[] channelEdges)
  {
    double[] out = new double[channelEdges.length - 1];
    for (int i = 0; i < channelEdges.length - 1; ++i)
    {
      for (int j = channelEdges[i]; j < channelEdges[i + 1]; ++j)
      {
        out[i] += data[j];
      }
    }
    return out;
  }

  /**
   * Aggregates Matrix rows into coarser bins based on specific edge indices.
   * Sums the values for each column within the specified row ranges.
   *
   * @param data the input Matrix.
   * @param channelEdges row indices defining the boundaries for collection.
   * @return a new Matrix with rows reduced according to channelEdges.
   */
  static public Matrix collect(Matrix data, int[] channelEdges)
  {
    Matrix out = MatrixFactory.newColumnMatrix(channelEdges.length - 1, data.columns());
    for (int k = 0; k < data.columns(); ++k)
    {
      for (int i = 0; i < channelEdges.length - 1; ++i)
      {
        double tmp = 0;
        for (int j = channelEdges[i]; j < channelEdges[i + 1]; ++j)
        {
          tmp += data.get(j, k);
        }
        out.set(i, k, tmp);
      }
    }
    return out;
  }

}
