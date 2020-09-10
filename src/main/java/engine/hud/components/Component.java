package engine.hud.components;

import engine.general.Window;
import engine.hud.Hud;

public class Component {

    /** maximum number of components the depth buffer (24 bit) can distinguish */
    public static final int MAX_IDS = 16777215;

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

    /** used to temporarily hide a component during its removal */
    private boolean removed;

    /**
     * sets default values
     */
    public Component() {
        useMask = true;
        beMask = true;
        visible = true;
        removed = false;
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
