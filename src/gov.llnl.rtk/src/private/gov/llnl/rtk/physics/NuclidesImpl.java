// --- file: gov/llnl/rtk/physics/NuclidesImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.proto.ProtoException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a singleton intended to insure that Nuclides with the same name
 * refer to the same object.
 *
 * @author nelson85
 */
public final class NuclidesImpl
{
  public final static NuclidesImpl INSTANCE;

  final HashMap<String, NuclideImpl> NUCLIDES_BY_NAME = new HashMap<>();
  final HashMap<Integer, NuclideImpl> NUCLIDES_BY_ID = new HashMap<>();
  final HashMap<String, ElementImpl> ELEMENTS_BY_NAME = new HashMap<>();
  final Element[] ELEMENT_TABLE_BY_MASS = new Element[200];

  final Pattern NUCLIDE_PATTERN1 = Pattern.compile("([0-9]+)-?([A-z])([A-z]?)([Mm]?[2-9]?)");
  final Pattern NUCLIDE_PATTERN2 = Pattern.compile("([A-z])([A-z]?)-?([0-9]+)([Mm]?[2-9]?)");
  final Pattern NUCLIDE_PATTERN4 = Pattern.compile("([A-z])([A-z]?)m([0-9]+)");
  final Pattern NUCLIDE_PATTERN3 = Pattern.compile("([A-z])([A-z]?)");

  static
  {
    INSTANCE = new NuclidesImpl();
    NuclidesEncoding encoding = new NuclidesEncoding();
    InputStream is = NuclidesImpl.class.getResourceAsStream("/gov/llnl/rtk/resources/nuclides.bin");
    try
    {
      encoding.parseStream(new BufferedInputStream(is));
    }
    catch (ProtoException ex)
    {
      Logger.getLogger(NuclidesImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public Collection<Nuclide> getNuclides()
  {
    return Collections.unmodifiableCollection(NUCLIDES_BY_NAME.values());
  }

  public Collection<Element> getElements()
  {
    return Collections.unmodifiableCollection(ELEMENTS_BY_NAME.values());
  }

  Element reserveElement(String symbol)
  {
    if (Character.isDigit(symbol.charAt(1)))
      symbol = symbol.substring(0, 1);
    else
      symbol = symbol.substring(0, 2);
    ElementImpl e = ELEMENTS_BY_NAME.get(symbol);
    if (e == null)
    {
      e = new ElementImpl(symbol);
      ELEMENTS_BY_NAME.put(symbol, e);
    }
    return e;
  }

  Element getElement(int z)
  {
    return ELEMENT_TABLE_BY_MASS[z];
  }

  Element getElement(String name)
  {
    Pattern pattern = Pattern.compile("([A-z][A-z]*?)([0-9]*)(m[0-9]?)?");
    Matcher match = pattern.matcher(name);
    if (!match.matches())
      throw new RuntimeException("Unable to parse element " + name);
    String s = match.group(1);
    if (s.length() > 1)
      s = new String(new char[]
      {
        s.charAt(0), Character.toLowerCase(s.charAt(1))
      });
    return ELEMENTS_BY_NAME.get(s);
  }

  void register(NuclideImpl nuclide)
  {
    nuclide.id = nuclide.getAtomicNumber() * 10000 + nuclide.getMassNumber() * 10 + nuclide.getIsomerNumber();
    NUCLIDES_BY_NAME.put(nuclide.name, nuclide);
    NUCLIDES_BY_ID.put(nuclide.id, nuclide);
  }

  void register(ElementImpl element)
  {
    ELEMENTS_BY_NAME.put(element.getSymbol(), element);
    ELEMENT_TABLE_BY_MASS[element.z] = element;
  }

//<editor-fold desc="loader" defaultstate="collapsed">
//  private class ElementDatabaseHandler extends DefaultHandler
//  {
//    List<ElementImpl> results = new ArrayList<>();
//    ElementImpl last = null;
//
//    @Override
//    public void startElement(String uri, String localName, String qName,
//            Attributes attributes) throws SAXException
//    {
//
//      if ("isotope".equals(qName))
//      {
//        String symbol = attributes.getValue("symbol");
//        String abundance = attributes.getValue("abundance");
//        last.abundance.add(new ComponentImpl(getByName(symbol), Double.parseDouble(abundance), 0, null));
//      }
//      if ("element".equals(qName))
//      {
//        String symbol = attributes.getValue("symbol");
//        String atomicNumber = attributes.getValue("atomicNumber");
//        String molarMass = attributes.getValue("molarMass");
//
//        if (symbol == null)
//          return;
//
//        ElementImpl element = ELEMENTS_BY_NAME.get(symbol.trim());
//        element.z = Integer.parseInt(atomicNumber);
//
//        if (molarMass != null)
//        {
//          element.molarMass = Double.parseDouble(molarMass.trim());
//        }
//        last = element;
//        results.add(element);
//      }
//    }
//
//    @Override
//    public void endElement(String uri, String localName,
//            String qName) throws SAXException
//    {
//      // not used
//    }
//
//    @Override
//    public void characters(char ch[], int start, int length) throws SAXException
//    {
//      // not used
//    }
//
//  }
//
//  static void loadElements(NuclidesImpl parent)
//  {
//    try (InputStream is = NuclidesImpl.class.getResourceAsStream("/gov/llnl/rtk/resources/elements.xml.gz"))
//    {
//      if (is == null)
//      {
//        throw new RuntimeException("Element library resource not found");
//      }
//      GZIPInputStream gis = new GZIPInputStream(is);
//      SAXParserFactory factory = SAXParserFactory.newInstance();
//      SAXParser saxParser = factory.newSAXParser();
//      ElementDatabaseHandler handler = parent.new ElementDatabaseHandler();
//      saxParser.parse(gis, handler);
//
//      for (var v : handler.results)
//      {
//        parent.ELEMENT_TABLE_BY_MASS[v.z] = v;
//      }
//    }
//    catch (SAXException | IOException | ParserConfigurationException ex)
//    {
//      Logger.getLogger(NuclidesImpl.class.getName()).log(Level.SEVERE, null, ex);
//    }
//  }
//</editor-fold>
  Nuclide getByName(String name)
  {
    if (name == null)
      return null;

    Nuclide nuclide = NUCLIDES_BY_NAME.get(name);
    if (nuclide != null)
      return nuclide;

    String desc;
    Matcher matcher = NUCLIDE_PATTERN1.matcher(name);
    if (matcher.matches())
    {
      desc = String.format("%s%s%s%s",
              matcher.group(2).toUpperCase(),
              matcher.group(3).toLowerCase(),
              matcher.group(1),
              matcher.group(4).toLowerCase());
      nuclide = NUCLIDES_BY_NAME.get(desc);
      if (nuclide != null)
        return nuclide;
      if (matcher.group(3).toLowerCase().equals("m"))
      {
        desc = String.format("%s%s%s%s",
                matcher.group(2).toUpperCase(),
                matcher.group(1),
                matcher.group(3).toLowerCase(),
                matcher.group(4).toLowerCase());
        nuclide = NUCLIDES_BY_NAME.get(desc);
        if (nuclide != null)
          return nuclide;
      }
    }

    matcher = NUCLIDE_PATTERN2.matcher(name);
    if (matcher.matches())
    {
      desc = String.format("%s%s%s%s",
              matcher.group(1).toUpperCase(),
              matcher.group(2).toLowerCase(),
              matcher.group(3),
              matcher.group(4).toLowerCase());
      nuclide = NUCLIDES_BY_NAME.get(desc);
      if (nuclide != null)
        return nuclide;
    }

    matcher = NUCLIDE_PATTERN3.matcher(name);
    if (matcher.matches())
      return natural(Elements.get(name));
    
    matcher = NUCLIDE_PATTERN4.matcher(name);
    if (matcher.matches())
    {
      desc = String.format("%s%s%s%s",
              matcher.group(1).toUpperCase(),
              matcher.group(2).toLowerCase(),
              matcher.group(3),
              "m");
      nuclide = NUCLIDES_BY_NAME.get(desc);
      if (nuclide != null)
        return nuclide;
    }

    return nuclide;
  }

  Nuclide getById(int id)
  {
    return NUCLIDES_BY_ID.get(id);
  }

  Nuclide natural(Element element)
  {
    if (element == null)
      return null;
    String name = element.getSymbol();
    NuclideImpl impl = new NuclideImpl(element.getSymbol(), element);
    impl.atomicMass = element.getMolarMass();
    impl.atomicNumber = element.getAtomicNumber();

    NUCLIDES_BY_NAME.put(name, impl);
    return impl;
  }

//  private class NuclideDatabaseHandler extends DefaultHandler
//  {
//
//    @Override
//    public void startElement(String uri, String localName, String qName,
//            Attributes attributes) throws SAXException
//    {
//      if ("nuclide".equals(qName))
//      {
//        String symbol = attributes.getValue("symbol");
//        String atomicNumber = attributes.getValue("atomicNumber");
//        String massNumber = attributes.getValue("massNumber");
//        String isomerNumber = attributes.getValue("isomerNumber");
//        String atomicMass = attributes.getValue("atomicMass");
//        String halfLife = attributes.getValue("halfLife");
//
//        if (symbol == null)
//          return;
//
//        Element element = reserveElement(symbol.trim());
//        NuclideImpl nuclide = new NuclideImpl(symbol, element);
//        if (atomicNumber != null)
//        {
//          int v = Integer.parseInt(atomicNumber.trim());
//          nuclide.atomicNumber = v;
//        }
//
//        if (massNumber != null)
//        {
//          int v = Integer.parseInt(massNumber.trim());
//          nuclide.massNumber = v;
//          nuclide.atomicMass = v;
//        }
//
//        if (isomerNumber != null)
//        {
//          int v = Integer.parseInt(isomerNumber.trim());
//          nuclide.isomerNumber = v;
//        }
//        if (atomicMass != null)
//        {
//          double v = Double.parseDouble(atomicMass.trim());
//          nuclide.atomicMass = v;
//        }
//
//        if (halfLife != null)
//        {
//          double v;
//          if ("INF".equals(halfLife))
//            v = Double.POSITIVE_INFINITY;
//          else
//            v = Double.parseDouble(halfLife.trim());
//          nuclide.halfLife = v;
//        }
//
//        nuclide.id = nuclide.getAtomicNumber() * 10000 + nuclide.getMassNumber() * 10 + nuclide.getIsomerNumber();
//        NUCLIDES_BY_NAME.put(nuclide.getName(), nuclide);
//        NUCLIDES_BY_ID.put(nuclide.id, nuclide);
//      }
//    }
//
//    @Override
//    public void endElement(String uri, String localName,
//            String qName) throws SAXException
//    {
//      // not used
//    }
//
//    @Override
//    public void characters(char ch[], int start, int length) throws SAXException
//    {
//      // not used
//    }
//
//  }
//
//  static void loadNuclides(NuclidesImpl parent)
//  {
//    try (InputStream is = NuclidesImpl.class.getResourceAsStream("/gov/llnl/rtk/resources/nuclides.xml.gz"))
//    {
//      if (is == null)
//      {
//        throw new RuntimeException("Nuclide library resource not found");
//      }
//      GZIPInputStream gis = new GZIPInputStream(is);
//      SAXParserFactory factory = SAXParserFactory.newInstance();
//      SAXParser saxParser = factory.newSAXParser();
//      NuclideDatabaseHandler handler = parent.new NuclideDatabaseHandler();
//      saxParser.parse(gis, handler);
//    }
//    catch (SAXException | IOException | ParserConfigurationException ex)
//    {
//      Logger.getLogger(NuclidesImpl.class.getName()).log(Level.SEVERE, null, ex);
//    }
//  }
}
