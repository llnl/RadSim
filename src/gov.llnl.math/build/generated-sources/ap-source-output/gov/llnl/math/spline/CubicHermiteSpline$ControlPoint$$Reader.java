package gov.llnl.math.spline;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.math.MathPackage.class, 
  name = "cubicControlPoint",
  cls = CubicHermiteSpline.ControlPoint.class,
  referenceable = false,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
@Reader.Attribute(name="x", type=java.lang.Double.class, required=true)
@Reader.Attribute(name="y", type=java.lang.Double.class, required=true)
@Reader.Attribute(name="m", type=java.lang.Double.class)
public class CubicHermiteSpline$ControlPoint$$Reader extends ObjectReader<CubicHermiteSpline.ControlPoint>
{
  @Override
  public CubicHermiteSpline.ControlPoint start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    CubicHermiteSpline.ControlPoint out = new CubicHermiteSpline.ControlPoint();
    attributeValue = attributes.getValue("x");
    if (attributeValue!=null)
       out.setX((java.lang.Double) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.Double.class).valueOf(attributeValue));
    attributeValue = attributes.getValue("y");
    if (attributeValue!=null)
       out.setY((java.lang.Double) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.Double.class).valueOf(attributeValue));
    attributeValue = attributes.getValue("m");
    if (attributeValue!=null)
       out.setM((java.lang.Double) gov.llnl.utility.ClassUtilities.newValueOf(java.lang.Double.class).valueOf(attributeValue));
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

}
