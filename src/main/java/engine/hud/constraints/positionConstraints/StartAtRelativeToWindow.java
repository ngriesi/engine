package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;

@SuppressWarnings("unused")
public class StartAtRelativeToWindow extends PositionConstraint {
    /**
     * sets initial value of the constraint
     *
     * @param value initial value
     */
    public StartAtRelativeToWindow(float value) {
        super(value);
    }

    /** calculates the position in order for the component to start at value */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(direction == Direction.X) {
            return value + component.getOnScreenWidth()/2;
        } else {
            return value + component.getOnScreenHeight()/2;
        }
    }
}
