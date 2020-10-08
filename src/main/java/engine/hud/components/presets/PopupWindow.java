package engine.hud.components.presets;

import engine.hud.mouse.MouseAction;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseInput;
import engine.hud.actions.Action;
import engine.hud.color.Color;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.mouse.MouseListener;

public class PopupWindow extends QuadComponent {

    protected QuadComponent window;

    private Action onCloseAction;

    public PopupWindow() {
        this.setWidthConstraint(new RelativeToParentSize(1));
        this.setHeightConstraint(new RelativeToParentSize(1));
        this.setxPositionConstraint(new RelativeInParent(0.5f));
        this.setyPositionConstraint(new RelativeInParent(0.5f));

        this.setColors(new Color(0,0,0,0.5f));

        this.getMouseListener().addMouseAction(new MouseAction() {
            @Override
            public boolean action(MouseEvent e) {
                if(e.getEvent() == MouseEvent.Event.CLICK_RELEASED && e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                    hide();
                    return true;
                }
                return false;
            }
        });

        window = new QuadComponent();
        window.setWidthConstraint(new RelativeToParentSize(0.5f));
        window.setHeightConstraint(new RelativeToParentSize(0.5f));
        window.setxPositionConstraint(new RelativeInParent(0.5f));
        window.setyPositionConstraint(new RelativeInParent(0.5f));

        window.getMouseListener().addMouseAction(e -> {
            if(e.getEvent()== MouseEvent.Event.CLICK_RELEASED && e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                return true;
            }
            return false;
        });

        this.addComponent(window);

    }

    public QuadComponent getPopupWindow() {
        return window;
    }

    public void show(SubComponent subComponent) {
        subComponent.addComponent(this);
        hud.needsNextRendering();
    }

    public void hide() {
        if(getParent() != null) {
            hud.addEndOFrameAction(() -> {
                hud.needsNextRendering();
                getParent().saveRemoveComponent(this);
                if(onCloseAction != null) {
                    onCloseAction.execute();
                }
            });
        }

    }

    public void setOnCloseAction(Action onCloseAction) {
        this.onCloseAction = onCloseAction;
    }
}
