package engine.hud.animations;

import engine.hud.Hud;
import org.joml.Vector2f;

/**
 * class used to animate a linear transition of a <code>Vector2f</code> object
 */
@SuppressWarnings("unused")
public class Vector2fAnimation extends Animation<Vector2f> {


    /**
     * constructor sets duration,hud,start and end value and the action of the animation
     *
     * @param hud        that uses the animation
     * @param duration   of the animation
     * @param startValue start value of the animation
     * @param endValue   of the animation
     * @param action     animation action
     */
    @SuppressWarnings("WeakerAccess")
    public Vector2fAnimation(Hud hud, int duration, Vector2f startValue, Vector2f endValue, AnimationAction<Vector2f> action) {
        super(hud, duration, new Vector2f(startValue), new Vector2f(endValue), action);
    }

    /**
     * calculates the size of the step performed every frame
     *
     * @return the new step size
     */
    @Override
    protected Vector2f calculateStep() {
        Vector2f result = new Vector2f();
        result.x = (endValue.x - startValue.x)/duration;
        result.y = (endValue.y - startValue.y)/duration;
        return result;
    }

    /**
     * action performed every frame the animation is running
     * (is part of the animations list in <class>hud</class>)
     *
     * @see Hud
     */
    @Override
    public void makeStep() {

        //copy end vector to keep its value
        Vector2f mov  = new Vector2f(endValue);
        //get vector form start to end7
        mov.sub(startValue);
        //copy progress vector to keep its value
        Vector2f progressTemp = new Vector2f(progress);
        //get vector from start to progress
        progressTemp.sub(startValue);

        //check if the vector from start to progress is still shorter than the one from start to end
        if(mov.length() > progressTemp.length()) {

            //set new component color
            action.execute(action.getProgress(component).add(step),component);
            progress.set(action.getProgress(component));

        } else {

            //set end value and end teh animation
            action.execute(endValue,component);
            endAnimation();
        }
    }

    /**
     * creates an independent copy of the animation
     *
     * @return copy of the animation
     */
    @Override
    public Animation<Vector2f> copy() {
        return new Vector2fAnimation(hud,duration,new Vector2f(startValue),new Vector2f(endValue),action);
    }
}
