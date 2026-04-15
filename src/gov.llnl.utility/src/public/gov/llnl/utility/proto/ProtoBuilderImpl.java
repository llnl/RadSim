/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.util.ArrayList;
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
 * Implementation for building proto fields.
 *
 * @author nelson85
 */
public class ProtoBuilderImpl extends ProtoBuilder
        implements
        ProtoBuilder.AsOther,
        ProtoBuilder.AsOneOf,
        ProtoBuilder.AsBool, ProtoBuilder.AsInt, ProtoBuilder.AsLong,
        ProtoBuilder.AsFloat, ProtoBuilder.AsDouble, ProtoBuilder.Options
{
  ArrayList<ProtoField> fields_ = new ArrayList<>();
  ProtoField next;
  TypeImpl internal = new TypeImpl();
  private final ProtoHeader header;

  ProtoBuilderImpl(Object pkg, String name, Supplier a, Function f)
  {
    this.header = new ProtoHeader(pkg, name, a, f);
    fields_.add(header);
  }

//<editor-fold desc="fields" defaultstate="collapsed">
  @Override
  public ProtoBuilder.TypeSpec field(String name, int id)
  {
    next = new ProtoField();
    fields_.add(next);
    next.name = name;
    next.id = id;
    return this.internal;
  }

  @Override
  public ProtoBuilder.TypeSpec field(String name, int id, Supplier supplier)
  {
    next = (ProtoField) supplier.get();
    fields_.add(next);
    next.name = name;
    next.id = id;
    return this.internal;
  }

  @Override
  public ProtoField[] toFields()
  {
    ProtoHeader header = (ProtoHeader) fields_.get(0);
    // Build a hashmap for quick access
    boolean repeated = false;
    for (ProtoField field : fields_)
    {
      if (field.id == -1)
        continue;
      if (field.encoding == null)
        throw new RuntimeException();
      if (field.id < 1)
        throw new RuntimeException("incorrect field id");
      if (header.map.containsKey(field.id))
        throw new RuntimeException("duplicate field");
      header.map.put(field.id, field);
      repeated |= field.repeated;
    }
    // Mark the
    if (repeated)
      header.repeated = repeated;
    return fields_.toArray(ProtoField[]::new);
  }
//</editor-fold>
//<editor-fold desc="types" defaultstate="collapsed">

  @Override
  public AsOneOf add(int code, Class cls, MessageEncoding encoding)
  {
    ((OneOfEncoding) next.encoding).add(code, cls, encoding);
    return this;
  }

  private class TypeImpl extends TypeSpec
  {
    @Override
    public AsObject type(MessageEncoding encoding)
    {
      next.encoding = encoding;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsBool type(BooleanEncoding encoder)
    {
      next.encoding = encoder;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsInt type(IntEncoding encoder)
    {
      next.encoding = encoder;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsLong type(LongEncoding encoder)
    {
      next.encoding = encoder;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsFloat type(FloatEncoding encoder)
    {
      next.encoding = encoder;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsDouble type(DoubleEncoding encoder)
    {
      next.encoding = encoder;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject packed(IntEncoding encoder)
    {
      next.encoding = new PackedScalarEncoding(encoder);
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject packed(LongEncoding encoder)
    {
      next.encoding = new PackedScalarEncoding(encoder);
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject packed(FloatEncoding encoder)
    {
      next.encoding = new PackedScalarEncoding(encoder);
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject packed(DoubleEncoding encoder)
    {
      next.encoding = new PackedScalarEncoding(encoder);
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject list(ProtoEncoding encoder)
    {
      if (encoder == null)
        throw new NullPointerException();
      next.encoding = new CollectionEncoding(encoder, null);
      next.repeated = true;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject list(ProtoEncoding encoder, Supplier supplier)
    {
      if (encoder == null)
        throw new NullPointerException();
      next.encoding = new CollectionEncoding(encoder, supplier);
      next.repeated = true;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject string()
    {
      next.encoding = ProtoEncoding.Type.String;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject map(ProtoEncoding m1, ProtoEncoding m2)
    {
      next.encoding = new MapEncoding(m1, m2, null);
      next.repeated = true;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject map(ProtoEncoding m1, ProtoEncoding m2, Supplier supplier)
    {
      next.encoding = new MapEncoding(m1, m2, supplier);
      next.repeated = true;
      return ProtoBuilderImpl.this;
    }

    @Override
    public ProtoBuilder.AsObject enumeration(Class cls)
    {
      next.encoding = new EnumEncoding(cls, null, null);
      return ProtoBuilderImpl.this;
    }

    @Override
    public AsOther encoding(ProtoEncoding encoding)
    {
      next.encoding = encoding;
      return ProtoBuilderImpl.this;
    }

    @Override
    public AsOneOf oneof(Class cls)
    {
      next.encoding = new OneOfEncoding(next.name + "_type", cls);
      return ProtoBuilderImpl.this;
    }
  }
//</editor-fold>
//<editor-fold desc="map">

  @Override
  public Options as(Function getter, BiConsumer setter)
  {
    next.getter = getter;
    next.setter = setter;
    return this;
  }

  @Override
  public ProtoBuilder.Options asBool(Predicate getter, ProtoBuilder.ObjBoolConsumer setter)
  {
    next.getter = getter;
    next.setter = setter;
    return this;
  }

  @Override
  public ProtoBuilder.Options asInt(ToIntFunction getter, ObjIntConsumer setter)
  {
    next.getter = getter;
    next.setter = setter;
    return this;
  }

  @Override
  public ProtoBuilder.Options asLong(ToLongFunction getter, ObjLongConsumer setter)
  {
    next.getter = getter;
    next.setter = setter;
    return this;
  }

  @Override
  public ProtoBuilder.Options asFloat(ProtoBuilder.ToFloatFunction getter, ProtoBuilder.ObjFloatConsumer setter)
  {
    next.getter = getter;
    next.setter = setter;
    return this;
  }

  @Override
  public ProtoBuilder.Options asDouble(ToDoubleFunction getter, ObjDoubleConsumer setter)
  {
    next.getter = getter;
    next.setter = setter;
    return this;
  }
//</editor-fold>
//<editor-fold desc="options">

  @Override
  public ProtoBuilder.Options repeated()
  {
    next.repeated = true;
    return this;
  }

  @Override
  public ProtoBuilder.Options optional(Predicate p)
  {
    next.optional = p;
    return this;
  }
//</editor-fold>
}
