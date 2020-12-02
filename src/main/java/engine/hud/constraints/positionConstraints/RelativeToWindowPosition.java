package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;

/**
 * constraint to set the position of a component. It defines where the component should
 * be relative to its window.
 */
public class RelativeToWindowPosition extends PositionConstraint {

    /**
     * sets the position value
     *
     * @param value position value
     */
    public RelativeToWindowPosition(float value) {
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
        return value;
    }
}
