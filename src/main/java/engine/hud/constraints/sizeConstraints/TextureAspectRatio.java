package engine.hud.constraints.sizeConstraints;

import com.sun.prism.TextureMap;
import engine.graph.items.Texture;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.TextComponent;
import engine.hud.components.contentcomponents.TextureComponent;
import engine.hud.text.TextItem;

public class TextureAspectRatio extends SizeConstraint {

    /**
     * constructor sets initial value of the constraint
     *
     */
    public TextureAspectRatio() {
        super(0);
    }

    @Override
    public float getValue(SubComponent component, Direction direction) {


        Texture texture;

        if(component instanceof TextureComponent) {
            texture = ((TextureComponent) component).getQuad().getMesh().getMaterial().getTexture();
        } else {
            return 1;
        }

        float ratio = texture.getWidth()/(float) texture.getHeight();

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

            return (height * ratio) * (1/windowRatio);
        } else {
            float width;
            if(component.getWidth() instanceof TextAspectRatio || component.getWidth() instanceof AspectRatio) {
                width = component.getOnScreenWidth();
            } else {
                width = component.getWidth().getValue(component,Direction.WIDTH);
            }


            return (width)/ratio * windowRatio;
        }
    }
}
