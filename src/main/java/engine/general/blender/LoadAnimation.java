package engine.general.blender;

import engine.graph.items.animation.Animation;
import engine.graph.items.animation.JointTransform;
import engine.graph.items.animation.KeyFrame;
import org.blender.dna.*;
import org.cakelab.blender.nio.CArrayFacade;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;

import java.io.IOException;
import java.util.*;

public class LoadAnimation {

    public static Animation createAnimation(BlenderObject animationObject, Map<String,Matrix4f> boneLocalBindMap) throws IOException {

        bArmature animation = animationObject.getData().cast(bArmature.class).get();

        AnimData animationData = animationObject.getAdt().get();

        bAction action = animationData.getAction().get();


        Map<String, BoneAnimationData> boneAnimationDataMap = new HashMap<>();


        Iterator<FCurve> iterator = BlenderListIterator.create(action.getCurves(), FCurve.class);

        BoneAnimationData currentBone;

        int maxLength = 0;

        List<String> boneNames = new ArrayList<>();

        while (iterator.hasNext()) {
            FCurve curve = iterator.next();


            String boneName = curve.getGrp().get().getName().asString();

            currentBone = boneAnimationDataMap.get(boneName);

            if(currentBone == null) {
                currentBone = new BoneAnimationData(boneName);
                boneAnimationDataMap.put(boneName, currentBone);
                boneNames.add(boneName);
            }

            BezTriple[] beziers = curve.getBezt().toArray(curve.getTotvert());

            FloatValueChange floatValueChange = new FloatValueChange(beziers.length);

            maxLength = Math.max(beziers.length, maxLength);

            int index = 0;


            for(BezTriple bez : beziers) {
                CArrayFacade<CArrayFacade<Float>> keyframes = bez.getVec();

                CArrayFacade<Float> keyframe = keyframes.get(1);

                floatValueChange.values[index] = new FloatTimeValue(keyframe.get(0), keyframe.get(1));

                index++;
            }

            currentBone.getArrayFromColorMode(curve.getColor_mode())[curve.getArray_index()] = floatValueChange;

        }

        for (BoneAnimationData data : boneAnimationDataMap.values()) {
            System.out.println(data.toString());
        }

        KeyFrame[] keyFrames = new KeyFrame[maxLength];

        float timestamp = 0;

        for(int i = 0; i < maxLength; i++) {
            Map<String, JointTransform> pose = new HashMap<>();

            for (String name : boneNames) {
                BoneAnimationData data = boneAnimationDataMap.get(name);

                Matrix4f ma = boneLocalBindMap.get(data.boneName);

                Matrix4f manuelInput = new Matrix4f(3.42285e-8f, -0.9999999f, 0f, 0f,
                        0.9999999f, 3.42285e-8f, 0f, 0f,
                        0f, 0f, 1f, 0f,
                        0f, 1.36183f, 0f, 1 );

                Quaternionf temp = fromMatrix(ma);

                Quaternionf tempData = data.getRotation(i);

                System.out.println("auto: " + tempData + "  - " + temp);
                Quaternionf fin;
                if(!name.equals("Bone.002") || i != -1) {

                    fin = tempData.mul(temp);
                    pose.put(data.boneName, new JointTransform(data.getPosition(i).add(new Vector3f(ma.m30(),ma.m31(),ma.m32())),fin));
                } else {
                    fin = fromMatrix(manuelInput);
                    System.out.println("manuel: " + fin + "  -  " + new Quaternionf(0,0,1,1).rotateAxis((float)Math.toRadians(-90),new Vector3f(1,0,0)));
                    System.out.println(data.getPosition(i).add(new Vector3f(ma.m30(),ma.m31(),ma.m32())));
                    pose.put(data.boneName, new JointTransform(new Vector3f(0,1.36f,0),new Quaternionf(-1,0,0,1)));
                }



               // pose.put(data.boneName, new JointTransform(data.getPosition(i).add(new Vector3f(ma.m30(),ma.m31(),ma.m32())),fin));

                timestamp = data.position[0].values[i].time;


            }

            keyFrames[i] = new KeyFrame(timestamp,pose);

            System.out.println(timestamp + "\n" + pose.get("Bone.002").getLocalTransform());

            System.out.println(boneLocalBindMap.get("Bone.002"));





        }

        /*

        keyFrames[0].getPose().get("Bone.002").setMatrix4f(new Matrix4f(
                0, -0.9999999f, 0, 0, -4.37114e-8f, 0, 1, 1.359684f, -1, 0, -4.37114e-8f, 0, 0, 0, 0, 1 ));


        keyFrames[1].getPose().get("Bone.002").setMatrix4f(new Matrix4f(
                4.37114e-8f, 1.34359e-7f, 1, 0, 1.06581e-14f, 1, -1.34359e-7f, 1.359684f, 	-1, 7.10543e-15f, -4.37114e-8f,	 0, 0, 0, 0, 1));

        keyFrames[2].getPose().get("Bone.002").setMatrix4f(new Matrix4f(
                0, -0.9999999f, 0, 0, -4.37114e-8f, 0, 1, 1.359684f, -1, 0, -4.37114e-8f, 0, 0, 0, 0, 1 ));


         */

        return new Animation(timestamp,keyFrames);

    }

    public static Quaternionf fromMatrix(Matrix4f matrix) {
        float w, x, y, z;
        float diagonal = matrix.m00() + matrix.m11() + matrix.m22();
        if (diagonal > 0) {
            float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
            w = w4 / 4f;
            x = (matrix.m21() - matrix.m12()) / w4;
            y = (matrix.m02() - matrix.m20()) / w4;
            z = (matrix.m10() - matrix.m01()) / w4;
        } else if ((matrix.m00() > matrix.m11()) && (matrix.m00() > matrix.m22())) {
            float x4 = (float) (Math.sqrt(1f + matrix.m00() - matrix.m11() - matrix.m22()) * 2f);
            w = (matrix.m21() - matrix.m12()) / x4;
            x = x4 / 4f;
            y = (matrix.m01() + matrix.m10()) / x4;
            z = (matrix.m02() + matrix.m20()) / x4;
        } else if (matrix.m11() > matrix.m22()) {
            float y4 = (float) (Math.sqrt(1f + matrix.m11() - matrix.m00() - matrix.m22()) * 2f);
            w = (matrix.m02() - matrix.m20()) / y4;
            x = (matrix.m01() + matrix.m10()) / y4;
            y = y4 / 4f;
            z = (matrix.m12() + matrix.m21()) / y4;
        } else {
            float z4 = (float) (Math.sqrt(1f + matrix.m22() - matrix.m00() - matrix.m11()) * 2f);
            w = (matrix.m10() - matrix.m01()) / z4;
            x = (matrix.m02() + matrix.m20()) / z4;
            y = (matrix.m12() + matrix.m21()) / z4;
            z = z4 / 4f;
        }
        return new Quaternionf(x, y, z, w);
    }

    private static Matrix4f toRotation(Quaternionf q) {
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

    private static class BoneAnimationData {

        FloatValueChange[] quaternion;

        FloatValueChange[] position;

        String boneName;

        BoneAnimationData(String boneName) {
            this.boneName = boneName;
            this.quaternion = new FloatValueChange[4];
            this.position = new FloatValueChange[3];
        }

        public FloatValueChange[] getArrayFromColorMode(int color_mode) {
            if (color_mode == 1) {
                return position;
            }
            return quaternion;
        }

        Quaternionf getRotation(int i) {
            return new Quaternionf(quaternion[3].values[i].value,-quaternion[2].values[i].value,quaternion[1].values[i].value,quaternion[0].values[i].value);
        }

        Vector3f getPosition(int i) {
            return new Vector3f(position[0].values[i].value,position[1].values[i].value,position[2].values[i].value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(boneName).append("\n");
            for(FloatValueChange pos : position) {
                if(pos != null) sb.append(pos.toString()).append("\n");
            }
            for(FloatValueChange qua : quaternion) {
                if(qua != null) sb.append(qua.toString()).append("\n");
            }

            return sb.toString();
        }
    }

    private static class FloatValueChange {
        FloatTimeValue[] values;
        FloatValueChange(int length) { values = new FloatTimeValue[length];}

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (FloatTimeValue ftv : values) {
                sb.append(ftv.toString()).append(new String(new char[20 - ftv.toString().toCharArray().length]).replace('\0', ' '));
            }
            return sb.toString();
        }
    }

    private static class FloatTimeValue {
        float time,value;

        public FloatTimeValue(float time, float value) {
            this.time = time;
            this.value = value;
        }

        @Override
        public String toString() {
            return time + ": " + value;
        }
    }
}
