package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;

import static engine.hud.constraints.positionConstraints.PositionConstraint.Direction.X;

/**
 * constraint to set the position of a component. It defines where the component should
 * be relative to its parent.
 */
public class RelativeToParentPosition extends PositionConstraint {

    /**
     * @param value value of the constraint (0 means center of component at start of its parent)
     */
    public RelativeToParentPosition(float value) {
        super(value);
    }

    /**
     * calculates the position of the component depending on the parents position and offset
     *
     * @param component component that uses this constraint
     * @param direction direction of this constraint
     * @return position of component
     */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(direction==X) {
            return component.getParent().getOnScreenXPosition() + component.getParent().getOnScreenWidth() * (-0.5f + component.getXOffset() + value);
        } else {
            return component.getParent().getOnScreenYPosition() + component.getParent().getOnScreenHeight() * (-0.5f + component.getYOffset() + value);
        }
    }
}
