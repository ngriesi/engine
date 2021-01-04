package engine.graph.items.animation;

import java.util.Map;

/**
 * represents a key frame of an animation. It contains a pose and a time stamp
 */
public class KeyFrame {

    /**
     * time stamp of the frame in the animation
     */
    private final float timeStamp;

    /**
     * pose of the model at this time stamp
     */
    private final Map<String, JointTransform> pose;

    /**
     * creating a KeyFrame with a timestamp and a pose
     *
     * @param timeStamp time stamp of the KeyFrame
     * @param pose map of strings and JointTransforms representing the pose
     */
    public KeyFrame(float timeStamp, Map<String, JointTransform> pose) {
        this.timeStamp = timeStamp;
        this.pose = pose;
    }

    public float getTimeStamp() {
        return timeStamp;
    }

    public Map<String, JointTransform> getPose() {
        return pose;
    }
}
