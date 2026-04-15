# Unit Tests for gov.bnl.nndc.ensdf

This directory contains unit tests for the gov.bnl.nndc.ensdf package using TestNG framework.

## Test Structure

The tests follow the same package structure as the main code:

- `gov.bnl.nndc.ensdf` - Tests for core ENSDF data structures
- `gov.bnl.nndc.ensdf.decay` - Tests for decay data handling classes

## Running Tests

You can run the tests using Ant:

```bash
cd src/gov.bnl.nndc.ensdf
ant test
```

Test reports will be generated in `build/test/reports/`.

## Test Classes

### Core Package Tests

- `EnsdfParserNGTest` - Tests for parsing ENSDF format data
- `EnsdfQuantityNGTest` - Tests for quantity with uncertainty handling
- `EnsdfTimeQuantityNGTest` - Tests for time value handling with units
- `EnsdfLevelNGTest` - Tests for nuclear level data structure

### Decay Package Tests

- `BNLDecayLibraryNGTest` - Tests for decay data library loading and access
- `GammaImplNGTest` - Tests for gamma radiation representation
- `BetaImplNGTest` - Tests for beta radiation representation
- `XrayImplNGTest` - Tests for X-ray representation
- `SplitIsomersNGTest` - Tests for isomer splitting functionality
- `EmissionCorrelationImplNGTest` - Tests for emission correlation handling

## Adding New Tests

To add a new test:

1. Create a new test class in the appropriate package with the suffix `NGTest`
2. Use TestNG annotations (`@Test`, `@BeforeClass`, etc.)
3. Implement test methods using TestNG assertions
4. Run the tests using `ant test`

## Test Dependencies

The tests require:
- TestNG library
- The main gov.bnl.nndc.ensdf classes
- Any additional dependencies used by the main code

These dependencies are automatically included in the test classpath by the Ant build script.