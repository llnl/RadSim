// --- file: gov/llnl/rtk/physics/NuclidesEncoding.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.proto.MessageEncoding;
import static gov.llnl.utility.proto.MessageEncoding.newBuilder;
import gov.llnl.utility.proto.ProtoField;
import java.util.ArrayList;

/**
 * Internal loader for nuclide and element data.
 *
 * This is a very complex loader which is interacting between the parent and
 * child through static methods. It may not be the best example to work from.
 *
 * FIXME it would be best if we could access the context for some parts of this
 * loader, but that feature does not exist in our utility library currently.
 *
 * @author nelson85
 */
public class NuclidesEncoding extends MessageEncoding<NuclidesImpl>
{
  static NuclidesImpl target;
  final static ProtoField[] NUCLIDE_FIELDS;
  final static ProtoField[] ELEMENT_FIELDS;
  final static ProtoField[] ABUNDANCE_FIELDS;
  final static ProtoField[] LIBRARY_FIELDS;

  static
  {
    // We will create encoding for the library and each of its pieces here.
    
    var nuclide_builder = newBuilder(null, "nuclide_data",
            NuclidesEncoding.NuclideData::new,
            NuclidesEncoding::convertNuclide);
    nuclide_builder.field("symbol", 1).type(Type.String)
            .as((o) -> o.getName(), (o, v) -> o.symbol = v);
    nuclide_builder.field("atomicNumber", 2).type(Type.Int32)
            .as((o) -> o.getAtomicNumber(), (o, v) -> o.atomicNumber = v);
    nuclide_builder.field("massNumber", 3).type(Type.Int32)
            .as((o) -> o.getMassNumber(), (o, v) -> o.massNumber = v);
    nuclide_builder.field("isomerNumber", 4).type(Type.Int32)
            .as((o) -> o.getIsomerNumber(), (o, v) -> o.isomerNumber = v);
    nuclide_builder.field("atomicMass", 5).type(Type.Double)
            .as((o) -> o.getAtomicMass(), (o, v) -> o.atomicMass = v);
    nuclide_builder.field("halfLife", 6).type(Type.Double)
            .as((o) -> o.getHalfLife(), (o, v) -> o.halfLife = v);
    NUCLIDE_FIELDS = nuclide_builder.toFields();

    var element_builder = newBuilder(null, "element_data",
            NuclidesEncoding.ElementData::new,
            NuclidesEncoding::convertElement);
    element_builder.field("symbol", 1).type(Type.String)
            .as((o) -> o.getSymbol(), (o, v) -> o.symbol = v);
    element_builder.field("atomicNumber", 2).type(Type.Int32)
            .as((o) -> o.getAtomicNumber(), (o, v) -> o.atomicNumber = v);
    element_builder.field("molarMass", 3).type(Type.Double)
            .as((o) -> o.molarMass, (o, v) -> o.molarMass = v);
    element_builder.field("density", 4).encoding(QuantityEncoding.INSTANCE)
            .as((o) -> o.density, (o, v) -> o.density = v);
    element_builder.field("abundance", 5).list(new AbundanceDataEncoding())
            .as((o) -> o.abundance, (o, v) -> o.abundance.addAll(v));
    ELEMENT_FIELDS = element_builder.toFields();

    var builder2 = newBuilder(null, "abundance_data",
            NuclidesEncoding.AbundanceData::new,
            NuclidesEncoding::convertAbundance);
    builder2.field("name", 1).type(Type.String)
            .as((o) -> o.nuclide.getName(), (o, v) -> o.nuclide = v);
    builder2.field("abundance", 2).type(Type.Double)
            .as((o) -> o.getMassFraction(), (o, v) -> o.abundance = v);
    ABUNDANCE_FIELDS = builder2.toFields();

    var library_builder = newBuilder(null, "nuclide_library", NuclidesEncoding::newLibrary);
    library_builder.field("nuclides", 1).list(new NuclideDataEncoding())
            .as((o) ->
            {
              ArrayList<NuclideImpl> a = new ArrayList<>(o.NUCLIDES_BY_ID.values());
              a.removeIf(p -> p.id == 0);
              a.sort((p1, p2) -> Integer.compare(p1.id, p2.id));
              return a;
            }, NuclidesEncoding::ignore);
    library_builder.field("elements", 2).list(new ElementDataEncoding())
            .as((o) ->
            {
              ArrayList<ElementImpl> a = new ArrayList<>(o.ELEMENTS_BY_NAME.values());
              a.removeIf(p -> p.z == 0);
              a.sort((p1, p2) -> Integer.compare(p1.z, p2.z));
              return a;
            }, NuclidesEncoding::ignore);
    LIBRARY_FIELDS = library_builder.toFields();
  }

  @Override
  public ProtoField[] getFields()
  {
    return LIBRARY_FIELDS;
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  static NuclidesImpl newLibrary()
  {
    target = NuclidesImpl.INSTANCE;
    return target;
  }

  static NuclideImpl convertNuclide(NuclideData data)
  {
    Element element = target.reserveElement(data.symbol);
    NuclideImpl out = new NuclideImpl(data.symbol, element);
    out.atomicMass = data.atomicMass;
    out.atomicNumber = data.atomicNumber;
    out.halfLife = data.halfLife;
    out.isomerNumber = data.isomerNumber;
    out.massNumber = data.massNumber;
    target.register(out);
    return out;
  }

  public static ElementImpl convertElement(ElementData data)
  {
    ElementImpl element = NuclidesImpl.INSTANCE.ELEMENTS_BY_NAME.get(data.symbol);
    element.z = data.atomicNumber;
    element.density = data.density;
    element.molarMass = data.molarMass;
    final double total = data.abundance.stream().mapToDouble(p -> p.massFraction).sum();
    data.abundance.stream().forEach(p -> p.massFraction /= total);
    element.abundance.addAll(data.abundance);
    target.register(element);
    return element;
  }

  static MaterialComponentImpl convertAbundance(AbundanceData data)
  {
    MaterialComponentImpl out = new MaterialComponentImpl();
    out.nuclide = Nuclides.get(data.nuclide);
    out.atomFraction = data.abundance;
    out.massFraction = data.abundance * out.nuclide.getAtomicMass();
    return out;
  }

  private static void ignore(Object o, Object v)
  {
  }

  public static class NuclideData
  {
    String symbol;
    int atomicNumber;
    int massNumber;
    int isomerNumber;
    double atomicMass;
    double halfLife;
  }

  public static class ElementData
  {
    String symbol;
    int atomicNumber;
    double molarMass;
    Quantity density;
    ArrayList<MaterialComponentImpl> abundance = new ArrayList<>();
  }

  public static class AbundanceData
  {
    String nuclide;
    double abundance;
  }

  private static class NuclideDataEncoding extends MessageEncoding<NuclideImpl>
  {
    @Override
    public ProtoField[] getFields()
    {
      return NUCLIDE_FIELDS;
    }
  }

  private static class ElementDataEncoding extends MessageEncoding<ElementImpl>
  {
    @Override
    public ProtoField[] getFields()
    {
      return ELEMENT_FIELDS;
    }
  }

  private static class AbundanceDataEncoding extends MessageEncoding<MaterialComponentImpl>
  {
    @Override
    public ProtoField[] getFields()
    {
      return ABUNDANCE_FIELDS;
    }
  }
//</editor-fold>
}
