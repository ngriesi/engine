package engine.general.blender;

import engine.graph.items.Material;
import engine.graph.items.Texture;
import org.blender.dna.*;
import org.cakelab.blender.nio.CArrayFacade;
import org.cakelab.blender.nio.CPointer;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class MaterialCreator {


    public static Material createMaterial(Mesh mesh, File file) throws IOException {

        // get the pointer to the material
        CPointer<org.blender.dna.Material> materialCPointer = mesh.getMat().get();

        // check if a material exists
        if (materialCPointer == null || !materialCPointer.isValid()) {
            return new Material();
        } else {

            org.blender.dna.Material material = materialCPointer.get();


            if (material.getUse_nodes() != 0) {
                return readNodes(material,file);
            } else {

                Vector4f baseColor = new Vector4f(material.getR(), material.getG(), material.getB(), material.getA());

                return new Material(baseColor,0);
            }
        }

    }

    private static Material readNodes(org.blender.dna.Material material, File file) throws IOException {

        CPointer<bNodeTree> p_nodeTree = material.getNodetree();

        Material result = new Material();

        if (p_nodeTree != null && p_nodeTree.get() != null) {
            bNodeTree tree = p_nodeTree.get();
            ListBase nodes = tree.getNodes();
            Iterator<bNode> it = BlenderListIterator.create(nodes.getFirst().cast(bNode.class));
            while (it.hasNext()) {
                bNode node = it.next();
                int type = node.getType();


                if (type == 124) {
                    bNode principalBSDF = Nodes.getConnectedNode(node, "Surface");
                    bNode texture = null;
                    if(Nodes.getConnectedLink(principalBSDF, "Base Color") != null) {
                        texture = Nodes.getConnectedNode(principalBSDF, "Base Color");
                    }


                    // get color or texture
                    if(texture != null) {
                        Image image = texture.getId().cast(Image.class).get();

                        String filename = image.getName().asString();

                        filename = filename.replaceAll("\\.\\.\\\\", "");

                        File imageFile;
                        if (filename.startsWith("//")) {

                            imageFile = new File(file.getParentFile().getAbsoluteFile(), filename.substring(1));
                        } else {
                            imageFile = new File(filename);
                        }
                        try {
                            result = new Material(new Texture(imageFile.getPath(), Texture.FilterMode.NEAREST));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        CArrayFacade<Float> color = Nodes.getDefaultRGBAInput(principalBSDF, "Base Color");

                        Vector4f colorVector = new Vector4f(color.get(0), color.get(1), color.get(2), color.get(3));

                        result.setAmbientColor(colorVector);

                    }

                    // get normal map
                    bNode normalMap = null;
                    if(Nodes.getConnectedLink(principalBSDF, "Normal") != null) {
                        normalMap = Nodes.getConnectedNode(principalBSDF, "Normal");
                    }

                    if (normalMap != null) {
                        bNode normalTexture = Nodes.getConnectedNode(normalMap, "Color");

                        Image image = normalTexture.getId().cast(Image.class).get();

                        String filename = image.getName().asString();



                        filename = filename.replaceAll("\\.\\.\\\\", "");



                        File imageFile;
                        if (filename.startsWith("//")) {
                            imageFile = new File(file.getParentFile().getAbsoluteFile(), filename.substring(1));
                        } else {
                            imageFile = new File(filename);
                        }
                        try {
                            //result.setNormalMap(new Texture(imageFile.getPath(), Texture.FilterMode.NEAREST));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    result.setReflectance(Nodes.getDefaultFloatInput(principalBSDF,"Specular"));

                    result.setReflectance(1);
                }
            }
        }

        return result;
    }
}
