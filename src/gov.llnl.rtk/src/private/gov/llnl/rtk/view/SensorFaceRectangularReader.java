// --- file: gov/llnl/rtk/view/SensorFaceRectangularReader.java ---
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
@Reader.Declaration(pkg = RtkPackage.class, name = "rectangularFace", 
        referenceable = true, cls = SensorFaceRectangular.class)
@Reader.Attribute(name="width", type=double.class, required=true)
@Reader.Attribute(name="height", type=double.class, required=true)
public class SensorFaceRectangularReader extends ObjectReader<SensorFaceRectangular>
{

  @Override
  public SensorFaceRectangular start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    SensorFaceRectangular obj = new SensorFaceRectangular();
    obj.height=Double.parseDouble(attributes.getValue("height"));
    obj.width=Double.parseDouble(attributes.getValue("width"));
    return obj;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<SensorFaceRectangular> builder = this.newBuilder();
    builder.element("origin").call((p, o) -> p.origin = o, Vector3.class);
    builder.element("orientation").call((p,o)->p.orientation=o, Versor.class);
    return builder.getHandlers();
  }

}