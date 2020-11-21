package engine.graph.items;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    /** vertex array object id */
    private final int vaoId;

    /** vertex buffer object id  list */
    private final List<Integer> vboidList;

    /** material of the mesh */
    private Material material;

    /** number of vertices */
    private final int vertexCount;

    /**
     * Method creates an vao that can be used by the open gl graphics context form the
     * position Array, texture coordinates, normal Vectors and an indices array used to identify vertices
     *
     * @param positions positions of the vertices
     * @param textCoords texture coordinates
     * @param normals normal vectors
     * @param indices indices of the vertices
     */

    public Mesh(float[] positions,float[] textCoords,float[] normals,int[] indices){
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer normalsBuffer = null;
        IntBuffer indicesBuffer = null;
        material = new Material();
        try{
            vertexCount = indices.length;
            vboidList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            //Position VBO
            int vboId = glGenBuffers();
            vboidList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER,posBuffer,GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);

            // texture coordinates VBO
            vboId = glGenBuffers();
            vboidList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1,2,GL_FLOAT,false,0,0);

            // normals VBO
            vboId = glGenBuffers();
            vboidList.add(vboId);
            normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            if(normalsBuffer.capacity() > 0) {
                normalsBuffer.put(normals).flip();
            } else {
                // Create empty structure
                normalsBuffer = MemoryUtil.memAllocFloat(positions.length);
            }
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER,normalsBuffer,GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2,3,GL_FLOAT,false,0,0);

            //Index VBO
            vboId = glGenBuffers();
            vboidList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer,GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER,0);
            glBindVertexArray(0);

        }finally {
            if(posBuffer != null){
                MemoryUtil.memFree(posBuffer);
            }
            if(textCoordsBuffer != null){
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if(normalsBuffer != null){
                MemoryUtil.memFree(normalsBuffer);
            }
            if(indicesBuffer != null){
                MemoryUtil.memFree(indicesBuffer);
            }

        }
    }

    /**
     * @return Material of the Mesh
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * sets the material of the mesh
     *
     * @param material new Material
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * returns the vaoid to identify the mesh
     *
     * @return vaoid
     */
    @SuppressWarnings("WeakerAccess")
    public int getVaoId() {
        return vaoId;
    }

    /**
     * @return number of vertices used
     */

    @SuppressWarnings("WeakerAccess")
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * initialises the rendering by binding the texture and the Vertex Array
     * to make them ready to be rendered
     */
    private void initRender() {
        Texture texture = material.getTexture();
        if(texture != null){
            //Activate first texture Bank
            glActiveTexture(GL_TEXTURE0);
            //Bind texture
            glBindTexture(GL_TEXTURE_2D,texture.getId());
        }

        //Draw the mesh
        glBindVertexArray(getVaoId());
    }

    /**
     * ends the rendering by unbinding texture and vertex array
     */
    private void endRender(){
        //restore state
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D,0);
    }

    /**
     * draws elements on screen
     */
    public void render() {
        initRender();

        glDrawElements(GL_TRIANGLES,getVertexCount(),GL_UNSIGNED_INT,0);

        endRender();
    }

    /**
     * used to render lists of GameItems with the same mesh
     *
     * @param gameItems gameItems with the same mesh
     * @param consumer links gameItems with the right transformation
     */
    public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer){
        initRender();


        for(GameItem gameItem : gameItems){
            //Setup data required by GameItem
            consumer.accept(gameItem);
            //Render this gameItem
            glDrawElements(GL_TRIANGLES,getVertexCount(),GL_UNSIGNED_INT,0);
        }

        endRender();
    }

    /**
     * frees resources and cleans up memory
     */
    public void cleanup(){

        glDisableVertexAttribArray(0);

        //Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER,0);
        for(int vboId : vboidList){
            glDeleteBuffers(vboId);
        }

        //Delete texture
        Texture texture = material.getTexture();
        if(texture != null){
            texture.cleanup();
        }


        //Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    /**
     * deletes the buffers to free resources
     */
    @SuppressWarnings("unused")
    public void deleteBuffers() {
        glDisableVertexAttribArray(0);

        //Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER,0);
        for(int vboId : vboidList){
            glDeleteBuffers(vboId);
        }

        //Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
