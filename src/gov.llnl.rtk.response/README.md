# Radiation Detector Response Modeling (gov.llnl.rtk.response)

## Overview

The `gov.llnl.rtk.response` package is a key component of the RADSIM simulation framework, focused on calculating simulated detector output for incident radiation flux. This repository implements advanced physics models for radiation transport, deposition, and detector response simulation using a combination of analytical methods and machine learning techniques.

## Key Features

- **Energy Deposition Modeling**: Simulates how radiation deposits energy in detector materials using sophisticated chord distribution calculations
- **Flow Matching ML**: Implements machine learning Flow matching methods to accurately calculate chord distributions through various geometries
- **Photon Physics**: Models photoelectric absorption, Compton scattering, and pair production processes
- **Multiple Geometry Support**: Includes models for both cuboid and cylindrical detector geometries
- **Readout Effects**: Applies detector-specific readout effects to simulate real-world instrument response

## Core Components

### Deposition Calculators

The package provides specialized calculators for simulating energy deposition in detector materials:
- `DepositionCalculator`: Central class for energy deposition calculations
- `MultiScatterCalculator`: Models multiple scattering events within the detector

#### Multi-Step Deposition Process

The `DepositionCalculator` implements a sophisticated multi-step approach to modeling radiation interactions:

1. **First Scattering (Spatial)**: Uses `SpatialChordQF` to calculate chord distributions for the initial interaction based on the source position relative to the detector geometry
2. **Second Scattering (Angular)**: Uses `AngularChordQF` to model secondary scatters with angular dependencies from the initial interaction, considering the scattering angle from Compton interactions
3. **Nth Scattering (Isotropic)**: Uses `IsotropicChordQF` to handle all subsequent interactions using an isotropic model for multi-scatter events

This layered approach allows for accurate modeling of the complex physics involved in radiation transport while maintaining computational efficiency. Each step uses specific quantile functions optimized for the particular scattering regime:

```
Incident Photon → First Interaction (Spatial) → Possible Outcomes:
                                              ├── Photoelectric Absorption (Full Energy Deposition)
                                              ├── Pair Production (511 keV escape peaks)
                                              └── Compton Scatter → Second Interaction (Angular) → Possible Outcomes:
                                                                                                 ├── Photoelectric Absorption
                                                                                                 ├── Pair Production
                                                                                                 ├── Escape from Detector
                                                                                                 └── Compton Scatter → Nth Interactions (Isotropic)
```

For each interaction, the calculator:
1. Determines interaction probabilities based on material cross-sections
2. Uses the appropriate chord distribution model (spatial/angular/isotropic)
3. Calculates energy deposition and tracks photon fate
4. Builds up the final energy deposition spectrum

### Chord Geometry Models

Chord distributions are calculated using both analytical methods and machine learning:
- `ChordGeometry`: Interface for shape-specific chord calculations
- `CylinderChordGeometry`: Chord calculations for cylindrical detectors
- `CuboidChordGeometry`: Chord calculations for cuboid detectors

### Machine Learning Components

The repository uses neural network models to efficiently calculate chord distributions:
- `FlowMatch`: Implements flow-based generative models for calculating vector fields
- `ArchEPP`: Encoder-predictor architecture used for chord distribution modeling
- `EncoderPredictor`: Interface for ML models used in flow matching

### Response Functions

Various response functions model how detectors transform deposited energy:
- `SpectralResponseFunction`: Models detector response to radiation
- `SpectralResponseEvaluator`: Evaluates response for specific detector configurations
- `RenderItem`: Handles visualization of detector response features (photoelectric peaks, escape peaks, etc.)

## Mathematical Methods

The codebase employs several advanced mathematical techniques:
- **Flow Matching**: Neural networks trained to model the flow of probability distributions through vector fields, enabling rapid computation of complex chord distributions that would otherwise require expensive Monte Carlo simulations
- **Cumulative Distribution Functions (CDFs)**: Used to represent chord length distributions
- **Quantile Functions (QFs)**: Inverse CDFs used to sample from chord distributions
- **Special Functions**: Elliptical integrals for analytical solutions of cylindrical geometries

## Python Integration

Python scripts in the `py/` directory provide interfaces to:
- Verify chord distribution calculations
- Run simulations and compare with analytical solutions
- Generate visualizations of detector response

## Dependencies

- `gov.llnl.math`: Mathematical utilities
- `gov.llnl.rtk.physics`: Radiation physics models
- `gov.llnl.utility`: General utility classes

## Usage

The package is typically used as part of the larger RADSIM framework. To use it:

1. Initialize the appropriate detector geometry
2. Set up the deposition calculator with material cross-sections
3. Configure the incident radiation flux
4. Compute the deposition and detector response

Example (simplified):
```java
// Set up detector geometry
CylinderChordGeometry geometry = new CylinderChordGeometry();
geometry.setDimensions(new Quantity(5.0, "cm"), new Quantity(10.0, "cm"));

// Create deposition calculator
DepositionCalculator calculator = new DepositionCalculator(
        materialCrossSections,
        geometry,
        scatteringDistribution,
        density,
        maxEnergy);

// Set source position
calculator.setPosition(100.0, 0.0, 0.0);

// Compute deposition
Deposition result = calculator.compute(new Quantity(662.0, "keV"), intensity);

// Process results
double[] spectrum = result.scattered;
```

## License

Copyright 2025, Lawrence Livermore National Security, LLC.
All rights reserved.

See "Notice" file for terms and conditions.