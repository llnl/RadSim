package gov.llnl.rtk.data;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "rule",
  cls = SpectraListFilter.RuleImpl.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
@Reader.Attribute(name="field", type=gov.llnl.rtk.data.SpectraListFilter.Field.class, required=true)
@Reader.Attribute(name="op", type=gov.llnl.rtk.data.SpectraListFilter.Operation.class)
@Reader.Attribute(name="value", type=java.lang.Double.class)
@Reader.Attribute(name="contains", type=java.lang.String.class)
@Reader.Attribute(name="matches", type=java.lang.String.class)
public class SpectraListFilter$RuleImpl$$Reader extends ObjectReader<SpectraListFilter.RuleImpl>
{
  @Override
  public SpectraListFilter.RuleImpl start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    SpectraListFilter.RuleImpl out = new SpectraListFilter.RuleImpl();
    attributeValue = attributes.getValue("field");
    if (attributeValue!=null)
       out.setField((gov.llnl.rtk.data.SpectraListFilter.Field) gov.llnl.utility.ClassUtilities.newValueOf(gov.llnl.rtk.data.SpectraListFilter.Field.class).valueOf(attributeValue));
    attributeValue = attributes.getValue("op");
    if (attributeValue!=null)
       out.setOperation((gov.llnl.rtk.data.SpectraListFilter.Operation) gov.llnl.utility.ClassUtilities.newValueOf(gov.llnl.rtk.data.SpectraListFilter.Operation.class).valueOf(attributeValue));
    attributeValue = attributes.getValue("value");
    if (attributeValue!=null)
       out.setValue((java.lang.Double) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.Double.class).valueOf(attributeValue));
    attributeValue = attributes.getValue("contains");
    if (attributeValue!=null)
       out.setPattern((java.lang.String) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.String.class).valueOf(attributeValue));
    attributeValue = attributes.getValue("matches");
    if (attributeValue!=null)
       out.setMatches((java.lang.String) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.String.class).valueOf(attributeValue));
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<SpectraListFilter.RuleImpl> builder = this.newBuilder();
    builder.element("roi")
      .contents(gov.llnl.rtk.data.EnergyRegionOfInterest.class)
      .call(SpectraListFilter.RuleImpl::setEnergyRegionOfInterest)
       ;
    return builder.getHandlers();
  }

}
