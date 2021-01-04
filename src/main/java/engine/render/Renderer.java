package engine.render;

import engine.general.Transformation;
import engine.general.Window;
import engine.general.save.Resources;
import engine.graph.environment.ShadowMap;
import engine.graph.general.Camera;
import engine.graph.general.Scene;
import engine.graph.environment.Skybox;
import engine.graph.items.GameItem;
import engine.graph.items.Material;
import engine.graph.items.Mesh;
import engine.graph.items.animation.AnimationItem;
import engine.graph.light.DirectionalLight;
import engine.graph.light.LightHandler;
import engine.hud.Hud;
import engine.hud.assets.Quad;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import test.MainGame;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer {


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


    /** shader program for the sky box */
    private ShaderProgram skyBoyShaderProgram;

    /** shader program for the shadow maps */
    private ShaderProgram depthShaderProgram;

    /** shadow map of the scene */
    private ShadowMap shadowMap;

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
        shadowMap = new ShadowMap();

        setupDepthShader();
        setupSceneShader(lightHandler);
        setupSkyBoxShader();
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
        sceneShaderProgram.createVertexShader(Resources.loadResource("/shader/sceneShader/sceneVertexShader.shader"));
        sceneShaderProgram.createFragmentShader(Resources.loadResource("/shader/sceneShader/sceneFragmentShader.shader"));
        sceneShaderProgram.link();

        sceneShaderProgram.createUniforms("projectionMatrix");
        sceneShaderProgram.createUniforms("modelViewMatrix");
        sceneShaderProgram.createUniforms("orthoProjectionMatrix");
        sceneShaderProgram.createUniforms("modelLightViewMatrix");
        sceneShaderProgram.createUniforms("texture_sampler");
        sceneShaderProgram.createUniforms("normalMap");
        sceneShaderProgram.createUniforms("isAnimation");
        sceneShaderProgram.createMatrix4fArrayUniform("jointTransforms", 50);

        sceneShaderProgram.createMaterialUniform("material");

        sceneShaderProgram.createFogUniform("fog");

        sceneShaderProgram.createUniforms("shadowMap");


        lightHandler.init(sceneShaderProgram);
    }

    /**
     * sets up the depth shader
     * creates the shaders and creates the uniforms
     *
     * @throws Exception if shaders cant be created or uniforms cant be found
     */
    private void setupDepthShader() throws Exception {
        depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(Resources.loadResource("/shader/depth/depthVertexShader.shader"));
        depthShaderProgram.createFragmentShader(Resources.loadResource("/shader/depth/depthFragmentShader.shader"));
        depthShaderProgram.link();

        depthShaderProgram.createUniforms("orthoProjectionMatrix");
        depthShaderProgram.createUniforms("modelLightViewMatrix");
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
        skyBoyShaderProgram.createVertexShader(Resources.loadResource("/shader/skyboxShader/skyboxVertexShader.shader"));
        skyBoyShaderProgram.createFragmentShader(Resources.loadResource("/shader/skyboxShader/skyboxFragmentShader.shader"));
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



        // render depth map before the view port has been set up
        renderDepthMap(window, camera, scene);

        GL11.glViewport(0,0,window.getWidth(),window.getHeight());
        if(window.isResized()){
            window.setResized(false);
            hud.updateBounds();
        }

        transformation.updateProjectionMatrix(FOV, window.getWidth(),window.getHeight(),Z_NEAR,Z_FAR);
        transformation.updateViewMatrix(camera);

        renderScene(scene);

        renderSkyBox(scene);

        glClear( GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        renderHud(window, hud);

        glColorMask(true,true,true,true);


    }

    public void renderDepthMap(Window window, Camera camera, Scene scene) {

        // set the viewport to batch the depth texture dimensions
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);


        // clear the depth buffer
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = scene.getLightHandler().getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        // calculates the light position and the rotation angles
        float lightAngleX = (float) Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float) Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCords orthoCords = light.getOrthoCords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthoCords.left, orthoCords.right, orthoCords.bottom, orthoCords.top, orthoCords.near, orthoCords.far);

        depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(gameItem, lightViewMatrix);
                depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
            });
        }

        // unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        sceneShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        //Update viewMatrix
        Matrix4f viewMatrix = transformation.getViewMatrix();

        scene.getLightHandler().renderLights(viewMatrix,sceneShaderProgram);

        sceneShaderProgram.setUniform("fog",scene.getFog());

        sceneShaderProgram.setUniform("texture_sampler",0);

        sceneShaderProgram.setUniform("normalMap",1);

        sceneShaderProgram.setUniform("shadowMap",2);

        //Render each mesh with the associated gameItem
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for(Mesh mesh : mapMeshes.keySet()){
            sceneShaderProgram.setUniform("material",mesh.getMaterial());
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D,shadowMap.getDepthMapTexture().getId());
            mesh.renderList(mapMeshes.get(mesh), gameItem -> {

                if (gameItem instanceof AnimationItem) {
                    sceneShaderProgram.setUniform("isAnimation", 1);
                    for (int i = 0; i < ((AnimationItem) gameItem).getJointTransforms().length; i++) {
                        sceneShaderProgram.setUniform("jointTransforms",((AnimationItem)gameItem).getJointTransforms());
                    }
                    //System.out.println((((AnimationItem)gameItem).getJointTransforms()[1]).transform(new Vector4f(1,1,0,1)));
                } else {
                    sceneShaderProgram.setUniform("isAnimation", 0);
                }



                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix);
                sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

                Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(gameItem, lightViewMatrix);
                sceneShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
            });
        }

        sceneShaderProgram.unbind();


    }

    /**
     * renders sky box
     *
     * @param scene used to get sky box
     */
    @SuppressWarnings("unused")
    private void renderSkyBox(Scene scene){
        if(scene.getSkybox() != null) {
            skyBoyShaderProgram.bind();

            skyBoyShaderProgram.setUniform("texture_sampler", 0);

            //Update Projection Matrix
            Matrix4f projectionMatrix = transformation.getProjectionMatrix();
            skyBoyShaderProgram.setUniform("projectionMatrix", projectionMatrix);
            Skybox skybox = scene.getSkybox();
            Matrix4f viewMatrix = transformation.getViewMatrix();
            viewMatrix.m30(0);
            viewMatrix.m31(0);
            viewMatrix.m32(0);
            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skybox, viewMatrix);
            skyBoyShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            skyBoyShaderProgram.setUniform("ambientLight", scene.getLightHandler().getAmbientLight());

            scene.getSkybox().getMesh().render();

            skyBoyShaderProgram.unbind();
        }
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

        Matrix4f orthographic = transformation.getOrtho2DProjectionMatrix(0,1,1,0,0,-Hud.MAX_IDS);

        hud.render(orthographic,transformation);

        hud.getShaderManager().unbindShader();



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
