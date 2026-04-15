package gov.llnl.rtk.quality;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "lowEnergyCheck",
  cls = LowEnergyCheck.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class LowEnergyCheck$$Reader extends ObjectReader<LowEnergyCheck>
{
  @Override
  public LowEnergyCheck start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    LowEnergyCheck out = new LowEnergyCheck();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<LowEnergyCheck> builder = this.newBuilder();
    builder.element("fractionBelowLLD")
      .contents(double.class)
      .call(LowEnergyCheck::setFractionBelowLLD)
       ;
    builder.element("lldChannel")
      .contents(int.class)
      .call(LowEnergyCheck::setLldChannel)
       ;
    return builder.getHandlers();
  }

}
