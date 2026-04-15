/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Network order double[][] encoding protocol.
 *
 * Note this uses network order and stores the size of the array as the first
 * element. packed(Type.Double) can store the same data with a different byte
 * order. Allows for ragged and nulls within the array.
 *
 * @author nelson85
 */
class NetworkDoubles2Encoding extends MessageEncoding<double[][]>
{
  static final NetworkDoubles2Encoding INSTANCE = new NetworkDoubles2Encoding();

  /**
   * Serialize a double[][] into bytes.
   *
   * @param values are the array to serialize, may be null.
   * @return the byte representation for this.
   */
  @Override
  public byte[] serializeContents(ProtoContext context, double[][] values)
  {
    // Deal with null array
    if (values == null)
    {
      ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES);
      bb.putInt(-1);
      return bb.array();
    }

    int dims = values.length + 1;
    int sz = 0;
    for (double[] v : values)
    {
      if (v != null)
        sz += v.length;
    }

    ByteBuffer bb = ByteBuffer.allocate(dims * Integer.BYTES + sz * Double.BYTES);
    bb.order(ByteOrder.BIG_ENDIAN);
    bb.putInt(values.length);
    for (double[] v : values)
    {
      if (v == null)
      {
        bb.putInt(-1);
        continue;
      }
      bb.putInt(v.length);
      bb.asDoubleBuffer().put(v);
      bb.position(bb.position() + Double.BYTES * v.length);
    }
    bb.rewind();
    return bb.array();
  }

  /**
   * Parse a ByteBuffer into a double[][].
   *
   * @param context is not used and can be null.
   * @param bs is the byte source to parse.
   * @return is a new double[][] or null if specified in the proto.
   */
  @Override
  public double[][] parseContents(ProtoContext context, ByteSource bs) throws ProtoException
  {
    if (!bs.hasRemaining())
      return null;

    // Fetch the first dimension
    ByteBuffer bb = bs.request(Integer.BYTES);
    if (bb.remaining() != Integer.BYTES)
      throw new ProtoException("truncated int", bs.position());
    bb.order(ByteOrder.BIG_ENDIAN);
    int sz = bb.getInt();
    if (sz < 0)
      return null;

    // Retrieve remaining dimensions
    double[][] values = new double[sz][];
    for (int i = 0; i < sz; ++i)
    {
      bb = bs.request(Integer.BYTES);
      if (bb.remaining() != Integer.BYTES)
        throw new ProtoException("truncated int", bs.position());
      int sz2 = bb.getInt();
      if (sz2 < 0)
        continue;
      values[i] = new double[sz2];
      bb = bs.request(Double.BYTES * sz2);
      if (bb.remaining() != Double.BYTES * sz2)
        throw new ProtoException("truncated double[]", bs.position());
      bb.asDoubleBuffer().get(values[i]);
    }
    return values;
  }

  @Override
  public ProtoField[] getFields()
  {
    return null;
  }

  @Override
  public String getSchemaName()
  {
    return "bytes";
  }
}
