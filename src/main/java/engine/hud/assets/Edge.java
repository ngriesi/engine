package engine.hud.assets;

import engine.hud.color.Color;
import engine.hud.constraints.elementSizeConstraints.ElementSizeConstraint;
import engine.hud.constraints.elementSizeConstraints.RelativeToComponentSizeE;

/**
 * class contains the data of the edge of a component
 */
@SuppressWarnings("unused")
public class Edge {

    /**
     * the way the edge color is added to the hud, it either is layered over the component
     * it belongs to or replaces it
     */
    public enum BlendMode {
        REPLACE,MULTIPLY
    }

    /**
     * constraint determining the size of the edge
     */
    private ElementSizeConstraint size;

    /**
     * colors of the edge where the start color is the outer color and the end color the
     * inner one. Different colors create a gradient.
     */
    private Color startColor,endColor;

    /**
     * Blend mode used for the edge
     *
     * @see BlendMode
     */
    private BlendMode blendMode;

    /**
     * constructor setting all values
     *
     * @param size size of the edge
     * @param startColor outer color
     * @param endColor inner color
     * @param blendMode blend mode
     */
    public Edge(ElementSizeConstraint size, Color startColor, Color endColor, BlendMode blendMode) {
        this.size = size;
        this.startColor = startColor;
        this.endColor = endColor;
        this.blendMode = blendMode;
    }

    /**
     * constructor using the default size  constraint
     *
     * @param size relative to component
     * @param startColor outer color
     * @param endColor inner color
     * @param blendMode blend mode
     */
    public Edge(float size, Color startColor, Color endColor, BlendMode blendMode) {
        this.size = new RelativeToComponentSizeE(size);
        this.startColor = startColor;
        this.endColor = endColor;
        this.blendMode = blendMode;
    }

    /**
     * default constructor creating a white edge with the size 0
     */
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
