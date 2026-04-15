package gov.llnl.rtk.filter;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "energyResolutionSpectralFilter",
  cls = EnergyResolutionSpectralFilter.class,
  referenceable = false,
  contents = Reader.Contents.TEXT,
  document = true)
public class EnergyResolutionSpectralFilter$$Reader extends ObjectReader<EnergyResolutionSpectralFilter>
{
  @Override
  public EnergyResolutionSpectralFilter start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    EnergyResolutionSpectralFilter out = new EnergyResolutionSpectralFilter();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

  public EnergyResolutionSpectralFilter contents(ReaderContext context, String textContents) throws ReaderException
  {
   getObject(context).setFilterCoef((double) gov.llnl.utility.ClassUtilities.newValueOf(double.class).valueOf(textContents));
    return null;
  }

}
