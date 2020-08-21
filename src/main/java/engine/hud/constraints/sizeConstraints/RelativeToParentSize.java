package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

public class RelativeToParentSize extends SizeConstraint {

    /**
     * sets value of the constraint
     * @param value constraint value
     */
    public RelativeToParentSize(float value) {
        super(value);
    }

    /**
     * calculates component size
     *
     * @param component component that uses this constraint
     * @param direction direction of this constraint
     * @return size of component
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
