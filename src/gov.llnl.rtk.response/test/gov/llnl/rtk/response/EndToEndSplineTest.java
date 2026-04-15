/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxEncoding;
import gov.llnl.utility.proto.ProtoException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author nelson85
 */
public class EndToEndSplineTest
{

  public static void main(String[] args) throws IOException, ProtoException
  {

    // Load the detector model
    Path filename = Paths.get("test/data/drf.bin");
    SpectralResponseFunctionSplineEncoding drfEncoding = new SpectralResponseFunctionSplineEncoding();
    SpectralResponseFunctionSpline responseFunction = drfEncoding.parseStreamGZ(Files.newInputStream(filename));

    // Set up an energy bin structure to use for the output
    EnergyScale energyScale = EnergyScaleFactory.newLinearScale(0, 3000, 4096);

    Flux flux;
    Spectrum spectrum;
    SpectralResponseEvaluator evaluator = responseFunction.newEvaluator();
    evaluator.setEnergyScale(energyScale);

    // Verify we can render one line
//    System.out.println("Render a line");
//    flux = FluxFactory.monoenergetic(2000, 1);
//    for (int i = 0; i < 1000; ++i)
//      spectrum = evaluator.apply(flux);

//    // Verify we can render one line
//    System.out.println("Render a group");
//    flux = FluxFactory.group(2200, 2400, 1.0);
//    for (int i = 0; i < 1000; ++i)
//      spectrum = evaluator.apply(flux);

    // Load a Flux file to be responsed
    FluxEncoding fluxEncoding = FluxEncoding.getInstance();
    flux = fluxEncoding.parseStream(Files.newInputStream(Paths.get("test/data/1kgPuWG.bin")));
    spectrum = evaluator.apply(flux);
  }
}
