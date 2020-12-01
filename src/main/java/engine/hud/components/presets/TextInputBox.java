package engine.hud.components.presets;

import engine.hud.assets.Edge;
import engine.hud.color.Color;
import engine.hud.components.contentcomponents.TextInputComponent;
import engine.hud.components.presets.scroll.ScrollView;
import engine.hud.constraints.elementSizeConstraints.ElementSizeConstraint;
import engine.hud.constraints.elementSizeConstraints.RelativeToParentSizeE;
import engine.hud.constraints.elementSizeConstraints.RelativeToScreenSizeE;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.positionConstraints.RelativeToParentPosition;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.TextAspectRatio;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseListener;
import engine.hud.text.FontTexture;
import engine.hud.text.TextItem;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * Component used to create a text input field to enter text
 * uses a Scroll view and a TextInputComponent
 */
@SuppressWarnings("unused")
public class TextInputBox extends ScrollView {

    /**
     * textInputComponent used for the text input
     */
    private final TextInputComponent textInputComponent;

    /**
     * number of  lines that are visible at once
     * (changes the size of the text input component)
     */
    private int visibleLines = 2;

    /**
     * constructor creating an empty text input box
     *
     * @param fontTexture used by the TextInputComponent
     */
    public TextInputBox(FontTexture fontTexture) {

        // formats the TextInputComponent
        textInputComponent = new TextInputComponent(fontTexture);
        textInputComponent.setWidthConstraint(new TextAspectRatio());
        textInputComponent.setHeightConstraint(new RelativeToParentSize(1f/visibleLines));
        textInputComponent.setYPositionConstraint(new RelativeToParentPosition(1f));
        textInputComponent.setXPositionConstraint(new RelativeInParent(0));
        textInputComponent.setOnCursorChanged(this::cursorChangedAction);
        textInputComponent.setFocusable(false);
        textInputComponent.setColors(Color.BLACK);
        textInputComponent.setTextAlignment(TextItem.TextAlignment.LEFT);

        // formats the ScrollView
        this.setContent(textInputComponent);
        this.setCornerSize(new RelativeToScreenSizeE(0.15f));
        this.setCornerProportion(ElementSizeConstraint.Proportion.KEEP_HEIGHT);

        this.setEdge(new Edge(new RelativeToParentSizeE(1),Color.TEAL,Color.getTransparent(Color.TEAL), Edge.BlendMode.MULTIPLY));


        setColors(Color.WHITE);

        // updates some values when the text of the TextInputComponent gets changed
        textInputComponent.setOnChangedAction(() -> {
            textInputComponent.setHeightConstraint(1f/visibleLines * textInputComponent.getTextItem().getLines());
            updateBounds();
            calculateValues();
            hud.needsNextRendering();
        });

        // defines the behavior of the ScrollView and the TextInputComponent when they get Left-Clicked (moves the cursor)
        getMouseListener().addMouseAction(e -> {

            if(e.getEvent() == MouseEvent.Event.CLICK_RELEASED && e.getMouseButton()== MouseListener.MouseButton.LEFT) {

                hud.setCurrentInputFocus(textInputComponent);
                hud.needsNextRendering();
                float x = e.getMouseInput().getRelativePos().x - (textInputComponent.getOnScreenXPosition() + textInputComponent.getXOffset()  - textInputComponent.getOnScreenWidth()/2);
                float y = e.getMouseInput().getRelativePos().y - (textInputComponent.getOnScreenYPosition() + textInputComponent.getYOffset()  - textInputComponent.getOnScreenHeight()/2);

                if(y < textInputComponent.getOnScreenHeight()) {
                    Vector2i newCursorPos = textInputComponent.getTextItem().getCursorPosition(x / textInputComponent.getOnScreenWidth(), y / textInputComponent.getOnScreenHeight());
                    textInputComponent.setCursor(newCursorPos);
                    updateBounds();
                }


                return false;

            }
            return false;
        });
    }

    public void setVisibleLines(int visibleLines) {
        this.visibleLines = visibleLines;
    }

    /**
     * links the cursor change with the box of the
     * text input box
     */
    private void cursorChangedAction() {



        Vector2f onScreenCursorPosition = textInputComponent.getOnScreenCursorPosition();

        Vector2f relativeCursorPosition = new Vector2f(onScreenCursorPosition);


        // cursor position relative to the TextInputComponent
        relativeCursorPosition.x = (relativeCursorPosition.x - (textInputComponent.getOnScreenXPosition() - textInputComponent.getOnScreenWidth() * (0.5f)))/textInputComponent.getOnScreenWidth();
        relativeCursorPosition.y = (relativeCursorPosition.y - (textInputComponent.getOnScreenYPosition() - textInputComponent.getOnScreenHeight()/2))/textInputComponent.getOnScreenHeight();


        // calculates and performs horizontal movements
        if(textInputComponent.getOnScreenWidth() > 0) {
            if(textInputComponent.getOnScreenWidth() < this.getOnScreenWidth()) {
                this.getHorizontalBar().setScrollPosition(0);
                calculateValues();
            }

            if (onScreenCursorPosition.x < (this.getOnScreenXPosition() + this.getOnScreenWidth() * (-0.5f + getXOffset()))) {

                float relativeWidth = this.getOnScreenWidth()/textInputComponent.getOnScreenWidth();

                this.getHorizontalBar().setScrollPosition((relativeCursorPosition.x)/(1 - relativeWidth));

                calculateValues();


            } else if (onScreenCursorPosition.x > (this.getOnScreenXPosition() + this.getOnScreenWidth() * (0.5f + getXOffset()))) {

                float relativeWidth = this.getOnScreenWidth()/textInputComponent.getOnScreenWidth();

                this.getHorizontalBar().setScrollPosition((relativeCursorPosition.x - relativeWidth)/(1 - relativeWidth));

                calculateValues();
            }
        }

        // calculates and performs vertical movements
        if(textInputComponent.getOnScreenHeight() > 0) {

            if(textInputComponent.getOnScreenHeight() < this.getOnScreenHeight()) {
                this.getVerticalBar().setScrollPosition(0);
                calculateValues();
            }

            if (onScreenCursorPosition.y < (this.getOnScreenYPosition() + this.getOnScreenHeight() * (-0.5f + getYOffset()))) {

                float relativeHeight = this.getOnScreenHeight()/textInputComponent.getOnScreenHeight();

                this.getVerticalBar().setScrollPosition((relativeCursorPosition.y - 1f/textInputComponent.getTextItem().getLines() * 0.5f)/(1 - relativeHeight));

                calculateValues();


            } else if (onScreenCursorPosition.y > (this.getOnScreenYPosition() + this.getOnScreenHeight() * (0.5f + getYOffset()))) {

                float relativeHeight = this.getOnScreenHeight()/textInputComponent.getOnScreenHeight();

                this.getVerticalBar().setScrollPosition((relativeCursorPosition.y + 1f/textInputComponent.getTextItem().getLines() * 0.5f - relativeHeight)/(1 - relativeHeight));

                System.out.println(relativeCursorPosition.y + " : " + this.getVerticalBar().getScrollPosition());

                calculateValues();
            }

        }


    }


    public TextInputComponent getTextInputComponent() {
        return textInputComponent;
    }
}
