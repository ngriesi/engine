package engine.hud.assets;

import engine.graph.items.GameItem;
import engine.graph.items.Material;
import engine.graph.items.Mesh;
import org.joml.Vector4f;

/**
 * Game Item Object used by many hud components
 * contains a quad mesh
 */
public class Quad extends GameItem {


    /**
     * default constructor creates the quad
     */
    public Quad() {
        createQuad();
    }

    /**
     * creates a quad with the origin at the center and the height and width of one
     * resizing is done by scaling
     */
    private void createQuad() {

        float[] positions = {
            -0.5f,-0.5f,0,
            0.5f,-0.5f,0,
            -0.5f,0.5f,0,
            0.5f,0.5f,0
        };

        float[] texCords = {
            0,0,
            1,0,
            0,1,
            1,1
        };

        float[] normals = {
            0,0,-1,
            0,0,-1,
            0,0,-1,
            0,0,-1
        };

        int[] indices = {
                1,2,3,
                1,0,2
        };

        Material material = new Material(new Vector4f(1,1,1,1),0);
        this.setPosition(0,0,1);
        Mesh mesh = new Mesh(positions,texCords,normals,indices);
        mesh.setMaterial(material);
        this.setMesh(mesh);
    }
}
