package engine.hud.components.presets;

import engine.hud.color.Color;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.contentcomponents.TextInputComponent;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.positionConstraints.RelativeToParentPosition;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.TextAspectRatio;
import engine.hud.mouse.MouseAction;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseListener;
import engine.hud.text.FontTexture;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class TextInputBox extends QuadComponent {

    private TextInputComponent textInputComponent;

    public TextInputBox(FontTexture fontTexture) {
        textInputComponent = new TextInputComponent(fontTexture);
        textInputComponent.setWidthConstraint(new TextAspectRatio());
        textInputComponent.setHeightConstraint(new RelativeToParentSize(0.7f));
        textInputComponent.setyPositionConstraint(new RelativeToParentPosition(0.5f));
        textInputComponent.setxPositionConstraint(new RelativeInParent(0));
        textInputComponent.setOnCursorChanged(this::cursorChangedAction);
        textInputComponent.setSelectable(false);
        this.addComponent(textInputComponent);
        this.setCornerSize(0.2f);
        this.setCornerProportion(CornerProportion.KEEP_Y);

        setColors(Color.RED);

        getMouseListener().addMouseAction(new MouseAction() {
            @Override
            public boolean action(MouseEvent e) {
                if(e.getEvent() == MouseEvent.Event.CLICK_RELEASED && e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                    hud.setCurrentKeyInputTarget(textInputComponent);
                    hud.needsNextRendering();
                    float x = e.getMouseInput().getRelativePos().x - (textInputComponent.getOnScreenXPosition() + textInputComponent.getxOffset() + textInputComponent.getAutoXOffset() - textInputComponent.getOnScreenWidth()/2);
                    float y = e.getMouseInput().getRelativePos().y - (textInputComponent.getOnScreenYPosition() + textInputComponent.getyOffset() + textInputComponent.getAutoYOffset() - textInputComponent.getOnScreenHeight()/2);

                    Vector2i newCursorPos = textInputComponent.getTextItem().getCursorPosition(x/textInputComponent.getOnScreenWidth(),y/textInputComponent.getOnScreenHeight());

                    textInputComponent.setCursor(newCursorPos);

                    updateBounds();
                    return false;
                }
                return false;
            }
        });
    }

    /**
     * links the cursor change with the box of the
     * text input box
     */
    private void cursorChangedAction() {
        Vector2f onScreenCursorPosition = textInputComponent.getOnScreenCursorPosition();




        if(textInputComponent.getOnScreenWidth() > 0) {
            textInputComponent.setAutoXOffset(0);

            if (onScreenCursorPosition.x < (this.getOnScreenXPosition() + this.getOnScreenWidth() * (-0.5f + getxOffset()))) {

                textInputComponent.setAutoXOffset(((this.getOnScreenXPosition() + this.getOnScreenWidth() * (-0.5f + getxOffset())) - onScreenCursorPosition.x));

            } else if (onScreenCursorPosition.x > (this.getOnScreenXPosition() + this.getOnScreenWidth() * (0.5f + getxOffset()))) {

                textInputComponent.setAutoXOffset(-(onScreenCursorPosition.x - (this.getOnScreenXPosition() + this.getOnScreenWidth() * (0.5f + getxOffset()))));

            }
        }


        if(textInputComponent.getOnScreenHeight() > 0) {
            textInputComponent.setAutoYOffset(0);
            float baseValue = getOnScreenHeight()/textInputComponent.getTextItem().getLines() * (textInputComponent.getTextItem().getFontTexture().getHeight()/textInputComponent.getTextItem().getLineHeight());
            float cursorHeight = baseValue * 0.7f;
            if (onScreenCursorPosition.y <= (this.getOnScreenYPosition() + this.getOnScreenHeight() * (-0.5f + getyOffset()) + (cursorHeight * textInputComponent.getOnScreenHeight()))) {

                textInputComponent.setAutoYOffset(((this.getOnScreenYPosition() + this.getOnScreenHeight() * (-0.5f + getyOffset())) - onScreenCursorPosition.y)+ (cursorHeight * textInputComponent.getOnScreenHeight()));

            } else if (onScreenCursorPosition.y > (this.getOnScreenYPosition() + this.getOnScreenHeight() * (0.5f + getyOffset()) - (cursorHeight * textInputComponent.getOnScreenHeight()))) {

                textInputComponent.setAutoYOffset(-(onScreenCursorPosition.y - (this.getOnScreenYPosition() + this.getOnScreenHeight() * (0.5f + getyOffset()) - (cursorHeight * textInputComponent.getOnScreenHeight()))));

            }

        }


    }


    public TextInputComponent getTextInputComponent() {
        return textInputComponent;
    }
}
