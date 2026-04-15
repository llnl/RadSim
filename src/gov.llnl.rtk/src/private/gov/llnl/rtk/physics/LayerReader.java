// --- file: gov/llnl/rtk/physics/LayerReader.java ---
/*
 * Copyright 2017, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Internal
@Reader.Declaration(pkg = RtkPackage.class, name = "layer", cls = LayerImpl.class,
        order = Reader.Order.SEQUENCE,
        referenceable = true)
@Reader.Attribute(name = "label", type = String.class)
public class LayerReader extends ObjectReader<LayerImpl>
{
  static final Geometry DEFAULT_GEOMETRY = Geometry.newSpherical();

  @Override
  public LayerImpl start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    LayerImpl out = new LayerImpl(DEFAULT_GEOMETRY, null);
    out.setLabel(attributes.getValue("label"));
    return out;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<LayerImpl> builder = this.newBuilder();
    builder.element("thickness")
            .reader(new QuantityReader(PhysicalProperty.LENGTH))
            .call((p,v)->p.setThickness(v));
    builder.element("geometry")
            .reader(new GeometryReader())
            .call(LayerImpl::setGeometry).optional();
    builder.element("material")
            .reader(new MaterialReader())
            .call(LayerImpl::setMaterial);
    return builder.getHandlers();
  }

}
