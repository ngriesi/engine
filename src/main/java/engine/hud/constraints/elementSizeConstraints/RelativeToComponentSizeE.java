package engine.hud.constraints.elementSizeConstraints;

import engine.hud.components.SubComponent;

public class RelativeToComponentSizeE extends ElementSizeConstraint {
    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public RelativeToComponentSizeE(float value) {
        super(value);
    }

    @Override
    public float getValue(SubComponent component, Proportion proportion) {
        return value;
    }
}
