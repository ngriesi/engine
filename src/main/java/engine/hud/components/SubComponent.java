package engine.hud.components;

import engine.general.Transformation;
import engine.hud.HudShaderManager;
import engine.hud.constraints.positionConstraints.PositionConstraint;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.positionConstraints.RelativeToWindowPosition;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.RelativeToWindowSize;
import engine.hud.constraints.sizeConstraints.SizeConstraint;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_EQUAL;

@SuppressWarnings("WeakerAccess")
public abstract class SubComponent extends ContentComponent {

    /**
     * determines what effect transparent pixels have on the mask
     */
    public enum MaskMode {
        DISPOSE_TRANSPARENT, USE_TRANSPARENT, ONLY_EDGES
    }


    /**
     * defines the rendering mode used
     */
    protected enum RenderMode {
        NORMAL, ONE_COLOR
    }

    /**
     * Constraint to determine x Position
     */
    private PositionConstraint xPosition;

    /**
     * Constraint to determine y Position
     */
    private PositionConstraint yPosition;

    /**
     * Constraint to determine component width
     */
    private SizeConstraint width;

    /**
     * Constraint to determine component height
     */
    private SizeConstraint height;

    /**
     * Parent of that contains this component,set in add method
     */
    private ContentComponent parent;

    /**
     * Mask of this component
     */
    private ContentComponent maskComponent;

    /**
     * if true the component writes to the depth buffer
     */
    private boolean writeToDepthBuffer;

    /**
     * decides of transparent pixels should be a mask
     */
    private MaskMode maskMode;

    public SubComponent() {
        this(new RelativeToWindowPosition(0.5f), new RelativeToWindowPosition(0.5f), new RelativeToWindowSize(0.5f), new RelativeToWindowSize(0.5f));
    }

    public SubComponent(PositionConstraint xPosition, PositionConstraint yPosition, SizeConstraint width, SizeConstraint height) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        writeToDepthBuffer = true;
        maskMode = MaskMode.USE_TRANSPARENT;

    }

    /**
     * sets the depth value of the component
     *
     * @param depthValue new depth value (z cord)
     */
    public abstract void setDepthValue(float depthValue);

    /**
     * renders only the shape of the component to the stencil buffer
     */
    public abstract void setupShader(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager);

    /**
     * renders the components mesh
     */
    public abstract void drawMesh();

    /**
     * sets the buffers and calls the methods for the rendering
     *
     * @param orthographic   orthographic projection matrix
     * @param transformation transformation object
     * @param shaderManager  shaderManager of the hud
     */
    public void renderComponent(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager,int level) {

        if (isVisible()) {

            glColorMask(true, true,true , true);


            glDepthMask(true);
            glStencilMask(0xFF);
            glStencilFunc(GL_EQUAL, level, 0xFF);
            setDepthValue(getId());
            glDepthFunc(GL_ALWAYS);
            glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
            setupShader(orthographic, transformation, shaderManager);

            super.renderNext(orthographic, transformation, shaderManager,level + 1);


            glColorMask(false, false, false, false);

            glDepthMask(false);
            glStencilFunc(GL_ALWAYS, 0, 0xff);
            glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);

            setupShader(orthographic, transformation, shaderManager);

            /*
            if (maskComponent != null) {

                glDepthMask(writeToDepthBuffer);
                glStencilFunc(GL_EQUAL, 1, 0xff);
                setDepthValue(getId());
                glDepthFunc(GL_ALWAYS);
                glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);

                setupShader(orthographic, transformation, shaderManager);
            } else {
                glDepthFunc(GL_ALWAYS);
                glStencilFunc(GL_ALWAYS, 0, 0);
                glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
                glDepthMask(writeToDepthBuffer);
                setDepthValue(getId());


                setupShader(orthographic, transformation, shaderManager);

            }

             */

        }

    }

    /**
     * returns the content component which is the parent of this component
     *
     * @return parent content component
     */
    public ContentComponent getParent() {
        return parent;
    }

    /**
     * sets an new parent ContentComponent
     *
     * @param parent new parent
     */
    public void setParent(ContentComponent parent) {
        this.parent = parent;
    }

    /**
     * adds a sub component to the content list of this component
     * calls super method and addToScene method which updates the
     * scenes hash map and the components id
     *
     * @param component to add
     * @see ContentComponent
     */
    @Override
    public void addComponent(SubComponent component) {
        super.addComponent(component);
        addToScene(getSceneComponent());
        component.setMaskComponent(this);
    }

    /**
     * removes a component from this components content and removes
     * everything that is no longer visible from the scenes hash map
     * for mouse picking
     *
     * @param subComponent to be removed
     */
    @Override
    public void removeComponent(SubComponent subComponent) {
        super.removeComponent(subComponent);
        SceneComponent sc = getSceneComponent();
        if (sc != null) {
            subComponent.removeFromScene(sc);
            sc.cleanupIds();
        }
    }

    /**
     * used to recursively get the scene component of the components scene
     *
     * @return scene component or null
     */
    @Override
    public SceneComponent getSceneComponent() {
        if (parent != null) {
            return parent.getSceneComponent();
        } else {
            return null;
        }
    }


    /**
     * updates the components position and size and its children
     */
    public void updateBounds() {
        if (window != null) {
            onScreenHeight = height.getValue(this, SizeConstraint.Direction.HEIGHT);
            onScreenWidth = width.getValue(this, SizeConstraint.Direction.WIDTH);
            onScreenXPosition = xPosition.getValue(this, PositionConstraint.Direction.X);
            onScreenYPosition = yPosition.getValue(this, PositionConstraint.Direction.Y);
        }
        super.updateBounds();
    }

    /**
     * if a scene component can be found by the recursive call it is used to create a new id for this component,
     * to add this component to the scene components hash map and to do the same with the child components of this
     * component
     *
     * @param sceneComponent scene component if this component is already a child of one, otherwise null
     */
    void addToScene(SceneComponent sceneComponent) {
        if (sceneComponent != null) {
            setId(sceneComponent.getNextId());
            sceneComponent.addSubComponent(this);

            content.forEach(subComponent -> subComponent.addToScene(sceneComponent));
        }
    }

    void removeFromScene(SceneComponent sceneComponent) {
        sceneComponent.removeSubComponent(id);
        content.forEach(subComponent -> subComponent.removeFromScene(sceneComponent));
    }

    /**
     * used to change the value of the constraint
     * used for animations (movement/resizing)
     */

    @SuppressWarnings("unused")
    public void changeHeightValue(float value) {
        height.changeValue(value);
        updateBounds();
    }

    public void changeWidthValue(float value) {
        width.changeValue(value);
        updateBounds();
    }


    @SuppressWarnings("unused")
    public void changeXValue(float value) {
        xPosition.changeValue(value);
        updateBounds();
    }

    @SuppressWarnings("unused")
    public void changeYValue(float value) {
        yPosition.changeValue(value);
        updateBounds();
    }

    /**
     * setter for default constraints
     */

    public void setxPositionConstraint(float xPosition) {
        this.xPosition = new RelativeInParent(xPosition);
    }

    public void setyPositionConstraint(float yPosition) {
        this.yPosition = new RelativeInParent(yPosition);
    }

    public void setWidthConstraint(float width) {
        this.width = new RelativeToParentSize(width);
    }

    public void setHeightConstraint(float height) {
        this.height = new RelativeToParentSize(height);
    }

    /**
     * setter for constraints
     */

    public void setxPositionConstraint(PositionConstraint xPosition) {
        this.xPosition = xPosition;
    }

    public void setyPositionConstraint(PositionConstraint yPosition) {
        this.yPosition = yPosition;
    }

    public void setWidthConstraint(SizeConstraint width) {
        this.width = width;
    }

    public void setHeightConstraint(SizeConstraint height) {
        this.height = height;
    }


    /**
     * getter for bounds
     *
     * @return values
     */

    @SuppressWarnings("unused")
    public PositionConstraint getxPosition() {
        return xPosition;
    }

    @SuppressWarnings("unused")
    public PositionConstraint getyPosition() {
        return yPosition;
    }

    public SizeConstraint getWidth() {
        return width;
    }

    public SizeConstraint getHeight() {
        return height;
    }

    public void setMaskComponent(ContentComponent maskComponent) {
        this.maskComponent = maskComponent;
    }

    public MaskMode getMaskMode() {
        return maskMode;
    }

    public ContentComponent getMaskComponent() {
        return maskComponent;
    }

    public void setWriteToDepthBuffer(boolean writeToDepthBuffer) {
        this.writeToDepthBuffer = writeToDepthBuffer;
    }
}
