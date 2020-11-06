package engine.hud.components.presets;

import engine.hud.color.Color;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.RelativeToScreenSize;
import engine.hud.events.DragEvent;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseListener;
import org.joml.Vector2f;

@SuppressWarnings("unused")
public class HorizontalScrollView extends QuadComponent implements DragEvent {

    private QuadComponent barBack,bar;

    private SubComponent contentBack;

    private float barHeight = 0.01f;

    private float scrollPosition;

    private boolean dragging = false;

    private float diff;

    public HorizontalScrollView() {

        contentBack = new QuadComponent();
        this.addComponent(contentBack);

        barBack = new QuadComponent();
        barBack.setHeightConstraint(new RelativeToScreenSize(barHeight));
        barBack.setWidthConstraint(new RelativeToParentSize(1));
        barBack.setyPositionConstraint(new RelativeInParent(1));
        barBack.setxPositionConstraint(new RelativeInParent(0.5f));
        barBack.setColors(Color.TRANSPARENT);

        barBack.setSelectable(false);

        this.addComponent(barBack);

        bar = new QuadComponent();
        bar.setHeightConstraint(new RelativeToParentSize(0.8f));
        bar.setWidthConstraint(new RelativeToParentSize(1));
        bar.setxPositionConstraint(new RelativeInParent(0));
        bar.setyPositionConstraint(new RelativeInParent(0.5f));
        bar.setColors(new Color(0.7f,0.7f,0.7f,0.5f));

        bar.setSelectable(false);

        bar.getMouseListener().addMouseAction(e -> {
            if(e.getEvent()== MouseEvent.Event.DRAG_STARTED && e.getMouseButton() == MouseListener.MouseButton.LEFT) {
                hud.setLeftDragEvent(HorizontalScrollView.this);
                return true;
            } else return e.getEvent() == MouseEvent.Event.DRAG_RELEASED && e.getMouseButton() == MouseListener.MouseButton.LEFT;
        });

        barBack.getMouseListener().addMouseAction(e -> {
            if(e.getEvent() == MouseEvent.Event.CLICK_RELEASED && e.getMouseButton()== MouseListener.MouseButton.LEFT) {
                float xValue = (hud.getLastMousePositionRelative().x) / (barBack.getOnScreenWidth()) - getOnScreenXPosition() - 2 * bar.getOnScreenWidth();

                float value = (xValue) > 0?Math.min(1,xValue):0;

                bar.changeXValue(value);
                scrollPosition = value;
                updateContentPosition();

                return false;
            }
            return false;
        });

        barBack.addComponent(bar);

        scrollPosition = 0;

        this.getMouseListener().addMouseAction(e -> {
            float xOffset = e.getMouseInput().getXOffsetScroll();
            System.out.println(xOffset);
            if(xOffset != 0) {
                if(contentBack.getOnScreenWidth() > getOnScreenWidth()) {
                    float value = scrollPosition + ((xOffset * 0.5f) / (contentBack.getOnScreenWidth() / getOnScreenWidth()));
                    value = value > 0 ? Math.min(value, 1) : 0;
                    System.out.println(value);
                    scrollPosition = value;
                    bar.changeXValue(value);
                    updateContentPosition();
                    updateBounds();
                    hud.needsNextRendering();

                }
            }
            return false;
        });
    }

    @Override
    public void updateBounds() {
        super.updateBounds();
        calculateValues();
    }

    public void setContent(SubComponent content) {
        removeComponent(contentBack);
        removeComponent(barBack);
        this.contentBack = content;
        addComponent(barBack);
        addComponent(contentBack);
        contentBack.setxPositionConstraint(new RelativeInParent(0));
        contentBack.setyPositionConstraint(new RelativeInParent(0));
        contentBack.setHeightConstraint(new RelativeToParentSize(1));
        scrollPosition = 0f;
        calculateValues();
    }

    private void calculateValues() {


        float ratio = (contentBack.getOnScreenWidth())/this.getOnScreenWidth();

        float width = ratio>1?1/ratio:1;

        barBack.setVisible(ratio>1);

        bar.changeWidthValue(width);
        bar.changeXValue(scrollPosition);

        updateContentPosition();
    }

    private void updateContentPosition() {
        contentBack.changeXValue(scrollPosition);
    }

    @Override
    public void dropAction() {
        dragging = false;
    }

    @Override
    public SubComponent getDragVisual() {
        return null;
    }


    @Override
    public void dragAction(Vector2f lastMousePosition) {

        float xValue = (lastMousePosition.x - (barBack.getOnScreenXPosition() - barBack.getOnScreenWidth()/2)) / (barBack.getOnScreenWidth());

        xValue = (xValue) * ((bar.getOnScreenWidth() + barBack.getOnScreenWidth())/barBack.getOnScreenWidth());

        if(!dragging) {
            diff = xValue - scrollPosition;
            dragging = true;
        }

        float value = (xValue - diff) > 0?Math.min(1,xValue - diff):0;

        bar.changeXValue(value);
        scrollPosition = value;
        updateContentPosition();
    }

    public QuadComponent getBarBack() {
        return barBack;
    }

    public QuadComponent getBar() {
        return bar;
    }

    public void setBarWidth(float barHeight) {
        this.barHeight = barHeight;
        barBack.changeHeightValue(barHeight);
    }
}
