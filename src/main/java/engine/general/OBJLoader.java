package engine.general;

import engine.general.save.Resources;
import engine.graph.items.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class used to load models from obj files
 * only supports triangles
 */
@SuppressWarnings("unused")
public class OBJLoader {

    /**
     * loads models with less memory use, faster for smaller models (<10000 vertices)
     *
     * @param filename of the obj model
     * @return a mesh of the obj model
     * @throws Exception if the file cant be red
     */
    public static Mesh loadNoDoubles(String filename) throws Exception{
        long time = System.nanoTime();
        List<String> lines = Resources.readAllLines(filename);
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<IndexData> indices = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        Map<String, Integer> verticesInd;

        for(String line : lines){
            String[] tokens = line.split("\\s+");
            switch (tokens[0]){
                case "v":
                    Vector3f vec3f = new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt":
                    //Texture cords
                    Vector2f vec2f = new Vector2f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn":
                    //Normal vectors
                    Vector3f vec3fNormal = new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3]));
                    normals.add(vec3fNormal);
                    break;
                case "f":
                    Face face = new Face(tokens[1],tokens[2],tokens[3]);

                    faces.add(face);
                    break;
                default:break;
            }
        }


        int index = 0;
        int count = 0;

        verticesInd = new HashMap<>();




        for(Face face : faces){
            for(IdxGroup idxGroup : face.getFaceVertexIndices()){
                if(verticesInd.get(idxGroup.idxPos+""+Math.max(idxGroup.idxTexCord,0)+""+Math.max(idxGroup.idxVecNormal,0)) == null) {
                    index++;
                    verticesInd.put(idxGroup.idxPos+""+Math.max(idxGroup.idxTexCord,0)+""+Math.max(idxGroup.idxVecNormal,0),index);
                    indices.add(new IndexData(index, idxGroup.idxPos, idxGroup.idxTexCord, idxGroup.idxVecNormal));


                    count++;
                }else{
                    indices.add(new IndexData(verticesInd.get(idxGroup.idxPos+""+Math.max(idxGroup.idxTexCord,0)+""+Math.max(idxGroup.idxVecNormal,0)), idxGroup.idxPos, idxGroup.idxTexCord, idxGroup.idxVecNormal));

                }



            }
        }



        float[] posArr = new float[count * 3];
        float[] texArr = new float[count * 2];
        float[] normArr = new float[count * 3];
        int[] indicesArr = new int[indices.size()];




        int i = 0;
        int j = 0;
        System.out.println(filename);
        for(IndexData data : indices){



            if(data.getIndex() > i) {
                posArr[i * 3] = vertices.get(data.pos).x;
                posArr[i * 3 + 1] = vertices.get(data.pos).y;
                posArr[i * 3 + 2] = vertices.get(data.pos).z;

                if(data.tex != -1) {
                    texArr[i * 2] = textures.get(data.tex).x;
                    texArr[i * 2 + 1] = 1 - textures.get(data.tex).y;
                }

                if(data.norm != -1) {
                    normArr[i * 3] = normals.get(data.norm).x;
                    normArr[i * 3 + 1] = normals.get(data.norm).y;
                    normArr[i * 3 + 2] = normals.get(data.norm).z;
                }
                indicesArr[j] = data.getIndex() - 1;
                i++;
                j++;
            }else{
                indicesArr[j] = data.getIndex() - 1;
                j++;
            }
        }

        int i1 = 0;
        for(Vector2f v2f : textures){
            System.out.println(v2f.x + "||"+v2f.y);
        }

        return new Mesh(posArr,texArr,normArr,indicesArr);

    }

    /**
     * loads a mesh by ordering the vertices the other way round
     *
     * @param filename obj model filename
     * @return mesh of obj model
     * @throws Exception if model cant be loaded
     */
    public static Mesh loadInverted(String filename) throws Exception {
        long time = System.nanoTime();
        List<String> lines = Resources.readAllLines(filename);
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<IndexData> indices = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        Map<String, Integer> verticesInd;

        for(String line : lines){
            String[] tokens = line.split("\\s+");
            switch (tokens[0]){
                case "v":
                    Vector3f vec3f = new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt":
                    //Texture cords
                    Vector2f vec2f = new Vector2f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn":
                    //Normal vectors
                    Vector3f vec3fNormal = new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3]));
                    normals.add(vec3fNormal);
                    break;
                case "f":
                    Face face = new Face(tokens[3],tokens[2],tokens[1]);

                    faces.add(face);
                    break;
                default:break;
            }
        }


        int index = 0;
        int count = 0;

        verticesInd = new HashMap<>();




        for(Face face : faces){
            for(IdxGroup idxGroup : face.getFaceVertexIndices()){
                if(verticesInd.get(idxGroup.idxPos+""+Math.max(idxGroup.idxTexCord,0)+""+Math.max(idxGroup.idxVecNormal,0)) == null) {
                    index++;
                    verticesInd.put(idxGroup.idxPos+""+Math.max(idxGroup.idxTexCord,0)+""+Math.max(idxGroup.idxVecNormal,0),index);
                    indices.add(new IndexData(index, idxGroup.idxPos, idxGroup.idxTexCord, idxGroup.idxVecNormal));


                    count++;
                }else{
                    indices.add(new IndexData(verticesInd.get(idxGroup.idxPos+""+Math.max(idxGroup.idxTexCord,0)+""+Math.max(idxGroup.idxVecNormal,0)), idxGroup.idxPos, idxGroup.idxTexCord, idxGroup.idxVecNormal));

                }



            }
        }



        float[] posArr = new float[count * 3];
        float[] texArr = new float[count * 2];
        float[] normArr = new float[count * 3];
        int[] indicesArr = new int[indices.size()];




        int i = 0;
        int j = 0;

        for(IndexData data : indices){



            if(data.getIndex() > i) {
                posArr[i * 3] = vertices.get(data.pos).x;
                posArr[i * 3 + 1] = vertices.get(data.pos).y;
                posArr[i * 3 + 2] = vertices.get(data.pos).z;

                if(data.tex != -1) {
                    texArr[i * 2] = textures.get(data.tex).x;
                    texArr[i * 2 + 1] = 1 - textures.get(data.tex).y;
                }

                if(data.norm != -1) {
                    normArr[i * 3] = normals.get(data.norm).x;
                    normArr[i * 3 + 1] = normals.get(data.norm).y;
                    normArr[i * 3 + 2] = normals.get(data.norm).z;
                }
                indicesArr[j] = data.getIndex() - 1;
                i++;
                j++;
            }else{
                indicesArr[j] = data.getIndex() - 1;
                j++;
            }
        }



        return new Mesh(posArr,texArr,normArr,indicesArr);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class IndexData{
        private int index;
        private int pos,tex,norm;

        public IndexData(int index, int pos, int tex, int norm) {
            this.index = index;
            this.pos = pos;
            this.tex = tex;
            this.norm = norm;
        }

        public int getIndex() {
            return index;
        }

        public int getPos() {
            return pos;
        }

        public int getTex() {
            return tex;
        }

        public int getNorm() {
            return norm;
        }
    }

    @SuppressWarnings("unused")
    private static class VertexData{
        private Vector3f postion,normal;
        private Vector2f texture;
        private int index;

        public VertexData(Vector3f position, Vector2f texture, Vector3f normals,int index) {
            this.postion = position;
            this.normal = normals;
            this.texture = texture;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public Vector3f getPostion() {
            return postion;
        }

        public Vector3f getNormal() {
            return normal;
        }

        public Vector2f getTexture() {
            return texture;
        }
    }

    /**
     * loads mesh, fast for very big obj files
     *
     * @param filename obj filename
     * @return mesh
     * @throws Exception if file cant be loaded
     */
    public static Mesh loadMesh(String filename) throws Exception{
        long time = System.nanoTime();
        List<String> lines = Resources.readAllLines(filename);
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        for(String line : lines){
            String[] tokens = line.split("\\s+");
            switch (tokens[0]){
                case "v":
                    //Geometric vertex
                    Vector3f vec3f = new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt":
                    //Texture cords
                    Vector2f vec2f = new Vector2f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn":
                    //Normal vectors
                    Vector3f vec3fNormal = new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3]));
                    normals.add(vec3fNormal);
                    break;
                case "f":
                    Face face = new Face(tokens[1],tokens[2],tokens[3]);
                    faces.add(face);
                    break;
                default:break;
            }
        }
        Mesh mesh = reorderLists(vertices,textures,normals,faces);
        System.out.println(System.nanoTime() - time);
        return mesh;
    }

    /**
     * help class for method loadMesh
     */
    @SuppressWarnings("WeakerAccess")
    protected static class IdxGroup {
        public static final int NO_VALUE = -1;

        public int idxPos;
        public int idxTexCord;
        public int idxVecNormal;
        public  IdxGroup(){
            idxPos = NO_VALUE;
            idxTexCord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }

    /**
     * help class for method loadMesh
     */
    protected static class Face{
        private IdxGroup[] idxGroups;

        @SuppressWarnings("WeakerAccess")
        public Face(String v1, String v2, String v3){
            idxGroups = new IdxGroup[3];
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        private IdxGroup parseLine(String line){
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0])-1;
            if(length > 1){
                String textCords = lineTokens[1];
                idxGroup.idxTexCord = textCords.length() > 0 ? Integer.parseInt(textCords)-1 : IdxGroup.NO_VALUE;
                if(length > 2){
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2])-1;
                }
            }
            return idxGroup;
        }

        @SuppressWarnings("WeakerAccess")
        public IdxGroup[] getFaceVertexIndices(){
            return idxGroups;
        }
    }

    /**
     * help method for loadMesh
     *
     * @param posList see loadMesh
     * @param texList see loadMesh
     * @param normList see loadMesh
     * @param faceList see loadMesh
     * @return mesh
     */
    private static Mesh reorderLists(List<Vector3f> posList, List<Vector2f> texList, List<Vector3f> normList, List<Face> faceList){

        List<Integer> indices = new ArrayList<>();

        float[] posArr = new float[posList.size() * 3];
        int i = 0;
        for(Vector3f pos :posList){
            posArr[i*3] = pos.x;
            posArr[i*3 +1] = pos.y;
            posArr[i*3 +2] = pos.z;
            i++;
        }

        float[] texCords = new float[posList.size() * 2];
        float[] normArr = new float[posList.size() * 3];

        for(Face face : faceList){
            IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for(IdxGroup indValue : faceVertexIndices){
                processVertex(indValue,texList,normList,indices,texCords,normArr);
            }
        }
        int[] indicesArray;
        indicesArray = indices.stream().mapToInt((Integer v) -> v).toArray();

        return new Mesh(posArr, texCords,normArr,indicesArray);
    }

    /**
     * help method for reorderLists
     *
     * @param indices see reorderLists
     * @param textCordList see reorderLists
     * @param normList see reorderLists
     * @param indicesList see reorderLists
     * @param texCordArr see reorderLists
     * @param normArr see reorderLists
     */
    private static void processVertex(IdxGroup indices,List<Vector2f> textCordList,List<Vector3f> normList,List<Integer> indicesList,
                                      float[] texCordArr, float[] normArr){

        // set index for vertex coordinates
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);

        //Reorder texture coordinates
        if(indices.idxTexCord >= 0){
            Vector2f textCord = textCordList.get(indices.idxTexCord);
            texCordArr[posIndex * 2] = textCord.x;
            texCordArr[posIndex * 2 + 1] = 1 - textCord.y;
        }

        // reorder normals
        if(indices.idxVecNormal >= 0){
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] =vecNorm.y;
            normArr[posIndex * 3 + 2] =vecNorm.z;
        }

    }
}
