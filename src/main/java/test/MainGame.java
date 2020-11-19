package test;

import engine.general.IGameLogic;
import engine.general.Window;
import engine.graph.general.Camera;
import engine.graph.general.Scene;
import engine.graph.light.LightHandler;
import engine.hud.Hud;
import engine.hud.animations.Animation;
import engine.hud.animations.ColorSchemeAnimation;
import engine.hud.animations.DefaultAnimations;
import engine.hud.assets.Edge;
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

        //TextureComponent quad2 = new TextureComponent(new Texture("textures/lockClosed.png", Texture.FilterMode.NEAREST));
        QuadComponent quad2 = new QuadComponent();
        quad2.setMaskMode(SubComponent.MaskMode.USE_TRANSPARENT);
        quad2.setHeightConstraint(0.5f);
        quad2.setWidthConstraint(0.5f);
        quad2.setxPositionConstraint(new RelativeToParentPosition(0.5f));
        quad2.setyPositionConstraint(0.5f);
        quad2.setUseColorShade(true);

        hud.setCurrentKeyInputTarget(quad2);

        quad2.getMouseListener().addLeftButtonAction(e -> {

            if (e.getEvent() == MouseEvent.Event.ENTERED) {
                DefaultAnimations.buttonEnteredAnimation.createForComponent(quad2).startAnimation();
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
        });


        QuadComponent quad3 = new QuadComponent();
        quad3.setHeightConstraint(0.5f);
        quad3.setWidthConstraint(0.5f);
        quad3.setxPositionConstraint(0.5f);
        quad3.setyPositionConstraint(new RelativeToParentPosition(0));
        quad3.setColorScheme(new ColorScheme(Color.RED,Color.BLUE,Color.GREEN,Color.RED));
        quad3.setColor(Color.GREEN);
        quad3.setUseColorShade(false);
        quad2.setCornerSize(0f);
        quad3.setColor(Color.GREEN);


        QuadComponent quad4 = new QuadComponent();
        quad4.setHeightConstraint(0.5f);
        quad4.setWidthConstraint(0.5f);
        quad4.setxPositionConstraint(1);
        quad4.setyPositionConstraint(0);
        quad4.setColors(new Color(1,0,0,0.5f));


        quad2.addComponent(quad3);
        quad2.addComponent(quad4);


        //background.addComponent(quad2);


        TextComponent text = new TextComponent(FontTexture.STANDARD_FONT_TEXTURE);
        text.setText("test");
        //quad2.addComponent(text);

        ScrollView vs = new ScrollView();
        vs.setWidthConstraint(0.5f);
        vs.setHeightConstraint(0.5f);
        QuadComponent back = new QuadComponent();
        back.setHeightConstraint(4);
        back.setWidthConstraint(4);
        back.setxPositionConstraint(0.5f);
        back.setyPositionConstraint(0);

        for(int i = 0; i < 10 ; i++) {
            Button bt = new Button(0.1f,0.1f,0.2f * i ,0.5f,"btn " + i,FontTexture.STANDARD_FONT_TEXTURE);
            back.addComponent(bt);
        }

        vs.setContent(back);


        Button btn = new Button(0.5f,0.5f,0.5f,0.5f,"test",FontTexture.STANDARD_FONT_TEXTURE);
        background.addComponent(vs);

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
