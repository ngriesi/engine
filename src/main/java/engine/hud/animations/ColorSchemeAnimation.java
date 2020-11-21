package engine.hud.animations;

import engine.hud.Hud;
import engine.hud.actions.Action;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.ContentComponent;
import engine.hud.components.contentcomponents.QuadComponent;

/**
 * class used to animate a linear transition of a <code>ColorScheme</code> object
 */
public class ColorSchemeAnimation extends Animation<ColorScheme> {


    private final ColorAnimation right,left,top,bottom;

    /**
     * constructor sets duration,hud,start and end value and the action of the animation
     *
     * creates the color animations for each side of the component
     *
     * @param hud        that uses the animation
     * @param duration   of the animation
     * @param startValue start value of the animation
     * @param endValue   of the animation
     */
    public ColorSchemeAnimation(Hud hud, int duration, ColorScheme startValue, ColorScheme endValue) {
        //parent constructor with empty animation action
        super(hud, duration, startValue, endValue, new AnimationAction<ColorScheme>() {
            @Override
            public void execute(ColorScheme nextValue, ContentComponent component) {

            }

            @Override
            public ColorScheme getProgress(ContentComponent component) {
                return null;
            }
        });

        //right side animation
        right = new ColorAnimation(hud, duration, startValue.getRight(), endValue.getRight(), new AnimationAction<Color>() {
            @Override
            public void execute(Color nextValue, ContentComponent component) {
                ((QuadComponent)component).setColor(nextValue, ColorScheme.ColorSide.RIGHT);
            }

            @Override
            public Color getProgress(ContentComponent component) {
                return ((QuadComponent)component).getColor(ColorScheme.ColorSide.RIGHT);
            }
        });

        //left side animation
        left = new ColorAnimation(hud, duration, startValue.getLeft(), endValue.getLeft(), new AnimationAction<Color>() {
            @Override
            public void execute(Color nextValue, ContentComponent component) {
                ((QuadComponent)component).setColor(nextValue, ColorScheme.ColorSide.LEFT);
            }

            @Override
            public Color getProgress(ContentComponent component) {
                return ((QuadComponent)component).getColor(ColorScheme.ColorSide.LEFT);
            }
        });

        //top side animation
        top = new ColorAnimation(hud, duration, startValue.getTop(), endValue.getTop(), new AnimationAction<Color>() {
            @Override
            public void execute(Color nextValue, ContentComponent component) {
                ((QuadComponent)component).setColor(nextValue, ColorScheme.ColorSide.TOP);
            }

            @Override
            public Color getProgress(ContentComponent component) {
                return ((QuadComponent)component).getColor(ColorScheme.ColorSide.TOP);
            }
        });

        //bottom side animation
        bottom = new ColorAnimation(hud, duration, startValue.getBottom(), endValue.getBottom(), new AnimationAction<Color>() {
            @Override
            public void execute(Color nextValue, ContentComponent component) {
                ((QuadComponent)component).setColor(nextValue, ColorScheme.ColorSide.BOTTOM);
            }

            @Override
            public Color getProgress(ContentComponent component) {
                return ((QuadComponent)component).getColor(ColorScheme.ColorSide.BOTTOM);
            }
        });
    }

    /**
     * step calculation for this animation is done in the single color
     * animations of the component sides
     *
     * @return null
     */
    @Override
    protected ColorScheme calculateStep() {
        return null;
    }

    /**
     * calls the step methods of the side animations
     */
    @Override
    public void makeStep() {

        right.makeStep();
        left.makeStep();
        top.makeStep();
        bottom.makeStep();
    }

    /**
     * starts all the side animations
     */
    @Override
    public void startAnimation() {
        right.startAnimation();
        left.startAnimation();
        top.startAnimation();
        bottom.startAnimation();
    }

    /**
     * ends all the side animations and this one for the on finished action to be called
     */
    @Override
    public void endAnimation() {
        right.endAnimation();
        left.endAnimation();
        top.endAnimation();
        bottom.endAnimation();
        super.endAnimation();
    }

    /**
     * sets the animation duration for this animations and the four side animations
     *
     * @param duration new duration of the animation in update frames
     */
    @Override
    public void setDuration(int duration) {
        right.setDuration(duration);
        left.setDuration(duration);
        top.setDuration(duration);
        bottom.setDuration(duration);
        super.setDuration(duration);
    }

    /**
     * sets an new component for this animation and the four side animations
     *
     * @param component new component
     */
    @Override
    public void setComponent(ContentComponent component) {
        right.setComponent(component);
        left.setComponent(component);
        top.setComponent(component);
        bottom.setComponent(component);
        super.setComponent(component);
    }

    /**
     * creates an independent copy of this animation
     *
     * @return copy of this animation
     */
    @Override
    public Animation<ColorScheme> copy() {
        return new ColorSchemeAnimation(hud,duration,startValue,endValue);
    }

    /**
     * creates a copy of this animation with inverted start and end values
     *
     * @return inverted copy
     */
    @Override
    public Animation<ColorScheme> getInverted() {
        return new ColorSchemeAnimation(hud,duration,endValue,startValue);
    }



    @Override
    public void setOnFinishedAction(Action onFinishedAction) {
        right.setOnFinishedAction(onFinishedAction);
    }


}
