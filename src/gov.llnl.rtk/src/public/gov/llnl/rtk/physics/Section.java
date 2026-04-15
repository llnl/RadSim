// --- file: gov/llnl/rtk/physics/Section.java ---
package gov.llnl.rtk.physics;

import gov.llnl.math.euclidean.Vector3;
import java.io.Serializable;

/**
 * @author lahmann
 */
public interface Section extends Serializable
{

  void setMaterial(Material material);

  Vector3 getOrigin();

  Vector3 getAxis();

  Material getMaterial();
}
