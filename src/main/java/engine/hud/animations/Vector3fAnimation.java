package engine.hud.animations;

import engine.hud.Hud;
import org.joml.Vector3f;

@SuppressWarnings("unused")
public class Vector3fAnimation extends Animation<Vector3f> {


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
    public Vector3fAnimation(Hud hud, int duration, Vector3f startValue, Vector3f endValue, AnimationAction<Vector3f> action) {
        super(hud, duration, new Vector3f(startValue), new Vector3f(endValue), action);
    }

    @Override
    protected Vector3f calculateStep() {
        Vector3f result = new Vector3f();
        result.x = (endValue.x - startValue.x)/duration;
        result.y = (endValue.y - startValue.y)/duration;
        result.z = (endValue.z - startValue.z)/duration;
        return result;
    }

    @Override
    public void makeStep() {


        Vector3f mov  = new Vector3f(endValue);
        mov.sub(startValue);
        Vector3f prog = new Vector3f(progress);
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
        return new Vector3fAnimation(hud,duration,new Vector3f(startValue),new Vector3f(endValue),action);
    }
}
