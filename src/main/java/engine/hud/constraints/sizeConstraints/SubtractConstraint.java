package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

public class SubtractConstraint extends SizeConstraint {

    private SizeConstraint constraint1,constraint2;

    /**
     * constructor sets initial value of the constraint
     *
     * @param constraint1 minuend
     * @param constraint2 subtrahend
     */
    public SubtractConstraint(SizeConstraint constraint1,SizeConstraint constraint2) {
        super(0);
        this.constraint1 = constraint1;
        this.constraint2 = constraint2;
    }

    @Override
    public float getValue(SubComponent component, Direction direction) {
        return constraint1.getValue(component,direction) - constraint2.getValue(component, direction);
    }
}
