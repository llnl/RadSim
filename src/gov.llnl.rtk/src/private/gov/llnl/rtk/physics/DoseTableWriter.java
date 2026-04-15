// --- file: gov/llnl/rtk/physics/DoseTableWriter.java ---
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
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;
import java.util.Map;

/**
 * Debugging class for trying to see what is stored in the dose table.
 *
 * @author nelson85
 */
@Internal
public class DoseTableWriter extends ObjectWriter<DoseTable>
{
  public DoseTableWriter()
  {
    super(Options.NONE, "doseTable", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, DoseTable object) throws WriterException
  {
  }

  @Override
  public void contents(DoseTable object) throws WriterException
  {
    DoseTableImpl impl = (DoseTableImpl) object;
    WriterBuilder builder = newBuilder();
    builder.element("version").putInteger(object.getVersion());
    WriteObject<InterpolationTable> wo = builder.writer(new InterpolationTableWriter());
    for (Map.Entry<String, InterpolationTable> entry : impl.tables_.entrySet())
    {
      wo.put(entry.getValue());//.id(entry.getKey());
    }
  }

  public class InterpolationTableWriter extends ObjectWriter<InterpolationTable>
  {
    public InterpolationTableWriter()
    {
      super(Options.NONE, "interpolationTable", RtkPackage.getInstance());
    }

    @Override
    public void attributes(WriterAttributes attributes, InterpolationTable object) throws WriterException
    {
    }

    @Override
    public void contents(InterpolationTable table) throws WriterException
    {
      WriterBuilder builder = newBuilder();
      builder.element("nuclide").putString(table.nuclide);
//      builder.element("atomicNumber").putInteger(table.atomicNumber);
//      builder.element("mass1").putInteger(table.mass1);
//      builder.element("mass2").putInteger(table.mass2);

      // FIXME this code was not tested after conversion
      builder.element("z").putContents(table.Z_list);
      builder.element("ad").putContents(table.AD_list);

      builder.element("values").putContents(table.values);
      builder.element("slopes").putContents(table.slopes);
    }
  }
}
