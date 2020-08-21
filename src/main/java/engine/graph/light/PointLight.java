package engine.graph.light;

import org.joml.Vector3f;

public class PointLight {

    /** light color as Vector (no transparency) */
    private Vector3f color;

    /** light position vector */
    private Vector3f position;

    /** light intensity, range 0 to 1 */
    private float intensity;

    /** attenuation saves parameters used to determine the lights look */
    private Attenuation attenuation;

    /**
     * constructor to customize light position,color, and intensity
     * uses default values for attenuation
     *
     * @param color light color
     * @param position light position
     * @param intensity light intensity
     */
    @SuppressWarnings("WeakerAccess")
    public PointLight(Vector3f color, Vector3f position, float intensity){
        attenuation = new Attenuation(1,0,0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    /**
     * constructor to set all parameters
     *
     * @param color light color
     * @param position light position
     * @param intensity light intesity
     * @param attenuation attenuation values
     */
    @SuppressWarnings("WeakerAccess")
    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation){
        this(color,position, intensity);
        this.attenuation = attenuation;
    }

    /**
     * copy constructor
     *
     * @param pointLight point light to copy
     */
    @SuppressWarnings({"WeakerAccess", "CopyConstructorMissesField"})
    public PointLight(PointLight pointLight){
        this(new Vector3f(pointLight.getColor()), new Vector3f(pointLight.getPosition()),pointLight.getIntensity(),pointLight.getAttenuation());
    }

    /**
     * @return returns light color vector
     */
    public Vector3f getColor() {
        return color;
    }

    /**
     * @param color sets new light color vector
     */
    public void setColor(Vector3f color) {
        this.color = color;
    }

    /**
     * @return returns light position vector
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * @param position sets light position vector
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * @return light intensity
     */
    public float getIntensity() {
        return intensity;
    }

    /**
     * @param intensity sets light intensity
     */
    @SuppressWarnings("unused")
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    /**
     * @return Attenuation object
     */
    public Attenuation getAttenuation() {
        return attenuation;
    }

    /**
     * @param attenuation sets new Attenuation object
     */
    @SuppressWarnings("unused")
    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    /**
     * inner class to store attributes for calculation of the lights behavior
     * all attributes are passed to shader using uniforms
     */
    public static class Attenuation{

        /** constant */
        private float constant;

        /** linear behaviour */
        private float linear;

        /** exponential behaviour */
        private float exponent;

        /**
         * constructor setting all params
         *
         * @param constant constant
         * @param linear linear behaviour
         * @param exponent exponential behaviour
         */
        @SuppressWarnings("WeakerAccess")
        public Attenuation(float constant, float linear, float exponent){
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        /**
         * @return returns constant field
         */
        public float getConstant() {
            return constant;
        }

        /**
         * @param constant sets new constant
         */
        @SuppressWarnings("unused")
        public void setConstant(float constant) {
            this.constant = constant;
        }

        /**
         * @return returns linear value
         */
        public float getLinear() {
            return linear;
        }

        /**
         * @param linear sets new linear value
         */
        @SuppressWarnings("unused")
        public void setLinear(float linear) {
            this.linear = linear;
        }

        /**
         * @return returns exponential value
         */
        public float getExponent() {
            return exponent;
        }

        /**
         * @param exponent sets new exponential value
         */
        @SuppressWarnings("unused")
        public void setExponent(float exponent) {
            this.exponent = exponent;
        }
    }
}

