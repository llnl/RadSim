package gov.llnl.rtk.mcnp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCNP_Tally {

    private static int totalTallies;

    private String name;
    private int id;
    private MCNP_Particle particle;
    private Type type;
    private double multiplier = 1.0;

    private List<Object> locations = new ArrayList<>();
    private double[] energyBins;
    private double[] timeBins;
    private double[] cosineBins;


    // todo: implement point detectors
    public enum Type {
        SURFACE_CURRENT,
        SURFACE_FLUX,
        CELL_FLUX,
        POINT_FLUX,
        PINHOLE_FLUX_IMAGE,
        PLANAR_RADIOGRAPH_FLUX_IMAGE,
        CYLINDRICAL_RADIOGRAPH_FLUX_IMAGE,
        CELL_ENERGY_DEPOSITION,
        COLLISION_HEATING,
        CELL_FISSION_ENERGY,
        PULSES,
        CHARGE_DEPOSITION
    }

    public MCNP_Tally(String name, MCNP_Particle particle, Type type) {
        totalTallies++;
        this.name = name;
        this.particle = particle;
        this.type = type;

        this.id = 10 *totalTallies;
        switch (type) {
            case SURFACE_CURRENT: id += 1; break;
            case SURFACE_FLUX: id += 2; break;
            case CELL_FLUX: id += 4; break;
            case POINT_FLUX:
            case PLANAR_RADIOGRAPH_FLUX_IMAGE:
            case PINHOLE_FLUX_IMAGE:
            case CYLINDRICAL_RADIOGRAPH_FLUX_IMAGE:
                id += 5; break;
            case CELL_ENERGY_DEPOSITION:
            case COLLISION_HEATING:
                id += 6; break;
            case CELL_FISSION_ENERGY: id += 7; break;
            case PULSES:
            case CHARGE_DEPOSITION:
                id += 8; break;
        }
    }

    public static void resetCount(){
        totalTallies = 0;
    }

    private boolean surfaceCompatible(){
        return (type == Type.SURFACE_CURRENT || type == Type.SURFACE_FLUX);
    }

    private boolean cellCompatible(){
        return !surfaceCompatible();
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void addSurfaces(MCNP_Surface... surfaces) throws Exception {
        if (!surfaceCompatible()) throw new Exception("Can't add surfaces to this tally type");
        locations.addAll(Arrays.asList(surfaces));
    }

    public void addCells(MCNP_Cell... cells) throws Exception {
        if (!cellCompatible()) throw new Exception("Can't add cells to this tally type");
        locations.addAll(Arrays.asList(cells));
    }

    public void addPoint(double x, double y, double z, double r) {
        locations.add(x);
        locations.add(y);
        locations.add(z);
        locations.add(r);
    }

    public void addEnergyBins(double ... bins) {
        energyBins = bins;
    }

    public void addTimeBins(double ... bins) {
        timeBins = bins;
    }

    public void addCosineBins(double ... bins) {
        cosineBins = bins;
    }

    public int getId() {
        return id;
    }

    public List<Object> getLocations() {
        return locations;
    }

    public List<MCNP_Card> getCards(){
        ArrayList<MCNP_Card> cards = new ArrayList<>();
        String mnemonic = "";
        switch (type) {
            case SURFACE_CURRENT:
            case SURFACE_FLUX:
            case CELL_FLUX:
            case CELL_ENERGY_DEPOSITION:
            case CELL_FISSION_ENERGY:
            case PULSES:
                mnemonic = String.format("F%d:%s", id, particle.getId()); break;
            case POINT_FLUX: mnemonic = String.format("F%d:%s", id, particle.getId()); break;
            case PINHOLE_FLUX_IMAGE: mnemonic = String.format("FIP%d:%s", id, particle.getId());break;
            case PLANAR_RADIOGRAPH_FLUX_IMAGE: mnemonic = String.format("FIR%d:%s", id, particle.getId()); break;
            case CYLINDRICAL_RADIOGRAPH_FLUX_IMAGE: mnemonic = String.format("FIC%d:%s", id, particle.getId()); break;
            case COLLISION_HEATING:
            case CHARGE_DEPOSITION:
                mnemonic = String.format("+F%d:%s", id, particle.getId()); break;
        }
        MCNP_Card locationCard = new MCNP_Card(mnemonic);
        for (Object location : locations) {
            if (location.getClass().equals(MCNP_Cell.class)) {
                locationCard.addEntry(((MCNP_Cell) location).getId());
            }
            else if (location.getClass().equals(MCNP_Surface.class)) {
                locationCard.addEntry(((MCNP_Surface) location).getId());
            }
            else {
                locationCard.addEntry(location);
            }
        }
        locationCard.addComment(name);
        cards.add(locationCard);

        if (multiplier != 1.0) {
            MCNP_Card multiplierCard = new MCNP_Card("FM" + id);
            multiplierCard.addEntry(multiplier);
            cards.add(multiplierCard);
        }

        if (energyBins != null) {
            MCNP_Card energyBinsCard = new MCNP_Card("E" + id);
            for (double bin : energyBins) {
                energyBinsCard.addEntry(bin);
            }
            cards.add(energyBinsCard);
        }
        if (timeBins != null) {
            MCNP_Card timeBinsCard = new MCNP_Card("T" + id);
            for (double bin : timeBins) {
                timeBinsCard.addEntry(bin);
            }
            cards.add(timeBinsCard);
        }
        if (cosineBins != null) {
            MCNP_Card cosineBinsCard = new MCNP_Card("C" + id);
            for (double bin : cosineBins) {
                cosineBinsCard.addEntry(bin);
            }
            cards.add(cosineBinsCard);
        }

        return cards;
    }

    public static void main(String ... args) throws Exception {
        MCNP_Tally tally = new MCNP_Tally("Test", new MCNP_Photon(), Type.COLLISION_HEATING);
        for (MCNP_Card card : tally.getCards()){
            System.out.println(card);
        }
    }


}
