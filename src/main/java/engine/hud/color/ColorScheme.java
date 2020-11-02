package engine.hud.color;

import org.joml.Vector4f;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ColorScheme {

    public enum StandardColorSchemes {
        BUTTON_STANDARD,BUTTON_ENTERED,BUTTON_PRESSED,BUTTON_TEXT_STANDARD,BUTTON_TEXT_PRESSED,BUTTON_DISABLED,BUTTON_TEXT_DISABLED
    }

    public static ColorScheme getStandardColorScheme(StandardColorSchemes colorScheme) {
        switch (colorScheme) {
            case BUTTON_STANDARD: return new ColorScheme(Color.WHITE,Color.WHITE,Color.WHITE,Color.DARK_GRAY);
            case BUTTON_ENTERED: return new ColorScheme(Color.WHITE,Color.WHITE,Color.WHITE,Color.GREY);
            case BUTTON_PRESSED: return new ColorScheme(Color.GREY,Color.GREY,Color.WHITE,Color.GREY);
            case BUTTON_TEXT_STANDARD:
            case BUTTON_TEXT_PRESSED:
                return new ColorScheme(Color.BLACK);
            case BUTTON_DISABLED: return new ColorScheme(Color.WHITE,Color.WHITE,Color.WHITE,Color.LIGHT_GRAY);
            case BUTTON_TEXT_DISABLED: return new ColorScheme(Color.GREY);
            default:return new ColorScheme();
        }
    }


    public enum GradientDirection {
        TOP_TO_BOTTOM,
        RIGHT_TOP_TO_LEFT_BOTTOM,
        RIGHT_TO_LEFT,
        RIGHT_BOTTOM_TO_LEFT_TOP,
        BOTTOM_TO_TOP,
        LEFT_BOTTOM_TO_RIGHT_TOP,
        LEFT_TO_RIGHT,
        LEFT_TOP_TO_RIGHT_BOTTOM
    }

    public enum ColorSide {
        LEFT,RIGHT,TOP,BOTTOM
    }

    private Color right,left,top,bottom;

    public ColorScheme(Color right, Color left, Color top, Color bottom) {
        this.right = right;
        this.left = left;
        this.top = top;
        this.bottom = bottom;
    }

    public ColorScheme(Color color) {
        this(color,color,color,color);
    }

    public ColorScheme() {
        this(new Color());
    }

    public ColorScheme(ColorScheme colorScheme) {
        this.right = colorScheme.getRight();
        this.left = colorScheme.getLeft();
        this.top = colorScheme.getTop();
        this.bottom = colorScheme.getBottom();
    }

    public void setAllColors(Color color) {
        this.right = new Color(color);
        this.left = new Color(color);
        this.bottom = new Color(color);
        this.top = new Color(color);
    }

    public void setColors(Color left,Color top,Color right,Color bottom) {
        this.right = right;
        this.left = left;
        this.top = top;
        this.bottom = bottom;
    }

    public void setValues(Vector4f value) {
        right.setColor(value);
        left.setColor(value);
        top.setColor(value);
        bottom.setColor(value);
    }


    public void createGradient(Color startColor,Color endColor,GradientDirection direction) {
        switch (direction) {
            case BOTTOM_TO_TOP: setColors(startColor,endColor,startColor,startColor);break;
            case TOP_TO_BOTTOM: setColors(startColor,startColor,startColor,endColor);break;
            case LEFT_TO_RIGHT: setColors(startColor,startColor,endColor,startColor);break;
            case RIGHT_TO_LEFT: setColors(endColor,startColor,startColor,startColor);break;
            case LEFT_BOTTOM_TO_RIGHT_TOP: setColors(startColor,endColor,endColor,startColor);break;
            case LEFT_TOP_TO_RIGHT_BOTTOM: setColors(startColor,startColor,endColor,endColor);break;
            case RIGHT_BOTTOM_TO_LEFT_TOP: setColors(endColor,endColor,startColor,startColor);break;
            case RIGHT_TOP_TO_LEFT_BOTTOM: setColors(endColor,startColor,startColor,endColor);break;
        }
    }

    public void setColor(Color color,ColorSide side) {
        switch (side) {
            case TOP: this.top = color;break;
            case BOTTOM: this.bottom = color;break;
            case LEFT: this.left = color;break;
            case RIGHT: this.right = color;break;
        }
    }

    public Color getColor(ColorSide side) {
        switch (side) {
            case TOP:return top;
            case BOTTOM:return bottom;
            case LEFT:return left;
            default:return right;
        }
    }

    public Vector4f[] getVectorArray() {

        return new Vector4f[] {left.getVector4f(),right.getVector4f(),top.getVector4f(),bottom.getVector4f()};
    }

    public Color getRight() {
        return right;
    }

    public void setRight(Color right) {
        this.right = right;
    }

    public Color getLeft() {
        return left;
    }

    public void setLeft(Color left) {
        this.left = left;
    }

    public Color getTop() {
        return top;
    }

    public void setTop(Color top) {
        this.top = top;
    }

    public Color getBottom() {
        return bottom;
    }

    public void setBottom(Color bottom) {
        this.bottom = bottom;
    }
}
