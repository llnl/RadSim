package gov.llnl.rtk.calibration;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "stabilizerTarget",
  cls = StabilizerTarget.class,
  referenceable = true,
  contents = Reader.Contents.TEXT,
  document = true)
@Reader.Attribute(name="units", type=gov.llnl.rtk.calibration.StabilizerTarget.Units.class, required=true)
public class StabilizerTarget$$Reader extends ObjectReader<StabilizerTarget>
{
  @Override
  public StabilizerTarget start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    StabilizerTarget out = new StabilizerTarget();
    attributeValue = attributes.getValue("units");
    if (attributeValue!=null)
       out.setUnits((gov.llnl.rtk.calibration.StabilizerTarget.Units) gov.llnl.utility.ClassUtilities.newValueOf(gov.llnl.rtk.calibration.StabilizerTarget.Units.class).valueOf(attributeValue));
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

  public StabilizerTarget contents(ReaderContext context, String textContents) throws ReaderException
  {
   getObject(context).setValue((double) gov.llnl.utility.ClassUtilities.newValueOf(double.class).valueOf(textContents));
    return null;
  }

}
