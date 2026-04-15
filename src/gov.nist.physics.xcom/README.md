# gov.nist.physics.xcom

## Overview

The `gov.nist.physics.xcom` package provides a Java implementation for accessing and utilizing the NIST XCOM database of photon cross sections. This package is a key component of the RADSIM simulation framework, enabling accurate modeling of photon interactions with matter across a wide energy range.

## Key Features

- **Comprehensive Photon Cross Section Data**: Covers elements Z=1–100 and arbitrary mixtures/compounds
- **Wide Energy Range**: 1 keV to 100 GeV
- **Multiple Interaction Types**: Provides photoelectric, incoherent (Compton), coherent (Rayleigh), pair production, and total attenuation cross sections
- **Material Composition Support**: Calculate cross sections for pure elements and arbitrary material mixtures
- **Flexible Units System**: Configurable input (energy) and output (cross section) units
- **Edge-Aware Calculations**: Properly handles absorption edges where cross sections exhibit discontinuities

## Main Components

### XCOMPhotonCrossSectionLibrary

The central class that implements the `PhotonCrossSectionLibrary` interface, providing access to the NIST XCOM database:

```java
// Get the singleton instance
XCOMPhotonCrossSectionLibrary lib = XCOMPhotonCrossSectionLibrary.getInstance();

// Access photon cross sections for an element
PhotonCrossSections elementCS = lib.get(Elements.getElement("Fe"));

// Access photon cross sections for a material (mixture/compound)
PhotonCrossSections materialCS = lib.get(myMaterial);
```

### CrossSectionsImpl

Implements the `PhotonCrossSections` interface, providing access to cross section data for a specific element or material:

```java
// Configure input/output units
elementCS.setInputUnits(Units.get("energy:keV"));
elementCS.setOutputUnits(Units.get("cross_section:barn/atom"));

// Create an evaluator to query cross sections at specific energies
PhotonCrossSectionsEvaluator evaluator = elementCS.newEvaluator();
```

### CrossSectionsEvaluatorImpl

Evaluates photon cross sections at specific energies using log-log interpolation:

```java
// Position the evaluator at a specific energy
evaluator.seek(511.0); // keV

// Query specific cross section types (in the configured output units)
double photoelectric = evaluator.getPhotoelectric();
double compton = evaluator.getIncoherent();
double rayleigh = evaluator.getCoherent();
double pairNuclear = evaluator.getPairNuclear();
double pairElectron = evaluator.getPairElectron();
double total = evaluator.getTotal();
```

### CrossSectionsTable

Data structure that stores the cross section values as a function of energy:
- All data is stored in log-log format for efficient interpolation
- Default energy units: keV
- Default cross section units: cm²/g

### CrossSectionsEncoding

Handles serialization and deserialization of cross section data to/from binary format.

## Data Source

The package uses the **NIST XCOM** database as its source for photon cross sections:

- **Comprehensive**: Covers all elements Z=1–100
- **Evaluated**: Based on theoretical calculations and experimental data, curated by NIST
- **Wide Energy Range**: 1 keV to 100 GeV
- **Community Standard**: Widely used in radiation transport, shielding, and detector simulation

### Citation

If using this data in published work, please cite:

> Berger, M.J., Hubbell, J.H., Seltzer, S.M., Chang, J., Coursey, J.S., Sukumar, R., Zucker, D.S., & Olsen, K. (2010).
> XCOM: Photon Cross Section Database (version 1.5).
> National Institute of Standards and Technology, Gaithersburg, MD.
> https://www.nist.gov/pml/xcom-photon-cross-sections-database

## Limitations

- Not primary data: For traceability, consult the original references in the XCOM documentation
- Mixture rules: Compound/mixture cross sections are calculated using the mixture rule (mass fractions)
- Updates: Database reflects the state as of 2010. For the latest values, check for updates or new releases

## Alternative Data Sources

- **NIST X-ray**: For atomic line energies and yields
- **NIST Compton Profile Database**: For electron momentum profiles
- **EPDL, Storm & Israel, Henke**: For specific energy ranges or materials

## Usage Example

```java
import gov.llnl.rtk.physics.Element;
import gov.llnl.rtk.physics.Elements;
import gov.llnl.rtk.physics.Material;
import gov.llnl.rtk.physics.Materials;
import gov.llnl.rtk.physics.PhotonCrossSections;
import gov.llnl.rtk.physics.PhotonCrossSectionsEvaluator;
import gov.llnl.rtk.physics.Units;
import gov.nist.physics.xcom.XCOMPhotonCrossSectionLibrary;

// Get the XCOM photon cross section library
XCOMPhotonCrossSectionLibrary lib = XCOMPhotonCrossSectionLibrary.getInstance();

// Get cross sections for lead
Element pb = Elements.getElement("Pb");
PhotonCrossSections pbCS = lib.get(pb);

// Configure units (keV for energy, barn/atom for cross sections)
pbCS.setInputUnits(Units.get("energy:keV"));
pbCS.setOutputUnits(Units.get("cross_section:barn/atom"));

// Create an evaluator and query at specific energies
PhotonCrossSectionsEvaluator eval = pbCS.newEvaluator();

// Print cross sections at different energies
double[] energies = {10.0, 100.0, 511.0, 1000.0};
for (double energy : energies) {
    eval.seek(energy);
    System.out.printf("Energy: %.1f keV | " +
                     "Photoelectric: %.4e | " +
                     "Compton: %.4e | " +
                     "Total: %.4e barn/atom%n",
                     energy,
                     eval.getPhotoelectric(),
                     eval.getIncoherent(),
                     eval.getTotal());
}

// Create a compound material (water: H2O)
Material water = Materials.compound()
    .addElement(Elements.getElement("H"), 2)
    .addElement(Elements.getElement("O"), 1)
    .build();

// Get cross sections for the compound
PhotonCrossSections waterCS = lib.get(water);

// Configure units and query
waterCS.setInputUnits(Units.get("energy:MeV"));
waterCS.setOutputUnits(Units.get("cross_section:cm2/g"));
PhotonCrossSectionsEvaluator waterEval = waterCS.newEvaluator();
waterEval.seek(1.0); // 1 MeV
System.out.printf("Water, 1 MeV, total attenuation: %.4e cm2/g%n",
                 waterEval.getTotal());
```

## Integration with RADSIM

This package is designed to work with the RADSIM framework for radiation simulations:

1. It provides photon interaction data needed for radiation detector response simulations
2. It integrates with other physics packages in the RADSIM ecosystem
3. It can be accessed through the Python interface using JPype

## Implementation Notes

- The cross section data is stored in binary resource files for each element
- Data is cached for efficient repeated access
- Log-log interpolation is used for accurate cross section calculation between data points
- Special handling is provided for absorption edges where cross sections are discontinuous
