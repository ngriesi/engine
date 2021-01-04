package engine.graph.environment;

import engine.graph.items.Texture;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL30.*;

/**
 * class handles the shadow map
 * it takes care of the depth map texture and the framebuffer objects used
 */
public class ShadowMap {

    /** width of the shadow map */
    public static final int SHADOW_MAP_WIDTH = 1024;

    /** height of the shadow map */
    public static final int SHADOW_MAP_HEIGHT = 1024;

    /** id of the depth map framebuffer object */
    private final int depthMapFBO;

    /** texture storing the depth value of this shadow map */
    private  final Texture depthMap;

    /**
     * constructor creating the texture end the framebuffer objects
     * usd by the shadow map
     *
     * @throws Exception if the framebuffer could not be created
     */
    public ShadowMap() throws Exception {

        // create a FBO to render the depth map
        depthMapFBO = glGenFramebuffers();

        // create the depth map texture
        depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);

        // attach the depth map texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0);

        // set only depth
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create Framebuffer");
        }

        // unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Texture getDepthMapTexture() {
        return depthMap;
    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }

    public void cleanup() {
        glDeleteFramebuffers(depthMapFBO);
        depthMap.cleanup();
    }
}
