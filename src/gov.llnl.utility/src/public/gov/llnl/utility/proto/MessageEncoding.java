/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import gov.llnl.utility.PathUtilities;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * An encoder for a specific message type.
 *
 * Message encoders should be static with no state information as members.
 *
 * To hold state create a State class and use newBuilder(pkg, name, Class,
 * State.class).
 *
 * @author nelson85
 * @param <T>
 */
public abstract class MessageEncoding<T> implements ProtoEncoding<T>
{

  /**
   * Used to construct the fields structure.
   *
   * This is used if there is a builder such that the initially created object
   * does not match the final object produced.
   *
   * @param <T>
   * @param pkg is an object that will define the package for the schema.
   * @param name is the name of this encoding (should be unique).
   * @param allocator supplies new object for the parser.
   * @return
   */
  public static <T> ProtoBuilder<T, T> newBuilder(Object pkg, String name, Supplier<T> allocator)
  {
    return new ProtoBuilderImpl(pkg, name, allocator, null);
  }

  /**
   * Used to construct the fields structure with a custom creator.
   *
   * This is used if there is a builder such that the initially created object
   * does not match the final object produced.
   *
   * @param <R>
   * @param <T>
   * @param pkg is an object that will define the package for the schema.
   * @param name is the name of this encoding (should be unique).
   * @param allocator supplies new object for the parser.
   * @param converter converted parsed objects into their final state.
   * @return
   */
  public static <R, T> ProtoBuilder<R, T> newBuilder(Object pkg, String name, Supplier<T> allocator, Function<T, R> converter)
  {
    return new ProtoBuilderImpl(pkg, name, allocator, converter);
  }

  /**
   * Used to construct fields in which there needs to be a custom state object.
   *
   * The message encoding should override allocate and finish methods whenever
   * this is used.
   *
   * @param <R>
   * @param <T>
   * @param pkg
   * @param name
   * @param cls1
   * @param cls2
   * @return
   */
  public static <R, T> ProtoBuilder<R, T> newBuilder(Object pkg, String name, Class<R> cls1, Class<T> cls2)
  {
    return new ProtoBuilderImpl(pkg, name, null, null);
  }

  /**
   * Get the list of fields that this type holds.
   *
   * Used only for message types. The fields should generally be a static array.
   * The fields structure can be constructed using the builder.
   *
   * @return
   */
  public abstract ProtoField[] getFields();

  /**
   * Parse a field from message.
   *
   * The stream will be in a state such that the Byte source is pointing to the
   * size of the message. The tag and wire type for the object will be remove.
   * The state after parsing should point to the next fields tag and wire type.
   *
   * @param field
   * @param type
   * @param obj
   * @param bs
   * @throws ProtoException
   */
  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object obj, ByteSource bs)
          throws ProtoException
  {
    if (type != WIRE_LEN)
      throw new ProtoException("bad wire type on field " + field.name + " of " + this.getClass().getName(), bs.position());

    // Limit consumption to the declared object size
    int size = Int32Encoding.decodeVInt32(bs);
//    System.out.println("STRUCT " + field.id + " " + this.getClass().getName() + " " + size);
    ByteSource bs2 = context.enterMessage(bs, size);
    ((BiConsumer) field.setter).accept(obj, parseContents(context, bs2));
    context.leaveMessage(bs2);
  }

  /**
   * Serialize a field into a message.Adds tag and size.
   *
   * @param field
   * @param baos
   * @param obj
   * @throws gov.llnl.utility.proto.ProtoException
   */
  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
  {
    //check for field existance
    T result = (T) ((Function) field.getter).apply(obj);

    // if missing skip
    if (result == null)
      return;

    // Allow all fields to pack themselves
    byte[] contents = this.serializeContents(null, result);

    // field and wire type
    ProtoEncoding.encodeTag(baos, field, WIRE_LEN);
    if (contents == null)
    {
      Int32Encoding.encodeVInt32(baos, 0);
      return;
    }
    // size
    Int32Encoding.encodeVInt32(baos, contents.length);
    // place contents
    baos.writeBytes(contents);
  }

  /**
   * Parse bytes into an object using an empty context.
   *
   * @param contents are bytes for a serialized object.
   * @return the object produced.
   * @throws ProtoException if a field in this message was unable to be
   * processed.
   */
  public T parseBytes(byte[] contents) throws ProtoException
  {
    ProtoContext context = new ProtoContext();
    return parseContents(context, ByteSource.wrap(contents));
  }

  public T parseFile(Path file) throws IOException, ProtoException
  {
    {
      try (InputStream is = Files.newInputStream(file))
      {
        InputStream is2 = is;
        if (PathUtilities.isGzip(file))
          is2 = new GZIPInputStream(is);
        return this.parseStream(new BufferedInputStream(is2));
      }
    }

  }

  /**
   * Parse a stream into an object using an empty context.
   *
   * @param stream is a stream holding a serialized object.
   * @return the object produced.
   * @throws ProtoException if a field in this message was unable to be
   * processed.
   */
  public T parseStream(InputStream stream) throws ProtoException
  {
    ProtoContext context = new ProtoContext();
    return parseContents(context, ByteSource.wrap(stream));
  }

  /**
   * Parse a gzipped stream into an object using an empty context.
   *
   * @param stream is a stream holding a serialized object.
   * @return the object produced.
   * @throws ProtoException if a field in this message was unable to be
   * processed.
   */
  public T parseStreamGZ(InputStream stream) throws ProtoException
  {
    ProtoContext context = new ProtoContext();
    try (GZIPInputStream gis = new GZIPInputStream(stream))
    {
      return parseContents(context, ByteSource.wrap(gis));
    }
    catch (IOException ex)
    {
      throw new ProtoException("error in stream", 0, ex);
    }
  }

  public void saveStream(OutputStream os, T t) throws IOException, ProtoException
  {
    byte[] bytes = this.toBytes(t);
    os.write(bytes);
  }

  public void saveStreamGZ(OutputStream os, T t) throws IOException, ProtoException
  {
    try (GZIPOutputStream gos = new GZIPOutputStream(os))
    {
      byte[] bytes = this.toBytes(t);
      gos.write(bytes);
    }
  }

  /**
   * Produce a contents message holding the bytes for this object.This does not
   * add the field or wire type.Nor should it contain the size for variable
   * length objects.
   *
   *
   * @param context
   * @param obj
   * @return
   * @throws ProtoException
   */
  public byte[] serializeContents(ProtoContext context, T obj) throws ProtoException
  {
    // Allow all fields to pack themselves
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (ProtoField field : getFields())
    {
      if (field.id == -1)
        continue;
      if ((field.optional != null)
              && (!field.optional.test(obj)))
        continue;

      field.encoding.serializeField(field, baos, obj);
    }
    return baos.toByteArray();
  }

  /**
   * Parse a ByteSource into an object.
   *
   * Unexpected fields are ignored. Incorrect types or sizes may produce a
   * ProtoException depending on the severity.
   *
   * @param context holds temporary data used during parsing.
   * @param bs is the byte source holding the data.
   * @return the object held in the stream.
   * @throws ProtoException on a parsing error.
   */
  public T parseContents(ProtoContext context, ByteSource bs) throws ProtoException
  {
    ProtoField[] fields = getFields();
    context.enterFields(fields);
    Object obj = this.allocate(context, fields);

    ProtoHeader header = (ProtoHeader) fields[0];
    ProtoField found = null;
    // Consume fields until we run out of contents
    while (bs.hasRemaining())
    {
      int tag;

      if (true)
      {
        tag = ProtoEncoding.decodeTag(bs);  // varint "key" (field<<3 | wire)
        if (tag == -1)
          break;
      }
      else
      {
        // There was a bug in the protocol implementation that failed to use a var int.   
        // To convert the broken file we need to turn on this old log then resave the file.
        tag = bs.get();
        // if EOF then stop
        if (tag == -1)
          break;
      }

      int id = tag >>> 3;
      int type = tag & 0x7;
//      System.out.println(bs.position() + " id=" + id + " type=" + type + " " + this.getClass());

      // Check for a field handler
      if (found == null || found.id != id)
        found = header.map.get(id);

      // If we find a valid field process it
      if (found != null)
      {
//        System.out.println(String.format("FIELD %2d %02x: %s.%s", id, tag, found.encoding.getClass().getName(), found.name));
        // Delegate to the marshaller
        found.encoding.parseField(context, found, type, obj, bs);
      }
      else
      {
//        System.out.println(String.format("FIELD %2d %02x: <ignore>", id, tag));
        // else skip it
        ignoreField(type, bs);
      }
    }

    // Complete work for list/map/repeated fields
    for (ProtoField field : fields)
    {
      if (field.encoding == null)
        continue;
      field.encoding.parseFinish(context, field, obj);
    }

    // Pull the fields off the stack
    T out = (T) finish(context, fields, obj);
    context.leaveFields(fields);
    return out;
  }

  /**
   * Allocate the object for construction during parsing.
   *
   * Special hook when the allocator for the object to be constructed needs
   * parameters from the context. Context enterFields is called before this
   * method. This will be overriden when the object has to interact with a
   * builder.
   *
   * @param context
   * @param fields
   * @return
   */
  protected Object allocate(ProtoContext context, ProtoField[] fields)
  {
    ProtoHeader header = (ProtoHeader) fields[0];
    return header.allocator.get();
  }

  /**
   * Convert to the final object.
   *
   * Special hook when the converter to the final object requires parameters
   * from the context. Context leaveFields is called after this method.
   *
   * @param context
   * @param fields
   * @param obj
   * @return
   */
  protected T finish(ProtoContext context, ProtoField[] fields, Object obj)
  {
    // Convert to the final object type before returning
    ProtoHeader header = (ProtoHeader) fields[0];
    if (header.converter != null)
      return (T) header.converter.apply(obj);
    else
      return (T) obj;
  }

  /**
   * Ignore a field in a proto structure.
   *
   * Used to eat fields that were not defined in the proto.
   *
   * @param type
   * @param bs
   * @throws ParseException
   */
  static void ignoreField(int type, ByteSource bs) throws ProtoException
  {
    int pos0 = bs.position();
    // eat unknown field base on wire type
    switch (type)
    {
      case WIRE_VARINT:
        Int64Encoding.decodeVInt64(bs);
        break;
      case WIRE_FIXED32:
        if (bs.request(4).remaining() != 4)
          throw new ProtoException("truncated fixed32", pos0);
        break;
      case WIRE_FIXED64:
        if (bs.request(8).remaining() != 8)
          throw new ProtoException("truncated fixed64", pos0);
        break;
      case WIRE_LEN:
        int sz = Int32Encoding.decodeVInt32(bs);
        if (bs.request(sz).remaining() != sz)
          throw new ProtoException("truncated field", pos0);
        break;
      case 3:
      case 4:
        throw new ProtoException("group not supported", pos0);
      default:
        throw new ProtoException("bad wire type " + type, pos0);
    }
  }

  /**
   * Serialize an object into bytes using a blank context.
   *
   * @param value
   * @return
   * @throws ProtoException
   */
  public byte[] toBytes(T value) throws ProtoException
  {
    return serializeContents(null, value);
  }

  /**
   * Get the package for the object.
   *
   * This will be used to identify messages that belong in the same proto
   * definition file.
   *
   * @return a object that defines the package or null if this object does not
   * have a schema.
   *
   */
  public Object getPackage()
  {
    ProtoField[] fields = getFields();
    if (fields == null)
      return null;
    ProtoHeader headers = (ProtoHeader) fields[0];
    return headers.pkg;
  }

  /**
   * Get the name of this message in the schema.
   *
   * This may be defined even if there is no schema for the encoding.
   *
   * @return
   */
  @Override
  public String getSchemaName()
  {
    ProtoField[] fields = getFields();
    if (fields == null)
      return null;
    ProtoHeader headers = (ProtoHeader) fields[0];
    return headers.name;
  }

  @Override
  public String toString()
  {
    return this.getClass().getSimpleName();
  }

}
