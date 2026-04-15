// --- file: gov/llnl/rtk/view/SensorViewCompositeReader.java ---
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
@Reader.Declaration(pkg = RtkPackage.class, name = "sensorViewComposite",
        order = Reader.Order.SEQUENCE, referenceable = true, cls=SensorViewComposite.class)
public class SensorViewCompositeReader extends ObjectReader<SensorViewComposite>
{

  @Override
  public SensorViewComposite start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new SensorViewComposite();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<SensorViewComposite> builder = this.newBuilder();
    builder.element("origin").call((p, o) -> p.origin = o, Vector3.class);
    builder.element("orientation").call((p, o) -> p.orientation = o, Versor.class);
    builder.element("elements").list(new SensorViewReader()).call((p, o) -> p.faces.addAll(o));
    return builder.getHandlers();
  }

}
