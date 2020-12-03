package engine.hud.mouse;

/**
 * class used as a shortcut to implement specific behaviour of the component on
 * the actions of a specific mouse button
 */
public class MouseAdapter implements MouseAction {

    /**
     * action preformed when an event for the passed MouseButton occurs
     */
    private final MouseAction mouseAction;

    /**
     * button this MouseAdapter is set to. This MouseAdapter only calls its
     * mouseActon action when this button gets an event.
     */
    private final MouseListener.MouseButton button;

    /**
     * constructor creating a MouseAdapter
     *
     * @param mouseAction action of the adapter
     * @param button button the adapter is linked to
     */
    public MouseAdapter(MouseAction mouseAction, MouseListener.MouseButton button) {
        this.mouseAction = mouseAction;
        this.button = button;
    }

    /**
     * action performed when an mouse event occurs in the component this MouseAdapter belongs to
     *
     * @param e event containing information about the actions of the mouse
     * @return true if the mouse event should be consumed and not passed to the next component
     */
    @Override
    public boolean action(MouseEvent e) {
        if(e.getMouseButton()==button) {
            return mouseAction.action(e);
        }
        return false;
    }
}
