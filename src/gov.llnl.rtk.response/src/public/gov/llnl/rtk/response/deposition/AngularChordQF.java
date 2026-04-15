// --- file: gov/llnl/rtk/response/deposition/AngularCDF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.rtk.physics.Quantity;

/**
 * This will be the ML cdf->chord length distribution based on position and
 * angle (including the cross section).
 *
 * The model itself will likely be a singleton and we need an evaluator. We use
 * encoder projector based models, so the evaluator gets updated with a new
 * position, we then sweep the angle/crosssection to get our chord distribution
 * from the quantiles.
 *
 * @author nelson85
 */
public interface AngularChordQF
{


    // Maybe this should handle permution and scale
    void setPosition(double x, double y, double z);
    
    default void setPosition(Quantity x, Quantity y, Quantity z)
    {
      setPosition(x.get(), y.get(), z.get());
    }

    // Quantile to chord (apply scale here)
    /**
     * 
     * @param quantile
     * @param cosAngle 
     * @param attenuation in SI units 1/m
     * @return 
     */
    double getChord(double quantile, double cosAngle, double attenuation);


}
