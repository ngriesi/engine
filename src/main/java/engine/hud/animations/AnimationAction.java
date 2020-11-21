package engine.hud.animations;

import engine.hud.components.ContentComponent;

/**
 * Animation action interface, used to apply the changes made by an animation per frame to
 * the component the attribute that gets changed belongs to
 *
 * @param <T> type of the attribute that gets changed
 */
public interface AnimationAction<T> {

    /**
     * sets the attribute value at the target component ot the next value of the animation
     *
     * @param nextValue next value of the animation
     * @param component component of the animation
     */
    void execute(T nextValue, ContentComponent component);

    /**
     * returns the progress of the animation from the component to the animation
     *
     * @param component component of the animation
     * @return current animation progress
     */
    T getProgress(ContentComponent component);
}
