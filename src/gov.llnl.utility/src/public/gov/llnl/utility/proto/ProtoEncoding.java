/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;

/**
 * Base class for all ProtoBuf message and field types.
 *
 * @author nelson85
 * @param <T>
 */
public interface ProtoEncoding<T>
{

  public static final int WIRE_VARINT = 0;
  public static final int WIRE_FIXED64 = 1;
  public static final int WIRE_LEN = 2;
  public static final int WIRE_FIXED32 = 5;

  /**
   * Common types used by the proto encoder.
   *
   * Uses these types when defining types with a builder.
   */
  public static class Type
  {
    /**
     * Encoding for boolean value.
     */
    public static final BooleanEncoding Bool = BoolEncodingImpl.INSTANCE;
    /**
     * Encoding for integer value with variable encoding.
     */
    public static final IntEncoding Int32 = Int32Encoding.INSTANCE;
    /**
     * Encoding for unsigned integer value with variable encoding.
     */
    public static final IntEncoding UInt32 = UInt32Encoding.INSTANCE;
    /**
     * Encoding for scissor integer value with variable encoding.
     */
    public static final IntEncoding SInt32 = SInt32Encoding.INSTANCE;
    /**
     * Encoding for integer value with fixed encoding.
     */
    public static final IntEncoding FixedInt32 = FixedInt32Encoding.INSTANCE;
    /**
     * Encoding for long value with variable encoding.
     */
    public static final LongEncoding Int64 = Int64Encoding.INSTANCE;
    /**
     * Encoding for unsigned long value with variable encoding.
     */
    public static final LongEncoding UInt4 = UInt64Encoding.INSTANCE;
    /**
     * Encoding for scissor long value with variable encoding.
     */
    public static final LongEncoding SInt64 = SInt64Encoding.INSTANCE;
    /**
     * Encoding for long value with fixed encoding.
     */
    public static final LongEncoding FixedInt64 = FixedInt64Encoding.INSTANCE;
    /**
     * Encoding for float value with fixed encoding.
     */
    public static final FloatEncoding Float = FloatEncodingImpl.INSTANCE;
    /**
     * Encoding for double value with fixed encoding.
     */
    public static final DoubleEncoding Double = DoubleEncodingImpl.INSTANCE;
    /**
     * Encoding for arbitrary bytes.
     */
    public static final MessageEncoding<byte[]> Bytes = BytesEncoding.INSTANCE;
    /**
     * Encoding for UTF-8 string.
     */
    public static final MessageEncoding<String> String = StringEncoding.INSTANCE;

    public static final MessageEncoding<double[]> NetworkDoubles = NetworkDoublesEncoding.INSTANCE;
    public static final MessageEncoding<double[][]> NetworkDoubles2 = NetworkDoubles2Encoding.INSTANCE;
    public static final MessageEncoding<double[][][]> NetworkDoubles3 = NetworkDoubles3Encoding.INSTANCE;

  }

  /**
   * Parse a field from message.
   *
   * Removes size and contents. The tag was removed by the caller. This method
   * is required to verify the wire type before proceding.
   *
   * @param context holds state data used to unpack repeated fields.
   * @param field is the field description such the field id, getter, and
   * setter.
   * @param type is the wire type used.
   * @param obj is the parent what will hold this field.
   * @param bs is the byte stream for this proto.
   * @throws ProtoException if there is an issue.
   */
  void parseField(ProtoContext context, ProtoField field, int type, Object obj, ByteSource bs)
          throws ProtoException;

  /**
   * Serialize a field into a message.
   *
   * Adds tag and size.
   *
   * @param field
   * @param baos
   * @param obj
   */
  void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj)
          throws ProtoException;

  default void parseFinish(ProtoContext context, ProtoField field, Object obj)
          throws ProtoException
  {
    // Do nothing
  }

  String getSchemaName();

  default String getSchemaOptions()
  {
    return "";
  }

  public static void encodeTag(ByteArrayOutputStream baos, ProtoField field, int wire)
          throws ProtoException
  {
    int id = field.id;

    // Field numbers: 1..(2^29-1), and 19000-19999 are reserved by protobuf
    if (id <= 0 || id > 0x1FFFFFFF)
      throw new ProtoException("invalid field id " + id, 0);
    if (id >= 19000 && id <= 19999)
      throw new ProtoException("field id " + id + " is in reserved range 19000-19999", 0);

    // Wire types: you do not support groups (3,4), and protobuf only defines 0..5
    if (wire < 0 || wire > 5 || wire == 3 || wire == 4)
      throw new ProtoException("invalid/unsupported wire type " + wire + " for field " + id, 0);

    int key = (id << 3) | wire;
    Int32Encoding.encodeVInt32(baos, key);
  }

  public static int decodeTag(ByteSource is) throws ProtoException
  {
    int pos0 = is.position();

    int i = 0;
    int shift = 0;

    // Key is a uint32 varint, max 5 bytes
    for (int count = 0; count < 5; ++count)
    {
      int j = is.get();

      // Preserve old behavior: return -1 only if we're at EOF before reading any tag bytes
      if (j == -1)
      {
        if (count == 0)
          return -1;
        throw new ProtoException("truncated tag", pos0);
      }

      i |= (j & 0x7f) << shift;

      if ((j & 0x80) == 0)
      {
        int fieldNumber = i >>> 3;
        int wireType = i & 0x7;

        if (fieldNumber == 0)
          throw new ProtoException("invalid tag, field number 0", pos0);
        if (fieldNumber > 0x1FFFFFFF)
          throw new ProtoException("invalid tag, field number too large " + fieldNumber, pos0);
        if (fieldNumber >= 19000 && fieldNumber <= 19999)
          throw new ProtoException("invalid tag, reserved field number " + fieldNumber, pos0);

        if (wireType > 5 || wireType == 3 || wireType == 4)
          throw new ProtoException("invalid/unsupported wire type " + wireType, pos0);

        return i;
      }

      shift += 7;
    }

    // If we consumed 5 bytes and still have continuation, it's a malformed varint32 key
    throw new ProtoException("malformed tag varint (too long)", pos0);
  }

}
