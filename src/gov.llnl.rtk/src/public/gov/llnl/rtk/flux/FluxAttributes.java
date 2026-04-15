// --- file: gov/llnl/rtk/flux/FluxAttributes.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author nelson85
 */
public class FluxAttributes
{
  public static final String URN = RtkPackage.getInstance().getNamespaceURI();
  // Common
  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "model", type = String.class)
  public static final String MODEL = URN + "#model"; // String

}
