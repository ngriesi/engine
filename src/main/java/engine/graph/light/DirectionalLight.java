package engine.graph.light;

import org.joml.Vector3f;

public class DirectionalLight {

    /** Light Color Vector */
    private Vector3f color;

    /** light direction vector */
    private Vector3f direction;

    /** light intensity, range 0 to 1 */
    private float intensity;

    /**
     * Constructor with all fields as parameters
     *
     * @param color light color
     * @param direction light direction
     * @param intensity light intensity
     */
    @SuppressWarnings("WeakerAccess")
    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    /**
     * Copy constructor uses new Vector3f to be independent from origin
     *
     * @param directionalLight light to copy
     */
    @SuppressWarnings({"CopyConstructorMissesField", "WeakerAccess"})
    public DirectionalLight(DirectionalLight directionalLight) {
        this(new Vector3f(directionalLight.getColor()),new Vector3f(directionalLight.getDirection()),directionalLight.getIntensity());
    }

    /**
     * @return light color vector
     */
    public Vector3f getColor() {
        return color;
    }

    /**
     * @param color new light color vector
     */
    public void setColor(Vector3f color) {
        this.color = color;
    }

    /**
     * @return light direction vector
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * @param direction new light direction vector
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    /**
     * @return light intensity
     */
    public float getIntensity() {
        return intensity;
    }

    /**
     * @param i new light intensity
     */
    @SuppressWarnings("unused")
    public void setIntensity(float i) {
        intensity = i;
    }
}
