package gov.llnl.rtk.mcnp;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.flux.*;
import gov.llnl.rtk.physics.Section;
import gov.llnl.rtk.physics.SphericalSection;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lahmann
 */
public class RadSim_MCNP_Job {

    private String name;
    private Path outputDir;
    private Path mcnpPath;
    private Result result;

    private MCNP_Source source;
    private MCNP_Distribution distribution;
    private MCNP_Particle[] particles;
    private double[] energyBins;
    private List<MCNP_Cell> cells = new ArrayList<>();
    private List<MCNP_Surface> tallySurfaces = new ArrayList<>();
    private double containingRadius = 1.0;
    private List<String> tallyKeys = new ArrayList<>();

    private final double CONTAINING_RADIUS_SAFETY_FACTOR = 2.0;
    private final MCNP_Particle[] PARTICLE_OUTPUT_PRIORITY = {
            new MCNP_Photon(),
            new MCNP_Electron()
    };

    public RadSim_MCNP_Job(String name, Path outputDir, Path mcnpPath) throws Exception {
        // todo: need a better system
        MCNP_Utils.resetAllCounts();

        this.name = name;
        this.outputDir = outputDir;
        this.mcnpPath = mcnpPath;
    }

    public void setFlux(Flux flux) throws Exception {
        if (flux.getClass() == FluxBinned.class) {
            distribution = MCNP_Utils.convertFluxToDistribution((FluxBinned) flux);
        } else if (flux.getClass() == FluxTrapezoid.class) {
            distribution = MCNP_Utils.convertFluxToDistribution((FluxTrapezoid) flux);
        } else if (flux.getClass() == FluxSpectrum.class) {
            distribution = MCNP_Utils.convertFluxToDistribution((FluxSpectrum) flux);
        } else {
            throw new Exception("Unsupported flux object");
        }
    }

    public void setParticleOptions(int numSourceParticles, MCNP_Particle sourceParticle, MCNP_Particle ... otherParticles) {
        this.source = new MCNP_Source("", sourceParticle, numSourceParticles);
        this.particles = new MCNP_Particle[otherParticles.length + 1];
        this.particles[0] = sourceParticle;
        for (int i = 0; i < otherParticles.length; i++) {
            this.particles[i+1] = otherParticles[i];
        }
    }

    public void setEnergyBins(double[] energyBins) {
        this.energyBins = energyBins;
    }

    public void setEnergyBins(EnergyScale scale) {
        setEnergyBins(scale.getEdges());
    }

    public void setEnergyBins(double min, double max, int N) {
        this.energyBins = new double[N];
        for (int i = 0; i < energyBins.length; i++) {
            energyBins[i] = min + i * (max - min) / (N-1);
        }
    }

    public void setSourceSection(Section section) throws Exception {
        addSection(section, true);
    }

    public void addSection(Section section) throws Exception {
        addSection(section, false);
    }

    public void addSection(Section section, boolean sourceSection) throws Exception {
        containingRadius = Math.max(containingRadius, CONTAINING_RADIUS_SAFETY_FACTOR * MCNP_Utils.getContainingRadius(section));

        MCNP_Cell cell = MCNP_Cell.fromSection("", section);
        cells.add(cell);

        if (sourceSection) {
            if (section.getClass() == SphericalSection.class) {
                source.setUniformSphericalDistribution(section.getOrigin(), ((SphericalSection) section).getOuterRadius());
                source.setCell(cell);
            } else {
                throw new Exception("Unsupported source section");
            }
        }
    }

    public void addSurfaceCurrentTally(String key, Vector3 origin, double radius) throws Exception {
        if (tallyKeys.contains(key)) {
            throw new Exception("Tally key must be unique");
        }

        tallyKeys.add(key);
        containingRadius = Math.max(containingRadius, CONTAINING_RADIUS_SAFETY_FACTOR * (origin.norm() + radius));
        tallySurfaces.add(MCNP_Surface.sphere("", origin.getX(), origin.getY(), origin.getZ(), radius));
    }

    public MCNP_Deck buildDeck() throws Exception {

        if (energyBins == null) {
            throw new Exception("No energy bins initialized. Please call setEnergyBins.");
        }

        if (distribution == null) {
            throw new Exception("No flux initialized. Please call setFlux.");
        }

        if (particles == null) {
            throw new Exception("No particles initialized. Please call setParticleOptions.");
        }

        // Create the source
        source.setEnergyDistribution(distribution);

        // Build the MCNP deck
        MCNP_Deck deck = new MCNP_Deck("RadSim Generated MCNP Deck");
        deck.setSource(source);
        deck.addParticles(particles);
        for (MCNP_Cell cell : cells) {
            deck.addCells(cell);
        }

        List<MCNP_Cell> tallyCells = new ArrayList<>();
        for (MCNP_Surface surface : tallySurfaces) {
            // Create a cell that uses this surface
            MCNP_Volume volume = new MCNP_Volume(surface, MCNP_Volume.Orientation.NEGATIVE);
            for (MCNP_Cell cell : cells) {
                volume.addCellComplement(cell);
            }
            MCNP_Cell cell = new MCNP_Cell("Tally Cell", volume);
            tallyCells.add(cell);
            deck.addCells(cell);

            // Create the tallies that use this surface
            for (MCNP_Particle particle : particles) {
                MCNP_Tally tally = new MCNP_Tally("", particle, MCNP_Tally.Type.SURFACE_CURRENT);
                tally.addSurfaces(surface);
                tally.addEnergyBins(energyBins);
                tally.addCosineBins(0.0, 1.0);
                deck.addTallys(tally);
            }
        }

        // We need an additional cell to contain all the geometry
        MCNP_Surface surface = MCNP_Surface.sphere("Containing Sphere", containingRadius);
        MCNP_Volume volume = new MCNP_Volume(surface, MCNP_Volume.Orientation.NEGATIVE);
        for (MCNP_Cell cell : cells) {
            volume.addCellComplement(cell);
        }
        for (MCNP_Cell cell : tallyCells) {
            volume.addCellComplement(cell);
        }
        MCNP_Cell cell = new MCNP_Cell("Containing Volume", volume);
        deck.addCells(cell);

        return deck;
    }

    public void run(int numTasks) throws Exception {
        MCNP_Job job = new MCNP_Job(name, buildDeck(), outputDir, mcnpPath);
        this.result = new Result(job.run(numTasks));
    }



    public FluxBinned getTallySpectrum(String key, MCNP_Particle particle, boolean escaping) throws Exception {
        if (!tallyKeys.contains(key)) {
            throw new Exception("Invalid tally key");
        }

        int tallyIndex = 0;
        for (int i = 0; i < tallyKeys.size(); i++) {
            if (key.equals(tallyKeys.get(i))) {
                tallyIndex = i;
                break;
            }
        }

        int particleIndex = 0;
        for (MCNP_Particle outputPriority : PARTICLE_OUTPUT_PRIORITY) {
            // If this is the particle break
            if (particle.getId().equals(outputPriority.getId())) {
                break;
            }
            // Else determine if this particle was simulated
            for (MCNP_Particle simulatedParticle : particles) {
                if (simulatedParticle.getId().equals(outputPriority.getId())) {
                    particleIndex++;
                }
            }
        }

        int index = tallyIndex * particles.length * 2 + particleIndex * 2;
        if (escaping) {
            index += 1;
        }

        return result.getSpectra().get(index);
    }



}
