package engine.graph.items.animation;

import engine.general.GameEngine;
import javafx.scene.transform.MatrixType;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

/**
 * animates an AnimationItem with an Animation
 */
public class Animator {

    /**
     * AnimationItem that gets animated
     */
    private final AnimationItem entity;

    /**
     * Animation that gets applied to the AnimationItem
     */
    private Animation currentAnimation;

    /**
     * current time of the animation
     */
    private float animationTime = 0;

    /**
     * @param animationItem model that gets animated by this animator
     */
    public Animator(AnimationItem animationItem) {
        this.entity = animationItem;
    }

    /**
     * indicates that the entity should carry out a given animation
     *
     * @param animation Animation to be carried out
     */
    public void doAnimation(Animation animation) {
        this.animationTime = 0;
        this.currentAnimation = animation;
    }

    /**
     * Method should be called each frame while the animation is running.
     * It updates the animation
     */
    public void update() {
        if (currentAnimation == null) {
            return;
        }
        increaseAnimationTime();
        Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
        applyPoseToJoints(currentPose, entity.getRootJoint(), new Matrix4f());
    }

    /**
     * increases current animation time
     */
    private void increaseAnimationTime() {
        animationTime += 1f/5;// / GameEngine.TARGET_UPS;
        if (animationTime > currentAnimation.getLength()) {
            this.animationTime %= currentAnimation.getLength();
        }
    }

    /**
     * Method returns the current animation pose of the entity
     */
    private Map<String, Matrix4f> calculateCurrentAnimationPose() {
        KeyFrame[] frames = getPreviousAndNextFrame();
        float progression = calculateProgression(frames[0], frames[1]);
        return interpolatePoses(frames[0], frames[1], progression);
    }

    /**
     * calculates and sets the JointTransforms in the Joint class
     *
     * @param currentPose     stores the current pose of the model
     * @param joint           the transformation is applied to
     * @param parentTransform transformation of the parent joint
     */
    private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
        System.out.println(joint.name);
        Matrix4f currentLocalTransform = currentPose.get(joint.name);
        System.out.println(joint.name + "currentLocal\n" + currentLocalTransform);
        Matrix4f currentTransform = new Matrix4f();
        currentTransform = parentTransform.mul(currentLocalTransform,currentTransform );
        System.out.println(joint.name + "current\n" + currentTransform);
        for (Joint childJoint : joint.children) {
            applyPoseToJoints(currentPose, childJoint, new Matrix4f(currentTransform));
        }

        System.out.println(joint.name + "inverse\n" + joint.getInverseBindTransform());
        currentTransform.mul(joint.getInverseBindTransform(), currentTransform);
        joint.setAnimatedTransform(new Matrix4f(currentTransform));
        System.out.println(joint.name + "animation\n" + currentTransform);
    }

    /**
     * Method finds the previous and next KeyFrames
     *
     * @return array with the previous and next KeyFrame
     */
    private KeyFrame[] getPreviousAndNextFrame() {
        KeyFrame[] allFrames = currentAnimation.getKeyFrames();
        KeyFrame previousFrame = allFrames[0];
        KeyFrame nextFrame = allFrames[0];
        for (int i = 1; i < allFrames.length; i++) {
            nextFrame = allFrames[i];
            if (nextFrame.getTimeStamp() > animationTime) {
                break;
            }
            previousFrame = allFrames[i];
        }
        return new KeyFrame[]{previousFrame, nextFrame};
    }

    /**
     * calculates the progression between the previous nad the next KeyFrame
     *
     * @param previousFrame previous KeyFrame
     * @param nextFrame     next KeyFrame
     * @return progression between KeyFrames as a number between 0 and 1
     */
    private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
        float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
        float currentTime = animationTime - previousFrame.getTimeStamp();
        return currentTime / totalTime;
    }

    /**
     * calculates all the local space joint transforms for the current time
     *
     * @param previousFrame previous KeyFrame
     * @param nextFrame     next KeyFrame
     * @param progression   progression between the KeyFrames
     * @return Map of all the joint transforms with the joints names
     */
    private Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
        Map<String, Matrix4f> currentPose = new HashMap<>();
        for (String jointName : previousFrame.getPose().keySet()) {
            JointTransform previousTransform = previousFrame.getPose().get(jointName);
            //System.out.println("previous\n" + previousTransform.getLocalTransform());
            JointTransform nextTransform = nextFrame.getPose().get(jointName);
            //System.out.println("next\n" + nextTransform.getLocalTransform());
            JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
            //System.out.println("current\n" + currentTransform.getLocalTransform());
            currentPose.put(jointName, currentTransform.getLocalTransform());
        }
        return currentPose;
    }

    public AnimationItem getEntity() {
        return entity;
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }
}
