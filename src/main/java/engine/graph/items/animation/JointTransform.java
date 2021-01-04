package engine.graph.items.animation;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * stores the transformation of a joint in bone space (relative to its parent joint)
 */
@SuppressWarnings("unused")
public class JointTransform {

    /**
     * position vector of the joint relative to its parent joint
     */
    private final Vector3f position;

    /**
     * Quaternion rotation of the Joint relative to its parent Joint
     */
    private final Quaternionf rotation;


    /**
     * creates a JointTransform with the position and rotation of a Joint
     * relative to its parent Joint
     *
     * @param position position of the Joint relative to its parent
     * @param rotation rotation of the joint relative to its parent
     */
    public JointTransform(Vector3f position, Quaternionf rotation) {
        this.position = position;
        this.rotation = rotation;


    }

    /**
     * constructs the bone space transformation matrix
     */
    public Matrix4f getLocalTransform() {

        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate(position);
        matrix4f.mul(toRotation(rotation),matrix4f);

        return matrix4f;
    }

    /**
     * Method interpolates between two JointTransforms
     *
     * @param frameA last KeyFrame passed
     * @param frameB next KeyFrame to pass
     * @param progression progression between 0 and 1
     */
    protected static JointTransform interpolate(JointTransform frameA, JointTransform frameB, float progression) {
        Vector3f pos = interpolate(frameA.position, frameB.position, progression);
        Quaternionf rot = interpolate(frameA.rotation, frameB.rotation, progression);

        return new JointTransform(pos, rot);
    }

    /**
     * Method interpolates between two Vector3f
     *
     * @param start start Vector
     * @param end end Vector
     * @param progression progression of the interpolation between 0 and 1
     */
    private static Vector3f interpolate(Vector3f start, Vector3f end, float progression) {
        float x = start.x + (end.x - start.x) * progression;
        float y = start.y + (end.y - start.y) * progression;
        float z = start.z + (end.z - start.z) * progression;
        return new Vector3f(x, y, z);
    }

    /**
     * Method interpolates between two quaternions
     *
     * @param start       rotation
     * @param end         rotation
     * @param progression progression of the interpolation between 0 and 1
     */
    private static Quaternionf interpolate(Quaternionf start, Quaternionf end, float progression) {
        Quaternionf result = new Quaternionf(0, 0, 0, 1);
        float dot = start.w * end.w + start.x * end.x + start.y * end.y + start.z * end.z;
        float blend = 1f - progression;
        if (dot < 0) {
            result.w = blend * start.w + progression * -end.w;
            result.x = blend * start.x + progression * -end.x;
            result.y = blend * start.y + progression * -end.y;
            result.z = blend * start.z + progression * -end.z;
        } else {
            result.w = blend * start.w + progression * end.w;
            result.x = blend * start.x + progression * end.x;
            result.y = blend * start.y + progression * end.y;
            result.z = blend * start.z + progression * end.z;
        }
        result.normalize();

        return result;
    }

    /**
     * Method turns a Quaternion into a rotation matrix
     */
    private Matrix4f toRotation(Quaternionf q) {
        Matrix4f matrix = new Matrix4f();
        final float xy = q.x * q.y;
        final float xz = q.x * q.z;
        final float xw = q.x * q.w;
        final float yz = q.z * q.y;
        final float yw = q.w * q.y;
        final float zw = q.w * q.z;
        final float xSquare = q.x * q.x;
        final float ySquare = q.y * q.y;
        final float zSquare = q.z * q.z;
        matrix.m00(1 - 2 * (ySquare + zSquare));
        matrix.m01(2 * (xy - zw));
        matrix.m02(2 * (xz + yw));
        matrix.m03(0);

        matrix.m10(2 * (xy + zw));
        matrix.m11(1 - 2 * (xSquare + zSquare));
        matrix.m12(2 * (yz - xw));
        matrix.m13(0);

        matrix.m20(2 * (xz - yw));
        matrix.m21(2 * (yz + xw));
        matrix.m22(1 - 2 * (xSquare + ySquare));
        matrix.m23(0);

        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
        matrix.m33(1);

        return matrix;
    }
}
