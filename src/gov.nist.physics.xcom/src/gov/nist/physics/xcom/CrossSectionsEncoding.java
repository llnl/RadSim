/*
 * Copyright 2025, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xcom;

import gov.llnl.rtk.physics.Element;
import gov.llnl.rtk.physics.Elements;
import gov.llnl.rtk.physics.Materials;
import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * Representation of cross sections in storage.
 *
 * The class is public to assist in regeneration of tables.
 *
 * @author nelson85
 */
public class CrossSectionsEncoding extends MessageEncoding<CrossSectionsTable>
{

  final static CrossSectionsEncoding INSTANCE = new CrossSectionsEncoding();
  final static ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "cross_sections", CrossSectionsTable::new, CrossSectionsEncoding::convert);
    builder.field("name", 1).type(Type.String)
            .as((o) -> o.name, (o, v) -> o.name = v);
    builder.field("symbol", 2).type(Type.String)
            .as((o) -> o.symbol, (o, v) -> o.symbol = v);

    builder.field("atomic_number", 3).type(Type.Double)
            .as((o) -> o.atomicNumber, (o, v) -> o.atomicNumber = v);
    builder.field("molar_mass", 4).type(Type.Double)
            .as((o) -> o.molarMass, (o, v) -> o.molarMass = v);

    builder.field("energy", 5).type(Type.NetworkDoubles)
            .as((o) -> o.energies, (o, v) -> o.energies = v);
    builder.field("photoelectric", 6).type(Type.NetworkDoubles)
            .as((o) -> o.photoelectric, (o, v) -> o.photoelectric = v);
    builder.field("incoherent", 7).type(Type.NetworkDoubles)
            .as((o) -> o.incoherent, (o, v) -> o.incoherent = v);
    builder.field("pair_nuclear", 8).type(Type.NetworkDoubles)
            .as((o) -> o.pairNuclear, (o, v) -> o.pairNuclear = v);
    builder.field("pair_electron", 9).type(Type.NetworkDoubles)
            .as((o) -> o.pairElectron, (o, v) -> o.pairElectron = v);
    builder.field("total", 10).type(Type.NetworkDoubles)
            .as((o) -> o.total, (o, v) -> o.total = v);
    FIELDS = builder.toFields();
  }

  private static CrossSectionsTable convert(CrossSectionsTable cs)
  {
    int an = (int) cs.atomicNumber;
    Element elem = Elements.getElement(an);
    cs.material = Materials.pure(elem);
    return cs;
  }

  public static CrossSectionsEncoding getInstance()
  {
    return INSTANCE;
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

}

//message CrossSectionsProto
//{
//  string name = 1;
//  string symbol = 2;
//  double atomic_number = 3;
//  double molar_mass = 4;
//  bytes energy = 5;
//  bytes photoelectric = 6;
//  bytes incoherent = 7;
//  bytes pair_nuclear = 8;
//  bytes pair_electron = 9;
//  bytes total = 10;
//}
