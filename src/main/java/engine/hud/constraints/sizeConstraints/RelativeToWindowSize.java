package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

public class RelativeToWindowSize extends SizeConstraint {

    /** sets the value of the constraint */
    public RelativeToWindowSize(float value) {
        super(value);
    }

    /** used by the component to set its size */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        return value;
    }
}
