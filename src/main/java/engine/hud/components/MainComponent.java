package engine.hud.components;

import engine.general.MouseInput;
import engine.general.Transformation;
import engine.general.Window;
import engine.hud.Hud;
import engine.hud.HudShaderManager;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;

public class MainComponent extends Component {


    private SceneComponent content;

    /**
     * constructor used to pass window to top most component
     *
     * @param window window of the hud
     */
    public MainComponent(Window window, Hud hud) {
        super();
        super.setWindow(window);
        super.setHud(hud);

        setOnScreenHeight(1);
        setOnScreenWidth(1);
        setOnScreenXPosition(0.5f);
        setOnScreenYPosition(0.5f);
    }

    /**
     * calls the render Method of the scene component currently used
     *
     * @param ortho transformation matrix
     * @param transformation class
     * @param shaderManager of the hud
     */
    public void render(Matrix4f ortho, Transformation transformation, HudShaderManager shaderManager) {
        shaderManager.setShaderProgram(shaderManager.getMaskShader());

        content.render(ortho,transformation,shaderManager);
    }

    /**
     * sets a new scene component as the content of the hud
     *
     * @param component new scene
     */
    public void setContent(SceneComponent component) {
        this.content = component;
        this.content.setWindow(this.getWindow());
        this.content.setHud(this.getHud());
        this.content.setParent(this);
        this.content.updateBounds();
        hud.needsNextRendering();
    }

    /**
     * method called in hud, starts the mouse input
     *
     * @param window of this main component
     * @param mouseInput mouse input of this window
     */
    public void input(Window window, MouseInput mouseInput) {
        if(content != null) {
            content.input(window,mouseInput);
        }
    }

    public SceneComponent getContent() {
        return content;
    }

    public void cleanup() {
        if(content != null) {
            content.cleanup();
        }
    }


    /**
     * updates the positions of the components
     */
    public void updateBounds() {
        if(content != null) {
            content.updateBounds();
        }
    }
}
