// --- file: gov/llnl/rtk/response/deposition/CuboidChordGeometry.java ---
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

public class CuboidChordGeometry implements ChordGeometry
{
  
  final static ArchEPP DIRECT_MODEL;
  final static ArchEPP ANGULAR_MODEL;

  // Load the surgate models
  static {
    try (InputStream stream = CuboidSpatialChordQF.class.getClassLoader()
            .getResourceAsStream("gov/llnl/rtk/response/resources/cuboid_direct.bin"))
    {
      if (stream == null)
        throw new RuntimeException("Resource not found: cuboid_direct.bin");
      ArchEPPEncoding encoding = new ArchEPPEncoding();
      DIRECT_MODEL = encoding.parseStream(stream);
    }
    catch (ProtoException | IOException ex)
    {
      throw new RuntimeException("Parse failed", ex);
    }
    
        try (InputStream stream = CuboidSpatialChordQF.class.getClassLoader()
            .getResourceAsStream("gov/llnl/rtk/response/resources/cuboid_angular.bin"))
    {
      if (stream == null)
        throw new RuntimeException("Resource not found: cuboid_scatter.bin");
      ArchEPPEncoding encoding = new ArchEPPEncoding();
      ANGULAR_MODEL = encoding.parseStream(stream);
    }
    catch (ProtoException | IOException ex)
    {
      throw new RuntimeException("Parse failed", ex);
    }
  }
  
  // spatial parameters for the model  
  private Quantity length;
  private Quantity width;
  private Quantity height;

  public void setDimensions(Quantity l, Quantity w, Quantity h)
  {
    this.length = l;
    this.width = w;
    this.height = h;
  }

  @Override
  public SpatialChordQF newSpatial()
  {
    return new CuboidSpatialChordQF(DIRECT_MODEL, length.get(), width.get(), height.get());
  }

  @Override
  public AngularChordQF newAngular()
  {
    return new CuboidAngularChordQF(ANGULAR_MODEL, length.get(), width.get(), height.get());
  }

  @Override
  public IsotropicChordQF newIsotropic()
  {
    return new CuboidIsotropicChordCDF(length.get(), width.get(), height.get()).inverse();
  }

}
