package engine.hud.components.presets;

import engine.general.MouseInput;
import engine.hud.actions.Action;
import engine.hud.color.Color;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;

public class PopupWindow extends QuadComponent {

    protected QuadComponent window;

    private Action onCloseAction;

    public PopupWindow() {
        this.setWidthConstraint(new RelativeToParentSize(1));
        this.setHeightConstraint(new RelativeToParentSize(1));
        this.setxPositionConstraint(new RelativeInParent(0.5f));
        this.setyPositionConstraint(new RelativeInParent(0.5f));

        this.setColors(new Color(0,0,0,0.5f));

        window = new QuadComponent();
        window.setWidthConstraint(new RelativeToParentSize(0.5f));
        window.setHeightConstraint(new RelativeToParentSize(0.5f));
        window.setxPositionConstraint(new RelativeInParent(0.5f));
        window.setyPositionConstraint(new RelativeInParent(0.5f));
        window.setOnLeftClickAction(() -> true);

        this.addComponent(window);

    }

    @Override
    public void leftClickAction(MouseInput mouseInput) {
        super.leftClickAction(mouseInput);
        hide();
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
