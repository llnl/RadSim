package gov.llnl.rtk.quality;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "liveTimeRangeCheck",
  cls = LiveTimeRangeCheck.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class LiveTimeRangeCheck$$Reader extends ObjectReader<LiveTimeRangeCheck>
{
  @Override
  public LiveTimeRangeCheck start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    LiveTimeRangeCheck out = new LiveTimeRangeCheck();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<LiveTimeRangeCheck> builder = this.newBuilder();
    builder.element("minimumLiveTime")
      .contents(double.class)
      .call(LiveTimeRangeCheck::setMinLiveTime)
       ;
    builder.element("maximumLiveTime")
      .contents(double.class)
      .call(LiveTimeRangeCheck::setMaxLiveTime)
       ;
    return builder.getHandlers();
  }

}
