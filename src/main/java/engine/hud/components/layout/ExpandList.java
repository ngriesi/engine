package engine.hud.components.layout;

import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeToParentPosition;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;

public class ExpandList extends QuadComponent {

    public enum Orientation {
        VERTICAL,HORIZONTAL
    }

    private Orientation orientation;

    private int elementsPerPage;

    public ExpandList(Orientation orientation) {
        this.orientation = orientation;
        if(orientation == Orientation.HORIZONTAL) {
            this.setHeightConstraint(new RelativeToParentSize(0));
        } else {
            this.setWidthConstraint(new RelativeToParentSize(0));
        }
        elementsPerPage = 1;
    }

    @Override
    public void addComponent(SubComponent component) {

        if(orientation == Orientation.HORIZONTAL) {
            changeHeightValue(getHeight().getAbsoluteValue() + 1.0f/elementsPerPage);

            component.setHeightConstraint(new RelativeToParentSize(1.0f/(getContent().size()+1)));
            component.setyPositionConstraint(new RelativeToParentPosition(1.0f/(getContent().size() + 1) * (getContent().size() + 0.5f)));

        } else {
            changeWidthValue(getWidth().getAbsoluteValue() + 1.0f/elementsPerPage);

            component.setWidthConstraint(new RelativeToParentSize(1.0f/(getContent().size()+1)));
            component.setxPositionConstraint(new RelativeToParentPosition(1.0f/(getContent().size() + 1) * (getContent().size() + 0.5f)));

        }

        for(int i = 0;i < getContent().size();i++) {
            if(orientation == Orientation.HORIZONTAL) {

                getContent().get(i).setHeightConstraint(new RelativeToParentSize(1.0f / (getContent().size() + 1)));
                getContent().get(i).setyPositionConstraint(new RelativeToParentPosition(1.0f / (getContent().size() + 1) * (i+0.5f)));
            } else {
                getContent().get(i).setWidthConstraint(new RelativeToParentSize(1.0f / (getContent().size() + 1)));
                getContent().get(i).setxPositionConstraint(new RelativeToParentPosition(1.0f / (getContent().size() + 1) * (i+0.5f)));

            }
        }

        super.addComponent(component);


    }

    @Override
    public void removeComponent(SubComponent component) {

        int index = content.indexOf(component);

        if(orientation == Orientation.HORIZONTAL) {
            changeHeightValue(getHeight().getAbsoluteValue() - 1.0f/elementsPerPage);
        } else {
            changeWidthValue(getHeight().getAbsoluteValue() - 1.0f/elementsPerPage);
        }

        for(int i = 0;i < getContent().size();i++) {
            if(orientation == Orientation.HORIZONTAL) {

                getContent().get(i).setHeightConstraint(new RelativeToParentSize(1.0f / (getContent().size() - 1)));
                if(i < index) {
                    getContent().get(i).setyPositionConstraint(new RelativeToParentPosition(1.0f / (getContent().size() - 1) * (i + 0.5f)));
                } else {
                    getContent().get(i).setyPositionConstraint(new RelativeToParentPosition(1.0f / (getContent().size() - 1) * (i - 0.5f)));
                }
            } else {
                getContent().get(i).setWidthConstraint(new RelativeToParentSize(1.0f / (getContent().size()-1)));
                if(i < index) {
                    getContent().get(i).setxPositionConstraint(new RelativeToParentPosition(1.0f / (getContent().size() - 1) * (i + 0.5f)));
                } else {
                    getContent().get(i).setxPositionConstraint(new RelativeToParentPosition(1.0f / (getContent().size() - 1) * (i - 0.5f)));
                }
            }
        }

        if(hud != null) {
            hud.addEndOFrameAction(() -> {
                super.removeComponent(component);
                updateBounds();
                hud.needsNextRendering();
            });

        } else {
            super.removeComponent(component);
            updateBounds();
        }

    }

    public void setElementsPerPage(int elementsPerPage) {
        this.elementsPerPage = elementsPerPage;
    }
}
