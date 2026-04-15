package gov.llnl.rtk.calibration;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "externalCalibration",
  cls = ExternalCalibration.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class ExternalCalibration$$Reader extends ObjectReader<ExternalCalibration>
{
  @Override
  public ExternalCalibration start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    ExternalCalibration out = new ExternalCalibration();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

}
