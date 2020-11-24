package engine.hud.components;

import engine.general.Transformation;
import engine.general.Window;
import engine.hud.Hud;
import engine.hud.HudShaderManager;
import engine.hud.actions.Action;
import engine.hud.keys.KeyListener;
import engine.hud.mouse.MouseAction;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseListener;
import org.joml.Matrix4f;

import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ContentComponent {

    /** stores components x Position of Component on screen (except for offset, the xOffset is applied after the calculation of onScreenXPosition) */
    float onScreenXPosition;

    /** stores components y Position of Component on screen (except for offset, the yOffset is applied after the calculation of onScreenYPosition) */
    float onScreenYPosition;

    /** stores the on Screen Height of the Component */
    float onScreenHeight;

    /** stores the on screen Width of the component */
    float onScreenWidth;

    /** offset on x Axis used to move the component; offset 1 means the component was moved its own width to the left */
    private float xOffset;

    /** offset on y Axis used to move the component; offset 1 means the component was moved its own height down */
    private float yOffset;

    /** window that contains the hud component */
    protected Window window;

    /** hud that contains the component */
    protected Hud hud;

    /** if false the component and its children are not drawn */
    private boolean visible;

    /** used to temporarily hide a component during its removal */
    private boolean removed;

    /** content of the hud component */
    protected CopyOnWriteArrayList<SubComponent> content;

    /** executed when component gets focus */
    private Action onFocusedAction;

    /** executed when component loses focus */
    private Action onFocusLostAction;

    /** executed when component gets input focus */
    private Action onInputFocusedAction;

    /** executed when component loses input focus */
    private Action onInputFocusLostAction;

    /** if true the component can be in focus */
    private boolean focusable = true;

    /** if true the component can get input focus */
    private boolean inputFocusable = true;

    /** id of the component */
    protected int id;

    /**Mouse listener for the component */
    private MouseListener mouseListener;

    /** Key listener for the component */
    private KeyListener keyListener;

    /**
     * constructor creates list object
     */
    ContentComponent() {
        content = new CopyOnWriteArrayList<>();
        visible = true;
        keyListener = new KeyListener();
        mouseListener = new MouseListener(this);
        mouseListener.addLeftButtonAction(new MouseAction() {
            @Override
            public boolean action(MouseEvent e) {
                if(e.getEvent()== MouseEvent.Event.CLICK_RELEASED || e.getEvent() == MouseEvent.Event.PRESS_RELEASED) {
                    ContentComponent.this.focus();
                }
                return false;
            }
        });
    }


    /**
     * calculates the bounds of the component in the getValue() methods of the constraints and for all content components
     */
    public void updateBounds() {
        content.forEach(SubComponent::updateBounds);
    }


    /**
     * calls the render component method for all content components
     *
     * @param orthographic orthographic transformation
     * @param transformation transformation object
     * @param shaderManager shaderManager of the Hud
     */
    public void renderNext(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager,int level) {
        content.forEach(contentComponent -> {
            if(contentComponent.isVisible() && !contentComponent.isRemoved()) {
                contentComponent.renderComponent(orthographic, transformation, shaderManager,level);
            }
        });
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
    public void setWindow(Window window) {
        this.window = window;
        content.forEach(subComponent -> subComponent.setWindow(window));
    }

    /**
     * sets the hud for this component and all its child components
     *
     * @param hud that contains the component
     */
    public void setHud(Hud hud) {
        this.hud = hud;
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
     * calls all contents cleanup methods
     */
    public void cleanup() {
        content.forEach(SubComponent::cleanup);
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
        updateBounds();
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
        updateBounds();
    }

    public void focus() {
        if(focusable) {
            hud.setCurrentFocus(this);
            if(inputFocusable) {
                hud.setCurrentInputFocus(this);
            }
        }
    }

    public void receivedFocus() {
        if(onFocusedAction != null) {
            onFocusedAction.execute();
        }
    }

    public void lostFocus() {
        if(onFocusLostAction != null) {
            onFocusLostAction.execute();
        }
    }

    public void receivedInputFocus() {
        if(onInputFocusedAction != null) {
            onInputFocusedAction.execute();
        }
    }

    public void lostInputFocus() {
        if(onInputFocusLostAction != null) {
            onInputFocusLostAction.execute();
        }
    }

    public Action getOnFocusedAction() {
        return onFocusedAction;
    }

    public void setOnFocusedAction(Action onFocusedAction) {
        this.onFocusedAction = onFocusedAction;
    }

    public Action getOnFocusLostAction() {
        return onFocusLostAction;
    }

    public void setOnFocusLostAction(Action onFocusLostAction) {
        this.onFocusLostAction = onFocusLostAction;
    }

    public Action getOnInputFocusedAction() {
        return onInputFocusedAction;
    }

    public void setOnInputFocusedAction(Action onInputFocusedAction) {
        this.onInputFocusedAction = onInputFocusedAction;
    }

    public Action getOnInputFocusLostAction() {
        return onInputFocusLostAction;
    }

    public void setOnInputFocusLostAction(Action onInputFocusLostAction) {
        this.onInputFocusLostAction = onInputFocusLostAction;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isInputFocusable() {
        return inputFocusable;
    }

    public void setInputFocusable(boolean inputFocusable) {
        this.inputFocusable = inputFocusable;
    }

    public MouseListener getMouseListener() {
        return mouseListener;
    }

    public KeyListener getKeyListener() {
        return keyListener;
    }

    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public float getOnScreenXPosition() {
        return onScreenXPosition;
    }

    public float getOnScreenYPosition() {
        return onScreenYPosition;
    }

    public float getOnScreenHeight() {
        return onScreenHeight;
    }

    public float getOnScreenWidth() {
        return onScreenWidth;
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public Window getWindow() {
        return window;
    }

    public Hud getHud() {
        return hud;
    }

}
