package engine.render;

import engine.graph.items.Material;
import engine.graph.light.DirectionalLight;
import engine.graph.light.PointLight;
import engine.graph.light.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    /** id of the program */
    private final int programId;

    /** id of the vertex shader*/
    private int vertexShaderId;

    /** if of fragment shader*/
    @SuppressWarnings("unused")
    private int fragmentShaderId;

    /** uniform pointer */
    private final Map<String, Integer> uniforms;

    /**
     * creates new shader program
     *
     * @throws Exception if open gl cant create a program
     */
    public ShaderProgram() throws Exception{
        uniforms = new HashMap<>();
        programId = glCreateProgram();
        if(programId == 0){
            throw new Exception("Could not create Shader");
        }
    }

    /**
     * creates the vertex shader
     *
     * @param shaderCode code of the vertex shader
     * @throws Exception if shader cant be created
     */
    void createVertexShader(String shaderCode) throws Exception{
        vertexShaderId = createShader(shaderCode,GL_VERTEX_SHADER);
    }

    /**
     * creates the fragment shader
     *
     * @param shaderCode code of the fragment shader
     * @throws Exception if shader cant be created
     */
    void createFragmentShader(String shaderCode) throws Exception{
        vertexShaderId = createShader(shaderCode,GL_FRAGMENT_SHADER);
    }

    /**
     * creates shader of certain type
     *
     * @param shaderCode content of the shader
     * @param shaderType type of the shader
     * @return shader id
     * @throws Exception if shader cant be created or has a compile error
     */
    private int createShader(String shaderCode, int shaderType) throws Exception{
        int shaderId = glCreateShader(shaderType);
        if(shaderId==0){
            throw new Exception("Error creating shader, shadertype: "+ shaderType);
        }

        glShaderSource(shaderId,shaderCode);
        glCompileShader(shaderId);

        if(glGetShaderi(shaderId, GL_COMPILE_STATUS)==0){
            throw new Exception("Error compiling shader code: "+ glGetShaderInfoLog(shaderId,1024));
        }

        glAttachShader(programId,shaderId);

        return shaderId;
    }

    /**
     * links a program (refereed to with id) with a shader
     * @throws Exception if shader cant be linked or validated
     */
    void link() throws Exception {
        glLinkProgram(programId);
        if(glGetProgrami(programId, GL_LINK_STATUS)==0){
            throw new Exception("Error linking shader code: "+ glGetProgramInfoLog(programId,1024));
        }

        if(vertexShaderId != 0){
            glDetachShader(programId,vertexShaderId);
        }
        if(fragmentShaderId != 0){
            glDetachShader(programId,fragmentShaderId);
        }

        glValidateProgram(programId);
        if(glGetProgrami(programId,GL_VALIDATE_STATUS)==0) {
            System.err.println("Warning validating Shader code: "+glGetProgramInfoLog(programId,1024));
        }
    }

    /**
     * binds th shader program
     */
    void bind(){
        glUseProgram(programId);
    }

    /**
     * unbinds the shader program
     */
    void unbind(){
        glUseProgram(0);
    }

    /**
     * unbinds shader program and deletes it
     */
    public void cleanup(){
        unbind();
        if(programId != 0){
            glDeleteProgram(programId);
        }
    }

    /**
     * creates a uniform variable
     *
     * @param uniformName name of the variable (has to be exactly same as in shader code)
     * @throws Exception if uniform cant be found
     */
    public void createUniforms(String uniformName) throws Exception{
        int uniformLocation = glGetUniformLocation(programId,uniformName);
        if(uniformLocation < 0){
            throw new Exception("Could not find uniform: " +uniformName);
        }
        uniforms.put(uniformName,uniformLocation);
    }

    /**
     * sets value of a Matrix4f uniform
     *
     * @param uniformName name
     * @param value value
     */
    public  void setUniform(String uniformName, Matrix4f value){
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName),false,fb);
        }
    }

    /**
     * sets value of a int uniform
     *
     * @param uniformName name
     * @param value value
     */
    public void setUniform(String uniformName, int value){
        glUniform1i(uniforms.get(uniformName),value);
    }

    /**
     * sets value of a float uniform
     *
     * @param uniformName name
     * @param value value
     */
    public void setUniform(String uniformName, float value){
        glUniform1f(uniforms.get(uniformName),value);
    }

    /**
     * sets value of a Vector3f uniform
     *
     * @param uniformName name
     * @param value value
     */
    public void setUniform(String uniformName, Vector3f value){
        glUniform3f(uniforms.get(uniformName),value.x,value.y,value.z);
    }

    /**
     * sets value of a Vector4f uniform
     *
     * @param uniformName name
     * @param value value
     */
    @SuppressWarnings("WeakerAccess")
    public void setUniform(String uniformName, Vector4f value){
        glUniform4f(uniforms.get(uniformName),value.x,value.y,value.z,value.w);
    }

    /**
     * creates a point light uniform
     *
     * @param uniformName name
     * @throws Exception if uniform cant be found
     */
    @SuppressWarnings("WeakerAccess")
    public void createPointLightUniform(String uniformName) throws Exception {
        createUniforms(uniformName + ".color");
        createUniforms(uniformName + ".position");
        createUniforms(uniformName + ".intensity");
        createUniforms(uniformName + ".att.constant");
        createUniforms(uniformName + ".att.linear");
        createUniforms(uniformName + ".att.exponent");
    }

    /**
     * creates a material uniform
     *
     * @param uniformName name
     * @throws Exception if uniform cant be found
     */
    @SuppressWarnings("WeakerAccess")
    public void createMaterialUniform(String uniformName) throws Exception {
        createUniforms(uniformName + ".ambient");
        createUniforms(uniformName + ".diffuse");
        createUniforms(uniformName + ".specular");
        createUniforms(uniformName + ".hasTexture");
        createUniforms(uniformName + ".reflectance");
    }

    /**
     * sets value of a PointLight uniform
     *
     * @param uniformName name
     * @param pointLight value
     */
    @SuppressWarnings("WeakerAccess")
    public void setUniform(String uniformName, PointLight pointLight){
        setUniform(uniformName + ".color",pointLight.getColor());
        setUniform(uniformName + ".position",pointLight.getPosition());
        setUniform(uniformName + ".intensity",pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniform(uniformName + ".att.constant", att.getConstant());
        setUniform(uniformName + ".att.linear", att.getLinear());
        setUniform(uniformName + ".att.exponent", att.getExponent());
    }

    /**
     * sets value of a Material uniform
     *
     * @param uniformName name
     * @param material value
     */
    @SuppressWarnings("WeakerAccess")
    public void setUniform(String uniformName, Material material){
        setUniform(uniformName + ".ambient", material.getAmbientColor());
        setUniform(uniformName + ".diffuse", material.getDiffuseColor());
        setUniform(uniformName + ".specular", material.getSpecularColor());
        setUniform(uniformName + ".hasTexture", material.isTexture()? 1: 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());

    }

    /**
     * creates a directional light uniform
     *
     * @param uniformName name
     * @throws Exception if uniform cant be found
     */
    public void createDirectionalLightUniform(String uniformName) throws Exception{
        createUniforms(uniformName + ".color");
        createUniforms(uniformName + ".direction");
        createUniforms(uniformName + ".intensity");
    }

    /**
     * sets value of a directional light uniform
     *
     * @param uniformName name
     * @param directionalLight value
     */
    public void setUniform(String uniformName, DirectionalLight directionalLight){
        setUniform(uniformName + ".color",directionalLight.getColor());
        setUniform(uniformName + ".direction",directionalLight.getDirection());
        setUniform(uniformName + ".intensity",directionalLight.getIntensity());
    }

    /**
     * creates a spot light uniform
     *
     * @param uniformName name
     * @throws Exception if uniform cant be found
     */
    @SuppressWarnings("WeakerAccess")
    public void createSpotLightUniform(String uniformName) throws Exception {
        createPointLightUniform(uniformName +".pl");
        createUniforms(uniformName + ".conedir");
        createUniforms(uniformName + ".cutoff");
    }

    /**
     * sets value of a spot light uniform
     *
     * @param uniformName name
     * @param spotLight value
     */
    @SuppressWarnings("WeakerAccess")
    public void setUniform(String uniformName, SpotLight spotLight){
        setUniform(uniformName + ".pl",spotLight.getPointLight());
        setUniform(uniformName + ".conedir",spotLight.getConeDirection());
        setUniform(uniformName + ".cutoff",spotLight.getCutOff());
    }

    /**
     * creates a list of point light uniforms
     *
     * @param uniformName name
     * @param size list length
     * @throws Exception if uniform cant be found
     */
    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for(int i = 0; i < size;i++){
            createPointLightUniform(uniformName + "["+i+"]");
        }
    }

    /**
     * sets list of point light uniforms
     *
     * @param uniformName name
     * @param pointLights value
     */
    @SuppressWarnings("unused")
    public void setUniform(String uniformName, PointLight[] pointLights){
        int numLights = pointLights != null ? pointLights.length:0;
        for(int i = 0; i < numLights;i++){
            setUniform(uniformName,pointLights[i],i);
        }
    }

    /**
     * sets value of a point light uniform inside an array
     *
     * @param uniformName name
     * @param pointLight value
     * @param pos position in array
     */
    public void setUniform(String uniformName, PointLight pointLight, int pos) {
        setUniform(uniformName + "["+pos+"]",pointLight);
    }

    /**
     * creates a list of spot light uniforms
     *
     * @param uniformName name
     * @param size list length
     * @throws Exception if uniform cant be found
     */
    public void createSpotLightListUniform(String uniformName, int size) throws Exception {
        for(int i = 0; i < size;i++){
            createSpotLightUniform(uniformName + "["+i+"]");
        }
    }

    /**
     * sets list of spot light uniforms
     *
     * @param uniformName name
     * @param spotLights value
     */
    @SuppressWarnings("unused")
    public void setUniform(String uniformName, SpotLight[] spotLights){
        int numLights = spotLights != null ? spotLights.length:0;
        for(int i = 0; i < numLights;i++){
            setUniform(uniformName,spotLights[i],i);
        }
    }

    /**
     * sets value of a spot light uniform inside an array
     *
     * @param uniformName name
     * @param spotLight value
     * @param pos position in array
     */
    public void setUniform(String uniformName, SpotLight spotLight, int pos) {
        setUniform(uniformName + "["+pos+"]",spotLight);
    }

    /**
     * sets the value of an array of uniforms
     *
     * @param uniformName name of the uniform
     * @param colors colors ( max 4 )
     */
    public void setUniform(String uniformName, Vector4f[] colors) {
        int numColors = colors != null ? colors.length : 0;
        for(int i = 0; i < numColors;i++) {
            setUniform(uniformName + "[" + i + "]",colors[i]);
        }
    }

    /**
     * creates an array of vector4f uniforms
     *
     * @param uniformName name of the array
     * @param size size of the array
     * @throws Exception if array cant be found
     */
    @SuppressWarnings("WeakerAccess")
    public void createVector4fArrayUniform(String uniformName, int size) throws Exception {
        for(int i = 0; i < size;i++){
            createUniforms(uniformName + "["+i+"]");
        }
    }
}
