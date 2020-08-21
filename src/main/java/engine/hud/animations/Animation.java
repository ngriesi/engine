package engine.hud.animations;

import engine.hud.Hud;
import engine.hud.actions.Action;
import engine.hud.components.Component;

public abstract class Animation <T> {

    /** hud that "contains" this animation */
    Hud hud;

    /** duration of the animation in update Frames */
    int duration;

    /** component that uses this animation */
    Component component;

    /** action which contains a method that gets executed when the animation is finished */
    @SuppressWarnings("unused")
    private Action onFinishedAction;

    /** start value of the animation */
    T startValue;

    /** end value of the animation */
    T endValue;

    /** progress of the animation */
    T progress;

    /** step made every update frame */
    T step;

    /** action, used by the make Step method to change the value of the component for the animation */
    AnimationAction<T> action;

    /**
     * constructor sets duration,hud,start and end value and the action of the animation
     *
     * @param hud that uses the animation
     * @param duration of the animation
     * @param startValue start value of the animation
     * @param endValue of the animation
     * @param action animation action
     */
    Animation(Hud hud, int duration, T startValue, T endValue, AnimationAction<T> action) {
        this.hud = hud;
        this.duration = duration;
        this.startValue = startValue;
        this.endValue = endValue;
        this.action = action;
        this.step = calculateStep();
    }

    /**
     * implemented in child classes to calculate the step with start and end value
     *
     * @return returns new step value
     */
    protected abstract T calculateStep();

    /**
     * implemented in child classes, contain the logic of making a step
     */
    public abstract void makeStep();

    /**
     * creates a copy of the current animation
     *
     * @return copy
     */
    public abstract Animation copy();

    /**
     * creates a new animation object containing the opposite of this by switching
     * start and end value
     *
     * @return the inverted animation
     */
    @SuppressWarnings("unused")
    public Animation getInverted() {
        Animation result = copy();
        result.endValue = startValue;
        result.startValue = endValue;
        result.calculateStep();
        return result;
    }

    /**
     * returns a new Instance of this animation made for the component passed
     *
     * @param component to use this animation on
     * @return Animation made for the component
     */
    public Animation createForComponent(Component component) {
        Animation result = copy();
        result.setComponent(component);
        return result;
    }

    /**
     * starts the animation by adding it to the huds list of animations that
     * should be started next update frame
     */
    public void startAnimation() {
        progress = action.getProgress(component);
        hud.addAnimation(this);
    }

    /**
     * ends the animation by adding it to the huds list of animations that
     * should be removed form the list of executing animations in the next
     * update frame
     * it also calls the execute method of the onFinished action if it has been
     * set
     */
    public void endAnimation() {
        hud.removeAnimation(this);
        if(onFinishedAction != null) {
            onFinishedAction.execute();
        }
    }

    /**
     * sets an new component and updates the progress by getting the value fro the component
     *
     * @param component new component
     */
    public void setComponent(Component component) {
        this.component = component;
        progress = action.getProgress(component);
    }

    public void setOnFinishedAction(Action onFinishedAction) {
        this.onFinishedAction = onFinishedAction;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        step = calculateStep();

    }
}
