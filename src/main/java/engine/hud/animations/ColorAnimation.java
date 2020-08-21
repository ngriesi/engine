package engine.hud.animations;

import engine.hud.Hud;
import engine.hud.color.Color;
import org.joml.Vector4f;

public class ColorAnimation extends Animation<Color> {
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
        super(hud, duration, startValue, endValue, action);
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

        Vector4f mov  = new Vector4f(endValue.getColor());
        mov.sub(startValue.getColor());
        Vector4f prog = new Vector4f(progress.getColor());
        prog.sub(startValue.getColor());


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
