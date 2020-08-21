package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

@SuppressWarnings("unused")
public class PixelValue extends SizeConstraint {
    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public PixelValue(int value) {
        super(value);
    }

    @Override
    public float getValue(SubComponent component, Direction direction) {
        if(direction== Direction.HEIGHT) {
            return value/(float)component.getWindow().getHeight();
        } else {
            return value/(float)component.getWindow().getWidth();
        }
    }
}
