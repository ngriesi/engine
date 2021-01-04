package engine.general.blender;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ConvertBlenderToOpenGL {
    /*
     * OpenGL and Blender use a right handed coordination system.
     *
     * OpenGL's, coordinate system has the following orientation:
     *    X-Axis points to the right,
     *    Y-Axis points up,
     *    Z-Axis points towards the viewer.
     * So, the viewer looks down the negative Z axis.
     *
     * The orientation of Blenders coordinate system is different.
     * When opening Blender with the default settings, the orientation is this:
     *    X-Axis points towards the viewer,
     *    Y-Axis points to the right,
     *    Z-Axis points up.
     * The user expects at least the up direction to be the same.
     * Now to transform from Blenders coordinate system into OpenGL
     * coordinate system, we have to turn its orientation, so that Y points up, which is:
     *    1. rotate -90 around X
     *    2. and rotate -90 around Y
     * We transform all vectors (even vertices) by this rotation.
     *
     * All other transformations, received from Blender, such as scale and rotate,
     * and the camera orientation will be adjusted for the new coordinate system.
     */

    /**
     * to transform vectors (who would have thought!)
     */
    private Matrix4f vectorTransform;
    /**
     * to transform blenders coordinate system into ours
     */
    private Quaternionf rotationTransform;
    /**
     * to transform our coordinate system into blenders
     */
    private Quaternionf inverseRotationTransform;

    /**
     * vector for temporary use
     */
    private Vector4f tmpVec4f = new Vector4f();
    /**
     * vector for temporary use
     */
    private Vector3f tmpVec3f = new Vector3f();

    public ConvertBlenderToOpenGL() {
        rotationTransform = new Quaternionf();
        rotationTransform.rotateY((float) Math.toRadians(-90));
        rotationTransform.rotateX((float) Math.toRadians(-90));


        inverseRotationTransform = new Quaternionf(rotationTransform).invert();


        vectorTransform = new Matrix4f();
        vectorTransform.rotate(rotationTransform);
    }


    public void convertVector(float[] array, int vectorStart, int len) {
        if (len == 3) {
            Vector3f vec = getTmpVector3f(array, vectorStart);
            convertVector(vec);
            setVector(vec, array, vectorStart);
        } else {
            Vector4f vec = getTmpVector4f(array, vectorStart);
            convertVector(vec);
            setVector(vec, array, vectorStart);
        }
    }


    public void convertVector(Vector3f v) {
        Vector4f vt = new Vector4f(v, 1);
        vectorTransform.transform(vt);
        v.x = vt.x;
        v.y = vt.y;
        v.z = vt.z;

    }


    public void convertVector(Vector4f v) {
        vectorTransform.transform(v);
    }



    private Vector4f getTmpVector4f(float[] array, int offset) {
        tmpVec4f.x = array[offset + 0];
        tmpVec4f.y = array[offset + 1];
        tmpVec4f.z = array[offset + 2];
        tmpVec4f.w = array[offset + 3];
        return tmpVec4f;
    }

    private void setVector(Vector3f vec, float[] array, int offset) {
        array[offset + 0] = vec.x;
        array[offset + 1] = vec.y;
        array[offset + 2] = vec.z;
    }

    private Vector3f getTmpVector3f(float[] array, int offset) {
        tmpVec3f.x = array[offset + 0];
        tmpVec3f.y = array[offset + 1];
        tmpVec3f.z = array[offset + 2];
        return tmpVec3f;
    }

    private void setVector(Vector4f vec, float[] array, int offset) {
        array[offset + 0] = vec.x;
        array[offset + 1] = vec.y;
        array[offset + 2] = vec.z;
        array[offset + 3] = vec.w;
    }

}