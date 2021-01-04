package engine.graph.environment;

import org.joml.Vector3f;

/**
 * fog class contains the information about the fog in the scene
 */
public class Fog {

    /** determines if the fog effect is used */
    private boolean active;

    /** the color of the fog */
    private Vector3f color;

    /** density of the fog */
    private float density;

    /** default fog object where the fog is deactivated */
    public static Fog NO_FOG = new Fog();

    /**
     * default constructor creating a deactivated fog
     */
    public Fog() {
        this(false, new Vector3f(), 0);
    }

    /**
     * constructor setting all the attributes of the fog
     *
     * @param active determines if the fog is used
     * @param color sets the color of the fog
     * @param density sets the density of the fog
     */
    public Fog(boolean active, Vector3f color, float density) {
        this.active = active;
        this.color = color;
        this.density = density;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
