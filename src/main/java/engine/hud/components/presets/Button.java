package engine.hud.components.presets;

import engine.hud.actions.ReturnAction;
import engine.hud.animations.ColorSchemeAnimation;
import engine.hud.animations.DefaultAnimations;
import engine.hud.assets.Edge;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.contentcomponents.TextComponent;
import engine.hud.constraints.elementSizeConstraints.ElementSizeConstraint;
import engine.hud.constraints.elementSizeConstraints.RelativeToScreenSizeE;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.TextAspectRatio;
import engine.hud.events.DragEvent;
import engine.hud.mouse.MouseListener;
import engine.hud.text.FontTexture;
import org.joml.Vector2f;

/**
 * Button class extends QuadComponent and is used to create buttons with
 * a default behaviour
 */
@SuppressWarnings("unused")
public class Button extends QuadComponent implements DragEvent {

    /**
     * text component containing the text of the button
     */
    private final TextComponent textComponent;

    /**
     * action performed when button gets clicked
     */
    private ReturnAction clickedAction;

    /**
     * button only performs the clickedAction if activated is true
     */
    private boolean activated;

    /**
     * it true the button will deactivate after the first click
     */
    private boolean autoDeactivate;

    /**
     * if true the button is enabled, if false the buttons functions is disabled and
     * its visuals are changed
     */
    private boolean enabled;

    /**
     * if true the button is currently pressed
     */
    private boolean pressed;

    /**
     * animation, played when the mouse enters this button
     */
    private final ColorSchemeAnimation entered;

    /**
     * animation, played when the mouse exits this button
     */
    private final ColorSchemeAnimation exited;

    /**
     * color schemes used in for the different states of the button
     */
    public ColorScheme normalColor,enteredColor,clickedColor,pressedColor,textColor,textClickedColor,disabledColor,textDisabledColor;

    /**
     * quad component used to display the main background of the button
     */
    private final QuadComponent mainBack;

    /**
     * constructor creating a button with text
     *
     * @param width width of the button relative to its parent size
     * @param height height of the button relative to its parent size
     * @param x x position of the button relative to its parent position
     * @param y y position of the button relative to its parent position
     * @param text text of the button
     * @param fontTexture font texture used by the button text
     */
    public Button(float width, float height, float x, float y, String text, FontTexture fontTexture) {

        //sets button bounds
        super.setWidthConstraint(new RelativeToParentSize(width));
        super.setHeightConstraint(new RelativeToParentSize(height));
        super.setxPositionConstraint(new RelativeInParent(x));
        super.setyPositionConstraint(new RelativeInParent(y));

        //gets default color schemes
        normalColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_STANDARD);
        enteredColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_ENTERED);
        clickedColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_PRESSED);
        pressedColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_PRESSED);
        textColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_TEXT_STANDARD);
        textClickedColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_TEXT_PRESSED);
        disabledColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_DISABLED);
        textDisabledColor = ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_TEXT_DISABLED);

        //sets standard boolean values
        autoDeactivate = false;
        enabled = true;
        pressed = false;


        //creates the text component of the button
        textComponent = new TextComponent(fontTexture);
        textComponent.setText(text);

        textComponent.setHeightConstraint(new RelativeToParentSize(textComponent.getTextItem().getLines()>1?0.8f:0.5f));
        textComponent.setWidthConstraint(new TextAspectRatio());
        textComponent.setxPositionConstraint(new RelativeInParent(0.5f));
        textComponent.setyPositionConstraint(new RelativeInParent(0.5f));

        textComponent.setColorScheme(textColor);


        //mainBack,this and effect are used to create multiple edges
        setCornerSize(new RelativeToScreenSizeE(0.015f));

        setCornerProportion(ElementSizeConstraint.Proportion.KEEP_WIDTH);

        setEdgeProportion(ElementSizeConstraint.Proportion.KEEP_WIDTH);

        setEdge(new Edge((0.5f), Color.getTransparent(Color.GREY),Color.GREY, Edge.BlendMode.REPLACE));

        this.setColors(Color.TRANSPARENT);

        mainBack = new QuadComponent();
        mainBack.setWidthConstraint(1f);
        mainBack.setHeightConstraint(1f);
        mainBack.setxPositionConstraint(0.5f);
        mainBack.setyPositionConstraint(0.5f);
        mainBack.setWriteToDepthBuffer(true);
        mainBack.setMaskMode(MaskMode.DISPOSE_TRANSPARENT);
        mainBack.setMaskComponent(null);
        mainBack.setEdgeProportion(ElementSizeConstraint.Proportion.KEEP_WIDTH);
        mainBack.setEdge(new Edge((0.5f),Color.TRANSPARENT,Color.TRANSPARENT, Edge.BlendMode.REPLACE));
        mainBack.setCornerSize(new RelativeToScreenSizeE(0.015f));
        mainBack.setCornerProportion(ElementSizeConstraint.Proportion.KEEP_WIDTH);
        this.addComponent(mainBack);

        QuadComponent effect = new QuadComponent();
        effect.setWidthConstraint(1);
        effect.setHeightConstraint(1);
        effect.setxPositionConstraint(0.5f);
        effect.setyPositionConstraint(0.5f);
        effect.setColors(Color.TRANSPARENT);
        effect.setMaskMode(MaskMode.DISPOSE_TRANSPARENT);
        effect.setWriteToDepthBuffer(false);
        effect.setCornerSize(new RelativeToScreenSizeE(0.02f));
        effect.setCornerProportion(ElementSizeConstraint.Proportion.KEEP_WIDTH);
        effect.setEdge(new Edge(0.9f,Color.TEAL,Color.getTransparent(Color.TEAL), Edge.BlendMode.MULTIPLY));
        mainBack.addComponent(effect);


        mainBack.addComponent(textComponent);

        mainBack.setUseColorShade(true);

        mainBack.setColorScheme(normalColor);

        activated = true;

        //sets default animations
        entered = (ColorSchemeAnimation) DefaultAnimations.buttonEnteredAnimation.createForComponent(mainBack);
        exited = (ColorSchemeAnimation) DefaultAnimations.buttonExitedAnimation.createForComponent(mainBack);

        //sets the mouse actions
        getMouseListener().addMouseAction(e -> {
            switch (e.getEvent()) {
                case ENTERED:
                    return enteredAction(e.getMouseInput().isPressed(MouseListener.MouseButton.LEFT));
                case EXITED:
                    if(enabled) {
                        if (pressed) {
                            mainBack.setColorScheme(normalColor);
                            textComponent.setColorScheme(textColor);
                            hud.needsNextRendering();
                            pressed = false;
                            entered.endAnimation();
                        } else {
                            entered.endAnimation();
                            exited.startAnimation();
                        }

                    }
                    return true;

                case DRAG_STARTED:
                    if(e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                        hud.setLeftDragEvent(Button.this);
                        return true;
                    }

                case CLICK_STARTED:
                    if(e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                        if (activated && enabled) {
                            entered.endAnimation();
                            mainBack.setColorScheme(clickedColor);
                            textComponent.setColorScheme(textClickedColor);
                            pressed = true;
                            hud.needsNextRendering();
                        }
                        return true;
                    }

                case DRAG_RELEASED:
                    if(e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                        if (e.getComponent().getHud().getLeftDragEvent() != null) {
                            if (e.getComponent().getHud().getLeftDragEvent().equals(Button.this)) {
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
                    }

                case CLICK_RELEASED:
                    if(e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                        return clickedAction();
                    }


            }
            return false;
        });
    }

    /**
     * method is called when button gets clicked
     *
     * @return returns true
     */
    private boolean clickedAction() {

        if(activated && enabled) {
            entered.endAnimation();
            mainBack.setColorScheme(normalColor);
            hud.needsNextRendering();
            textComponent.setColorScheme(textColor);
            pressed = false;
            if(clickedAction != null) {
                clickedAction.execute();
            }
            if(autoDeactivate)deactivate();

        }
        return true;
    }

    /**
     * method is called when the mouse enters the button
     *
     * @param leftPressed it true the mouse was dragged into the button
     * @return whether the entered event was consumed or not
     */
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
            mainBack.setColorScheme(clickedColor);
            textComponent.setColorScheme(textClickedColor);
            pressed = true;
            return true;
        } else {
            return false;
        }


    }

    /**
     * enables the button and sets its respective color
     */
    public void enable() {
        textComponent.setColorScheme(textColor);
        mainBack.setColorScheme(normalColor);
        enabled = true;
    }

    /**
     * disables the component and sets its respective color
     */
    public void disable() {
        textComponent.setColorScheme(textDisabledColor);
        mainBack.setColorScheme(disabledColor);
        enabled = false;
    }


    public void setOnLeftClickAction(ReturnAction onLeftClickAction) {
        clickedAction = onLeftClickAction;
    }

    public void setText(String text) {
        textComponent.setText(text);
    }

    public void deactivate() {activated = false;}

    public void activate() {activated = true;}

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
