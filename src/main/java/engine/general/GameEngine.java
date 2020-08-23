package engine.general;

import engine.hud.Hud;

public class GameEngine implements Runnable{

    /**frames per second (rendering and input) */
    @SuppressWarnings("WeakerAccess")
    public static final int TARGET_FPS = 60;

    /** updates per second */
    @SuppressWarnings("WeakerAccess")
    public static final int TARGET_UPS = 30;

    /** window used*/
    private final Window window;

    /** mouse input */
    private final MouseInput mouseInput;

    /**timer of loop times*/
    private final Timer timer;

    /** interface implemented by the game class */
    private final IGameLogic gameLogic;

    /**hud of the window */
    private final Hud hud;

    /** value needed for time calculations */
    private float accumulator;
    @SuppressWarnings("FieldCanBeLocal")
    private float interval = 1f/TARGET_UPS;
    @SuppressWarnings("FieldCanBeLocal")
    private float elapsedTime;

    /**
     * constructor uses parameters to create the window
     * it also gets the gameLogic passed
     *
     * @param windowTitle for window
     * @param width for window
     * @param height for window
     * @param vSync for window
     * @param gameLogic the game
     * @param preset for window
     */
    public GameEngine(String windowTitle, int width, int height, boolean vSync, IGameLogic gameLogic, Window.WindowPreset preset) {
        window = new Window(windowTitle,width,height,vSync,preset,this);
        this.gameLogic = gameLogic;
        timer = new Timer();
        mouseInput = new MouseInput();
        hud = new Hud(window);
    }

    /**
     * run method of the main game loop thread
     *
     * initialises everything and runs the loop
     *
     * cleans up at the end
     */
    @Override
    public void run() {
        try{
            init();
            gameLoop();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    /**
     * initialises all the used objects
     *
     * @throws Exception if game logic initialization fails
     */
    private void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        hud.init(window);
        gameLogic.init(window,hud);
        hud.needsNextRendering();
    }

    /**
     * main game loop
     */
    private void gameLoop(){

        accumulator = 0f;


        boolean running = true;
        //noinspection ConstantConditions
        while (running && !window.windowShouldClose()){


            frameAction();

            if(!window.isvSync() || (!hud.isNeedsRendering() && !window.isRenderAlways())){
                sync();
            }
        }
    }

    void frameAction() {

        elapsedTime = timer.getElapsedTime();
        accumulator += elapsedTime;

        input();

        while (accumulator >= interval){
            update(interval);
            accumulator -= interval;
        }

        render();
    }

    /**
     * used to calculate the waiting time if vSync isn't used
     */
    private void sync(){
        float loopSlot = 1f/TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }
    }

    /**calls the input of the objects */
    protected void input(){
        mouseInput.input(window);
        gameLogic.input(window,mouseInput);
        hud.input(window,mouseInput);
    }

    /**
     * updates the game
     *
     * @param interval used to pass the time it took since the last update cycle
     */
    protected  void update(float interval){

        gameLogic.update(interval,mouseInput);
        if(window.isResized()) {
            hud.getMainComponent().updateBounds();
        }
        hud.update(interval);

    }

    /**
     * renders the game
     */
    protected void render(){

        hud.removeComponents();
        if(window.isRenderAlways() || hud.isNeedsRendering() || window.isResized()) {
            gameLogic.render(window);
            window.swapBuffers();
            hud.wasRendered();

        }
        window.events();



    }

    /**
     * calls the cleanup method of the game
     */
    private void cleanup(){
        gameLogic.cleanup();
        hud.cleanup();
    }
}
