package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;

/**
 * constraint to set the position of a component. It defines where the component should
 * end relative to the window it is inside
 */
@SuppressWarnings("unused")
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

    /**
     * method called by the component to get its position
     *
     * @param component component that uses this constraint
     * @param direction declares which position (x or y) is set with this constraint
     * @return value for this constraint (x or y position of the component)
     */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(direction == Direction.X) {
            return value - component.getOnScreenWidth()/2;
        } else {
            return value - component.getOnScreenHeight()/2;
        }
    }
}
