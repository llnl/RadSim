// --- file: gov/llnl/rtk/model/DefaultResolutionModelReader.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.model.DefaultResolutionModel;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "defaultResolutionModel",
        cls = DefaultResolutionModel.class)
@Reader.Attribute(name = "fwhm0", type = double.class)
@Reader.Attribute(name = "fwhm", type = double.class)
@Reader.Attribute(name = "energy", type = double.class)
@Reader.Attribute(name = "power", type = double.class)
public class DefaultResolutionModelReader extends ObjectReader<DefaultResolutionModel>
{
  @Override
  public DefaultResolutionModel start(ReaderContext context, Attributes attr)
  {
    // These defaults are for NaI.  They may be forced to be required.
    double fwhm0 = getAttribute(attr, "fwhm0", Double.class, 1.0);
    double fwhm = getAttribute(attr, "fwhm", Double.class, 0.8 * 661.5);
    double energy = getAttribute(attr, "energy", Double.class, 661.5);
    double power = getAttribute(attr, "power", Double.class, 0.6);
    return DefaultResolutionModel.createFromMeasurement(fwhm0, fwhm, energy, power);
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return super.getHandlers(null);
  }

}
