package engine.hud.constraints.positionConstraints;

import engine.hud.components.SubComponent;

public class RelativeToWindowPosition extends PositionConstraint {

    /** sets the position value
     * @param value position value
     * */
    public RelativeToWindowPosition(float value) {
        super(value);
    }

    /** used by the component to determine its position */
    @Override
    public float getValue(SubComponent component, Direction direction) {
        return value;
    }
}
