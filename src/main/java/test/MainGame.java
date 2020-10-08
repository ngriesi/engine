package test;

import engine.general.IGameLogic;
import engine.hud.actions.KeyAction;
import engine.hud.mouse.MouseAction;
import engine.hud.mouse.MouseEvent;
import engine.hud.mouse.MouseInput;
import engine.general.Window;
import engine.graph.general.Camera;
import engine.graph.general.Scene;
import engine.graph.light.LightHandler;
import engine.hud.Hud;
import engine.hud.animations.Animation;
import engine.hud.animations.ColorSchemeAnimation;
import engine.hud.color.Color;
import engine.hud.color.ColorScheme;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.presets.Background;
import engine.hud.components.presets.Button;
import engine.hud.constraints.positionConstraints.RelativeToParentPosition;
import engine.hud.text.FontTexture;
import engine.render.Renderer;

public class MainGame implements IGameLogic {


    private Renderer renderer;

    private Hud hud;

    private Window window;

    private QuadComponent[] quads;


    @Override
    public void init(Window window, Hud hud) throws Exception {
        this.hud = hud;
        this.window = window;
        renderer = new Renderer();
        renderer.init(window,new LightHandler());

        Background background = new Background(new ColorScheme());
        background.getColorSheme().setAllColors(Color.VERY_LIGHT_GRAY);
        background.setUseColorShade(false);

        ColorSchemeAnimation colorAnimation = new ColorSchemeAnimation(hud,60,new ColorScheme(Color.RED,Color.BLUE,Color.GREEN,Color.RED),
                new ColorScheme(Color.GREEN,Color.BLUE,Color.RED,Color.WHITE));


        quads = new QuadComponent[0];
        for(QuadComponent quad : quads) {
            quad = new QuadComponent();
            quad.setHeightConstraint(0.2f);
            quad.setWidthConstraint(0.2f);
            quad.setxPositionConstraint((float) Math.random());
            quad.setyPositionConstraint((float)Math.random());
            quad.setColorScheme(new ColorScheme(Color.RED, Color.BLUE, Color.GREEN, Color.RED));
            quad.setUseColorShade(true);


            Animation start = colorAnimation.createForComponent(quad);
            Animation end = colorAnimation.getInverted().createForComponent(quad);

            start.setOnFinishedAction(end::startAnimation);
            end.setOnFinishedAction(start::startAnimation);

            background.addComponent(quad);


            end.startAnimation();
        }

        QuadComponent quad2 = new QuadComponent();
        quad2.setHeightConstraint(0.5f);
        quad2.setWidthConstraint(0.5f);
        quad2.setxPositionConstraint(new RelativeToParentPosition(1));
        quad2.setyPositionConstraint(0.5f);
        quad2.setColorScheme(new ColorScheme(Color.BLUE,Color.BLUE,Color.BLUE,Color.RED));
        quad2.setUseColorShade(false);

        quad2.getMouseListener().addLeftButtonAction(new MouseAction() {
            @Override
            public boolean action(MouseEvent e) {

                if (e.getEvent() == MouseEvent.Event.ENTERED) {
                    quad2.setColors(Color.RED);
                    hud.needsNextRendering();
                }
                if (e.getEvent() == MouseEvent.Event.DRAG_STARTED) {
                    quad2.setColors(Color.WHITE);
                    hud.needsNextRendering();
                    return true;
                }
                if (e.getEvent() == MouseEvent.Event.EXITED) {
                    quad2.setColors(Color.BLUE);
                    hud.needsNextRendering();
                }
                if (e.getEvent() == MouseEvent.Event.CLICK_STARTED) {
                    quad2.setColors(Color.GREY);
                    hud.needsNextRendering();
                }
                if (e.getEvent() == MouseEvent.Event.CLICK_RELEASED) {
                    quad2.setColors(Color.YELLOW);
                    hud.needsNextRendering();
                }
                if (e.getEvent() == MouseEvent.Event.PRESS_STARTED) {
                    quad2.setColors(Color.PINK);
                    hud.needsNextRendering();
                }
                if (e.getEvent() == MouseEvent.Event.PRESS_RELEASED) {
                    quad2.setColors(Color.GREEN);
                    hud.needsNextRendering();
                    return true;
                }
                return false;
            }
        });

        quad2.getKeyListener().setKeyAction(new KeyAction() {
            @Override
            public void execute(int keyCode) {
                System.out.println(keyCode);
            }
        });

        QuadComponent quad3 = new QuadComponent();
        quad3.setHeightConstraint(0.5f);
        quad3.setWidthConstraint(0.5f);
        quad3.setxPositionConstraint(0.5f);
        quad3.setyPositionConstraint(new RelativeToParentPosition(0));
        quad3.setColorScheme(new ColorScheme(Color.RED,Color.BLUE,Color.GREEN,Color.RED));
        quad3.setUseColorShade(false);

        quad2.addComponent(quad3);
        background.addComponent(quad2);




        Button btn = new Button(0.5f,0.5f,0.5f,0.5f,"test",FontTexture.STANDARD_FONT_TEXTURE);
        background.addComponent(btn);

        hud.getScene().addComponent(background);





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
