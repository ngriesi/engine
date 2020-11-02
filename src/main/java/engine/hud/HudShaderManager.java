package engine.hud;

import engine.general.save.Resources;
import engine.render.ShaderProgram;

public class HudShaderManager {





    private ShaderProgram currentlyUsed;

    private ShaderProgram maskShader;

    public void init() throws Exception{
        setupMaskShader();
    }


    public void setupMaskShader() throws Exception {

        maskShader = new ShaderProgram();
        maskShader.createVertexShader(Resources.loadResource("/shader/hudshader/maskShader/maskShader.vs"));
        maskShader.createFragmentShader(Resources.loadResource("/shader/hudshader/maskShader/maskShader.fs"));
        maskShader.link();

        maskShader.createUniforms("projModelMatrix");
        maskShader.createUniforms("transparancyMode");
        maskShader.createUniforms("keepCornerProportion");
        maskShader.createUniforms("cornerScale");
        maskShader.createUniforms("cornerSize");
        maskShader.createUniforms("useTexture");
        maskShader.createUniforms("texture");
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

    public void setShaderProgram(ShaderProgram shader) {

        if(!shader.equals(currentlyUsed)) {
            if(currentlyUsed != null) {
                currentlyUsed.unbind();
            }
            currentlyUsed = shader;
            currentlyUsed.bind();
        }
    }

    public void cleanUp() {
        if(maskShader!= null) {
            maskShader.cleanup();
        }
    }


}
