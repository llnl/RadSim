// --- file: gov/llnl/rtk/view/SensorFaceRectangularCollimatedReader.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Versor;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "rectangularCollimatedFace", referenceable = true,
        cls = SensorFaceRectangularCollimated.class)
@Reader.Attribute(name = "width", type = double.class, required = true)
@Reader.Attribute(name = "height", type = double.class, required = true)
@Reader.Attribute(name = "size", type = double.class, required = true)
@Reader.Attribute(name = "top", type = double.class, required = true)
public class SensorFaceRectangularCollimatedReader extends ObjectReader<SensorFaceRectangularCollimated>
{

  @Override
  public SensorFaceRectangularCollimated start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    SensorFaceRectangularCollimated obj = new SensorFaceRectangularCollimated();
    obj.height = Double.parseDouble(attributes.getValue("height"));
    obj.width = Double.parseDouble(attributes.getValue("width"));
    obj.extentSide = Double.parseDouble(attributes.getValue("side"));
    obj.extentTop = Double.parseDouble(attributes.getValue("top"));
    return obj;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<SensorFaceRectangularCollimated> builder = this.newBuilder();
    builder.element("origin").call((p, o) -> p.origin = o, Vector3.class);
    builder.element("orientation").call((p, o) -> p.orientation = o, Versor.class);
    return builder.getHandlers();
  }

}
