package engine.hud.components.presets;

import engine.hud.actions.Action;
import engine.hud.color.Color;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseListener;

/**
 * class used to display a window in a sub component on top of
 * all the other components
 */
@SuppressWarnings("unused")
public class PopupWindow extends QuadComponent {

    /**
     * this component is the window in the middle of the popup menu
     */
    protected QuadComponent window;

    /**
     * action that if it exists is performed when the popup window gets closed
     */
    private Action onCloseAction;

    /**
     * default constructor creates a white rectangle as popup window with a greyed out
     * background0
     */
    public PopupWindow() {

        // sets the bounds of the background
        this.setWidthConstraint(new RelativeToParentSize(1));
        this.setHeightConstraint(new RelativeToParentSize(1));
        this.setxPositionConstraint(new RelativeInParent(0.5f));
        this.setyPositionConstraint(new RelativeInParent(0.5f));

        // sets the color of the background
        this.setColors(new Color(0,0,0,0.5f));

        // makes the popup disappear when the background gets clicked
        this.getMouseListener().addMouseAction(e -> {
            if(e.getEvent() == MouseEvent.Event.CLICK_RELEASED && e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                hide();
                return true;
            }
            return false;
        });

        // creates the standard popup window
        window = new QuadComponent();
        window.setWidthConstraint(new RelativeToParentSize(0.5f));
        window.setHeightConstraint(new RelativeToParentSize(0.5f));
        window.setxPositionConstraint(new RelativeInParent(0.5f));
        window.setyPositionConstraint(new RelativeInParent(0.5f));

        // makes the window consume left click events so it does not get closed when it is clicked
        window.getMouseListener().addMouseAction(e -> e.getEvent() == MouseEvent.Event.CLICK_RELEASED && e.getMouseButton() == MouseListener.MouseButton.LEFT);

        this.addComponent(window);

    }

    /**
     * getter for the window component to create custom popups
     *
     * @return window component
     */
    public QuadComponent getPopupWindow() {
        return window;
    }

    /**
     * shows the popup by adding it to the passed SubComponent and requests
     * re-rendering of the hud
     *
     * @param subComponent the popup gets added to
     */
    public void show(SubComponent subComponent) {
        subComponent.addComponent(this);
        hud.needsNextRendering();
    }

    /**
     * removes the popup at the and of the this frame and calls the onCloseAction
     * if it exists
     */
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
