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
import org.joml.Vector4f;

public class QuadComponent extends SubComponent {

    /**
     * determines proportion of corners
     */
    public enum CornerProportion {
        KEEP_X, KEEP_Y, FREE
    }

    /**
     * determines proportion of edges
     */
    public enum EdgeProportion {
        KEEP_X, KEEP_Y, FREE
    }


    /**
     * mesh of the component
     */
    private Quad quad;
    /**
     * if not FREE corners are the same in both x and y direction
     */
    private CornerProportion cornerProportion;
    /**
     * if not FREE edges are the same in both x and y direction
     */
    private EdgeProportion edgeProportion;
    /**
     * way the mask for the children is created
     */
    private MaskMode maskMode;
    /**
     * size of the rounded corners relative to component size (0 disables rounded corners)
     */
    private ElementSizeConstraint cornerSize;
    /**
     * edges of the component
     */
    private Edge outerEdge, middleEdge, innerEdge;
    /**
     * enables edges
     */
    private boolean useEdges;
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
        cornerProportion = CornerProportion.FREE;
        edgeProportion = EdgeProportion.FREE;
        maskMode = MaskMode.USE_TRANSPARENT;
        cornerSize = new RelativeToComponentSizeE(0);
        colors = new ColorScheme();
        useColorShade = false;
        useEdges = false;
        outerEdge = new Edge();
        middleEdge = new Edge();
        innerEdge = new Edge();


    }

    @Override
    public void setDepthValue(float depthValue) {
        quad.setPosition(quad.getPosition().x,quad.getPosition().y,depthValue);
    }

    @Override
    public void renderSimpleStencil(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager) {
        Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(quad, orthographic);
        ShaderProgram shader = hud.getShaderManager().getMaskShader();

        shader.setUniform("projModelMatrix", projModelMatrix);
        shader.setUniform("useTransparent", maskMode == MaskMode.USE_TRANSPARENT?1:0);

        drawMesh();
    }

    @Override
    public void addToShaderList(HudShaderManager hudShaderManager) {
        if (quad.getMesh().getMaterial().getTexture() == null) {
            if (cornerSize.getAbsoluteValue() == 0 && !useColorShade && !useEdges) {
                hudShaderManager.addToSimpleQuadShaderList(this);

            } else if (cornerSize.getAbsoluteValue() == 0 && !useEdges) {
                hudShaderManager.addToColorShadeQuadShaderList(this);
            }
        }
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
     * Method called before the rendering parameters are set up for this component
     * to prepare the shader
     *
     * @param renderMode rendering mode
     */


    public void prepareShader(RenderMode renderMode, Matrix4f ortho, Transformation transformation) {
        Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(quad, ortho);


        if (quad.getMesh().getMaterial().getTexture() == null) {
            if (cornerSize.getAbsoluteValue() == 0 && !useColorShade && !useEdges) {
                ShaderProgram shader = hud.getShaderManager().getSimpleQuadShader();
                if (renderMode == RenderMode.NORMAL) {

                    hud.getShaderManager().setShaderProgram(shader);

                    shader.setUniform("projModelMatrix", projModelMatrix);
                    shader.setUniform("depth", getId());
                    shader.setUniform("inColor", colors.getVectorArray()[0]);
                } else {

                    hud.getShaderManager().setShaderProgram(shader);

                    shader.setUniform("projModelMatrix", projModelMatrix);
                    shader.setUniform("depth", getId());
                    shader.setUniform("inColor", new Vector4f(1, 1, 1, 1));
                }


            } else if (cornerSize.getAbsoluteValue() == 0 && !useEdges) {

                ShaderProgram shader = hud.getShaderManager().getColorShadeQuadShader();

                if (renderMode == RenderMode.NORMAL) {

                    hud.getShaderManager().setShaderProgram(shader);

                    shader.setUniform("projModelMatrix", projModelMatrix);
                    shader.setUniform("depth", getId());
                    shader.setUniform("colors",colors.getVectorArray());
                } else {

                    shader = hud.getShaderManager().getSimpleQuadShader();
                    hud.getShaderManager().setShaderProgram(shader);

                    shader.setUniform("projModelMatrix", projModelMatrix);
                    shader.setUniform("depth", getId());
                    shader.setUniform("inColor",Color.RED.getColor());


                }




            } else {

                /*

                hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
                hudShaderProgram.setUniform("depth", getId());
                hudShaderProgram.setUniform("isText", 0);
                if (renderMode == Component.RenderMode.NORMAL) {
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
                    if (cornerSize == 0) {
                        switch (edgeProportion) {
                            case FREE:
                                hudShaderProgram.setUniform("keepEdgeProportion", 0);
                                break;
                            case KEEP_Y:
                                hudShaderProgram.setUniform("keepEdgeProportion", (super.getOnScreenWidth() / super.getOnScreenHeight()) * ((float) super.getWindow().getWidth() / super.getWindow().getHeight()));
                                break;
                            case KEEP_X:
                                hudShaderProgram.setUniform("keepEdgeProportion", -(super.getOnScreenWidth() / super.getOnScreenHeight()) * ((float) super.getWindow().getWidth() / super.getWindow().getHeight()));
                        }
                    }
                    hudShaderProgram.setUniform("outerEdge", outerEdge);
                    hudShaderProgram.setUniform("middleEdge", middleEdge);
                    hudShaderProgram.setUniform("innerEdge", innerEdge);

                } else {
                    hudShaderProgram.setUniform("colors", new Vector4f[]{new Vector4f(1, 1, 1, 1)});
                    hudShaderProgram.setUniform("useColorShade", 0);
                    hudShaderProgram.setUniform("hasTexture", 0);
                }

                 */
            }
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

    @SuppressWarnings("unused")
    public void setCornerProportion(CornerProportion cornerProportion) {
        this.cornerProportion = cornerProportion;
    }

    public Quad getQuad() {
        return quad;
    }

    public void setQuad(Quad quad) {
        this.quad = quad;
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
