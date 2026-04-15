// --- file: gov/llnl/rtk/data/SpectrumAttributes.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.utility.xml.bind.Reader;
import java.time.Instant;

/**
 * This is a holder class that will define all of the attributes supported by
 * the different file types.
 *
 * @author nelson85
 */
public class SpectrumAttributes
{
  public static final String URN = RtkPackage.getInstance().getNamespaceURI();
  // Common
  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "title", type = String.class)
  public static final String TITLE = URN + "#title"; // String

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "timestamp", type = Instant.class)
  public static final String TIMESTAMP = URN + "#timestamp";  // Instant

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "neutrons", type = double.class)
  public static final String NEUTRONS = URN + "#neutrons"; // Double

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "energyPolynomial", type = double[].class)
  public static final String ENERGY_POLYNOMIAL = URN + "#energyPolynomial"; // EnergyPolynomial

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "gammaFluxUncollided", type = Quantity.class)
  public static final String GAMMA_FLUX_UNCOLLIDED = URN + "#gammaFluxUncollided"; // Quantity

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "gammaFlux", type = Quantity.class)
  public static final String GAMMA_FLUX = URN + "#gammaFlux"; // Quantity

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "gammaDose", type = Quantity.class)
  public static final String GAMMA_DOSE = URN + "#gammaDose"; // Quantity

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "neutronFlux", type = Quantity.class)
  public static final String NEUTRON_FLUX = URN + "#neutronFlux"; // Quantity

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "neutronDose", type = Quantity.class)
  public static final String NEUTRON_DOSE = URN + "#neutronDose"; // Quantity

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "temperature", type = Quantity.class)
  public static final String TEMPERATURE = URN + "#temperature"; // Double (C)

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "highVoltage", type = Quantity.class)
  public static final String HIGH_VOLTAGE = URN + "#highVoltage"; // Double (V)

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "distance", type = Quantity.class)
  public static final String DISTANCE = URN + "#distance"; // Double (m)

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "gammaLeakage", type = Quantity.class)
  public static final String GAMMA_LEAKAGE = URN + "#gammaLeakage"; // Quantity

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "neutronLeakage", type = Quantity.class)
  public static final String NEUTRON_LEAKAGE = URN + "#neutronLeakage"; // Quantity

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "powerLaw", type = double.class)
  public static final String POWER_LAW = URN + "#powerLaw"; // Double

  /**
   * Option to writer to prevent fields from being written
   */
  @Deprecated

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "gammaFluxLines", type = Quantity.class)
  public static final String GAMMA_FLUX_LINES = URN + "#gammaFluxLines"; // Quantity

  @Deprecated

  @Reader.ElementDeclaration(pkg = RtkPackage.class, name = "gammaFluxTotal", type = Quantity.class)
  public static final String GAMMA_FLUX_TOTAL = URN + "#gammaFluxTotal"; // Quantity

}
