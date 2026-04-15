// --- file: gov/llnl/rtk/physics/MaterialComponentReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
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
@Reader.Declaration(pkg = RtkPackage.class, name = "component",
        cls = MaterialComponent.class,
        order = Reader.Order.OPTIONS,
        referenceable = true)
@Reader.AnyAttribute(processContents = Reader.ProcessContents.Skip)
public class MaterialComponentReader extends ObjectReader<MaterialComponent>
{

  @Override
  public MaterialComponent start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    MaterialComponentImpl entry = new MaterialComponentImpl();
    String nuclideName = attributes.getValue("nuclide");
    Nuclide nuclide = Nuclides.get(nuclideName);
    if (nuclide == null)
      throw new ReaderException("Unable to find nuclide for "+ nuclideName);
    entry.setNuclide(nuclide);
    return entry;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<MaterialComponentImpl> rb = newBuilder(MaterialComponentImpl.class);
    rb.element("doseFraction").callDouble(MaterialComponentImpl::setDoseFraction).optional();
    rb.element("atomFraction").callDouble(MaterialComponentImpl::setAtomFraction).optional();
    rb.element("massFraction").callDouble(MaterialComponentImpl::setMassFraction).optional();
    rb.element("activity")
            .reader(new QuantityReader())
            .call(MaterialComponentImpl::setActivity).optional();
    return rb.getHandlers();
  }

}
