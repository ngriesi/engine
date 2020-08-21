package engine.hud.components;

import engine.general.Transformation;
import engine.general.Window;
import engine.hud.Hud;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static engine.hud.components.Component.RenderMode.NORMAL;
import static engine.hud.components.Component.RenderMode.ONE_COLOR;
import static org.lwjgl.opengl.GL11.*;

public class Component {

    /** maximum number of components the depth buffer (24 bit) can distinguish */
    public static final int MAX_IDS = 16777215;

    /**
     * defines the rendering mode used
     */
    protected enum RenderMode {
        NORMAL,ONE_COLOR
    }

    /** stores components x Position of Component on screen (except for offset, the xOffset is applied after the calculation of onScreenXPosition) */
    float onScreenXPosition;

    /** stores components y Position of Component on screen (except for offset, the yOffset is applied after the calculation of onScreenYPosition) */
    float onScreenYPosition;

    /** stores the on Screen Height of the Component */
    float onScreenHeight;

    /** stores the on screen Width of the component */
    float onScreenWidth;

    /** offset on x Axis used to move the component; offset 1 means the component was moved its own width to the left */
    private float xOffset;

    /** offset on y Axis used to move the component; offset 1 means the component was moved its own height down */
    private float yOffset;

    /** window that contains the hud component */
    protected Window window;

    /** hud that contains the component */
    protected Hud hud;

    /** determines if the component uses its parent as mask */
    private boolean useMask;

    /** determines if the component writes to the stencil buffer and with it functions as a mask for its children if they have useMask == true */
    private boolean beMask;

    /** if false the component and its children are not drawn */
    private boolean visible;

    /** used to temporarily hid a component during its removal */
    private boolean removed;

    /**
     * determines if the rendered image of the component writes its id to the depth buffer,
     * if this is false the component does not react to the mouse an does not overwrite the
     * values for the mouse selection of the components underneath it
     */
    private boolean writeToDepthBuffer;

    /**
     * sets default values
     */
    public Component() {
        useMask = true;
        beMask = true;
        writeToDepthBuffer = true;
        visible = true;
        removed = false;
    }



    /**
     * recursive rendering of the hud, called by the content component class to render the next hud components
     *
     * @param ortho orthographic transformation
     * @param transformation transformation object
     * @param hudShaderProgram shader
     */
    public void renderNext(Matrix4f ortho, Transformation transformation, ShaderProgram hudShaderProgram, int level) {

    }

    /**
     * sets up the buffers for the rendering of one component
     *
     * @param ortho transformation matrix
     * @param transformation class
     * @param hudShaderProgram shader for hud components
     * @param level mask level (max 255)
     */
    public void renderComponent(Matrix4f ortho, Transformation transformation, ShaderProgram hudShaderProgram, int level) {


        if (beMask) {
            glStencilMask(0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);

            glDepthMask(writeToDepthBuffer);
            glDepthFunc(GL_ALWAYS);

            if (useMask) {
                glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
                glStencilFunc(GL_EQUAL, level, 0xFF);
            } else {
                glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
                glStencilFunc(GL_ALWAYS, level + 1, 0xFF);
            }

            drawMesh(ortho, transformation, hudShaderProgram, NORMAL);
            renderNext(ortho, transformation, hudShaderProgram, level + 1);

            glDepthMask(false);

            glColorMask(false, false, false, false);

            glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);
            glStencilFunc(GL_EQUAL, level + 1, 0xFF);

            drawMesh(ortho, transformation, hudShaderProgram, ONE_COLOR);

            glColorMask(true, true, true, true);
        } else {
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);

            glDepthMask(writeToDepthBuffer);
            glDepthFunc(GL_ALWAYS);

            if (useMask) {
                glStencilFunc(GL_EQUAL, level, 0xFF);
            } else {
                glStencilFunc(GL_ALWAYS, level, 0xFF);
            }
            drawMesh(ortho, transformation, hudShaderProgram, NORMAL);
            renderNext(ortho, transformation, hudShaderProgram, level);
        }
    }

    /**
     * called by components with meshes, contains the rendering calls for these meshes
     *
     * @param ortho transformation matrix
     * @param transformation class
     * @param hudShaderProgram shader used for hud rendering
     * @param renderMode rendering mode (either drawing for visuals (color buffer) ore for mouse picker(depth buffer)
     */
    public void drawMesh(Matrix4f ortho, Transformation transformation, ShaderProgram hudShaderProgram, RenderMode renderMode) {}

    /**
     * returns an int color value for the pixel color at the window coordinates x and y
     *
     * @param x x coordinate starting right of the window
     * @param y y coordinate starting bottom of the window
     * @return color
     */
    static int getPixelColor(int x, int y) {
        ByteBuffer rgb = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());
        glReadPixels(x, y, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT,rgb);
        float t = rgb.getFloat(0);
        return (int) (t * MAX_IDS);
    }

    /**
     * sets the window of the component
     * always called by the parent when the parents add method is called
     * main component gets it in its constructor
     *
     * @param window that contains the hud
     */
    public void setWindow(Window window) {
        this.window = window;
    }

    /**
     * sets the hud of the component
     * always called by the parent when the parents add method is called
     * main component gets it in its constructor
     *
     * @param hud that contains the component
     */
    public void setHud(Hud hud) {
        this.hud = hud;
    }

    /**
     * @return window of the hud
     */
    public Window getWindow() {
        return window;
    }

    public Hud getHud() {
        return hud;
    }

    public float getOnScreenXPosition() {
        return onScreenXPosition;
    }

    public float getOnScreenYPosition() {
        return onScreenYPosition;
    }

    public float getOnScreenHeight() {
        return onScreenHeight;
    }

    public float getOnScreenWidth() {
        return onScreenWidth;
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    protected void setOnScreenXPosition(float onScreenXPosition) {
        this.onScreenXPosition = onScreenXPosition;
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    protected void setOnScreenYPosition(float onScreenYPosition) {
        this.onScreenYPosition = onScreenYPosition;
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    protected void setOnScreenHeight(float onScreenHeight) {
        this.onScreenHeight = onScreenHeight;
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    protected void setOnScreenWidth(float onScreenWidth) {
        this.onScreenWidth = onScreenWidth;
    }

    @SuppressWarnings("unused")
    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    @SuppressWarnings("unused")
    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setUseMask(boolean useMask) {
        this.useMask = useMask;
    }

    public void setBeMask(boolean beMask) {
        this.beMask = beMask;
    }

    @SuppressWarnings("unused")
    public boolean isMask() {
        return beMask;
    }

    public boolean useMask() {
        return useMask;
    }

    public void setWriteToDepthBuffer(boolean writeToDepthBuffer) {
        this.writeToDepthBuffer = writeToDepthBuffer;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}
