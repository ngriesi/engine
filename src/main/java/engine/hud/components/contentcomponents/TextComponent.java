package engine.hud.components.contentcomponents;

import engine.general.Transformation;
import engine.graph.items.Mesh;
import engine.hud.HudShaderManager;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.SubComponent;
import engine.hud.constraints.sizeConstraints.RelativeToWindowSize;
import engine.hud.constraints.sizeConstraints.TextAspectRatio;
import engine.hud.text.FontTexture;
import engine.hud.text.TextItem;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;

/**
 * class used to display a text with a transparent background
 */
@SuppressWarnings("unused")
public class TextComponent extends SubComponent {

    /** determines what effect transparent pixels have on the mask */
    @SuppressWarnings("unused")
    public enum MaskMode {
        DISPOSE_TRANSPARENT,USE_TRANSPARENT
    }

    /** contains meshes of the component */
    private final TextItem textItem;

    /** text of the component */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String text;

    /** way the mask for the children is created */
    private QuadComponent.MaskMode maskMode = SubComponent.MaskMode.DISPOSE_TRANSPARENT;

    /**
     *  colors of the quad, when color shade is off only the first one is used,
     *  when its on the colors in the array are the colors at the edges of the
     *  quad and everything between is shaded
     */
    private ColorScheme colors = new ColorScheme(Color.BLACK);

    /**
     * if true all four colors are used otherwise only the first
     */
    private boolean useColorShade;

    /**
     * position offset dependent on the alignment of the text. Due to the way the TextItem gets created,
     * is has to have different offsets for different alignments
     */
    private float alignmentOffset = 0;

    /**
     * creates the text component without setting a text,
     * only to be used by sub components which set the text themselves
     */
    public TextComponent(FontTexture fontTexture) {
        super();
        textItem = new TextItem(fontTexture);
        textItem.setText("");
        setHeightConstraint(new TextAspectRatio());
        setWidthConstraint(new RelativeToWindowSize(1));

        calculateAlignmentOffset();

    }

    /**
     * sets the offsetAlignment dependent of the textAlignment of the TextItem
     */
    private void calculateAlignmentOffset() {
        switch (textItem.getAlignment()) {
            case CENTER:
            case RIGHT:
                alignmentOffset = 0;break;
            case LEFT: alignmentOffset = -0.5f;break;
        }
    }

    public float getAlignmentOffset() {
        return alignmentOffset;
    }

    /**
     * sets the alignment of the text and calculates the right offset for it
     *
     * @param alignment new alignment of the TextItem
     */
    public void setTextAlignment(TextItem.TextAlignment alignment) {
        textItem.setAlignment(alignment);
        calculateAlignmentOffset();
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

    /**
     * extra method for position update to be called by other components which need
     * to move text inside of them
     */
    @SuppressWarnings("WeakerAccess")
    protected void updatePosition() {


        textItem.setPosition(super.getOnScreenXPosition() + super.getOnScreenWidth() * (super.getxOffset() + alignmentOffset),(super.getOnScreenYPosition() + super.getOnScreenHeight() * super.getyOffset()),textItem.getPosition().z);
    }

    /**
     * sets the depth value of the component by setting its z position to
     * the passed value
     *
     * @param depthValue new depth value (z cord)
     */
    @Override
    public void setDepthValue(float depthValue) {
        textItem.setPosition(textItem.getPosition().x,textItem.getPosition().y,depthValue);
    }

    /**
     * sets up the shader for drawing the text component and draws it to the screen
     *
     * @param orthographic projection matrix for the hud
     * @param transformation transformation class for matrix calculations
     * @param shaderManager shader manager of the hud provides the shader reference
     */
    @Override
    public void setupShader(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager) {
        Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(textItem, orthographic);
        ShaderProgram shader = hud.getShaderManager().getMaskShader();


        shader.setUniform("projModelMatrix", projModelMatrix);
        shader.setUniform("transparencyMode", maskMode.ordinal());

        shader.setUniform("useTexture",2);

        shader.setUniform("colors", colors.getVectorArray());
        shader.setUniform("useColorShade",useColorShade?1:0);



        shader.setUniform("cornerSize",0);

        shader.setUniform("edgeSize",1);

        drawMesh();
    }


    /**
     * draws the mesh
     */
    @Override
    public void drawMesh() {
        Mesh mesh = textItem.getMesh();
        mesh.render();
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

    public String getText() {
        return getTextItem().getText();
    }

    public void setColorScheme(ColorScheme textColor) {
        this.colors = new ColorScheme(textColor);
    }

    public void setUseColorShade(boolean useColorShade) {
        this.useColorShade = useColorShade;
    }
}
