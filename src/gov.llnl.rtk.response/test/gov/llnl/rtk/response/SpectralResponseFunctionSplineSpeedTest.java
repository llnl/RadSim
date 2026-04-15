package gov.llnl.rtk.response;

import java.io.IOException;


/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
/**
 *
 * @author nelson85
 */
public class SpectralResponseFunctionSplineSpeedTest
{
  static public void main(String[] args) throws IOException
  {
//    String path = "py/detector/identifinder-ng";
//    SpectralResponseFunctionSpline responseFunction = ResponseFormats.toSpectralResponseFunctionCurve(
//            Utility.loadFile(Paths.get(path, "drf.bin")));
//    SpectralResponseEvaluator re = new SpectralResponseEvaluatorCached(responseFunction);
//
//    Flux flux;
//    try ( InputStream fd = java.nio.file.Files.newInputStream(Paths.get("py/1kgPu239.bin")))
//    {
//      byte[] bt = fd.readAllBytes();
//      flux = FluxFormats.toJava(bt);
//    }
//
//    flux = FluxUtilities.simplify((FluxBinned) flux, re.getResolutionFunction());
//
////      re.setRenderItems(RenderItem.ALL);
//    re.setEnergyScale(EnergyScaleFactory.newLinearScale(0, 3000, 1025));
//    for (int i = 0; i < 1000; i++)
//    {
//      re.apply(flux);
//    }
  }
}
