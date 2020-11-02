package engine.hud.assets;

import engine.hud.color.Color;
import engine.hud.constraints.elementSizeConstraints.ElementSizeConstraint;
import engine.hud.constraints.elementSizeConstraints.RelativeToComponentSizeE;

public class Edge {

    public enum BlendMode {
        REPLACE,MULTIPLY
    }

    private ElementSizeConstraint size;

    private Color startColor,endColor;

    private BlendMode blendMode;

    public Edge(ElementSizeConstraint size, Color startColor, Color endColor, BlendMode blendMode) {
        this.size = size;
        this.startColor = startColor;
        this.endColor = endColor;
        this.blendMode = blendMode;
    }

    public Edge(float size, Color startColor, Color endColor, BlendMode blendMode) {
        this.size = new RelativeToComponentSizeE(size);
        this.startColor = startColor;
        this.endColor = endColor;
        this.blendMode = blendMode;
    }

    public Edge() {
        this(0, new Color(),new Color(),BlendMode.REPLACE);
    }

    public ElementSizeConstraint getSize() {
        return size;
    }

    public void setSize(ElementSizeConstraint size) {
        this.size = size;
    }

    public void setSize(float size) {
        this.size = new RelativeToComponentSizeE(size);
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
