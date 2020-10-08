package engine.general;

import engine.hud.Hud;
import engine.hud.mouse.MouseInput;

public interface IGameLogic {

    /**
     * method to perform actions that have to happen before the first rendering cycle
     *
     * @param window passes the window instance to the game
     * @param hud passes the hud to the game
     * @throws Exception if initialization fails
     */
    void init(Window window, Hud hud) throws Exception;

    /**
     * called every rendering frame to check for inputs
     *
     * @param window window used
     * @param mouseInput to get mouse input
     */
    void input(Window window, MouseInput mouseInput);

    /**
     * called every update frame to update the game
     *
     * @param interval time since last update cycle
     * @param mouseInput mouse input
     */
    void update(float interval, MouseInput mouseInput);

    /**
     * called every rendering frame, used to render
     *
     * @param window to render in
     */
    void render(Window window);

    /**
     * should contain the cleanup for all the resource used
     */
    void cleanup();
}
