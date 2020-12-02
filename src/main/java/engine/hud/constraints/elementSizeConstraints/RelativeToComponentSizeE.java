package engine.hud.constraints.elementSizeConstraints;

import engine.hud.components.SubComponent;

/**
 * constraint to set the size of an element of a component relative to the size
 * of the component the element belongs to
 */
public class RelativeToComponentSizeE extends ElementSizeConstraint {
    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public RelativeToComponentSizeE(float value) {
        super(value);
    }

    /**
     * method called by the component to get the size of its element
     *
     * @param component component that uses this constraint
     * @param proportion declares which value (width or height) should be used as reference
     * @return value for this constraint (width or height of the component)
     */
    @Override
    public float getValue(SubComponent component, Proportion proportion) {
        return value;
    }
}
