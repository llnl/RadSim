package gov.llnl.rtk.mcnp;

import gov.llnl.math.euclidean.Vector3;

import java.util.ArrayList;
import java.util.List;

public class MCNP_Source {

    private String name;
    private MCNP_Particle particle;
    private int numParticles;

    // todo: implement other distributions
    private Vector3 vector, position, axis;
    private MCNP_Distribution radialDistribution;
    private MCNP_Distribution axialDistribution;
    private MCNP_Distribution energyDistribution;
    private MCNP_Distribution directionalDistribution;

    private MCNP_Cell cell;

    public MCNP_Source(String name, MCNP_Particle particle, int numParticles) {
        this.name = name;
        this.particle = particle;
        this.numParticles = numParticles;
    }

    public void setUniformSphericalDistribution(Vector3 center, double radius){
        MCNP_Distribution radialDistribution = new MCNP_Distribution("Source Radial Distribution");
        radialDistribution.setNodes(MCNP_Distribution.NodeType.HISTOGRAM, 0.0, radius);
        radialDistribution.setProbabilities(MCNP_Distribution.ProbabilityType.PRE_DEFINED, -21, 2);

        setPosition(center);
        setRadialDistribution(radialDistribution);
    }

    public void setUniformCylindricalDistribution(Vector3 center, Vector3 axis, double radius, double height) {
        MCNP_Distribution radialDistribution = new MCNP_Distribution("Source Radial Distribution");
        radialDistribution.setNodes(MCNP_Distribution.NodeType.HISTOGRAM, 0.0, radius);
        radialDistribution.setProbabilities(MCNP_Distribution.ProbabilityType.PRE_DEFINED, -21, 1);

        MCNP_Distribution axialDistribution = new MCNP_Distribution("Source Axial Distribution");
        axialDistribution.setNodes(MCNP_Distribution.NodeType.HISTOGRAM, -height/2.0, height/2.0);
        axialDistribution.setProbabilities(MCNP_Distribution.ProbabilityType.PROBABILITIES, 0, 1);

        setPosition(center);
        setAxis(axis);
        setRadialDistribution(radialDistribution);
        setAxialDistribution(axialDistribution);
    }

    public void setRadialDistribution(MCNP_Distribution radialDistribution) {
        this.radialDistribution = radialDistribution;
    }

    public void setAxialDistribution(MCNP_Distribution axialDistribution) {
        this.axialDistribution = axialDistribution;
    }

    public void setEnergyDistribution(MCNP_Distribution energyDistribution) {
        this.energyDistribution = energyDistribution;
    }

    public void setDirectionalDistribution(MCNP_Distribution directionalDistribution) {
        this.directionalDistribution = directionalDistribution;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setAxis(Vector3 axis) {
        this.axis = axis;
    }

    public void setVector(Vector3 vector) {
        this.vector = vector;
    }

    public void setCell(MCNP_Cell cell) {
        this.cell = cell;
    }

    public List<MCNP_Card> getCards() {
        ArrayList<MCNP_Card> cards = new ArrayList<>();
        cards.add(new MCNP_Card("NPS " + numParticles));

        MCNP_Card definitionCard = new MCNP_Card("SDEF");
        definitionCard.addEntry("PAR=" + particle.getId());

        if (cell != null) {
            definitionCard.addEntry("CEL=" + cell.getId());
        }

        if (position != null) {
            definitionCard.addEntry("POS=");
            definitionCard.addEntry(position.getX());
            definitionCard.addEntry(position.getY());
            definitionCard.addEntry(position.getZ());
        }

        if (axis != null) {
            definitionCard.addEntry("AXS=");
            definitionCard.addEntry(axis.getX());
            definitionCard.addEntry(axis.getY());
            definitionCard.addEntry(axis.getZ());
        }

        if (vector != null) {
            definitionCard.addEntry("VEC=");
            definitionCard.addEntry(vector.getX());
            definitionCard.addEntry(vector.getY());
            definitionCard.addEntry(vector.getZ());
        }

        if (radialDistribution != null) {
            definitionCard.addEntry("RAD=D" + radialDistribution.getId());
        }

        if (axialDistribution != null) {
            definitionCard.addEntry("EXT=D" + axialDistribution.getId());
        }

        if (energyDistribution != null) {
            definitionCard.addEntry("ERG=D" + energyDistribution.getId());
        }

        if (directionalDistribution != null) {
            definitionCard.addEntry("DIR=D" + directionalDistribution.getId());
        }

        definitionCard.addComment(name);
        cards.add(definitionCard);

        if (radialDistribution != null) {
            cards.addAll(radialDistribution.getCards());
        }

        if (axialDistribution != null) {
            cards.addAll(axialDistribution.getCards());
        }

        if (energyDistribution != null) {
            cards.addAll(energyDistribution.getCards());
        }

        if (directionalDistribution != null) {
            cards.addAll(directionalDistribution.getCards());
        }

        return cards;
    }


}
