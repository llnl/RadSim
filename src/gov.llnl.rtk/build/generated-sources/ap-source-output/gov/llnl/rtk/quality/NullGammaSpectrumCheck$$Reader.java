package gov.llnl.rtk.quality;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "nullGammaSpectrumCheck",
  cls = NullGammaSpectrumCheck.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class NullGammaSpectrumCheck$$Reader extends ObjectReader<NullGammaSpectrumCheck>
{
  @Override
  public NullGammaSpectrumCheck start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    NullGammaSpectrumCheck out = new NullGammaSpectrumCheck();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

}
