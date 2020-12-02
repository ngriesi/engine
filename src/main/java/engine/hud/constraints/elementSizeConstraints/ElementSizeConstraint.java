package engine.hud.constraints.elementSizeConstraints;

import engine.hud.components.SubComponent;

/**
 * constraint used to define the size of elements of a component for Example corner size or edge size of a
 * quad component
 */
public abstract class ElementSizeConstraint {

    /** value of the constraint */
    protected float value;

    /**
     * passed by the component to determine direction
     */
    public enum Proportion {
        KEEP_WIDTH,KEEP_HEIGHT,FREE
    }


    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public ElementSizeConstraint(float value) {
        this.value = value;
    }

    /**
     * method called by the component to get the size of its element
     *
     * @param component component that uses this constraint
     * @param proportion declares which value (width or height) should be used as reference
     * @return value for this constraint (width or height of the component)
     */
    public abstract float getValue(SubComponent component,Proportion proportion);

    /**
     * changes the value of the constraint, can be used for animations
     *
     * @param value new value
     */
    public void changeValue(float value) {
        this.value = value;
    }

    /**
     * returns the absolute value of the constraint
     * @return value
     */
    @SuppressWarnings("unused")
    public float getAbsoluteValue() {
        return value;
    }
}
