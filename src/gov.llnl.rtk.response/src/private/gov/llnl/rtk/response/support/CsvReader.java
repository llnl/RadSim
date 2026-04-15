// --- file: private/gov/llnl/rtk/response/support/CsvReader.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reader for tab separated value files.
 *
 * This is used to load the IRCP-119 table for use in the interpolator. This
 * code is all specific to Java and will be replaced in the C# version.
 *
 * @author nelson85
 */
public class CsvReader
{
  private String separator = ",";
  HashMap<String, ColumnHandler> columnHandler = new HashMap<>();
  ColumnHandler defaultHandler = ColumnType.DOUBLE;

  public enum ColumnType implements ColumnHandler
  {
    DOUBLE(new DoubleColumn()),
    STRING(new StringColumn());
    private final ColumnHandler handler;

    ColumnType(ColumnHandler handler)
    {
      this.handler = handler;
    }

    @Override
    public Object generate(int size)
    {
      return handler.generate(size);
    }

    @Override
    public void set(Object obj, int index, String value)
    {
      handler.set(obj, index, value);
    }

  }

  static public interface ColumnHandler
  {

    Object generate(int size);

    void set(Object obj, int index, String value);
  }

  public void setDefaultHandler(ColumnHandler handler)
  {
    this.defaultHandler = handler;
  }

  public void setHandler(String name, ColumnHandler column)
  {
    this.columnHandler.put(name, column);
  }

  /**
   * Convert an input stream into a HashMap.
   *
   * Each column is one entry in the resulting hashmap.
   *
   * @param stream is the stream to be converted.
   * @return a new HashMap.
   * @throws IOException
   */
  public HashMap<String, Object> readStream(InputStream stream) throws IOException
  {
    BufferedReader ir = new BufferedReader(new InputStreamReader(stream));
    String header = ir.readLine();
    List<String> lines = new ArrayList<>();
    while (true)
    {
      String line = ir.readLine();
      if (line == null)
        break;
      lines.add(line);
    }

    String[] columns = header.split(separator);
    ColumnHandler[] handlers = Stream.of(columns)
            .map(p -> this.columnHandler.getOrDefault(p, this.defaultHandler))
            .toArray(ColumnHandler[]::new);
    int n = lines.size();
    Object[] values = Stream.of(handlers).map(p -> p.generate(n)).toArray();
    HashMap<String, Object> table = new HashMap<>();
    for (int i = 0; i < lines.size(); ++i)
    {
      String[] u = lines.get(i).split(separator);
      for (int j = 0; j < columns.length; ++j)
      {
        handlers[j].set(values[j], i, u[j]);
      }
    }
    for (int j = 0; j < columns.length; ++j)
    {
      table.put(columns[j], values[j]);
    }
    return table;
  }

  private static class StringColumn implements ColumnHandler
  {

    @Override
    public Object generate(int size)
    {
      return new String[size];
    }

    @Override
    public void set(Object obj, int index, String value)
    {
      ((String[]) obj)[index] = value;
    }

  }

  private static class DoubleColumn implements ColumnHandler
  {

    @Override
    public Object generate(int size)
    {
      return new double[size];
    }

    @Override
    public void set(Object obj, int index, String value)
    {
      ((double[]) obj)[index] = Double.parseDouble(value);
    }

  }

  /**
   * @return the separator
   */
  public String getSeparator()
  {
    return separator;
  }

  /**
   * @param separator the separator to set
   */
  public void setSeparator(String separator)
  {
    this.separator = separator;
  }

}
