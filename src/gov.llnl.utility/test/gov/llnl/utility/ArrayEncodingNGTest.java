/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.text.ParseException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ArrayEncoding.
 */
public class ArrayEncodingNGTest
{

  public ArrayEncodingNGTest()
  {
  }

  /**
   * Test of constructor, of class ArrayEncoding.
   */
  @Test
  public void testConstructor()
  {
    // Class has default constructor, test is for coverage
    ArrayEncoding ae = new ArrayEncoding();
  }

  /**
   * Test of encodeShorts method, of class ArrayEncoding.
   */
  @Test
  public void testEncodeShorts()
  {
    // A null produces an empty string.
    short[] values = null;
    String expResult = "";
    String result = ArrayEncoding.encodeShorts(values);
    assertEquals(result, expResult);

    values = new short[]
    {
      1443, -11770, 29290, -12712, 20854, -30718, 1183
    };
    expResult = "eJyLDrZmYGBgZ118ia0o61xEYFkHE8t8AD2ABmc=";
    result = ArrayEncoding.encodeShorts(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of encodeIntegers method, of class ArrayEncoding.
   */
  @Test
  public void testEncodeIntegers()
  {
    int[] values = null;
    String expResult = "";
    String result = ArrayEncoding.encodeIntegers(values);
    assertEquals(result, expResult);

    values = new int[]
    {
      1579529154, 1565839951, -419500139, -1761767805, 408596037, -1248081680, -1209735170, 533670125
    };
    expResult = "eJwBJwDY/1tJOwAAAAheJbPCXVTST+b+75WW/Y6DGFquRbWbyPC35Of+H88o7Vm3FK4=";
    result = ArrayEncoding.encodeIntegers(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of encodeLongs method, of class ArrayEncoding.
   */
  @Test
  public void testEncodeLongs()
  {
    long[] values = null;
    String expResult = "";
    String result = ArrayEncoding.encodeLongs(values);
    assertEquals(result, expResult);

    values = new long[]
    {
      -4146831782791384556L, 410436159644505059L, 1738383944632390563L, 1499069204296745459L, -4800692573659495840L, 637647492956349165L, 6912004237637102210L, -1955151751113166701L
    };
    expResult = "eJwBRwC4/1tMOwAAAAjGc35242tyFAWyKYM6lhPjGB/6/L8OU6MUzcN/t9XB871gg4oXK4JgCNlhBDWU4u1f7F87llqqguTd59dY6rCTQzIimQ==";
    result = ArrayEncoding.encodeLongs(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of encodeFloats method, of class ArrayEncoding.
   */
  @Test
  public void testEncodeFloats()
  {
    float[] values = null;
    String expResult = "";
    String result = ArrayEncoding.encodeFloats(values);
    assertEquals(result, expResult);

    values = new float[]
    {
      0.895342f, 0.213250f, 0.304512f, 0.917112f, 0.912794f, 0.953354f, 0.747538f, 0.905595f
    };
    expResult = "eJyLdrNmYGDgsE81VbKLijO1m/3iv33W8Vv2mWvu2ZfwM9nbxy2zT78qDAD5aQ4b";
    result = ArrayEncoding.encodeFloats(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of encodeDoubles method, of class ArrayEncoding.
   */
  @Test
  public void testEncodeDoubles()
  {
    double[] values = null;
    String expResult = "";
    String result = ArrayEncoding.encodeDoubles(values);
    assertEquals(result, expResult);

    values = new double[]
    {
      0.187250, 0.488825, 0.022674, 0.525196, 0.344635, 0.935423, 0.171828, 0.762522
    };
    expResult = "eJwBRwC4/1tEOwAAAAg/x/fO2RaHKz/fSOinHeabP5c32mHgxag/4M5n13+uNj/WDn/1g6U8P+3u/Dcdo38/xf51vES/TT/oZpSJj2BbxwEkAw==";
    result = ArrayEncoding.encodeDoubles(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of encodeDoublesAsFloats method, of class ArrayEncoding.
   */
  @Test
  public void testEncodeDoublesAsFloats()
  {
    double[] data = new double[]
    {
      -597904219.861853D, 828042688.070218D, 596063559.074515D, -861836357.569190D, -259263536.034188D, 164725038.288901D, -429435145.547622D, -19784862.188393D
    };
    String expResult = "eJyLdrNmYGDgOMfXq+vnmr3dj0/m6jnfqsCz5Q6HfWUlhM+eOepxetonfwAAow+H";
    String result = ArrayEncoding.encodeDoublesAsFloats(data);
    assertEquals(result, expResult);
  }

  /**
   * Test of convertToFloats method, of class ArrayEncoding.
   */
  @Test
  public void testConvertToFloats()
  {
    double[] values = new double[]
    {
      -456140859.766920D, -522321888.606352D, -344911454.874654D, -866595362.207846D,
      -695333209.518432D, -580729553.595671D, -354571538.535899D, -403660656.508311D
    };
    float[] expResult = new float[]
    {
      (float) -456140859.766920D, (float) -522321888.606352D, (float) -344911454.874654D, (float) -866595362.207846D,
      (float) -695333209.518432D, (float) -580729553.595671D, (float) -354571538.535899D, (float) -403660656.508311D
    };

    float[] result = ArrayEncoding.convertToFloats(values);
    assertTrue(Arrays.equals(result, expResult));
  }

  /**
   * Test of convertToInts method, of class ArrayEncoding.
   */
  @Test
  public void testConvertToInts() throws Exception
  {
    int[] result = null;

    double[] values = new double[]
    {
      92384221D, 281147844D, -980486909D, -110545257D, 631487186D, 682961301D, 533158025D, 839217069D
    };

    int[] expResult = new int[]
    {
      (int) 92384221, (int) 281147844, (int) -980486909, (int) -110545257, (int) 631487186, (int) 682961301, (int) 533158025, (int) 839217069
    };

    result = ArrayEncoding.convertToInts(values);
    assertEquals(result, expResult);

    // Test DataFormatException
    try
    {
      result = ArrayEncoding.convertToInts(new double[]
      {
        1.1
      });
    }
    catch (Exception ex)
    {
      assertSame(ex.getClass(), DataFormatException.class);
    }
  }

  /**
   * Test of deflate method, of class ArrayEncoding.
   */
  @Test
  public void testDeflate()
  {
    byte[] contents = "Hello, World!".getBytes();
    byte[] expResult = new byte[]
    {
      120, -100, -13, 72, -51, -55, -55, -41, 81, 8, -49, 47, -54, 73, 81, 4, 0, 31, -98, 4, 106
    };
    byte[] result = ArrayEncoding.deflate(contents);
    assertEquals(result, expResult);

    // Can not test RuntimeException
  }

  /**
   * Test of inflate method, of class ArrayEncoding.
   */
  @Test(expectedExceptions =
  {
    RuntimeException.class
  })
  public void testInflate()
  {
    byte[] contents = new byte[]
    {
      120, -100, -13, 72, -51, -55, -55, -41, 81, 8, -49, 47, -54, 73, 81, 4, 0, 31, -98, 4, 106
    };
    byte[] expResult = "Hello, World!".getBytes();
    byte[] result = ArrayEncoding.inflate(contents);
    assertEquals(result, expResult);

    // Test RuntimeException
    ArrayEncoding.inflate(new byte[0]);
  }

  /**
   * Test of decodeFloats method, of class ArrayEncoding.
   */
  @Test(expectedExceptions =
  {
    ParseException.class
  })
  public void testDecodeFloats() throws Exception
  {
    String contents = "eJyLdrNmYGDgsE81VbKLijO1m/3iv33W8Vv2mWvu2ZfwM9nbxy2zT78qDAD5aQ4b";
    float[] expResult = new float[]
    {
      0.895342f, 0.213250f, 0.304512f, 0.917112f, 0.912794f, 0.953354f, 0.747538f, 0.905595f
    };
    float[] result = ArrayEncoding.decodeFloats(contents);
    assertEquals(result, expResult);

    // Test null
    assertNull(ArrayEncoding.decodeFloats(""));

    // Test double
    contents = "1.0D 12345678.123D 42.42";
    result = ArrayEncoding.decodeFloats(contents);
    assertEquals(result, new float[]
    {
      1.0f, 12345678.123f, 42.42f
    });

    // Test ParseException
    ArrayEncoding.decodeFloats("abcdef");
  }

  /**
   * Test of decodeDoubles method, of class ArrayEncoding.
   */
  @Test(expectedExceptions =
  {
    ParseException.class
  })
  public void testDecodeDoubles() throws Exception
  {
    String contents = "eJwBRwC4/1tEOwAAAAg/x/fO2RaHKz/fSOinHeabP5c32mHgxag/4M5n13+uNj/WDn/1g6U8P+3u/Dcdo38/xf51vES/TT/oZpSJj2BbxwEkAw==";
    double[] expResult = new double[]
    {
      0.187250, 0.488825, 0.022674, 0.525196, 0.344635, 0.935423, 0.171828, 0.762522
    };
    double[] result = ArrayEncoding.decodeDoubles(contents);
    assertEquals(result, expResult);

    // Test null
    assertNull(ArrayEncoding.decodeDoubles(""));

    // Test double
    contents = "1.0D 12345678.123D 42.42";
    result = ArrayEncoding.decodeDoubles(contents);
    assertEquals(result, new double[]
    {
      1.0D, 12345678.123D, 42.42D
    });

    // Test ParseException
    ArrayEncoding.decodeDoubles("abcdef");
  }

  /**
   * Test of decodeIntegers method, of class ArrayEncoding.
   */
  @Test(expectedExceptions =
  {
    ParseException.class
  })
  public void testDecodeIntegers() throws Exception
  {
    String contents = "eJwBJwDY/1tJOwAAAAheJbPCXVTST+b+75WW/Y6DGFquRbWbyPC35Of+H88o7Vm3FK4=";
    int[] expResult = new int[]
    {
      1579529154, 1565839951, -419500139, -1761767805, 408596037, -1248081680, -1209735170, 533670125
    };
    int[] result = ArrayEncoding.decodeIntegers(contents);
    assertEquals(result, expResult);

    // Test null
    assertNull(ArrayEncoding.decodeIntegers(""));

    // Test double , uses Matcher.find
    result = ArrayEncoding.decodeIntegers("1.0D 12345678.0D 42.0");
    assertEquals(result, new Integer[]
    {
      1, 0, 12345678, 0, 42, 0
    });

    // Test ParseException
    ArrayEncoding.decodeIntegers("abcdef");
    ArrayEncoding.decodeIntegers("e!@#$%^&*()_+-=\\");
  }

  /**
   * Test of decodeLongs method, of class ArrayEncoding.
   */
  @Test(expectedExceptions =
  {
    ParseException.class
  })
  public void testDecodeLongs() throws Exception
  {
    String contents = "eJwBRwC4/1tMOwAAAAjGc35242tyFAWyKYM6lhPjGB/6/L8OU6MUzcN/t9XB871gg4oXK4JgCNlhBDWU4u1f7F87llqqguTd59dY6rCTQzIimQ==";
    long[] expResult = new long[]
    {
      -4146831782791384556L, 410436159644505059L, 1738383944632390563L, 1499069204296745459L, -4800692573659495840L, 637647492956349165L, 6912004237637102210L, -1955151751113166701L
    };
    long[] result = ArrayEncoding.decodeLongs(contents);
    assertEquals(result, expResult);

    // Test null
    assertNull(ArrayEncoding.decodeLongs(""));

    // Test double 
    result = ArrayEncoding.decodeLongs("1000000000000D 12345678D 42");
    assertEquals(result, new long[]
    {
      1000000000000L, 12345678L, 42L
    });

    // Test ParseException
    ArrayEncoding.decodeLongs("abcdef");
    ArrayEncoding.decodeLongs("e!@#$%^&*()_+-=\\");
  }

  /**
   * Test of toStringDoubles method, of class ArrayEncoding.
   */
  @Test
  public void testToStringDoubles_doubleArr()
  {
    double[] values = new double[]
    {
      1.0D, 6.0D, 15.0D, 20.0D, 15.0D, 6.0D, 1.0D
    };
    String expResult = "1.0 6.0 15.0 20.0 15.0 6.0 1.0";
    String result = ArrayEncoding.toStringDoubles(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of toStringDoubles method, of class ArrayEncoding.
   */
  @Test
  public void testToStringDoubles_doubleArr_String()
  {
    double[] values = new double[]
    {
      1.0D, 6.0D, 15.0D, 20.0D, 15.0D, 6.0D, 1.0D
    };
    String format = "%.1f,";
    String expResult = "1.0, 6.0, 15.0, 20.0, 15.0, 6.0, 1.0,";
    String result = ArrayEncoding.toStringDoubles(values, format);
    assertEquals(result, expResult);
  }

  /**
   * Test of isDouble method, of class ArrayEncoding.
   */
  @Test
  public void testIsDouble()
  {
    String str = "fortytwo";
    boolean expResult = false;
    boolean result = ArrayEncoding.isDouble(str);
    assertEquals(result, expResult);

    // IsDouble uses Matcher.find()
    assertEquals(ArrayEncoding.isDouble("42"), true);
    assertEquals(ArrayEncoding.isDouble("42.0"), true);
    assertEquals(ArrayEncoding.isDouble("42.0D"), true);
    assertEquals(ArrayEncoding.isDouble("42.0f"), true);
    assertEquals(ArrayEncoding.isDouble("42.0d"), true);
    assertEquals(ArrayEncoding.isDouble("42.0abcde"), true);
    assertEquals(ArrayEncoding.isDouble("42.0.42"), true);
    assertEquals(ArrayEncoding.isDouble("e42.0.42"), false);
  }

  /**
   * Test of parseFloats method, of class ArrayEncoding.
   */
  @Test
  public void testParseFloats()
  {
    String str = "0.0a 1.00b 1.000c 2.0000d 3.00000e 5.000000f";
    float[] expResult = new float[]
    {
      0.0f, 1.0f, 1.0f, 2.0f, 3.0f, 5.0f
    };
    float[] result = ArrayEncoding.parseFloats(str);
    assertEquals(result, expResult);

    assertEquals(ArrayEncoding.parseFloats(""), new float[0]);
  }

  /**
   * Test of parseDoubles method, of class ArrayEncoding.
   */
  @Test
  public void testParseDoubles()
  {
    String str = "";
    double[] expResult = new double[0];
    double[] result = ArrayEncoding.parseDoubles(str);
    assertEquals(result, expResult);

    expResult = new double[]
    {
      1701.0, 0514.0
    };
    assertEquals(ArrayEncoding.parseDoubles("1701.0 0514.0"), expResult);
  }

  /**
   * Test of parseIntegers method, of class ArrayEncoding.
   */
  @Test
  public void testParseIntegers()
  {
    String str = "";
    int[] expResult = new int[0];
    int[] result = ArrayEncoding.parseIntegers(str);
    assertEquals(result, expResult);

    expResult = new int[]
    {
      1701, 514
    };
    assertEquals(ArrayEncoding.parseIntegers("1701 514"), expResult);
  }

  /**
   * Test of parseLongs method, of class ArrayEncoding.
   */
  @Test
  public void testParseLongs()
  {
    String str = "";
    long[] expResult = new long[0];
    long[] result = ArrayEncoding.parseLongs(str);
    assertEquals(result, expResult);

    expResult = new long[]
    {
      17010000000L, 5140000000L
    };
    assertEquals(ArrayEncoding.parseLongs("17010000000 5140000000"), expResult);
  }

  /**
   * Test of encodeFloatsArray method, of class ArrayEncoding.
   */
  @Test
  public void testEncodeFloatsArray()
  {
    float[][] values = new float[0][0];
    String expResult = "eJyLjnZzZoACAA1QAUA=";
    String result = ArrayEncoding.encodeFloatsArray(values);
    assertEquals(result, expResult);

    values = new float[1][2];
    values[0][0] = 1.1f;
    values[0][1] = 2.22f;
    expResult = "eJyLjnZzZmBgYARiJvueM2cd+ESqASfRBIQ=";
    result = ArrayEncoding.encodeFloatsArray(values);
    assertEquals(result, expResult);

    // Variable columnns per rows
    values = new float[3][];
    values[0] = new float[1];
    values[1] = new float[2];
    values[2] = new float[3];
    values[0][0] = 1.1f;
    values[1][0] = 1.1f;
    values[1][1] = 2.22f;
    values[2][0] = 1.1f;
    values[2][1] = 2.22f;
    values[2][2] = 3.333f;
    expResult = "eJyLjnYLY2BgYAZiRvueM2eBNBOIduATqQaJw9gOof73AeaNDAU=";
    result = ArrayEncoding.encodeFloatsArray(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of encodeDoublesArray method, of class ArrayEncoding.
   */
  @Test
  public void testEncodeDoublesArray()
  {
    double[][] values = new double[0][0];
    boolean compact = false;
    String expResult = "eJyLjnZxZoACAA08AT4=";
    String result = ArrayEncoding.encodeDoublesArray(values, compact);
    assertEquals(result, expResult);

    compact = true;
    expResult = "eJyLjnZzZoACAA1QAUA=";
    result = ArrayEncoding.encodeDoublesArray(values, compact);
    assertEquals(result, expResult);

    values = new double[1][2];
    values[0][0] = 1.1D;
    values[0][1] = 2.22D;
    expResult = "eJyLjnZxZmBgYARiJvuPM0FglgPjof4Yja+HAWqbCdY=";
    result = ArrayEncoding.encodeDoublesArray(values, false);
    assertEquals(result, expResult);

    expResult = "eJyLjnZzZmBgYARiJvueM2cd+ESqASfRBIQ=";
    result = ArrayEncoding.encodeDoublesArray(values, true);
    assertEquals(result, expResult);

    // Variable columnns per rows
    values = new double[3][];
    values[0] = new double[1];
    values[1] = new double[2];
    values[2] = new double[3];
    values[0][0] = 1.1D;
    values[1][0] = 1.1D;
    values[1][1] = 2.22D;
    values[2][0] = 1.1D;
    values[2][1] = 2.22D;
    values[2][2] = 3.333D;
    expResult = "eJyLjnYJY2BgYAZiRvuPM0FgFpDNBGM7MB7qj9H4ehikBl3MgWvl7+c53S4ALK8bWw==";
    result = ArrayEncoding.encodeDoublesArray(values, false);
    assertEquals(result, expResult);

    expResult = "eJyLjnYLY2BgYAZiRvueM2eBNBOIduATqQaJw9gOof73AeaNDAU=";
    result = ArrayEncoding.encodeDoublesArray(values, true);
    assertEquals(result, expResult);
  }

  /**
   * Test of decodeFloatsArray method, of class ArrayEncoding.
   */
  @Test(expectedExceptions =
  {
    RuntimeException.class
  })
  public void testDecodeFloatsArray()
  {
    ArrayEncoding.decodeFloatsArray("");

    String contents = "eJyLjnZzZoACAA1QAUA=";
    float[][] expResult = new float[0][0];
    float[][] result = ArrayEncoding.decodeFloatsArray(contents);
    assertEquals(result, expResult);

    expResult = new float[1][2];
    expResult[0][0] = 1.1f;
    expResult[0][1] = 2.22f;
    result = ArrayEncoding.decodeFloatsArray("eJyLjnZzZmBgYARiJvueM2cd+ESqASfRBIQ=");
    assertEquals(result, expResult);

    expResult = new float[3][];
    expResult[0] = new float[1];
    expResult[1] = new float[2];
    expResult[2] = new float[3];
    expResult[0][0] = 1.1f;
    expResult[1][0] = 1.1f;
    expResult[1][1] = 2.22f;
    expResult[2][0] = 1.1f;
    expResult[2][1] = 2.22f;
    expResult[2][2] = 3.333f;
    result = ArrayEncoding.decodeFloatsArray("eJyLjnYLY2BgYAZiRvueM2eBNBOIduATqQaJw9gOof73AeaNDAU=");
    assertEquals(result, expResult);
  }

  /**
   * Test of decodeDoublesArray method, of class ArrayEncoding.
   */
  @Test(expectedExceptions =
  {
    RuntimeException.class
  })
  public void testDecodeDoublesArray()
  {
    ArrayEncoding.decodeDoublesArray("");

    double[][] expResult = new double[0][0];
    double[][] result = ArrayEncoding.decodeDoublesArray("eJyLjnZxZoACAA08AT4=");
    assertEquals(result, expResult);

    expResult = new double[1][2];
    expResult[0][0] = 1.1D;
    expResult[0][1] = 2.22D;
    result = ArrayEncoding.decodeDoublesArray("eJyLjnZxZmBgYARiJvuPM0FglgPjof4Yja+HAWqbCdY=");
    assertEquals(result, expResult);

    expResult = new double[3][];
    expResult[0] = new double[1];
    expResult[1] = new double[2];
    expResult[2] = new double[3];
    expResult[0][0] = 1.1D;
    expResult[1][0] = 1.1D;
    expResult[1][1] = 2.22D;
    expResult[2][0] = 1.1D;
    expResult[2][1] = 2.22D;
    expResult[2][2] = 3.333D;
    result = ArrayEncoding.decodeDoublesArray("eJyLjnYJY2BgYAZiRvuPM0FgFpDNBGM7MB7qj9H4ehikBl3MgWvl7+c53S4ALK8bWw==");
    assertEquals(result, expResult);
  }

}
