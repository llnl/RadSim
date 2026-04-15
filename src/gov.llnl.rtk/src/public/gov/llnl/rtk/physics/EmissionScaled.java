// --- file: gov/llnl/rtk/physics/EmissionScaled.java ---
/* 
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public class EmissionScaled
{
  public static Emission scaled(Emission e, double f)
  {
    if (e instanceof Alpha)
      return new ScaledAlpha((Alpha) e, f);
    if (e instanceof Beta)
      return new ScaledBeta((Beta) e, f);
    if (e instanceof Gamma)
      return new ScaledGamma((Gamma) e, f);
    if (e instanceof ElectronCapture)
      return new ScaledCapture((ElectronCapture) e, f);
    if (e instanceof Positron)
      return new ScaledPositron((Positron) e, f);
    if (e instanceof Xray)
      return new ScaledXray((Xray) e, f);
    throw new UnsupportedOperationException("Unable to scale particle of type " + e.getClass());
  }

  public static class ScaledAlpha implements Alpha
  {
    private static final long serialVersionUID = 0xA341_0000_0051_0000L;
    private final Alpha parent;
    private final ScaledQuantity intensity;

    private ScaledAlpha(Alpha e, double f)
    {
      this.parent = e;
      this.intensity = new ScaledQuantity(e.getIntensity(), f);
    }

    @Override
    public Quantity getEnergy()
    {
      return parent.getEnergy();
    }

    @Override
    public Quantity getIntensity()
    {
      return intensity;
    }

    @Override
    public Quantity getHindrance()
    {
      return parent.getHindrance();
    }

    @Override
    public Transition getOrigin()
    {
      return parent.getOrigin();
    }

  }

  public static class ScaledBeta implements Beta
  {
    private static final long serialVersionUID = 0xA341_0000_0052_0000L;
    private final Beta parent;
    private final ScaledQuantity intensity;

    private ScaledBeta(Beta e, double f)
    {
      this.parent = e;
      this.intensity = new ScaledQuantity(e.getIntensity(), f);
    }

    @Override
    public Quantity getEnergy()
    {
      return parent.getEnergy();
    }

    @Override
    public Quantity getIntensity()
    {
      return intensity;
    }

    @Override
    public Quantity getLogFT()
    {
      return parent.getLogFT();
    }

    @Override
    public String getForbiddenness()
    {
      return parent.getForbiddenness();
    }

    @Override
    public Transition getOrigin()
    {
      return parent.getOrigin();
    }

  }

  private static class ScaledPositron implements Positron
  {
    private static final long serialVersionUID = 0xA341_0000_0053_0000L;
    private final Positron parent;
    private final ScaledQuantity intensity;

    public ScaledPositron(Positron e, double f)
    {
      this.parent = e;
      this.intensity = new ScaledQuantity(e.getIntensity(), f);
    }

    @Override
    public Quantity getEnergy()
    {
      return parent.getEnergy();
    }

    @Override
    public Quantity getIntensity()
    {
      return intensity;
    }

    @Override
    public Quantity getLogFT()
    {
      return parent.getLogFT();
    }

    @Override
    public Transition getOrigin()
    {
      return parent.getOrigin();
    }

    @Override
    public String getForbiddenness()
    {
      return parent.getForbiddenness();
    }
  }

  public static class ScaledCapture implements ElectronCapture
  {
    private static final long serialVersionUID = 0xA341_0000_0054_0000L;
    private final ElectronCapture parent;
    private final ScaledQuantity intensity;

    public ScaledCapture(ElectronCapture e, double f)
    {
      this.parent = e;
      this.intensity = new ScaledQuantity(e.getIntensity(), f);
    }

    @Override
    public Quantity getEnergy()
    {
      return parent.getEnergy();
    }    
    
    @Override
    public Quantity getIntensity()
    {
      return intensity;
    }

    @Override
    public Quantity getLogFT()
    {
      return parent.getLogFT();
    }

    @Override
    public Transition getOrigin()
    {
      return parent.getOrigin();
    }

    @Override
    public String getForbiddenness()
    {
      return parent.getForbiddenness();
    }

  }

  private static class ScaledGamma implements Gamma
  {
    private static final long serialVersionUID = 0xA341_0000_0055_0000L;
    private final Gamma parent;
    private final ScaledQuantity intensity;

    public ScaledGamma(Gamma e, double f)
    {
      this.parent = e;
      this.intensity = new ScaledQuantity(e.getIntensity(), f);
    }

    @Override
    public Quantity getEnergy()
    {
      return parent.getEnergy();
    }

    @Override
    public Quantity getIntensity()
    {
      return intensity;
    }

    @Override
    public Transition getOrigin()
    {
      return parent.getOrigin();
    }
  }

  private static class ScaledXray implements Xray
  {
    private static final long serialVersionUID = 0xA341_0000_0056_0000L;
    private final Xray parent;
    private final ScaledQuantity intensity;

    public ScaledXray(Xray e, double f)
    {
      this.parent = e;
      this.intensity = new ScaledQuantity(e.getIntensity(), f);
    }

    @Override
    public String getName()
    {
      return parent.getName();
    }

    @Override
    public Quantity getEnergy()
    {
      return parent.getEnergy();
    }

    @Override
    public Quantity getIntensity()
    {
      return intensity;
    }

    @Override
    public Transition getOrigin()
    {
      return parent.getOrigin();
    }
  }
}
