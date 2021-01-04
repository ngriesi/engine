package engine.general.blender;

import engine.general.Utils;
import engine.graph.items.Mesh;
import org.blender.dna.MLoop;
import org.blender.dna.MLoopUV;
import org.blender.dna.MPoly;
import org.blender.dna.MVert;
import org.cakelab.blender.nio.CArrayFacade;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeshCreator {

    static Mesh createMesh(org.blender.dna.Mesh mesh, File file) throws IOException {

        int totpolies = mesh.getTotpoly();

        MPoly[] polies = mesh.getMpoly().toArray(totpolies);
        MVert[] vertices = mesh.getMvert().toArray(mesh.getTotvert());
        MLoop[] loops = mesh.getMloop().toArray(mesh.getTotloop());
        MLoopUV[] loopUVS = mesh.getMloopuv().toArray(mesh.getTotloop());

        Mesh result = createMesh(polies, vertices, loops, loopUVS, totpolies);

        result.setMaterial(MaterialCreator.createMaterial(mesh,file));

        return result;
    }

    public static Mesh createMesh(MPoly[] polies, MVert[] vertices, MLoop[] loops, MLoopUV[] loopUVS, int totpolies) throws IOException {

        List<Float> positions = new ArrayList<>();
        List<Float> textCords = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> tangents = new ArrayList<>();
        List<Float> biTangents = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int nextIndex = 0;


        for (int p = 0; p < totpolies; p++) {

            MPoly poly = polies[p];

            int loff = poly.getLoopstart();
            int nvertices = poly.getTotloop();

            addPolygon(poly,loff,vertices,loops,loopUVS,positions,textCords,normals,tangents,biTangents,indices,nextIndex);

            nextIndex += nvertices;
        }


        float[] positionsArr = Utils.listToArray(positions);
        float[] textCordsArr = Utils.listToArray(textCords);
        float[] normalsArr = Utils.listToArray(normals);
        int[] indicesArr = new int[indices.size()];
        for(int i = 0; i < indices.size(); i++) {
            indicesArr[i] = indices.get(i);
        }

        return new Mesh(positionsArr, textCordsArr, normalsArr, indicesArr);
    }

    private static void addPolygon(MPoly poly, int loopStart, MVert[] vertices, MLoop[] loops, MLoopUV[] loopUVS, List<Float> positions, List<Float> textCords, List<Float> normals, List<Float> tangents, List<Float> biTangents, List<Integer> indices, int nextIndex) throws IOException {

        Vector3f normal = calcNormal(vertices, loops, poly);


        int nVertices = poly.getTotloop();

        for(int i = 0; i < nVertices; i++) {
            MLoop loop = loops[loopStart + i];
            MVert v = vertices[loop.getV()];
            MLoopUV uvs = loopUVS[loopStart + i];

            addWithoutIndex(v,positions,uvs,textCords,normal,normals);
        }


        int i, j, k; // indices of the vertices of one particular triangle
        int front = 0, back = nVertices-1;
        int ntriangles = nVertices -2;

        for (int t = 0; t < ntriangles; t++) {
            if (t%2 == 0) {
                i = front;
                j = ++front;
                k = back;
            } else {
                k = back;
                j = --back;
                i = front;
            }

            addIndex(nextIndex + i, indices);
            addIndex(nextIndex + j, indices);
            addIndex(nextIndex + k, indices);

            createTangents(tangents,biTangents,nextIndex + i,nextIndex + j,nextIndex + k,positions,textCords);
        }
    }

    private static void createTangents(List<Float> tangents, List<Float> biTangents, int index1, int index2, int index3, List<Float> positions, List<Float> textCords) {
        Vector3f pos1 = new Vector3f(positions.get(index1 * 3),positions.get(index1 * 3 + 1),positions.get(index1 * 3 + 2));
        Vector3f pos2 = new Vector3f(positions.get(index2 * 3),positions.get(index2 * 3 + 1),positions.get(index2 * 3 + 2));
        Vector3f pos3 = new Vector3f(positions.get(index3 * 3),positions.get(index3 * 3 + 1),positions.get(index3 * 3 + 2));

        Vector2f uv1 = new Vector2f(textCords.get(index1 * 2), textCords.get(index1 * 2 + 1));
        Vector2f uv2 = new Vector2f(textCords.get(index2 * 2), textCords.get(index2 * 2 + 1));
        Vector2f uv3 = new Vector2f(textCords.get(index3 * 2), textCords.get(index3 * 2 + 1));

        Vector3f edge1 = pos2.sub(pos1);
        Vector3f edge2 = pos3.sub(pos1);
        Vector2f deltaUV1 = uv2.sub(uv1);
        Vector2f deltaUV2 = uv3.sub(uv1);

        float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

        Vector3f tangent = new Vector3f();

        tangent.x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x);
        tangent.y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y);
        tangent.z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z);

        addAt(tangent, tangents);
        addAt(tangent, tangents);
        addAt(tangent, tangents);

        Vector3f biTangent = new Vector3f();

        biTangent.x = f * (-deltaUV2.x * edge1.x + deltaUV1.x * edge2.x);
        biTangent.y = f * (-deltaUV2.x * edge1.y + deltaUV1.x * edge2.y);
        biTangent.z = f * (-deltaUV2.x * edge1.z + deltaUV1.x * edge2.z);

        addAt(biTangent, biTangents);
        addAt(biTangent, biTangents);
        addAt(biTangent, biTangents);

    }

    private static void addAt(Vector3f value, List<Float> list) {
        list.add(value.x);
        list.add(value.y);
        list.add(value.z);
    }

    private static void addWithoutIndex(MVert vertex, List<Float> positions, MLoopUV uvCords, List<Float> textCords, Vector3f normal, List<Float> normals) throws IOException {
        CArrayFacade<Float> pos = vertex.getCo();

        positions.add(pos.get(1));
        positions.add(pos.get(2));
        positions.add(pos.get(0));

        CArrayFacade<Float> uvs = uvCords.getUv();
        textCords.add(uvs.get(0));
        textCords.add(-uvs.get(1));

        normals.add(normal.y);
        normals.add(normal.z);
        normals.add(normal.x);
    }

    private static void addIndex(int index, List<Integer> indices) {
        indices.add(index);
    }

    private static Vector3f calcNormal(MVert[] vertices, MLoop[] loops, MPoly poly) throws IOException {
        int loopStart = poly.getLoopstart();
        /*
         Blender stores only normals which are the average
         of the normals of the adjacent polygons. But if we are not
         using smooth shading, we want the normals perpendicular to
         the plane spanned by the polygon's vertices.
         calculating the normal of the polygon from 3 vertices:
        			vec3 ab = b - a;
                  vec3 ac = c - a;
                  vec3 normal = normalize(cross(ab, ac));
        */

        Vector3f normal;

        MLoop loop = loops[loopStart];
        CArrayFacade<Float> a = vertices[loop.getV()].getCo();
        loop = loops[loopStart + 1];
        CArrayFacade<Float> b = vertices[loop.getV()].getCo();
        loop = loops[loopStart + 2];
        CArrayFacade<Float> c = vertices[loop.getV()].getCo();

        Vector3f ab = new Vector3f(b.get(0) - a.get(0), b.get(1) - a.get(1), b.get(2) - a.get(2));
        Vector3f ac = new Vector3f(c.get(0) - a.get(0), c.get(1) - a.get(1), c.get(2) - a.get(2));

        normal = ab.cross(ac);

        //converter.convertVector(normal);
        normal.normalize();

        return normal;
    }
}
