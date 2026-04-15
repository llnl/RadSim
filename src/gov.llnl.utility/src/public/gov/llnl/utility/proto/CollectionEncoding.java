/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Handler for an array of objects.
 *
 * @author nelson85
 * @param <T>
 */
class CollectionEncoding<T> implements ProtoEncoding
{
  final ProtoField embedded = new ProtoField();
  final static BiConsumer setter = (BiConsumer) (Object t, Object u) -> ((Collection) t).add(u);
  final static Function getter = (Function) (Object o) -> o;
  final Supplier<Collection<T>> supplier;

  /** 
   * Create a new collection encoding.
   * 
   * @param encoding is the encoding for the item.
   * @param supplier is a a method to produce the list.
   */
  public CollectionEncoding(ProtoEncoding encoding, Supplier<Collection<T>> supplier)
  {
    embedded.name = encoding.getClass().getSimpleName();
    embedded.encoding = encoding;
    embedded.setter = setter;
    if (supplier == null)
      supplier = ArrayList::new;
    this.supplier = supplier;
  }

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object obj, ByteSource bs)
          throws ProtoException
  {
    Collection array = (Collection) (context.getState(field, false));
    if (array == null)
    {
      array = supplier.get();
      context.setState(field, array);
    }

    // delegate to the original encoder
    embedded.encoding.parseField(context, embedded, type, array, bs);
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj)
          throws ProtoException
  {
    //check for field existance
    Iterable<T> result = (Iterable<T>) ((Function) field.getter).apply(obj);

    // if missing skip
    if (result == null)
      return;

    ProtoField field1 = new ProtoField();
    field1.id = field.id;
    field1.encoding = embedded.encoding;
    field1.getter = getter;

    for (T value : result)
    {
      embedded.encoding.serializeField(field1, baos, value);
    }
  }

  @Override
  public void parseFinish(ProtoContext context, ProtoField field, Object obj)
  {
    Object state = context.getState(field, false);
    if (state != null)
      ((BiConsumer) field.setter).accept(obj, state);
  }

  @Override
  public String getSchemaName()
  {
    return this.embedded.encoding.getSchemaName();
  }

}
