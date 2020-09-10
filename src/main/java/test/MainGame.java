package test;

import engine.general.IGameLogic;
import engine.general.MouseInput;
import engine.general.Window;
import engine.graph.general.Camera;
import engine.graph.general.Scene;
import engine.graph.light.LightHandler;
import engine.hud.Hud;
import engine.hud.animations.Animation;
import engine.hud.animations.ColorSchemeAnimation;
import engine.hud.assets.Quad;
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

        quads = new QuadComponent[1];
        for(QuadComponent quad : quads) {
            quad = new QuadComponent();
            quad.setHeightConstraint(0.2f);
            quad.setWidthConstraint(0.2f);
            quad.setxPositionConstraint((float) Math.random());
            quad.setyPositionConstraint((float)Math.random());
            quad.setColorScheme(new ColorScheme(Color.BLUE, Color.WHITE, Color.RED, Color.ORANGE));
            quad.setUseColorShade(true);

            ColorSchemeAnimation colorAnimation = new ColorSchemeAnimation(hud,60,new ColorScheme(Color.RED,Color.GREEN,Color.YELLOW,Color.RED),
                    new ColorScheme(Color.BLUE,Color.WHITE,Color.RED,Color.ORANGE));

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

        QuadComponent quad3 = new QuadComponent();
        quad3.setHeightConstraint(0.5f);
        quad3.setWidthConstraint(0.5f);
        quad3.setxPositionConstraint(0.5f);
        quad3.setyPositionConstraint(new RelativeToParentPosition(0));
        quad3.setColorScheme(new ColorScheme(Color.RED,Color.BLUE,Color.GREEN,Color.RED));
        quad3.setUseColorShade(false);

        quad2.addComponent(quad3);
        background.addComponent(quad2);

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
