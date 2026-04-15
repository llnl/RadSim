/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

/**
 * Network order double[] encoding protocol.
 *
 * Note this uses network order and stores the size of the array as the first
 * element. packed(Type.Double) can store the same data with a different byte
 * order.
 *
 * On some implementations like Java protobuf, the doubles will be stored as a
 * List&lt;Double&gt; which is very inefficient.
 *
 * @author nelson85
 */
class NetworkDoublesEncoding extends MessageEncoding<double[]>
{
  static final NetworkDoublesEncoding INSTANCE = new NetworkDoublesEncoding();

  /**
   * Serialize a double[] into bytes.
   *
   * @param values are the array to serialize, may be null.
   * @return the byte representation for this.
   */
  @Override
  public byte[] serializeContents(ProtoContext context, double[] values)
  {
    if (values == null)
    {
      ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES);
      bb.putInt(-1);
      bb.rewind();
      return bb.array();
    }
    ByteBuffer bb = ByteBuffer.allocate(values.length * Double.BYTES + Integer.BYTES);
    bb.order(ByteOrder.BIG_ENDIAN);
    bb.putInt(values.length);
    bb.asDoubleBuffer().put(values);
    bb.rewind();
    return bb.array();
  }

  /**
   * Parse a ByteBuffer into a double[].
   *
   * @param context is not used and can be null.
   * @param bs is the byte source to parse.
   * @return is a new double[] or null if specified in the proto.
   */
  @Override
  public double[] parseContents(ProtoContext context, ByteSource bs) throws ProtoException
  {
    if (!bs.hasRemaining())
      return null;
    ByteBuffer bb = bs.request(Integer.BYTES);
    if (bb.remaining() != Integer.BYTES)
      throw new ProtoException("trunacted int", bs.position());
    bb.order(ByteOrder.BIG_ENDIAN);
    int sz = bb.getInt();
    if (sz < 0)
      return null;
    double[] values = new double[sz];
    bb = bs.request(sz * Double.BYTES);
    if (bb.remaining() != sz * Double.BYTES)
      throw new ProtoException("trunacted double[]", bs.position());
    DoubleBuffer db = bb.asDoubleBuffer();
    if (db.limit() != sz)
      throw new ProtoException("size mismatch (" + sz + "," + db.limit() + ")", bs.position());
    db.get(values);
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
