package engine.hud.animations;

import engine.hud.Hud;
import engine.hud.color.Color;
import org.joml.Vector4f;

public class ColorAnimation extends Animation<Color> {

    private Vector4f endSave;

    /**
     * constructor sets duration,hud,start and end value and the action of the animation
     *
     * @param hud        that uses the animation
     * @param duration   of the animation
     * @param startValue start value of the animation
     * @param endValue   of the animation
     * @param action     animation action
     */
    public ColorAnimation(Hud hud, int duration, Color startValue, Color endValue, AnimationAction<Color> action) {
        super(hud, duration, new Color(startValue), new Color(endValue), action);



    }

    @Override
    protected Color calculateStep() {

        Color result = new Color();
        result.setRed((endValue.getRed() - startValue.getRed())/duration);
        result.setGreen((endValue.getGreen() - startValue.getGreen())/duration);
        result.setBlue((endValue.getBlue() - startValue.getBlue())/duration);
        result.setAlpha((endValue.getAlpha() - startValue.getAlpha())/duration);
        return result;
    }

    @Override
    public void makeStep() {

        Vector4f mov  = new Vector4f(endValue.getVector4f());
        mov.sub(startValue.getVector4f());
        Vector4f prog = new Vector4f(progress.getVector4f());
        prog.sub(startValue.getVector4f());


        if(mov.length() > prog.length()) {

            action.execute(action.getProgress(component).add(step),component);
            progress = new Color(action.getProgress(component));

        } else {

            action.execute(endValue,component);
            endAnimation();
        }
    }

    @Override
    public Animation copy() {
        return new ColorAnimation(hud,duration,new Color(startValue),new Color(endValue),action);

    }
}
