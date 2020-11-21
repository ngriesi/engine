package engine.hud.actions;

/**
 * simple interface for passing Methods with boolean return type
 */
public interface ReturnAction {

    /**
     * contains the action (method call) that needs to be passed
     * to an engine class
     *
     * @return should return method result
     */
    boolean execute();
}
