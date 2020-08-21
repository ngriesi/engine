package engine.general;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    /** stores previous mouse position */
    private final Vector2d previousPos;

    /** stores current mouse position (pixel, origin at left top corner of the window)*/
    private final Vector2d currentPos;

    /** stores relative mouse position (from 0 to 1, origin at left top corner of the window*/
    private final Vector2f relativePos;

    /** movement Vector of the mouse (direction and speed) */
    private final Vector2f displVec;

    /** true if mouse is inside the window */
    private boolean inWindow = false;

    /** true while left mouse button is pressed */
    private boolean leftButtonPressed = false;

    /** true while right mouse button is pressed */
    private boolean rightButtonPressed = false;

    /**stores the rotation of the mouse wheel */
    private float mouseWheelRotation = 0;

    /**
     * creates objects for all the vectors used
     */
    public MouseInput(){
        previousPos = new Vector2d(-1,-1);
        currentPos = new Vector2d(0,0);
        relativePos = new Vector2f();
        displVec = new Vector2f();

    }

    /**
     * initialises the listener by setting up all the needed callbacks
     *
     * @param window the mouse listener is used in
     */
    void init(Window window){

        glfwSetCursorPosCallback(window.getWindowHandle(),((window1, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;

        }));

        glfwSetCursorEnterCallback(window.getWindowHandle(),((window1, entered) -> {
            inWindow = entered;
        }));

        glfwSetMouseButtonCallback(window.getWindowHandle(),((window1, button, action, mods) -> {
            leftButtonPressed = button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button==GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        }));

        glfwSetScrollCallback(window.getWindowHandle(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                mouseWheelRotation = (float) -yoffset;
            }
        });


    }

    /**
     * @return the display vector (movement of the mouse)
     */
    public Vector2f getDisplVec(){return displVec;}

    /**
     * @return current mouse position in pixels relative to window top left corner
     */
    public Vector2d getCurrentPos() {
        return currentPos;
    }

    /**
     * @return current mouse position (value between 0 and 1)
     */
    public Vector2f getRelativePos() {
        return relativePos;
    }

    /**
     * @return true if mouse is in the window
     */
    @SuppressWarnings("unused")
    public boolean isInWindow() {
        return inWindow;
    }

    /**
     * called every frame
     * updates the current mouse position and relative mouse position
     * calculates new display vector (mouse movement vector) with the
     * value of previousPos and updates it
     *
     * @param window used to get window bounds to determine relativePos
     */
    public void input(Window window){
        displVec.x = 0;
        displVec.y = 0;

        relativePos.x = (float) (currentPos.x/window.getWidth());
        relativePos.y = (float) (currentPos.y/window.getHeight());

        if(previousPos.x > 0 && previousPos.y > 0 && inWindow){

            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.y = (float)deltax;
            }
            if(rotateY) {
                displVec.x = (float)deltay;
            }
        }

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;

    }

    /**
     * @return returns true if the left mouse button is currently pressed
     */
    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    /**
     * @return returns true if the right mouse button is currently pressed
     */
    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    /**
     * returns how much the wheel has been scrolled since the last frame and resets the value
     *
     * @return distance scrolled
     */
    @SuppressWarnings("unused")
    public float getYOffsetScroll() {
        float t = mouseWheelRotation;
        mouseWheelRotation = 0;
        return t;
    }
}
