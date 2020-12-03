package engine.hud.mouse;

import engine.general.Window;
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
    private final Vector2f displayVec;

    /** true if mouse is inside the window */
    private boolean inWindow = false;

    /**
     * stores for all eight possible mouse buttons if they are currently pressed
     */
    private final boolean[] buttonsPressed;

    /**stores the rotation of the mouse wheel */
    private float mouseWheelRotation = 0;

    /**stores horizontal scroll value */
    private float horizontalScroll = 0;

    /**
     * creates objects for all the vectors used
     */
    public MouseInput(){
        previousPos = new Vector2d(-1,-1);
        currentPos = new Vector2d(0,0);
        relativePos = new Vector2f();
        displayVec = new Vector2f();
        buttonsPressed = new boolean[8];

    }

    /**
     * initialises the listener by setting up all the needed callbacks
     *
     * @param window the mouse listener is used in
     */
    public void init(Window window){

        // gives the mouse position
        glfwSetCursorPosCallback(window.getWindowHandle(),((window1, xPos, yPos) -> {
            currentPos.x = xPos;
            currentPos.y = yPos;
        }));

        // tells if the mouse is inside the window
        glfwSetCursorEnterCallback(window.getWindowHandle(),((window1, entered) -> inWindow = entered));

        // sets for all the buttons the pressed values
        glfwSetMouseButtonCallback(window.getWindowHandle(),((window1, button, action, mods) -> {
            buttonsPressed[0] = button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            buttonsPressed[1] = button==GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
            buttonsPressed[2] = button==GLFW_MOUSE_BUTTON_3 && action == GLFW_PRESS;
            buttonsPressed[3] = button==GLFW_MOUSE_BUTTON_4 && action == GLFW_PRESS;
            buttonsPressed[4] = button==GLFW_MOUSE_BUTTON_5 && action == GLFW_PRESS;
            buttonsPressed[5] = button==GLFW_MOUSE_BUTTON_6 && action == GLFW_PRESS;
            buttonsPressed[6] = button==GLFW_MOUSE_BUTTON_7 && action == GLFW_PRESS;
            buttonsPressed[7] = button==GLFW_MOUSE_BUTTON_8 && action == GLFW_PRESS;
        }));

        // gives the scroll value of the current frame
        glfwSetScrollCallback(window.getWindowHandle(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                mouseWheelRotation = (float) -yOffset;
                horizontalScroll = (float) -xOffset;
            }
        });


    }

    /**
     * @return the display vector (movement of the mouse)
     */
    public Vector2f getDisplayVec(){return displayVec;}

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
        displayVec.x = 0;
        displayVec.y = 0;

        relativePos.x = (float) (currentPos.x/window.getWidth());
        relativePos.y = (float) (currentPos.y/window.getHeight());

        if(previousPos.x > 0 && previousPos.y > 0 && inWindow){

            double deltaX = currentPos.x - previousPos.x;
            double deltaY = currentPos.y - previousPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if (rotateX) {
                displayVec.y = (float)deltaX;
            }
            if(rotateY) {
                displayVec.x = (float)deltaY;
            }
        }

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;

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

    public float getXOffsetScroll() {
        float t = horizontalScroll;
        horizontalScroll = 0;
        return t;
    }

    /**
     * checks for a specific button if its pressed
     *
     * @param button that gets checked
     * @return true if the checked button is currently pressed
     */
    public boolean isPressed(MouseListener.MouseButton button) {
        return buttonsPressed[button.ordinal()];
    }


}
