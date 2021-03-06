package engine.hud.components.contentcomponents;

import engine.general.Transformation;
import engine.graph.items.Texture;
import engine.hud.HudShaderManager;
import engine.hud.constraints.elementSizeConstraints.ElementSizeConstraint;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * class to add an image to the hud as a texture of a component
 */
public class TextureComponent extends QuadComponent {

    /**
     * texture of the component
     */
    private Texture texture;

    /**
     * constructor sets the texture as the material of the quad
     *
     * @param texture of teh component
     */
    public TextureComponent(Texture texture) {
        this.getQuad().getMesh().getMaterial().setTexture(texture);
        this.texture = texture;
    }

    /**
     * sets teh shader up for drawing a texture to the quad
     *
     * @param orthographic projection matrix for the hud
     * @param transformation transformation class for matrix calculations
     * @param shaderManager shader manager of the hud provides the shader reference
     */
    @Override
    public void setupShader(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager) {
        Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(quad, orthographic);
        ShaderProgram shader = hud.getShaderManager().getMaskShader();


        shader.setUniform("projModelMatrix", projModelMatrix);
        shader.setUniform("useTransparent", maskMode == MaskMode.USE_TRANSPARENT?1:0);
        shader.setUniform("useTexture",1);
        shader.setUniform("texture2d",0);

        switch (getCornerProportion()) {
            case FREE:
                shader.setUniform("keepCornerProportion", 0);
                shader.setUniform("cornerScale",new Vector2f(-0.5f, -0.5f).add(cornerSize.getValue(this, ElementSizeConstraint.Proportion.KEEP_WIDTH),cornerSize.getValue(this, ElementSizeConstraint.Proportion.KEEP_WIDTH)));
                shader.setUniform("cornerSize",cornerSize.getValue(this, ElementSizeConstraint.Proportion.KEEP_WIDTH));
                break;
            case KEEP_WIDTH:
                shader.setUniform("keepCornerProportion", (getOnScreenWidth() / getOnScreenHeight()) * ((float) getWindow().getWidth() / getWindow().getHeight()));
                shader.setUniform("cornerScale",
                        new Vector2f(((getOnScreenWidth() * 0.5f)/getOnScreenHeight()) * (window.getWidth()/(float)window.getHeight()), 0.5f));
                shader.setUniform("cornerSize",cornerSize.getValue(this, ElementSizeConstraint.Proportion.KEEP_HEIGHT));
                break;
            case KEEP_HEIGHT:
                shader.setUniform("keepCornerProportion", -(getOnScreenWidth() / getOnScreenHeight()) * ((float) getWindow().getWidth() / getWindow().getHeight()));
                shader.setUniform("cornerScale",
                        new Vector2f(0.5f, ((((getOnScreenHeight() * 0.5f)/getOnScreenWidth()) / (window.getWidth()/(float)window.getHeight())))));
                shader.setUniform("cornerSize",cornerSize.getValue(this, ElementSizeConstraint.Proportion.KEEP_WIDTH));

        }



        drawMesh();
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
