package gov.llnl.rtk.mcnp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCNP_Deck {

    private String title;
    private List<MCNP_Cell> cells = new ArrayList<>();
    private List<MCNP_Surface> surfaces = new ArrayList<>();
    private List<MCNP_Transformation> transformations = new ArrayList<>();
    private List<MCNP_Material> materials = new ArrayList<>();
    private List<MCNP_Particle> particlesToSimulate = new ArrayList<>();
    private List<MCNP_Tally> tallies = new ArrayList<>();
    private MCNP_Source source;

    public MCNP_Deck(String title) {
        this.title = title;
    }

    public void addCells(MCNP_Cell... cells) {
        for (MCNP_Cell cell : cells) {
            if (!this.cells.contains(cell)) {
                this.cells.add(cell);
            }

            for (MCNP_Volume volume : cell.getVolumes()) {
                for (MCNP_Surface surface : volume.getSurfaces()) {
                    if (!surfaces.contains(surface)) {
                        surfaces.add(surface);
                    }
                    if (surface.getTransformation() != null && !transformations.contains(surface.getTransformation())) {
                        transformations.add(surface.getTransformation());
                    }
                }
            }
            if (cell.getMaterial() != null && !materials.contains(cell.getMaterial())) {
                materials.add(cell.getMaterial());
            }
        }
    }

    public void addParticles(MCNP_Particle... particles) {
        this.particlesToSimulate.addAll(Arrays.asList(particles));
    }

    public void addTallys(MCNP_Tally... tallys) {
        for (MCNP_Tally tally : tallys) {
            for (Object location : tally.getLocations()) {
                if (location.getClass() == MCNP_Surface.class) {
                    if (!surfaces.contains((MCNP_Surface) location)) {
                        surfaces.add((MCNP_Surface) location);
                    }
                }
            }
        }
        this.tallies.addAll(Arrays.asList(tallys));
    }

    public void setSource(MCNP_Source source) {
        this.source = source;
    }

    public List<MCNP_Card> getCards() {
        ArrayList<MCNP_Card> cards = new ArrayList<>();

        // Title
        cards.add(MCNP_Card.centeredTextCard(title));

        // Cells
        cards.add(MCNP_Card.fullLineCommentCard());
        cards.add(MCNP_Card.centeredTextCard("C ", "CELL CARDS", "*"));
        cards.add(MCNP_Card.fullLineCommentCard());
        for (MCNP_Cell cell : cells) {
            cards.add(cell.getCard());
        }

        // Outside world "cell"
        MCNP_Card outsideWorldCellCard = new MCNP_Card();
        outsideWorldCellCard.addEntry(999);         // todo: inelegant solution, consider redoing
        outsideWorldCellCard.addEntry(0);
        for (MCNP_Cell cell : cells) {
            outsideWorldCellCard.addEntry("#" + cell.getId());
        }
        outsideWorldCellCard.addComment("Outside World");
        cards.add(outsideWorldCellCard);
        cards.add(new MCNP_Card(""));

        // Surfaces
        cards.add(MCNP_Card.fullLineCommentCard());
        cards.add(MCNP_Card.centeredTextCard("C ", "SURFACE CARDS", "*"));
        cards.add(MCNP_Card.fullLineCommentCard());
        for (MCNP_Surface surface : surfaces) {
            cards.add(surface.getCard());

        }
        cards.add(new MCNP_Card(""));

        // Data cards
        cards.add(MCNP_Card.fullLineCommentCard());
        cards.add(MCNP_Card.centeredTextCard("C ", "DATA CARDS", "*"));
        cards.add(MCNP_Card.fullLineCommentCard());

        // Transformation cards
        for (MCNP_Transformation transformation : transformations) {
            cards.add(transformation.getCard());
        }

        // Material cards
        for (MCNP_Material material : materials) {
            cards.add(material.getCard());
        }

        // Mode card
        MCNP_Card modeCard = new MCNP_Card("MODE");
        for (MCNP_Particle particle : particlesToSimulate) {
            modeCard.addEntry(particle.getId());
        }
        cards.add(modeCard);

        // Physics and cutoff cards
        for (MCNP_Particle particle : particlesToSimulate) {
            MCNP_Card importanceCard = new MCNP_Card("IMP:" + particle.getId());
            for (MCNP_Cell cell : cells) {
                importanceCard.addEntry(cell.getImportance(particle));
            }
            importanceCard.addEntry(0);
            cards.add(importanceCard);

            if (particle.getId().equals("n") || particle.getId().equals("p")) {
                MCNP_Card collisionsCard = new MCNP_Card("FCL:" + particle.getId());
                for (MCNP_Cell cell : cells) {
                    collisionsCard.addEntry(cell.getForcedCollisions(particle));
                }
                cards.add(collisionsCard);
            }
            cards.add(particle.getPhysicsCard());
            cards.add(particle.getCutoffCard());
        }

        // todo: temp
        /*
        MCNP_Card bremCard = new MCNP_Card("BBREM");
        bremCard.addEntry(1.0);
        bremCard.addEntry(1.0);
        bremCard.addEntry("46I");
        bremCard.addEntry(100.0);
        for (MCNP_Material material : materials) {
            bremCard.addEntry(material.getId());
        }
        cards.add(bremCard);
         */

        // Source cards
        cards.addAll(source.getCards());

        // Tally cards
        for (MCNP_Tally tally : tallies) {
            cards.addAll(tally.getCards());
        }

        return cards;
    }

    public void plotGeometry() throws Exception {
        File file = new File("temp_geometry_" + System.currentTimeMillis() + ".input");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(toString());
        writer.close();
        String command = "mcnp6 ip i = " + file.getPath();
        Process p = Runtime.getRuntime().exec(command);

        // todo: verbose option
        System.out.println(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = stdInput.readLine();
        while (line != null) {
            System.out.println(line);
            line = stdInput.readLine();
        }

        file.delete();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MCNP_Card card : getCards()) {
            stringBuilder.append(card).append("\n");
        }
        return stringBuilder.toString();
    }

    public static void main(String ... args) {
        MCNP_Material uranium = new MCNP_Material("Uranium");
        uranium.addIsotope(new MCNP_Isotope("U235", 92, 235), 0.007);
        uranium.addIsotope(new MCNP_Isotope("U238", 92, 238), 0.993);

        MCNP_Surface innerSphereSurface = MCNP_Surface.sphere("Inner Sphere", 1.0);
        MCNP_Surface outerSphereSurface = MCNP_Surface.sphere("Outer Sphere", 2.0);

        MCNP_Volume innerSphere = new MCNP_Volume(innerSphereSurface, MCNP_Volume.Orientation.NEGATIVE);
        MCNP_Volume hollowSphere = new MCNP_Volume(innerSphereSurface, MCNP_Volume.Orientation.POSITIVE);
        hollowSphere.addSurface(outerSphereSurface, MCNP_Volume.Orientation.NEGATIVE);

        MCNP_Cell innerSphereCell = new MCNP_Cell("Inner Sphere", innerSphere);
        MCNP_Cell hollowSphereCell = new MCNP_Cell("Hollow Uranium Sphere", hollowSphere);
        hollowSphereCell.setMaterial(uranium, -19.93);

        MCNP_Source source = new MCNP_Source("Test Source", new MCNP_Photon(), 1000000);
        MCNP_Distribution energyDistribution = new MCNP_Distribution("Cs137 Beta Spectrum");
        MCNP_Deck deck = new MCNP_Deck("Hollow Uranium Sphere");

        deck.addCells(innerSphereCell, hollowSphereCell);
        deck.addParticles(new MCNP_Photon(), new MCNP_Electron());
        deck.setSource(new MCNP_Source("Test Source", new MCNP_Electron(), 1000000));
        System.out.println(deck);
    }
}
