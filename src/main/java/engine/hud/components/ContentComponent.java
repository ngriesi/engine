package engine.hud.components;

import engine.general.MouseInput;
import engine.general.Transformation;
import engine.general.Window;
import engine.hud.Hud;
import engine.hud.actions.Action;
import engine.hud.actions.KeyAction;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ContentComponent extends Component {

    /* delay before the key spam starts (input frames) */
    @SuppressWarnings("WeakerAccess")
    public static final int START_PAUSE = 0;

    /* interval the key spam uses (input frames) */
    @SuppressWarnings("WeakerAccess")
    public static final int PRESSED_INTERVAL = 0;

    /** content of the hud component */
    protected CopyOnWriteArrayList<SubComponent> content;

    /* last used key */
    private int lastUsed;

    /* true if key start delay is over */
    private boolean pressed;

    /* determines if the spam of keys should be enabled */
    @SuppressWarnings("FieldCanBeLocal")
    private boolean useKeySpam = true;

    /* timer for multiple key actions of one key in a row (when pressed) */
    private int timer;

    @SuppressWarnings("unused")
    private KeyAction onKeyPressedAction;

    /** executed when component gets deselected */
    private Action deselectedAction;

    /** if true the component can be selected */
    private boolean selectable;

    /**
     * constructor creates list object
     */
    ContentComponent() {
        content = new CopyOnWriteArrayList<>();
        selectable = true;
    }

    /**
     * calculates the bounds of the component in the getValue() methods of the constraints and for all content components
     */
    protected void updateBounds() {
        content.forEach(SubComponent::updateBounds);
    }


    /**
     * calls the render component method for all content components
     *
     * @param ortho orthographic transformation
     * @param transformation transformation object
     * @param hudShaderProgram shader
     * @param level mask level
     */
    @Override
    public void renderNext(Matrix4f ortho, Transformation transformation, ShaderProgram hudShaderProgram, int level) {
        content.forEach(contentComponent -> {
            if(contentComponent.isVisible() && !contentComponent.isRemoved()) {
                contentComponent.renderComponent(ortho, transformation, hudShaderProgram, level);
            }
        });
    }

    /**
     * adds component to the content, sets components window and parents and calculates its bounds
     *
     * @param component to add
     */
    public void addComponent(SubComponent component) {
        content.add(component);
        component.setWindow(window);
        component.setHud(hud);
        component.setParent(this);
        component.updateBounds();
    }

    /**
     * removes a component form the content list and resets its window, hud and parent
     *
     * @param subComponent to be removed
     */

    public void removeComponent(SubComponent subComponent) {
        content.remove(subComponent);
        subComponent.setRemoved(false);
        subComponent.setWindow(null);
        subComponent.setHud(null);
        subComponent.setParent(null);
    }

    /**
     * removes a Component at the end of the next frame
     */
    public void saveRemoveComponent(SubComponent component) {
        component.setRemoved(true);
        if(hud != null) {
            hud.removeComponent(component);
        } else {
            removeComponent(component);
        }
    }



    /**
     * sets the window for this component and all its child components
     *
     * @param window that contains the hud
     */
    @Override
    public void setWindow(Window window) {
        super.setWindow(window);
        content.forEach(subComponent -> subComponent.setWindow(window));
    }

    /**
     * sets the hud for this component and all its child components
     *
     * @param hud that contains the component
     */
    @Override
    public void setHud(Hud hud) {
        super.setHud(hud);
        content.forEach(subComponent -> subComponent.setHud(hud));
    }

    /**
     * returns the content list
     * @return list of child components
     */
    @SuppressWarnings("unused")
    public CopyOnWriteArrayList<SubComponent> getContent() {
        return content;
    }

    /**
     * overwritten by scene component and sub component to recursively get the scene component used
     * @return scene component witch contains this component (or the itself if it is called in scene component)
     */
    public abstract SceneComponent getSceneComponent();

    /**
     * overwritten by subComponent
     *
     * @param mouseInput tracks mouse actions
     * @see SubComponent
     */
    protected abstract void mouseExited(MouseInput mouseInput);

    protected abstract void mouseEntered(MouseInput mouseInput);

    protected abstract void mouseActionStart(MouseInput mouseInput);

    /**
     * handles the key input
     *
     * @param window to get the last pressed key
     */
    public void handleKeyInput(Window window) {
        int key = window.getLastPressed();

        if(key!=0) {
            if(key != lastUsed || (useKeySpam && (!pressed && timer > START_PAUSE)) || (useKeySpam && (pressed && timer > PRESSED_INTERVAL))) {
                lastUsed = key;
                timer = 0;

                keyPressedAction(window.getLastPressed());

            } else {
                timer++;
                if(timer==START_PAUSE) {
                    pressed = true;
                }
            }
        } else {
            lastUsed = 0;
            timer = 0;
            pressed = false;
        }
    }

    protected void keyPressedAction(int keyCode) {
        if(onKeyPressedAction != null) {
            onKeyPressedAction.execute(keyCode);
        }
    }

    /**
     * calls all contents cleanup methods
     */
    public void cleanup() {
        content.forEach(SubComponent::cleanup);
    }

    /**
     * Methods to determine in which components the mouse currently is
     */
    public void mouseEnteredRecursiveSave(){}

    public void mouseExitedRecursiveSave(){}

    /**
     * abstract mouse action methods used in SubComponent
     *
     * @see SubComponent for implementation
     */

    protected abstract void mouseAction(MouseInput mouseInput);

    protected abstract void startRightDrag();

    protected abstract void startLeftDrag();

    protected abstract boolean rightDragReleasedAction();

    protected abstract boolean leftDragReleasedAction();

    protected abstract void rightClickStartedAction();

    protected abstract void leftClickStartedAction();

    protected abstract void rightPressStartedAction();

    protected abstract void leftPressStartedAction();

    protected abstract void rightPressedAction();

    protected abstract void leftPressedAction();

    protected abstract void rightClickAction(MouseInput mouseInput);

    protected abstract void leftClickAction(MouseInput mouseInput);

    public void setUseKeySpam(boolean useKeySpam) {
        this.useKeySpam = useKeySpam;
    }

    public void setOnKeyPressedAction(KeyAction onKeyPressedAction) {
        this.onKeyPressedAction = onKeyPressedAction;
    }

    @Override
    public void setxOffset(float xOffset) {
        super.setxOffset(xOffset);
        updateBounds();
    }

    @Override
    public void setyOffset(float yOffset) {
        super.setyOffset(yOffset);
        updateBounds();
    }

    public void deselected() {
        if(deselectedAction != null) {
            deselectedAction.execute();
        }
    }

    public void setDeselectedAction(Action deselectedAction) {
        this.deselectedAction = deselectedAction;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public void setWriteToDepthBuffer(boolean writeToDepthBuffer) {
        super.setWriteToDepthBuffer(writeToDepthBuffer);
        content.forEach(subComponent -> subComponent.setWriteToDepthBuffer(writeToDepthBuffer));
    }
}
