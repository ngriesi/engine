package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;

public class EndAtRelativeToWindow extends PositionConstraint {
    /**
     * sets initial value of the constraint
     *
     * @param value initial value
     */
    @SuppressWarnings("unused")
    public EndAtRelativeToWindow(float value) {
        super(value);
    }

    /** calculates component position so it end at value */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(direction == Direction.X) {
            return value - component.getOnScreenWidth()/2;
        } else {
            return value - component.getOnScreenHeight()/2;
        }
    }
}
