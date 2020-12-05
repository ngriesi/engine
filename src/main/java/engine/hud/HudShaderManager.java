package engine.hud;

import engine.general.save.Resources;
import engine.render.ShaderProgram;

/**
 * class handles the setup and use of the shader of the hud
 */
@SuppressWarnings("unused")
public class HudShaderManager {

    /**
     * shader program for the hud
     */
    private ShaderProgram maskShader;

    /**
     * initialises the shader
     *
     * @throws Exception if shader cant be created
     */
    public void init() throws Exception{
        setupMaskShader();
    }

    /**
     * initialises the shader
     *
     * @throws Exception if shader cant be created
     */
    public void setupMaskShader() throws Exception {

        maskShader = new ShaderProgram();

        // loads shader code
        maskShader.createVertexShader(Resources.loadResource("/shader/hudShader/maskShader/maskVertexShader.shader"));
        maskShader.createFragmentShader(Resources.loadResource("/shader/hudShader/maskShader/maskFragmentShader.shader"));
        maskShader.link();

        // creates uniforms
        maskShader.createUniforms("projModelMatrix");
        maskShader.createUniforms("transparencyMode");
        maskShader.createUniforms("keepCornerProportion");
        maskShader.createUniforms("cornerScale");
        maskShader.createUniforms("cornerSize");
        maskShader.createUniforms("useTexture");
        maskShader.createUniforms("texture2d");
        maskShader.createVector4fArrayUniform("colors",4);
        maskShader.createUniforms("useColorShade");
        maskShader.createUniforms("edgeStartColor");
        maskShader.createUniforms("edgeEndColor");
        maskShader.createUniforms("edgeSize");
        maskShader.createUniforms("edgeBlendMode");


    }

    public ShaderProgram getMaskShader() {
        return maskShader;
    }

    /**
     * unbinds the shader
     */
    public void unbindShader() {
        maskShader.unbind();
    }

    /**
     * binds the shader
     */
    public void bindShader() {
        maskShader.bind();
    }

    /**
     * cleans the shader up
     */
    public void cleanUp() {
        if(maskShader!= null) {
            maskShader.cleanup();
        }
    }


}
