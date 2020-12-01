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

/**
 * The Quad component class provides the implementation to add a rectangle to the hud scene.
 * The rect can be scaled, colored, have rounded corners and an edge
 */
@SuppressWarnings("unused")
public class QuadComponent extends SubComponent {


    /**
     * mesh of the component
     */
    Quad quad = new Quad();

    /**
     * if not FREE corners are the same in both x and y direction
     */
    private ElementSizeConstraint.Proportion cornerProportion = FREE;

    /**
     * if not FREE edges are the same in both x and y direction
     */
    private ElementSizeConstraint.Proportion edgeProportion = FREE;

    /**
     * defines if transparent areas should be handled as colored areas or different
     */
    MaskMode maskMode = MaskMode.USE_TRANSPARENT;

    /**
     * size of the rounded corners relative to component size (0 disables rounded corners)
     */
    ElementSizeConstraint cornerSize = new RelativeToComponentSizeE(0);

    /**
     * edges of the component
     */
    private Edge edge = new Edge();

    /**
     * colors of the quad, when color shade is off only the first one is used,
     * when its on the colors in the array are the colors at the edges of the
     * quad and everything between is shaded
     */
    private ColorScheme colors = new ColorScheme();

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
     * sets the depth value of the component by setting its z position to
     * the passed value
     *
     * @param depthValue new depth value (z cord)
     */
    @Override
    public void setDepthValue(float depthValue) {
        quad.setPosition(quad.getPosition().x,quad.getPosition().y,depthValue);
    }

    /**
     * sets up the shader for drawing the quad and draws it to the screen
     *
     * @param orthographic projection matrix for the hud
     * @param transformation transformation class for matrix calculations
     * @param shaderManager shader manager of the hud provides the shader reference
     */
    @Override
    public void setupShader(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager) {
        //calculates the projection model matrix for this component
        Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(quad, orthographic);

        ShaderProgram shader = hud.getShaderManager().getMaskShader();

        /*
            SET UNIFORM VALUES
         */
        shader.setUniform("projModelMatrix", projModelMatrix);
        shader.setUniform("transparencyMode", maskMode.ordinal());

        shader.setUniform("useTexture",0);

        shader.setUniform("colors", getColorScheme().getVectorArray());
        shader.setUniform("useColorShade",isUseColorShade()?1:0);   //boolean is made to an int uniform

        shader.setUniform("edgeStartColor",edge.getStartColor().getVector4f());
        shader.setUniform("edgeEndColor",edge.getEndColor().getVector4f());

        shader.setUniform("edgeBlendMode",edge.getBlendMode().ordinal());

        float cornerSizeTemp = cornerSize.getValue(this,cornerProportion);
        shader.setUniform("cornerSize",cornerSizeTemp);

        shader.setUniform("edgeSize",cornerSizeTemp!=0?cornerSizeTemp - (edge.getSize().getValue(this,edgeProportion) * cornerSizeTemp):1 - edge.getSize().getValue(this,edgeProportion));


        /*
           CALCULATING AND SETTING VALUES FOR CORNER CREATION
         */
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



        //draws the mesh
        drawMesh();
    }

    /**
     * uses the updated values form update bounds to update the GameItem of the quad
     */
    @Override
    public void updateBounds() {

        // updates the values of this component
        super.updateBounds();

        // sets the updated values for the quads new position
        quad.setPosition((super.getOnScreenXPosition() + super.getOnScreenWidth() * super.getXOffset()), (super.getOnScreenYPosition() + super.getOnScreenHeight() * super.getYOffset()), quad.getPosition().z);

        // calculates value updates for the rotation
        float rotationComplete = calculateRotation();
        float rotation = Math.abs(rotationComplete);

        float aspectRatio = window == null ? 1 : window.getWidth() / (float) window.getHeight();


        double sinus = Math.cos(Math.toRadians(rotation));


        // sets new values for rotation and scale, scale is used to create a rotation
        // that is independent from the window size
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

    public ColorScheme getColorScheme() {
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
