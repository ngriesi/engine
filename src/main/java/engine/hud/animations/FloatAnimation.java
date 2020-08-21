package engine.hud.animations;

import engine.hud.Hud;

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

    @Override
    protected Float calculateStep() {
        return (endValue - startValue)/duration;
    }

    /**
     * execution of one animation step
     */
    @Override
    public void makeStep() {


        if(endValue - startValue > 0) {

            if(progress<endValue) {
                action.execute(action.getProgress(component) + step,component);
                progress = action.getProgress(component);
            } else {
                action.execute(endValue,component);
                endAnimation();
            }
        }else {

            if(progress>endValue) {
                action.execute(action.getProgress(component) + step,component);
                progress  = action.getProgress(component);
            } else {
                action.execute(endValue,component);
                endAnimation();
            }
        }
    }

    @Override
    public Animation copy() {
        return new FloatAnimation(hud,duration,startValue,endValue,action);
    }
}
