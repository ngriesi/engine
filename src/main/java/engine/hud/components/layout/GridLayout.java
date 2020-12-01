package engine.hud.components.layout;

import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeToParentPosition;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;

import java.util.HashMap;

/**
 * Grid layout is a quad component that allows the adding of sub components
 * in a grid layout. For these sub components the x and y position can be specified as a value between
 * one and the number of columns/rows of the grid
 */
@SuppressWarnings("unused")
public class GridLayout extends QuadComponent {

    /**
     * number of rows and columns in the grid layout
     */
    private int columns,rows;

    /**
     * vertical padding between the elemants of the grid
     */
    private float verticalPadding = 0;

    /**
     * horizontal padding between the elements of the grid
     */
    private float horizontalPadding = 0;

    /**
     * links the sub components in the content components list of the layout with their
     * position data in the gri layout
     */
    private final HashMap<SubComponent,GridData> positionData;

    /**
     * constructor sets number of rows and columns of the grid layout
     *
     * @param columns of the grid
     * @param rows of the grid
     */
    public GridLayout(int columns,int rows) {
        this.columns = columns;
        this.rows = rows;
        positionData = new HashMap<>();
    }

    /**
     * sets a new column number and updates the position after
     *
     * @param columns new column number
     */
    public void setColumns(int columns) {
        this.columns = columns;
        updatePositions();
    }

    /**
     * sets a new number of rows and updates the position after
     *
     * @param rows new number of rows
     */
    public void setRows(int rows) {
        this.rows = rows;
        updatePositions();
    }

    /**
     * sets an new vertical padding and updates the position
     *
     * @param verticalPadding new vertical padding
     */
    public void setVerticalPadding(float verticalPadding) {
        this.verticalPadding = verticalPadding;
        updatePositions();
    }

    /**
     * sets a new horizontal padding and updates the position
     *
     * @param horizontalPadding new horizontal padding
     */
    public void setHorizontalPadding(float horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
        updatePositions();
    }

    /**
     * returns the component at a specific position
     *
     * @param row of the component
     * @param column of the component
     * @return component at the position
     */
    public SubComponent getComponent(int row,int column) {
        for(SubComponent subComponent : positionData.keySet()) {
            GridData data = positionData.get(subComponent);
            if(data.getRow() == row && data.getColumn() == column) {
                return subComponent;
            }
        }
        return null;
    }

    /**
     * updates the position of the while grid
     */
    private void updatePositions() {
        for(SubComponent subComponent : positionData.keySet()) {
            GridData data = positionData.get(subComponent);
            subComponent.setHeightConstraint(new RelativeToParentSize(1.0f/rows * (data.getHeight() - verticalPadding)));
            subComponent.setWidthConstraint(new RelativeToParentSize(1.0f/columns * (data.getWidth() - horizontalPadding)));
            subComponent.setXPositionConstraint(new RelativeToParentPosition((data.getColumn()+(0.5f * (data.getWidth() - 2))) * 1.0f/columns));
            subComponent.setYPositionConstraint(new RelativeToParentPosition((data.getRow()+(0.5f * (data.getHeight() - 2))) * 1.0f/rows));
        }
        updateBounds();
    }

    /**
     * adds a sub component to the grid at specific location if that location is empty
     *
     * @param component to be added
     * @param column of the component
     * @param row of the component
     */
    public void addToGrid(SubComponent component, int column, int row) {
        if(getComponent(row, column)==null) {
            component.setHeightConstraint(new RelativeToParentSize(1.0f / rows * (1 - verticalPadding)));
            component.setWidthConstraint(new RelativeToParentSize(1.0f / columns * (1 - horizontalPadding)));
            component.setXPositionConstraint(new RelativeToParentPosition((column - 0.5f) * 1.0f / columns));
            component.setYPositionConstraint(new RelativeToParentPosition((row - 0.5f) * 1.0f / rows));
            positionData.put(component, new GridData(row, column, 1, 1));
            super.addComponent(component);
        }
    }

    /**
     * adds a sub component to the grid at a specific location with a specific size. If width or size are
     * greater than 1, the position specified will be the top left corner of the component. If this field is not empty
     * the component does not get added
     *
     * @param component to be added
     * @param column of the components top left corner
     * @param row of the components top left corner
     * @param width of the components in columns
     * @param height of the components in rows
     */
    public void addToGrid(SubComponent component,int column,int row,int width,int height) {
        if(getComponent(row, column)== null) {
            component.setHeightConstraint(new RelativeToParentSize(1.0f / rows * (height - verticalPadding)));
            component.setWidthConstraint(new RelativeToParentSize(1.0f / columns * (width - horizontalPadding)));
            component.setXPositionConstraint(new RelativeToParentPosition((column + (0.5f * (width - 2))) * 1.0f / columns));
            component.setYPositionConstraint(new RelativeToParentPosition((row + (0.5f * (height - 2))) * 1.0f / rows));
            positionData.put(component, new GridData(row, column, width, height));
            super.addComponent(component);
        }
    }

    /**
     * removes a component at a specific location
     *
     * @param row of the removed component
     * @param column of the removed component
     */
    public void remove(int row,int column) {
        SubComponent temp = getComponent(row, column);
        positionData.remove(temp);
        saveRemoveComponent(temp);
    }

    /**
     * puts a components at a specific location: if the component is already in the
     * grid, it gets moved otherwise it gets added
     *
     * @param subComponent to be moved/added
     * @param row target row of the component
     * @param column target column of the component
     */
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

    /**
     * private data class to store the information about position and size of
     * a component in the grid
     */
    private static class GridData {

        /**
         * position and size attributes
         */
        private int row,column,width,height;

        /**
         * constructor setting all values
         *
         * @param row of the component
         * @param column of the component
         * @param width of the component
         * @param height of the component
         */
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

        public void setRow(int row) {
            this.row = row;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
