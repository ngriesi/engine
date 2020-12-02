package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

/**
 * size constraint for a component to keep its aspect ratio even when the
 * window gets resized
 */
@SuppressWarnings("unused")
public class AbsoluteAspectRatio extends SizeConstraint{

    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public AbsoluteAspectRatio(float value) {
        super(value,true);
    }

    /**
     * method called by the component to get its size
     *
     * @param component component that uses this constraint
     * @param direction declares which size (width or height) is set with this constraint
     * @return value for this constraint (width  or height of the component)
     */
    @Override
    public float getValue(SubComponent component, Direction direction) {

        // calculate window aspect ratio
        float aspectRatio = component.getWindow() != null?(float)component.getWindow().getWidth()/(float)component.getWindow().getHeight():1;

        if(direction == Direction.HEIGHT) {
            // prevent a loop when both size constraints are aspect ratios
            if(component.getWidth().isAspectRatio()) {
                return 1;
            } else {
                return component.getWidth().getValue(component,Direction.WIDTH) * value * aspectRatio;
            }
        } else {
            // prevent a loop when both size constraints are aspect ratios
            if(component.getHeight().isAspectRatio()) {
                return 1;
            } else {
                return component.getHeight().getValue(component,Direction.HEIGHT) * value * (1/aspectRatio);
            }
        }
    }
}
