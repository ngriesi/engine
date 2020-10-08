package engine.hud.mouse;

public class MouseAdapter implements MouseAction {

    private MouseAction mouseAction;

    private MouseListener.MouseButton button;

    public MouseAdapter(MouseAction mouseAction, MouseListener.MouseButton button) {
        this.mouseAction = mouseAction;
        this.button = button;
    }

    @Override
    public boolean action(MouseEvent e) {
        if(e.getMouseButton()==button || e.getMouseButton()==null) {
            return mouseAction.action(e);
        }
        return false;
    }
}
