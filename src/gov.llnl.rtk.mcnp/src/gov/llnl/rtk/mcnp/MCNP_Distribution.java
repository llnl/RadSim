package gov.llnl.rtk.mcnp;

import java.util.ArrayList;
import java.util.List;

public class MCNP_Distribution {

    private static int totalDistributions;

    private String name;
    private int id;
    private NodeType nodeType;
    private ProbabilityType probabilityType, biasesType;

    private Object[] nodes;
    private double[] probabilities;
    private double[] biases;

    public enum NodeType {
        HISTOGRAM,
        DISCRETE,
        DENSITY,
        DISTRIBUTION_NUMBER
    }

    public enum ProbabilityType {
        PROBABILITIES,
        CUMULATIVE_PROBABILITIES,
        CELL_DISTRIBUTIONS,
        INTENSITIES,
        PRE_DEFINED
    }

    public MCNP_Distribution(String name) {
        totalDistributions++;
        this.name = name;
        this.id = totalDistributions;
    }

    public static void resetCount(){
        totalDistributions = 0;
    }

    public void setNodes(NodeType type, Object ... nodes) {
        nodeType = type;
        this.nodes = nodes;
    }

    public void setNodes(NodeType type, double[] nodes) {
        nodeType = type;
        this.nodes = new Double[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            this.nodes[i] = nodes[i];
        }
    }

    public void setProbabilities(ProbabilityType type, double ... probabilities) {
        probabilityType = type;
        this.probabilities = probabilities;
    }

    public void setBiases(ProbabilityType type, double ... biases) {
        biasesType = type;
        this.biases = biases;
    }

    public int getId() {
        return id;
    }

    public List<MCNP_Card> getCards() {
        ArrayList<MCNP_Card> cards = new ArrayList<>();

        if (nodes != null) {
            MCNP_Card nodeCard = new MCNP_Card("SI" + id);
            switch (nodeType) {
                case HISTOGRAM:
                    nodeCard.addEntry("H");
                    break;
                case DISCRETE:
                    nodeCard.addEntry("L");
                    break;
                case DENSITY:
                    nodeCard.addEntry("A");
                    break;
                case DISTRIBUTION_NUMBER:
                    nodeCard.addEntry("S");
                    break;
            }
            for (Object node : nodes) {
                if (node.getClass() == MCNP_Distribution.class) {
                    nodeCard.addEntry(((MCNP_Distribution) node).getId());
                } else {
                    nodeCard.addEntry(node);
                }
            }
            nodeCard.addComment(name + " Nodes");
            cards.add(nodeCard);
        }

        if (probabilities != null) {
            MCNP_Card probabilityCard = new MCNP_Card("SP" + id);
            switch (probabilityType) {
                case PROBABILITIES:
                    probabilityCard.addEntry("D");
                    break;
                case CUMULATIVE_PROBABILITIES:
                    probabilityCard.addEntry("C");
                    break;
                case CELL_DISTRIBUTIONS:
                    probabilityCard.addEntry("V");
                    break;
                case INTENSITIES:
                    probabilityCard.addEntry("W");
                    break;
            }
            for (double probability : probabilities) {
                probabilityCard.addEntry(probability);
            }
            probabilityCard.addComment(name + " Probabilities");
            cards.add(probabilityCard);
        }

        if (biases != null) {
            MCNP_Card biasesCard = new MCNP_Card("SB" + id);
            switch (biasesType) {
                case PROBABILITIES:
                    biasesCard.addEntry("D");
                    break;
                case CUMULATIVE_PROBABILITIES:
                    biasesCard.addEntry("C");
                    break;
                case CELL_DISTRIBUTIONS:
                    biasesCard.addEntry("V");
                    break;
                case INTENSITIES:
                    biasesCard.addEntry("W");
                    break;
            }
            for (double bias : biases) {
                biasesCard.addEntry(bias);
            }
            biasesCard.addComment(name + " Biases");
            cards.add(biasesCard);
        }

        // Add other distributions in this distribution
        if (nodes != null) {
            for (Object node : nodes) {
                if (node.getClass() == MCNP_Distribution.class) {
                    cards.addAll(((MCNP_Distribution) node).getCards());
                }
            }
        }

        return cards;
    }
}
