// --- file: gov/llnl/rtk/flux/FluxWriter.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.rtk.data.*;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.physics.SourceModel;
import gov.llnl.rtk.physics.SourceModelWriter;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;
import java.util.function.Predicate;

/**
 *
 * @author nelson85

 */
public class FluxWriter extends ObjectWriter<Flux>
{
  public FluxWriter()
  {
    super(Options.NONE, "flux", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, Flux object) throws WriterException
  {

  }

  @Override
  @SuppressWarnings("unchecked")
  public void contents(Flux object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    Predicate<String> exclude = (s)-> s.equals(FluxAttributes.MODEL);
    this.getContext().setProperty(AttributesWriter.WRITER_EXCLUDE, exclude);
    if (!object.getAttributes().isEmpty())
      wb.element("attributes").writer(new AttributesWriter()).put(object);
    if (object.hasAttribute(FluxAttributes.MODEL))
      wb.element("model")
              .writer(new SourceModelWriter())
              .put(object.getAttribute(FluxAttributes.MODEL, SourceModel.class));
    wb.element("gammaLines").putContents(this.getGammaLines(object));
    wb.element("gammaGroups").putContents(this.getGammaGroups(object));
    wb.element("neutronGroups").putContents(this.getNeutronGroups(object));
  }

  double[][] getGammaLines(Flux object)
  {
    double[][] out = new double[object.getPhotonLines().size()][];

    int i = 0;
    for (FluxLine v : object.getPhotonLines())
    {
      if (v instanceof FluxLineStep)
      {
        FluxLineStep v2 = (FluxLineStep) v;
        if (v2.getStep() != 0.0)
          out[i++] = new double[]
          {
            v2.getEnergy(), v2.getIntensity(), v2.getStep()
          };
        else
          out[i++] = new double[]
          {
            v2.getEnergy(), v2.getIntensity()
          };
        continue;
      }
      if (v instanceof FluxLineZAD)
      {
        FluxLineZAD v2 = (FluxLineZAD) v;
        out[i++] = new double[]
        {
          v2.getEnergy(), v2.getIntensity(), v2.getZ(), v2.getAD()
        };
        continue;
      }
      throw new UnsupportedOperationException();
    }
    return out;
  }

  double[][] getGammaGroups(Flux object)
  {
    double[][] out = new double[object.getPhotonGroups().size()][];
    int i = 0;
    for (FluxGroup group : object.getPhotonGroups())
    {
      out[i++] = new double[]
      {
        group.getEnergyLower(), group.getCounts()
      };
    }
    return out;
  }

  double[][] getNeutronGroups(Flux object)
  {
    double[][] out = new double[object.getNeutronGroups().size()][];
    int i = 0;
    for (FluxGroup group : object.getNeutronGroups())
    {
      out[i++] = new double[]
      {
        group.getEnergyLower(), group.getCounts()
      };
    }
    return out;
  }
}
