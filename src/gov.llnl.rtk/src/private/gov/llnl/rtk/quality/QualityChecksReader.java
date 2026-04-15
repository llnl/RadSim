// --- file: gov/llnl/rtk/quality/QualityChecksReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.quality.QualityCheck;
import gov.llnl.rtk.quality.QualityChecks;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author seilhan3
 */
@Internal
@Reader.Declaration(pkg = RtkPackage.class, name = "QualityChecks",
        cls = QualityChecks.class,
        order = Reader.Order.FREE,
        referenceable = true)
public class QualityChecksReader extends ObjectReader<QualityChecks>
{

  public QualityChecksReader()
  {
  }

  @Override
  public QualityChecks start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new QualityChecks();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<QualityChecks> builder = newBuilder();
    builder.any(QualityCheck.class)
            .call(QualityChecksReader::add);
    return builder.getHandlers();
  }

  static void add(QualityChecks obj, QualityCheck qc)
  {
    obj.add(qc);
  }

}
