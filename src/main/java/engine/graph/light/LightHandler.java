package engine.graph.light;

import engine.render.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class LightHandler {

    /** List of point lights used in scene */
    private List<PointLight> pointLightsList;

    /** list of spot lights used in scene */
    private List<SpotLight> spotLightsList;

    /** list of directional lights used in scene */
    private DirectionalLight directionalLight;

    /** color of the ambient light used in scene */
    private Vector3f ambientLight;

    /** intensity of direct reflections ( glow / glance ) */
    private float specularPower;

    /** maximum number of point lights used in scene */
    private final int MAX_POINT_LIGHTS = 5;

    /** maximum number of spot lights used in scene */
    private final int MAX_SPOT_LIGHTS = 5;

    /**
     * creates new empty light handler with 10 as specular power
     */
    public LightHandler(){
        specularPower = 10.0f;
        pointLightsList = new ArrayList<>();
        spotLightsList = new ArrayList<>();
    }


    /**
     * initialises light handler by creating uniforms
     *
     * @param shaderProgram shader used for lights ( normally scene shader )
     * @throws Exception if uniform creation fails
     */
    public void inti(ShaderProgram shaderProgram) throws Exception{
        shaderProgram.createUniforms("specularPower");
        shaderProgram.createUniforms("ambientLight");
        shaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        shaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        shaderProgram.createDirectionalLightUniform("directionalLight");
    }

    /**
     * method creates the uniforms for spot and point lights and sets their values along
     * with the uniforms for the ambient light, specular power and directional light
     *
     * @param viewMatrix view matrix of scene (camera)
     * @param shaderProgram shader program used to render
     */
    public void renderLights(Matrix4f viewMatrix, ShaderProgram shaderProgram) {

        shaderProgram.setUniform("ambientLight",ambientLight);
        shaderProgram.setUniform("specularPower",specularPower);

        //Point Lights
        PointLight[] pointLights = pointLightsList.toArray(new PointLight[MAX_POINT_LIGHTS]);
        int numLights = Math.min(pointLightsList.size(), MAX_POINT_LIGHTS);
        for(int i = 0; i < numLights;i++){
            PointLight currPointLight = new PointLight(pointLights[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos,1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            shaderProgram.setUniform("pointLights",currPointLight,i);
        }

        SpotLight[] spotLights = spotLightsList.toArray(new SpotLight[MAX_SPOT_LIGHTS]);
        numLights = Math.min(spotLightsList.size(), MAX_SPOT_LIGHTS);
        for(int i = 0; i < numLights;i++){
            SpotLight currSpotLight = new SpotLight(spotLights[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(),0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x,dir.y,dir.z));
            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos,1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            shaderProgram.setUniform("spotLights",currSpotLight,i);
        }

        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(),0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x,dir.y,dir.z));
        shaderProgram.setUniform("directionalLight",currDirLight);
    }

    /**
     * changes the ambient light
     *
     * @param value new ambient light
     */
    @SuppressWarnings("unused")
    public void setAmbientLight(Vector3f value){
        ambientLight = value;
    }

    /**
     * @return currently used ambient light
     */
    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    /**
     * @return currently used directional light
     */
    @SuppressWarnings("unused")
    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    /**
     * changes directional light
     *
     * @param directionalLight new directional light
     */
    @SuppressWarnings("unused")
    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    /**
     * adds a point light to list
     * if list exceeds MAX_POINT_LIGHTS the light is still added but not rendered
     *
     * @param light point light to add
     */
    @SuppressWarnings("unused")
    public void addPointLight(PointLight light){
        pointLightsList.add(light);
    }

    /**
     * removes point light from light list
     * if number of point lights was over MAX_POINT_LIGHTS and the removed light was getting rendered
     * another one will automatically be rendered instead now
     *
     * @param light point light to be removed
     */
    @SuppressWarnings("unused")
    public void removePointLight(PointLight light){
        pointLightsList.remove(light);
    }

    /**
     * adds a spot light to list
     * if list exceeds MAX_SPOT_LIGHTS the light is still added but not rendered
     *
     * @param light spot light to add
     */
    @SuppressWarnings("unused")
    public void addSpotLight(SpotLight light){
        spotLightsList.add(light);
    }

    /**
     * removes spot light from light list
     * if number of point lights was over MAX_SPOT_LIGHTS and the removed light was getting rendered
     * another one will automatically be rendered instead now
     *
     * @param light spot light to be removed
     */
    @SuppressWarnings("unused")
    public void removeSpotLight(SpotLight light){
        spotLightsList.remove(light);
    }
}
