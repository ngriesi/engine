package test;

import engine.general.blender.BlenderLoader;
import engine.general.IGameLogic;
import engine.general.OBJLoader;
import engine.general.Window;
import engine.graph.environment.Terrain;
import engine.graph.general.Camera;
import engine.graph.general.Scene;
import engine.graph.items.GameItem;
import engine.graph.items.Material;
import engine.graph.items.Mesh;
import engine.graph.items.Texture;
import engine.graph.items.animation.AnimationItem;
import engine.graph.items.animation.Animator;
import engine.graph.items.animation.JointTransform;
import engine.graph.light.DirectionalLight;
import engine.graph.light.LightHandler;
import engine.hud.Hud;
import engine.hud.mouse.MouseInput;
import engine.hud.mouse.MouseListener;
import engine.render.Renderer;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;


public class MainGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private Hud hud;

    private static final float CAMERA_POS_STEP = 0.05f;

    private Terrain terrain;

    private GameItem cubeGameItem;

    private float angleInc;

    private float lightAngle;

    private LightHandler lightHandler;

    private Animator animator;

    public MainGame() {
        renderer = new Renderer();
        camera = new Camera();
        camera.setPosition(0,2,0);
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 45;
    }

    @Override
    public void init(Window window, Hud hud) throws Exception {
        lightHandler = new LightHandler();
        this.hud  = hud;
        renderer.init(window,lightHandler);


        window.setRenderAlways(true);



        scene = new Scene();

        // Setup  GameItems
        float reflectance = 1f;
        Mesh cubeMesh = OBJLoader.loadNoDoubles("/models/test.obj");
        Material cubeMaterial = new Material(new Vector4f(0, 1, 0, 1), reflectance);
        Material test = new Material(new Texture("textures/wood.png", Texture.FilterMode.NEAREST));
        test.setNormalMap(new Texture("textures/normalmapWood.png", Texture.FilterMode.NEAREST));
        cubeMesh.setMaterial(test);
        cubeGameItem = BlenderLoader.loadGameItemsFromBlendFile("test.blend")[0];
        cubeGameItem.setPosition(0, 1, 0);
        cubeGameItem.setScale(0.5f);

        Mesh quadMesh = OBJLoader.loadMesh("/models/test.obj");
        Material quadMaterial = new Material(new Vector4f(0.0f, 0.0f, 1.0f, 10.0f), reflectance);
        quadMesh.setMaterial(quadMaterial);
        GameItem quadGameItem = new GameItem(quadMesh);
        quadGameItem.setPosition(0, -10, 0);
        quadGameItem.setScale(2.5f);

        Terrain terrain = new Terrain(5, 5, 0, 0.2f, "/heightmap.png", "textures/terrain.png", 3);
        scene.setGameItems(terrain.getGameItems());

        scene.setGameItems(new GameItem[]{ quadGameItem});

        // Setup Lights
        setupLights();

        camera.getPosition().z = 2;

        animator = BlenderLoader.loadAnimationFromBlendFile("simpelAni.blend");

        animator.getEntity().setPosition(0,3,0);

        scene.setGameItems(new GameItem[]{animator.getEntity()});


        System.out.println(getLocalTransform(new Vector3f(0,0,2),new Quaternionf(0.7,0,0,0.7)));


    }

    private void setupLights() {
        scene.setLightHandler(lightHandler);

        // Ambient Light
        lightHandler.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        directionalLight.setShadowPosMult(5);
        directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
        lightHandler.setDirectionalLight(directionalLight);
    }


    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            angleInc -= 0.05f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            angleInc += 0.05f;
        } else {
            angleInc = 0;
        }

        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            animator.update();
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {


        //animator.update();
        // Update camera based on mouse
        if (mouseInput.isPressed(MouseListener.MouseButton.LEFT)) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            camera.moveRotation(rotVec.x * MainGame.MOUSE_SENSITIVITY, rotVec.y * MainGame.MOUSE_SENSITIVITY, 0);
        }

        // Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * MainGame.CAMERA_POS_STEP, cameraInc.y * MainGame.CAMERA_POS_STEP, cameraInc.z * MainGame.CAMERA_POS_STEP);
        // Check if there has been a collision. If true, set the y position to
        // the maximum height
        float height = terrain != null ? terrain.getHeight(camera.getPosition()) : -Float.MAX_VALUE;
        if (camera.getPosition().y <= height) {
            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
        }

        float rotY = cubeGameItem.getRotation().y;
        rotY += 0.5f;
        if (rotY >= 360) {
            rotY -= 360;
        }
        cubeGameItem.getRotation().y = rotY;

        lightAngle += angleInc;
        if (lightAngle < 0) {
            lightAngle = 0;
        } else if (lightAngle > 180) {
            lightAngle = 180;
        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getLightHandler().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();
        float lightAngle = (float) Math.toDegrees(Math.acos(lightDirection.z));

    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }


    /**
     * constructs the bone space transformation matrix
     */
    public Matrix4f getLocalTransform(Vector3f position, Quaternionf rotation) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate(position);
        matrix4f.mul(toRotation(rotation),matrix4f);

        return matrix4f;
    }

    /**
     * Method turns a Quaternion into a rotation matrix
     */
    private Matrix4f toRotation(Quaternionf q) {
        Matrix4f matrix = new Matrix4f();
        final float xy = q.x * q.y;
        final float xz = q.x * q.z;
        final float xw = q.x * q.w;
        final float yz = q.z * q.y;
        final float yw = q.w * q.y;
        final float zw = q.w * q.z;
        final float xSquare = q.x * q.x;
        final float ySquare = q.y * q.y;
        final float zSquare = q.z * q.z;
        matrix.m00(1 - 2 * (ySquare + zSquare));
        matrix.m01(2 * (xy - zw));
        matrix.m02(2 * (xz + yw));
        matrix.m03(0);

        matrix.m10(2 * (xy + zw));
        matrix.m11(1 - 2 * (xSquare + zSquare));
        matrix.m12(2 * (yz - xw));
        matrix.m13(0);

        matrix.m20(2 * (xz - yw));
        matrix.m21(2 * (yz + xw));
        matrix.m22(1 - 2 * (xSquare + ySquare));
        matrix.m23(0);

        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
        matrix.m33(1);

        return matrix;
    }
}