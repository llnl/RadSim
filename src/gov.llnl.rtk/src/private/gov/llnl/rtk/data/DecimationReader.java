// --- file: gov/llnl/rtk/data/DecimationReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.IntegerArray;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.ArrayEncoding;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectStringReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import java.text.ParseException;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Internal
@Reader.Declaration(pkg = RtkPackage.class, name = "decimation",
        cls = Decimation.class,
        referenceable = true,
        contents = Reader.Contents.TEXT)
@Reader.Attribute(name = "type", required = true)
public class DecimationReader extends ObjectStringReader<Decimation>
{

  @Override
  public Decimation start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new Decimation();
  }

  @Override
  public Decimation contents(ReaderContext context, String textContents)
          throws ReaderException
  {
    try
    {
      String type = context.getCurrentContext().getAttributes().getValue("type");
      if (type.equals("energy"))
        getObject(context).setTargetEnergyEdges(ArrayEncoding.decodeDoubles(textContents));
      else if (type.equals("bins"))
        getObject(context).setChannelEdges(IntegerArray.fromString(textContents));
      return null;
    }
    catch (ParseException ex)
    {
      throw new ReaderException(ex);
    }
  }

}
