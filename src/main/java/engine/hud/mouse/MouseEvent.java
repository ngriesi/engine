package engine.hud.mouse;

import engine.hud.components.ContentComponent;

public class MouseEvent {

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

    private MouseInput mouseInput;

    private Event event;

    private ContentComponent component;

    private MouseListener.MouseButton mouseButton;

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
