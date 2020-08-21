package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;

public class EndAtRelativeToParent extends PositionConstraint {
    /**
     * sets initial value of the constraint
     *
     * @param value initial value
     */
    @SuppressWarnings("unused")
    public EndAtRelativeToParent(float value) {
        super(value);
    }

    /** calculates the position of the component to start at value relative to its parent */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(direction == Direction.X) {
            return -component.getOnScreenWidth()/2 - component.getParent().getOnScreenWidth() * (0.5f - value) + component.getParent().getOnScreenXPosition();
        } else {
            return -component.getOnScreenHeight()/2 - component.getParent().getOnScreenHeight() * (0.5f - value) + component.getParent().getOnScreenYPosition();
        }
    }
}
