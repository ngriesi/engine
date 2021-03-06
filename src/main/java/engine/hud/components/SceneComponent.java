package engine.hud.components;

import engine.general.Transformation;
import engine.general.Window;
import engine.hud.HudShaderManager;
import engine.hud.mouse.MouseInput;
import org.joml.Matrix4f;

import java.util.HashMap;

/**
 * topmost component containing all the other (Sub)Components of a hud
 *
 * starts the recursive mouse input handling. For this it stores a map
 * of all the SubComponents in the Scene with its ids.
 */
@SuppressWarnings("unused")
public class SceneComponent extends ContentComponent {

    /** id for given to last added content component */
    private int lastId = 0;

    /** id of currently selected component */
    private int currentId = 0;

    /** content of the scene and its ids */
    private HashMap<Integer,SubComponent> subComponents;

    /** last component that contained the mouse */
    private SubComponent lastComponent;

    /** component that is always rendered on top */
    private SubComponent renderedOnTop;

    /**
     * creates the hash map that contains the ids of the components and the components itself
     * the map is used by of the mouse picker
     */
    public SceneComponent() {
        subComponents = new HashMap<>();
        onScreenHeight = 1;
        onScreenWidth = 1;
        onScreenXPosition = 0.5f;
        onScreenYPosition = 0.5f;
    }

    /**
     * returns this scene component for the sub component which called this method to be used
     * @return this
     */
    @Override
    public SceneComponent getSceneComponent() {
        return this;
    }



    /**
     * starts the rendering for all components including the drag event visuals if existing
     *
     * @param orthographic transformation matrix
     * @param transformation class
     * @param shaderManager of the Hud
     */
    public void render(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager) {

        super.renderNext(orthographic,transformation,shaderManager,0);
        if(renderedOnTop != null) {

            renderedOnTop.renderComponent(orthographic,transformation,shaderManager,0);
        }
        hud.renderDragEvents(orthographic,transformation,shaderManager);
    }

    /**
     * returns the next free id (integer)
     * @return int id
     */
    int getNextId() {
        lastId+=1;
        return lastId;
    }



    /**
     * adds a sub component to the hash map for the mouse picking
     * @param subComponent added to subComponents hash map
     */
    void addSubComponent(SubComponent subComponent) {
        subComponents.put(subComponent.getId(),subComponent);
    }

    /**
     * removes a sub component from the hash map for mouse picking
     * @param id of the sub component that gets removed
     */
    void removeSubComponent(int id) {
        if(lastComponent != null && subComponents.get(id).equals(lastComponent)) lastComponent = null;
        subComponents.remove(id);

    }



    /**
     * starts the recursion which adds all the components to the
     * subComponents map for mouse picking
     *
     * @param component to add
     */
    @Override
    public void addComponent(SubComponent component) {
        super.addComponent(component);
        component.addToScene(this);
    }

    /**
     * removes the component from the scene and removes it and all its
     * children from the hash map for mouse picking
     *
     * @param subComponent to be removed
     */
    @Override
    public void removeComponent(SubComponent subComponent) {
        super.removeComponent(subComponent);
        subComponent.removeFromScene(this);
        cleanupIds();
    }

    /**
     * Method removes all gaps from the id system to prevent overflow
     */
    void cleanupIds() {
        HashMap<Integer,SubComponent> temp = new HashMap<>();
        int i = 0;
        for(SubComponent subComponent : subComponents.values()) {
            i++;
            temp.put(i,subComponent);
            subComponent.setId(i);
        }

        lastId = i+1;
        subComponents = temp;
    }


    /**
     * starts the input chain at the sub component that currently contains the mouse
     *
     * @param window window the mouse is inside
     * @param mouseInput mouse input to track mouse actions
     */
    public void input(Window window, MouseInput mouseInput) {
        SubComponent currentComponent;

        // finds the new component the mouse points at
        if(mouseInput.isInWindow()) {

           SceneComponent.this.currentId = hud.getPixelColor((int) mouseInput.getCurrentPos().x, (int) (window.getHeight() - mouseInput.getCurrentPos().y));
           currentComponent = subComponents.get(currentId);

        } else {
            currentComponent = null;
            window.forceEvents();
        }

        // calls the methods to perform the mouseEntered, mouseExited and the normal mouseAction
        if(currentComponent != null && lastComponent != null) {

            if (!lastComponent.equals(currentComponent)) {


                lastComponent.getMouseListener().mouseExitedRecursiveSave();
                currentComponent.getMouseListener().mouseEnteredRecursiveSave();
                lastComponent.getMouseListener().mouseExited(mouseInput);
                currentComponent.getMouseListener().mouseEntered(mouseInput);
                lastComponent = currentComponent;
            }
            currentComponent.getMouseListener().mouseActionStart(mouseInput);
        } else if(currentComponent == null && lastComponent != null) {
            lastComponent.getMouseListener().mouseExitedRecursiveSave();
            lastComponent.getMouseListener().mouseExited(mouseInput);
            lastComponent = null;

        } else if(currentComponent != null) {

            currentComponent.getMouseListener().mouseEnteredRecursiveSave();
            currentComponent.getMouseListener().mouseEntered(mouseInput);
            lastComponent = currentComponent;
            currentComponent.getMouseListener().mouseActionStart(mouseInput);
        }
    }

    /**
     * sets the focus and input focus to this SceneComponent
     */
    @Override
    public void focus() {
        hud.setCurrentFocus(this);
        hud.setCurrentInputFocus(this);
    }

    public void setRenderedOnTop(SubComponent renderedOnTop) {
        this.renderedOnTop = renderedOnTop;
    }

    public SubComponent getRenderedOnTop() {
        return renderedOnTop;
    }

    public SubComponent getCurrentMouseTarget() {
        return lastComponent;
    }
}
