import math
import startJVM
import jpype
import jpype.imports
import numpy as np
import matplotlib.pyplot as plt

from gov.llnl.rtk.physics import (
    Nuclides,
    SourceImpl,
    DecayCalculator,
    EmissionCalculator,
    Quantity,
    Units,
    Material,
    KleinNishinaDistribution,
    Elements,
)
from gov.bnl.nndc.ensdf.decay import BNLDecayLibrary
from gov.nist.physics.xray import NISTXrayLibrary
from gov.nist.physics.xcom import XCOMPhotonCrossSectionLibrary
from gov.llnl.rtk.response.deposition import CuboidChordGeometry, DepositionCalculator
from java.nio.file import Paths
from java.util import ArrayList

# -------------------------------------------------
# Optional plotting style
# -------------------------------------------------
try:
    plt.style.use("matplotlibrc")
except Exception:
    pass

# -------------------------------------------------
# Centralized detector + source definition
# -------------------------------------------------
INCH_TO_CM = 2.54
MAX_ENERGY_KEV = 3000.0
NBINS = 3000

DETECTOR = {
    "name": "NaI 2x4x16 in",
    "dimensions_in": (2.0, 4.0, 16.0),   # length, width, height
    "density_g_cm3": 3.67,
    # Stoichiometric NaI. Tl activator omitted from bulk transport model.
    "composition": [
        ("Na", 1.0),
        ("I", 1.0),
    ],
}

SOURCE = {
    "position_cm": (300.0, 0.0, 0.0),  # source at +x, 3 m away
    "apply_inverse_square": False,
}

# Convert detector dimensions once
DETECTOR["dimensions_cm"] = tuple(d * INCH_TO_CM for d in DETECTOR["dimensions_in"])


# -------------------------------------------------
# Helpers
# -------------------------------------------------
def add_delta(spectrum, energy_keV, amplitude):
    energy_keV = float(energy_keV)
    amplitude = float(amplitude)

    if not np.isfinite(energy_keV) or not np.isfinite(amplitude):
        return None
    if amplitude <= 0:
        return None

    idx = int(np.floor(energy_keV))
    if 0 <= idx < len(spectrum):
        spectrum[idx] += amplitude
        return idx
    return None


def build_detector_material(detector_cfg):
    builder = Material.builder()
    builder.density(Quantity.of(float(detector_cfg["density_g_cm3"]), "g/cm^3"))
    for symbol, amount in detector_cfg["composition"]:
        builder.add(Elements.get(symbol), float(amount))
    return builder.build()


def build_detector_geometry(detector_cfg):
    length_cm, width_cm, height_cm = detector_cfg["dimensions_cm"]
    chords = CuboidChordGeometry()
    chords.setDimensions(
        Quantity.of(float(length_cm), "cm"),
        Quantity.of(float(width_cm), "cm"),
        Quantity.of(float(height_cm), "cm"),
    )
    return chords


def get_projected_area_cm2_for_axis_aligned_source(detector_cfg, source_pos_cm):
    """
    For a source located on one coordinate axis, return the detector face area
    normal to that axis.

    Detector dimensions are interpreted as:
      x -> length
      y -> width
      z -> height
    """
    x, y, z = source_pos_cm
    length_cm, width_cm, height_cm = detector_cfg["dimensions_cm"]

    ax = abs(float(x))
    ay = abs(float(y))
    az = abs(float(z))

    # Use the dominant axis direction
    if ax >= ay and ax >= az:
        return width_cm * height_cm     # face normal to x
    elif ay >= ax and ay >= az:
        return length_cm * height_cm    # face normal to y
    else:
        return length_cm * width_cm     # face normal to z


def geometric_factor(detector_cfg, source_cfg):
    """
    Far-field geometric factor:
        projected_area / (4*pi*r^2)
    """
    sx, sy, sz = source_cfg["position_cm"]
    r_cm = math.sqrt(sx * sx + sy * sy + sz * sz)
    if r_cm <= 0:
        return 1.0

    area_cm2 = get_projected_area_cm2_for_axis_aligned_source(
        detector_cfg, source_cfg["position_cm"]
    )
    return area_cm2 / (4.0 * math.pi * r_cm * r_cm)


# -------------------------------------------------
# Build decay library
# -------------------------------------------------
bnllib = BNLDecayLibrary()
bnllib.setXrayLibrary(NISTXrayLibrary.getInstance())
bnllib.loadFile(Paths.get("../src/gov.bnl.nndc.ensdf/py/BNL2023.txt"))

# -------------------------------------------------
# Source: pure Cs-137, 100 Bq
# -------------------------------------------------
cs137 = SourceImpl.fromActivity(Nuclides.get("Cs137"), Quantity.of(100.0, "Bq"))

# -------------------------------------------------
# Compute transient equilibrium mixture
# -------------------------------------------------
dc = DecayCalculator()
dc.setDecayLibrary(bnllib)

eq_sources = dc.transientEquilibrium(cs137)

print("Transient equilibrium composition from pure Cs-137:")
for s in eq_sources:
    nuclide = str(s.getNuclide())
    atoms = float(s.getAtoms())
    activity = float(s.getActivity(Units.get("Bq")))
    print(f"{nuclide:>10} | atoms = {atoms:.6e} | activity = {activity:.6e} Bq")

source_list_eq = ArrayList()
for s in eq_sources:
    source_list_eq.add(s)

# -------------------------------------------------
# Emission calculation from equilibrium mixture
# -------------------------------------------------
emcal = EmissionCalculator()
emcal.setDecayLibrary(bnllib)
em_out = emcal.apply(source_list_eq)

beta_E, beta_I = [], []
gamma_E, gamma_I = [], []
xray_E, xray_I = [], []

print("\nEmissions from transient equilibrium mixture:")
for emission in em_out.getBetas():
    e = float(emission.getEnergy().getValue())
    i = float(emission.getIntensity().getValue())
    beta_E.append(e)
    beta_I.append(i)
    print(f"Beta- : E = {e:.6f} keV, I = {i:.6e}")

for emission in em_out.getGammas():
    e = float(emission.getEnergy().getValue())
    i = float(emission.getIntensity().getValue())
    gamma_E.append(e)
    gamma_I.append(i)
    print(f"Gamma : E = {e:.6f} keV, I = {i:.6e}")

for emission in em_out.getXrays():
    e = float(emission.getEnergy().getValue())
    i = float(emission.getIntensity().getValue())
    try:
        name = str(emission.getName())
    except Exception:
        name = "xray"
    xray_E.append(e)
    xray_I.append(i)
    print(f"Xray  : E = {e:.6f} keV, I = {i:.6e}, {name}")

beta_E = np.array(beta_E, dtype=float)
beta_I = np.array(beta_I, dtype=float)
gamma_E = np.array(gamma_E, dtype=float)
gamma_I = np.array(gamma_I, dtype=float)
xray_E = np.array(xray_E, dtype=float)
xray_I = np.array(xray_I, dtype=float)

# -------------------------------------------------
# Combine photon lines and filter invalid entries
# -------------------------------------------------
if len(gamma_E) + len(xray_E) == 0:
    raise RuntimeError("No gamma or X-ray emissions found.")

E_src = np.concatenate([gamma_E, xray_E]) if len(xray_E) > 0 else gamma_E.copy()
I_src = np.concatenate([gamma_I, xray_I]) if len(xray_I) > 0 else gamma_I.copy()

valid_mask = np.isfinite(E_src) & np.isfinite(I_src) & (E_src > 0.0) & (I_src > 0.0)

if not np.all(valid_mask):
    print("\nSkipping invalid photon lines:")
    for e, i in zip(E_src[~valid_mask], I_src[~valid_mask]):
        print(f"  bad line: E={e}, I={i}")

E_src = E_src[valid_mask]
I_src = I_src[valid_mask]

# -------------------------------------------------
# Detector geometry and material
# -------------------------------------------------
detector_material = build_detector_material(DETECTOR)
chords = build_detector_geometry(DETECTOR)

calc = DepositionCalculator(
    detector_material,
    chords,
    KleinNishinaDistribution(),
    MAX_ENERGY_KEV,
)

calc.setUnits(Units.get("cm"))
calc.setPhotonCrossSectionLibrary(XCOMPhotonCrossSectionLibrary.getInstance())

sx, sy, sz = SOURCE["position_cm"]
calc.setPosition(
    Quantity.of(float(sx), "cm"),
    Quantity.of(float(sy), "cm"),
    Quantity.of(float(sz), "cm"),
)

geom_factor = geometric_factor(DETECTOR, SOURCE) if SOURCE["apply_inverse_square"] else 1.0

print("\nDetector configuration:")
print(f"  name              = {DETECTOR['name']}")
print(f"  dimensions_in     = {DETECTOR['dimensions_in']}")
print(f"  dimensions_cm     = {DETECTOR['dimensions_cm']}")
print(f"  density_g_cm3     = {DETECTOR['density_g_cm3']}")
print(f"  composition       = {DETECTOR['composition']}")
print(f"  source_position_cm= {SOURCE['position_cm']}")
print(f"  inverse_square    = {SOURCE['apply_inverse_square']}")
print(f"  geometric_factor  = {geom_factor:.6e}")

# -------------------------------------------------
# Build total deposited-energy spectrum
# -------------------------------------------------
E_dep = np.arange(NBINS, dtype=float) + 0.5

total_initial = np.zeros(NBINS, dtype=float)
total_scattered = np.zeros(NBINS, dtype=float)
total_spectrum = np.zeros(NBINS, dtype=float)

line_summaries = []

for E_line, I_line in zip(E_src, I_src):
    E_line = float(E_line)
    I_line = float(I_line)

    if not math.isfinite(E_line) or not math.isfinite(I_line):
        print(f"Skipping non-finite emission: E={E_line}, I={I_line}")
        continue
    if E_line <= 0.0 or I_line <= 0.0:
        print(f"Skipping non-positive emission: E={E_line}, I={I_line}")
        continue
    if E_line > NBINS:
        print(f"Skipping emission above deposition range: E={E_line}, I={I_line}")
        continue

    line_scale = I_line * geom_factor

    try:
        dep = calc.compute(Quantity.of(E_line, "keV"), line_scale)
    except Exception as err:
        print(f"Skipping emission due to compute failure: E={E_line}, I={I_line}, error={err}")
        continue

    initial = np.array(dep.initial, dtype=float)
    scattered = np.array(dep.scattered, dtype=float)

    if len(initial) < NBINS:
        initial = np.pad(initial, (0, NBINS - len(initial)))
    else:
        initial = initial[:NBINS]

    if len(scattered) < NBINS:
        scattered = np.pad(scattered, (0, NBINS - len(scattered)))
    else:
        scattered = scattered[:NBINS]

    total_initial += initial
    total_scattered += scattered

    line_total = initial + scattered
    add_delta(line_total, dep.energy, dep.totalPhoto)
    add_delta(line_total, dep.singleEnergy, dep.singleEscape)
    add_delta(line_total, dep.doubleEnergy, dep.doubleEscape)

    total_spectrum += line_total

    line_summaries.append(
        {
            "energy": E_line,
            "intensity": I_line,
            "scaled_intensity": line_scale,
            "photo": float(dep.totalPhoto),
            "pair": float(dep.totalPair),
            "single_escape": float(dep.singleEscape),
            "double_escape": float(dep.doubleEscape),
            "continuum_sum": float(initial.sum() + scattered.sum()),
            "total_sum": float(line_total.sum()),
        }
    )

# -------------------------------------------------
# Plot
# -------------------------------------------------
plt.rcParams.update(
    {
        "font.size": 13,
        "axes.titlesize": 15,
        "axes.labelsize": 15,
        "legend.fontsize": 11,
    }
)

fig, axes = plt.subplots(3, 1, figsize=(12, 14), constrained_layout=True)
ax1, ax2, ax3 = axes

# Panel 1: equilibrium activities
labels = [str(s.getNuclide()) for s in eq_sources]
activities = [float(s.getActivity(Units.get("Bq"))) for s in eq_sources]

x = np.arange(len(labels))
ax1.bar(x, activities)
ax1.set_xticks(x)
ax1.set_xticklabels(labels, rotation=45, ha="right")
ax1.set_yscale("log")
ax1.set_ylabel("Activity (Bq)")
ax1.set_title("Transient equilibrium composition from pure Cs-137")
ax1.grid(True, axis="y", alpha=0.3)

# Panel 2: emission lines
if len(beta_E) > 0:
    beta_plot = ax2.stem(beta_E, beta_I, linefmt="b-", markerfmt="bo", basefmt=" ")
    plt.setp(beta_plot.stemlines, linewidth=1.6)
    plt.setp(beta_plot.markerline, markersize=4)

if len(gamma_E) > 0:
    gamma_plot = ax2.stem(gamma_E, gamma_I, linefmt="r-", markerfmt="ro", basefmt=" ")
    plt.setp(gamma_plot.stemlines, linewidth=1.6)
    plt.setp(gamma_plot.markerline, markersize=4)

if len(xray_E) > 0:
    xray_plot = ax2.stem(xray_E, xray_I, linefmt="g-", markerfmt="go", basefmt=" ")
    plt.setp(xray_plot.stemlines, linewidth=1.6)
    plt.setp(xray_plot.markerline, markersize=4)

ax2.set_xlabel("Emission energy (keV)")
ax2.set_ylabel("Intensity")
ax2.set_yscale("log")
ax2.set_title("Emissions from transient equilibrium mixture")
ax2.grid(True, alpha=0.3)

# Panel 3: Deposited spectrum
ax3.plot(E_dep, total_spectrum, linewidth=2, label="Deposited spectrum")
ax3.set_yscale("log")
ax3.set_xlim(0, 800)
ax3.set_ylim(1e-5, 1e2)
ax3.set_xlabel("Detected energy (keV)")
ax3.set_ylabel("Contribution")
ax3.set_title("Cs-137 equilibrium point source: deposited-energy spectrum")
ax3.grid(True, alpha=0.3)
ax3.legend()

plt.show()

# -------------------------------------------------
# Numerical summary
# -------------------------------------------------
print("\nPhoton-line deposition summary:")
for item in line_summaries:
    print(
        f"E = {item['energy']:9.3f} keV | "
        f"I = {item['intensity']:.6e} | "
        f"I_eff = {item['scaled_intensity']:.6e} | "
        f"photo = {item['photo']:.6e} | "
        f"pair = {item['pair']:.6e} | "
        f"single = {item['single_escape']:.6e} | "
        f"double = {item['double_escape']:.6e} | "
        f"continuum sum = {item['continuum_sum']:.6e} | "
        f"total sum = {item['total_sum']:.6e}"
    )

print(f"\nTotal deposited spectrum sum: {total_spectrum.sum():.6e}")