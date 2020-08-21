package test;

import engine.general.GameEngine;
import engine.general.Window;

public class Main {

    public static void main(String[] args) {

        MainGame mainGame = new MainGame();

        GameEngine gameEngine = null;

        try {
            gameEngine = new GameEngine("Burg Spiel",1200,600,true,mainGame, Window.WindowPreset.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(gameEngine).start();

    }
}
