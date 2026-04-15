// --- file: gov/llnl/rtk/physics/DecayTransition.java ---
/* 
 * Copyright 2023, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public interface DecayTransition extends Transition
{

  Nuclide getChild();

}
