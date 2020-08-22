package engine.hud.components.presets;

import engine.hud.actions.ReturnAction;
import engine.hud.animations.DefaultAnimations;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.contentcomponents.TextComponent;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.TextAspectRatio;
import engine.hud.events.DragEvent;
import engine.hud.text.FontTexture;
import org.joml.Vector2f;

public class Button extends QuadComponent implements DragEvent {

    private TextComponent textComponent;

    private ReturnAction clickedAction;

    private boolean activated;

    private boolean autoDeactivate;

    private boolean enabled;

    private boolean pressed;

    private engine.hud.animations.Animation entered;

    private engine.hud.animations.Animation exited;

    public ColorScheme normalColor,enteredColor,clickedColor,pressedColor,textColor,textClickedColor,disabledColor,textDisabledColor;

    public Button(float width, float height, float x, float y, String text, FontTexture fontTexture) {

        super.setWidthConstraint(new RelativeToParentSize(width));
        super.setHeightConstraint(new RelativeToParentSize(height));
        super.setxPositionConstraint(new RelativeInParent(x));
        super.setyPositionConstraint(new RelativeInParent(y));

        normalColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_STANDARD);
        enteredColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_ENTERED);
        clickedColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_PRESSED);
        pressedColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_PRESSED);
        textColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_TEXT_STANDARD);
        textClickedColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_TEXT_PRESSED);
        disabledColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_DISABLED);
        textDisabledColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_TEXT_DISABLED);

        autoDeactivate = true;
        enabled = true;
        pressed = false;



        textComponent = new TextComponent(fontTexture);
        textComponent.setText(text);

        textComponent.setHeightConstraint(new RelativeToParentSize(textComponent.getTextItem().getLines()>1?0.8f:0.5f));
        textComponent.setWidthConstraint(new TextAspectRatio());
        textComponent.setxPositionConstraint(new RelativeInParent(0.5f));
        textComponent.setyPositionConstraint(new RelativeInParent(0.5f));

        textComponent.setColorScheme(textColor);

        setCornerSize(0.5f);

        setCornerProportion(CornerProportion.FREE);

        setEdgeProportion(EdgeProportion.KEEP_X);

        setEdgeColor(Color.BLUE);

        super.addComponent(textComponent);

        super.setUseColorShade(true);

        super.setColorScheme(new ColorScheme(Color.BLUE));

        super.setUseColorShade(true);

        activated = true;


        entered = DefaultAnimations.buttonEnteredAnimation.createForComponent(this);
        exited = DefaultAnimations.buttonExitedAnimation.createForComponent(this);

        super.setMouseEnteredAction(mouseInput1 -> enteredAction(mouseInput1.isLeftButtonPressed()));

        super.setMouseExitedAction(mouseInput -> {

            if(enabled) {
                if (pressed) {
                    this.setColorScheme(pressedColor);
                    textComponent.setColorScheme(textColor);
                }
                entered.endAnimation();
                exited.startAnimation();
            }
            return true;
        });

        super.setOnLeftDragStarted(() -> {
            hud.setLeftDragEvent(Button.this);
            return true;
        });

        super.setOnLeftClickStarted(() -> {

            if(activated && enabled) {
                entered.endAnimation();
                this.setColorScheme(clickedColor);
                textComponent.setColorScheme(textClickedColor);
                pressed = true;
                hud.needsNextRendering();
            }
            return true;
        });

        super.setLeftDragEnterRelease(dragEvent -> {
            if(dragEvent != null) {
                if (dragEvent.equals(Button.this)) {
                    clickedAction();
                    return true;
                } else {
                    enteredAction(false);
                    return false;
                }
            } else {
                enteredAction(false);
                return false;
            }
        });

        super.setOnLeftClickAction(this::clickedAction);


    }

    private boolean clickedAction() {

        if(activated && enabled) {
            entered.endAnimation();
            this.setColorScheme(normalColor);
            textComponent.setColorScheme(textColor);
            pressed = false;
            if(clickedAction != null) {
                clickedAction.execute();
            }
            if(autoDeactivate)deactivate();

        }
        return true;
    }

    private boolean enteredAction(boolean leftPressed) {
        if(hud.getLeftDragEvent() == null) {
            if (!leftPressed) {
                exited.endAnimation();
                if (enabled) {
                    entered.startAnimation();
                }
                return true;
            } else {
                return false;
            }
        } else if (hud.getLeftDragEvent().equals(this)) {
            this.setColorScheme(clickedColor);
            textComponent.setColorScheme(textClickedColor);
            return true;
        } else {
            return false;
        }


    }


    @Override
    public void setOnLeftClickAction(ReturnAction onLeftClickAction) {
        clickedAction = onLeftClickAction;
    }

    public void setText(String text) {
        textComponent.setText(text);
    }

    public void deactivate() {activated = false;}

    public void activate() {activated = true;}

    public void enable() {
        textComponent.setColorScheme(textColor);
        this.setColorScheme(normalColor);
        enabled = true;

    }

    public void disable() {
        textComponent.setColorScheme(textDisabledColor);
        this.setColorScheme(disabledColor);
        enabled = false;
    }

    public void setAutoDeactivate(boolean autoDeactivate) {
        this.autoDeactivate = autoDeactivate;
    }

    @Override
    public void dropAction() {}

    @Override
    public SubComponent getDragVisual() { return null;}

    @Override
    public void dragAction(Vector2f lastMousePosition) { }
}
