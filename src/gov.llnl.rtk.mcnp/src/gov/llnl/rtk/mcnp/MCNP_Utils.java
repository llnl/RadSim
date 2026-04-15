package gov.llnl.rtk.mcnp;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.rtk.flux.*;
import gov.llnl.rtk.physics.ConicalSection;
import gov.llnl.rtk.physics.CylindricalSection;
import gov.llnl.rtk.physics.Section;
import gov.llnl.rtk.physics.SphericalSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCNP_Utils {

    public static Object valueIfTrueElse(boolean condition, Object ifTrue, Object ifFalse) {
        if (condition) return ifTrue;
        return ifFalse;
    }

    public static void resetAllCounts(){
        MCNP_Cell.resetCount();
        MCNP_Surface.resetCount();
        MCNP_Material.resetCount();
        MCNP_Tally.resetCount();
        MCNP_Transformation.resetCount();
        MCNP_Distribution.resetCount();
    }

    public static double getContainingRadius(Section section) {
        return getContainingRadius(Vector3.ZERO, section);
    }

    public static double getContainingRadius(Vector3 center, Section section) {
        if (section.getClass() == SphericalSection.class) {
            return getContainingRadius(center, (SphericalSection) section);
        }
        if (section.getClass() == ConicalSection.class) {
            return getContainingRadius(center, (ConicalSection) section);
        }
        if (section.getClass() == CylindricalSection.class) {
            return getContainingRadius(center, (CylindricalSection) section);
        }
        return Double.NaN;
    }

    public static double getContainingRadius(Vector3 center, SphericalSection section) {
        Vector3 distance = Vector3.of(
                center.getX() - section.getOrigin().getX(),
                center.getY() - section.getOrigin().getY(),
                center.getZ() - section.getOrigin().getZ()
        );
        return distance.norm() + section.getOuterRadius();
    }

    public static double getContainingRadius(Vector3 center, ConicalSection section) {
        Vector3 distance = Vector3.of(
                center.getX() - section.getOrigin().getX(),
                center.getY() - section.getOrigin().getY(),
                center.getZ() - section.getOrigin().getZ()
        );
        return distance.norm() + section.getEndLength() / Math.cos(section.getTheta2());
    }

    public static double getContainingRadius(Vector3 center, CylindricalSection section) {
        Vector3 distance = Vector3.of(
                center.getX() - section.getOrigin().getX(),
                center.getY() - section.getOrigin().getY(),
                center.getZ() - section.getOrigin().getZ()
        );
        return distance.norm() + Math.sqrt(Math.pow(section.getOuterRadius(), 2.0) + Math.pow(section.getDepth(), 2.0));
    }

    public static MCNP_Distribution convertFluxToDistribution(FluxBinned flux) {

        // Set the distribution
        MCNP_Distribution distribution = new MCNP_Distribution("Distribution");

        // Init the distributions
        MCNP_Distribution lineDistribution = new MCNP_Distribution("Line Distribution");
        MCNP_Distribution groupDistribution = new MCNP_Distribution("Group Distribution");

        // Parse the flux lines
        List<FluxLineStep> lines = flux.getPhotonLines();
        double[] energies = new double[lines.size()];
        double[] intensities = new double[lines.size()];
        double[] biases = new double[lines.size()];
        double totalLineIntensities = 0.0;
        for (int i = 0; i < lines.size(); i++) {
            energies[i] = 0.001 * lines.get(i).getEnergy();
            intensities[i] = lines.get(i).getIntensity();
            biases[i] = 1.0;
            totalLineIntensities += intensities[i];
        }
        lineDistribution.setNodes(MCNP_Distribution.NodeType.DISCRETE, energies);
        lineDistribution.setProbabilities(MCNP_Distribution.ProbabilityType.PROBABILITIES, intensities);
        lineDistribution.setBiases(MCNP_Distribution.ProbabilityType.PROBABILITIES, biases);

        // Parse the flux groups
        List<FluxGroupBin> groups = flux.getPhotonGroups();
        double[] lowerEnergies = new double[groups.size()];
        double[] upperEnergies = new double[groups.size()];
        double[] counts = new double[groups.size()];
        double totalGroupCounts = 0.0;
        for (int i = 0; i < groups.size(); i++) {
            lowerEnergies[i] = 0.001 * groups.get(i).getEnergyLower();
            upperEnergies[i] = 0.001 * groups.get(i).getEnergyUpper();
            counts[i] = groups.get(i).getCounts();
            totalGroupCounts += counts[i];
        }


        ArrayList<Double> histBins = new ArrayList<>();
        ArrayList<Double> histValues = new ArrayList<>();
        ArrayList<Double> histBiases = new ArrayList<>();
        for (int i = 0; i < lowerEnergies.length; i++) {
            if (histBins.isEmpty() || lowerEnergies[i] != histBins.get(histBins.size() - 1)) {
                histBins.add(lowerEnergies[i]);
                histValues.add(0.0);
                histBiases.add(0.0);
            }
            if (upperEnergies[i] != histBins.get(histBins.size() - 1)) {
                histBins.add(upperEnergies[i]);
                histValues.add(counts[i]);
                if (counts[i] == 0.0) {
                    histBiases.add(0.0);
                }
                else {
                    histBiases.add(1.0);
                }
            }
        }
        groupDistribution.setNodes(MCNP_Distribution.NodeType.HISTOGRAM,
                histBins.stream().mapToDouble(d -> d).toArray());
        groupDistribution.setProbabilities(MCNP_Distribution.ProbabilityType.PROBABILITIES,
                histValues.stream().mapToDouble(d -> d).toArray());
        groupDistribution.setBiases(MCNP_Distribution.ProbabilityType.PROBABILITIES,
                histBiases.stream().mapToDouble(d -> d).toArray());

        // Combine the two distributions
        distribution.setNodes(MCNP_Distribution.NodeType.DISTRIBUTION_NUMBER,
                lineDistribution, groupDistribution);
        distribution.setProbabilities(MCNP_Distribution.ProbabilityType.PROBABILITIES,
                totalLineIntensities, totalGroupCounts);

        if (!flux.getPhotonLines().isEmpty() && !flux.getPhotonGroups().isEmpty()) {
            return distribution;
        }
        if (!flux.getPhotonLines().isEmpty()) {
            return lineDistribution;
        }
        if (!flux.getPhotonGroups().isEmpty()) {
            return groupDistribution;
        }

        return null;
    }

    public static MCNP_Distribution convertFluxToDistribution(FluxTrapezoid flux) {
        MCNP_Distribution distribution = new MCNP_Distribution("Distribution");

        // Init the distributions
        MCNP_Distribution lineDistribution = new MCNP_Distribution("Line Distribution");
        MCNP_Distribution groupDistribution = new MCNP_Distribution("Group Distribution");

        // Parse the flux lines
        List<FluxLineStep> lines = flux.getPhotonLines();
        Double[] energies = new Double[lines.size()];
        double[] intensities = new double[lines.size()];
        double[] biases = new double[lines.size()];
        double totalLineIntensities = 0.0;
        for (int i = 0; i < lines.size(); i++) {
            energies[i] = 0.001 * lines.get(i).getEnergy();
            intensities[i] = lines.get(i).getIntensity();
            biases[i] = 1.0;
            totalLineIntensities += intensities[i];
        }
        lineDistribution.setNodes(MCNP_Distribution.NodeType.DISCRETE, (Object[]) energies);
        lineDistribution.setProbabilities(MCNP_Distribution.ProbabilityType.PROBABILITIES, intensities);
        lineDistribution.setBiases(MCNP_Distribution.ProbabilityType.PROBABILITIES, biases);

        // Parse the flux groups
        List<FluxGroupTrapezoid> groups = flux.getPhotonGroups();
        double[] lowerEnergies = new double[groups.size()];
        double[] upperEnergies = new double[groups.size()];
        double[] lowerDensities = new double[groups.size()];
        double[] upperDensities = new double[groups.size()];
        double totalGroupCounts = 0.0;
        for (int i = 0; i < groups.size(); i++) {
            lowerEnergies[i] = 0.001 * groups.get(i).getEnergyLower();
            upperEnergies[i] = 0.001 * groups.get(i).getEnergyUpper();
            lowerDensities[i] = groups.get(i).getDensityLower();
            upperDensities[i] = groups.get(i).getDensityUpper();
            totalGroupCounts += groups.get(i).getCounts();
        }


        ArrayList<Double> pdfNodes = new ArrayList<>();
        ArrayList<Double> pdfValues = new ArrayList<>();
        ArrayList<Double> pdfBiases = new ArrayList<>();
        for (int i = 0; i < lowerEnergies.length; i++) {
            if (i > 0 && lowerEnergies[i] != upperEnergies[i-1]) {
                pdfNodes.add(upperEnergies[i-1]);
                pdfValues.add(0.0);
                pdfBiases.add(0.0);
                pdfNodes.add(lowerEnergies[i]);
                pdfValues.add(0.0);
                pdfBiases.add(0.0);
            }
            pdfNodes.add(lowerEnergies[i]);
            pdfValues.add(lowerDensities[i]);
            if (lowerDensities[i] == 0.0){
                pdfBiases.add(0.0);
            } else {
                pdfBiases.add(1.0);
            }

            pdfNodes.add(upperEnergies[i]);
            pdfValues.add(upperDensities[i]);
            if (upperDensities[i] == 0.0){
                pdfBiases.add(0.0);
            } else {
                pdfBiases.add(1.0);
            }

        }
        groupDistribution.setNodes(MCNP_Distribution.NodeType.DENSITY,
                pdfNodes.toArray());
        groupDistribution.setProbabilities(MCNP_Distribution.ProbabilityType.PROBABILITIES,
                pdfValues.stream().mapToDouble(d -> d).toArray());
        groupDistribution.setBiases(MCNP_Distribution.ProbabilityType.PROBABILITIES,
                pdfBiases.stream().mapToDouble(d -> d).toArray());

        // Combine the two distributions
        distribution.setNodes(MCNP_Distribution.NodeType.DISTRIBUTION_NUMBER,
                lineDistribution, groupDistribution);
        distribution.setProbabilities(MCNP_Distribution.ProbabilityType.PROBABILITIES,
                totalLineIntensities, totalGroupCounts);

        if (!flux.getPhotonLines().isEmpty() && !flux.getPhotonGroups().isEmpty()) {
            return distribution;
        }
        if (!flux.getPhotonLines().isEmpty()) {
            return lineDistribution;
        }
        if (!flux.getPhotonGroups().isEmpty()) {
            return groupDistribution;
        }

        return null;

    }

    public static MCNP_Distribution convertFluxToDistribution(FluxSpectrum flux) {

        MCNP_Distribution distribution = new MCNP_Distribution("Distribution");

        double[] edges = flux.getPhotonScale().getEdges();
        double[] counts = flux.getPhotonCounts();
        for (int i = 0; i < edges.length; i++) {
            edges[i] *= 0.001;
        }

        if (edges[0] == 0) {
            distribution.setNodes(MCNP_Distribution.NodeType.HISTOGRAM,
                    Arrays.copyOfRange(edges, 1, edges.length));
            distribution.setProbabilities(MCNP_Distribution.ProbabilityType.PROBABILITIES,
                    counts);
        } else {
            double[] newCounts = new double[counts.length + 1];
            newCounts[0] = 0.0;
            for (int i = 0; i < counts.length; i++) {
                newCounts[i + 1] = counts[i];
            }
            distribution.setNodes(MCNP_Distribution.NodeType.HISTOGRAM, edges);
            distribution.setProbabilities(MCNP_Distribution.ProbabilityType.PROBABILITIES, newCounts);
        }

        return distribution;
    }
}
