package gov.llnl.rtk.data;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "spectraListFilter",
  cls = SpectraListFilter.class,
  referenceable = true,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
@Reader.Attribute(name="limit", type=java.lang.Integer.class)
public class SpectraListFilter$$Reader extends ObjectReader<SpectraListFilter>
{
  @Override
  public SpectraListFilter start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    SpectraListFilter out = new SpectraListFilter();
    attributeValue = attributes.getValue("limit");
    if (attributeValue!=null)
       out.setLimit((java.lang.Integer) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.Integer.class).valueOf(attributeValue));
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<SpectraListFilter> builder = this.newBuilder();
    builder.element("rule")
      .contents(gov.llnl.rtk.data.SpectraListFilter.RuleImpl.class)
      .call(SpectraListFilter::addRule)
       ;
    builder.element("include")
      .contents(gov.llnl.rtk.data.SpectraListFilter.RuleImpl.class)
      .call(SpectraListFilter::addInclude)
       ;
    builder.element("exclude")
      .contents(gov.llnl.rtk.data.SpectraListFilter.RuleImpl.class)
      .call(SpectraListFilter::addExclude)
       ;
    return builder.getHandlers();
  }

}
