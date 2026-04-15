package gov.llnl.rtk.mcnp;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for MCNP_Particle classes.
 */
public class MCNP_ParticleNGTest {

    @BeforeMethod
    public void setUp() {
        // Reset MCNP counters between tests
        MCNP_Utils.resetAllCounts();
    }

    @Test
    public void testPhotonCreation() {
        // Create photon particle
        MCNP_Photon photon = new MCNP_Photon();

        // Verify photon properties
        assertNotNull(photon);
        assertEquals("p", photon.getId());

        // Check toString representation
        String photonStr = photon.toString();
        assertNotNull(photonStr);
        assertTrue(photonStr.contains("p"));
    }

    @Test
    public void testElectronCreation() {
        // Create electron particle
        MCNP_Electron electron = new MCNP_Electron();

        // Verify electron properties
        assertNotNull(electron);
        assertEquals("e", electron.getId());

        // Check toString representation
        String electronStr = electron.toString();
        assertNotNull(electronStr);
        assertTrue(electronStr.contains("e"));

        // Test bremsstrahlung settings
        electron.setNumBremPhotonsPerStep(1000);
        electron.setNumBremPerStep(1000);

        // The toString method should still work after setting properties
        String electronWithBremsStr = electron.toString();
        assertNotNull(electronWithBremsStr);
    }

    @Test
    public void testNeutronCreation() {
        // Create neutron particle
        MCNP_Neutron neutron = new MCNP_Neutron();

        // Verify neutron properties
        assertNotNull(neutron);
        assertEquals("n", neutron.getId());

        // Check toString representation
        String neutronStr = neutron.toString();
        assertNotNull(neutronStr);
        assertTrue(neutronStr.contains("n"));
    }

    @Test
    public void testParticleEquality() {
        // Create two particles of each type
        MCNP_Photon photon1 = new MCNP_Photon();
        MCNP_Photon photon2 = new MCNP_Photon();

        MCNP_Electron electron1 = new MCNP_Electron();
        MCNP_Electron electron2 = new MCNP_Electron();

        MCNP_Neutron neutron1 = new MCNP_Neutron();
        MCNP_Neutron neutron2 = new MCNP_Neutron();

        // Check that particles of same type have same ID
        assertEquals(photon1.getId(), photon2.getId());
        assertEquals(electron1.getId(), electron2.getId());
        assertEquals(neutron1.getId(), neutron2.getId());

        // Check that particles of different types have different IDs
        assertNotEquals(photon1.getId(), electron1.getId());
        assertNotEquals(photon1.getId(), neutron1.getId());
        assertNotEquals(electron1.getId(), neutron1.getId());
    }

    @Test
    public void testParticleInheritance() {
        // Create particles
        MCNP_Photon photon = new MCNP_Photon();
        MCNP_Electron electron = new MCNP_Electron();
        MCNP_Neutron neutron = new MCNP_Neutron();

        // Verify they are all instances of MCNP_Particle
        assertTrue(photon instanceof MCNP_Particle);
        assertTrue(electron instanceof MCNP_Particle);
        assertTrue(neutron instanceof MCNP_Particle);
    }
}