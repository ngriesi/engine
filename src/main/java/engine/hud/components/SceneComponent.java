package engine.hud.components;

import engine.general.GameEngine;
import engine.general.MouseInput;
import engine.general.Transformation;
import engine.general.Window;
import engine.graph.items.Mesh;
import engine.hud.HudShaderManager;
import engine.hud.assets.Quad;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.HashMap;

import static engine.general.GameEngine.pTime;
import static org.lwjgl.opengl.GL11.glColorMask;

public class SceneComponent extends ContentComponent {

    /** id for given to last added content component */
    private int lastId = 0;

    /** id of currently selected component */
    private int currentId = 0;

    /**content of the scene and its ids */
    private HashMap<Integer,SubComponent> subComponents;

    /**
     * main component witch is the only possible parent of this component
     */
    private MainComponent parent;

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
        setBeMask(false);
        setWriteToDepthBuffer(true);
        setOnScreenHeight(1);
        setOnScreenWidth(1);
        setOnScreenXPosition(0.5f);
        setOnScreenYPosition(0.5f);

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
     * returns the parent of this component
     * @return main component
     */
    @SuppressWarnings("unused")
    public MainComponent getParent() {
        return parent;
    }

    /**
     * sets the parent (main Component) of this scene
     * @param parent main component
     */
    @SuppressWarnings("WeakerAccess")
    public void setParent(MainComponent parent) {
        this.parent = parent;
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

            System.out.println(currentId);

        } else {
            currentComponent = null;
        }



        if(currentComponent != null && lastComponent != null) {

            if (!lastComponent.equals(currentComponent)) {


                lastComponent.mouseExitedRecursiveSave();
                pTime("input 2");
                currentComponent.mouseEnteredRecursiveSave();
                pTime("input 3");
                lastComponent.mouseExited(mouseInput);
                pTime("input 4");
                currentComponent.mouseEntered(mouseInput);
                pTime("input 5");
                lastComponent = currentComponent;
            }
            currentComponent.mouseActionStart(mouseInput);
            pTime("input 6");
        } else if(currentComponent == null && lastComponent != null) {
            lastComponent.mouseExitedRecursiveSave();
            lastComponent.mouseExited(mouseInput);
            lastComponent = null;

        } else if(currentComponent != null) {

            currentComponent.mouseEnteredRecursiveSave();
            currentComponent.mouseEntered(mouseInput);
            lastComponent = currentComponent;
            currentComponent.mouseActionStart(mouseInput);
        }
    }

    /**
     * Empty mouse action methods
     *
     * @see SubComponent for implementation
     * @see ContentComponent for abstract methods
     */

    @Override
    protected void mouseExited(MouseInput mouseInput) {}

    @Override
    protected void mouseEntered(MouseInput mouseInput) {}

    @Override
    protected void mouseActionStart(MouseInput mouseInput) {}

    @Override
    protected void mouseAction(MouseInput mouseInput) {

    }

    @Override
    protected void startRightDrag() {

    }

    @Override
    protected void startLeftDrag() {

    }

    @Override
    protected boolean rightDragReleasedAction() {

        return false;
    }

    @Override
    protected boolean leftDragReleasedAction() {

        return false;
    }

    @Override
    protected void rightClickStartedAction() {

    }

    @Override
    protected void leftClickStartedAction() {

    }

    @Override
    protected void rightPressStartedAction() {

    }

    @Override
    protected void leftPressStartedAction() {

    }

    @Override
    protected void rightPressedAction() {

    }

    @Override
    protected void leftPressedAction() {

    }

    @Override
    protected void rightClickAction(MouseInput mouseInput) {

    }

    @Override
    protected void leftClickAction(MouseInput mouseInput) {

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
