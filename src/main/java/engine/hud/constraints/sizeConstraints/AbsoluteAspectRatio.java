package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

@SuppressWarnings("unused")
public class AbsoluteAspectRatio extends SizeConstraint{
    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public AbsoluteAspectRatio(float value) {
        super(value);
    }

    @Override
    public float getValue(SubComponent component, Direction direction) {

        float aspectRatio = component.getWindow() != null?(float)component.getWindow().getWidth()/(float)component.getWindow().getHeight():1;

        if(direction == Direction.HEIGHT) {
            if(component.getWidth() instanceof AspectRatio ||component.getWidth() instanceof TextAspectRatio || component.getWidth() instanceof AbsoluteAspectRatio) {
                return 1;
            } else {
                return component.getWidth().getValue(component,Direction.WIDTH) * value * aspectRatio;
            }
        } else {
            if(component.getHeight() instanceof AspectRatio ||component.getHeight() instanceof TextAspectRatio || component.getHeight() instanceof AbsoluteAspectRatio) {
                return 1;
            } else {
                return component.getHeight().getValue(component,Direction.HEIGHT) * value * (1/aspectRatio);
            }
        }
    }
}
