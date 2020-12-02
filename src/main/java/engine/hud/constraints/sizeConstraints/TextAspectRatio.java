package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.TextComponent;
import engine.hud.text.TextItem;

/**
 * constraint that sets the size of a TextComponent the way that the text proportion fits
 */
public class TextAspectRatio extends SizeConstraint {

    /**
     * constructor sets initial value of the constraint
     */
    public TextAspectRatio() {
        super(0,true);
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

        TextItem textItem;

        // checks if there is actually a text component passed
        if(component instanceof TextComponent) {
            textItem = ((TextComponent) component).getTextItem();
        } else {
            return 1;
        }

        // calculates the text aspect ratio
        float textAspectRatio = textItem.getWidth()/ textItem.getLineHeight();

        float windowRatio = 1;

        // calculates window aspect ratio
        if(component.getWindow() != null) {
            windowRatio = component.getWindow().getWidth()/(float)component.getWindow().getHeight();
        }

        if(direction == Direction.WIDTH) {
            float height;

            // prevent a loop when both size constraints are aspect ratios
            if(component.getHeight().isAspectRatio()) {
                height = component.getOnScreenHeight();
            } else {
               height = component.getHeight().getValue(component,Direction.HEIGHT);
            }

            return (height * textAspectRatio)/textItem.getLines() * (1/windowRatio);
        } else {
            float width;

            // prevent a loop when both size constraints are aspect ratios
            if(component.getWidth().isAspectRatio()) {
                width = component.getOnScreenWidth();
            } else {
                width = component.getWidth().getValue(component,Direction.WIDTH);
            }


            return (width)/textAspectRatio * textItem.getLines() * windowRatio;
        }
    }
}
