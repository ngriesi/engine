package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

/**
 * constraint to set the size of a component relative to the size
 * of the screen
 */
@SuppressWarnings("unused")
public class RelativeToScreenSize extends SizeConstraint {

    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public RelativeToScreenSize(float value) {
        super(value);
    }

    /**
     * method called by the component to get its size
     *
     * @param component component that uses this constraint
     * @param direction declares which size (width or height) is set with this constraint
     * @return value for this constraint (width  or height of the component)
     */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(component.getWindow() != null) {
            if (direction == Direction.HEIGHT) {
                return (value * component.getWindow().getMonitorData().height()) / (float) component.getWindow().getHeight();
            } else {
                return (value * component.getWindow().getMonitorData().width()) / (float) component.getWindow().getWidth();
            }
        } else {
            return 1;
        }
    }
}
