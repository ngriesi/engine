package engine.hud.animations;

import engine.hud.Hud;
import org.joml.Vector4f;

/**
 * class used to animate a linear transition of a <code>Vector4f</code> object
 */
@SuppressWarnings("unused")
public class Vector4fAnimation extends Animation<Vector4f> {


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
    public Vector4fAnimation(Hud hud, int duration, Vector4f startValue, Vector4f endValue, AnimationAction<Vector4f> action) {
        super(hud, duration, new Vector4f(startValue), new Vector4f(endValue), action);
    }

    /**
     * calculates the size of the step performed every frame
     *
     * @return the new step size
     */
    @Override
    protected Vector4f calculateStep() {
        Vector4f result = new Vector4f();
        result.x = (endValue.x - startValue.x)/duration;
        result.y = (endValue.y - startValue.y)/duration;
        result.z = (endValue.z - startValue.z)/duration;
        result.w = (endValue.w - startValue.w)/duration;
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
        Vector4f mov  = new Vector4f(endValue);
        //get vector form start to end7
        mov.sub(startValue);
        //copy progress vector to keep its value
        Vector4f progressTemp = new Vector4f(progress);
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
    public Animation<Vector4f> copy() {
        return new Vector4fAnimation(hud,duration,new Vector4f(startValue),new Vector4f(endValue),action);
    }
}
