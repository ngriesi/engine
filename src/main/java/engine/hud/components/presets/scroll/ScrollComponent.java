package engine.hud.components.presets.scroll;

import engine.hud.components.contentcomponents.QuadComponent;

/**
 * parent class provides methods to scroll view that need to be
 * accessed by the scroll bar
 */
public abstract class ScrollComponent extends QuadComponent {

    /**
     * calls the calculate values method when update bounds gets called
     */
    @Override
    public void updateBounds() {
        super.updateBounds();
        calculateValues();
    }

    /**
     * calculates new values for the size and position of the scroll bars and the content
     * of the scroll view
     */
    protected abstract void calculateValues();

    /**
     * sets the updated position values to the content components position attribute
     */
    protected abstract void updateContentPosition();
}
