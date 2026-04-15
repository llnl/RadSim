# gov.nist.physics.xray

## Overview

The `gov.nist.physics.xray` package is a Java implementation that provides access to the NIST Elam, Ravel, Sieber (ElamDB12) database for X-ray atomic data. This package is part of the RADSIM simulation framework and serves as a key component for handling X-ray physics calculations in radiation simulations.

## Key Features

- **Comprehensive X-ray Data**: Covers elements Z=1–98 and a wide range of X-ray transitions (energies, rates, yields)
- **Element-Based Access**: Retrieve X-ray data by element symbol or atomic number
- **X-ray Edge Information**: Access to edge energies, fluorescence yields, and ratio jumps
- **X-ray Lines**: Access to X-ray emission line data with IUPAC and Siegbahn notation
- **Scatter Information**: Data for coherent and incoherent scattering
- **Photo-absorption**: Photo-absorption data for elements

## Main Components

### NISTXrayLibrary

The central class that implements the `XrayLibrary` interface. It loads and provides access to the ElamDB12 dataset for X-ray atomic data.

```java
// Get the singleton instance of the NIST X-ray library
NISTXrayLibrary lib = NISTXrayLibrary.getInstance();

// Access X-ray data for an element
XrayData elementData = lib.get(Elements.getElement("Fe"));
```

### XrayDataImpl

Implements the `XrayData` interface to provide X-ray data for a specific element, including:
- Basic element properties (atomic number, weight, density)
- X-ray edges
- Photo-absorption data
- Scatter data (coherent and incoherent)

### XrayEdgeImpl

Represents an X-ray edge (K, L, M, etc.) with:
- Edge energy
- Fluorescence yield
- Ratio jump
- Associated X-ray emission lines
- Coster-Kronig transition probabilities

### XrayImpl

Represents an individual X-ray emission line with:
- IUPAC and Siegbahn notation
- Energy (in keV)
- Relative intensity

### XrayParser

Internal utility class that parses the ElamDB12.txt data file into the object model.

## Data Source

The package uses the **Elam, Ravel, Sieber (ElamDB12)** database as its default "NIST X-ray" library for atomic line energies, emission rates, and related X-ray atomic data.

### Citation

If using this data in published work, please cite:

> Elam, W. T., Ravel, B., & Sieber, J. R. (2002).
> A new atomic database for X-ray spectroscopic calculations.
> *Radiation Physics and Chemistry*, 63(2), 121–128.
> [https://doi.org/10.1016/S0969-806X(01)00227-4](https://doi.org/10.1016/S0969-806X(01)00227-4)

## Usage Example

```java
// Import necessary classes
import gov.llnl.rtk.physics.Elements;
import gov.llnl.rtk.physics.XrayData;
import gov.llnl.rtk.physics.XrayEdge;
import gov.nist.physics.xray.NISTXrayLibrary;

// Get the NIST X-ray library
NISTXrayLibrary lib = NISTXrayLibrary.getInstance();

// Get X-ray data for iron
XrayData ironData = lib.get(Elements.getElement("Fe"));

// Access the K-edge information
for (XrayEdge edge : ironData.getEdges()) {
    if (edge.getName().equals("K")) {
        System.out.println("Fe K-edge fluorescence yield: " + edge.getFlorencenceYield());

        // Print K-edge X-ray emission lines
        edge.getXrays().forEach(xray ->
            System.out.printf("%s: %.4f keV, intensity: %.4f%n",
                xray.getName(),
                xray.getEnergy().getValue(),
                xray.getIntensity().getValue())
        );
        break;
    }
}
```

## Integration with RADSIM

This package is designed to work with the RADSIM framework for radiation simulations:

1. It provides X-ray atomic data needed for simulating X-ray physics
2. It integrates with other physics packages in the RADSIM ecosystem
3. It can be accessed through the Python interface using JPype

