// --- file: gov/llnl/rtk/view/SensorFaceSmallReader.java ---
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
@Reader.Declaration(pkg = RtkPackage.class, name = "smallFace", referenceable = true,
        cls = SensorFaceSmall.class)
@Reader.Attribute(name = "width", type = double.class, required = true)
@Reader.Attribute(name = "area", type = double.class, required = true)
public class SensorFaceSmallReader extends ObjectReader<SensorFaceSmall>
{
  @Override
  public SensorFaceSmall start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    SensorFaceSmall obj = new SensorFaceSmall();
    obj.area = Double.parseDouble(attributes.getValue("area"));
    return obj;
  }

  @Override
  public Reader.ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    Reader.ReaderBuilder<SensorFaceSmall> builder = this.newBuilder();
    builder.element("origin").call((p, o) -> p.origin = o, Vector3.class);
    builder.element("orientation").call((p, o) -> p.orientation = o, Versor.class);
    return builder.getHandlers();
  }

}
