package gov.llnl.rtk.quality;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "singleChannelSpikeCheck",
  cls = SingleChannelSpikeCheck.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class SingleChannelSpikeCheck$$Reader extends ObjectReader<SingleChannelSpikeCheck>
{
  @Override
  public SingleChannelSpikeCheck start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    SingleChannelSpikeCheck out = new SingleChannelSpikeCheck();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<SingleChannelSpikeCheck> builder = this.newBuilder();
    builder.element("singleChannelFraction")
      .contents(double.class)
      .call(SingleChannelSpikeCheck::setSingleChannelFraction)
       ;
    return builder.getHandlers();
  }

}
