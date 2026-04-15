// --- file: gov/llnl/rtk/data/SpectraListFilter.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.utility.annotation.Matlab;
import gov.llnl.utility.xml.bind.Reader;
import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "spectraListFilter",
        referenceable = true)
public class SpectraListFilter
{
  private int limit = -1;

  public interface FieldExtractor
  {
    Object get(Spectrum spectrum, EnergyRegionOfInterest roi);
  }

  public enum Field implements FieldExtractor
  {
    Z((s, roi) -> getZ(s.getTitle())),
    AD((s, roi) -> getAD(s.getTitle())),
    COUNTS((s, roi) -> s.getCounts(roi)),
    RATE((s, roi) -> s.getRate(roi)),
    GAMMA_FLUX_LINES((s, roi) -> s.getAttribute(SpectrumAttributes.GAMMA_FLUX_UNCOLLIDED, Quantity.class).get()),
    GAMMA_FLUX_TOTAL((s, roi) -> s.getAttribute(SpectrumAttributes.GAMMA_FLUX, Quantity.class).get()),
    GAMMA_FLUX_LINE_FRACTION(SpectraListFilter::evalFluxFraction),
    COUNT_FRACTION(SpectraListFilter::evalCountFraction),
    TITLE((s, roi) -> s.getTitle());

    private FieldExtractor extractor;

    Field(FieldExtractor ext)
    {
      this.extractor = ext;
    }

    @Override
    public Object get(Spectrum spectrum, EnergyRegionOfInterest roi)
    {
      return extractor.get(spectrum, roi);
    }

  }

  public enum Operation
  {
    // double operations
    GT((p1, p2) -> ((double) p1) > ((double) p2)),
    LT((p1, p2) -> ((double) p1) < ((double) p2)),
    GE((p1, p2) -> ((double) p1) >= ((double) p2)),
    EQ((p1, p2) -> ((double) p1) == ((double) p2)),
    NE((p1, p2) -> ((double) p1) != ((double) p2)),
    LE((p1, p2) -> ((double) p1) <= ((double) p2)),
    // String operations
    RE_CONTAINS((p1, p2) -> evalContains((String) p1, (String) p2)),
    RE_MATCHES((p1, p2) -> evalContains((String) p1, (String) p2));

    BiPredicate operation;

    Operation(BiPredicate operation)
    {
      this.operation = operation;
    }

    boolean eval(Object p1, Object p2)
    {
      return operation.test(p1, p2);
    }
  }

  ArrayList<Rule> rules = new ArrayList<>();

  public SpectraListFilter()
  {
  }

  @Reader.Element(name = "rule")
  public void addRule(RuleImpl rule)
  {
    this.rules.add(rule);
  }

  @Reader.Element(name = "include")
  public void addInclude(RuleImpl rule)
  {
    this.rules.add(rule);
  }

  @Reader.Element(name = "exclude")
  public void addExclude(RuleImpl rule)
  {
    this.rules.add((s) -> !rule.evaluate(s));
  }

  @Matlab
  public RuleImpl createRule(String field, String op, double value)
  {
    Field f = Field.valueOf(field.toUpperCase());
    Operation o = Operation.valueOf(op.toUpperCase());
    RuleImpl rule = new RuleImpl(f, o, value);
    this.rules.add(rule);
    return rule;
  }

  public DoubleSpectraList filter(DoubleSpectraList list)
  {
    int items = 0;
    DoubleSpectraList out = new DoubleSpectraList();
    for (DoubleSpectrum entry : list)
    {
      if (this.matches(entry) == true)
      {
        out.add(entry);
        items++;
        if (limit > 0 && items >= limit)
          return out;
      }
    }
    return out;
  }

  public boolean matches(Spectrum spectrum)
  {
    for (Rule rule : rules)
      if (rule.evaluate(spectrum) == false)
        return false;
    return true;
  }

  /**
   * @param limit the limit to set
   */
  @Reader.Attribute(name = "limit")
  public void setLimit(int limit)
  {
    this.limit = limit;
  }

  static public interface Rule
  {
    public boolean evaluate(Spectrum spectrum);
  }

  @Reader.Declaration(pkg = RtkPackage.class, name = "rule", referenceable = true)
  static public class RuleImpl implements Rule
  {
    Field field;
    Operation op;
    Object value;
    EnergyRegionOfInterest roi;

    public RuleImpl()
    {
    }

    public RuleImpl(Field field, Operation op, double value)
    {
      this.field = field;
      this.op = op;
      this.value = value;
    }

    @Reader.Attribute(name = "field", required = true)
    public void setField(Field field)
    {
      this.field = field;
    }

    @Reader.Attribute(name = "op")
    public void setOperation(Operation op)
    {
      this.op = op;
    }

    @Reader.Attribute(name = "value")
    public void setValue(double value)
    {
      this.value = value;
    }

    @Reader.Attribute(name = "contains")
    public void setPattern(String value)
    {
      this.op = Operation.RE_CONTAINS;
      this.value = value;
    }

    @Reader.Attribute(name = "matches")
    public void setMatches(String value)
    {
      this.op = Operation.RE_MATCHES;
      this.value = value;
    }

    @Reader.Element(name = "roi")
    public void setEnergyRegionOfInterest(EnergyRegionOfInterest roi)
    {
      this.roi = roi;
    }

    public boolean evaluate(Spectrum spectrum)
    {
      Object measured = field.get(spectrum, roi);

      // if not set assume true
      if (measured == null)
        return true;

      return op.eval(measured, value);
    }
  }

//<editor-fold desc="internal">
  static Double getZ(String str)
  {
    if (str == null)
      return null;
    // String to be scanned to find the pattern.
    String pattern = "\\{(\\S*),\\S*\\}";

    // Create a Pattern object
    Pattern r = Pattern.compile(pattern);

    // Now create matcher object.
    Matcher m = r.matcher(str);
    if (!m.find())
      return null;

    return Double.parseDouble(m.group(1));
  }

  static Double getAD(String str)
  {
    if (str == null)
      return null;

    // String to be scanned to find the pattern.
    String pattern = "\\{\\S*,(\\S*)\\}";

    // Create a Pattern object
    Pattern r = Pattern.compile(pattern);

    // Now create matcher object.
    Matcher m = r.matcher(str);
    if (!m.find())
      return null;

    return Double.parseDouble(m.group(1));
  }

  static Double evalFluxFraction(Spectrum s, EnergyRegionOfInterest roi)
  {
    try
    {
      double lines = s.getAttribute(SpectrumAttributes.GAMMA_FLUX_UNCOLLIDED, Quantity.class).get();
      double total = s.getAttribute(SpectrumAttributes.GAMMA_FLUX, Quantity.class).get();
      return lines / total;
    }
    catch (NullPointerException ex)
    {
      @Deprecated
      double lines = s.getAttribute(SpectrumAttributes.GAMMA_FLUX_LINES, Quantity.class).get();
      double total = s.getAttribute(SpectrumAttributes.GAMMA_FLUX_TOTAL, Quantity.class).get();
      return lines / total;
    }
  }

  static Double evalCountFraction(Spectrum s, EnergyRegionOfInterest roi)
  {
    double lines = s.getCounts(roi);
    double total = s.getCounts();
    return lines / total;
  }

  static boolean evalContains(String value, String pattern)
  {
    Pattern p = Pattern.compile(pattern);
    return p.matcher(value).find();
  }

  static boolean evalMatches(String value, String pattern)
  {
    Pattern p = Pattern.compile(pattern);
    return p.matcher(value).matches();
  }
//</editor-fold>
}
