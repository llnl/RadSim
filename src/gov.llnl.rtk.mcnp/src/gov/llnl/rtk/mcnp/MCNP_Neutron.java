package gov.llnl.rtk.mcnp;

public class MCNP_Neutron extends MCNP_Particle {

    private double upperEnergyLimit = 100.0;
    private double analogEnergyLimit = 0.0;
    private boolean unresolvedResonanceRangeTableTreatment = true;
    // todo: add other options

    public MCNP_Neutron() {
        super("Neutron", "n");
        setPhysicsOptions();
    }

    private void setPhysicsOptions() {
        setPhysicsOptions(
                upperEnergyLimit
        );
    }

    public static void main(String ... args) {
        MCNP_Neutron neutron = new MCNP_Neutron();
        System.out.println(neutron.getPhysicsCard());
    }
}
