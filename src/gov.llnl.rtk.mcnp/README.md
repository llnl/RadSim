# RTK MCNP API

This is an API to build and run MCNP decks in a Java environment.
Building MCNP decks with this API still requires basic familiarity of MCNP.
The manuals for MCNP are located [here](https://mcnp.lanl.gov/manual.html).
This framework removes some of the problematic idiosyncrasies of MCNP such as strict ordering of declarations and
strict arbitrary whitespace requirements while also allowing the user to declare variable parameters that can be
changed across multiple simulations.

For RadSim, this API will be used "underneath" another allowing users to specify detector geometries and sources
to be simulated in MCNP without any prior knowledge of MCNP.

## Use

The API works by declaring a `Job` to be simulated which contains a `Deck` describing the problem that must be built
beforehand. The `Deck` is essentially a container of various Lists of Objects required to describe the problem. The includes:
* Cells
* Particles
* Tallys

### Cells

Cells are the building blocks of the MCNP geometry. All physical space must be defined by some cell. In MCNP, this includes
a cell that represents the "outside world" or everything outside the system of interest. In this API this cell is generated
automatically and does not need to be included in decks.

Cells are defined by:
* A volume
* A material with some density

Volumes are initiated with a surface and an orientation relative to that surface. The volume is then further refined by
union and/or intersection operations with additional surface-orientation pairs. For example, a hollow sphere could be defined
with the following code:

    Volume hollowSphere = new Volume(
        Surface.sphere("Inner Sphere", 1.0),        // Sphere surface centered at 0,0,0 with radius 1.0
        Volume.Orientation.POSITIVE                 // Everything oriented outside the sphere
        );

    hollowSphere.addSurface(
        Surface.sphere("Outer Sphere",2.0),         // Sphere surface centered at 0,0,0 with radius 2.0
        Volume.Orientation.NEGATIVE,                // Everything oriented inside the sphere
        Volume.Operator.INTERSECTION                // Intersect this volume with the current volume
        );

Surfaces can be defined using their MCNP keywords and parameters in the order expected by MCNP. For example a sphere can be defined by:

    Surface sphere = new Surface(name, "s", x0, y0, z0, radius);

The goal of this API is to replace these keywords with prebuilt surface constructor calls for simplicity and readability.
Currently, spheres can be alternatively constructed via the call:

    Surface.sphere(String name, Double x0, Double y0, Double z0, Double radius)

Materials are a collection of isotopes with associated number or weight fractions. These fractions do not need to be normalized,
this is handled automatically by MCNP. Isotopes are defined by their Z and A and then added to a material with a corresponding
fraction. For example, a material of uranium could be defined by the following code:

    Material uranium = new Material("Uranium");
    uranium.addIsotope(new Isotope("U235", 92, 235), 0.007);        // U235
    uranium.addIsotope(new Isotope("U238", 92, 238), 0.993);        // U238

In simulations where individual isotopes are unimportant, one can set A = 0 to represent the natural abundance of isotopes.
This is generally true with gamma transport.

Also note that negative numbers should be used to represent mass fractions. Otherwise, the fractions are assumed to be number fractions.
This notation is brought over from MCNP and may be changed in future updates to be more transparent.

With these pieces, one can define a cell with the following calls:

        Cell cell = new Cell("Hollow Uranium Sphere", hollowSphere);
        cell.setMaterial(uranium, -19.3);

Note that the `-19.3` is a mass density in g/cc. Positive densities are assumed to be number densities. Like mentioned
previously, this is a left over from MCNP and may be updated for transparency in future updates.

### Predefined Geometries

The MCNP module supports several geometry types through the Section classes, which simplify the creation of common shapes:

#### Spherical Geometries
- **SphericalSection**: General spherical section defined by origin, axis, theta/phi angles, and inner/outer radii
  ```java
  SphericalSection(origin, axis, theta1, theta2, phi1, phi2, innerRadius, outerRadius)
  ```
  - Factory methods:
    - `SphericalSection.Sphere(origin, radius)` - Complete sphere
    - `SphericalSection.HollowSphere(origin, innerRadius, outerRadius)` - Hollow spherical shell
    - `SphericalSection.HalfSphere(origin, axis, radius)` - Hemisphere
    - `SphericalSection.HalfHollowSphere(origin, axis, innerRadius, outerRadius)` - Hollow hemisphere

#### Cylindrical Geometries
- **CylindricalSection**: General cylindrical section defined by origin, axis, phi angles, inner/outer radii, and height
  ```java
  CylindricalSection(origin, axis, phi1, phi2, innerRadius, outerRadius, height)
  ```
  - Factory methods:
    - `CylindricalSection.Cylinder(origin, axis, radius, height)` - Full cylinder

#### Conical Geometries
- **ConicalSection**: General conical section defined by origin, axis, theta/phi angles, and start/end lengths
  ```java
  ConicalSection(origin, axis, theta1, theta2, phi1, phi2, startLength, endLength)
  ```
  - Factory methods:
    - `ConicalSection.Cone(origin, axis, theta, length)` - Full cone

#### Angle and Parameter Conventions
- theta: Angle from the axis (0 < theta < π)
- phi: Azimuthal angle (0 < phi < 2π)
- All length units are in centimeters

### Transformations

The MCNP module supports transformations for positioning and orienting geometries:

```java
// Create a transformation from displacement vector and rotation matrix
MCNP_Transformation transformation = new MCNP_Transformation(displacementVector, rotationMatrix);

// Create a transformation that rotates from originVector to destVector
MCNP_Transformation transformation = new MCNP_Transformation(displacementVector, originVector, destVector);

// Apply transformation to a surface
surface.setTransformation(transformation);
```

## Particles

Particles are simply what particles are to be considered in the MCNP simulation. They are not restricted to the source particles
although, the source particle should be included in the simulation. Particles are straightforward to define:

    public Particle(String name, String id)

However, it requires knowing the MCNP `id` for the particle you want. This is alleviated by child classes like
`Photon` and `Electron` that carry that information internally. Particles also have various physics options that can be
set by providing a list of Objects in the order expected by the MCNP physics card. All these options have been given
explicit variables in the child classes to increase transparency. For example, the upper energy limit of a photon could be
set to 100.0 MeV via the following code:

    Photon photon = new Photon()
    photon.setUpperEnergyLimit(100.0)

## Tallys

Tallys represent the information to be recorded by the simulation. Tallys can have the following types:
* SURFACE_CURRENT
* SURFACE_FLUX
* CELL_FLUX
* POINT_FLUX
* PINHOLE_FLUX_IMAGE
* PLANAR_RADIOGRAPH_FLUX_IMAGE
* CYLINDRICAL_RADIOGRAPH_FLUX_IMAGE
* CELL_ENERGY_DEPOSITION
* COLLISION_HEATING
* CELL_FISSION_ENERGY
* PULSES
* CHARGE_DEPOSITION

Each representing a different type of integration to be performed by MCNP. Tallys require:
* A particle to integrate
* A list of locations to perform the integration(s)
* An integration type

The most common tallys are done on surfaces and cells and these are the tallys currently supported by the API.

## Python Interface

The API can be accessed from Python using JPype. The `py` directory contains example scripts showing how to:

1. Initialize the JVM and load the necessary Java classes
2. Define geometries, materials, and sources
3. Set up and run MCNP simulations
4. Process and visualize the results

Key examples include:
- `run_mcnp_example.py`: Shows a complete workflow for Cs137 source simulation
- `build_mcnp_job.py`: Helper functions for configuring MCNP jobs
- `mcnp_example.ipynb`: Jupyter notebook demonstrating the API usage
- `source_flux.py`: Tools for creating and managing radiation flux