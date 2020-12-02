package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

/**
 * size constraint for components which subtracts the result of the passed constraint
 * from the size of the window
 */
@SuppressWarnings("unused")
public class InvertedRelativeToWindow extends SizeConstraint {

    /**
     * constraints result gets inverted
     */
    private final SizeConstraint toInvert;
    /**
     * constructor sets initial value of the constraint
     *
     */
    public InvertedRelativeToWindow(SizeConstraint toInvert) {
        super(1);
        this.toInvert = toInvert;
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
        return 1 - toInvert.getValue(component,direction);
    }
}
