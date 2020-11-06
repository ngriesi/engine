package engine.hud.components.presets;

import engine.hud.color.Color;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.layout.ExpandList;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.RelativeToScreenSize;
import engine.hud.events.DragEvent;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseListener;
import org.joml.Vector2f;

@SuppressWarnings("unused")
public class ScrollView extends QuadComponent {

    private SubComponent contentBack;

    private ScrollBar verticalBar,horizontalBar;

    public ScrollView() {

        contentBack = new QuadComponent();
        this.addComponent(contentBack);

        verticalBar = new ScrollBar(this, ExpandList.Orientation.VERTICAL);
        horizontalBar = new ScrollBar(this, ExpandList.Orientation.HORIZONTAL);

        this.getMouseListener().addMouseAction(e -> {
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

    @Override
    public void updateBounds() {
        super.updateBounds();
        calculateValues();
    }

    public void setContent(SubComponent content) {
        removeComponent(contentBack);
        removeComponent(verticalBar.getBarBack());
        removeComponent(horizontalBar.getBarBack());
        this.contentBack = content;
        addComponent(horizontalBar.getBarBack());
        addComponent(verticalBar.getBarBack());
        addComponent(contentBack);
        contentBack.setxPositionConstraint(new RelativeInParent(0));
        contentBack.setyPositionConstraint(new RelativeInParent(0));
        verticalBar.setScrollPosition(0);
        horizontalBar.setScrollPosition(0);
        calculateValues();
    }

    private void calculateValues() {


        float ratioY = (contentBack.getOnScreenHeight())/this.getOnScreenHeight();

        float ratioX = (contentBack.getOnScreenWidth())/this.getOnScreenWidth();

        float height = ratioY>1?1/ratioY:1;

        float width = ratioX>1?1/ratioX:1;

        verticalBar.getBarBack().setVisible(ratioY>1);
        horizontalBar.getBarBack().setVisible(ratioX>1);

        if(ratioX>1 && ratioY>1) {
            horizontalBar.getBarBack().changeWidthValue(0.9f);
        }

        verticalBar.getBar().changeHeightValue(height);
        horizontalBar.getBar().changeWidthValue(width);

        verticalBar.getBar().changeYValue(verticalBar.getScrollPosition());
        horizontalBar.getBar().changeXValue(horizontalBar.getScrollPosition());

        updateContentPosition();
    }

    void updateContentPosition() {
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
