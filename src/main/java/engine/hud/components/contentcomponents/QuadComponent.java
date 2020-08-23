package engine.hud.components.contentcomponents;

import engine.general.Transformation;
import engine.graph.items.Mesh;
import engine.hud.assets.Edge;
import engine.hud.assets.Quad;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.Component;
import engine.hud.components.SubComponent;
import engine.hud.constraints.elementSizeConstraints.ElementSizeConstraint;
import engine.hud.constraints.elementSizeConstraints.RelativeToComponentSizeE;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class QuadComponent extends SubComponent {



    /** determines proportion of corners */
    public enum CornerProportion {
        KEEP_X,KEEP_Y,FREE
    }

    /** determines proportion of edges */
    public enum EdgeProportion {
        KEEP_X,KEEP_Y,FREE
    }

    /** determines what effect transparent pixels have on the mask */
    public enum MaskMode {
        DISPOSE_TRANSPARENT,USE_TRANSPARENT
    }

    /** mesh of the component */
    private Quad quad;

    /** if not FREE corners are the same in both x and y direction */
    private CornerProportion cornerProportion;

    /** if not FREE edges are the same in both x and y direction */
    private EdgeProportion edgeProportion;

    /** way the mask for the children is created */
    private MaskMode maskMode;

    /** size of the rounded corners relative to component size (0 disables rounded corners)*/
    private ElementSizeConstraint cornerSize;

    private Edge outerEdge,middleEdge,innerEdge;

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
        cornerProportion = CornerProportion.FREE;
        edgeProportion = EdgeProportion.FREE;
        maskMode = MaskMode.USE_TRANSPARENT;
        cornerSize = new RelativeToComponentSizeE(0);
        colors = new ColorScheme();
        useColorShade = false;
        outerEdge = new Edge();
        middleEdge = new Edge();
        innerEdge = new Edge();


    }

    /**
     * uses the updated values form update bounds to update the GameItem of the quad
     */
    @Override
    public void updateBounds() {

        super.updateBounds();
        quad.setPosition((super.getOnScreenXPosition() + super.getOnScreenWidth() * super.getxOffset()),(super.getOnScreenYPosition() + super.getOnScreenHeight() * super.getyOffset()),quad.getPosition().z);
        float rotationComplete = calculateRotation();
        float rotation = Math.abs(rotationComplete);

        float aspectRatio = window==null?1:window.getWidth()/(float)window.getHeight();


        double sinus = Math.cos(Math.toRadians(rotation));



        quad.setScale3(
                (float) (super.getOnScreenWidth() * (sinus) + super.getOnScreenWidth() * (aspectRatio) * (float) (1-sinus)),
                (float) (super.getOnScreenHeight() * (sinus) + super.getOnScreenHeight() * (1/aspectRatio) * (float) (1-sinus)),
                quad.getScale3().z);
        quad.setRotation(quad.getRotation().x,quad.getRotation().y,rotationComplete);


    }

    /**
     * calculates the absolute rotation of the component
     * @return rotation (absolute)
     */

    private float calculateRotation() {
        if(useAbsoluteRotation) {
            if(window != null) {

                double tan1 = Math.tan(Math.toRadians(rotation));

                double result = Math.toDegrees(Math.atan(tan1 * ((float)window.getWidth()/(float)window.getHeight())));

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
     * @param ortho orthographic transformation
     * @param transformation transformation object
     * @param hudShaderProgram shader
     */
    @Override
    public void drawMesh(Matrix4f ortho, Transformation transformation, ShaderProgram hudShaderProgram, Component.RenderMode renderMode) {

        Mesh mesh = quad.getMesh();
        Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(quad, ortho);
        hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
        hudShaderProgram.setUniform("depth",getId());
        hudShaderProgram.setUniform("isText",0);
        if(renderMode == Component.RenderMode.NORMAL) {
            hudShaderProgram.setUniform("colors", colors.getVectorArray());
            hudShaderProgram.setUniform("useColorShade", useColorShade ? 1 : 0);
            hudShaderProgram.setUniform("hasTexture", mesh.getMaterial().isTexture() ? 1 : 0);
            switch (cornerProportion) {
                case FREE:
                    hudShaderProgram.setUniform("keepCornerProportion", 0);
                    break;
                case KEEP_Y:
                    hudShaderProgram.setUniform("keepCornerProportion", (super.getOnScreenWidth() / super.getOnScreenHeight()) * ((float) super.getWindow().getWidth() / super.getWindow().getHeight()));
                    break;
                case KEEP_X:
                    hudShaderProgram.setUniform("keepCornerProportion", -(super.getOnScreenWidth() / super.getOnScreenHeight()) * ((float) super.getWindow().getWidth() / super.getWindow().getHeight()));
            }

            hudShaderProgram.setUniform("maskMode", maskMode.ordinal());
            float cornerSize = this.cornerSize.getValue(this, ElementSizeConstraint.Direction.WIDTH);
            hudShaderProgram.setUniform("cornerSize", cornerSize);
            if(cornerSize == 0) {
                switch (edgeProportion) {
                    case FREE:
                        hudShaderProgram.setUniform("keepEdgeProportion",0);
                        break;
                    case KEEP_Y:
                        hudShaderProgram.setUniform("keepEdgeProportion", (super.getOnScreenWidth() / super.getOnScreenHeight()) * ((float) super.getWindow().getWidth() / super.getWindow().getHeight()));
                        break;
                    case KEEP_X:
                        hudShaderProgram.setUniform("keepEdgeProportion", -(super.getOnScreenWidth() / super.getOnScreenHeight()) * ((float) super.getWindow().getWidth() / super.getWindow().getHeight()));
                }
            }
            hudShaderProgram.setUniform("outerEdge",outerEdge);
            hudShaderProgram.setUniform("middleEdge",middleEdge);
            hudShaderProgram.setUniform("innerEdge",innerEdge);

        } else {
            hudShaderProgram.setUniform("colors", new Vector4f[] {new Vector4f(1,1,1,1)});
            hudShaderProgram.setUniform("useColorShade",0);
            hudShaderProgram.setUniform("hasTexture", 0);
        }


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
     * @param id new id and z coordinate of the component
     */
    @Override
    protected void setId(int id) {
        super.setId(id);
        quad.setPosition(quad.getPosition().x,quad.getPosition().y,id);

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
        colors.setColor(new Color(color),side);
    }

    public void setRotation(float rotation) {

        this.rotation = rotation;
    }

    @SuppressWarnings("unused")
    public void setCornerProportion(CornerProportion cornerProportion) {
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

    public void setQuad(Quad quad) {
        this.quad = quad;
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

    public void setEdgeProportion(EdgeProportion edgeProportion) {
        this.edgeProportion = edgeProportion;
    }

    public Edge getOuterEdge() {
        return outerEdge;
    }

    public void setOuterEdge(Edge outerEdge) {
        this.outerEdge = outerEdge;
    }

    public Edge getMiddleEdge() {
        return middleEdge;
    }

    public void setMiddleEdge(Edge middleEdge) {
        this.middleEdge = middleEdge;
    }

    public Edge getInnerEdge() {
        return innerEdge;
    }

    public void setInnerEdge(Edge innerEdge) {
        this.innerEdge = innerEdge;
    }

    public void setUseAbsoluteRotation(boolean useAbsoluteRotation) {
        this.useAbsoluteRotation = useAbsoluteRotation;
    }
}
