package engine.hud.constraints.sizeConstraints;

import engine.graph.items.Texture;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.TextureComponent;

/**
 * constraint used to keep the aspect ratio of an image in a TextureComponent
 */
@SuppressWarnings("unused")
public class TextureAspectRatio extends SizeConstraint {

    /**
     * constructor sets initial value of the constraint
     */
    public TextureAspectRatio() {
        super(0);
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


        Texture texture;

        // check if the passed component is actually a TextureComponent
        if(component instanceof TextureComponent) {
            texture = ((TextureComponent) component).getQuad().getMesh().getMaterial().getTexture();
        } else {
            return 1;
        }

        // calculates Texture aspect ratio
        float textureAspectRatio = texture.getWidth()/(float) texture.getHeight();

        float windowRatio = 1;

        // calculates window aspect ratio
        if(component.getWindow() != null) {
            windowRatio = component.getWindow().getWidth()/(float)component.getWindow().getHeight();
        }

        if(direction == Direction.WIDTH) {
            float height;

            // prevent a loop when both size constraints are aspect ratios
            if(component.getHeight() instanceof TextAspectRatio || component.getHeight() instanceof AspectRatio) {
                height = component.getOnScreenHeight();
            } else {
                height = component.getHeight().getValue(component,Direction.HEIGHT);
            }

            return (height * textureAspectRatio) * (1/windowRatio);
        } else {
            float width;

            // prevent a loop when both size constraints are aspect ratios

            if(component.getWidth() instanceof TextAspectRatio || component.getWidth() instanceof AspectRatio) {
                width = component.getOnScreenWidth();
            } else {
                width = component.getWidth().getValue(component,Direction.WIDTH);
            }


            return (width)/textureAspectRatio * windowRatio;
        }
    }
}
