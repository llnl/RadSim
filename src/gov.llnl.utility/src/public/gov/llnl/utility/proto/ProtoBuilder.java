/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Builder for mapping fields/methods to proto fields in a message type.
 *
 * This should generally be used in a static block so that each proto wrapper
 * only needs to be constructed once.
 *
 *
 * @author nelson85
 * @param <T>
 * @param <B>
 */
public abstract class ProtoBuilder<T, B>
{

  /**
   * Create a new proto field map.
   *
   * @param name
   * @param id
   * @return
   */
  public abstract ProtoBuilder<T, B>.TypeSpec<T, B> field(String name, int id);

  public abstract ProtoBuilder<T, B>.TypeSpec<T, B> field(String name, int id, Supplier<? extends ProtoField> supplier);

  /**
   * Convert to a list of mapped fields.
   *
   * This should be held statically in the message encoder.
   *
   * @return
   */
  public abstract ProtoField[] toFields();

  /**
   * Specify the type for the field.
   *
   * This was required to be an abstract class to satisfy Java generic
   * specifications.
   *
   * @param <T>
   */
  public abstract class TypeSpec<T, B>
  {
    /**
     * Store a single boolean value.
     *
     * @param encoding is the type for the scalar.
     * @return a handle for setting the mapping.
     */
    public abstract ProtoBuilder.AsBool<T, B> type(BooleanEncoding encoding);

    /**
     * Store a single int value.
     *
     * The encoding depends on the Type selected. Valid types are Type.Int32,
     * Type.SInt32, Type.UInt32, Type.FixedInt32.
     *
     * @param encoding is the type for the scalar.
     * @return a handle for setting the field mapping.
     */
    public abstract ProtoBuilder.AsInt<T, B> type(IntEncoding encoding);

    /**
     * Store a single long value.
     *
     * The encoding depends on the Type selected. Valid types are Type.Int64,
     * Type.SInt64, Type.UInt64, Type.FixedInt64.
     *
     * @param encoding is the type for the scalar.
     * @return a handle for setting the field mapping.
     */
    public abstract ProtoBuilder.AsLong<T, B> type(LongEncoding encoding);

    /**
     * Store a single float value.
     *
     * @param encoding is the type for the scalar (must be Type.Float).
     * @return a handle for setting the field mapping.
     */
    public abstract ProtoBuilder.AsFloat<T, B> type(FloatEncoding encoding);

    /**
     * Store a single double value.
     *
     * @param encoding is the type for the scalar (must be Type.Double).
     * @return a handle for setting the field mapping.
     */
    public abstract ProtoBuilder.AsDouble<T, B> type(DoubleEncoding encoding);

    /**
     * Store a single object value.
     *
     * @param <V>
     * @param encoding is the encoding for this object type.
     * @return a handle for setting the field mapping.
     */
    public abstract <V> ProtoBuilder.AsObject<T, B, V, V> type(MessageEncoding<V> encoding);

    public abstract <C> ProtoBuilder.AsOneOf<T, B, C, C> oneof(Class<C> cls);

    /**
     * Store a single object value using a custom encoding.
     *
     * @param encoding is the encoding for this object type.
     * @return a handle for setting the field mapping.
     */
    public abstract <V> ProtoBuilder.AsOther<T, B, V, V> encoding(ProtoEncoding<V> encoding);

    /**
     * Store a packed array of integers.
     *
     * @param encoding is the type for the scalar.
     * @return a handle for setting the field mapping.
     */
    public abstract ProtoBuilder.AsObject<T, B, int[], int[]> packed(IntEncoding encoding);

    /**
     * Store a packed array of longs.
     *
     * @param encoding is the type for the scalar.
     * @return a handle for setting the field mapping.
     */
    public abstract ProtoBuilder.AsObject<T, B, long[], long[]> packed(LongEncoding encoding);

    /**
     * Store a packed array of floats.
     *
     * @param encoding is the type for the scalar.
     * @return a handle for setting the field mapping.
     */
    public abstract ProtoBuilder.AsObject<T, B, float[], float[]> packed(FloatEncoding encoding);

    /**
     * Store a packed array of doubles.
     *
     * @param encoder is the type for the scalar.
     * @return a handle for setting the field mapping.
     */
    public abstract ProtoBuilder.AsObject<T, B, double[], double[]> packed(DoubleEncoding encoder);

    /**
     * Store a list of values.
     *
     * Used when the data is a collection of objects in a list. Note that with
     * scalars they will appear a list of boxed types. For more efficient
     * packing consider using the packed encoder for scalars. This type is
     * automatically repeated and thus the repeated tag is not applicable.
     *
     * @param <V>
     * @param encoder
     * @return a handle for setting the field mapping.
     */
    public abstract <V> ProtoBuilder.AsObject<T, B, Iterable<V>, List<V>> list(ProtoEncoding<V> encoder);

    public abstract <V, C extends Collection<V>> ProtoBuilder.AsObject<T, B, Iterable<V>, C> list(ProtoEncoding<V> encoder, Supplier<C> supplier);

    /**
     * Store a map of values.
     *
     * Scalar types will be represents as boxed types. The order of the elements
     * is arbitrary and will not be preserved. This type is automatically
     * repeated and thus the repeated tag is not applicable.
     *
     * @param <Key>
     * @param <Value>
     * @param m1 is the encoder for the key.
     * @param m2 is the encoder for the value.
     * @return a handle for setting the field mapping.
     */
    public abstract <Key, Value> ProtoBuilder.AsObject<T, B, Map<Key, Value>, Map<Key, Value>> map(ProtoEncoding<Key> m1, ProtoEncoding<Value> m2);

    public abstract <Key, Value, Q extends Map<Key, Value>> ProtoBuilder.AsObject<T, B, Map<Key, Value>, Q> map(ProtoEncoding<Key> m1, ProtoEncoding<Value> m2, Supplier<Q> supplier);

    /**
     * Store an enumeration value.
     *
     * Currently this uses the ordinals as defined by Java. Thus order maters.
     *
     * TODO there should be a version with user defined translation between
     * objects.
     *
     * @param <EnumType>
     * @param cls is the class for the enum.
     * @return a handle for setting the field mapping.
     */
    public abstract <EnumType extends Enum> ProtoBuilder.AsObject<T, B, EnumType, EnumType> enumeration(Class<EnumType> cls);

    /**
     * Store a string type.
     *
     * This is just a short hand for .type(Type.String). This was added to make
     * the format less confusing.
     *
     * @return a handle for setting the field mapping.
     */
    public abstract ProtoBuilder.AsObject<T, B, String, String> string();
  }

  public interface AsObject<T, B, G, S>
  {
    /**
     * Map to an object field.
     *
     * This will box scalars. A null value usually denotes that the field was
     * not set. If the field has a default it will not be overriden if the field
     * does not appear in the proto buffer.
     *
     * @param getter
     * @param setter
     * @return
     */
    ProtoBuilder.Options<T> as(Function<T, G> getter, BiConsumer<B, S> setter);
  }

  public interface AsOneOf<T, B, G, S> extends AsObject<T, B, G, S>
  {
    ProtoBuilder.AsOneOf<T, B, G, S> add(int code, Class<? extends G> cls, MessageEncoding<? extends G> encoding);
  }


  public interface AsOther<T, B, G, S> extends AsObject<T, B, G, S>
  {
  }

  public interface AsBool<U, B> extends ProtoBuilder.AsObject<U, B, Boolean, Boolean>
  {
    /**
     * Map to a boolean field.
     *
     * @param getter is the lambda to retrieve the value from the object.
     * @param setter is the lambda to store the value in the object.
     * @return a handle for setting options.
     */
    ProtoBuilder.Options asBool(Predicate<U> getter, ObjBoolConsumer<U> setter);
  }

  public interface AsInt<U, B> extends ProtoBuilder.AsObject<U, B, Integer, Integer>
  {
    /**
     * Map to an integer field.
     *
     * @param getter is the lambda to retrieve the value from the object.
     * @param setter is the lambda to store the value in the object.
     * @return a handle for setting options.
     */
    ProtoBuilder.Options asInt(ToIntFunction<U> getter, ObjIntConsumer<B> setter);
  }

  public interface AsLong<U, B> extends ProtoBuilder.AsObject<U, B, Long, Long>
  {
    /**
     * Map to a long field.
     *
     * @param getter is the lambda to retrieve the value from the object.
     * @param setter is the lambda to store the value in the object.
     * @return a handle for setting options.
     */
    ProtoBuilder.Options asLong(ToLongFunction<U> getter, ObjLongConsumer<B> setter);
  }

  public interface AsFloat<U, B> extends ProtoBuilder.AsObject<U, B, Float, Float>
  {
    /**
     * Map to a float field.
     *
     * @param getter is the lambda to retrieve the value from the object.
     * @param setter is the lambda to store the value in the object.
     * @return a handle for setting options.
     */
    ProtoBuilder.Options<U> asFloat(ToFloatFunction<U> getter, ObjFloatConsumer<B> setter);
  }

  public interface AsDouble<U, B> extends ProtoBuilder.AsObject<U, B, Double, Double>
  {
    /**
     * Map to a double field.
     *
     * @param getter is the lambda to retrieve the value from the object.
     * @param setter is the lambda to store the value in the object.
     * @return a handle for setting options.
     */
    ProtoBuilder.Options<U> asDouble(ToDoubleFunction<U> getter, ObjDoubleConsumer<B> setter);
  }

  public interface Options<T>
  {
    /**
     * Indicates that the encoder can call the same set method an unlimited
     * number of times.
     *
     * Certain types are automatically repeated such as map and list, but these
     * will all produce on call at the end of the parsing.
     *
     * @return a handle for setting options.
     */
    Options<T> repeated();

    /**
     * Determines if the object is stored or skipped when serializing.
     *
     * @param predicate must return true to store the field.
     * @return a handle for setting options.
     */
    Options<T> optional(Predicate<T> predicate);
  }

//<editor-fold desc="lambda">
  static public interface ToFloatFunction<T>
  {
    float applyAsFloat(T t);
  }

  static public interface ObjBoolConsumer<T>
  {
    void accept(T t, boolean value);
  }

  static public interface ObjFloatConsumer<T>
  {
    void accept(T t, float value);
  }

//</editor-fold>
}
