package test;

import engine.general.IGameLogic;
import engine.general.MouseInput;
import engine.general.Window;
import engine.graph.general.Camera;
import engine.graph.general.Scene;
import engine.graph.light.LightHandler;
import engine.hud.Hud;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.presets.Background;
import engine.hud.components.presets.Button;
import engine.hud.text.FontTexture;
import engine.render.Renderer;

public class MainGame implements IGameLogic {


    private Renderer renderer;

    private Hud hud;

    private Window window;

    @Override
    public void init(Window window, Hud hud) throws Exception {
        this.hud = hud;
        this.window = window;
        renderer = new Renderer();
        renderer.init(window,new LightHandler());

        Background background = new Background(new ColorScheme());
        background.getColorSheme().setAllColors(Color.VERY_LIGHT_GRAY);

        Button btn = new Button(0.5f,0.5f,0.5f,0.5f,"Test", FontTexture.STANDARD_FONT_TEXTURE);

        background.addComponent(btn);
        hud.getMainComponent().getContent().addComponent(background);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

    }

    @Override
    public void render(Window window) {
        renderer.render(window,new Camera(),new Scene(),hud);
    }

    @Override
    public void cleanup() {

    }
}
