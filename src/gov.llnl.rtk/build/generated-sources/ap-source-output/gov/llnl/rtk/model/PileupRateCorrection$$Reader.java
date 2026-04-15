package gov.llnl.rtk.model;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "rateCorrection",
  cls = PileupRateCorrection.class,
  referenceable = false,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
@Reader.Attribute(name="rate", type=java.lang.Double.class, required=true)
public class PileupRateCorrection$$Reader extends ObjectReader<PileupRateCorrection>
{
  @Override
  public PileupRateCorrection start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    PileupRateCorrection out = new PileupRateCorrection();
    attributeValue = attributes.getValue("rate");
    if (attributeValue!=null)
       out.setMaxRate((java.lang.Double) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.Double.class).valueOf(attributeValue));
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

}
