package test;

import engine.general.GameEngine;
import engine.general.Window;

public class Main {

    /**
     <?xml version="1.0" encoding="UTF-8"?>
     <project xmlns="http://maven.apache.org/POM/4.0.0"
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
     <modelVersion>4.0.0</modelVersion>

     <groupId>engine</groupId>
     <artifactId>engine</artifactId>
     <version>1.0-SNAPSHOT</version>
     <build>
     <plugins>
     <plugin>
     <groupId>org.apache.maven.plugins</groupId>
     <artifactId>maven-compiler-plugin</artifactId>
     <configuration>
     <source>8</source>
     <target>8</target>
     </configuration>
     </plugin>
     </plugins>
     </build>
     <dependencies>
     <dependency>
     <groupId>org.jetbrains</groupId>
     <artifactId>annotations</artifactId>
     <version>RELEASE</version>
     <scope>compile</scope>
     </dependency>
     </dependencies>


     </project>
     */

    public static void main(String[] args) {

        MainGame mainGame = new MainGame();

        GameEngine gameEngine = null;

        try {
            gameEngine = new GameEngine("Burg Spiel",500,500,true,mainGame, Window.WindowPreset.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(gameEngine).start();

    }
}


