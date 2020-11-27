package engine.hud.components.presets.scroll;

import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.layout.ExpandList;
import engine.hud.constraints.positionConstraints.RelativeInParent;

/**
 * scroll view for horizontal and vertical scrolling, unused bars
 * are hidden automatically
 */
@SuppressWarnings("unused")
public class HorizontalScrollView extends ScrollComponent {

    /**
     * component that gets scrolled, can be set with the <code>setContent</code> method
     */
    private SubComponent contentBack;

    /**
     * horizontal scroll bar
     */
    private final ScrollBar horizontalBar;

    /**
     * constructor creating an empty scroll view
     */
    public HorizontalScrollView() {

        contentBack = new QuadComponent();
        this.addComponent(contentBack);

        horizontalBar = new ScrollBar(this, ExpandList.Orientation.HORIZONTAL);

        // sets up the scrolling of the view
        this.getMouseListener().addMouseAction(e -> {


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
     * update bounds method calls the method to calculate new scroll position
     */
    @Override
    public void updateBounds() {
        super.updateBounds();
        calculateValues();
    }

    /**
     * sets a new content for the scroll view
     *
     * @param content new content of the scroll view
     */
    public void setContent(SubComponent content) {
        // removes old content
        removeComponent(contentBack);
        removeComponent(horizontalBar.getBarBack());
        this.contentBack = content;

        // adds new content
        addComponent(contentBack);
        addComponent(horizontalBar.getBarBack());


        // resets the scroll positions
        contentBack.setxPositionConstraint(new RelativeInParent(0));
        horizontalBar.setScrollPosition(0);
        calculateValues();
    }

    /**
     * updates the values of the scroll bars
     */
    public void calculateValues() {


        // calculates  new width nad height values for the bars
        float ratioX = (contentBack.getOnScreenWidth())/this.getOnScreenWidth();

        float width = ratioX>1?1/ratioX:1;

        horizontalBar.getBarBack().setVisible(ratioX>1);

        horizontalBar.getBarBack().setWidthConstraint(1);

        // sets the new heights
        horizontalBar.getBar().changeWidthValue(width);

        // sets the new positions
        horizontalBar.getBar().changeXValue(horizontalBar.getScrollPosition());

        // updates the position of the content
        updateContentPosition();
    }

    /**
     * updates the position of the content of the scroll view
     */
    @Override
    protected void updateContentPosition() {
        contentBack.changeXValue(horizontalBar.getScrollPosition());
    }

    public ScrollBar getHorizontalBar() {
        return horizontalBar;
    }


}
