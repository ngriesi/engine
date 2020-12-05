package engine.hud.text;

import engine.general.save.Resources;
import engine.graph.items.Texture;

import java.util.HashMap;
import java.util.List;

/**
 * class used to load the data for a font texture. It combines the information from a sign distant field png
 * and a font (fnt) file to create a CharData object for all the characters specified in these files
 */
public class FontTexture {

    /**
     * default arial font texture
     */
    public static FontTexture STANDARD_FONT_TEXTURE = new FontTexture("font/arial.png","/font/arial.fnt");

    /**
     * hash map linking the digit of a char with its font data
     */
    private final HashMap<Integer,CharData> charData;

    /**
     * texture storing the sing distant field
     */
    private Texture texture;

    /**
     * locations of the files used to create the font texture
     */
    private final String textureFile;
    private final String fontDataFile;

    /**
     * line height of this font
     */
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

                // gets the line height of the font
                if(s.startsWith("common")) {
                    height = Integer.parseInt(s.split("=")[1].split(" ")[0]);
                }

                if(s.startsWith("char id")) {
                    String[] data = s.split("=");

                    // creates a new CharData object from one line in the .fnt file and puts if on the charData map
                    // with its corresponding char as key
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

            // loads the texture
            texture = new Texture(Resources.getResourcePath(textureFile), Texture.FilterMode.LINEAR);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the CharData object for the passed char
     *
     * @param c char of which the information is needed
     * @return returns CharData of the char
     */
    CharData getCharData(char c) {
        return charData.get((int)c);
    }

    /**
     * returns the sign distant field texture
     *
     * @return sing distant field as texture
     */
    Texture getTexture() {
        return texture;
    }

    /**
     * returns the line height of the font
     *
     * @return height of a line in this font
     */
    public int getHeight() {
        return height;
    }

    /**
     * returns the names of the files which were used to create this FontTexture
     *
     * @return filenames of the font file and the sign distant field png
     */
    @Override
    public String toString() {
        return "( "+textureFile+" "+fontDataFile + ")";
    }

    /**
     * an Object of this class saves the data for one letter of a font
     */
    static class CharData {

        /**
         * x position of the char in the texture
         */
        private final int x;

        /**
         * y position of the char in the texture
         */
        private final int y;

        /**
         * width of the char in the texture
         */
        private final int width;

        /**
         * height of the char in the texture
         */
        private final int height;

        /**
         * x offset of the char when drawn to a mesh (Quad)
         */
        private final int xOffset;

        /**
         * y offset of the char when drawn to a mesh (Quad)
         */
        private final int yOffset;

        /**
         * defines where the next char should go on the mesh
         */
        private final int xAdvance;

        /**
         * constructor setting all values
         *
         * @param x x position of the char in the texture
         * @param y y position of the char in the texture
         * @param width width of the char in the texture
         * @param height height of the char in the texture
         * @param xOffset x offset of the char when it gets drawn to a mesh (quad)
         * @param yOffset y offset of the char when it gets drawn to a mesh (quad)
         * @param xAdvance defines where the next char in the line should be positioned
         */
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
        public int getXOffset() {
            return xOffset;
        }

        @SuppressWarnings("WeakerAccess")
        public int getYOffset() {
            return yOffset;
        }

        @SuppressWarnings("WeakerAccess")
        public int getXAdvance() {
            return xAdvance;
        }
    }
}
