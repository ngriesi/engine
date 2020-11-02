package engine.hud.constraints.elementSizeConstraints;

import engine.hud.components.SubComponent;

@SuppressWarnings("unused")
public class RelativeToScreenSizeE extends ElementSizeConstraint {
    /**
     * constructor sets initial value of the constraint
     *
     * @param value initial value
     */
    public RelativeToScreenSizeE(float value) {
        super(value);
    }

    @Override
    public float getValue(SubComponent component, Proportion proportion) {
        if(component.getWindow() != null) {
            if (proportion != Proportion.KEEP_HEIGHT) {
                return (value * component.getWindow().getMonitorData().height()) / (float) component.getWindow().getHeight();
            } else {
                return (value * component.getWindow().getMonitorData().width()) / (float) component.getWindow().getWidth();
            }
        } else {
            return 1;
        }
    }
}
