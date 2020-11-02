package engine.hud.constraints.elementSizeConstraints;

import engine.hud.components.SubComponent;

public class RelativeToParentSizeE extends ElementSizeConstraint {

    /**
     * sets value of the constraint
     * @param value constraint value
     */
    public RelativeToParentSizeE(float value) {
        super(value);
    }

    /**
     * calculates component size
     *
     * @param component component that uses this constraint
     * @return size of component
     */
    @Override
    public float getValue(SubComponent component, Proportion proportion) {
        if( proportion != Proportion.KEEP_HEIGHT) {
            return component.getParent().getOnScreenWidth() * value;
        } else {
            return component.getParent().getOnScreenHeight() * value;
        }
    }
}
