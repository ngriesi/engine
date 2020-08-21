package engine.graph.general;


import engine.general.OBJLoader;
import engine.graph.items.GameItem;
import engine.graph.items.Material;
import engine.graph.items.Mesh;
import engine.graph.items.Texture;

public class Skybox extends GameItem {

    /**
     * constructor creates sky box form obj model and texture
     * obj model is loaded inverted to prevent it form being
     * removed by face culling because the camera is inside
     * the box
     *
     * @param objModel sky box model
     * @param textureFile sky box texture
     * @throws Exception if texture creation fails
     */
    public Skybox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadInverted(objModel);
        Texture skyBoxTexture = new Texture(textureFile, Texture.FilterMode.NEAREST);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture,0.0f));
        setMesh(skyBoxMesh);
        setPosition(0,0,0);
    }
}
