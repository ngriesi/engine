package engine.hud.events;

import engine.hud.components.SubComponent;
import org.joml.Vector2f;

/**
 * interface implemented by objects (normally components) that can be dragged and perform actions while
 * they are
 */
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

    /**
     * action called every frame this drag event is active
     *
     * @param lastMousePosition last position of the mouse
     */
    void dragAction(Vector2f lastMousePosition);
}
