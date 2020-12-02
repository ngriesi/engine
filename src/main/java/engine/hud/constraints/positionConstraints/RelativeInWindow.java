package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;
import engine.hud.constraints.sizeConstraints.SizeConstraint;


/**
 * constraint to set the position of a component. It defines where the component should
 * be relative inside its window. 0 means the component starts where its window starts
 * and 1 means the component ends where its window ends
 */
@SuppressWarnings("unused")
public class RelativeInWindow extends PositionConstraint {
    /**
     * sets initial value of the constraint
     *
     * @param value initial value
     */
    public RelativeInWindow(float value) {
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
            return component.getWidth().getValue(component, SizeConstraint.Direction.WIDTH)/2 + (1 - component.getWidth().getValue(component, SizeConstraint.Direction.WIDTH)) * value;
        } else {
            return component.getHeight().getValue(component, SizeConstraint.Direction.HEIGHT)/2 + (1 - component.getHeight().getValue(component, SizeConstraint.Direction.HEIGHT)) * value;
        }
    }
}
