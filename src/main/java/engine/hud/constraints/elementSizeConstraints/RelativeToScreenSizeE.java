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
        // 0.1 * scS = cS * (scS/wS)
        if(component.getWindow() != null) {
            if (proportion == Proportion.KEEP_HEIGHT) {
                return (value * component.getWindow().getMonitorData().height()) / (float) component.getWindow().getHeight() * component.getOnScreenHeight();
            } else {
                return (value * component.getWindow().getMonitorData().width()) / (float) component.getWindow().getWidth() * component.getOnScreenWidth();
            }
        } else {
            return 1;
        }
    }
}
