# gov.llnl.rtk

## Overview

The `gov.llnl.rtk` package is a Radiation Toolkit (RTK) developed by Lawrence Livermore National Laboratory (LLNL). It provides a comprehensive suite of tools for radiation physics modeling, simulation, and analysis. This Java library serves as a core component of the RADSIM simulation framework, enabling accurate modeling of radiation sources, transport, and detector response.

## Key Features

- **Radiation Physics Modeling**: Complete toolkit for simulating radiation interactions and transport
- **Nuclide Database**: Extensive database of nuclear isotopes with decay properties
- **Emission Calculation**: Simulation of radiation emissions from nuclear sources
- **Material Properties**: Modeling of material compositions and their interaction with radiation
- **Flux Representations**: Multiple methods for representing radiation flux
- **Detector Response**: Tools for modeling detector response to radiation
- **Geometric Modeling**: Support for various geometric models for radiation transport
- **Units System**: Comprehensive physical units and quantities system
- **Integration with Transport Codes**: Interfaces to external radiation transport codes (MCNP, GEANT4)

## Main Components

### Physics Package (`gov.llnl.rtk.physics`)

The physics package provides core functionality for radiation physics:

- **Nuclides**: Classes for representing nuclear isotopes (`Nuclide`, `Nuclides`)
- **Elements**: Periodic table elements and their properties (`Element`, `Elements`)
- **Materials**: Material compositions and properties (`Material`, `Materials`)
- **Emissions**: Models for various radiation emissions (gamma, beta, alpha) (`Emission`, `Emissions`)
- **Decay**: Nuclear decay calculations and libraries (`DecayCalculator`, `DecayLibrary`)
- **Cross Sections**: Photon interaction cross sections (`PhotonCrossSections`)
- **Units**: Physical quantities with units (`Quantity`, `Units`, `PhysicalProperty`)
- **Sources**: Radiation source models (`Source`, `SourceModel`)

### Flux Package (`gov.llnl.rtk.flux`)

This package handles radiation flux representations:

- **Flux Models**: Various flux representation models (`FluxBinned`, `FluxSpectrum`)
- **Flux Operations**: Tools for manipulating and combining flux objects
- **Flux Evaluators**: Classes for evaluating flux at specific energies

### Data Package (`gov.llnl.rtk.data`)

The data package provides data structures for radiation data:

- **Energy Scales**: Energy binning and calibration (`EnergyScale`)
- **Spectra**: Radiation spectra representation (`Spectrum`, `DoubleSpectraList`)

### Transport Integration (`gov.llnl.rtk.mcnp`, `gov.llnl.rtk.geant4`)

These packages provide integration with external radiation transport codes:

- **MCNP**: Monte Carlo N-Particle code integration (`MCNP_Deck`, `MCNP_Material`)
- **GEANT4**: GEANT4 toolkit integration (`GEANT4Environment`, `SourceGenerator`)

### Response Package (`gov.llnl.rtk.response`)

Tools for modeling detector response to radiation:

- **Response Functions**: Models for detector response to radiation (`SpectralResponseFunction`)
- **Response Evaluators**: Classes for evaluating detector response (`SpectralResponseEvaluator`)
- **Dose Calculations**: Tools for calculating radiation dose (`DoseEvaluator`, `DoseResponseFunction`)

## Usage Examples

### Creating and Using Material Compositions

```java
import gov.llnl.rtk.physics.Element;
import gov.llnl.rtk.physics.Elements;
import gov.llnl.rtk.physics.Material;
import gov.llnl.rtk.physics.Materials;

// Create a water material (H2O)
Material water = Materials.compound()
    .addElement(Elements.getElement("H"), 2)
    .addElement(Elements.getElement("O"), 1)
    .build();

// Access material properties
double density = water.getDensity().getValue();  // g/cm³
```

### Working with Radioactive Sources

```java
import gov.llnl.rtk.physics.Nuclides;
import gov.llnl.rtk.physics.Source;
import gov.llnl.rtk.physics.SourceImpl;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.physics.Units;

// Create a Cs-137 source with 1 μCi activity
Quantity activity = Quantity.of(1, "μCi");
Source source = SourceImpl.fromActivity(Nuclides.get("Cs137"), activity);
```

### Calculating Emissions from a Source

```java
import gov.llnl.rtk.physics.EmissionCalculator;
import gov.llnl.rtk.physics.DecayLibrary;
import gov.llnl.rtk.physics.Emissions;
import java.util.List;
import java.util.Arrays;

// Initialize decay library (implementation dependent)
DecayLibrary decayLib = getDecayLibrary();

// Create emission calculator
EmissionCalculator calculator = new EmissionCalculator();
calculator.setDecayLibrary(decayLib);

// Calculate emissions from a list of sources
List<Source> sources = Arrays.asList(source1, source2);
Emissions emissions = calculator.apply(sources);

// Access emission information
for (Emission emission : emissions.getEmissions()) {
    if (emission instanceof Gamma) {
        Gamma gamma = (Gamma) emission;
        double energy = gamma.getEnergy().getValue();  // keV
        double intensity = gamma.getIntensity().getValue();  // particles/s
        System.out.printf("Gamma: %.2f keV, %.2e particles/s%n", energy, intensity);
    }
}
```

### Working with Flux

```java
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxFactory;
import gov.llnl.rtk.flux.FluxEvaluator;

// Create a monoenergetic flux (662 keV, 1000 photons/cm²/s)
Flux flux = FluxFactory.monoenergetic(662.0, 1000.0);

// Evaluate flux at a specific energy
FluxEvaluator evaluator = flux.getEvaluator();
evaluator.setEnergy(662.0);
double fluxValue = evaluator.getFlux();  // photons/cm²/s
```

## Integration with RADSIM

This package serves as a foundation for the RADSIM simulation framework:

1. It provides the core physics models used throughout the framework
2. It enables accurate modeling of radiation sources and their emissions
3. It supports integration with various radiation transport codes
4. It provides tools for modeling detector response to radiation

## Dependencies

The package has the following dependencies:

- **gov.llnl.math**: LLNL Mathematical utilities
- **gov.llnl.utility**: LLNL Utility library
- **Java Standard Library**

## License

Copyright 2016-2026, Lawrence Livermore National Security, LLC.
All rights reserved.

Terms and conditions are given in the "Notice" file.