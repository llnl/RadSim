package gov.llnl.rtk.mcnp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.rtk.physics.ConicalSection;
import gov.llnl.rtk.physics.CylindricalSection;
import gov.llnl.rtk.physics.PhysicalProperty;
import gov.llnl.rtk.physics.Section;
import gov.llnl.rtk.physics.SphericalSection;

public class MCNP_Cell {

    private static int totalCells;

    private String name;
    private int id;
    private MCNP_Material material;
    private double density;
    private HashMap<MCNP_Particle, Integer> importances = new HashMap<>();
    private HashMap<MCNP_Particle, Double> forcedCollisions = new HashMap<>();

    private MCNP_Volume[] volumes;

    public MCNP_Cell(String name, MCNP_Volume ... volumes) {
        totalCells++;
        this.name = name;
        this.id = totalCells;
        this.volumes = volumes;
    }

    public static MCNP_Cell fromSection(String name, Section section) throws Exception {
        if (section.getClass() == SphericalSection.class) {
            return MCNP_Cell.fromSection(name, (SphericalSection) section);
        }
        if (section.getClass() == CylindricalSection.class) {
            return MCNP_Cell.fromSection(name, (CylindricalSection) section);
        }
        if (section.getClass() == ConicalSection.class) {
            return MCNP_Cell.fromSection(name, (ConicalSection) section);
        }
        throw new Exception("Unsupported section type");
    }

    private static MCNP_Cell fromSection(String name, SphericalSection section) {
        // Because of the way MCNP handles cones (they extend in both the positive and negative direction)
        // We need to construct the shape in two halves (positive x and negative x) and then combine
        MCNP_Volume upperHalf = new MCNP_Volume();
        MCNP_Volume lowerHalf = new MCNP_Volume();
        boolean includeUpperHalf = section.getTheta1() < Math.PI / 2.0;
        boolean includeLowerHalf = section.getTheta2() > Math.PI / 2.0;

        // Handle the transform
        MCNP_Transformation transformation = new MCNP_Transformation(section.getOrigin(), Vector3.AXIS_X, section.getAxis());

        // Make a dividing plane
        MCNP_Surface plane = new MCNP_Surface(name + " Dividing Plane", "px", 0.0);
        plane.setTransformation(transformation);
        upperHalf.addSurface(plane, MCNP_Volume.Orientation.POSITIVE);
        lowerHalf.addSurface(plane, MCNP_Volume.Orientation.NEGATIVE);

        // Handle the outer radius
        MCNP_Surface outerSphere = MCNP_Surface.sphere(name + " Outer Sphere", section.getOuterRadius());
        outerSphere.setTransformation(transformation);
        upperHalf.addSurface(outerSphere, MCNP_Volume.Orientation.NEGATIVE);
        lowerHalf.addSurface(outerSphere, MCNP_Volume.Orientation.NEGATIVE);

        // Handle the inner radius
        if (section.getInnerRadius() > 0.0) {
            MCNP_Surface innerSphere = MCNP_Surface.sphere(name + " Inner Sphere", section.getInnerRadius());
            innerSphere.setTransformation(transformation);
            upperHalf.addSurface(innerSphere, MCNP_Volume.Orientation.POSITIVE);
            lowerHalf.addSurface(innerSphere, MCNP_Volume.Orientation.POSITIVE);
        }

        // Construct the upper half
        if (section.getTheta1() > 0 && section.getTheta1() < Math.PI / 2.0) {
            double t2 = Math.pow(Math.tan(section.getTheta1()), 2.0);
            MCNP_Surface cone = new MCNP_Surface(name + " Inner Cone", "kx", 0.0, t2);
            cone.setTransformation(transformation);
            upperHalf.addSurface(cone, MCNP_Volume.Orientation.POSITIVE);
        }
        if (section.getTheta2() > 0 && section.getTheta2() < Math.PI / 2.0) {
            double t2 = Math.pow(Math.tan(section.getTheta2()), 2.0);
            MCNP_Surface cone = new MCNP_Surface(name + " Outer Cone", "kx", 0.0, t2);
            cone.setTransformation(transformation);
            upperHalf.addSurface(cone, MCNP_Volume.Orientation.NEGATIVE);
        }

        // Construct the lower half
        if (section.getTheta1() > Math.PI / 2.0 && section.getTheta1() < Math.PI) {
            double t2 = Math.pow(Math.tan(section.getTheta1()), 2.0);
            MCNP_Surface cone = new MCNP_Surface(name + " Inner Cone", "kx", 0.0, t2);
            cone.setTransformation(transformation);
            lowerHalf.addSurface(cone, MCNP_Volume.Orientation.NEGATIVE);
        }
        if (section.getTheta2() > Math.PI / 2.0 && section.getTheta2() < Math.PI) {
            double t2 = Math.pow(Math.tan(section.getTheta2()), 2.0);
            MCNP_Surface cone = new MCNP_Surface(name + " Outer Cone", "kx", 0.0, t2);
            cone.setTransformation(transformation);
            lowerHalf.addSurface(cone, MCNP_Volume.Orientation.POSITIVE);
        }

        MCNP_Cell cell = null;
        if (includeUpperHalf & includeLowerHalf) {
            cell = new MCNP_Cell(name, upperHalf, lowerHalf);
        } else if (includeUpperHalf) {
            cell = new MCNP_Cell(name, upperHalf);
        } else if (includeLowerHalf) {
            cell = new MCNP_Cell(name, lowerHalf);
        }
        cell.setMaterial(new MCNP_Material(name + " Material", section.getMaterial()), -section.getMaterial().getDensity().getValue());
        return cell;
    }

    private static MCNP_Cell fromSection(String name, CylindricalSection section) {
        MCNP_Volume volume = new MCNP_Volume();

        // Handle the transform
        MCNP_Transformation transformation = new MCNP_Transformation(section.getOrigin(), Vector3.AXIS_X, section.getAxis());

        MCNP_Surface lowerPlane = new MCNP_Surface(name + " Lower Plane", "px", -1*section.getDepth()/2.);
        lowerPlane.setTransformation(transformation);
        volume.addSurface(lowerPlane, MCNP_Volume.Orientation.POSITIVE);

        MCNP_Surface upperPlane = new MCNP_Surface(name + " Upper Plane", "px", section.getDepth()/2.);
        upperPlane.setTransformation(transformation);
        volume.addSurface(upperPlane, MCNP_Volume.Orientation.NEGATIVE);

        MCNP_Surface outerSurface = new MCNP_Surface(name + " Outer Surface", "cx", section.getOuterRadius());
        outerSurface.setTransformation(transformation);
        volume.addSurface(outerSurface, MCNP_Volume.Orientation.NEGATIVE);

        if (section.getInnerRadius() > 0.0) {
            MCNP_Surface innerSurface = new MCNP_Surface(name + " Inner Surface", "cx", section.getInnerRadius());
            innerSurface.setTransformation(transformation);
            volume.addSurface(innerSurface, MCNP_Volume.Orientation.POSITIVE);
        }

        MCNP_Cell cell = new MCNP_Cell(name, volume);
        cell.setMaterial(new MCNP_Material(name + " Material", section.getMaterial()), -section.getMaterial().getDensity().as(PhysicalProperty.DENSITY));
        return cell;
    }

    private static MCNP_Cell fromSection(String name, ConicalSection section) {
        MCNP_Volume volume = new MCNP_Volume();

        // Handle the transform
        MCNP_Transformation transformation = new MCNP_Transformation(section.getOrigin(), Vector3.AXIS_X, section.getAxis());

        MCNP_Surface lowerPlane = new MCNP_Surface(name + " Lower Plane", "px", section.getStartLength());
        lowerPlane.setTransformation(transformation);
        volume.addSurface(lowerPlane, MCNP_Volume.Orientation.POSITIVE);

        MCNP_Surface upperPlane = new MCNP_Surface(name + " Upper Plane", "px", section.getEndLength());
        upperPlane.setTransformation(transformation);
        volume.addSurface(upperPlane, MCNP_Volume.Orientation.NEGATIVE);

        if (section.getTheta1() > 0 && section.getTheta1() < Math.PI / 2.0) {
            double t2 = Math.pow(Math.tan(section.getTheta1()), 2.0);
            MCNP_Surface cone = new MCNP_Surface(name + " Inner Cone", "kx", 0.0, t2);
            cone.setTransformation(transformation);
            volume.addSurface(cone, MCNP_Volume.Orientation.POSITIVE);
        }

        if (section.getTheta2() > 0 && section.getTheta2() < Math.PI / 2.0) {
            double t2 = Math.pow(Math.tan(section.getTheta2()), 2.0);
            MCNP_Surface cone = new MCNP_Surface(name + " Outer Cone", "kx", 0.0, t2);
            cone.setTransformation(transformation);
            volume.addSurface(cone, MCNP_Volume.Orientation.NEGATIVE);
        }

        MCNP_Cell cell = new MCNP_Cell(name, volume);
        cell.setMaterial(new MCNP_Material(name + " Material", section.getMaterial()), -section.getMaterial().getDensity().getValue());
        return cell;
    }

    public static void resetCount(){
        totalCells = 0;
    }

    public void setMaterial(MCNP_Material material, double density) {
        this.material = material;
        this.density = density;
    }

    public void setImportance(MCNP_Particle particle, int importance) {
        importances.put(particle, importance);
    }

    // Todo: rename forced collisions to make more intuitive sense
    public void setForcedCollisions(MCNP_Particle particle, double forcedCollisions) {
        this.forcedCollisions.put(particle, forcedCollisions);
    }

    public int getId() {
        return id;
    }

    public MCNP_Material getMaterial() {
        return material;
    }

    public MCNP_Volume[] getVolumes() {
        return volumes;
    }

    public List<MCNP_Surface> getSurfaces() {
        ArrayList<MCNP_Surface> surfaces = new ArrayList<>();
        for (MCNP_Volume volume : volumes) {
            surfaces.addAll(volume.getSurfaces());
        }
        return surfaces;
    }

    public int getImportance(MCNP_Particle particle) {
        return importances.getOrDefault(particle, 1);
    }

    public double getForcedCollisions(MCNP_Particle particle) {
        return forcedCollisions.getOrDefault(particle, 1.0);
    }

    public MCNP_Card getCard() {
        MCNP_Card card = new MCNP_Card();
        card.addEntry(id);
        if (material == null) {
            card.addEntry(0);
        } else {
            card.addEntry(material.getId());
            card.addEntry(density);
        }

        StringBuilder builder = new StringBuilder();
        String prefix = "";
        for (MCNP_Volume volume : volumes) {
            builder.append(prefix).append("(").append(volume).append(")");
            prefix = ":";
        }
        card.addEntry(builder);
        card.addComment(name);
        return card;
    }

    public static void main(String ... args) {
        MCNP_Volume volume = new MCNP_Volume();
        volume.addSurface(
                MCNP_Surface.sphere("Inner Sphere", 1.0),
                MCNP_Volume.Orientation.POSITIVE
        );
        volume.addSurface(
                MCNP_Surface.sphere("Outer Sphere",2.0),
                MCNP_Volume.Orientation.NEGATIVE
        );

        MCNP_Material material = new MCNP_Material("Uranium");
        material.addIsotope(new MCNP_Isotope("U235", 92, 235), 0.007);
        material.addIsotope(new MCNP_Isotope("U238", 92, 238), 0.993);

        MCNP_Cell cell = new MCNP_Cell("Hollow Sphere", volume);
        cell.setMaterial(material, -19.93);

        System.out.println(cell.getCard());
    }
}
