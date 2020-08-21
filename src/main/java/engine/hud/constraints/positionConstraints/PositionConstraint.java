package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;

public abstract class PositionConstraint {

    /**
     * passed by the component to determine direction of the constraint
     */
    public enum Direction {
        X,Y
    }

    /** value of the constraint */
    protected float value;

    /**
     * sets initial value of the constraint
     *
     * @param value initial value
     */
    PositionConstraint(float value) {
        this.value = value;
    }

    /**
     * method called by the component to get the position of itself
     *
     * @param component component that uses this constraint
     * @param direction direction of this constraint
     * @return value for this constraint (x or y - position of the component)
     */
    public abstract float getValue(SubComponent component, Direction direction);

    /**
     * changes the value of the constraint, can be used for animations
     *
     * @param value new value
     */
    public void changeValue(float value) {
        this.value = value;
    }

    /**
     * returns the absolute value of the constraint
     * @return value
     */
    @SuppressWarnings("unused")
    public float getAbsoluteValue() {
        return value;
    }
}
