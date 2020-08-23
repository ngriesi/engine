package engine.render;

import engine.general.Transformation;
import engine.general.Window;
import engine.general.save.Resources;
import engine.graph.general.Camera;
import engine.graph.general.Scene;
import engine.graph.general.Skybox;
import engine.graph.items.GameItem;
import engine.graph.items.Mesh;
import engine.graph.light.LightHandler;
import engine.hud.Hud;
import engine.hud.assets.Quad;
import engine.hud.components.Component;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengles.GLES20.GL_COLOR_BUFFER_BIT;

public class Renderer {

    private Quad temp;

    /** the angele of the field of view */
    private static final float FOV = (float)Math.toRadians(60.0f);

    /** the near plane for the field of view */
    @SuppressWarnings("WeakerAccess")
    public static final float Z_NEAR = 0.01f;

    /** the far plane for the field of view */
    @SuppressWarnings("WeakerAccess")
    public static final float Z_FAR = 1000.f;

    /** transformation object used for the matrix calculations */
    private final Transformation transformation;

    /** shader program for the scene (3d objects) */
    private ShaderProgram sceneShaderProgram;

    /** shader program for the hud */
    private ShaderProgram hudShaderProgram;

    /** shader program for the sky box */
    private ShaderProgram skyBoyShaderProgram;

    /**
     * constructor creates transformation object
     */
    public Renderer(){
        transformation = new Transformation();
    }

    /**
     * initialization: sets up the shader programs
     *
     * @param window unused
     * @param lightHandler links lights with the scene shader
     * @throws Exception if shaders cant be created or the uniforms cant be found
     */
    @SuppressWarnings("unused")
    public void init(Window window, LightHandler lightHandler) throws Exception{
        //setupSceneShader(lightHandler);
        setupHudShader();
        //setupSkyBoxShader();
        temp = new Quad();
        temp.setPosition(0.5f,0.5f,1);
        temp.getMesh().getMaterial().setAmbientColor(new Vector4f(0,0,1,1));
    }

    /**
     * sets up scene shader:
     * creates the shaders, creates the uniforms and initializes the light handler
     *
     * @param lightHandler link lights with shader
     * @throws Exception if shaders cant be created or uniforms cant be found
     */
    @SuppressWarnings("unused")
    private void setupSceneShader(LightHandler lightHandler) throws Exception {
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(Resources.loadResource("/vertex.vs"));
        sceneShaderProgram.createFragmentShader(Resources.loadResource("/fragment.fs"));
        sceneShaderProgram.link();

        sceneShaderProgram.createUniforms("projectionMatrix");
        sceneShaderProgram.createUniforms("modelViewMatrix");
        sceneShaderProgram.createUniforms("texture_sampler");

        sceneShaderProgram.createMaterialUniform("material");

        lightHandler.inti(sceneShaderProgram);
    }

    /**
     * sets up hud shader
     * creates the shaders and creates the uniforms
     *
     * @throws Exception if shaders cant be created or uniforms cant be found
     */
    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(Resources.loadResource("/hud_vertex.vs"));
        hudShaderProgram.createFragmentShader(Resources.loadResource("/hud_fragment.fs"));
        hudShaderProgram.link();

        hudShaderProgram.createUniforms("projModelMatrix");
        hudShaderProgram.createVector4fArrayUniform("colors",4);
        hudShaderProgram.createUniforms("useColorShade");
        hudShaderProgram.createUniforms("hasTexture");
        hudShaderProgram.createUniforms("keepCornerProportion");
        hudShaderProgram.createUniforms("keepEdgeProportion");
        hudShaderProgram.createEdgeUniform("outerEdge");
        hudShaderProgram.createEdgeUniform("innerEdge");
        hudShaderProgram.createEdgeUniform("middleEdge");
        hudShaderProgram.createUniforms("maskMode");
        hudShaderProgram.createUniforms("cornerSize");
        hudShaderProgram.createUniforms("depth");
        hudShaderProgram.createUniforms("full");
        hudShaderProgram.createUniforms("isText");

    }

    /**
     * sets up sky box shader
     * creates the shaders and creates the uniforms
     *
     * @throws Exception if shaders cant be created or uniforms cant be found
     */
    @SuppressWarnings("unused")
    private void setupSkyBoxShader() throws Exception {
        skyBoyShaderProgram = new ShaderProgram();
        skyBoyShaderProgram.createVertexShader(Resources.loadResource("/sb_vertex.vs"));
        skyBoyShaderProgram.createFragmentShader(Resources.loadResource("/sb_fragment.fs"));
        skyBoyShaderProgram.link();
        skyBoyShaderProgram.createUniforms("projectionMatrix");
        skyBoyShaderProgram.createUniforms("modelViewMatrix");
        skyBoyShaderProgram.createUniforms("texture_sampler");
        skyBoyShaderProgram.createUniforms("ambientLight");
    }

    /**
     * Main rendering method updates the viewport if the window got resized,
     * updates the main matrices with the camera and calls the three rendering methods
     *
     * @param window in witch the rendering happens
     * @param camera used to update the view matrix
     * @param scene scene that gets rendered
     * @param hud hud that gets rendered
     */
    @SuppressWarnings("unused")
    public void render(Window window, Camera camera, Scene scene, Hud hud){
        clear();

        if(window.isResized()){

            GL11.glViewport(0,0,window.getWidth(),window.getHeight());
            window.setResized(false);
            hud.updateBounds();
        }

        transformation.updateProjectionMatrix(FOV, window.getWidth(),window.getHeight(),Z_NEAR,Z_FAR);
        transformation.updateViewMatrix(camera);

        //renderScene(scene);

        renderHud(window, hud);

        //renderSkyBox(scene);
    }

    /**
     * renders scene: lights and 3d objects
     *
     * @param scene scene to be rendered
     */
    @SuppressWarnings("unused")
    public void renderScene(Scene scene){




        sceneShaderProgram.bind();

        //Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        sceneShaderProgram.setUniform("projectionMatrix",projectionMatrix);

        //Update viewMatrix
        Matrix4f viewMatrix = transformation.getViewMatrix();

        scene.getLightHandler().renderLights(viewMatrix,sceneShaderProgram);

        sceneShaderProgram.setUniform("texture_sampler",0);
        //Render each mesh with the associated gameItem
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for(Mesh mesh : mapMeshes.keySet()){
            sceneShaderProgram.setUniform("material",mesh.getMaterial());
            mesh.renderList(mapMeshes.get(mesh), gameItem -> {
                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix);
                sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            });
        }

        sceneShaderProgram.unbind();



        sceneShaderProgram.unbind();
    }

    /**
     * renders sky box
     *
     * @param scene used to get sky box
     */
    @SuppressWarnings("unused")
    private void renderSkyBox(Scene scene){
        skyBoyShaderProgram.bind();

        skyBoyShaderProgram.setUniform("texture_sampler",0);

        //Update Projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        skyBoyShaderProgram.setUniform("projectionMatrix",projectionMatrix);
        Skybox skybox = scene.getSkybox();
        Matrix4f viewMatrix = transformation.getViewMatrix();
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skybox,viewMatrix);
        skyBoyShaderProgram.setUniform("modelViewMatrix",modelViewMatrix);
        skyBoyShaderProgram.setUniform("ambientLight",scene.getLightHandler().getAmbientLight());

        scene.getSkybox().getMesh().render();

        skyBoyShaderProgram.unbind();
    }

    /**
     * calls method to render hud
     * rendering is done in the hud components classes
     *
     * @param window window to render in
     * @param hud hud to be rendered
     */
    @SuppressWarnings("unused")
    private void renderHud(Window window, Hud hud) {
        hudShaderProgram.bind();


        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0,1,1,0,0,-Component.MAX_IDS);

        hud.render(ortho,transformation,hudShaderProgram);

        /*for(GameItem gameItem : hud.getGameItems()){
            Mesh mesh = gameItem.getMesh();
            Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(gameItem,ortho);
            hudShaderProgram.setUniform("projModelMatrix",projModelMatrix);
            hudShaderProgram.setUniform("color",gameItem.getMesh().getMaterial().getAmbientColor());
            hudShaderProgram.setUniform("hasTexture",gameItem.getMesh().getMaterial().isTexture() ? 1 : 0);

            mesh.render();
        }*/



        glStencilFunc(GL_EQUAL,1,0xFF);

        Mesh mesh = temp.getMesh();
        Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(temp,ortho);
        hudShaderProgram.setUniform("projModelMatrix",projModelMatrix);
        //hudShaderProgram.setUniform("colorUni",temp.getMesh().getMaterial().getAmbientColor());
        hudShaderProgram.setUniform("hasTexture",temp.getMesh().getMaterial().isTexture() ? 1 : 0);
        hudShaderProgram.setUniform("keepCornerProportion",1);
        hudShaderProgram.setUniform("colors",new Vector4f[] {new Vector4f(1,1,1,1)});
        hudShaderProgram.setUniform("useColorShade",0);

        //mesh.render();

        hudShaderProgram.unbind();
    }

    /**
     * cleans up the shaders
     */
    public void cleanup(){
        if(skyBoyShaderProgram != null){
            skyBoyShaderProgram.cleanup();
        }
        if(sceneShaderProgram != null){
            sceneShaderProgram.cleanup();
        }
        if(hudShaderProgram != null){
            hudShaderProgram.cleanup();
        }
    }

    /**
     * clears the buffers:
     * colorBuffer
     * depthBuffer
     * stencilBuffer
     */
    private void clear(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }
}
