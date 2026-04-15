/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.flux.FluxBinned;
import gov.llnl.rtk.flux.FluxGroupBin;
import gov.llnl.rtk.flux.FluxLineStep;
import gov.llnl.utility.proto.ProtoEncoding;
import gov.llnl.utility.proto.ProtoException;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class SpectralResponseEvaluatorBaseNGTest
{

  public SpectralResponseEvaluatorBaseNGTest()
  {
  }

  @Test
  public void testGetResponseFunction()
  {
    SpectralResponseEvaluatorBase instance = new SpectralResponseEvaluatorBaseImpl();
    SpectralResponseFunction expResult = null;
    SpectralResponseFunction result = instance.getResponseFunction();
    assertEquals(result, expResult);
  }

  @Test
  public void testGetEnergyScale()
  {
    SpectralResponseEvaluatorBase instance = new SpectralResponseEvaluatorBaseImpl();
    EnergyScale expResult = EnergyScaleFactory.newLinearScale(0, 20, 20);
    EnergyScale result = instance.getEnergyScale();
    assertEquals(result, expResult);
  }

  @Test
  public void testSetEnergyScale()
  {
    EnergyScale scale = EnergyScaleFactory.newLinearScale(0, 20, 20);
    SpectralResponseEvaluatorBase instance = new SpectralResponseEvaluatorBaseImpl();
    instance.setEnergyScale(scale);
    assertEquals(instance.getEnergyScale(), scale);
  }

  @Test
  public void testRenderGroup() throws ProtoException
  {
    SpectralBuffer buffer = new SpectralBuffer();
    buffer.target = new double[20];
    double energy0 = 6.0;
    double intensity0 = 1.0;
    double energy1 = 10.0;
    double intensity1 = 1.0;
    SpectralResponseEvaluatorBase instance = new SpectralResponseEvaluatorBaseImpl();
    instance.renderGroup(buffer, energy0, intensity0, energy1, intensity1);
    assertEquals(TestSupport.md5Checksum(ProtoEncoding.Type.NetworkDoubles.toBytes(buffer.target)),
            "aca997270633a71d00f7b1d2b84728c0");
  }

  @Test
  public void testApply() throws ProtoException
  {
    FluxBinned flux = new FluxBinned();
    flux.addPhotonLine(new FluxLineStep(4, 100, 1));
    flux.addPhotonLine(new FluxLineStep(15, 5, 1));
    flux.addPhotonGroup(new FluxGroupBin(6, 10, 1));
    SpectralResponseEvaluatorBase instance = new SpectralResponseEvaluatorBaseImpl();
    Spectrum result = instance.apply(flux);
    assertEquals(TestSupport.md5Checksum(ProtoEncoding.Type.NetworkDoubles.toBytes(result.toDoubles())),
            "087d745e482a58a655605c4c305a5d1b");
  }

  public class SpectralResponseEvaluatorBaseImpl extends SpectralResponseEvaluatorBase
  {
    public SpectralResponseEvaluatorBaseImpl()
    {
      super(null, EnergyScaleFactory.newLinearScale(0, 20, 20));
    }

    @Override
    public void renderLine(SpectralBuffer buffer, double energy, double intensity)
    {
      buffer.set(this);
      buffer.target[(int) energy] += intensity;
    }

    @Override
    public void finish(SpectralBuffer buffer)
    {
      for (int i = 0; i < 10; i++)
      {
        buffer.target[i] /= 10;
      }
    }

    @Override
    public Spectrum getInternal()
    {
      return null;
    }

    @Override
    public DoubleUnaryOperator getEfficiencyFunction()
    {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DoubleUnaryOperator getResolutionFunction()
    {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setRenderItems(Set<RenderItem> renderItems)
    {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SpectralBufferDeferred deferred()
    {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getLower()
    {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Set<RenderItem> getRenderItems()
    {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
  }

}
