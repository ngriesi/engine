package engine.hud.animations;

import engine.hud.Hud;

/**
 * class used to animate a linear transition of a <code>Float</code> object
 */
@SuppressWarnings("unused")
public class FloatAnimation extends Animation<Float> {

    /**
     * constructor sets duration,hud,start and end value and the action of the animation
     *
     * @param hud        that uses the animation
     * @param duration   of the animation
     * @param startValue start value of the animation
     * @param endValue   of the animation
     * @param action     animation action
     */
    public FloatAnimation(Hud hud, int duration, float startValue, float endValue, AnimationAction<Float> action) {
        super(hud, duration, startValue, endValue, action);
    }

    /**
     * calculates the size of the step performed every frame
     *
     * @return the new step size
     */
    @Override
    protected Float calculateStep() {
        return (endValue - startValue)/duration;
    }

    /**
     * action performed every frame the animation is running
     * (is part of the animations list in <class>hud</class>)
     *
     * @see Hud
     */
    @Override
    public void makeStep() {

        //checks if value should decrease or increase
        if(endValue - startValue > 0) {

            //increasing

            if(progress<endValue) {
                //sets new value
                action.execute(action.getProgress(component) + step,component);
                progress = action.getProgress(component);
            } else {
                //sets end value and ends animation
                action.execute(endValue,component);
                endAnimation();
            }
        }else {

            //decreasing

            if(progress>endValue) {
                //sets new value
                action.execute(action.getProgress(component) + step,component);
                progress  = action.getProgress(component);
            } else {
                //sets end value and ends animation
                action.execute(endValue,component);
                endAnimation();
            }
        }
    }

    /**
     * creates an independent copy of the animation
     *
     * @return copy of the animation
     */
    @Override
    public Animation<Float> copy() {
        return new FloatAnimation(hud,duration,startValue,endValue,action);
    }
}
