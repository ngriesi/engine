package engine.hud.constraints.elementSizeConstraints;

import engine.hud.components.SubComponent;

public class RelativeToWindowSizeE extends ElementSizeConstraint {

    /** sets the value of the constraint */
    public RelativeToWindowSizeE(float value) {
        super(value);
    }

    /** used by the component to set its size */
    @Override
    public float getValue(SubComponent component, Proportion proportion) {
        if( proportion != Proportion.KEEP_HEIGHT) {
            return value/component.getOnScreenWidth();
        } else {
            return value/component.getOnScreenHeight();
        }
    }
}
