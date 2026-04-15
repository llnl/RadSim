/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Network order double[][][] encoding protocol.
 *
 * Note this uses network order and stores the size of the array as the first
 * element. packed(Type.Double) can store the same data with a different byte
 * order. Allows for ragged and nulls within the array.
 *
 * @author nelson85
 */
class NetworkDoubles3Encoding extends MessageEncoding<double[][][]>
{
  static final NetworkDoubles3Encoding INSTANCE = new NetworkDoubles3Encoding();

  /**
   * Serialize a double[][][] into bytes.
   *
   * @param values are the array to serialize, may be null.
   * @return the byte representation for this.
   */
  @Override
  public byte[] serializeContents(ProtoContext context, double[][][] values)
  {
    ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES);
    // Deal with null array
    if (values == null)
    {
      bb.putInt(-1);
      return bb.array();
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bb.putInt(values.length);
    baos.writeBytes(bb.array());
    for (double[][] v : values)
    {
      baos.writeBytes(NetworkDoubles2Encoding.INSTANCE.serializeContents(null, v));
    }
    return baos.toByteArray();
  }

  /**
   * Parse a ByteBuffer into a double[][][].
   *
   * @param context is not used and can be null.
   * @param bs is the byte source to parse.
   * @return is a new double[][][] or null if specified in the proto.
   */
  @Override
  public double[][][] parseContents(ProtoContext context, ByteSource bs)
          throws ProtoException
  {
    if (!bs.hasRemaining())
      return null;

    ByteBuffer bb = bs.request(Integer.BYTES);
    if (bb.remaining() != Integer.BYTES)
      throw new ProtoException("truncated int", bs.position());
    bb.order(ByteOrder.BIG_ENDIAN);
    int sz = bb.getInt();
    if (sz < 0)
      return null;

    double[][][] values = new double[sz][][];
    for (int i = 0; i < sz; ++i)
    {
      values[i] = NetworkDoubles2Encoding.INSTANCE.parseContents(null, bs);
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
