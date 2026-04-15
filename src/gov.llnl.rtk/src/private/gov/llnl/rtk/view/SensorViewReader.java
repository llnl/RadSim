// --- file: gov/llnl/rtk/view/SensorViewReader.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.view.SensorView;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.PolymorphicReader;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "sensorView",
        referenceable = true, cls = SensorView.class)
public class SensorViewReader extends PolymorphicReader<SensorView>
{
  
  @Override
  public ObjectReader<? extends SensorView>[] getReaders() throws ReaderException
  {
    return group(new SensorFaceSmallReader(),
            new SensorFaceRectangularReader(),
            new SensorFaceRectangularCollimatedReader(),
            new SensorViewCompositeReader());
  }

}
