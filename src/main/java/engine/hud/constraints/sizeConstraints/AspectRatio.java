package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

/**
 * size constraint for a component to set its width relative to its height or vice versa
 *
 * does not keep the aspect ratio when the window gets resized for this
 * @see AbsoluteAspectRatio
 */
public class AspectRatio extends SizeConstraint {

    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public AspectRatio(float value) {
        super(value);
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
        if(direction == Direction.HEIGHT) {
            // prevent a loop when both size constraints are aspect ratios
            if(component.getWidth().isAspectRatio()) {
                return 1;
            } else {
                return component.getWidth().getValue(component,Direction.WIDTH) * value;
            }
        } else {
            // prevent a loop when both size constraints are aspect ratios
            if(component.getHeight().isAspectRatio()) {
                return 1;
            } else {
                return component.getHeight().getValue(component,Direction.HEIGHT) * value;
            }
        }
    }
}
