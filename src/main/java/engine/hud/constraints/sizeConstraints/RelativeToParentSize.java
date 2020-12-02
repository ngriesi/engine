package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

/**
 * constraint to set the size of a component relative to the size
 * of its parent
 */
public class RelativeToParentSize extends SizeConstraint {

    /**
     * sets value of the constraint
     *
     * @param value constraint value
     */
    public RelativeToParentSize(float value) {
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
        if( direction == Direction.WIDTH) {
            return component.getParent().getOnScreenWidth() * value;
        } else {
            return component.getParent().getOnScreenHeight() * value;
        }
    }
}
