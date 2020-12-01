package test;

import engine.general.IGameLogic;
import engine.general.OBJLoader;
import engine.general.Window;
import engine.graph.general.Camera;
import engine.graph.general.Scene;
import engine.graph.items.GameItem;
import engine.graph.items.Texture;
import engine.graph.light.LightHandler;
import engine.hud.Hud;
import engine.hud.assets.Quad;
import engine.hud.color.Color;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.contentcomponents.TextComponent;
import engine.hud.components.contentcomponents.TextInputComponent;
import engine.hud.components.presets.*;
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

        QuadComponent quadComponent = new QuadComponent();
        quadComponent.setColor(Color.WHITE);

        DropDownMenu<Quad> drop = new DropDownMenu<>();
        for(int i = 0;i < 20 ; i ++) {
            drop.addElement(i + "test",new Quad());
        }
        //quadComponent.addComponent(drop);

        hud.getScene().addComponent(quadComponent);

        TextComponent textComponent = new TextComponent(FontTexture.STANDARD_FONT_TEXTURE);
        textComponent.setText("test");
        //quadComponent.addComponent(textComponent);

        TextInputBox textBox = new TextInputBox(FontTexture.STANDARD_FONT_TEXTURE);
        textBox.setHeightConstraint(0.5f);
        textBox.setWidthConstraint(0.9f);
        quadComponent.addComponent(textBox);

        TextInputComponent textin = new TextInputComponent(FontTexture.STANDARD_FONT_TEXTURE);
        textin.setColors(Color.RED);
        textin.setHeightConstraint(0.5f);
        textin.setWidthConstraint(1);
        textin.setXPositionConstraint(0.5f);
        textin.setYPositionConstraint(0.5f);
        textin.setText("test");
        //quadComponent.addComponent(textin);


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
