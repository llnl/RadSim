// --- file: gov/llnl/rtk/physics/DoseTableReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.physics.DoseTableImpl.InterpolationTable;
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
@Reader.Declaration(pkg = RtkPackage.class, name = "doseTable",
        cls = DoseTable.class,
        referenceable = true, order = Reader.Order.SEQUENCE)
public class DoseTableReader extends ObjectReader<DoseTable>
{
  @Override
  public DoseTable start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    DoseTableImpl obj = new DoseTableImpl();
    obj.tables_.clear();
    return obj;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<DoseTableImpl> builder = newBuilder(DoseTableImpl.class);
    builder.element("version").callInteger(DoseTableImpl::setVersion);
    builder.element("interpolationTable")
            .reader(new InterpolationTableReader())
            .call(DoseTableImpl::add)
            .unbounded();
    return builder.getHandlers();
  }

  @Reader.Declaration(pkg = RtkPackage.class, name = "interpolationTable",
          order = Reader.Order.SEQUENCE, cls = InterpolationTable.class)
  public class InterpolationTableReader extends ObjectReader<InterpolationTable>
  {
    @Override
    public InterpolationTable start(ReaderContext context, Attributes attributes) throws ReaderException
    {
      return new InterpolationTable();
    }

    @Override
    public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
    {
      ReaderBuilder<InterpolationTable> builder = newBuilder(InterpolationTable.class);
      builder.element("nuclide").callString(InterpolationTable::setNuclide);
      builder.element("z").call(InterpolationTable::setZ_list, float[].class);
      builder.element("ad").call(InterpolationTable::setAD_list, float[].class);
      builder.element("values").call(InterpolationTable::setValues, float[][].class);
      builder.element("slopes").call(InterpolationTable::setSlopes, float[][].class);
      return builder.getHandlers();
    }

  }

}
