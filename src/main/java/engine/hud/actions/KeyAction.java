package engine.hud.actions;

/**
 * Action for key inputs
 */
public interface KeyAction {

    /**
     * contains the action for the key stroke, gets the pressed key as parameter
     * @param keyCode pressed key
     */
    void execute(int keyCode);
}
