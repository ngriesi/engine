package engine.hud.components.contentcomponents;

import engine.general.Transformation;
import engine.graph.items.Mesh;
import engine.hud.HudShaderManager;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.Component;
import engine.hud.components.SubComponent;
import engine.hud.constraints.sizeConstraints.RelativeToWindowSize;
import engine.hud.constraints.sizeConstraints.TextAspectRatio;
import engine.hud.text.FontTexture;
import engine.hud.text.TextItem;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class TextComponent extends SubComponent {



    /** determines what effect transparent pixels have on the mask */
    @SuppressWarnings("unused")
    public enum MaskMode {
        DISPOSE_TRANSPARENT,USE_TRANSPARENT
    }

    /** contains meshes of the component */
    private TextItem textItem;

    /** text of the component */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String text;

    /** way the mask for the children is created */
    private QuadComponent.MaskMode maskMode;

    /**
     *  colors of the quad, when color shade is off only the first one is used,
     *  when its on the colors in the array are the colors at the edges of the
     *  quad and everything between is shaded
     */
    private ColorScheme colors;

    /**
     * if true all four colors are used otherwise only the first
     */
    private boolean useColorShade;

    private float autoXOffset;

    private float autoYOffset;

    /**
     * creates the text component without setting a text,
     * only to be used by sub components which set the text themselves
     */
    public TextComponent(FontTexture fontTexture) {
        super();

        textItem = new TextItem(fontTexture);
        textItem.setText("");
        maskMode = QuadComponent.MaskMode.DISPOSE_TRANSPARENT;
        colors = new ColorScheme();
        useColorShade = false;
        setHeightConstraint(new TextAspectRatio());
        setWidthConstraint(new RelativeToWindowSize(1));
        autoXOffset = 0;
        autoYOffset = 0;

    }

    /**
     * sets an new text for the component
     *
     * @param text new Text
     */
    public void setText(String text) {
        this.text = text;
        textItem.setText(text);
        if(getParent() != null) {
            updateBounds();
        }
    }

    /**
     * uses the updated values form update bounds to update the GameItem of the quad
     */
    @Override
    public void updateBounds() {

        super.updateBounds();

        textItem.setScale3(super.getOnScreenWidth()/textItem.getWidth(),super.getOnScreenHeight()/textItem.getHeight(),textItem.getPosition().z);
        updatePosition();

    }

    @SuppressWarnings("WeakerAccess")
    protected void updatePosition() {
        textItem.setPosition(super.getOnScreenXPosition() + super.getOnScreenWidth() * super.getxOffset() + autoXOffset,(super.getOnScreenYPosition() + super.getOnScreenHeight() * super.getyOffset() + autoYOffset),textItem.getPosition().z);
    }

    /**
     * renders the quad mesh to the screen and than calls the render method of all its content components
     *
     * @param ortho orthographic transformation
     * @param transformation transformation object
     * @param hudShaderProgram shader
     */
   // @Override
    public void drawMesh(Matrix4f ortho, Transformation transformation, ShaderProgram hudShaderProgram, RenderMode renderMode) {

        Mesh mesh = textItem.getMesh();
        Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(textItem, ortho);
        hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
        hudShaderProgram.setUniform("depth",getId());
        hudShaderProgram.setUniform("isText",1);
        hudShaderProgram.setUniform("full",0.5f);
        if(renderMode == RenderMode.NORMAL) {
            hudShaderProgram.setUniform("colors", colors.getVectorArray());

            hudShaderProgram.setUniform("useColorShade", useColorShade ? 1 : 0);
            hudShaderProgram.setUniform("hasTexture", mesh.getMaterial().isTexture() ? 1 : 0);

            hudShaderProgram.setUniform("maskMode", maskMode.ordinal());


        } else {
            hudShaderProgram.setUniform("colors", new Vector4f[] {new Vector4f(1,1,1,1)});
            hudShaderProgram.setUniform("useColorShade",0);
            hudShaderProgram.setUniform("hasTexture", 0);
        }


        mesh.render();
    }

    @Override
    public void setDepthValue(float depthValue) {

    }

    @Override
    public void renderSimpleStencil(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager) {

    }

    @Override
    public void addToShaderList(HudShaderManager hudShaderManager) {

    }

    @Override
    public void drawMesh() {

    }

    /**
     * cleans up the mesh
     */
    @Override
    public void cleanup() {
        textItem.getMesh().cleanup();
        super.cleanup();
    }

    /**
     * sets the id, quad component needs to set its z coordinate as its id for the id system to work (mouse picker)
     * @param id new id and z coordinate of the component
     */
    @Override
    protected void setId(int id) {
        super.setId(id);
        textItem.setPosition(textItem.getPosition().x,textItem.getPosition().y,id);
    }

    /**
     * sets the first color (only color used if shading is disabled)
     *
     * @param color new color
     */
    public void setColors(Color color) {
        colors.setAllColors(color);
    }

    /**
     * sets color of specific side (only used if color shading is true
     */
    @SuppressWarnings("unused")
    public void setColor(Color color, ColorScheme.ColorSide side) {
        colors.setColor(new Color(color),side);
    }

    @SuppressWarnings("unused")
    public void setMaskMode(QuadComponent.MaskMode maskMode) {
        this.maskMode = maskMode;
    }

    @SuppressWarnings("unused")
    public Color getColor(ColorScheme.ColorSide side) {
        return colors.getColor(side);
    }

    public TextItem getTextItem() {
        return textItem;
    }

    @SuppressWarnings("WeakerAccess")
    public QuadComponent.MaskMode getMaskMode() {
        return maskMode;
    }

    public float getAutoXOffset() {
        return autoXOffset;
    }

    public void setAutoXOffset(float autoXOffset) {
        this.autoXOffset = autoXOffset;
    }

    public float getAutoYOffset() {
        return autoYOffset;
    }

    public void setAutoYOffset(float autoYOffset) {
        this.autoYOffset = autoYOffset;
    }

    public String getText() {
        return getTextItem().getText();
    }


    public void setColorScheme(ColorScheme textColor) {
        this.colors = new ColorScheme(textColor);
    }
}
