# RadSim: Open-Source Gamma-Ray Detector Simulation Framework

RadSim is a comprehensive, modular, open-source framework for rapid simulation of gamma-ray detector output, including energy spectra and gross counts. It provides end-to-end simulation capabilities through three integrated stages:

1. **Source Generation**: Precise modeling of radiation sources and their emissions
2. **Flux Transport**: Multiple methods to simulate radiation transport through materials
3. **Detector Response**: Fast, physics-based simulation of detector interactions and output

## Key Features

- **Fully Open Source**: Completely free for open-source use
- **Platform Independent**: Runs on Windows, macOS, and Linux systems
- **Modular Architecture**: Mix and match components for customized simulations
- **Multiple Transport Options**: Integrations with MCNP and GEANT4
- **Machine Learning Acceleration**: Flow matching techniques for rapid detector response simulation
- **Comprehensive Physics**: Accurate modeling of complex radiation interactions
- **Python Interface**: Easy-to-use Python bindings for all components

## System Architecture

RadSim follows an integrated, modular design that connects three main simulation stages:

```
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│  Source Module   │    │ Transport Module │    │ Detector Module  │
│  ───────────     │    │  ───────────     │    │  ───────────     │
│  • Nuclides      │    │  • MCNP          │    │  • Response      │
│  • Emissions     │──▶ │  • GEANT4        │──▶ │  • Deposition    │
│  • Decay Data    │    │  • Bacchus       │    │  • Readout       │
└──────────────────┘    └──────────────────┘    └──────────────────┘
```

### Key Components

#### 1. Source Module (gov.llnl.rtk.physics, gov.bnl.nndc.ensdf)
- Comprehensive nuclide database
- Decay calculations using evaluated nuclear data
- Multiple source geometry options
- Activity, age, and composition customization

#### 2. Transport Module (gov.llnl.rtk.mcnp, gov.llnl.rtk.geant4)
- Integration with industry-standard transport codes
- Material definition and cross-section handling
- Complex geometry modeling capabilities
- Energy deposition calculations

#### 3. Detector Module (gov.llnl.rtk.response)
- **Flow Matching ML Technology**: Advanced machine learning for fast chord distribution calculation
- Multi-step deposition modeling (spatial, angular, isotropic)
- Support for various detector geometries (cuboid, cylindrical)
- Realistic detector response effects (energy resolution, efficiency)

## ML-Powered Fast Simulation

A key innovation in RadSim is the use of flow matching techniques in the `gov.llnl.rtk.response` package:

- **Neural Network Models**: Replace computationally expensive Monte Carlo simulations
- **Flow-Based Generative Models**: Calculate complex chord distributions through detector geometries
- **Physics-Informed Training**: Models maintain physical accuracy while improving performance
- **Multi-Scatter Modeling**: Efficiently simulates photoelectric absorption, Compton scattering, and pair production

This approach enables simulation speeds orders of magnitude faster than traditional Monte Carlo methods while maintaining physical accuracy.

## Platform Compatibility

RadSim is designed to be platform-independent and runs on:

- **Windows**: Windows 10/11 (64-bit)
- **macOS**: macOS 10.15 (Catalina) or newer
- **Linux**: Most modern distributions (Ubuntu 20.04+, CentOS 7+, Debian 10+)

The same codebase works identically across all platforms with no modifications needed, providing consistent results regardless of your operating system. Any platform with support for Python 3.x and Java JDK 11 can run RadSim with the necessary dependencies installed.

## Getting Started

### Prerequisites

```
Python 3.x
Java (JDK11)
Apache Ant (to build Java jars)
JPype (Python-Java bridge)
```

### Installation

1. Clone the repository and build dependencies:
```
python build.py src
```

2. Build the JAR files:
```
python build.py jar
```

3. Run an example simulation:
```
python py/full_simulation_example.py
```

### Basic Usage

```python
import startJVM
import jpype
import jpype.imports

# Import RadSim components
from gov.llnl.rtk.physics import Nuclides, SourceImpl, ActivityUnit
from gov.llnl.rtk.physics import EmissionCalculator
from gov.llnl.ensdf.decay import BNLDecayLibrary
from gov.llnl.rtk.response import DepositionCalculator

# Create a source (Cs-137, 1 μCi)
Cs137 = SourceImpl.fromActivity(Nuclides.get("Cs137"), 1, ActivityUnit.uCi)

# Calculate emissions
calculator = EmissionCalculator()
calculator.setDecayLibrary(BNLDecayLibrary())
emissions = calculator.apply([Cs137])

# Simulate detector response
# (See example notebooks for complete workflow)
```

## Key Subpackages

- **gov.llnl.rtk**: Core radiation toolkit providing physics models, emission calculation, and detector response
- **gov.bnl.nndc.ensdf**: Nuclear structure and decay data from evaluated files
- **gov.llnl.rtk.response**: Detector response simulation with ML-powered chord distribution calculation
- **gov.llnl.rtk.mcnp**: MCNP integration for detailed radiation transport simulation
- **gov.llnl.rtk.geant4**: GEANT4 integration for complex geometry transport modeling
- **gov.nist.physics.xcom**: NIST photon cross-section data
- **gov.llnl.math**: Mathematical utilities for simulation components

## Example Applications

- Radiation detector development and testing
- Source identification and characterization
- Shielding and facility design
- Emergency response training and planning
- Nuclear safeguards and security

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct and the process for submitting pull requests.

## Authors

* Karl Nelson
* Dhanush Hangal
* Vincent Cheung
* Brandon Lahmann
* Bonnie Canion
* Simon Labov
* Caleb Mattoon
* Noah McFerran

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* This work was supported by the Office of Defense Nuclear Nonproliferation Research and Development
within the U.S. Department of Energy's National Nuclear Security Administration.

* This work was produced under the auspices of the U.S. Department of Energy by
Lawrence Livermore National Laboratory under Contract DE-AC52-07NA27344.

* LLNL-CODE-854710 LLNL-CODE-855199 LLNL-CODE-2000457 LLNL-CODE-2015030
