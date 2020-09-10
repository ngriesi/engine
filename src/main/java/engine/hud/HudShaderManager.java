package engine.hud;

import engine.general.Transformation;
import engine.general.save.Resources;
import engine.hud.color.ColorScheme;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class HudShaderManager {

    private ShaderProgram currentlyUsed;

    private ShaderProgram maskShader;

    private ShaderProgram simpleQuadShader;

    private List<QuadComponent> simpleQuadShaderList;

    private ShaderProgram colorShadeQuadShader;

    private List<QuadComponent> colorShadeQuadShaderList;

    private ShaderProgram simpleTextureShader;

    public HudShaderManager() throws Exception {
        simpleQuadShaderList = new ArrayList<>();
        colorShadeQuadShaderList = new ArrayList<>();
    }

    public void init() throws Exception{
        setupMaskShader();
        setupSimpleQuadShader();
        setupSimpleTextureShader();
        setupColorShadeQuadShader();

    }

    public void renderFrame(Matrix4f orthographic, Transformation transformation) {

        ShaderProgram shader = getSimpleQuadShader();
        setShaderProgram(shader);
        for(QuadComponent quadComponent : simpleQuadShaderList) {
            Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(quadComponent.getQuad(), orthographic);


            shader.setUniform("projModelMatrix", projModelMatrix);
            shader.setUniform("inColor", quadComponent.getColor(ColorScheme.ColorSide.RIGHT).getColor());

            quadComponent.drawMesh();

        }

        simpleQuadShaderList.clear();

        shader = getColorShadeQuadShader();
        setShaderProgram(shader);

        for(QuadComponent quadComponent : colorShadeQuadShaderList) {
            Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(quadComponent.getQuad(), orthographic);


            shader.setUniform("projModelMatrix", projModelMatrix);
            shader.setUniform("colors", quadComponent.getColorSheme().getVectorArray());

            quadComponent.drawMesh();
        }

        colorShadeQuadShaderList.clear();
    }

    public void setupSimpleTextureShader() throws Exception {
        simpleTextureShader = new ShaderProgram();
        simpleTextureShader.createVertexShader(Resources.loadResource("/shader/hudshader/simpleTextureShader.vs"));
        simpleTextureShader.createFragmentShader(Resources.loadResource("/shader/hudshader/simpleTextureFragmentShader.fs"));
        simpleTextureShader.link();

        simpleTextureShader.createUniforms("projModelMatrix");
        simpleTextureShader.createUniforms("texture_sampler");
    }

    public ShaderProgram getSimpleTextureShader() {
        return simpleTextureShader;
    }

    public void setupMaskShader() throws Exception {

        maskShader = new ShaderProgram();
        maskShader.createVertexShader(Resources.loadResource("/shader/hudshader/maskShader.vs"));
        maskShader.createFragmentShader(Resources.loadResource("/shader/hudshader/maskShader.fs"));
        maskShader.link();

        maskShader.createUniforms("projModelMatrix");
        maskShader.createUniforms("useTransparent");

    }

    public ShaderProgram getMaskShader() {
        return maskShader;
    }

    private void setupColorShadeQuadShader() throws Exception{
        colorShadeQuadShader = new ShaderProgram();
        colorShadeQuadShader.createVertexShader(Resources.loadResource("/shader/hudshader/colorShadeQuadVertexShader.vs"));
        colorShadeQuadShader.createFragmentShader(Resources.loadResource("/shader/hudshader/simpleQuadFragmentShader.fs"));
        colorShadeQuadShader.link();

        colorShadeQuadShader.createUniforms("projModelMatrix");
        colorShadeQuadShader.createVector4fArrayUniform("colors",4);


    }

    public ShaderProgram getColorShadeQuadShader() {
        return colorShadeQuadShader;
    }

    public void addToColorShadeQuadShaderList(QuadComponent component) {
        colorShadeQuadShaderList.add(component);
    }

    private void setupSimpleQuadShader() throws Exception {
        simpleQuadShader = new ShaderProgram();
        simpleQuadShader.createVertexShader(Resources.loadResource("/shader/hudshader/simpleQuadVertexShader.vs"));
        simpleQuadShader.createFragmentShader(Resources.loadResource("/shader/hudshader/simpleQuadFragmentShader.fs"));
        simpleQuadShader.link();

        simpleQuadShader.createUniforms("projModelMatrix");
        simpleQuadShader.createUniforms("inColor");
    }

    public ShaderProgram getSimpleQuadShader() {
        return simpleQuadShader;
    }

    public void addToSimpleQuadShaderList(QuadComponent component) {
        simpleQuadShaderList.add(component);
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
        if(simpleQuadShader!= null) {
            simpleQuadShader.cleanup();
        }
        if(colorShadeQuadShader != null) {
            colorShadeQuadShader.cleanup();
        }
    }


}
