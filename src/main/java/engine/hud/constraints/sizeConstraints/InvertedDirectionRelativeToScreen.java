package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

/**
 * size constraint for components which sets the height relative to the screens width
 * or the  width of the component relative to the windows height
 */
public class InvertedDirectionRelativeToScreen extends SizeConstraint {

    private final SizeConstraint constraint;

    /**
     * constructor sets initial value of the constraint
     *
     * @param value constraint value
     */
    public InvertedDirectionRelativeToScreen(float value) {
        super(value);
        this.constraint = new RelativeToScreenSize(value);
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

        // gets normal relative to screen constraint value
        float value = constraint.getValue(component, direction);

        // calculates screen aspect ratio
        float screenAr = component.getWindow().getMonitorData().width()/(float)component.getWindow().getMonitorData().height();

        // inverts the direction of the constraint
        if(direction == Direction.HEIGHT) {
            value = value  * screenAr;
        } else {
            value = value/screenAr;
        }
        return value;
    }
}
