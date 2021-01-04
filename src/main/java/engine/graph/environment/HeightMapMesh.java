package engine.graph.environment;

import engine.general.Utils;
import engine.graph.items.Material;
import engine.graph.items.Mesh;
import engine.graph.items.Texture;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * class used ot generate a terrain from a height map image
 */
public class HeightMapMesh {

    /** maximum value of one pixel (white) */
    private static final int MAX_COLOR = 255 * 255 * 255;

    /** start of the mesh on the x axis */
    public static final float START_X = -0.5f;

    /** start of the mesh on the z axis */
    public static final float START_Z = -0.5f;

    /** lowest possible point of the mesh */
    private float minY;

    /** highest possible point of the mesh */
    private float maxY;

    /** mesh of the terrain */
    private Mesh mesh;

    /** saves the heights of the terrain vertices */
    private final float[][] heightArray;

    /**
     * constructor creating the mesh for the terrain from a height map with a texture
     *
     * @param minY lowes height of the terrain mesh
     * @param maxY highest height of the terrain mesh
     * @param heightMapImage Buffer containing the height map image
     * @param textureFile filepath to the texture file
     * @param textInc scale of the texture to apply it multiple times in a grid
     * @throws Exception when the height map or the texture cant be loaded
     */
    public HeightMapMesh(float minY, float maxY, ByteBuffer heightMapImage,int width, int height, String textureFile, int textInc) throws Exception {

        // setting maximum and minimum height value
        this.minY = minY;
        this.maxY = maxY;

        heightArray = new float[height][width];

        Texture texture = new Texture(textureFile, Texture.FilterMode.NEAREST);

        // used to make the height map fit to the mesh
        float incX = getXLength() / (width - 1);
        float incZ = getZLength() / (height - 1);

        // crates the lists to build a mesh
        List<Float> positions = new ArrayList<>();
        List<Float> textCords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {

                // create a vertex for the current position
                positions.add(START_X + col * incX);
                float currentHeight = getHeight(col, row, width, heightMapImage);
                heightArray[row][col] = currentHeight;
                positions.add(currentHeight);
                positions.add(START_Z + row * incZ);

                // set texture coordinates
                textCords.add((float) textInc * (float) col / (float) width);
                textCords.add((float) textInc * (float) row / (float) height);

                // create indices
                if(col < width - 1 && row < height - 1) {

                    // set indices
                    int leftTop = row * width + col;
                    int leftBottom = (row + 1) * width + col;
                    int rightBottom = (row + 1) * width + col + 1;
                    int rightTop = row * width + col + 1;

                    // add indices for first vertex
                    indices.add(leftTop);
                    indices.add(leftBottom);
                    indices.add(rightTop);

                    // add indices for second vertex
                    indices.add(rightTop);
                    indices.add(leftBottom);
                    indices.add(rightBottom);
                }
            }
        }

        float[] posArr = Utils.listToArray(positions);
        int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
        float[] textCordsArr = Utils.listToArray(textCords);
        float[] normalsArr = calcNormals(posArr, width, height);
        this.mesh = new Mesh(posArr, textCordsArr, normalsArr, indicesArr);
        Material material = new Material(texture, 0.0f);
        mesh.setMaterial(material);
    }

    /**
     * calculates the height of vertex of the terrain from the color of the corresponding height
     * map pixel
     *
     * @param x column of the vertex of the mesh and the height map pixel
     * @param z row of the vertex of the mesh and the height map pixel
     * @param width width of the height map image
     * @param buffer buffer containing the height map image
     * @return a height between minY and maxY
     */
    private Float getHeight(int x, int z, int width, ByteBuffer buffer) {

        // accessing the color bytes
        //noinspection PointlessArithmeticExpression
        byte r = buffer.get(x * 4 + 0 + z * 4 * width);
        byte g = buffer.get(x * 4 + 1 + z * 4 * width);
        byte b = buffer.get(x * 4 + 2 + z * 4 * width);
        byte a = buffer.get(x * 4 + 3 + z * 4 * width);

        // combining the color bytes to a number
        int argb = ((0xFF & a) << 24) | ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);

        // returns a value for the y coordinate between minY and maxY
        return this.minY + Math.abs(this.maxY - this.minY) * ((float) argb/(float) MAX_COLOR);
    }

    public static float getXLength() {
        return Math.abs(-START_X * 2);
    }

    public static float getZLength() {
        return Math.abs(-START_Z * 2);
    }

    public Mesh getMesh() {
        return mesh;
    }

    /**
     * method returns the content of the heightArray at a specific location
     * it returns the height of a vertex at a specific position
     *
     * @param row of the vertex
     * @param col of the vertex
     * @return height of the vertex
     */
    public float getHeight(int row, int col) {
        float result = 0;
        if(row >= 0 && row < heightArray.length) {
            if(col >= 0 && col < heightArray[row].length) {
                result = heightArray[row][col];
            }
        }
        return result;
    }

    /**
     * calculates the normals of the terrain form the positions of the vertices
     *
     * @param posArr positions of the vertices
     * @param width width of the height map/ terrain
     * @param height height of the height map/ terrain
     * @return array of normals of the terrain
     */
    private float[] calcNormals(float[] posArr, int width, int height) {

        // creates vectors needed for the calculation
        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f v3 = new Vector3f();
        Vector3f v4 = new Vector3f();
        Vector3f v12 = new Vector3f();
        Vector3f v23 = new Vector3f();
        Vector3f v34 = new Vector3f();
        Vector3f v41 = new Vector3f();

        // creates the result list
        List<Float> normals = new ArrayList<>();

        Vector3f normal = new Vector3f();

        for(int row = 0; row < height; row++) {
            for( int col = 0; col < width; col++) {
                if(row > 0 && row < height - 1 && col > 0 && col < width - 1) {

                    int i0 = row * width * 3 + col * 3;
                    v0.x = posArr[i0];
                    v0.y = posArr[i0 + 1];
                    v0.z = posArr[i0 + 2];

                    int i1 = row * width * 3 + (col - 1) * 3;
                    v1.x = posArr[i1];
                    v1.y = posArr[i1 + 1];
                    v1.z = posArr[i1 + 2];
                    v1 = v1.sub(v0);

                    int i2 = (row + 1) * width * 3 + col * 3;
                    v2.x = posArr[i2];
                    v2.y = posArr[i2 + 1];
                    v2.z = posArr[i2 + 2];
                    v2 = v2.sub(v0);

                    int i3 = row * width * 3 + (col + 1) * 3;
                    v3.x = posArr[i3];
                    v3.y = posArr[i3 + 1];
                    v3.z = posArr[i3 + 2];
                    v3 = v3.sub(v0);

                    int i4 = (row - 1) * width * 3 + col * 3;
                    v4.x = posArr[i4];
                    v4.y = posArr[i4 + 1];
                    v4.z = posArr[i4 + 2];
                    v4 = v4.sub(v0);

                    v1.cross(v2, v12);
                    v12.normalize();

                    v2.cross(v3, v23);
                    v23.normalize();

                    v3.cross(v4, v34);
                    v34.normalize();

                    v4.cross(v1, v41);
                    v41.normalize();

                    normal = v12.add(v23).add(v34).add(v41);
                    normal.normalize();
                } else {
                    normal.x = 0;
                    normal.y = 1;
                    normal.z = 0;
                }
                normal.normalize();
                normals.add(normal.x);
                normals.add(normal.y);
                normals.add(normal.z);
            }
        }

        return Utils.listToArray(normals);
    }
}
