package engine.hud.animations;

import engine.hud.Hud;
import org.joml.Vector4f;

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
    public Vector4fAnimation(Hud hud, int duration, Vector4f startValue, Vector4f endValue, AnimationAction<Vector4f> action) {
        super(hud, duration, new Vector4f(startValue), new Vector4f(endValue), action);
    }

    @Override
    protected Vector4f calculateStep() {
        Vector4f result = new Vector4f();
        result.x = (endValue.x - startValue.x)/duration;
        result.y = (endValue.y - startValue.y)/duration;
        result.z = (endValue.z - startValue.z)/duration;
        result.w = (endValue.w - startValue.w)/duration;
        return result;
    }

    @Override
    public void makeStep() {

        Vector4f mov  = new Vector4f(endValue);
        mov.sub(startValue);
        Vector4f prog = new Vector4f(progress);
        prog.sub(startValue);


        if(mov.length() > prog.length()) {

            action.execute(action.getProgress(component).add(step),component);
            progress.set(action.getProgress(component));

        } else {
            action.execute(endValue,component);
            endAnimation();
        }
    }

    @Override
    public Animation copy() {
        return new Vector4fAnimation(hud,duration,new Vector4f(startValue),new Vector4f(endValue),action);
    }
}
