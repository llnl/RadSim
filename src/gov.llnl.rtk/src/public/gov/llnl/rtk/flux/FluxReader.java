// --- file: gov/llnl/rtk/flux/FluxReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.physics.SourceModelReader;
import gov.llnl.utility.Expandable;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import java.io.Serializable;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "flux", order = Reader.Order.OPTIONS,
        cls = Flux.class,
        document = true,
        referenceable = true)
@Reader.AnyAttribute(processContents = Reader.ProcessContents.Skip)
public class FluxReader extends ObjectReader<Flux>
{

  @Override
  public Flux start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    FluxBinned object = new FluxBinned();
    return object;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<FluxBinned> builder = this.newBuilder(FluxBinned.class);
    builder.section(new AttributesSection());
    builder.element("model").reader(new SourceModelReader())
            .call((p, v) -> p.setAttribute(FluxAttributes.MODEL, v));
    builder.element("gammaLines")
            .callContext(FluxReader::setGammaLines, double[][].class);
    builder.element("gammaGroups")
            .callContext(FluxReader::setGammaGroups, double[][].class);
    builder.element("neutronGroups")
            .callContext(FluxReader::setNeutronGroups, double[][].class);
    return builder.getHandlers();
  }

  private static void setGammaLines(ReaderContext context, FluxBinned flux, double[][] values)
  {
    for (double[] v : values)
    {
      switch (v.length)
      {
        case 2:
          flux.addPhotonLine(new FluxLineStep(v[0], v[1], 0));
          break;
        case 3:
          flux.addPhotonLine(new FluxLineStep(v[0], v[1], v[2]));
          break;
        case 4:
          flux.addPhotonLine(new FluxLineZAD(v[0], v[1], v[2], v[3]));
          break;
        default:
          throw new UnsupportedOperationException();
      }
    }
  }

  /**
   * Used by the loader.
   *
   * @param values
   */
  private static void setGammaGroups(ReaderContext context, FluxBinned flux, double[][] values)
  {
    for (int i = 0; i < values.length - 1; ++i)
      flux.addPhotonGroup(new FluxGroupBin(values[i][0], values[i + 1][0], values[i][1]));
  }

  /**
   * Used by the loader
   *
   * @param values
   */
  private static void setNeutronGroups(ReaderContext context, FluxBinned flux, double[][] values)
  {
    for (int i = 0; i < values.length - 1; ++i)
      flux.addNeutronGroup(new FluxGroupBin(values[i][0], values[i + 1][0], values[i][1]));
  }

  class AttributesSection extends Section
  {
    public AttributesSection()
    {
      super(Order.FREE, "attributes");
    }

    @Override
    public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
    {
      ReaderBuilder<Flux> builder = this.newBuilder();
      builder.any(Object.class)
              .callContext(FluxReader::setAttribute).optional()
              .options(Option.ANY_ALL);
      return builder.getHandlers();
    }
  }

  private static void setAttribute(ReaderContext context, Flux flux, Object object)
  {
    ReaderContext.ElementContext hc = context.getChildContext();
    String namespace = hc.getNamespaceURI();
    String localname = hc.getLocalName();
    if (namespace == null)
      namespace = "";
    ((Expandable) flux).setAttribute(String.format("%s#%s", namespace, localname), (Serializable) object);
  }
}
