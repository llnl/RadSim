// --- file: gov/llnl/rtk/data/AttributesWriter.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.utility.Expandable;
import gov.llnl.utility.PackageResource;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;
import gov.llnl.utility.xml.bind.SchemaManager;
import java.io.Serializable;
import java.util.function.Predicate;

/**
 *
 * @author nelson85
 */
public class AttributesWriter extends ObjectWriter<Expandable>
{
  public static final String WRITER_EXCLUDE = "AttributesWriter#exclude"; // Predicate

  public AttributesWriter()
  {
    super(Options.NONE, "attributes", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, Expandable object) throws WriterException
  {
  }

  @Override
  @SuppressWarnings(value = "unchecked")
  public void contents(Expandable object) throws WriterException
  {
    Predicate<String> exclude = this.getContext().getProperty(AttributesWriter.WRITER_EXCLUDE, Predicate.class, null);
    WriterBuilder wb = newBuilder();
    for (String keys : object.getAttributes().keySet())
    {
      if (exclude != null && exclude.test(keys))
        continue;
      String[] tokens = keys.split("#");
      if (tokens.length < 2)
        continue;
      PackageResource pkg = SchemaManager.getInstance().findPackage(tokens[0]);

      // Skip tags without a schema
      if (pkg == null)
        continue;

      String localName = tokens[1];
      Serializable value = object.getAttribute(keys);
      if (value == null)
        continue;
      if (value instanceof Quantity)
        wb.element(pkg, localName).contents(Quantity.class).put(value);
      else
        wb.element(pkg, localName).putContents(value);
    }
  }

}
