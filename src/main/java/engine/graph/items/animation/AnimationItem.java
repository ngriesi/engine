package engine.graph.items.animation;

import engine.graph.items.GameItem;
import engine.graph.items.Mesh;
import org.joml.Matrix4f;

/**
 * class represents a game item that can be animated. This class stores the information about  the
 * animation (e.g. the skeleton) and the parent class stores the information about the skin.
 */
public class AnimationItem extends GameItem {

    /**
     * root joint of the skeleton
     */
    private final Joint rootJoint;

    /**
     * number of joints in the skeleton
     */
    private final int jointCount;

    /**
     * Animator for this animation
     */
    private final Animator animator;

    /**
     * constructor creating a GameItem with teh mesh and receiving the animation information
     *
     * @param mesh mesh of the animation
     * @param rootJoint root joint of the skeleton
     * @param jointCount number of joints in the skeleton
     */
    public AnimationItem(Mesh mesh, Joint rootJoint, int jointCount) {
        super(mesh);
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
        this.animator = new Animator(this);
        rootJoint.calcInverseBindTransform(new Matrix4f());
    }

    /**
     * instructs this model to carry out a specific animation
     *
     * @param animation animation to be performed by this model
     */
    public void doAnimation(Animation animation) {
        animator.doAnimation(animation);
    }

    /**
     * updates the animator (called every frame)
     */
    public void update() {
        animator.update();
    }

    /**
     * creates the array of joint matrices to be passed to the shader
     *
     * @return array of joint matrices
     */
    public Matrix4f[] getJointTransforms() {
        Matrix4f[] jointMatrices = new Matrix4f[jointCount];
        addJointsToArray(rootJoint, jointMatrices);
        return jointMatrices;
    }

    /**
     * adds all the joint matrices from the skeleton to the array of matrices
     *
     * @param headJoint root joint of the skeleton
     * @param jointMatrices array of matrices to be modified
     */
    private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
        jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();

        for (Joint childJoint : headJoint.children) {
            addJointsToArray(childJoint, jointMatrices);
        }

    }

    public Joint getRootJoint() {
        return rootJoint;
    }
}
