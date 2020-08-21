package engine.hud.animations;

import engine.hud.Hud;
import org.joml.Vector2f;

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

    @Override
    protected Vector2f calculateStep() {
        Vector2f result = new Vector2f();
        result.x = (endValue.x - startValue.x)/duration;
        result.y = (endValue.y - startValue.y)/duration;
        return result;
    }

    @Override
    public void makeStep() {


        Vector2f mov  = new Vector2f(endValue);
        mov.sub(startValue);
        Vector2f prog = new Vector2f(progress);
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
        return new Vector2fAnimation(hud,duration,new Vector2f(startValue),new Vector2f(endValue),action);
    }
}
