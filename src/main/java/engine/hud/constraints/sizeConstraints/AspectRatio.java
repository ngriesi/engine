package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

public class AspectRatio extends SizeConstraint {
    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public AspectRatio(float value) {
        super(value);
    }

    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(direction == Direction.HEIGHT) {
            if(component.getWidth() instanceof AspectRatio ||component.getWidth() instanceof TextAspectRatio) {
                return 1;
            } else {
                return component.getWidth().getValue(component,Direction.WIDTH) * value;
            }
        } else {
            if(component.getHeight() instanceof AspectRatio ||component.getHeight() instanceof TextAspectRatio) {
                return 1;
            } else {
                return component.getHeight().getValue(component,Direction.HEIGHT) * value;
            }
        }
    }
}
