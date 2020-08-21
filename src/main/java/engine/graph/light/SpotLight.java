package engine.graph.light;

import org.joml.Vector3f;

public class SpotLight {

    /** point light of which the spot is a cutout */
    private PointLight pointLight;

    /** direction vector of the light */
    private Vector3f coneDirection;

    /** angle of the cutout -> width of the spot light */
    private float cutOff;

    /**
     * Constructor with all parameters
     *
     * @param pointLight used to set color intensity and position
     * @param coneDirection sets direction of spot
     * @param cutOffAngle sets size of spot
     */
    @SuppressWarnings("WeakerAccess")
    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle){
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        this.cutOff = cutOffAngle;
    }

    /**
     * copy constructor
     *
     * @param spotLight to copy
     */
    @SuppressWarnings({"WeakerAccess", "CopyConstructorMissesField"})
    public SpotLight(SpotLight spotLight){
        this(new PointLight(spotLight.getPointLight()),new Vector3f(spotLight.getConeDirection()),0);
        setCutOff(spotLight.getCutOff());
    }

    /**
     * @return light source of spot
     */
    public PointLight getPointLight() {
        return pointLight;
    }

    /**
     * @param pointLight new point light light source
     */
    @SuppressWarnings("unused")
    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    /**
     * @return cone direction vector
     */
    public Vector3f getConeDirection() {
        return coneDirection;
    }

    /**
     * @param coneDirection new cone direction vector
     */
    @SuppressWarnings("WeakerAccess")
    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    /**
     * @return cutoff angle
     */
    public float getCutOff() {
        return cutOff;
    }

    /**
     * @param cutOff new cutoff angle
     */
    @SuppressWarnings("WeakerAccess")
    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    /**
     * @param cutOffAngle sets cut off angle from degree parameter
     */
    @SuppressWarnings("unused")
    public final void setCutOffAngle(float cutOffAngle){
        this.setCutOff((float)Math.cos(Math.toRadians(cutOffAngle)));
    }
}
