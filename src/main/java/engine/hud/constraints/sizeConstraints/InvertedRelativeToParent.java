package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

public class InvertedRelativeToParent extends SizeConstraint {

    /**
     * constraints result gets inverted
     */
    private SizeConstraint toInvert;
    /**
     * constructor sets initial value of the constraint
     *
     */
    public InvertedRelativeToParent(SizeConstraint toInvert) {
        super(1);
        this.toInvert = toInvert;
    }

    @Override
    public float getValue(SubComponent component, SizeConstraint.Direction direction) {
        if(direction==Direction.WIDTH) {
            return component.getParent().getOnScreenWidth() * (1-toInvert.getValue(component,direction));
        } else {
            return component.getParent().getOnScreenHeight() * (1-toInvert.getValue(component,direction));
        }
    }
}
