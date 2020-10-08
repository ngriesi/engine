package engine.hud.mouse;

public class MouseState {

    private boolean[] pressedSinceEntered;

    private boolean[] pressedOnLastFrame;

    private boolean[] pressed;

    private int[] buttonTimer;

    private boolean mouseMoved;

    private boolean stillInside;

    private boolean mouseInside;

    public MouseState() {
        pressedOnLastFrame = new boolean[8];
        pressed = new boolean[8];
        pressedSinceEntered = new boolean[8];
        buttonTimer = new int[8];
    }

    public void setState(MouseInput mouseInput, MouseListener.MouseButton button) {
        pressed[button.ordinal()] = mouseInput.isPressed(button);
        buttonTimer[button.ordinal()] = (pressedOnLastFrame[button.ordinal()] && !pressedSinceEntered[button.ordinal()])?buttonTimer[button.ordinal()]+1:0;
        mouseMoved = checkForDrag(mouseInput);

    }

    public void resetFrameAction(MouseInput mouseInput, MouseListener.MouseButton button) {
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

        return mouseInput.getDisplVec().x != 0 || mouseInput.getDisplVec().y != 0;

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
