package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;
import engine.hud.constraints.sizeConstraints.SizeConstraint;

@SuppressWarnings("unused")
public class RelativeInParent extends PositionConstraint {
    /**
     * sets initial value of the constraint
     *
     * @param value initial value
     */
    @SuppressWarnings("unused")
    public RelativeInParent(float value) {
        super(value);
    }

    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(direction == Direction.X) {
            return component.getParent().getOnScreenXPosition() - component.getParent().getOnScreenWidth()*(0.5f - component.getParent().getXOffset()) + component.getWidth().getValue(component, SizeConstraint.Direction.WIDTH)/2
                    + (component.getParent().getOnScreenWidth() - component.getWidth().getValue(component, SizeConstraint.Direction.WIDTH)) * value;
        } else {
            return component.getParent().getOnScreenYPosition() - component.getParent().getOnScreenHeight()*(0.5f - component.getParent().getYOffset()) + component.getHeight().getValue(component, SizeConstraint.Direction.HEIGHT)/2
                    + (component.getParent().getOnScreenHeight() - component.getHeight().getValue(component, SizeConstraint.Direction.HEIGHT)) * value;
        }
    }
}
