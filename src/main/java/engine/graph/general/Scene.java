package engine.graph.general;


import engine.graph.environment.Fog;
import engine.graph.environment.Skybox;
import engine.graph.items.GameItem;
import engine.graph.items.Mesh;
import engine.graph.light.LightHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    /** content of the scene */
    @SuppressWarnings("unused")
    private GameItem[] gameItems;

    /** skybox of scene */
    private Skybox skybox;

    /** handles lights of the scene */
    private LightHandler lightHandler;

    /** the fog of the scene */
    private Fog fog;

    /**
     * map of meshes with game items used for rendering of lists of meshes to improve performance
     */
    private final Map<Mesh, List<GameItem>> meshMap;

    /**
     * constructor creates hashMap and lightHandler
     */
    public Scene(){
        lightHandler = new LightHandler();
        meshMap = new HashMap<>();
        fog = Fog.NO_FOG;
    }

    /**
     * @return returns list of game items used for single mesh rendering
     */
    @SuppressWarnings("unused")
    public GameItem[] getGameItems() {
        return gameItems;
    }

    /**
     * adds array of game items to the map for list rendering
     *
     * @param gameItems to be added
     */
    @SuppressWarnings("unused")
    public void setGameItems(GameItem[] gameItems) {
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for(int i = 0; i< numGameItems;i++){
            GameItem gameItem = gameItems[i];
            Mesh mesh = gameItem.getMesh();
            List<GameItem> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
            list.add(gameItem);
        }
    }

    /**
     * @param skybox new skybox
     */
    @SuppressWarnings("unused")
    public void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    /**
     * @param lightHandler new lightHandler
     */
    @SuppressWarnings("unused")
    public void setLightHandler(LightHandler lightHandler) {
        this.lightHandler = lightHandler;
    }

    /**
     * @return returns sky box
     */
    public Skybox getSkybox() {
        return skybox;
    }

    /**
     * @return returns light handler
     */
    public LightHandler getLightHandler() {
        return lightHandler;
    }

    /**
     * @return returns hash map for list rendering
     */
    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }
}
