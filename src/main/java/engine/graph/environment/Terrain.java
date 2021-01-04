package engine.graph.environment;

import engine.graph.items.GameItem;
import engine.graph.items.Texture;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.stb.STBImage.*;

/**
 * creates a terrain by reusing a block of terrain created
 * with a height map
 */
public class Terrain {

    /** GameItems the terrain consists of */
    private final GameItem[] gameItems;

    /** Bounding boxes of the terrain blocks */
    private  final Box2D[][] boundingBoxes;

    /** number of terrain blocks in one row and column */
    private int terrainSize;

    /** number of vertices per column in one terrain block */
    private int verticesPerCol;

    /** number of vertices per row in one terrain block */
    private int verticesPerRow;

    /** mesh the terrain blocks consist of */
    private HeightMapMesh heightMapMesh;


    /**
     * constructor creates a terrain with multiple equal meshes
     *
     * @param terrainSize number of blocks in one row and column of the terrain
     * @param scale scale of one terrain element
     * @param minY lowest possible height of the terrain
     * @param maxY highest possible height of the terrain
     * @param heightMap height map file path
     * @param textureFile texture file path
     * @param textInc if textInc is greater than 1 the texture of the terrain gets used multiple times per
     *                mesh
     * @throws Exception if the height map or the texture cant be loaded
     */
    public Terrain(int terrainSize, float scale, float minY, float maxY, String heightMap, String textureFile, int textInc) throws Exception {
        this.terrainSize = terrainSize;
        gameItems = new GameItem[terrainSize * terrainSize];

        ByteBuffer buf;
        int width;
        int height;

        try(MemoryStack stack = MemoryStack.stackPush()) {

            // creating buffers for height, width and channels of the image
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // getting the absolute file path
            URL url = Texture.class.getResource(heightMap);
            File file = Paths.get(url.toURI()).toFile();
            String filePath = file.getAbsolutePath();

            // loading the image
            buf = stbi_load(filePath, w, h, channels, 4);
            if(buf == null) {
                throw new Exception("Image file [" + filePath + "] not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        verticesPerCol = width - 1;
        verticesPerRow = height - 1;

        heightMapMesh = new HeightMapMesh(minY, maxY,buf,width,height, textureFile, textInc);
        boundingBoxes = new Box2D[terrainSize][terrainSize];

        for(int row = 0; row < terrainSize; row++) {
            for( int col = 0; col < terrainSize; col++) {
                float xDisplacement = (col - ((float) terrainSize - 1) / (float) 2) * scale * HeightMapMesh.getXLength();
                float zDisplacement = (row - ((float) terrainSize - 1) / (float) 2) * scale * HeightMapMesh.getZLength();

                GameItem terrainBlock = new GameItem(heightMapMesh.getMesh());
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
                gameItems[row * terrainSize + col] = terrainBlock;

                boundingBoxes[row][col] = getBoundingBox(terrainBlock);
            }
        }

        stbi_image_free(buf);
    }

    /**
     * method calculates and returns the bounding box of a terrain block
     *
     * @param terrainBlock the bounding box is created for
     * @return returns the bounding box
     */
    private Box2D getBoundingBox(GameItem terrainBlock) {
        // get values form the game item
        float scale = terrainBlock.getScale();
        Vector3f position = terrainBlock.getPosition();

        //calculate bounding box
        float topLeftX = HeightMapMesh.START_X * scale + position.x;
        float topLeftZ = HeightMapMesh.START_Z * scale + position.z;
        float width = Math.abs(HeightMapMesh.START_X * 2) * scale;
        float height = Math.abs(HeightMapMesh.START_Z * 2) * scale;

        return new Box2D(topLeftX, topLeftZ, width, height);
    }

    /**
     * method takes in a position vector in view coordinates and returns the height of the terrain at
     * that specific position
     *
     * @param position position, the height gets checked
     * @return height of the terrain at the given position
     */
    public float getHeight(Vector3f position) {

        float result = Float.MIN_VALUE;

        // For each terrain block we get the bounding box, translate it to view coordinates
        // and check if the position is contained in that bounding box
        Box2D boundingBox = null;
        boolean found = false;
        GameItem terrainBlock = null;
        for(int row = 0; row < terrainSize && !found; row++) {
            for(int col = 0; col < terrainSize && !found;col++) {
                terrainBlock = gameItems[row * terrainSize + col];
                boundingBox = boundingBoxes[row][col];
                found = boundingBox.contains(position.x,position.z);
            }
        }

        // If the terrain block at the passed position is found the height of
        // that position needs to be calculated
        if(found) {
            Vector3f[] triangle = getTriangle(position,boundingBox,terrainBlock);
            result = interpolateHeight(triangle[0], triangle[1], triangle[2], position.x, position.z);
        }

        return result;
    }

    protected float interpolateHeight(Vector3f pA, Vector3f pB, Vector3f pC, float x, float z) {

        // plane equation ax + by + cz + d = 0
        float a = (pB.y - pA.y) * (pC.z - pA.z) - (pC.y - pA.y) * (pB.z - pA.z);
        float b = (pB.z - pA.z) * (pC.x - pA.x) - (pC.z - pA.z) * (pB.x - pA.x);
        float c = (pB.x - pA.x) * (pC.y - pA.y) - (pC.x - pA.x) * (pB.y - pA.y);
        float d = -(a * pA.x + b * pA.y + c * pA.z);
        // y = (-d -ax -cz) / b
        return (-d - a * x - c * z) / b;
    }

    /**
     * method returns the triangle the given position is above, below or inside
     *
     * @param position position for which the corresponding triangle gets searched
     * @param boundingBox bounding box of the terrain block that corresponds to the position
     * @param terrainBlock terrain block that corresponds to the position
     * @return the position vectors of the vertices forming the target triangle
     */
    protected Vector3f[] getTriangle(Vector3f position, Box2D boundingBox, GameItem terrainBlock) {
        // get the column and row of the height map associated to the current position
        float cellWidth = boundingBox.width / (float) verticesPerCol;
        float cellHeight = boundingBox.height / (float) verticesPerRow;
        int col = (int) ((position.x - boundingBox.x) / cellWidth);
        int row = (int) ((position.z - boundingBox.y) / cellHeight);

        Vector3f[] triangle = new Vector3f[3];
        triangle[1] = new Vector3f(
            boundingBox.x + col * cellWidth,
            getWorldHeight(row + 1,col,terrainBlock),
            boundingBox.y + (row + 1) * cellHeight
        );
        triangle[2] = new Vector3f(
                boundingBox.x + (col + 1) * cellWidth,
                getWorldHeight(row,col + 1,terrainBlock),
                boundingBox.y + row * cellHeight
        );

        if(position.z < getDiagonalZCord(triangle[1].x,triangle[1].z,triangle[2].x,triangle[2].z,position.x)) {
            triangle[0] = new Vector3f(
                    boundingBox.x + col * cellWidth,
                    getWorldHeight(row,col,terrainBlock),
                    boundingBox.y + row * cellHeight
            );
        } else {
            triangle[0] = new Vector3f(
                    boundingBox.x + (col + 1) * cellWidth,
                    getWorldHeight(row + 1,col + 1,terrainBlock),
                    boundingBox.y + (row + 1) * cellHeight
            );
        }

        return triangle;
    }

    protected float getDiagonalZCord(float x1, float z1, float x2, float z2, float x) {
        return ((z1 - z2) / (x1 - x2)) * (x - x1) + z1;
    }

    protected float getWorldHeight(int row, int col, GameItem gameItem) {
        float y = heightMapMesh.getHeight(row,col);
        return y * gameItem.getScale() + gameItem.getPosition().y;
    }

    public GameItem[] getGameItems() {
        return gameItems;
    }

    /**
     * class for storing a rectangle and checking if a point is inside of it
     */
    static class Box2D {

        /** x position of the rectangle (left) */
        public float x;

        /** y position of the rectangle (top) */
        public float y;

        /** width of the rectangle */
        public float width;

        /** height of the rectangle */
        public float height;

        /**
         * constructor setting the bounds of the rectangle
         *
         * @param x x position of the rectangle
         * @param y y position of the rectangle
         * @param width width of the rectangle
         * @param height height of the rectangle
         */
        public Box2D(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        /**
         * method checks if the point at x2, y2 is inside the rectangle
         *
         * @param x2 x coordinate of the checked point
         * @param y2 y coordinate of the checked point
         * @return true if the point is inside the rectangle
         */
        public boolean contains(float x2, float y2) {
            return x2 >= x && y2 >= y && x2 < x + width && y2 < y + height;
        }
    }
}
