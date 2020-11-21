package engine.hud.animations;

import engine.hud.Hud;
import engine.hud.color.Color;
import org.joml.Vector4f;

/**
 * class used to animate a linear transition of a <code>Color</code> object
 */
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
        super(hud, duration, new Color(startValue), new Color(endValue), action);
    }

    /**
     * calculates the size of the step performed every frame
     *
     * @return the new step size
     */
    @Override
    protected Color calculateStep() {

        Color result = new Color();
        result.setRed((endValue.getRed() - startValue.getRed())/duration);
        result.setGreen((endValue.getGreen() - startValue.getGreen())/duration);
        result.setBlue((endValue.getBlue() - startValue.getBlue())/duration);
        result.setAlpha((endValue.getAlpha() - startValue.getAlpha())/duration);
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
        Vector4f mov  = new Vector4f(endValue.getVector4f());
        //get vector from start to end
        mov.sub(startValue.getVector4f());
        //copy progress vector to keep its value
        Vector4f progressTemp = new Vector4f(progress.getVector4f());
        //get vector from start to progress
        progressTemp.sub(startValue.getVector4f());

        //check if the vector from start to progress is still shorter than the one from start to end
        if(mov.length() > progressTemp.length()) {

            //set new component color
            action.execute(action.getProgress(component).add(step),component);
            progress = new Color(action.getProgress(component));

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
    public Animation<Color> copy() {
        return new ColorAnimation(hud,duration,startValue,endValue,action);
    }
}
