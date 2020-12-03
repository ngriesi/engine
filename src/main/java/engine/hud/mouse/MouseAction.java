package engine.hud.mouse;

/**
 * interface to pass actions from components to the mouse listener
 */
public interface MouseAction {

    /**
     * method called by the mouse listener, overwritten at the implementation of this action
     *
     * @param e event containing information about the actions of the mouse
     * @return returns true if the mouse event should be consumed and not passed on to the parent of the
     * component
     */
    boolean action(MouseEvent e);

}
