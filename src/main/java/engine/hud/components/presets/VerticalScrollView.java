package engine.hud.components.presets;

import engine.hud.color.Color;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.RelativeToScreenSize;
import engine.hud.events.DragEvent;
import org.joml.Vector2f;

public class VerticalScrollView extends QuadComponent implements DragEvent {

    private QuadComponent barBack,bar;

    private SubComponent contentBack;

    private float barWidth = 0.015f;

    private float scrollPosition;

    private boolean dragging = false;

    private float diff;

    public VerticalScrollView() {

        contentBack = new QuadComponent();
        this.addComponent(contentBack);

        barBack = new QuadComponent();
        barBack.setWidthConstraint(new RelativeToScreenSize(barWidth));
        barBack.setHeightConstraint(new RelativeToParentSize(1));
        barBack.setxPositionConstraint(new RelativeInParent(1));
        barBack.setyPositionConstraint(new RelativeInParent(0.5f));
        barBack.setColors(Color.VERY_LIGHT_GRAY);

        barBack.setSelectable(false);

        this.addComponent(barBack);

        bar = new QuadComponent();
        bar.setWidthConstraint(new RelativeToParentSize(1));
        bar.setHeightConstraint(new RelativeToParentSize(1));
        bar.setyPositionConstraint(new RelativeInParent(0));
        bar.setxPositionConstraint(new RelativeInParent(0.5f));
        bar.setColors(Color.GREY);

        bar.setSelectable(false);

        bar.setOnLeftDragStarted(() -> {
            hud.setLeftDragEvent(VerticalScrollView.this);
            return true;
        });

        barBack.setOnLeftClickAction(() -> {
            float yValue = (hud.getLastMousePositionRelative().y) / (barBack.getOnScreenHeight()) - getOnScreenYPosition() - 2 * bar.getOnScreenHeight();

            float value = (yValue) > 0?Math.min(1,yValue):0;

            bar.changeYValue(value);
            scrollPosition = value;
            updateContentPosition();
            return false;
        });

        barBack.addComponent(bar);

        scrollPosition = 0;

        this.setMouseAction(mouseInput -> {

            float yOffset = mouseInput.getYOffsetScroll();
            if(yOffset != 0) {
                if(contentBack.getOnScreenHeight() > getOnScreenHeight()) {
                    float value = scrollPosition + ((yOffset * 0.5f) / (contentBack.getOnScreenHeight() / getOnScreenHeight()));
                    value = value > 0 ? Math.min(value, 1) : 0;
                    scrollPosition = value;
                    bar.changeYValue(value);
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
        addComponent(contentBack);
        addComponent(barBack);
        contentBack.setxPositionConstraint(new RelativeInParent(0));
        contentBack.setyPositionConstraint(new RelativeInParent(0));
        contentBack.setWidthConstraint(new RelativeToParentSize(1));
        scrollPosition = 0f;
        calculateValues();
    }

    protected void calculateValues() {


        float ratio = (contentBack.getOnScreenHeight())/this.getOnScreenHeight();

        float height = ratio>1?1/ratio:1;

        barBack.setVisible(ratio>1);

        bar.changeHeightValue(height);
        bar.changeYValue(scrollPosition);

        updateContentPosition();
    }

    private void updateContentPosition() {
        contentBack.changeYValue(scrollPosition);
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

        float yValue = (lastMousePosition.y) / (barBack.getOnScreenHeight()-bar.getOnScreenHeight());

        if(!dragging) {
            diff = yValue - scrollPosition;
            dragging = true;
        }

        float value = (yValue -diff) > 0?Math.min(1,yValue - diff):0;


        bar.changeYValue(value);
        scrollPosition = value;
        updateContentPosition();
    }

    public QuadComponent getBarBack() {
        return barBack;
    }

    public QuadComponent getBar() {
        return bar;
    }
}
