package engine.hud.assets;

import engine.hud.color.Color;

public class Edge {

    public enum BlendMode {
        REPLACE,MULTIPLY
    }

    private float size,position;

    private Color startColor,endColor;

    private BlendMode blendMode;

    public Edge(float size, float position, Color startColor, Color endColor, BlendMode blendMode) {
        this.size = size;
        this.position = position;
        this.startColor = startColor;
        this.endColor = endColor;
        this.blendMode = blendMode;
    }

    public Edge() {
        this(0,0,new Color(),new Color(),BlendMode.REPLACE);
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    public Color getStartColor() {
        return startColor;
    }

    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    public Color getEndColor() {
        return endColor;
    }

    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    public void setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode;
    }
}
