package engine.hud.components.presets.scroll;

import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.layout.ExpandList;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.RelativeToScreenSize;
import engine.hud.constraints.sizeConstraints.SubtractConstraint;

/**
 * scroll view for horizontal and vertical scrolling, unused bars
 * are hidden automatically
 */
@SuppressWarnings("unused")
public class ScrollView extends ScrollComponent {

    /**
     * component that gets scrolled, can be set with the <code>setContent</code> method
     */
    private SubComponent contentBack;

    /**
     * vertical scroll bar
     */
    private final ScrollBar verticalBar;

    /**
     * horizontal scroll bar
     */
    private final ScrollBar horizontalBar;

    /**
     * constructor creating an empty scroll view
     */
    public ScrollView() {

        contentBack = new QuadComponent();
        this.addComponent(contentBack);

        verticalBar = new ScrollBar(this, ExpandList.Orientation.VERTICAL);
        horizontalBar = new ScrollBar(this, ExpandList.Orientation.HORIZONTAL);

        // sets up the scrolling of the view
        this.getMouseListener().addMouseAction(e -> {

            // vertical scrolling setup
            float yOffset = e.getMouseInput().getYOffsetScroll();
            if(yOffset != 0) {
                if(contentBack.getOnScreenHeight() > getOnScreenHeight()) {
                    float value = verticalBar.getScrollPosition() + ((yOffset * 0.5f) / (contentBack.getOnScreenHeight() / getOnScreenHeight()));
                    value = value > 0 ? Math.min(value, 1) : 0;
                    verticalBar.setScrollPosition(value);
                    verticalBar.getBar().changeYValue(value);
                    updateContentPosition();
                    updateBounds();
                    hud.needsNextRendering();
                }
            }

            // horizontal scroll setup
            float xOffset = e.getMouseInput().getXOffsetScroll();
            if(xOffset != 0) {
                if(contentBack.getOnScreenWidth() > getOnScreenWidth()) {
                    float value = horizontalBar.getScrollPosition() + ((xOffset * 0.5f) / (contentBack.getOnScreenWidth() / getOnScreenWidth()));
                    value = value > 0 ? Math.min(value, 1) : 0;
                    horizontalBar.setScrollPosition(value);
                    horizontalBar.getBar().changeXValue(value);
                    updateContentPosition();
                    updateBounds();
                    hud.needsNextRendering();

                }
            }
            return false;
        });
    }

    /**
     * sets a new content for the scroll view
     *
     * @param content new content of the scroll view
     */
    public void setContent(SubComponent content) {
        // removes old content
        removeComponent(contentBack);
        removeComponent(verticalBar.getBarBack());
        removeComponent(horizontalBar.getBarBack());
        this.contentBack = content;

        // adds new content
        addComponent(contentBack);
        addComponent(horizontalBar.getBarBack());
        addComponent(verticalBar.getBarBack());

        // resets the scroll positions
        contentBack.setxPositionConstraint(new RelativeInParent(0));
        contentBack.setyPositionConstraint(new RelativeInParent(0));
        verticalBar.setScrollPosition(0);
        horizontalBar.setScrollPosition(0);
        calculateValues();
    }

    /**
     * updates the values of the scroll bars
     */
    @Override
    public void calculateValues() {


        // calculates  new width nad height values for the bars
        float ratioY = (contentBack.getOnScreenHeight())/this.getOnScreenHeight();

        float ratioX = (contentBack.getOnScreenWidth())/this.getOnScreenWidth();

        float height = ratioY>1?1/ratioY:1;

        float width = ratioX>1?1/ratioX:1;

        verticalBar.getBarBack().setVisible(ratioY>1);
        horizontalBar.getBarBack().setVisible(ratioX>1);

        // sets the size of the horizontal bar
        if(ratioX>1 && ratioY>1) {
            horizontalBar.getBarBack().setWidthConstraint(new SubtractConstraint(new RelativeToParentSize(1),new RelativeToScreenSize(0.01f)));
        }

        // sets the new heights
        verticalBar.getBar().changeHeightValue(height);
        horizontalBar.getBar().changeWidthValue(width);

        // sets the new positions
        verticalBar.getBar().changeYValue(verticalBar.getScrollPosition());
        horizontalBar.getBar().changeXValue(horizontalBar.getScrollPosition());

        // updates the position of the content
        updateContentPosition();
    }

    /**
     * updates the position of the content of the scroll view
     */
    @Override
    protected void updateContentPosition() {
        contentBack.changeYValue(verticalBar.getScrollPosition());
        contentBack.changeXValue(horizontalBar.getScrollPosition());
    }

    public ScrollBar getVerticalBar() {
        return verticalBar;
    }

    public ScrollBar getHorizontalBar() {
        return horizontalBar;
    }


}
