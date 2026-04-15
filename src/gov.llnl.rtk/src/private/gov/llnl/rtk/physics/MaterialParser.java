// --- file: gov/llnl/rtk/physics/MaterialParser.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author nelson85
 */
class MaterialParser
{
  static Pattern NUCLIDE = Pattern.compile("^([A-Z][a-z]?)(-[0-9]+)?");
  static Pattern FRACTION = Pattern.compile("^:?([0-9]+(\\.[0-9+])?)");

  /**
   * Parse the material description.
   *
   * @param str
   * @return
   */
  static Material parse(String str)
  {
    Pair first = null;
    Pair last = null;
    LinkedList<Pair> stack = new LinkedList<>();
    for (Token t : tokenize(str))
    {
      switch (t.type)
      {
        case SPECIFIER:
          Pair next = new Pair((Nuclide) t.value);
          if (first == null)
            first = next;
          if (last != null)
            last.next = next;
          last = next;
          continue;

        case QUANITY:
          if (last == null)
            throw new RuntimeException("missing last");
          last.scale((Double) t.value);
          continue;

        case GROUP_START:
          stack.add(first);
          stack.add(last);
          first = null;
          last = null;
          continue;

        case GROUP_END:
          if (stack.isEmpty())
            throw new RuntimeException("missing group");
          Pair current = first;
          last = stack.removeLast();
          first = stack.removeLast();
          last.next = current;
          last = current;
          if (first == null)
            first = last;
          continue;

        default:
          throw new RuntimeException("bad token");
      }
    }

    if (!stack.isEmpty())
      throw new RuntimeException("incomplete group");
    if (first == null)
      throw new RuntimeException("null specification");

    HashMap<Nuclide, Pair> pairs = new HashMap<>();
    while (first != null)
    {
      Pair prev = pairs.get(first.nuclide);
      if (prev == null)
      {
        prev = new Pair(first.nuclide);
        prev.atoms = 0;
        pairs.put(first.nuclide, prev);
      }
      prev.atoms += first.atoms;
      first = first.next;
    }

    double atoms = 0;
    double mass = 0;
    for (var pair : pairs.values())
    {
      Nuclide nuclide = pair.nuclide;
      pair.mass = pair.atoms * nuclide.getAtomicMass();
      mass += pair.mass;
      atoms += pair.atoms;
    }

    MaterialImpl material = new MaterialImpl();
    for (var pair : pairs.values())
    {
      MaterialComponentImpl component = new MaterialComponentImpl();
      component.nuclide = pair.nuclide;
      component.atomFraction = pair.atoms / atoms;
      component.massFraction = pair.mass / mass;
      material.addEntry(component);
    }
    material.normalize();
    return material;
  }

  /**
   * Simple tokenizer for a material specification string.
   *
   * @param str
   * @return
   */
  static List<Token> tokenize(String str)
  {
    Matcher nuclideMatcher = NUCLIDE.matcher(str);
    Matcher fractionMatcher = FRACTION.matcher(str);
    int i = 0;
    int len = str.length();
    List tokens = new ArrayList<>();
    while (i < len)
    {
      if (str.charAt(i) == '(')
      {
        tokens.add(new Token(TokenType.GROUP_START));
        i++;
        continue;
      }
      if (str.charAt(i) == ')')
      {
        tokens.add(new Token(TokenType.GROUP_END));
        i++;
        continue;
      }
      if (Character.isSpaceChar(str.charAt(i)))
      {
        i++;
        continue;
      }

      nuclideMatcher.region(i, str.length());
      if (nuclideMatcher.lookingAt())
      {
        if (nuclideMatcher.group(2) == null)
        {
          Element element = Elements.get(nuclideMatcher.group());
          if (element == null)
            throw new RuntimeException("Unknown element " + nuclideMatcher.group());
          tokens.add(new Token(TokenType.SPECIFIER, Nuclides.natural(element)));
        }
        else
          tokens.add(new Token(TokenType.SPECIFIER, Nuclides.get(nuclideMatcher.group())));
        i = nuclideMatcher.end();
        continue;
      }

      fractionMatcher.region(i, str.length());
      if (fractionMatcher.lookingAt())
      {
        tokens.add(new Token(TokenType.QUANITY, Double.valueOf(fractionMatcher.group(1))));
        i = fractionMatcher.end();
        continue;
      }
      throw new RuntimeException("Bad parse " + i + " " + str.substring(i));
    }
    return tokens;
  }

  enum TokenType
  {
    SPECIFIER,
    QUANITY,
    GROUP_START,
    GROUP_END
  }

  static class Pair
  {
    final Nuclide nuclide;
    double atoms = 1;
    double mass = 0;
    Pair next = null;

    public Pair(Nuclide nuclide)
    {
      this.nuclide = nuclide;
    }

    void scale(double value)
    {
      this.atoms *= value;
      if (this.next != null)
      {
        next.scale(value);
      }
    }

    @Override
    public String toString()
    {
      return String.format("(%s,%f)", this.nuclide, this.atoms);
    }
  }

  static class Token
  {
    TokenType type;
    Object value;

    private Token(TokenType type)
    {
      this.type = type;
      this.value = null;
    }

    private Token(TokenType type, Object value)
    {
      this.type = type;
      this.value = value;
    }

    @Override
    public String toString()
    {
      if (value == null)
        return String.format("%s", type);
      return String.format("%s,%s", type, value);
    }
  }
}
