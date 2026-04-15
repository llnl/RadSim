package gov.llnl.rtk.calibration;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "cumulativePeakFinder",
  cls = CumulativePeakFinder.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class CumulativePeakFinder$$Reader extends ObjectReader<CumulativePeakFinder>
{
  @Override
  public CumulativePeakFinder start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    CumulativePeakFinder out = new CumulativePeakFinder();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<CumulativePeakFinder> builder = this.newBuilder();
    builder.element("expectedCumulative")
      .contents(double.class)
      .call(CumulativePeakFinder::setExpectedCumulative)
      .required()
       ;
    builder.element("expectedLowerFraction")
      .contents(double.class)
      .call(CumulativePeakFinder::setExpectedLowerFaction)
      .required()
       ;
    builder.element("empiricalChannel")
      .contents(double.class)
      .call(CumulativePeakFinder::setEmpiricalChannel)
      .required()
       ;
    return builder.getHandlers();
  }

}
