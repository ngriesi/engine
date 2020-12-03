package engine.hud.mouse;

/**
 * class stores and updates the state value for the mouse and its buttons for a
 * specific MouseListener
 */
public class MouseState {

    /**
     * stores if a specific mouse button has been pressed since the mouse entered the
     * component its currently inside
     */
    private final boolean[] pressedSinceEntered;

    /**
     * stores if a specific mouse button was pressed on the last frame
     */
    private final boolean[] pressedOnLastFrame;

    /**
     * stores if a specific mouse button is currently pressed
     */
    private final boolean[] pressed;

    /**
     * stores the timers for all mouse buttons to determine if the action is a press or a click
     */
    private final int[] buttonTimer;

    /**
     * stores if the mouse moved since the last frame
     */
    private boolean mouseMoved;

    /**
     * stores if the mouse is inside the component this MouseState belongs to
     *
     * this is a temporally set value used and only to be used by the mouse entered and exited
     * methods in MouseListener
     *
     * @see MouseListener
     */
    private boolean stillInside;

    /**
     * stores whether the mouse is currently inside the component this MouseState belongs to
     */
    private boolean mouseInside;

    /**
     * constructor creates arrays
     */
    public MouseState() {
        pressedOnLastFrame = new boolean[8];
        pressed = new boolean[8];
        pressedSinceEntered = new boolean[8];
        buttonTimer = new int[8];
    }

    /**
     * sets the state of the buttons before the MouseListener checks for mouse events
     *
     * @param mouseInput reference to the MouseInput class
     * @param button of which the state currently gets changed
     */
    public void setState(MouseInput mouseInput, MouseListener.MouseButton button) {
        pressed[button.ordinal()] = mouseInput.isPressed(button);
        buttonTimer[button.ordinal()] = (pressedOnLastFrame[button.ordinal()] && !pressedSinceEntered[button.ordinal()])?buttonTimer[button.ordinal()]+1:0;
        mouseMoved = checkForDrag(mouseInput);

    }

    /**
     * sets the state for the next frame after the MouseListener has checked for mouse events
     *
     * @param button of which the state currently gets changed
     */
    public void resetFrameAction(MouseListener.MouseButton button) {
        pressedSinceEntered[button.ordinal()] = pressedSinceEntered[button.ordinal()] && pressed[button.ordinal()];
        pressedOnLastFrame[button.ordinal()] = pressed[button.ordinal()];
        pressed[button.ordinal()] = false;

    }

    /**
     * check if mouse has moved by comparing the current mouse position
     * with lastMousePosition which was updated the frame before
     *
     * @param mouseInput used to get current mouse position
     */
    private boolean checkForDrag(MouseInput mouseInput) {

        return mouseInput.getDisplayVec().x != 0 || mouseInput.getDisplayVec().y != 0;

    }

    public void setPressedSinceEntered(MouseListener.MouseButton button, boolean value) {
        pressedSinceEntered[button.ordinal()] = value;
    }

    public void setPressedOnLastFrame(MouseListener.MouseButton button, boolean value) {
        pressedOnLastFrame[button.ordinal()] = value;
    }

    public boolean isPressed(MouseListener.MouseButton button) {
        return pressed[button.ordinal()];
    }

    public boolean wasPressedOnLastFrame(MouseListener.MouseButton button) {
        return pressedOnLastFrame[button.ordinal()];
    }

    public boolean isPressedSinceEntered(MouseListener.MouseButton button) {
        return pressedSinceEntered[button.ordinal()];
    }

    public int getButtonTimer(MouseListener.MouseButton button) {
        return buttonTimer[button.ordinal()];
    }

    public boolean isStillInside() {
        return stillInside;
    }

    public void setStillInside(boolean stillInside) {
        this.stillInside = stillInside;
    }

    public boolean isMouseInside() {
        return mouseInside;
    }

    public void setMouseInside(boolean mouseInside) {
        this.mouseInside = mouseInside;
    }

    public boolean isMouseMoved() {
        return mouseMoved;
    }
}
