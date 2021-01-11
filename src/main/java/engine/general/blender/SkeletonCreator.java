package engine.general.blender;

import engine.graph.items.animation.Joint;
import org.blender.dna.BlenderObject;
import org.blender.dna.Bone;
import org.blender.dna.bArmature;
import org.cakelab.blender.nio.CArrayFacade;
import org.joml.*;

import java.io.IOException;
import java.lang.Math;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SkeletonCreator {

    public static AnimationData createSkeleton(BlenderObject animationObject) throws IOException {

        bArmature animation = animationObject.getData().cast(bArmature.class).get();


        Bone root = animation.getBonebase().getFirst().cast(Bone.class).get();


        return createJoint(root);

    }

    private static AnimationData createJoint(Bone bone) throws IOException {

        CArrayFacade<Float> row1 = bone.getBone_mat().get(0);
        CArrayFacade<Float> row2 = bone.getBone_mat().get(1);
        CArrayFacade<Float> row3 = bone.getBone_mat().get(2);

        Matrix4f rot = new Matrix4f(
                row1.get(0),row1.get(1),row1.get(2),0,
                row2.get(0),row2.get(1),row2.get(2), 0,
                row3.get(0),row3.get(1),row3.get(2),0,
                0,0,0,1);

        System.out.println(rot + " rot root");


        rot.rotate((float)Math.toRadians(-90),new Vector3f(1,0,0));


        System.out.println(rot + " rot root2");

        String name = bone.getName().asString();

        Joint joint = new Joint(0, name, rot);

        Iterator<Bone> iterator = BlenderListIterator.create(bone.getChildbase(),Bone.class);

        int currentIndex = 1;

        AnimationData result = new AnimationData(joint);

        result.getBoneLocalBindMap().put(bone.getName().asString(), rot);

        while (iterator.hasNext()) {
            Bone child = iterator.next();
            currentIndex = createJoint(child, currentIndex,joint,bone.getLength(), result.boneLocalBindMap);

        }

        result.setTotalJoints(currentIndex);

        return result;

    }

    private static int createJoint(Bone bone, int index, Joint parent, float parentLength, Map<String, Matrix4f> boneLocalBindMap) throws IOException {

        CArrayFacade<Float> row1 = bone.getBone_mat().get(0);
        CArrayFacade<Float> row2 = bone.getBone_mat().get(1);
        CArrayFacade<Float> row3 = bone.getBone_mat().get(2);

        Matrix4f rot = new Matrix4f(
                row1.get(0),row1.get(1),row1.get(2),0,
                row2.get(0),row2.get(1),row2.get(2),0,
                row3.get(0),row3.get(1),row3.get(2),0,
                0,parentLength,0,1);

        System.out.println(rot + "  " + bone.getName().asString());

        String name = bone.getName().asString();

        Joint joint = new Joint(index, name, rot);

        parent.addChild(joint);

        Iterator<Bone> iterator = BlenderListIterator.create(bone.getChildbase(),Bone.class);

        int currentIndex = index + 1;

        boneLocalBindMap.put(bone.getName().asString(),rot);

        while (iterator.hasNext()) {
            Bone child = iterator.next();
            currentIndex = createJoint(child, currentIndex, joint, bone.getLength(),boneLocalBindMap);

        }

        return currentIndex;

    }


    static class AnimationData {
        private final Joint root;
        private int totalJoints;
        private final Map<String,Matrix4f> boneLocalBindMap;

        public AnimationData(Joint root) {
            this.root = root;
            boneLocalBindMap = new HashMap<>();
        }

        public Map<String, Matrix4f> getBoneLocalBindMap() {
            return boneLocalBindMap;
        }

        public void setTotalJoints(int totalJoints) {
            this.totalJoints = totalJoints;
        }

        public Joint getRoot() {
            return root;
        }

        public int getTotalJoints() {
            return totalJoints;
        }
    }

}
