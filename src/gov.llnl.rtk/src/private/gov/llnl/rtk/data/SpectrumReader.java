// --- file: gov/llnl/rtk/data/SpectrumReader.java ---
package gov.llnl.rtk.data;

import gov.llnl.utility.ArrayEncoding;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 * @param <T>
 */
public abstract class SpectrumReader<T extends SpectrumBase> extends ObjectReader<T>
{
  Class<T> spectrumClass;

  public SpectrumReader(Class<T> cls)
  {
    this.spectrumClass = cls;
  }

  @Override
  public T start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    try
    {
      return this.spectrumClass.getDeclaredConstructor().newInstance();
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
    return this.spectrumClass;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<T> builder = this.newBuilder();
    builder.element("attributes").reader(new AttributesReader()).call((p,v)->p.getAttributes().putAll(v))
            .optional();
    builder.section(new AttributesSection());
    builder.element("title").callString(T::setTitle);
    builder.element("realTime").callDouble(T::setRealTime);
    builder.element("liveTime").callDouble(T::setLiveTime);
    builder.element("gammaEnergyBins").call(T::setEnergyScale, EnergyScale.class);
    builder.element("gammaCounts").contents(String.class)
            .callContext(SpectrumReader::assign);
    builder.element("minimumValidChannel").callInteger(T::setMinimumValidChannel);
    builder.element("maximumValidChannel").callInteger(T::setMaximumValidChannel);
    return builder.getHandlers();
  }

  static void assign(ReaderContext context, SpectrumBase obj, String contents)
          throws ReaderException
  {
    try
    {
//      Attributes attr = context.getChildContext().getAttributes();
      Class cls = obj.getCountClass();
      if (cls.equals(int[].class))
        obj.assignData(ArrayEncoding.decodeIntegers(contents));
      else if (cls.equals(double[].class))
        obj.assignData(ArrayEncoding.decodeDoubles(contents));
      else
        throw new ReaderException("Unable to handle " + cls);
    }
    catch (ParseException ex)
    {
      throw new ReaderException(ex);
    }
  }

  class AttributesSection extends Section
  {
    public AttributesSection()
    {
      super(Order.FREE, "attributes");
    }

    @Override
    public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
    {
      ReaderBuilder<T> builder = this.newBuilder();
      builder.any(Object.class)
              .callContext(SpectrumReader::setAttribute).optional()
              .options(Option.ANY_ALL);
      return builder.getHandlers();
    }
  }

  private static void setAttribute(ReaderContext context, SpectrumBase spectrum, Object object)
  {
    ReaderContext.ElementContext hc = context.getChildContext();
    String namespace = hc.getNamespaceURI();
    String localname = hc.getLocalName();
    if (namespace == null)
      namespace = "";
    spectrum.setAttribute(String.format("%s#%s", namespace, localname), (Serializable) object);
  }

}
