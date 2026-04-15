package gov.llnl.rtk.model;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "deadTimeCorrection",
  cls = PileupDeadTimeCorrection.class,
  referenceable = false,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class PileupDeadTimeCorrection$$Reader extends ObjectReader<PileupDeadTimeCorrection>
{
  @Override
  public PileupDeadTimeCorrection start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    PileupDeadTimeCorrection out = new PileupDeadTimeCorrection();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

}
