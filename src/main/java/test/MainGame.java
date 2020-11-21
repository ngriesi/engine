package test;

import engine.general.IGameLogic;
import engine.general.OBJLoader;
import engine.general.Window;
import engine.graph.general.Camera;
import engine.graph.general.Scene;
import engine.graph.items.GameItem;
import engine.graph.items.Mesh;
import engine.graph.items.Texture;
import engine.graph.light.LightHandler;
import engine.hud.Hud;
import engine.hud.animations.Animation;
import engine.hud.animations.ColorSchemeAnimation;
import engine.hud.animations.DefaultAnimations;
import engine.hud.assets.Edge;
import engine.hud.assets.Quad;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.SubComponent;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.contentcomponents.TextComponent;
import engine.hud.components.presets.*;
import engine.hud.constraints.elementSizeConstraints.ElementSizeConstraint;
import engine.hud.constraints.elementSizeConstraints.RelativeToComponentSizeE;
import engine.hud.constraints.elementSizeConstraints.RelativeToScreenSizeE;
import engine.hud.constraints.positionConstraints.RelativeToParentPosition;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseInput;
import engine.hud.mouse.MouseListener;
import engine.hud.text.FontTexture;
import engine.render.Renderer;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class MainGame implements IGameLogic {


    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.2f;
    private Renderer renderer;

    private Hud hud;

    private Window window;

    private QuadComponent[] quads;

    private Scene scene;

    private Camera camera;

    private final Vector3f cameraInc;

    public MainGame() {
        this.cameraInc = new Vector3f();
    }

    @Override
    public void init(Window window, Hud hud) throws Exception {
        this.hud = hud;
        this.window = window;
        renderer = new Renderer();
        renderer.init(window,new LightHandler());
        camera = new Camera();

        hud.getScene().addComponent(new QuadComponent());


        scene = new Scene();

        int i = 3;
        GameItem qd = new GameItem(OBJLoader.loadNoDoubles("/models/test.obj"));
        qd.setPosition(0,0,-i);
        qd.getMesh().getMaterial().setTexture(new Texture("textures/wood.png", Texture.FilterMode.NEAREST));
        qd.setScale(1);



        scene.setGameItems(new GameItem[] {qd});



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
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse            
        if (mouseInput.isPressed(MouseListener.MouseButton.LEFT)) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            hud.needsNextRendering();
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window,camera,scene,hud);
    }

    @Override
    public void cleanup() {

    }
}
