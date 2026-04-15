# ENSDF Package (gov.bnl.nndc.ensdf)

## Overview

The ENSDF (Evaluated Nuclear Structure Data File) package provides tools for parsing, processing, and utilizing nuclear structure and decay data in the standardized ENSDF format. This package is a key component of the RADSIM framework, serving as the bridge between raw nuclear data files and the radiation simulation capabilities.

## Key Features

- **ENSDF File Parsing**: Robust parsing of the ENSDF card-format data files
- **Nuclear Decay Data**: Comprehensive representation of decay modes including:
  - Beta decay
  - Gamma emission
  - Alpha decay
  - Electron capture
  - X-ray emission
  - Internal conversion
- **Decay Libraries**: Fast access to decay transition data for all nuclides
- **Isomer Handling**: Special processing for nuclear isomeric states
- **Emission Calculation**: Computation of decay emissions for radiation sources

## Package Structure

The package is organized into several key namespaces:

### Core Package (`gov.bnl.nndc.ensdf`)

Contains the fundamental data structures for representing ENSDF records:

- `EnsdfParser`: Parses ENSDF formatted files into data structures
- `EnsdfDataSet`: Container for all ENSDF records from a dataset
- `EnsdfLevel`: Represents a nuclear energy level
- `EnsdfQuantity`: Handles physical quantities with uncertainties
- `EnsdfTimeQuantity`: Special handling for time values with units
- `EnsdfEmission`: Base class for different radiation emissions
- Various radiation type classes: `EnsdfGamma`, `EnsdfBeta`, `EnsdfAlpha`, `EnsdfXray`, etc.

### Decay Package (`gov.bnl.nndc.ensdf.decay`)

Implements the radiation decay calculation capabilities:

- `BNLDecayLibrary`: Main library for accessing decay data
- `DecayTransitionImpl`: Implementation of decay transitions
- `GammaImpl`, `BetaImpl`, `AlphaImpl`, etc.: Specific radiation implementations
- `SplitIsomers`: Utility for handling nuclear isomeric states
- `ElectronCaptureImpl`: Electron capture decay implementation

## Usage

### Loading ENSDF Data

```java
// Create a decay library
BNLDecayLibrary library = new BNLDecayLibrary();

// Load data from an ENSDF format file
library.loadFile(Paths.get("BNL2023.txt"));
```

### Accessing Decay Transitions

```java
// Get all decay transitions from a specific nuclide
Nuclide cs137 = Nuclides.get("Cs137");
List<DecayTransition> transitions = library.getTransitionsFrom(cs137);

// Iterate through transitions
for (DecayTransition transition : transitions) {
    System.out.println("Parent: " + transition.getParent());
    System.out.println("Child: " + transition.getChild());
    System.out.println("Half-life: " + transition.getHalfLife() + " seconds");
    System.out.println("Decay Mode: " + transition.getMode());
}
```

### Calculating Emissions

```java
// Create an emission calculator with the decay library
EmissionCalculator calculator = new EmissionCalculator();
calculator.setDecayLibrary(library);

// Create a source
Source source = SourceImpl.fromActivity(Nuclides.get("Cs137"), 100, ActivityUnit.Bq);

// Calculate emissions
EmissionResults results = calculator.calculate(source);

// Access specific radiation types
for (Gamma gamma : results.getGammas()) {
    System.out.println("Energy: " + gamma.getEnergy() + " keV");
    System.out.println("Intensity: " + gamma.getIntensity() + " /decay");
}
```

## Python Interface

The package can be accessed from Python using JPype. Example notebooks in the `/py` directory demonstrate:

- Source creation and configuration
- Emission calculation
- Decay chain simulation
- Source aging over time

See `sourcegen_example.ipynb` for a comprehensive example.

## Dependencies

- `gov.llnl.rtk.physics`: Core radiation toolkit physics classes
- `gov.nist.xray`: NIST X-ray data
- Java 11 or higher

## Testing

Unit tests are provided using the TestNG framework. To run the tests:

```bash
cd src/gov.bnl.nndc.ensdf
ant test
```

Test reports are generated in `build/test/reports/`.

## References

- [ENSDF Format Documentation](https://www.nndc.bnl.gov/ensdf/ensdf-doc.pdf)
- [BNL Nuclear Data Center](https://www.nndc.bnl.gov/)
- [IAEA Nuclear Data Services](https://www-nds.iaea.org/)