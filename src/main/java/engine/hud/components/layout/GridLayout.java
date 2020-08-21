package engine.hud.components.layout;

import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeToParentPosition;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;

import java.util.HashMap;

public class GridLayout extends QuadComponent {

    private int columns,rows;

    private float verticalPadding = 0;

    private float horizontalPadding = 0;

    private HashMap<SubComponent,GridData> positionData;

    public GridLayout(int columns,int rows) {
        this.columns = columns;
        this.rows = rows;
        positionData = new HashMap<>();
    }

    public void setColumns(int columns) {
        this.columns = columns;
        updatePositions();
    }

    public void setRows(int rows) {
        this.rows = rows;
        updatePositions();
    }

    public void setVerticalPadding(float verticalPadding) {
        this.verticalPadding = verticalPadding;
        updatePositions();
    }

    public void setHorizontalPadding(float horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
        updatePositions();
    }

    public SubComponent getComponent(int row,int column) {
        for(SubComponent subComponent : positionData.keySet()) {
            GridData data = positionData.get(subComponent);
            if(data.getRow() == row && data.getColumn() == column) {
                return subComponent;
            }
        }
        return null;
    }

    private void updatePositions() {
        for(SubComponent subComponent : positionData.keySet()) {
            GridData data = positionData.get(subComponent);
            subComponent.setHeightConstraint(new RelativeToParentSize(1.0f/rows * (data.getHeight() - verticalPadding)));
            subComponent.setWidthConstraint(new RelativeToParentSize(1.0f/columns * (data.getWidth() - horizontalPadding)));
            subComponent.setxPositionConstraint(new RelativeToParentPosition((data.getColumn()+(0.5f * (data.getWidth() - 2))) * 1.0f/columns));
            subComponent.setyPositionConstraint(new RelativeToParentPosition((data.getRow()+(0.5f * (data.getHeight() - 2))) * 1.0f/rows));
        }
        updateBounds();
    }

    public void addToGrid(SubComponent component, int column, int row) {
        if(getComponent(row, column)==null) {
            component.setHeightConstraint(new RelativeToParentSize(1.0f / rows * (1 - verticalPadding)));
            component.setWidthConstraint(new RelativeToParentSize(1.0f / columns * (1 - horizontalPadding)));
            component.setxPositionConstraint(new RelativeToParentPosition((column - 0.5f) * 1.0f / columns));
            component.setyPositionConstraint(new RelativeToParentPosition((row - 0.5f) * 1.0f / rows));
            positionData.put(component, new GridData(row, column, 1, 1));
            super.addComponent(component);
        }
    }

    public void addToGrid(SubComponent component,int column,int row,int width,int height) {
        if(getComponent(row, column)== null) {
            component.setHeightConstraint(new RelativeToParentSize(1.0f / rows * (height - verticalPadding)));
            component.setWidthConstraint(new RelativeToParentSize(1.0f / columns * (width - horizontalPadding)));
            component.setxPositionConstraint(new RelativeToParentPosition((column + (0.5f * (width - 2))) * 1.0f / columns));
            component.setyPositionConstraint(new RelativeToParentPosition((row + (0.5f * (height - 2))) * 1.0f / rows));
            positionData.put(component, new GridData(row, column, width, height));
            super.addComponent(component);
        }
    }

    public void remove(int row,int column) {
        SubComponent temp = getComponent(row, column);
        positionData.remove(temp);
        saveRemoveComponent(temp);
    }

    public void setToPosition(SubComponent subComponent,int row, int column) {
        SubComponent temp = getComponent(row, column);
        if(temp==null) {
            addToGrid(subComponent,row,column);
        } else {
            GridData data = positionData.get(temp);
            positionData.remove(temp);
            saveRemoveComponent(temp);
            addToGrid(subComponent,row,column,data.width,data.height);
        }
    }

    private static class GridData {

        private int row,column,width,height;

        GridData(int row, int column, int width, int height) {
            this.row = row;
            this.column = column;
            this.width = width;
            this.height = height;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
