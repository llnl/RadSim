// --- file: gov/llnl/rtk/RtkPackage.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk;

import gov.llnl.math.MathPackage;
import gov.llnl.rtk.data.DoubleSpectraList;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.utility.PackageResource;
import gov.llnl.utility.UtilityPackage;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.DomUtilities;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.SchemaBuilder;
import gov.llnl.utility.xml.bind.Schema;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nelson85
 */
@Schema(namespace = "http://rtk.llnl.gov",
        schema = "http://rtk.llnl.gov/schema/rtk.xsd",
        prefix = "rtk")
@Schema.Using(UtilityPackage.class)
@Schema.Using(MathPackage.class)
public class RtkPackage extends PackageResource
{
  final static public RtkPackage SELF
          = new RtkPackage();
  final static public Logger LOGGER = Logger.getLogger("rtk");

  private RtkPackage()
  {
  }

  static public RtkPackage getInstance()
  {
    return SELF;
  }

  public static RtkPackage rtk()
  {
    return SELF;
  }

  public static Logger rtkLogger()
  {
    return LOGGER;
  }

  public static void main(String[] args) throws ReaderException, FileNotFoundException, IOException
  {
    UtilityPackage.getInstance().enableLog(Level.WARNING);
    SchemaBuilder sb = new SchemaBuilder();
//    sb.include("http://rtk.llnl.gov/schema/rtk_attrib.xsd");
//    sb.include("http://rtk.llnl.gov/schema/spectrum_attributes.xsd");
    sb.setTargetNamespace(SELF);
    sb.addObjectReader(ObjectReader.create(EnergyScale.class));
    sb.scanForReaders(Paths.get("src/public"));
    sb.scanForReaders(Paths.get("src/private"));
    sb.scanForReaders(Paths.get("build/generated-sources/ap-source-output/"));
    sb.alias(DoubleSpectraList.class, "spectraList");

    Path file = Paths.get(args[0], args[1]);
    try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(file)))
    {
      DomUtilities.printXml(os, sb.getDocument());
    }
    System.out.println("SchemaBuilder return "+sb.getError());
    System.exit(sb.getError());
  }

  public InputStream getResource(String name)
  {
    InputStream resource = this.getClass().getClassLoader().getResourceAsStream("gov/llnl/rtk/resources/" + name);
    return resource;
  }
}
