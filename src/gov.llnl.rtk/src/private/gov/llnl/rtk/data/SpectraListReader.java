// --- file: gov/llnl/rtk/data/SpectraListReader.java ---
/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 * @param <T>
 */
//@Reader.Declaration(pkg = RtkPackage.class, name = "spectraList",
//        referenceable = true, document = true, order = Reader.Order.CHOICE)
public abstract class SpectraListReader<T extends SpectraList> extends ObjectReader<T>
{
  private T list;
  private Class<T> listClass;
  private final Class contentsClass;

  @SuppressWarnings("unchecked")
  public SpectraListReader(T list)
  {
    this.list = list;
    this.listClass = (Class<T>) list.getClass();
    contentsClass = list.getSpectrumClass();
  }

  SpectraListReader(Class<T> listClass)
  {
    try
    {
      this.listClass = listClass;
      contentsClass = listClass.getDeclaredConstructor().newInstance().getSpectrumClass();
    }
    catch (IllegalAccessException | IllegalArgumentException 
            | InstantiationException | NoSuchMethodException 
            | SecurityException | InvocationTargetException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public T start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    if (list != null)
      return list;
    try
    {
      return listClass.getDeclaredConstructor().newInstance();
    }
    catch (IllegalAccessException | IllegalArgumentException 
            | InstantiationException | NoSuchMethodException 
            | SecurityException | InvocationTargetException ex)
    {
      throw new ReaderException(ex);
    }
  }

  @Override
  public Class<T> getObjectClass()
  {
    return listClass;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<T> builder = this.newBuilder();
    builder.element("gadrasVersion").contents(String.class).nop();
    builder.element("energyBins").contents(EnergyScale.class).nop();
    BiConsumer<SpectraList, Spectrum> add = SpectraList::add;
    builder.element("spectrum").contents(contentsClass)
            .call(add);
    return builder.getHandlers();
  }

}
