package gov.llnl.rtk.calibration;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "nonlinearMap",
  cls = NonlinearityMap.class,
  referenceable = false,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class NonlinearityMap$$Reader extends ObjectReader<NonlinearityMap>
{
  @Override
  public NonlinearityMap start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    NonlinearityMap out = new NonlinearityMap();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<NonlinearityMap> builder = this.newBuilder();
    builder.element("control")
      .contents(gov.llnl.rtk.calibration.ControlMapping.class)
      .call(NonlinearityMap::addControl)
       ;
    return builder.getHandlers();
  }

}
