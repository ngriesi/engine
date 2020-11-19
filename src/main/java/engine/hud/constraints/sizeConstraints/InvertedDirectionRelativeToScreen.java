package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;

public class InvertedDirectionRelativeToScreen extends SizeConstraint {

    private SizeConstraint constraint;

    /**
     * constructor sets initial value of the constraint
     *
     * @param value constraint value
     */
    public InvertedDirectionRelativeToScreen(float value) {
        super(value);
        this.constraint = new RelativeToScreenSize(value);
    }

    @Override
    public float getValue(SubComponent component, Direction direction) {

        float value = constraint.getValue(component, direction);


        float screenAr = component.getWindow().getMonitorData().width()/(float)component.getWindow().getMonitorData().height();

        if(direction == Direction.HEIGHT) {
            value = value  * screenAr;
        } else {
            value = value/screenAr;
        }
        return value;
    }
}
