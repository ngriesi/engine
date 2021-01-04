package engine.graph.items.animation;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a bone of a skeleton animation
 */
public class Joint {

    /**
     * id of the Joint and its position in the array of transformation matrices
     * passed to the shader
     */
    public final int index;

    /**
     * name of the joint
     */
    public final String name;

    /**
     * list of child joints this object is the parent of
     */
    public final List<Joint> children = new ArrayList<>();

    /**
     * joint matrix which positions the joint in the current pose. It moves the joint from its original position
     * in the model to its position in the current pose. The transformation is in model space.
     */
    private Matrix4f animatedTransform = new Matrix4f();

    /**
     * original transform of the joint in relation to its parent joint, used to calculate
     * the inverseBindTransform
     */
    private final Matrix4f localBindTransform;

    /**
     * original transform of the joint relative to the models position but inverted. Used to calculate
     * the joint matrices in the Animator class
     *
     * @see Animator
     */
    private Matrix4f inverseBindTransform = new Matrix4f();

    /**
     * constructor taking in the following parameters
     *
     * @param index index of the joint in the model
     * @param name name of the joint
     * @param bindLocationTransform transformation of the joint relative to its parent joint
     */
    public Joint(int index, String name, Matrix4f bindLocationTransform) {
        this.index = index;
        this.name = name;
        this.localBindTransform = bindLocationTransform;
    }

    /**
     * adds a Joint to this joint as children, used in setup when creating an animation
     *
     * @param joint joint to be added
     */
    public void addChild(Joint joint) {
        this.children.add(joint);
    }

    /**
     * returns the joint transform stored in the filed animationTransform
     *
     * @return animationTransform matrix
     */
    public Matrix4f getAnimatedTransform() {
        return animatedTransform;
    }

    /**
     * sets the joint transform stored in the animationTransform filed. This determines
     * the current position and rotation of the joint
     *
     * @param animatedTransform joint transform
     */
    public void setAnimatedTransform(Matrix4f animatedTransform) {
        this.animatedTransform = animatedTransform;
    }

    /**
     * returns the inverted model space bind transform of the joint
     *
     * @return inverse model space bind transform
     */
    public Matrix4f getInverseBindTransform() {
        return inverseBindTransform;
    }

    /**
     * method used during setup to calculate the inverse bind transform
     * for this joint and all of its children
     */
    protected void calcInverseBindTransform(Matrix4f parentBindTransform) {

        System.out.println(name + "parentBind\n" + parentBindTransform);

        System.out.println(name + "localBind\n" + localBindTransform);

        Matrix4f bindTransform;
        bindTransform = parentBindTransform.mul(localBindTransform);

        System.out.println(name + "bind\n" + bindTransform);

        inverseBindTransform  = new Matrix4f(bindTransform).invert();

        System.out.println(name + "inverseBind\n" + inverseBindTransform);

        for (Joint child : children) {
            child.calcInverseBindTransform(bindTransform);
        }

        switch ("test") {
            case "Bone":
                inverseBindTransform = new Matrix4f(
                        1,0,0,0,
                        0,1,0,0,
                        0,0,1,0,
                        0,0,0,1);break;

            case "Bone.001":
                inverseBindTransform = new Matrix4f(
                        1,0,0,0,
                        0,1,0,0,
                        0,0,1,0,
                        0,-1.439163f,0,1);break;
            case "Bone.002":
                inverseBindTransform = new Matrix4f(
                        1,0,0,0,
                        0,1,0,0,
                        0,0,1,0,
                        0,0,-2.798847f,1
                );break;
        }
        System.out.println(inverseBindTransform);
    }
}
