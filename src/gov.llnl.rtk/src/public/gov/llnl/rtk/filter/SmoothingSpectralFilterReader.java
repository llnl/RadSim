// --- file: gov/llnl/rtk/filter/SmoothingSpectralFilterReader.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.filter;

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
@Reader.Declaration(pkg = RtkPackage.class,
        name = "smoothingSpectralFilter",
        cls = SmoothingSpectralFilter.class,
        order = Reader.Order.OPTIONS)
@Reader.Attribute(name = "coef", type = double.class, required = true)
public class SmoothingSpectralFilterReader extends ObjectReader<SmoothingSpectralFilter>
{
  @Override
  public SmoothingSpectralFilter start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    Double coef = getAttribute(attributes, "coef", Double.class, 0.0);
    return SmoothingSpectralFilter.create(coef);
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return super.getHandlers(null);
  }

}
