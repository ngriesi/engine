package engine.hud.constraints.sizeConstraints;

import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.TextComponent;
import engine.hud.text.TextItem;

public class TextAspectRatio extends SizeConstraint {

    /**
     * constructor sets initial value of the constraint
     *
     */
    public TextAspectRatio() {
        super(0);
    }

    @Override
    public float getValue(SubComponent component, Direction direction) {

        TextItem textItem = ((TextComponent)component).getTextItem();

        float ratio = textItem.getWidth()/ textItem.getLineHeight();

        float windowRatio = 1;

        if(component.getWindow() != null) {
            windowRatio = component.getWindow().getWidth()/(float)component.getWindow().getHeight();
        }

        if(direction == Direction.WIDTH) {
            float height;
            if(component.getHeight() instanceof TextAspectRatio || component.getHeight() instanceof AspectRatio) {
                height = component.getOnScreenHeight();
            } else {
               height = component.getHeight().getValue(component,Direction.HEIGHT);
            }

            return (height * ratio)/textItem.getLines() * (1/windowRatio);
        } else {
            float width;
            if(component.getWidth() instanceof TextAspectRatio || component.getWidth() instanceof AspectRatio) {
                width = component.getOnScreenWidth();
            } else {
                width = component.getWidth().getValue(component,Direction.WIDTH);
            }


            return (width)/ratio * textItem.getLines() * windowRatio;
        }
    }
}
