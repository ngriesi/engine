package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

/**
 * constraint that subtracts two constraints form each other
 */
public class SubtractConstraint extends SizeConstraint {

    /**
     * constraints that get subtracted form one another
     */
    private final SizeConstraint constraint1,constraint2;

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

    /**
     * method called by the component to get its size
     *
     * @param component component that uses this constraint
     * @param direction declares which size (width or height) is set with this constraint
     * @return value for this constraint (width  or height of the component)
     */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        return constraint1.getValue(component,direction) - constraint2.getValue(component, direction);
    }
}
