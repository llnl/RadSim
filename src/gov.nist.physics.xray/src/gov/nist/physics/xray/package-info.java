/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/package-info.java to edit this template
 */
package gov.nist.physics.xray;


/* 
TODO

Absolutely, that's a great approach. Here’s a clear, actionable prompt you can use for a fresh session (or as a planning checklist for your team):

---

## 1. What We Need for the Doppler-Broadened Klein-Nishina (KN) Calculation

To accurately model Doppler-broadened Compton scattering (as in high-resolution gamma spectroscopy), you need:

| Requirement                  | Details                                                                                      |
|------------------------------|---------------------------------------------------------------------------------------------|
| **Electron momentum profile**| The Compton profile \( J(p_z) \) for each electron shell (or total), as a function of electron momentum \( p_z \). |
| **Shell structure**          | Ability to access profiles for individual shells (K, L1, L2, etc.) and/or the total atom.   |
| **Energy grid**              | Interpolation routines to evaluate the profile at arbitrary \( p_z \) values.               |
| **Units and normalization**  | Profiles must be in correct units (usually electrons per atomic unit momentum) and normalized.|
| **Integration routine**      | Numerical integration over the profile to compute the Doppler-broadened cross section for a given incident and scattered photon energy. |

---

## 2. Where to Get the Profile Data

| Source/Database                             | Access/Format                                    | Notes                                       |
|---------------------------------------------|--------------------------------------------------|---------------------------------------------|
| **NIST XCOM**                              | https://physics.nist.gov/PhysRefData/Xcom/html/xcom1.html | XCOM itself does NOT provide Compton profiles—only cross sections.|
| **NIST Electron Compton Profile Database**  | https://physics.nist.gov/PhysRefData/Compton/Profile.html | Tabulated \( J(p_z) \) for elements, shells, and total.|
| **LLNL Atomic Physics Codes**               | https://www-nds.iaea.org/public/llnl/           | Some LLNL codes provide similar data.       |
| **Other sources**                           | EADL, Biggs/Lighthill, etc.                      | For completeness, but NIST is the standard. |

**Summary:**  
- For Doppler broadening, you want the NIST Compton Profile database, not XCOM.

---

## 3. What the Integration Task Would Need to Be

To integrate Compton profile data into your xray data access layer (or a related physics data wrapper):

**a. Data Model/Interface**
- Extend your data model to include Compton profiles:
    - Add a `ComptonProfileLibrary` interface (see previous message).
    - Add `ComptonProfile` objects (element, shell, pz[], Jpz[]).

**b. Data Parsing**
- Write a parser for the NIST Compton Profile text files (or download and convert to a more convenient format).
- Store profiles in an efficient and accessible structure (e.g., in-memory maps, indexed by element and shell).

**c. API Extension**
- Provide methods to retrieve the profile for a given element and shell.
- Optionally, provide interpolation utilities for arbitrary \( p_z \).

**d. Integration with Physics Calculations**
- In your Doppler-broadened KN calculator, use the profile to perform the convolution/integration:
    - For a given incident and scattered photon energy, compute the required electron momentum.
    - Integrate \( J(p_z) \) over the relevant \( p_z \) range for the scattering event.
    - Combine with the Klein-Nishina cross section as needed.

**e. Documentation and Testing**
- Document the provenance, units, and normalization of the profile data.
- Validate your integration against reference calculations (e.g., GEANT4, NIST publications).

---

## Example Prompt for a Fresh Session

> I want to extend our physics data wrapper to support Doppler-broadened Compton scattering using the NIST Compton profile database.  
> 
> 1. What data and interfaces do I need to add for Compton profiles (per element, per shell)?  
> 2. Where do I get the Compton profile data, and what is the best way to parse and store it?  
> 3. How do I connect this profile data to my Doppler-broadened Klein-Nishina calculation (i.e., what does the integration look like)?  
> 4. What are the best practices for units, normalization, and API design for this feature?

---

*/