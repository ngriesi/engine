package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;
import engine.hud.constraints.sizeConstraints.SizeConstraint;

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

    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(direction == Direction.X) {
            return component.getWidth().getValue(component, SizeConstraint.Direction.WIDTH)/2 + (1 - component.getWidth().getValue(component, SizeConstraint.Direction.WIDTH)) * value;
        } else {
            return component.getHeight().getValue(component, SizeConstraint.Direction.HEIGHT)/2 + (1 - component.getHeight().getValue(component, SizeConstraint.Direction.HEIGHT)) * value;
        }
    }
}
