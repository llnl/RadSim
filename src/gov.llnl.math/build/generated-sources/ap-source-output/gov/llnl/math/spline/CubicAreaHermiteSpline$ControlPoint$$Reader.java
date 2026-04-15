package gov.llnl.math.spline;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.ReaderContext;

@Reader.Declaration(
  pkg = gov.llnl.math.MathPackage.class, 
  name = "cubicAreaControlPoint",
  cls = CubicAreaHermiteSpline.ControlPoint.class,
  referenceable = false,
  contents = Reader.Contents.ELEMENTS,
  order = Reader.Order.FREE,
  document = true)
public class CubicAreaHermiteSpline$ControlPoint$$Reader extends ObjectReader<CubicAreaHermiteSpline.ControlPoint>
{
  @Override
  public CubicAreaHermiteSpline.ControlPoint start(ReaderContext context, org.xml.sax.Attributes attributes) throws ReaderException
  {
    String attributeValue;
    CubicAreaHermiteSpline.ControlPoint out = new CubicAreaHermiteSpline.ControlPoint();
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

}
