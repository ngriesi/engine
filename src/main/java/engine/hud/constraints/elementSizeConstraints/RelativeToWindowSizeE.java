package engine.hud.constraints.elementSizeConstraints;

import engine.hud.components.SubComponent;

public class RelativeToWindowSizeE extends ElementSizeConstraint {

    /** sets the value of the constraint */
    public RelativeToWindowSizeE(float value) {
        super(value);
    }

    /** used by the component to set its size */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        if( direction == Direction.WIDTH) {
            return value/component.getOnScreenWidth();
        } else {
            return value/component.getOnScreenHeight();
        }
    }
}
