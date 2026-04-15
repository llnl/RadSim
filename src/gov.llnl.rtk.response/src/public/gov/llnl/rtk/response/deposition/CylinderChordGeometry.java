// --- file: gov/llnl/rtk/response/deposition/CylinderChordGeometry.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.rtk.ml.ArchEPP;
import gov.llnl.rtk.ml.ArchEPPEncoding;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.utility.proto.ProtoException;
import java.io.IOException;
import java.io.InputStream;

public class CylinderChordGeometry implements ChordGeometry
{
  
  final static ArchEPP DIRECT_MODEL;
  final static ArchEPP ANGULAR_MODEL;

  // Load the surgate models
  static {
    try (InputStream stream = CylinderChordGeometry.class.getClassLoader()
            .getResourceAsStream("gov/llnl/rtk/response/resources/cylinder_direct.bin"))
    {
      if (stream == null)
        throw new RuntimeException("Resource not found: cylinder_direct.bin");
      ArchEPPEncoding encoding = new ArchEPPEncoding();
      DIRECT_MODEL = encoding.parseStream(stream);
    }
    catch (ProtoException | IOException ex)
    {
      throw new RuntimeException("Parse failed", ex);
    }
    
        try (InputStream stream = CylinderChordGeometry.class.getClassLoader()
            .getResourceAsStream("gov/llnl/rtk/response/resources/cylinder_angular.bin"))
    {
      if (stream == null)
        throw new RuntimeException("Resource not found: cylinder_angular.bin");
      ArchEPPEncoding encoding = new ArchEPPEncoding();
      ANGULAR_MODEL = encoding.parseStream(stream);
    }
    catch (ProtoException | IOException ex)
    {
      throw new RuntimeException("Parse failed", ex);
    }
  }
  
  // spatial parameters for the model  
  private Quantity diameter;
  private Quantity height;

  public void setDimensions(Quantity diameter, Quantity height)
  {
    this.diameter = diameter;
    this.height = height;
  }

  @Override
  public SpatialChordQF newSpatial()
  {
    return new CylinderSpatialChordQF(DIRECT_MODEL, diameter.get(),  height.get());
  }

  @Override
  public AngularChordQF newAngular()
  {
    return new CylinderAngularChordQF(ANGULAR_MODEL, diameter.get(), height.get());
  }

  @Override
  public IsotropicChordQF newIsotropic()
  {
    return new CylinderIsotropicChordCDF(diameter.get(), height.get()).inverse();
  }

}
