package engine.hud.mouse;

import engine.hud.components.ContentComponent;
import engine.hud.components.SubComponent;

import java.util.ArrayList;
import java.util.List;

import static engine.hud.mouse.MouseEvent.Event.*;

@SuppressWarnings("unused")
public class MouseListener {

    public enum MouseButton {
        LEFT,RIGHT,MIDDLE,BUTTON_4,BUTTON_5,BUTTON_6,BUTTON_7,BUTTON_8
    }

    private boolean startDragOnlyOnPressed;

    private int pressedTime = 30;

    private ContentComponent component;

    private List<MouseAction> mouseActions;

    private MouseState mouseState;


    private boolean executeActions(MouseInput mouseInput, MouseEvent.Event event, MouseButton mouseButton) {
        MouseEvent mouseEvent = new MouseEvent(mouseInput, event, component, mouseButton);
        boolean consumed = false;
        for(MouseAction action : mouseActions) {
            consumed = action.action(mouseEvent);
        }
        return consumed;
    }



    public MouseListener(ContentComponent component) {
        mouseState = new MouseState();
        mouseActions = new ArrayList<>();
        this.component = component;
    }

    /**
     * called if the mouse enters the component or one of its parents
     */

    public void mouseEnteredRecursiveSave() {
        mouseState.setStillInside(true);
        if(component instanceof SubComponent) {
            ((SubComponent)component).getParent().getMouseListener().mouseEnteredRecursiveSave();
        }
    }

    /**
     * called if the mouse exits the component or one of its parents
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

            if(!mouseState.isPressedSinceEntered(mouseButton) && !mouseState.wasPressedOnLastFrame(mouseButton) && mouseState.isPressed(mouseButton)) {
                mouseAction(mouseInput,mouseButton,CLICK_STARTED);
            }

            if(mouseState.getButtonTimer(mouseButton) == pressedTime) {
                mouseAction(mouseInput,mouseButton,PRESS_STARTED);
            }

            if(mouseState.wasPressedOnLastFrame(mouseButton) && !mouseState.isPressed(mouseButton)) {
                if(mouseState.isPressedSinceEntered(mouseButton)) {
                    mouseAction(mouseInput,mouseButton,DRAG_RELEASED);
                    component.getHud().dropRightDragEvent();
                } else {
                    if(component.getHud().getRightDragEvent() != null) {
                        mouseAction(mouseInput,mouseButton,DRAG_RELEASED);
                        component.getHud().dropRightDragEvent();
                    } else {
                        if(mouseState.getButtonTimer(mouseButton) >= pressedTime) {
                            mouseAction(mouseInput,mouseButton,PRESS_RELEASED,CLICK_RELEASED);
                        } else {
                            mouseAction(mouseInput,mouseButton,CLICK_RELEASED);
                        }
                    }
                }
            }

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

            mouseAction(mouseInput,mouseButton,ACTION);

            mouseState.resetFrameAction(mouseInput,mouseButton);
        }


    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean mouseActionNoPassing(MouseInput mouseInput, MouseEvent.Event event) {
        return executeActions(mouseInput,event,null);
    }

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

    public void addMouseAction(MouseAction action) {
        mouseActions.add(action);
    }

    public void removeMouseAction(MouseAction action) {
        mouseActions.remove(action);
    }

    public void addLeftButtonAction(MouseAction action) {
        mouseActions.add(new MouseAdapter(action,MouseButton.LEFT));
    }

    public void addRightButtonAction(MouseAction action) {
        mouseActions.add(new MouseAdapter(action,MouseButton.RIGHT));
    }

    int getPressedTime() {
        return pressedTime;
    }

    public MouseState getMouseState() {
        return mouseState;
    }
}

