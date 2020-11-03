package engine.hud.components.contentcomponents;

import engine.general.Transformation;
import engine.graph.items.Mesh;
import engine.hud.HudShaderManager;
import engine.hud.assets.Edge;
import engine.hud.assets.Quad;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.SubComponent;
import engine.hud.constraints.elementSizeConstraints.ElementSizeConstraint;
import engine.hud.constraints.elementSizeConstraints.RelativeToComponentSizeE;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static engine.hud.constraints.elementSizeConstraints.ElementSizeConstraint.Proportion.*;

public class QuadComponent extends SubComponent {


    /**
     * mesh of the component
     */
    Quad quad;
    /**
     * if not FREE corners are the same in both x and y direction
     */
    private ElementSizeConstraint.Proportion cornerProportion;
    /**
     * if not FREE edges are the same in both x and y direction
     */
    private ElementSizeConstraint.Proportion edgeProportion;
    /**
     * way the mask for the children is created
     */
    MaskMode maskMode;
    /**
     * size of the rounded corners relative to component size (0 disables rounded corners)
     */
    ElementSizeConstraint cornerSize;
    /**
     * edges of the component
     */
    private Edge edge;
    /**
     * colors of the quad, when color shade is off only the first one is used,
     * when its on the colors in the array are the colors at the edges of the
     * quad and everything between is shaded
     */
    private ColorScheme colors;
    /**
     * if true all four colors are used otherwise only the first
     */
    private boolean useColorShade;
    /**
     * rotation of the component, one circle equals 360.0f
     */
    private float rotation;
    /**
     * if true the window size influences the way the rotation is displayed
     */
    private boolean useAbsoluteRotation = true;

    /**
     * creates a quad for the component
     * sets default values
     */
    public QuadComponent() {
        super();
        quad = new Quad();
        cornerProportion = FREE;
        edgeProportion = FREE;
        maskMode = MaskMode.USE_TRANSPARENT;
        cornerSize = new RelativeToComponentSizeE(0);
        colors = new ColorScheme();
        useColorShade = false;
        edge = new Edge();


    }

    @Override
    public void setDepthValue(float depthValue) {
        quad.setPosition(quad.getPosition().x,quad.getPosition().y,depthValue);
    }

    @Override
    public void setupShader(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager) {
        Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(quad, orthographic);
        ShaderProgram shader = hud.getShaderManager().getMaskShader();


        shader.setUniform("projModelMatrix", projModelMatrix);
        shader.setUniform("transparancyMode", maskMode.ordinal());

        shader.setUniform("useTexture",0);

        shader.setUniform("colors", getColorSheme().getVectorArray());
        shader.setUniform("useColorShade",isUseColorShade()?1:0);

        shader.setUniform("edgeStartColor",edge.getStartColor().getVector4f());
        shader.setUniform("edgeEndColor",edge.getEndColor().getVector4f());

        shader.setUniform("edgeBlendMode",edge.getBlendMode().ordinal());

        float cornerSizeTemp = cornerSize.getValue(this,cornerProportion);
        shader.setUniform("cornerSize",cornerSizeTemp);

        shader.setUniform("edgeSize",cornerSizeTemp!=0?cornerSizeTemp - (edge.getSize().getValue(this,edgeProportion) * cornerSizeTemp):1 - edge.getSize().getValue(this,edgeProportion));

        if(cornerSize.getAbsoluteValue() > 0) {
            switch (getCornerProportion()) {
                case FREE:
                    shader.setUniform("keepCornerProportion", 0);
                    shader.setUniform("cornerScale",
                            new Vector2f(0.5f, 0.5f));
                    break;
                case KEEP_HEIGHT:
                    shader.setUniform("keepCornerProportion", (getOnScreenWidth() / getOnScreenHeight()) * ((float) getWindow().getWidth() / getWindow().getHeight()));
                    shader.setUniform("cornerScale",
                            new Vector2f(-((getOnScreenWidth() * 0.5f) / getOnScreenHeight()) * (window.getWidth() / (float) window.getHeight()), -0.5f));
                    break;
                case KEEP_WIDTH:
                    shader.setUniform("keepCornerProportion", -(getOnScreenWidth() / getOnScreenHeight()) * ((float) getWindow().getWidth() / getWindow().getHeight()));
                    shader.setUniform("cornerScale",
                            new Vector2f(0.5f, ((((getOnScreenHeight() * 0.5f) / getOnScreenWidth()) / (window.getWidth() / (float) window.getHeight())))));

            }
        } else if(getEdge().getSize().getAbsoluteValue() > 0){
            switch (edgeProportion) {
                case FREE:
                    shader.setUniform("keepCornerProportion", 0);
                    break;
                case KEEP_HEIGHT:
                    shader.setUniform("keepCornerProportion", (getOnScreenWidth() / getOnScreenHeight()) * ((float) getWindow().getWidth() / getWindow().getHeight()));
                    break;
                case KEEP_WIDTH:
                    shader.setUniform("keepCornerProportion", -(getOnScreenWidth() / getOnScreenHeight()) * ((float) getWindow().getWidth() / getWindow().getHeight()));
                    break;

            }
        }



        drawMesh();
    }

    /**
     * uses the updated values form update bounds to update the GameItem of the quad
     */
    @Override
    public void updateBounds() {

        super.updateBounds();
        quad.setPosition((super.getOnScreenXPosition() + super.getOnScreenWidth() * super.getxOffset()), (super.getOnScreenYPosition() + super.getOnScreenHeight() * super.getyOffset()), quad.getPosition().z);
        float rotationComplete = calculateRotation();
        float rotation = Math.abs(rotationComplete);

        float aspectRatio = window == null ? 1 : window.getWidth() / (float) window.getHeight();


        double sinus = Math.cos(Math.toRadians(rotation));


        quad.setScale3(
                (float) (super.getOnScreenWidth() * (sinus) + super.getOnScreenWidth() * (aspectRatio) * (float) (1 - sinus)),
                (float) (super.getOnScreenHeight() * (sinus) + super.getOnScreenHeight() * (1 / aspectRatio) * (float) (1 - sinus)),
                quad.getScale3().z);
        quad.setRotation(quad.getRotation().x, quad.getRotation().y, rotationComplete);


    }

    /**
     * calculates the absolute rotation of the component
     *
     * @return rotation (absolute)
     */

    private float calculateRotation() {
        if (useAbsoluteRotation) {
            if (window != null) {

                double tan1 = Math.tan(Math.toRadians(rotation));

                double result = Math.toDegrees(Math.atan(tan1 * ((float) window.getWidth() / (float) window.getHeight())));

                return (float) result;
            } else {
                return rotation;
            }
        } else {
            return rotation;
        }
    }


    /**
     * renders the quad mesh to the screen and than calls the render method of all its content components
     *
     */
    @Override
    public void drawMesh() {

        Mesh mesh = quad.getMesh();
        mesh.render();
    }

    /**
     * cleans up the mesh
     */
    @Override
    public void cleanup() {
        quad.getMesh().cleanup();
        super.cleanup();
    }

    /**
     * sets the id, quad component needs to set its z coordinate as its id for the id system to work (mouse picker)
     *
     * @param id new id and z coordinate of the component
     */
    @Override
    protected void setId(int id) {
        super.setId(id);
        quad.setPosition(quad.getPosition().x, quad.getPosition().y, id);

    }

    /**
     * sets the first color (only color used if shading is disabled)
     *
     * @param color new color
     */
    public void setColors(Color color) {
        colors.setAllColors(new Color(color));
    }

    /**
     * sets color of specific side (only used if color shading is true
     */
    public void setColor(Color color, ColorScheme.ColorSide side) {
        colors.setColor(new Color(color), side);
    }

    public void setColor(Color color) {
        colors.setColor(color, ColorScheme.ColorSide.LEFT);
    }

    @SuppressWarnings("unused")
    public void setCornerProportion(ElementSizeConstraint.Proportion cornerProportion) {
        this.cornerProportion = cornerProportion;
    }

    public Quad getQuad() {
        return quad;
    }

    @SuppressWarnings("unused")
    public void setMaskMode(MaskMode maskMode) {
        this.maskMode = maskMode;
    }

    @SuppressWarnings("unused")
    public void setCornerSize(float cornerSize) {
        this.cornerSize = new RelativeToComponentSizeE(cornerSize);
    }

    public void setCornerSize(ElementSizeConstraint cornerSize) {
        this.cornerSize = cornerSize;
    }

    public Color getColor(ColorScheme.ColorSide side) {
        return colors.getColor(side);
    }

    public void setUseColorShade(boolean useColorShade) {
        this.useColorShade = useColorShade;
    }

    public void setColorScheme(ColorScheme colors) {
        this.colors = new ColorScheme(colors);
    }

    public ColorScheme getColorSheme() {
        return colors;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {

        this.rotation = rotation;
    }

    public void setEdgeProportion(ElementSizeConstraint.Proportion edgeProportion) {
        this.edgeProportion = edgeProportion;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public void setUseAbsoluteRotation(boolean useAbsoluteRotation) {
        this.useAbsoluteRotation = useAbsoluteRotation;
    }

    public boolean isUseColorShade() {
        return useColorShade;
    }

    public ElementSizeConstraint.Proportion getCornerProportion() {
        return cornerProportion;
    }

    public ElementSizeConstraint getCornerSize() {
        return cornerSize;
    }
}
