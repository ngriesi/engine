package engine.hud.text;

import engine.general.save.Resources;
import engine.graph.items.Texture;

import java.util.HashMap;
import java.util.List;

public class FontTexture {

    public static FontTexture STANDARD_FONT_TEXTURE = new FontTexture("font/arial.png","/font/arial.fnt");

    private HashMap<Integer,CharData> charData;

    private Texture texture;

    private String textureFile, fontDataFile;

    private int height;

    /**
     * saves the data from the fontDataFile and loads the font texture
     *
     * @param textureFile texture for the text (sign distance field)
     * @param fontDataFile data for spacing the letters
     */
    public FontTexture(String textureFile,String fontDataFile) {
        charData = new HashMap<>();
        this.textureFile = textureFile;
        this.fontDataFile = fontDataFile;
        try {
            List<String> strings = Resources.readAllLines(fontDataFile);

            for(String s : strings) {

                if(s.startsWith("common")) {
                    height = Integer.parseInt(s.split("=")[1].split(" ")[0]);
                }

                if(s.startsWith("char id")) {
                    String[] data = s.split("=");

                    charData.put(Integer.parseInt(data[1].split(" ")[0]), new CharData(
                            Integer.parseInt(data[2].split(" ")[0]), //x
                            Integer.parseInt(data[3].split(" ")[0]), //y
                            Integer.parseInt(data[4].split(" ")[0]), //width
                            Integer.parseInt(data[5].split(" ")[0]), //height
                            Integer.parseInt(data[6].split(" ")[0]), //xOffset
                            Integer.parseInt(data[7].split(" ")[0]), //yOffset
                            Integer.parseInt(data[8].split(" ")[0])  //xAdvance
                    ));


                }
            }

            texture = new Texture(Resources.getResourcePath(textureFile), Texture.FilterMode.LINEAR);




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CharData getCharData(char c) {
        return charData.get((int)c);
    }

    Texture getTexture() {
        return texture;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "( "+textureFile+" "+fontDataFile + ")";
    }

    /**
     * an Object of this class saves the data for one letter of a font
     */
    static class CharData {
        private int x;
        private int y;
        private int width;
        private int height;
        private int xOffset;
        private int yOffset;
        private int xAdvance;

        CharData(int x, int y, int width, int height, int xOffset, int yOffset, int xAdvance) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.xAdvance = xAdvance - 12;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @SuppressWarnings("WeakerAccess")
        public int getxOffset() {
            return xOffset;
        }

        @SuppressWarnings("WeakerAccess")
        public int getyOffset() {
            return yOffset;
        }

        @SuppressWarnings("WeakerAccess")
        public int getxAdvance() {
            return xAdvance;
        }
    }
}
