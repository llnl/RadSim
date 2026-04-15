package gov.llnl.rtk.calibration;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.rtk.RtkPackage.class, 
  name = "controlMapping",
  cls = ControlMapping.class,
  referenceable = false,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
@Reader.Attribute(name="origin", type=java.lang.Double.class, required=true)
@Reader.Attribute(name="energy", type=java.lang.Double.class)
public class ControlMapping$$Reader extends ObjectReader<ControlMapping>
{
  @Override
  public ControlMapping start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    ControlMapping out = new ControlMapping();
    attributeValue = attributes.getValue("origin");
    if (attributeValue!=null)
       out.setOrigin((java.lang.Double) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.Double.class).valueOf(attributeValue));
    attributeValue = attributes.getValue("energy");
    if (attributeValue!=null)
       out.setEnergy((java.lang.Double) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.Double.class).valueOf(attributeValue));
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<ControlMapping> builder = this.newBuilder();
    builder.element("spline")
      .contents(gov.llnl.math.spline.CubicHermiteSpline.class)
      .call(ControlMapping::setSpline)
       ;
    return builder.getHandlers();
  }

}
