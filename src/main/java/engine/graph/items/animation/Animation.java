package engine.graph.items.animation;

/**
 * represents an animation that can be applied to an AnimationItem.
 * An Animation stores a series of KeyFrames and a length
 *
 * @see AnimationItem,KeyFrame
 */
public class Animation {

    /**
     * length of the animation in seconds
     */
    private final float length;

    /**
     * Array of key frames of the animation
     */
    private final KeyFrame[] keyFrames;

    /**
     * creates an animation with its length and KeyFrames
     *
     * @param length length of the animation in seconds
     * @param keyFrames KeyFrames of the animation
     */
    public Animation(float length, KeyFrame[] keyFrames) {
        this.length = length;
        this.keyFrames = keyFrames;
    }

    public float getLength() {
        return length;
    }

    public KeyFrame[] getKeyFrames() {
        return keyFrames;
    }
}
