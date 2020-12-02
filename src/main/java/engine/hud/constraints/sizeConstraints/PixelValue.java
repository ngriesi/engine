package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

/**
 * constraint set the size of a component in pixels of the screen
 */
@SuppressWarnings("unused")
public class PixelValue extends SizeConstraint {

    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public PixelValue(int value) {
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
        if(direction== Direction.HEIGHT) {
            return value/(float)component.getWindow().getHeight();
        } else {
            return value/(float)component.getWindow().getWidth();
        }
    }
}
