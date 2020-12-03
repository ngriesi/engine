package engine.hud.mouse;

import engine.hud.components.ContentComponent;
import engine.hud.components.SubComponent;

import java.util.ArrayList;
import java.util.List;

import static engine.hud.mouse.MouseEvent.Event.*;

/**
 * mouse listener class that creates and handles mouse events depending on the
 * position of the mouse and the state if its buttons
 *
 * every component has one MouseListener
 */
@SuppressWarnings("unused")
public class MouseListener {

    /**
     * enum of all supported mouse buttons
     */
    public enum MouseButton {
        LEFT,RIGHT,MIDDLE,BUTTON_4,BUTTON_5,BUTTON_6,BUTTON_7,BUTTON_8
    }

    /**
     * if this is true a drag event can only be started if the mouse has been pressed for
     * the time specified in pressedTime in update frames
     */
    private boolean startDragOnlyOnPressed;

    /**
     * time a button has to be pressed till a press started event gets called
     * (in update frames)
     */
    private int pressedTime = 30;

    /**
     * component this MouseListener belongs to
     */
    private final ContentComponent component;

    /**
     * list of all mouse actions this listener contains
     */
    private final List<MouseAction> mouseActions;

    /**
     * state of the mouse form the perspective of the component of this MouseListener
     * stores the timers for the buttons and booleans to check if the mouse left the
     * component
     */
    private final MouseState mouseState;

    /**
     * constructor setting the component of this MouseListener and creating the final attributes
     *
     * @param component this MouseListener belongs to
     */
    public MouseListener(ContentComponent component) {
        mouseState = new MouseState();
        mouseActions = new ArrayList<>();
        this.component = component;
    }

    /**
     * called if the mouse enters the component or one of its parents
     *
     * Method is used to differ the mouse entering the component of this MouseListener
     * or one of its child components
     */
    public void mouseEnteredRecursiveSave() {
        mouseState.setStillInside(true);
        if(component instanceof SubComponent) {
            ((SubComponent)component).getParent().getMouseListener().mouseEnteredRecursiveSave();
        }
    }

    /**
     * called if the mouse exits the component or one of its parents
     *
     * Method is used to differ the mouse leaving the component of this MouseListener
     * or one of its child components
     */
    public void mouseExitedRecursiveSave() {
        mouseState.setStillInside(false);
        if(component instanceof SubComponent) {
            ((SubComponent)component).getParent().getMouseListener().mouseExitedRecursiveSave();
        }
    }

    /**
     * method gets executed if the mouse exits the component or one of its children
     *
     * @param mouseInput tracks mouse actions
     */
    public void mouseExited(MouseInput mouseInput) {

        if(!mouseState.isStillInside()) {

            if (mouseState.isMouseInside()) {
                mouseState.setMouseInside(false);

                if(!mouseActionNoPassing(mouseInput,EXITED)) {
                    if(component instanceof SubComponent) {
                        ((SubComponent)component).getParent().getMouseListener().mouseExited(mouseInput);
                    }
                }

                for(MouseButton button : MouseButton.values()) {
                    mouseState.setPressedSinceEntered(button,false);
                    mouseState.setPressedOnLastFrame(button,false);
                }

            }
        }
    }

    /**
     * method gets executed every time the mouse points on a different component, that is this component or one
     * of its children, than in the frame before
     *
     * @param mouseInput tracks mouse actions
     */
    public void mouseEntered(MouseInput mouseInput) {


        if(!mouseState.isMouseInside()) {
            mouseState.setMouseInside(true);


            for(MouseButton button : MouseButton.values()) {
                mouseState.setPressedSinceEntered(button,mouseInput.isPressed(button));
            }


            if(!mouseActionNoPassing(mouseInput,ENTERED)) {
                if(component instanceof SubComponent) {
                    ((SubComponent)component).getParent().getMouseListener().mouseEntered(mouseInput);
                }
            }
        }
    }

    /**
     * mouse action called by the scene component of this component
     *
     * method gets called every frame the mouse is inside this component
     *
     * @param mouseInput tracks mouse events
     */
    public void mouseActionStart(MouseInput mouseInput) {

        for(MouseButton mouseButton : MouseButton.values()) {
            mouseState.setState(mouseInput,mouseButton);

            // checks for a click start
            if(!mouseState.isPressedSinceEntered(mouseButton) && !mouseState.wasPressedOnLastFrame(mouseButton) && mouseState.isPressed(mouseButton)) {
                mouseAction(mouseInput,mouseButton,CLICK_STARTED);
            }

            // checks for a press start
            if(mouseState.getButtonTimer(mouseButton) == pressedTime) {
                mouseAction(mouseInput,mouseButton,PRESS_STARTED);
            }

            // handles releases of mouse buttons
            if(mouseState.wasPressedOnLastFrame(mouseButton) && !mouseState.isPressed(mouseButton)) {
                if(mouseState.isPressedSinceEntered(mouseButton)) {
                    mouseAction(mouseInput,mouseButton,DRAG_RELEASED);
                    component.getHud().dropDragEvent(mouseButton);
                } else {
                    if(component.getHud().getDragEvent(mouseButton) != null) {
                        mouseAction(mouseInput,mouseButton,DRAG_RELEASED);
                        component.getHud().dropDragEvent(mouseButton);
                    } else {
                        if(mouseState.getButtonTimer(mouseButton) >= pressedTime) {
                            mouseAction(mouseInput,mouseButton,PRESS_RELEASED,CLICK_RELEASED);
                        } else {
                            mouseAction(mouseInput,mouseButton,CLICK_RELEASED);
                        }
                    }
                }
            }

            // checks for a started drag event
            if(!mouseState.isPressedSinceEntered(mouseButton) && mouseState.isPressed(mouseButton)) {
                if(mouseState.isMouseMoved() && component.getHud().getRightDragEvent()==null) {
                    if(startDragOnlyOnPressed) {
                        if(mouseState.getButtonTimer(mouseButton) >= pressedTime) {
                            mouseAction(mouseInput,mouseButton,DRAG_STARTED);
                        }
                    } else {
                        mouseAction(mouseInput,mouseButton,DRAG_STARTED);
                    }
                }
            }

            // default action (always called)
            mouseAction(mouseInput,mouseButton,ACTION);

            // resets some state values for the next frame
            mouseState.resetFrameAction(mouseButton);
        }


    }

    /**
     * executes only the actions of this MouseListener with no specific button and with no
     * possibility to call the parents mouse actions
     *
     * @param mouseInput MouseInput class reference
     * @param event kind of the occurred event
     * @return true if the event got consumed and should not be passed to its parent
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean mouseActionNoPassing(MouseInput mouseInput, MouseEvent.Event event) {
        return executeActions(mouseInput,event,null);
    }

    /**
     * performs the mouse actions of this listener and recursively calls itself in the MouseListener of
     * the parent of the component of this MouseLister
     *
     * @param mouseInput reference to the MouseInput class
     * @param mouseButton mouse button that performed the event
     * @param events events this call contains
     */
    private void mouseAction(MouseInput mouseInput,MouseButton mouseButton, MouseEvent.Event... events) {
        for(MouseEvent.Event event : events) {
            if(executeActions(mouseInput,event,mouseButton)) {
                return;
            }
        }
        if(component instanceof SubComponent) {
            ((SubComponent)component).getParent().getMouseListener().mouseAction(mouseInput,mouseButton, events);
        }
    }

    /**
     * executes all mouse actions and returns if the event is consumed
     *
     * @param mouseInput class reference to MouseInput
     * @param event mouse event of this call
     * @param mouseButton button that caused the event
     * @return true if the mouse event got consumed
     */
    private boolean executeActions(MouseInput mouseInput, MouseEvent.Event event, MouseButton mouseButton) {
        MouseEvent mouseEvent = new MouseEvent(mouseInput, event, component, mouseButton);
        boolean consumed = false;
        for(MouseAction action : mouseActions) {
            consumed = action.action(mouseEvent);
        }
        return consumed;
    }

    /**
     * adds an action to the mouse actions list ot be preformed when a mouse event occurs
     *
     * @param action action to be added
     */
    public void addMouseAction(MouseAction action) {
        mouseActions.add(action);
    }

    /**
     * removes an action from the mouse actions list
     *
     * @param action actions to be removed
     */
    public void removeMouseAction(MouseAction action) {
        mouseActions.remove(action);
    }

    /**
     * adds an action to the mouse actions list that only gets called when the event comes form the left mouse button
     *
     * @param action action to be added to the actions list
     */
    public void addLeftButtonAction(MouseAction action) {
        mouseActions.add(new MouseAdapter(action,MouseButton.LEFT));
    }

    /**
     * adds an action to the mouse actions list that only gets called when the event comes form the right mouse button
     *
     * @param action action to be added to the actions list
     */
    public void addRightButtonAction(MouseAction action) {
        mouseActions.add(new MouseAdapter(action,MouseButton.RIGHT));
    }

    int getPressedTime() {
        return pressedTime;
    }

    public MouseState getMouseState() {
        return mouseState;
    }

    public void setPressedTime(int pressedTime) {
        this.pressedTime = pressedTime;
    }
}

