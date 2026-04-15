# LLNL Mathematical Library (gov.llnl.math)

[![pipeline status](https://lc.llnl.gov/gitlab/rda/gov.llnl.math/badges/ernie/pipeline.svg)](https://lc.llnl.gov/gitlab/rda/gov.llnl.math/-/commits/ernie)

## Overview

`gov.llnl.math` is a comprehensive Java library providing mathematical utilities, functions, and data structures for scientific computing. This library is part of the RADSIM simulation framework, supporting radiation transport calculations, numerical methods, and statistical analysis.

## Key Features

- **Linear Algebra**: Matrix operations, vector calculations, and decomposition algorithms
- **Numerical Methods**: Interpolation, Fourier transforms, and wavelet transforms
- **Statistical Functions**: Distributions, random number generation, and statistical utilities
- **Euclidean Geometry**: Vector and quaternion operations for 3D calculations
- **Special Functions**: Mathematical special functions including gamma, beta, and error functions

## Package Structure

The library is organized into the following key packages:

- `gov.llnl.math`: Core mathematical utilities and functions
- `gov.llnl.math.algebra`: Linear algebra operations and solvers
- `gov.llnl.math.distribution`: Statistical distributions
- `gov.llnl.math.euclidean`: 3D geometry operations
- `gov.llnl.math.function`: Function interfaces and implementations
- `gov.llnl.math.matrix`: Matrix data structures and operations
- `gov.llnl.math.random`: Random number generation
- `gov.llnl.math.spline`: Spline interpolation
- `gov.llnl.math.statistical`: Statistical calculations
- `gov.llnl.math.stream`: Stream operations for numerical data
- `gov.llnl.math.wavelet`: Wavelet transforms and operations

## Unit Testing

The library includes an extensive test suite using TestNG. Unit tests cover:

- Core numerical operations
- Matrix and vector operations
- Distribution functions
- Interpolation methods
- Special mathematical functions
- Random number generators

To run the tests:
```bash
cd src/gov.llnl.math
ant test
```

## Building

This library uses Apache Ant for building:

```bash
cd src/gov.llnl.math
ant jar
```

## Integration

This library integrates with the RADSIM framework to provide mathematical foundations for radiation simulation and transport calculations.

## Dependencies

- TestNG for unit testing
- Apache Ivy for dependency management

## License

Copyright 2016, Lawrence Livermore National Security, LLC. All rights reserved.
See the "Notice" file for terms and conditions.