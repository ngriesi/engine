package engine.hud.constraints.elementSizeConstraints;

import engine.hud.components.SubComponent;


/**
 * constraint to set the size of an element of a component relative to the size
 * of the screen (makes it independent from window resizing)
 */
@SuppressWarnings("unused")
public class RelativeToScreenSizeE extends ElementSizeConstraint {

    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public RelativeToScreenSizeE(float value) {
        super(value);
    }

    /**
     * method called by the component to get the size of its element
     *
     * @param component component that uses this constraint
     * @param proportion declares which value (width or height) should be used as reference
     * @return value for this constraint (width or height of the component)
     */
    @Override
    public float getValue(SubComponent component, Proportion proportion) {
        if(component.getWindow() != null) {
            if (proportion == Proportion.KEEP_HEIGHT) {
                return (value * component.getWindow().getMonitorData().height()) / (float) component.getWindow().getHeight() * component.getOnScreenHeight();
            } else {
                return (value * component.getWindow().getMonitorData().width()) / (float) component.getWindow().getWidth() * component.getOnScreenWidth();
            }
        } else {
            return 1;
        }
    }
}
