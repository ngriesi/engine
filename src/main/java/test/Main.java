package test;

import engine.general.GameEngine;
import engine.general.Window;

public class Main {


    public static void main(String[] args) {

        MainGame mainGame = new MainGame();

        System.out.println("test");

        GameEngine gameEngine = null;

        try {
            gameEngine = new GameEngine("Burg Spiel",500,500,true,mainGame, Window.WindowPreset.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(gameEngine).start();

    }
}


