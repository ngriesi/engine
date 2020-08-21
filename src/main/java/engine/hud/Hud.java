package engine.hud;

import engine.general.MouseInput;
import engine.general.Transformation;
import engine.general.Window;
import engine.hud.actions.Action;
import engine.hud.animations.Animation;
import engine.hud.animations.DefaultAnimations;
import engine.hud.components.ContentComponent;
import engine.hud.components.MainComponent;
import engine.hud.components.SceneComponent;
import engine.hud.components.SubComponent;
import engine.hud.constraints.positionConstraints.RelativeToWindowPosition;
import engine.hud.events.DragEvent;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Hud {

    /** window that uses this hud */
    private final Window window;

    /** main Component of the hud */
    private MainComponent mainComponent;

    /** if true the window re-renders its (hud) content every rendering frame */
    private boolean alwaysRender;

    /**
     * if alwaysRender is false needsRendering has to be true
     * for the hud to be re-rendered in the window
     */
    private boolean needsRendering;

    /** can be set true through any instance of hud, forces rendering on the next frame */
    private boolean needsNextRendering;

    /** list of animations to be added to animations in the next update frame */
    private List<Animation> newAnimations;

    /** animations that currently get executed */
    private List<Animation> animations;

    /** list of animations to be removed from animations in the next update frame */
    private List<Animation> finishedAnimations;

    /** Actions that get performed at the end of the frame */
    private CopyOnWriteArrayList<Action> endOfFrameActions;

    /** saves the component that currently gets all the key inputs */
    private ContentComponent currentKeyInputTarget;

    /** List of components that should get removed */
    private List<SubComponent> removedComponents;

    /** DragEvent for the left mouse button */
    private DragEvent leftDragEvent;

    /** DragEvent for the left mouse button */
    private DragEvent rightDragEvent;

    /** stores mask behaviour of the right drag event */
    private boolean rightDragEventMaskSave;

    /** stores mask behaviour of the left drag event */
    private boolean leftDragEventMaskSave;

    /** stores the last mouse position*/
    private Vector2f lastMousePositionRelative;

    /** actions from another thread / to be performed in the update cycle*/
    private List<Action> newActions;

    /** actions from another thread / to be performed in the update cycle*/
    private List<Action> actions;

    /**
     * sets window
     *
     * @param window window to use
     */
    public Hud(Window window) {
        this.window = window;
        mainComponent = new MainComponent(window,this);
        alwaysRender = window.isRenderAlways();
        newAnimations = new ArrayList<>();
        animations = new ArrayList<>();
        finishedAnimations = new ArrayList<>();
        endOfFrameActions = new CopyOnWriteArrayList<>();
        removedComponents = new ArrayList<>();
        newActions = new ArrayList<>();
        actions = new ArrayList<>();

        new DefaultAnimations(this);


    }

    /**
     * initialises Hud
     * @param window needed for mainComponent
     */

    public void init(Window window) {
        mainComponent = new MainComponent(window,this);
        mainComponent.setContent(new SceneComponent());
    }

    /**
     * updates the bounds of all components
     */
    public void updateBounds() {
        mainComponent.updateBounds();
    }

    /**
     * handles the mouse and key inputs
     *
     * @param window of the hud
     * @param mouseInput input
     */
    public void input(Window window, MouseInput mouseInput) {
        lastMousePositionRelative = mouseInput.getRelativePos();
        mainComponent.input(window,mouseInput);
        mainComponent.getContent().handleKeyInput(window);
        if(currentKeyInputTarget != null) {
            currentKeyInputTarget.handleKeyInput(window);
        }

        if(rightDragEvent != null && !mouseInput.isRightButtonPressed()) {
            dropRightDragEvent();
        }
        if(leftDragEvent != null && !mouseInput.isLeftButtonPressed()) {
            dropLeftDragEvent();
        }
    }

    /**
     * updates the animations and positions
     *
     * @param interval time since last update cycle
     */
    public void update(@SuppressWarnings("unused") float interval) {

        actions.addAll(newActions);
        newActions.clear();
        for(Action action : actions) {
            action.execute();
        }
        actions.clear();

        executeAnimations();

        updateDragEventPositions();

        needsRendering = checkForUpdate();

        for(Action action : endOfFrameActions) {
            action.execute();
        }
        endOfFrameActions.clear();


    }

    /**
     * updates the positions of the drag event visuals with the last stored mouse position
     */
    private void updateDragEventPositions() {
        if(rightDragEvent != null) {
            rightDragEvent.dragAction(lastMousePositionRelative);
            if(rightDragEvent.getDragVisual() != null) {
                rightDragEvent.getDragVisual().changeXValue(lastMousePositionRelative.x);
                rightDragEvent.getDragVisual().changeYValue(lastMousePositionRelative.y);
                rightDragEvent.getDragVisual().updateBounds();
            }
        }
        if(leftDragEvent != null) {
            leftDragEvent.dragAction(lastMousePositionRelative);
            if(leftDragEvent.getDragVisual() != null) {
                leftDragEvent.getDragVisual().changeXValue(lastMousePositionRelative.x);
                leftDragEvent.getDragVisual().changeYValue(lastMousePositionRelative.y);
                leftDragEvent.getDragVisual().updateBounds();
            }
        }
    }

    /**
     * checks if the window needs to be rendered in the next frame
     *
     * @return value for needsRendering
     */
    private boolean checkForUpdate() {

        if(!alwaysRender) {
            return (animations.size() > 0 || newAnimations.size() > 0 || finishedAnimations.size() > 0) ||
                    (window.getLastPressed() != 0) ||
                    (rightDragEvent != null || leftDragEvent != null) ||
                    needsNextRendering;
        }

        return false;
    }

    /**
     * starts hud rendering process in the main component
     *
     * @param ortho orthographic projection matrix
     * @param transformation transformation object
     * @param hudShaderProgram shader
     */
    public void render(Matrix4f ortho, Transformation transformation, ShaderProgram hudShaderProgram) {
        mainComponent.render(ortho,transformation,hudShaderProgram);
    }

    /**
     * adds all animations from newAnimations to animations
     * clears the newAnimations list
     *
     * for all animations in the animations list makeStep is called
     *
     * removes all animations in finished animations from animations
     * clears the finished animations list
     */
    private void executeAnimations() {



        for(Animation animation : finishedAnimations) {
            animations.remove(animation);
        }
        finishedAnimations.clear();
        animations.addAll(newAnimations);
        newAnimations.clear();
        for(Animation animation : animations) {
            animation.makeStep();
        }


    }

    /**
     * adds an animation to the list of new Animations
     *
     * @param animation animation to be added to the animations list
     */
    public void addAnimation(Animation animation) {
        newAnimations.add(animation);
    }

    /**
     * adds an animation to the list of finished Animations
     *
     * @param animation to be removed from the animations list
     */
    public void removeAnimation(Animation animation) {
        finishedAnimations.add(animation);
    }

    /**
     * sets the content of the main component
     * @param scene new hud-scene in the window
     */
    public void setScene(SceneComponent scene) {
        mainComponent.setContent(scene);
        needsNextRendering();
    }

    /**
     * @return main Component of the hud
     */
    @SuppressWarnings("unused")
    public MainComponent getMainComponent() {
        return mainComponent;
    }

    public boolean isNeedsRendering() {
        return needsRendering;
    }

    /**
     * sets the component that will get future key inputs next to the current
     * used scene component
     *
     * @param currentKeyInputTarget new component for key inputs
     */
    public void setCurrentKeyInputTarget(ContentComponent currentKeyInputTarget) {

        if(this.currentKeyInputTarget != null && !currentKeyInputTarget.equals(this.currentKeyInputTarget)) {
            this.currentKeyInputTarget.deselected();
        }
        this.currentKeyInputTarget = currentKeyInputTarget;
    }

    /**
     * if method gets called, the window will get rendered
     * in the next frame
     * (only useful if renderAlways is set to false in the window object)
     */
    public void needsNextRendering() {
        this.needsNextRendering = true;
    }

    /**
     * confirms that the hud got rendered so the needsNextRendering call
     * works with different update and render frame durations
     */
    public void wasRendered() {
        this.needsNextRendering = false;
    }

    /**
     * used to render the current visuals of the drag events
     *
     * @param ortho transformation matrix
     * @param transformation transformation object
     * @param hudShaderProgram shader of this hud
     */
    public void renderDragEvents(Matrix4f ortho, Transformation transformation,ShaderProgram hudShaderProgram) {
        if(rightDragEvent != null && rightDragEvent.getDragVisual() != null) {
            rightDragEvent.getDragVisual().renderComponent(ortho, transformation, hudShaderProgram, 1);
        }
        if(leftDragEvent != null && leftDragEvent.getDragVisual() != null) {
            leftDragEvent.getDragVisual().renderComponent(ortho,transformation,hudShaderProgram,1);
        }
    }

    /**
     * Methods that get executed if a drag event gets dropped (used or not used)
     */
    public void dropRightDragEvent() {
        if(rightDragEvent != null) {
            rightDragEvent.dropAction();
            if(rightDragEvent.getDragVisual() != null) {
                rightDragEvent.getDragVisual().setWriteToDepthBuffer(true);
                rightDragEvent.getDragVisual().setUseMask(rightDragEventMaskSave);
            }
            rightDragEvent = null;
            needsNextRendering();
        }
    }

    public void dropLeftDragEvent() {
        if(leftDragEvent != null) {
            leftDragEvent.dropAction();
            if(leftDragEvent.getDragVisual() != null) {
                leftDragEvent.getDragVisual().setWriteToDepthBuffer(true);
                leftDragEvent.getDragVisual().setUseMask(leftDragEventMaskSave);
            }
            leftDragEvent = null;
            needsNextRendering();
        }
    }

    /**
     * sets a new left drag event and sets the positions of its visuals
     *
     * @param leftDragEvent new left drag event
     */
    public void setLeftDragEvent(DragEvent leftDragEvent) {
        this.leftDragEvent = leftDragEvent;
        if(leftDragEvent != null && leftDragEvent.getDragVisual() != null) {
            leftDragEvent.getDragVisual().setWriteToDepthBuffer(false);
            leftDragEventMaskSave = leftDragEvent.getDragVisual().useMask();
            leftDragEvent.getDragVisual().setUseMask(false);
            leftDragEvent.getDragVisual().setxPositionConstraint(new RelativeToWindowPosition(lastMousePositionRelative.x));
            leftDragEvent.getDragVisual().setyPositionConstraint(new RelativeToWindowPosition(lastMousePositionRelative.y));
        }

    }

    /**
     * sets a new right drag event and sets the positions of its visuals
     *
     * @param rightDragEvent new left drag event
     */
    @SuppressWarnings("unused")
    public void setRightDragEvent(DragEvent rightDragEvent) {
        this.rightDragEvent = rightDragEvent;

        if(rightDragEvent != null && rightDragEvent.getDragVisual() != null) {
            rightDragEvent.getDragVisual().setWriteToDepthBuffer(false);
            rightDragEventMaskSave = rightDragEvent.getDragVisual().useMask();
            rightDragEvent.getDragVisual().setUseMask(false);
            rightDragEvent.getDragVisual().setxPositionConstraint(new RelativeToWindowPosition(lastMousePositionRelative.x));
            rightDragEvent.getDragVisual().setyPositionConstraint(new RelativeToWindowPosition(lastMousePositionRelative.y));
        }
    }

    /**
     * add new Action
     */
    public void addAction(Action action) {
        newActions.add(action);
    }


    public DragEvent getLeftDragEvent() {
        return leftDragEvent;
    }

    public DragEvent getRightDragEvent() {
        return rightDragEvent;
    }

    public void cleanup() {
        mainComponent.cleanup();
    }

    public Vector2f getLastMousePositionRelative() {
        return lastMousePositionRelative;
    }

    public Window getWindow() {
        return window;
    }

    public void addEndOFrameAction(Action action) {
        endOfFrameActions.add(action);
    }

    public void removeComponents() {
        for (SubComponent c : removedComponents) {
            c.getParent().removeComponent(c);
        }
        removedComponents.clear();
    }

    public void removeComponent(SubComponent component) {
        removedComponents.add(component);
    }

    public ContentComponent getCurrentKeyInputTarget() {
        return currentKeyInputTarget;
    }


}
