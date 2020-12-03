package engine.hud.mouse;

import engine.hud.components.ContentComponent;

/**
 * data class stores the information about a mouse event that occures
 * e.g. a press or release of a mouse button
 */
public class MouseEvent {

    /**
     * enum containing all possible mouse events to identify them
     */
    public enum Event {
        CLICK_STARTED,
        CLICK_RELEASED,
        PRESS_STARTED,
        PRESS_RELEASED,
        DRAG_STARTED,
        DRAG_RELEASED,
        ENTERED,
        EXITED,
        ACTION
    }

    /**
     * mouse input class reference to access mouse position data
     */
    private final MouseInput mouseInput;

    /**
     * kind of the event that occurred
     */
    private final Event event;

    /**
     * component in which the event happened
     */
    private final ContentComponent component;

    /**
     * button that performed the event
     */
    private final MouseListener.MouseButton mouseButton;

    /**
     * constructor setting all attributes
     *
     * @param mouseInput MouseInput class reference of the window
     * @param event kind of the event that occurred
     * @param component component in which the event occurred
     * @param mouseButton MouseButton that caused the event
     */
    MouseEvent(MouseInput mouseInput, Event event, ContentComponent component, MouseListener.MouseButton mouseButton) {
        this.mouseInput = mouseInput;
        this.event = event;
        this.component = component;
        this.mouseButton = mouseButton;
    }

    public Event getEvent() {
        return event;
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public ContentComponent getComponent() {
        return component;
    }

    public MouseListener.MouseButton getMouseButton() {
        return mouseButton;
    }
}
