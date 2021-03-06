package engine.hud.components.presets.scroll;

import engine.hud.color.Color;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.layout.ExpandList;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.InvertedDirectionRelativeToScreen;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.RelativeToScreenSize;
import engine.hud.events.DragEvent;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseListener;
import org.joml.Vector2f;

/**
 * used to create a scroll bar, used in the ScrollViews
 *
 * @see ScrollView for useage
 */
@SuppressWarnings("WeakerAccess")
public class ScrollBar implements DragEvent {

    /**
     * determines the orientation of the scroll bar
     */
    private final ExpandList.Orientation orientation;

    /**
     * components the scroll bar consists of
     */
    private final QuadComponent barBack,bar;

    /**
     * sets the thickness of the bar relative to the screen width
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final float barThickness = 0.01f;

    /**
     * if true the scroll bar currently get dragged
     */
    private boolean dragging = false;

    /**
     * saves the offset between the mouse coordinates and the scroll bar position while dragging
     */
    private float diff;

    /**
     * current position of the scroll bar
     */
    private float scrollPosition;

    /**
     * scroll view the bar belongs to
     */
    private final ScrollComponent scrollView;

    /**
     * constructor creating a scroll bar for a scroll view with a defined rotation
     *
     * @param scrollView the bar belongs to
     * @param orientation of the scroll bar
     */
    public ScrollBar(ScrollComponent scrollView, ExpandList.Orientation orientation) {
        this.scrollView = scrollView;
        this.orientation = orientation;

        // formats the bars background
        barBack = new QuadComponent();
        if(orientation== ExpandList.Orientation.VERTICAL) {
            barBack.setWidthConstraint(new RelativeToScreenSize(barThickness));
            barBack.setHeightConstraint(new RelativeToParentSize(1));
            barBack.setXPositionConstraint(new RelativeInParent(1));
            barBack.setYPositionConstraint(new RelativeInParent(0.5f));
        } else {
            barBack.setHeightConstraint(new InvertedDirectionRelativeToScreen(barThickness));
            barBack.setWidthConstraint(new RelativeToParentSize(1));
            barBack.setYPositionConstraint(new RelativeInParent(1));
            barBack.setXPositionConstraint(new RelativeInParent(0));
        }
        barBack.setColors(Color.TRANSPARENT);

        barBack.setFocusable(false);

        scrollView.addComponent(barBack);

        // formats the scroll bar bar
        bar = new QuadComponent();
        if(orientation == ExpandList.Orientation.VERTICAL) {
            bar.setWidthConstraint(new RelativeToParentSize(0.8f));
            bar.setHeightConstraint(new RelativeToParentSize(1));
            bar.setYPositionConstraint(new RelativeInParent(0));
            bar.setXPositionConstraint(new RelativeInParent(0.5f));
        } else {
            bar.setHeightConstraint(new RelativeToParentSize(0.8f));
            bar.setWidthConstraint(new RelativeToParentSize(1));
            bar.setXPositionConstraint(new RelativeInParent(0));
            bar.setYPositionConstraint(new RelativeInParent(0.5f));
        }
        bar.setColors(new Color(0.7f,0.7f,0.7f,0.5f));

        bar.setFocusable(false);

        // defines the behaviour of the bar with the mouse
        bar.getMouseListener().addMouseAction(e -> {
            if(e.getEvent()== MouseEvent.Event.DRAG_STARTED && e.getMouseButton() == MouseListener.MouseButton.LEFT) {
                scrollView.getHud().setLeftDragEvent(ScrollBar.this);
                return true;
            } else return e.getEvent() == MouseEvent.Event.DRAG_RELEASED && e.getMouseButton() == MouseListener.MouseButton.LEFT;
        });

        barBack.getMouseListener().addMouseAction(e -> {
            if(e.getEvent() == MouseEvent.Event.CLICK_RELEASED && e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                if(orientation == ExpandList.Orientation.VERTICAL) {
                    float yValue = (scrollView.getHud().getLastMousePositionRelative().y) / (barBack.getOnScreenHeight()) - scrollView.getOnScreenYPosition() - 2 * bar.getOnScreenHeight();

                    float value = (yValue) > 0 ? Math.min(1, yValue) : 0;

                    bar.changeYValue(value);
                    scrollPosition = value;
                    scrollView.updateContentPosition();

                    return false;
                } else {
                    float xValue = (scrollView.getHud().getLastMousePositionRelative().x) / (barBack.getOnScreenWidth()) - scrollView.getOnScreenXPosition() - 2 * bar.getOnScreenWidth();

                    float value = (xValue) > 0 ? Math.min(1, xValue) : 0;

                    bar.changeXValue(value);
                    scrollPosition = value;
                    scrollView.updateContentPosition();
                    return false;
                }
            }
            return false;
        });

        barBack.addComponent(bar);

        scrollPosition = 0;
    }

    public QuadComponent getBar() {
        return bar;
    }

    public QuadComponent getBarBack() {
        return barBack;
    }

    public void setScrollPosition(float scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    public float getScrollPosition() {
        return scrollPosition;
    }

    @Override
    public void dropAction() {
        dragging = false;
    }

    @Override
    public SubComponent getDragVisual() {
        return null;
    }

    /**
     * action performed when the bar gets dragged
     *
     * @param lastMousePosition last position of the mouse (last frame)
     */
    @Override
    public void dragAction(Vector2f lastMousePosition) {
        float value;
        if(orientation == ExpandList.Orientation.VERTICAL) {
            float yValue = (lastMousePosition.y - (barBack.getOnScreenYPosition() - barBack.getOnScreenHeight() / 2)) / (barBack.getOnScreenHeight());

            yValue = (yValue) * ((bar.getOnScreenHeight() + barBack.getOnScreenHeight()) / barBack.getOnScreenHeight());

            if (!dragging) {
                diff = yValue - scrollPosition;
                dragging = true;
            }

            value = (yValue - diff) > 0 ? Math.min(1, yValue - diff) : 0;

            bar.changeYValue(value);
        } else {
            float xValue = (lastMousePosition.x - (barBack.getOnScreenXPosition() - barBack.getOnScreenWidth() / 2)) / (barBack.getOnScreenWidth());

            xValue = (xValue) * ((bar.getOnScreenWidth() + barBack.getOnScreenWidth()) / barBack.getOnScreenWidth());

            if (!dragging) {
                diff = xValue - scrollPosition;
                dragging = true;
            }

            value = (xValue - diff) > 0 ? Math.min(1, xValue - diff) : 0;

            bar.changeXValue(value);
        }

        scrollPosition = value;
        scrollView.updateContentPosition();
    }
}