// --- file: gov/llnl/rtk/physics/SourceImpl.java ---
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public class SourceImpl implements Source
{
  public Nuclide nuclide;
  public double activity;
  public double atoms;

  public SourceImpl(Source s)
  {
    this.nuclide = s.getNuclide();
    this.activity = s.getActivity(PhysicalProperty.ACTIVITY);
    this.atoms = s.getAtoms();
  }

  public SourceImpl(Nuclide nuc)
  {
    this.nuclide = nuc;
  }

  @Override
  public String toString()
  {
    return String.format("Source(%s,%.3e)", this.getNuclide().getName(), getActivity(PhysicalProperty.ACTIVITY));
  }

  @Override
  public Nuclide getNuclide()
  {
    return nuclide;
  }

  @Override
  public double getAtoms()
  {
    return atoms;
  }

  @Override
  public double getActivity(Units activityUnits)
  {
    activityUnits.require(PhysicalProperty.ACTIVITY);
    return activityUnits.fromSI(activity);
  }

  @Override
  public Quantity getActivityQuantity()
  {
    return Quantity.of(activity, PhysicalProperty.ACTIVITY);
  }

}
