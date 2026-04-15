package gov.llnl.rtk.quality;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "saturationCheck",
  cls = SaturationCheck.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class SaturationCheck$$Reader extends ObjectReader<SaturationCheck>
{
  @Override
  public SaturationCheck start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    SaturationCheck out = new SaturationCheck();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<SaturationCheck> builder = this.newBuilder();
    builder.element("maximumCountRate")
      .contents(double.class)
      .call(SaturationCheck::setMaximumCountRate)
       ;
    return builder.getHandlers();
  }

}
