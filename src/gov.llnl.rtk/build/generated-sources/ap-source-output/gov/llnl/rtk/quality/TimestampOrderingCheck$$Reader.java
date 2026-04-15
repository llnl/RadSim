package gov.llnl.rtk.quality;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "timestampOrderingCheck",
  cls = TimestampOrderingCheck.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class TimestampOrderingCheck$$Reader extends ObjectReader<TimestampOrderingCheck>
{
  @Override
  public TimestampOrderingCheck start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    TimestampOrderingCheck out = new TimestampOrderingCheck();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<TimestampOrderingCheck> builder = this.newBuilder();
    builder.element("timestampValidRange")
      .contents(long.class)
      .call(TimestampOrderingCheck::setTimestampValidRange)
       ;
    return builder.getHandlers();
  }

}
