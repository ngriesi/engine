package engine.hud.animations;

import engine.hud.Hud;
import engine.hud.actions.Action;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.Component;
import engine.hud.components.contentcomponents.QuadComponent;

public class ColorSchemeAnimation extends Animation<ColorScheme> {


    private ColorAnimation right,left,top,bottom;

    /**
     * constructor sets duration,hud,start and end value and the action of the animation
     *
     * @param hud        that uses the animation
     * @param duration   of the animation
     * @param startValue start value of the animation
     * @param endValue   of the animation
     */
    public ColorSchemeAnimation(Hud hud, int duration, ColorScheme startValue, ColorScheme endValue) {
        super(hud, duration, startValue, endValue, new AnimationAction<ColorScheme>() {
            @Override
            public void execute(ColorScheme nextValue, Component component) {

            }

            @Override
            public ColorScheme getProgress(Component component) {
                return null;
            }
        });

        right = new ColorAnimation(hud, duration, startValue.getRight(), endValue.getRight(), new AnimationAction<Color>() {
            @Override
            public void execute(Color nextValue, Component component) {
                ((QuadComponent)component).setColor(nextValue, ColorScheme.ColorSide.RIGHT);
            }

            @Override
            public Color getProgress(Component component) {
                return ((QuadComponent)component).getColor(ColorScheme.ColorSide.RIGHT);
            }
        });


        left = new ColorAnimation(hud, duration, startValue.getLeft(), endValue.getLeft(), new AnimationAction<Color>() {
            @Override
            public void execute(Color nextValue, Component component) {
                ((QuadComponent)component).setColor(nextValue, ColorScheme.ColorSide.LEFT);
            }

            @Override
            public Color getProgress(Component component) {
                return ((QuadComponent)component).getColor(ColorScheme.ColorSide.LEFT);
            }
        });

        top = new ColorAnimation(hud, duration, startValue.getTop(), endValue.getTop(), new AnimationAction<Color>() {
            @Override
            public void execute(Color nextValue, Component component) {
                ((QuadComponent)component).setColor(nextValue, ColorScheme.ColorSide.TOP);
            }

            @Override
            public Color getProgress(Component component) {
                return ((QuadComponent)component).getColor(ColorScheme.ColorSide.TOP);
            }
        });

        bottom = new ColorAnimation(hud, duration, startValue.getBottom(), endValue.getBottom(), new AnimationAction<Color>() {
            @Override
            public void execute(Color nextValue, Component component) {
                ((QuadComponent)component).setColor(nextValue, ColorScheme.ColorSide.BOTTOM);
            }

            @Override
            public Color getProgress(Component component) {
                return ((QuadComponent)component).getColor(ColorScheme.ColorSide.BOTTOM);
            }
        });
    }

    @Override
    protected ColorScheme calculateStep() {
        return null;
    }

    @Override
    public void makeStep() {
        right.makeStep();
        left.makeStep();
        top.makeStep();
        bottom.makeStep();
    }

    @Override
    public void startAnimation() {
        right.startAnimation();
        left.startAnimation();
        top.startAnimation();
        bottom.startAnimation();
    }

    @Override
    public void endAnimation() {
        right.endAnimation();
        left.endAnimation();
        top.endAnimation();
        bottom.endAnimation();
        super.endAnimation();
    }

    @Override
    public void setDuration(int duration) {
        right.setDuration(duration);
        left.setDuration(duration);
        top.setDuration(duration);
        bottom.setDuration(duration);
    }

    @Override
    public void setComponent(Component component) {
        right.setComponent(component);
        left.setComponent(component);
        top.setComponent(component);
        bottom.setComponent(component);
    }

    @Override
    public Animation copy() {
        return new ColorSchemeAnimation(hud,duration,startValue,endValue);
    }

    @Override
    public void setOnFinishedAction(Action onFinishedAction) {
        right.setOnFinishedAction(onFinishedAction);
    }

    @Override
    public Animation getInverted() {
        ColorSchemeAnimation result = (ColorSchemeAnimation) copy();

        result.right = (ColorAnimation) right.getInverted();
        result.left = (ColorAnimation) left.getInverted();
        result.top = (ColorAnimation) top.getInverted();
        result.bottom = (ColorAnimation) bottom.getInverted();

        result.endValue = startValue;
        result.startValue = endValue;
        result.calculateStep();
        return result;
    }
}
