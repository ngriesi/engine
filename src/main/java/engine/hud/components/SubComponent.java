package engine.hud.components;

import engine.general.MouseInput;
import engine.hud.actions.DragAction;
import engine.hud.actions.MouseAction;
import engine.hud.actions.ReturnAction;
import engine.hud.constraints.positionConstraints.PositionConstraint;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.positionConstraints.RelativeToWindowPosition;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.RelativeToWindowSize;
import engine.hud.constraints.sizeConstraints.SizeConstraint;
import org.joml.Vector2f;

public class SubComponent extends ContentComponent {

    /** Action, executed when the mouse enters the component */
    private MouseAction mouseEnteredAction;

    /** Action, executed when the mouse exits the component */
    private MouseAction mouseExitedAction;

    /** Action, executed when mouse enters the component with right button pressed and releases it for the first time */
    private DragAction rightDragEnterRelease;

    /** Action, executed when mouse enters the component with left button pressed and releases it for the first time */
    private DragAction leftDragEnterRelease;

    /** Action, executed when component gets right clicked or right pressed and the pressed Action doesn't consume the event*/
    private ReturnAction onRightClickAction;

    /** Action, executed when component gets left clicked or left pressed and the pressed Action doesn't consume the event*/
    private ReturnAction onLeftClickAction;

    /** Action, executed when component gets right pressed */
    private ReturnAction onRightPressedAction;

    /** Action, executed when component gets left pressed */
    private ReturnAction onLeftPressedAction;

    /** Action gets executed every time the mouse is inside the component (no conditions) */
    private MouseAction mouseAction;

    /** true if mouse is inside the component*/
    private boolean mouseInside;

    /**
     * true if right mouse button was pressed on last frame except it has been pressed
     * since it entered the component
     */
    private boolean wasRightPressedOnLastFrame;

    /**
     * true if left mouse button was pressed on last frame except it has been pressed
     * since it entered the component
     */
    private boolean wasLeftPressedOnLastFrame;

    /** true if right mouse button has been pressed since the mouse entered the component */
    private boolean rightPressedSinceEntered;

    /** true if left mouse button has been pressed since the mouse entered the component */
    private boolean leftPressedSinceEntered;

    /** timer to distinguish pressed and clicked event of the right mouse button*/
    private int leftButtonTimer;

    /** timer to distinguish pressed and clicked event of the left mouse button*/
    private int rightButtonTimer;

    /**
     * limit for click event (if the button time is still smaller than this number
     * when it the button is released a click event is performed, otherwise a pressed
     * event; unit is update frames)
     */
    @SuppressWarnings("FieldCanBeLocal")
    private int pressedTime = 30;

    /**
     * if this value is set to true a drag event can only ba started from this component
     * if the mouse has been pressed until the specific button timer is over the pressedTime
     * value
     */
    @SuppressWarnings("unused")
    private boolean startDragOnlyOnPressed;

    /**
     * action, executed the fame, the right mouse button has been pressed long enough for it to
     * call the onRightPressedAction - when the rightButtonTimer equals pressedTime
     */
    private ReturnAction onRightPressStarted;

    /**
     * action, executed the fame, the left mouse button has been pressed long enough for it to
     * call the onLeftPressedAction - when the leftButtonTimer equals pressedTime
     */
    private ReturnAction onLeftPressStarted;

    /**
     * action, executed the fame, the left mouse button was pressed down on this component
     */
    private ReturnAction onLeftClickStarted;

    /**
     * action, executed the fame, the right mouse button was pressed down on this component
     */
    private ReturnAction onRightClickStarted;

    /**
     * action, gets executed when a drag event for the right Button starts in this component
     */
    private ReturnAction onRightDragStarted;

    /**
     * action, gets executed when a drag event for the left Button starts in this component
     */
    private ReturnAction onLeftDragStarted;

    /**
     * saves the last mouse Position to determine whether a drag event should start
     */
    @SuppressWarnings("unused")
    private Vector2f lastMousePosition;

    /**
     * true for the frame the mouse entered the component
     * used to prevent unwanted calls of mouseEnteredAction
     * and mouseExitedAction when the mouse for e.g is still
     * inside this component but just changed the child component
     * its pointing at
     */
    protected boolean stillInside;

    /** Constraint to determine x Position */
    private PositionConstraint xPosition;

    /** Constraint to determine y Position */
    private PositionConstraint yPosition;

    /** Constraint to determine component width */
    private SizeConstraint width;

    /** Constraint to determine component height */
    private SizeConstraint height;

    /** color used to identify component after rendering */
    private int id;

    /**Parent of that contains this component,set in add method */
    private ContentComponent parent;

    public SubComponent() {
        this(new RelativeToWindowPosition(0.5f),new RelativeToWindowPosition(0.5f),new RelativeToWindowSize(0.5f),new RelativeToWindowSize(0.5f));
    }

    public SubComponent(PositionConstraint xPosition, PositionConstraint yPosition, SizeConstraint width, SizeConstraint height) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        lastMousePosition = new Vector2f();
    }

    /**
     * returns the content component which is the parent of this component
     * @return parent content component
     */
    public ContentComponent getParent() {
        return parent;
    }

    /**
     * sets an new parent ContentComponent
     * @param parent new parent
     */
    @SuppressWarnings("WeakerAccess")
    public void setParent(ContentComponent parent) {
        this.parent = parent;
    }

    /**
     * adds a sub component to the content list of this component
     * calls super method and addToScene method which updates the
     * scenes hash map and the components id
     * @param component to add
     * @see ContentComponent
     */
    @Override
    public void addComponent(SubComponent component) {
        super.addComponent(component);
        addToScene(getSceneComponent());
    }

    /**
     * removes a component from this components content and removes
     * everything that is no longer visible from the scenes hash map
     * for mouse picking
     *
     * @param subComponent to be removed
     */
    @Override
    public void removeComponent(SubComponent subComponent) {
        super.removeComponent(subComponent);
        SceneComponent sc = getSceneComponent();
        if(sc != null) {
            subComponent.removeFromScene(sc);
            sc.cleanupIds();
        }
    }

    /**
     * used to recursively get the scene component of the components scene
     *
     * @return scene component or null
     */
    @Override
    public SceneComponent getSceneComponent() {
        if(parent != null) {
            return parent.getSceneComponent();
        } else {
            return null;
        }
    }

    /**
     * called if the mouse enters the component or one of its parents
     */
    @Override
    public void mouseEnteredRecursiveSave() {
        stillInside = true;
        parent.mouseEnteredRecursiveSave();
    }

    /**
     * called if the mouse exits the component or one of its parents
     */
    @Override
    public void mouseExitedRecursiveSave() {
        stillInside = false;
        parent.mouseExitedRecursiveSave();
    }

    /**
     * method gets executed if the mouse exits the component or one of its children
     *
     * @param mouseInput tracks mouse actions
     */
    @Override
    protected void mouseExited(MouseInput mouseInput) {

        /*
        Prevents the action from being executed although the mouse just leaves one
        of its child components and not the component itself
         */



        if(!stillInside) {

            if (mouseInside) {
                mouseInside = false;
                boolean consumed = false;
                if (mouseExitedAction != null) {
                    consumed = mouseExitedAction.action(mouseInput);
                }
                if(!consumed) {
                    parent.mouseExited(mouseInput);
                }
                rightPressedSinceEntered = false;
                leftPressedSinceEntered = false;
                wasRightPressedOnLastFrame = false;
                wasLeftPressedOnLastFrame = false;
            }
        }
    }

    /**
     * method gets executed every time the mouse points on a different component, that is this component or one
     * of its children, than in the frame before
     *
     * @param mouseInput tracks mouse actions
     */
    @Override
    protected void mouseEntered(MouseInput mouseInput) {



        if(!mouseInside) {
            mouseInside = true;
            boolean consumed = false;




            rightPressedSinceEntered = mouseInput.isRightButtonPressed();
            leftPressedSinceEntered = mouseInput.isLeftButtonPressed();

            if (mouseEnteredAction != null) {
                consumed = mouseEnteredAction.action(mouseInput);
            }
            if(!consumed) {
                parent.mouseEntered(mouseInput);
            }
        }
    }

    /**
     * mouse action called by the scene component of this component
     *
     * method gets called every frame the mouse is inside this component
     *
     * @param mouseInput tracks mouse events
     */
    @Override
    protected void mouseActionStart(MouseInput mouseInput) {
        /*
        Detects the first release of the right mouse button if it
        has been pressed since it entered the component
         */
        if(rightPressedSinceEntered && !mouseInput.isRightButtonPressed()) {
            rightPressedSinceEntered = false;
            rightDragReleasedAction();
            hud.dropRightDragEvent();
        }

        /*
        Detects the first release of the left mouse button if it
        has been pressed since it entered the component
         */
        if (leftPressedSinceEntered && !mouseInput.isLeftButtonPressed()) {
            leftPressedSinceEntered = false;
            leftDragReleasedAction();
            hud.dropLeftDragEvent();
        }

        /*
        Sets values if right button has been pressed
         */
        if(mouseInput.isLeftButtonPressed() && !leftPressedSinceEntered) {
            if(!wasLeftPressedOnLastFrame) {
                leftClickStartedAction();
            }
            wasLeftPressedOnLastFrame = true;
            leftButtonTimer = 0;
        }

        /*
        Sets values if left button has been pressed
         */
        if(mouseInput.isRightButtonPressed() && !rightPressedSinceEntered) {
            if(!wasRightPressedOnLastFrame) {
                rightClickStartedAction();
            }
            wasRightPressedOnLastFrame = true;
            rightButtonTimer = 0;
        }

        /*
        further mouse actions
         */
        mouseActionExecution(mouseInput);
    }

    /**
     * handles the mouse event
     * manages the callings of the mouse actions
     *
     * @param mouseInput tracks mouse actions
     */
    private void mouseActionExecution(MouseInput mouseInput) {

        stillInside = false;

        /*
        updates the timer for the right mouse button if its pressed
        and checks if a right button drag event should start
         */
        if(wasRightPressedOnLastFrame) {
            rightButtonTimer++;

            if(checkForDrag(mouseInput)) {
                if(startDragOnlyOnPressed) {
                    if(rightButtonTimer >= pressedTime) {
                        startRightDrag();
                    }
                } else {
                    startRightDrag();
                }
            }
        }

        /*
        updates the timer for the left mouse button if its pressed
        and checks if a left button drag event should start
         */
        if(wasLeftPressedOnLastFrame) {
            leftButtonTimer++;

            if(checkForDrag(mouseInput)) {
                if(startDragOnlyOnPressed) {
                    if(leftButtonTimer >= pressedTime) {
                        startLeftDrag();
                    }
                } else {
                    startLeftDrag();
                }
            }
        }


        lastMousePosition = mouseInput.getRelativePos();



        if(rightButtonTimer == pressedTime) {
            rightPressStartedAction();
        }

        if(leftButtonTimer == pressedTime) {
            leftPressStartedAction();
        }

        /*
        gets executed when the mouse button (right or left) was released
        (except the button has been pressed since the mouse entered the
        component)
         */
        if(!mouseInput.isLeftButtonPressed() && wasLeftPressedOnLastFrame) {

            wasLeftPressedOnLastFrame = false;
            if(isSelectable()) {
                hud.setCurrentKeyInputTarget(this);
            }
            if(hud.getLeftDragEvent() != null && leftDragReleasedAction()) {
                hud.dropLeftDragEvent();
            } else {
                hud.dropLeftDragEvent();
                if(leftButtonTimer < pressedTime) {
                    leftClickAction(mouseInput);
                } else {
                    leftPressedAction();
                }
            }
        } else if(!mouseInput.isRightButtonPressed() && wasRightPressedOnLastFrame) {
            wasRightPressedOnLastFrame = false;
            if(hud.getRightDragEvent() != null && rightDragReleasedAction()) {
                hud.dropRightDragEvent();
            } else {
                hud.dropRightDragEvent();
                if (rightButtonTimer < pressedTime) {
                    rightClickAction(mouseInput);
                } else {
                    rightPressedAction();
                }
            }
        }

        mouseAction(mouseInput);
    }

    /**
     * general mouse action method used to customize iwn behavior to mouse
     * events
     *
     * @param mouseInput mouse data
     */
    protected void mouseAction(MouseInput mouseInput) {
        if(mouseAction == null) {
            parent.mouseAction(mouseInput);
        } else {
            if(!mouseAction.action(mouseInput)) {
                parent.mouseAction(mouseInput);
            }
        }
    }

    /**
     * check if mouse has moved by comparing the current mouse position
     * with lastMousePosition which was updated the frame before
     *
     * @param mouseInput used to get current mouse position
     */
    private boolean checkForDrag(MouseInput mouseInput) {

        return mouseInput.getDisplVec().x != 0 || mouseInput.getDisplVec().y != 0;
    }

    /**
     * gets called when a right drag event should start
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     */
    protected void startRightDrag() {

        if(onRightDragStarted != null && hud.getRightDragEvent() == null) {
            if(!onRightDragStarted.execute()) {
                parent.startRightDrag();
            }
        } else if(hud.getRightDragEvent() == null) {
            parent.startRightDrag();
        }
    }

    /**
     * gets called when a left drag event should start
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     */
    protected void startLeftDrag() {
        if(onLeftDragStarted != null && hud.getLeftDragEvent() == null) {
            if(!onLeftDragStarted.execute()) {
                parent.startLeftDrag();
            }
        } else if(hud.getLeftDragEvent() == null) {
            parent.startLeftDrag();
        }
    }

    /**
     * gets called when the right mouse button is released
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     * @return if true event got consumed
     */
    protected boolean rightDragReleasedAction() {

        if(rightDragEnterRelease != null) {
            if(!rightDragEnterRelease.action(hud.getRightDragEvent())) {
                return parent.rightDragReleasedAction();
            } else {
                return true;
            }
        } else {
            return parent.rightDragReleasedAction();
        }

    }

    /**
     * gets called when the left mouse button is released
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     * @return if true, event got consumed
     */
    protected boolean leftDragReleasedAction() {

        if(leftDragEnterRelease != null) {
            if(!leftDragEnterRelease.action(hud.getLeftDragEvent())) {
                return parent.leftDragReleasedAction();
            } else {
                return true;
            }
        } else {
            return parent.leftDragReleasedAction();
        }

    }

    /**
     * gets called when the right button was pressed down in this component
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     */
    protected void rightClickStartedAction() {
        if(onRightClickStarted == null) {
            parent.rightClickStartedAction();
        } else {
            if(!onRightClickStarted.execute()) {
                parent.rightClickStartedAction();
            }
        }
    }

    /**
     * gets called when the left button was pressed down in this component
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     */
    protected void leftClickStartedAction() {
        if(onLeftClickStarted == null) {
            parent.leftClickStartedAction();
        } else {
            if(!onLeftClickStarted.execute()) {
                parent.leftClickStartedAction();
            }
        }
    }

    /**
     * gets called when the right button has been held for the
     * press time interval
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     */
    protected void rightPressStartedAction() {
        if(onRightPressStarted == null) {
            parent.rightPressStartedAction();
        } else {
            if(!onRightPressStarted.execute()) {
                parent.rightPressStartedAction();
            }
        }
    }

    /**
     * gets called when the left button has been hold for the
     * press time interval
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     */
    protected void leftPressStartedAction() {
        if(onLeftPressStarted == null) {
            parent.leftPressStartedAction();
        } else {
            if(!onLeftPressStarted.execute()) {
                parent.leftPressStartedAction();
            }
        }
    }

    /**
     * gets called when component gets a right press
     *
     * if the event gets not consumed (rightPress returns false or is null)
     * it first causes a click event and than gets passed to the parent
     */
    protected void rightPressedAction() {

        if(onRightPressedAction == null) {
            if(onRightClickAction == null) {
                parent.rightPressedAction();
            } else {
                if(!onRightClickAction.execute()) {
                    parent.rightPressedAction();
                }
            }
        } else {
            if(!onRightPressedAction.execute()) {
                if(onRightClickAction == null) {
                    parent.rightPressedAction();
                } else {
                    if(!onRightClickAction.execute()) {
                        parent.rightPressedAction();
                    }
                }
            }
        }
    }

    /**
     * gets called when component gets a left press
     *
     * if the event gets not consumed (rightPress returns false or is null)
     * it first causes a click event and than gets passed to the parent
     */
    public void leftPressedAction() {

        if(onLeftPressedAction == null) {
            if(onLeftClickAction == null) {
                parent.leftPressedAction();
            } else {
                if(!onLeftClickAction.execute()) {
                    parent.leftPressedAction();
                }
            }
        } else {
            if(!onLeftPressedAction.execute()) {
                if(onLeftClickAction == null) {
                    parent.leftPressedAction();
                } else {
                    if(!onLeftClickAction.execute()) {
                        parent.leftPressedAction();
                    }
                }
            }
        }
    }

    /**
     * gets called when component gets a right click
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     */
    protected void rightClickAction(MouseInput mouseInput) {
        if(onRightClickAction == null) {
            parent.rightClickAction(mouseInput);
        } else {
            if(!onRightClickAction.execute()) {
                parent.rightClickAction(mouseInput);
            }
        }
    }

    /**
     * gets called when component gets a left click
     *
     * if there is no action on this component or the event does not
     * get consumed it is passed to the parent
     */
    public void leftClickAction(MouseInput mouseInput) {



        if(onLeftClickAction == null) {
            parent.leftClickAction(mouseInput);
        } else {
            if(!onLeftClickAction.execute()) {
                parent.leftClickAction(mouseInput);
            }
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    /**
     * updates the components position and size and its children
     */
    public void updateBounds() {
        if(window != null) {
            onScreenHeight = height.getValue(this, SizeConstraint.Direction.HEIGHT);
            onScreenWidth = width.getValue(this, SizeConstraint.Direction.WIDTH);
            onScreenXPosition = xPosition.getValue(this, PositionConstraint.Direction.X);
            onScreenYPosition = yPosition.getValue(this, PositionConstraint.Direction.Y);
        }
        super.updateBounds();
    }

    /**
     * if a scene component can be found by the recursive call it is used to create a new id for this component,
     * to add this component to the scene components hash map and to do the same with the child components of this
     * component
     * @param sceneComponent scene component if this component is already a child of one, otherwise null
     */
    void addToScene(SceneComponent sceneComponent) {
        if(sceneComponent != null) {
            setId(sceneComponent.getNextId());
            sceneComponent.addSubComponent(this);

            content.forEach(subComponent -> subComponent.addToScene(sceneComponent));
        }
    }

    void removeFromScene(SceneComponent sceneComponent) {
        sceneComponent.removeSubComponent(id);
        content.forEach(subComponent -> subComponent.removeFromScene(sceneComponent));
    }

    /**
     * returns the id of this component which has the same value as the depth buffer at the pixels where this component
     * is drawn at the top with an alpha value greater than 0
     * @return components id
     */
    public Integer getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    /**
     * used to change the value of the constraint
     * used for animations (movement/resizing)
     */

    @SuppressWarnings("unused")
    public void changeHeightValue(float value) {
        height.changeValue(value);
        updateBounds();
    }

    public void changeWidthValue(float value) {
        width.changeValue(value);
        updateBounds();
    }


    @SuppressWarnings("unused")
    public void changeXValue(float value) {
        xPosition.changeValue(value);
        updateBounds();
    }

    @SuppressWarnings("unused")
    public void changeYValue(float value) {
        yPosition.changeValue(value);
        updateBounds();
    }

    /**
     * setter for default constraints
     */

    public void setxPositionConstraint(float xPosition) {
        this.xPosition = new RelativeInParent(xPosition);
    }

    public void setyPositionConstraint(float yPosition) {
        this.yPosition = new RelativeInParent(yPosition);
    }

    public void setWidthConstraint(float width) {
        this.width = new RelativeToParentSize(width);
    }

    public void setHeightConstraint(float height) {
        this.height = new RelativeToParentSize(height);
    }

    /**
     * setter for constraints
     */

    public void setxPositionConstraint(PositionConstraint xPosition) {
        this.xPosition = xPosition;
    }

    public void setyPositionConstraint(PositionConstraint yPosition) {
        this.yPosition = yPosition;
    }

    public void setWidthConstraint(SizeConstraint width) {
        this.width = width;
    }

    public void setHeightConstraint(SizeConstraint height) {
        this.height = height;
    }

    /**
     * setter for mouse actions
     */

    @SuppressWarnings("unused")
    public void setMouseEnteredAction(MouseAction mouseEnteredAction) {
        this.mouseEnteredAction = mouseEnteredAction;
    }

    @SuppressWarnings("unused")
    public void setMouseExitedAction(MouseAction mouseExitedAction) {
        this.mouseExitedAction = mouseExitedAction;
    }

    @SuppressWarnings("unused")
    public void setOnRightClickAction(ReturnAction onRightClickAction) {
        this.onRightClickAction = onRightClickAction;
    }

    public void setOnLeftClickAction(ReturnAction onLeftClickAction) {
        this.onLeftClickAction = onLeftClickAction;
    }

    @SuppressWarnings("unused")
    public void setMouseAction(MouseAction mouseAction) {
        this.mouseAction = mouseAction;
    }

    @SuppressWarnings("unused")
    public void setRightDragEnterRelease(DragAction rightDragEnterRelease) {
        this.rightDragEnterRelease = rightDragEnterRelease;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void setLeftDragEnterRelease(DragAction leftDragEnterRelease) {
        this.leftDragEnterRelease = leftDragEnterRelease;
    }

    @SuppressWarnings("unused")
    public void setOnRightPressedAction(ReturnAction onRightPressedAction) {
        this.onRightPressedAction = onRightPressedAction;
    }

    @SuppressWarnings("unused")
    public void setOnLeftPressedAction(ReturnAction onLeftPressedAction) {
        this.onLeftPressedAction = onLeftPressedAction;
    }

    @SuppressWarnings("unused")
    public void setOnRightPressStarted(ReturnAction onRightPressStarted) {
        this.onRightPressStarted = onRightPressStarted;
    }

    @SuppressWarnings("unused")
    public void setOnLeftPressStarted(ReturnAction onLeftPressStarted) {
        this.onLeftPressStarted = onLeftPressStarted;
    }

    @SuppressWarnings("unused")
    public void setOnRightDragStarted(ReturnAction onRightDragStarted) {
        this.onRightDragStarted = onRightDragStarted;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void setOnLeftDragStarted(ReturnAction onLeftDragStarted) {
        this.onLeftDragStarted = onLeftDragStarted;
    }

    public void setOnLeftClickStarted(ReturnAction onLeftClickStarted) {
        this.onLeftClickStarted = onLeftClickStarted;
    }

    public void setOnRightClickStarted(ReturnAction onRightClickStarted) {
        this.onRightClickStarted = onRightClickStarted;
    }



    /**
     *  getter for bounds
     *
     * @return values
     */

    @SuppressWarnings("unused")
    public PositionConstraint getxPosition() {
        return xPosition;
    }

    @SuppressWarnings("unused")
    public PositionConstraint getyPosition() {
        return yPosition;
    }

    public SizeConstraint getWidth() {
        return width;
    }

    public SizeConstraint getHeight() {
        return height;
    }
}
