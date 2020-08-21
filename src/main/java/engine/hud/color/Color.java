package engine.hud.color;

import org.joml.Vector4f;

import java.lang.reflect.Field;

@SuppressWarnings({"unused"})
public class Color {

    public static final Color BLACK = new Color(0,0,0,1f);
    public static final Color WHITE = new Color(1f,1f,1f,1f);
    public static final Color TRANSPARENT = new Color(0,0,0,0);
    public static final Color RED = new Color(1f,0,0,1f);
    public static final Color GREEN = new Color(0,1f,0,1f);
    public static final Color BLUE = new Color(0,0,1f,1f);
    public static final Color PINK = new Color(1f,0,1f,1f);
    public static final Color YELLOW = new Color(1f,1f,0,1f);
    public static final Color TEAL = new Color(0,1f,1f,1f);
    public static final Color ORANGE = new Color(1f,0.5f,0,1f);
    public static final Color PURPLE = new Color(0.5f,0,1f,1f);
    public static final Color MID_GREEN = new Color(0,1f,0.5f,1f);
    public static final Color DARK_GREEN = new Color(0,0.4f,0,1f);
    public static final Color GREY = new Color(0.5f,0.5f,0.5f,1f);
    public static final Color VERY_LIGHT_GRAY = new Color(0.9f,0.9f,0.9f,1f);
    public static final Color LIGHT_GRAY = new Color(0.7f,0.7f,0.7f,1f);
    public static final Color DARK_GRAY = new Color(0.3f,0.3f,0.3f,1f);


    public static String getColorName(Vector4f color) {
        for(Field field : engine.hud.color.Color.class.getFields()) {

            try {
                if(field.get(new Vector4f()).toString().equals(color.toString())) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static Vector4f getColorByName(String name) {
        for(Field field : engine.hud.color.Color.class.getFields()) {
            if(field.getName().equals(name)) {
                try {
                    return (Vector4f) field.get(new Vector4f());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Vector4f color;

    public Color(Color color) {
        this.color = new Vector4f(color.getColor());
    }

    public Color() {
        this(new Vector4f(1,1,1,1));
    }

    public Color(float red,float green,float blue,float alpha) {
        this.color = new Vector4f(red,green,blue,alpha);
    }

    public Color(float red,float green,float blue) {
        this(red,green,blue,1f);
    }

    public Color(Vector4f color) {
        this.color = color;
    }

    public Color(int red,int green,int blue) {
        this(red,green,blue,255);
    }

    public Color(int red,int green,int blue,int alpha) {
        color = new Vector4f(red/255f,green/255f,blue/255f,alpha/255f);
    }

    public void shiftColor(float shift) {
        color = new Vector4f(color.x * shift,color.y * shift,color.z * shift,color.w);
    }

    public Color add(Color color) {
        this.color.x += color.getRed();
        this.color.y += color.getGreen();
        this.color.z += color.getBlue();
        this.color.w += color.getAlpha();
        return this;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = new Vector4f(color);
    }

    public void setRed(float red) {
        this.color.x = red;
    }

    public void setRed(int red) {
        this.color.x = red/255f;
    }

    public float getRed() {
        return this.color.x;
    }

    public void setGreen(float green) {
        this.color.y = green;
    }

    public void setGreen(int green) {
        this.color.y = green/255f;
    }

    public float getGreen() {
        return this.color.y;
    }

    public void setBlue(float blue) {
        this.color.z = blue;
    }

    public void setBlue(int blue) {
        this.color.z = blue/255f;
    }

    public float getBlue() {
        return this.color.z;
    }

    public void setAlpha(float alpha) {
        this.color.w = alpha;
    }

    public void setAlpha(int alpha) {
        this.color.w = alpha/255f;
    }

    public float getAlpha() {
        return this.color.w;
    }
}
