package engine.general.blender;

import org.blender.dna.*;
import org.cakelab.blender.nio.CArrayFacade;

import java.io.IOException;
import java.util.Iterator;

public class Nodes {

    public static bNodeSocket findInputSocket(bNode node, String identifier) throws IOException {
        Iterator<bNodeSocket> sockets = BlenderListIterator.create(node.getInputs(), bNodeSocket.class);
        while (sockets.hasNext()) {
            bNodeSocket socket = sockets.next();
            String socketId = socket.getIdentifier().asString();
            if (identifier.equals(socketId)) {
                return socket;
            }
        }
        return null;
    }
    public static bNodeSocket findOutputSocket(bNode node, String identifier) throws IOException {
        Iterator<bNodeSocket> sockets = BlenderListIterator.create(node.getOutputs(), bNodeSocket.class);
        while (sockets.hasNext()) {
            bNodeSocket socket = sockets.next();
            if (identifier.equals(socket.getIdentifier().asString())) {
                return socket;
            }
        }
        return null;
    }
    public static CArrayFacade<Float> getDefaultRGBAInput(bNode node, String identifier) throws IOException {
        bNodeSocket socket = findInputSocket(node, identifier);
        bNodeSocketValueRGBA default_value = socket.getDefault_value().cast(bNodeSocketValueRGBA.class).get();


        return default_value != null ? default_value.getValue() : null;
    }

    public static float getDefaultFloatInput(bNode node, String identifier) throws IOException {
        bNodeSocket socket = findInputSocket(node, identifier);
        bNodeSocketValueFloat default_value = socket.getDefault_value().cast(bNodeSocketValueFloat.class).get();


        return default_value != null ? default_value.getValue() : 0;
    }

    public static bNodeLink getConnectedLink(bNode node, String identifier) throws IOException {
        bNodeSocket socket = findInputSocket(node, identifier);

        bNodeLink link = socket.getLink().get();

        return link;
    }

    public static bNode getConnectedNode(bNode node, String identifier) throws IOException {
        bNodeSocket socket = findInputSocket(node, identifier);

        bNodeLink link = socket.getLink().get();

        return link.getFromnode().get();
    }
}
