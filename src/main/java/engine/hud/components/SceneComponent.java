package engine.hud.components;

import engine.hud.mouse.MouseInput;
import engine.general.Transformation;
import engine.general.Window;
import engine.hud.HudShaderManager;
import org.joml.Matrix4f;

import java.util.HashMap;

import static engine.general.GameEngine.pTime;

public class SceneComponent extends ContentComponent {

    /** id for given to last added content component */
    private int lastId = 0;

    /** id of currently selected component */
    private int currentId = 0;

    /**content of the scene and its ids */
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
     * @param ortho transformation matrix
     * @param transformation class
     * @param shaderManager of the Hud
     */
    public void render(Matrix4f ortho, Transformation transformation, HudShaderManager shaderManager) {

        shaderManager.setShaderProgram(shaderManager.getMaskShader());

        super.renderNext(ortho,transformation,shaderManager);
        if(renderedOnTop != null) {

            renderedOnTop.renderComponent(ortho,transformation,shaderManager);
        }
        hud.renderDragEvents(ortho,transformation,shaderManager);
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

        if(mouseInput.isInWindow()) {



           SceneComponent.this.currentId = hud.getPixelColor((int) mouseInput.getCurrentPos().x, (int) (window.getHeight() - mouseInput.getCurrentPos().y));

            pTime("input read");



            currentComponent = subComponents.get(currentId);

            if(currentId != 0) {
                //System.out.println(currentId);
            }


        } else {
            currentComponent = null;
            window.forceEvents();

        }




        if(currentComponent != null && lastComponent != null) {

            if (!lastComponent.equals(currentComponent)) {


                lastComponent.getMouseListener().mouseExitedRecursiveSave();
                pTime("input 2");
                currentComponent.getMouseListener().mouseEnteredRecursiveSave();
                pTime("input 3");
                lastComponent.getMouseListener().mouseExited(mouseInput);
                pTime("input 4");
                currentComponent.getMouseListener().mouseEntered(mouseInput);
                pTime("input 5");
                lastComponent = currentComponent;
            }
            currentComponent.getMouseListener().mouseActionStart(mouseInput);
            pTime("input 6");
        } else if(currentComponent == null && lastComponent != null) {
            lastComponent.getMouseListener().mouseExitedRecursiveSave();
            lastComponent.getMouseListener().mouseExited(mouseInput);
            lastComponent = null;
            System.out.println("test");

        } else if(currentComponent != null) {

            currentComponent.getMouseListener().mouseEnteredRecursiveSave();
            currentComponent.getMouseListener().mouseEntered(mouseInput);
            lastComponent = currentComponent;
            currentComponent.getMouseListener().mouseActionStart(mouseInput);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }


    public void setRenderedOnTop(SubComponent renderedOnTop) {
        this.renderedOnTop = renderedOnTop;
    }

    public SubComponent getRenderedOnTop() {
        return renderedOnTop;
    }

}
