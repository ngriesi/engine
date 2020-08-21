package engine.hud.events;

import engine.hud.components.SubComponent;
import org.joml.Vector2f;

public interface DragEvent {

    /**
     * Method gets executed when the drag event is dropped
     */
    void dropAction();

    /**
     * Used to get the component that is drawn at the mouse position while the
     * drag event is active
     *
     * @return component to be drawn
     */
    SubComponent getDragVisual();

    void dragAction(Vector2f lastMousePosition);
}
