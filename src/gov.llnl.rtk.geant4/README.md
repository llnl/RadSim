# RTK GEANT4

This package provides an interface between RADSIM and Geant4, a toolkit for the simulation of the passage of particles through matter. The integration allows users to:

1. Define radiation sources with specific activity and nuclide information
2. Create complex 3D geometries with different materials
3. Simulate radiation transport through these geometries
4. Extract and analyze resulting spectra for various particles

## Prerequisites

- Python 3.x
- Java (JDK11)
- JPype (Python-Java bridge)
- RADSIM core libraries

## External Dependencies

- [ ] [GEANT4 Installation](https://geant4.web.cern.ch/download)
- [ ] [TENDL Dataset](https://cern.ch/geant4-data/datasets/G4TENDL.1.4.tar.gz)
- [ ] [LEND Dataset](ftp://gdo-nuclear.ucllnl.org)

## Define variables

export G4PARTICLEHPDATA=/path/to/geant4-install/share/Geant4/data/G4TENDL1.4

export G4LENDDATA=/path/to/LEND_GND1.3/LEND_GND1.3_ENDF.BVII.1

## Getting Started

### Setting up the environment

1. Build the JAR files according to the main project instructions
2. Make sure you have the `runGEANT4.sh` script in your working directory
3. Import the necessary modules:

```python
import startJVM
import jpype
import jpype.imports

from gov.llnl.rtk.flux import FluxTrapezoid, FluxGroupTrapezoid
from gov.llnl.rtk.physics import SourceImpl, ActivityUnit, Nuclides, EmissionCalculator
from gov.llnl.ensdf.decay import BNLDecayLibrary
from gov.llnl.rtk.geant4 import SourceGenerator
```

### Basic workflow

The typical workflow for using the Geant4 integration involves:

1. Initialize the SourceGenerator
2. Define radiation sources and calculate emissions
3. Configure the geometry and materials
4. Run the simulation using the provided shell script
5. Parse and analyze the results

## Key components

### SourceGenerator

The main class for interfacing with Geant4. It allows you to:
- Set flux information from radiation sources
- Define the type of particles to simulate
- Configure the geometry and materials of the simulation

### Geometry objects

Several types of geometric objects can be defined:
- Spherical objects
- Cylindrical objects
- Conical objects

Each can be positioned and configured with different materials.

### Spectrum analysis

After running the simulation, you can extract spectra for:
- Gamma rays
- Electrons
- Positrons

## Example use cases

- Simulating radiation transport through shielding materials
- Modeling detector response to various radiation sources
- Analyzing scattering and secondary particle production
- Calculating dose distributions in complex geometries

## Further information

For more detailed examples, see the provided example notebook. For additional information on the Geant4 toolkit itself, visit the [Geant4 website](https://geant4.web.cern.ch/)
