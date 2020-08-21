package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

public class InvertedRelativeToWindow extends SizeConstraint {

    /**
     * constraints result gets inverted
     */
    private SizeConstraint toInvert;
    /**
     * constructor sets initial value of the constraint
     *
     */
    public InvertedRelativeToWindow(SizeConstraint toInvert) {
        super(1);
        this.toInvert = toInvert;
    }

    @Override
    public float getValue(SubComponent component, Direction direction) {
        return 1 - toInvert.getValue(component,direction);
    }
}
