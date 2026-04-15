package gov.llnl.rtk.calibration;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "ratioPeakTester",
  cls = RatioPeakTester.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class RatioPeakTester$$Reader extends ObjectReader<RatioPeakTester>
{
  @Override
  public RatioPeakTester start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    RatioPeakTester out = new RatioPeakTester();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<RatioPeakTester> builder = this.newBuilder();
    builder.element("minimumPeakRatio")
      .contents(double.class)
      .call(RatioPeakTester::setMinimumPeakRatio)
       ;
    return builder.getHandlers();
  }

}
