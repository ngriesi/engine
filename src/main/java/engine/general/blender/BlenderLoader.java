package engine.general.blender;

import engine.graph.items.GameItem;
import engine.graph.items.animation.Animation;
import engine.graph.items.animation.AnimationItem;
import engine.graph.items.animation.Animator;
import org.blender.dna.*;
import org.blender.utils.MainLib;
import org.cakelab.blender.io.BlenderFile;
import org.cakelab.blender.nio.CArrayFacade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlenderLoader {

    enum Model {
        MODEL,ANIMATION
    }

    enum BlenderObjectKind {
        MESH(1),
        ANIMATION(25);

        public final int id;

        BlenderObjectKind(int id) {
            this.id = id;
        }
    }


    public static BlenderFile loadBlenderFile(String filename) throws IOException {

        return new BlenderFile(new File("models/" + filename));
    }

    private static BlenderObject getFirstOfItsKind(Collection collection, BlenderObjectKind blenderObjectKind) throws IOException {


        Iterator<CollectionObject> it_gObj = BlenderListIterator.create(collection.getGobject(), CollectionObject.class);
        while (it_gObj.hasNext()) {
            CollectionObject gObj = it_gObj.next();
            BlenderObject ob = gObj.getOb().get();

            if (ob == null) {
                System.out.println("null");
                continue;
            }

            if (ob.getType() == blenderObjectKind.id) {
                return ob;
            }
        }


        Iterator<CollectionChild> it_child = BlenderListIterator.create(collection.getChildren(), CollectionChild.class);
        while(it_child.hasNext()) {
            CollectionChild child = it_child.next();
            Collection childCollection = child.getCollection().get();
            if (childCollection != null) return getFirstOfItsKind(childCollection,blenderObjectKind);
        }

        return null;

    }


    private static List<GameItem> traverseCollection(Collection collection, File file, Model model) throws IOException {

        List<GameItem> gameItems = new ArrayList<>();

        Iterator<CollectionObject> it_gObj = BlenderListIterator.create(collection.getGobject(), CollectionObject.class);
        while (it_gObj.hasNext()) {
            CollectionObject gObj = it_gObj.next();
            BlenderObject ob = gObj.getOb().get();

            if (ob == null) {
                System.out.println("null");
                continue;
            }

            System.out.println(ob.getId().getName().asString());

            // check if type is a mesh
            if(ob.getType()==1 && model==Model.MODEL) {


                gameItems.add(createGameItemFromBlenderObject(ob, file));

            } else if (ob.getType() == 25 && model == Model.ANIMATION) {

                //gameItems.add(createAnimationFromBlendObject(ob, file));

            }
        }


        Iterator<CollectionChild> it_child = BlenderListIterator.create(collection.getChildren(), CollectionChild.class);
        while(it_child.hasNext()) {
            CollectionChild child = it_child.next();
            Collection childCollection = child.getCollection().get();
            if (childCollection != null) gameItems.addAll(traverseCollection(childCollection, file,model));
        }



        return gameItems;

    }

    public static GameItem createGameItemFromBlenderObject(BlenderObject blenderObject,File file) throws IOException {


        org.blender.dna.Mesh mesh = blenderObject.getData().cast(org.blender.dna.Mesh.class).get();

        CArrayFacade<Float> location = blenderObject.getLoc();

        GameItem gameItem = new GameItem(MeshCreator.createMesh(mesh,file));

        gameItem.setPosition(location.get(1),location.get(2),location.get(0));

        CArrayFacade<Float> scale = blenderObject.getSize();

        gameItem.setScale3(scale.get(1),scale.get(2),scale.get(0));

        return gameItem;
    }

    public static GameItem[] loadGameItemsFromBlendFile(String filename) throws IOException {

        MainLib main = new MainLib(loadBlenderFile(filename));

        Scene scene = main.getScene();

        Collection collection = scene.getMaster_collection().get();

        List<GameItem> gameItems = traverseCollection(collection,new File(filename).getAbsoluteFile(),Model.MODEL);


        GameItem[] gameItemsArr = new GameItem[gameItems.size()];
        for(int i = 0; i < gameItems.size(); i++) {
            gameItemsArr[i] = gameItems.get(i);
        }

        return gameItemsArr;

    }

    public static Animator loadAnimationFromBlendFile(String filename) throws IOException {

        BlenderFile file = new BlenderFile(new File("models/" + filename));

        MainLib main = new MainLib(file);

        file.close();

        Scene scene = main.getScene();

        Collection collection = scene.getMaster_collection().get();

        return createAnimationFromBlendObject(getFirstOfItsKind(collection, BlenderObjectKind.MESH), getFirstOfItsKind(collection, BlenderObjectKind.ANIMATION), new File(filename).getAbsoluteFile());

    }

    private static Animator createAnimationFromBlendObject(BlenderObject meshObject, BlenderObject animationObject, File file) throws IOException {
        org.blender.dna.Mesh mesh = meshObject.getData().cast(org.blender.dna.Mesh.class).get();

        engine.graph.items.Mesh animationMesh = AnimationMeshCreator.createMesh(mesh, file);

        SkeletonCreator.AnimationData animationData = SkeletonCreator.createSkeleton(animationObject);

        Animation animation = LoadAnimation.createAnimation(animationObject, animationData.getBoneLocalBindMap());

        Animator result =  new Animator(new AnimationItem(animationMesh,animationData.getRoot(),animationData.getTotalJoints()));

        result.doAnimation(animation);

        return result;
    }

}
