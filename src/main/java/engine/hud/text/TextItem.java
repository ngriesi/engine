package engine.hud.text;

import engine.general.Utils;
import engine.graph.items.GameItem;
import engine.graph.items.Material;
import engine.graph.items.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class TextItem extends GameItem {



    /**
     * enums for text alignment
     */
    public enum TextAlignment {
        LEFT,CENTER,RIGHT
    }

    private static final int VERTICAL_PADDING_ZERO = 30;

    /**
     * constants to create the mesh
     */
    private static final float Z_POS = 0;
    private static final int VERTICES_PER_QUAD = 4;
    private static final int START_PADDING = 4;

    /** texture of the font */
    private FontTexture fontTexture;

    /** width of the longest line */
    private int maxLineWidth;

    /** number of lines */
    private int lines;

    /** y Position of the text item */
    private int yPos;

    /** content of the text item */
    @SuppressWarnings("unused")
    private String text;

    /** determines alignment of the text */
    private TextAlignment alignment;

    /** stores the data of each line in LineData objects */
    private List<LineData> lineData;

    /** array of vertex positions of the letters */
    private float[] posArr;

    /** array of texture coordinates for the letters */
    private float[] texArr;

    /** normals array (only one default vector for all */
    private float[] normals;

    /** indices array for vertices */
    private int[] indicesArr;

    /** next free index for the vertices of a letter that gets added */
    private int nextIndex;

    /** padding between lines */
    private int verticalPadding = 10;

    public TextItem(FontTexture fontTexture) {
        this.fontTexture = fontTexture;
        alignment = TextAlignment.CENTER;
        lineData = new ArrayList<>();
        posArr = new float[0];
        texArr = new float[0];
        normals = new float[0];
        indicesArr = new int[0];
    }

    /**
     * sets text at a specific y cord
     *
     * @param text new text for the item
     * @param yPos new y Position
     */
    @SuppressWarnings("unused")
    public void setTextAt(String text, int yPos) {

        this.text = text;
        this.yPos = yPos;
        this.setMesh(buildMesh(text));
    }

    /**
     * Method builds the mesh for the text item when it gets created
     * or the complete text gets changed
     *
     * @param text of the item
     * @return the mesh for the item
     */
    private Mesh buildMesh(String text) {

        nextIndex = 0;

        lineData = new ArrayList<>();

        List<Float> positions = new ArrayList<>();
        List<Float> textCords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        String[] lines = text.split("\n",-1);

        this.lines = lines.length;

        yPos = (int) ( -getLineHeight() * this.lines/2);


        int startX;

        int xStart = 0;

        int[] indicesLine;

        for(String line : lines) {
            char[] chars = line.toCharArray();

            indicesLine = new int[line.length()];

            switch (alignment) {
                case CENTER:xStart = -getLineWidth(line)/2;break;
                case LEFT:xStart = 0;break;
                case RIGHT:xStart = -getLineWidth(line);
            }

            startX = xStart + START_PADDING;

            int count = 0;

            for (char c : chars) {

                FontTexture.CharData charData = fontTexture.getCharData(c);

                indicesLine[count] = nextIndex;
                count++;

                int yOffset = charData.getyOffset() -(VERTICAL_PADDING_ZERO - verticalPadding) /2;

                // Top left vertex
                positions.add((float) (startX + charData.getxOffset())); // x
                positions.add((float) (yPos + yOffset)); // y
                positions.add(Z_POS); // z
                textCords.add((float) (charData.getX()) / 512.0f); // texture x
                textCords.add((float) (charData.getY()) / 512.0f); // texture y
                indices.add(nextIndex * VERTICES_PER_QUAD);

                // Bottom left vertex
                positions.add((float) (startX + charData.getxOffset())); // x
                positions.add((float) (yPos + yOffset + charData.getHeight())); // y
                positions.add(Z_POS); // z
                textCords.add((float) (charData.getX()) / 512.0f); // texture x
                textCords.add((float) (charData.getY() + charData.getHeight()) / 512.0f); // texture y
                indices.add(nextIndex * VERTICES_PER_QUAD + 1);

                // Top right vertex
                positions.add((float) (startX + charData.getxOffset() + charData.getWidth())); // x
                positions.add((float) (yPos + yOffset)); // y
                positions.add(Z_POS); // z
                textCords.add((float) (charData.getX() + charData.getWidth()) / 512.0f); // texture x
                textCords.add((float) (charData.getY()) / 512.0f); // texture y
                indices.add(nextIndex * VERTICES_PER_QUAD + 2);

                // Bottom right vertex
                positions.add((float) (startX + charData.getxOffset() + charData.getWidth())); // x
                positions.add((float) (yPos + yOffset + charData.getHeight())); // y
                positions.add(Z_POS); // z
                textCords.add((float) (charData.getX() + charData.getWidth()) / 512.0f); // texture x
                textCords.add((float) (charData.getY() + charData.getHeight()) / 512.0f); // texture y
                indices.add(nextIndex * VERTICES_PER_QUAD + 3);

                //add indices for left top and bottom right vertices
                indices.add(nextIndex * VERTICES_PER_QUAD + 2);
                indices.add(nextIndex * VERTICES_PER_QUAD + 1);

                nextIndex++;
                startX += charData.getxAdvance();
            }

            lineData.add(new LineData(line,xStart,indicesLine,yPos));


            yPos += getLineHeight();
        }

        maxLineWidth = calculateMaxWidth();

        posArr = Utils.listToArray(positions);
        texArr = Utils.listToArray(textCords);
        indicesArr = indices.stream().mapToInt(i->i).toArray();
        Mesh mesh = new Mesh(posArr,texArr,normals,indicesArr);
        mesh.setMaterial(new Material(fontTexture.getTexture()));
        return mesh;
    }

    /**
     * calculates the width of a specific line
     *
     * @param line to calculate the width of
     * @return the absolute width of the unscaled line
     */
    private int getLineWidth(String line) {
        int result = 0;
        for(char c : line.toCharArray()) {
            result += fontTexture.getCharData(c).getxAdvance();
        }
        return result + START_PADDING;
    }

    /**
     * returns the length of a line out of the lineData list
     *
     * @param line index of the line
     * @return line width
     */
    public int getLineLength(int line) {
        return lineData.get(line).line.length();
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

    /**
     * calculates the maximum width of the text item
     *
     * @return width (int)
     */
    private int calculateMaxWidth() {
        int result = 0;
        for(LineData data : lineData) {
            int t = getLineWidth(data.line);
            result = Math.max(result,t + START_PADDING);
        }

        return result;
    }

    /**
     * sets a new text and rebuilds the mesh
     *
     * @param text new text
     */
    public void setText(String text) {
        this.text = text;
        setMesh(buildMesh(text));
    }

    /**
     * returns the unscaled height of one line of the text
     * (always the same for one font texture)
     *
     * @return height
     */
    public float getLineHeight() {
        return fontTexture.getHeight()- (VERTICAL_PADDING_ZERO - verticalPadding);
    }

    /**
     * returns the width of the item which is equal to the width of the
     * longest line
     *
     * @return maximum width
     */
    public int getWidth() {
        return maxLineWidth;
    }

    public void setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
    }

    /**
     * calculates the height of the item (absolute)
     *
     * @return height
     */
    public int getHeight() {
        return (int) (getLineHeight() * lines);
    }

    /**
     * returns the number of lines
     * @return line number
     */
    public int getLines() {
        return lines;
    }

    /**
     * returns a vector of position coordinates relative to the top left corner of the
     * text item dependent on a cursor position in the text
     *
     * @param x cursor position in line
     * @param y line the cursor is inside
     * @return position Vector
     */
    public Vector2f getCursorPosition(int x, int y) {
        Vector2f result = new Vector2f(0,0);
        y = (lines > 0 && y >= lines)?lines -1:y;

        result.y = -0.5f + 1.0f/lines * (y + 0.5f);

        if(lineData.size() > y) {
            result.x = (lineData.get(y).start + lineData.get(y).getPosition(x)) / (float) maxLineWidth;

        }

        return result;
    }

    /**
     * calculates the index of a cursor position relative to
     * the coordinates x and y. X and y are floats giving the position
     * of the mouse click relative to the text items top left corner
     *
     * @param x Position in text item
     * @param y Position in text item
     * @return the cursor index position
     */
    public Vector2i getCursorPosition(float x, float y) {
        int yIndex;
        int xIndex;

        yIndex = (int) (y / (getLineHeight()/getHeight()));

        LineData line = lineData.get(yIndex);

        float temp = getLineWidth(line.line)/(float)maxLineWidth;

        float temp2 = (maxLineWidth - getLineWidth(line.line))/(float)maxLineWidth;

        switch(alignment) {
            case LEFT:x = x/temp;break;
            case RIGHT:x = x;break;
            case CENTER:x = (x - temp2/2)/temp;
        }


        xIndex = line.getCursorPosition(x);

        return new Vector2i(xIndex,yIndex);
    }

    /**
     * adds a letter to a specific line at a specific position
     *
     * @param line where the letter gets added
     * @param position the letter gets added
     * @param letter to add
     */
    public void addLetter(int line,int position,char letter) {
        lineData.get(line).addLetter(letter,position);
        rebuildString();

    }

    /**
     * removes a letter at a specific position
     *
     * @param line in which the letter should be removed
     * @param position of the letter that gets removed
     */
    public void removeLetter(int line,int position) {
        lineData.get(line).removeLetter(position);
        rebuildString();

    }

    /**
     * rebuilds the text string from the line data list
     * after it got changed
     */
    private void rebuildString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lineData.get(0).line);
        for(int i = 1;i < lineData.size();i++) {
            sb.append("\n").append(lineData.get(i).line);
        }
        text = sb.toString();
    }

    /**
     * adds a new line at a specific position
     *
     * @param line determines y position of the new line
     * @param position determines which letters get put in the new line and which stay in the old
     */
    public void addLine(int line,int position) {
        lines++;
        lineData.add(line,new LineData("",START_PADDING,new int[]{}, (int) (getLineHeight() * (-lines/2.0 + line))));
        for(int i = 0; i < lineData.size();i++) {
            if(i < line) {
                lineData.get(i).moveUp();
            } else if(i > line) {
                lineData.get(i).moveDown();
            }
        }
        for(int i = 0;i < position;i++) {
            lineData.get(line).addLetter(lineData.get(line + 1).removeLetter(0),i);
        }
        rebuildString();
    }

    /**
     * removes the line at the given position and adds its content to the line above
     *
     * @param position where the line should be removed
     */
    public void removeLine(int position) {
        lines--;
        String content = lineData.get(position).line;
        lineData.get(position).removeAll();
        lineData.remove(position);
        for(int i = 0; i < lineData.size();i++) {
            if(i < position) {
                lineData.get(i).moveDown();
            } else if(i >= position) {
                lineData.get(i).moveUp();
            }
        }
        for(char c : content.toCharArray()) {
            lineData.get(position - 1).addLetter(c, lineData.get(position - 1).line.length());
        }
    }

    /**
     * @return font texture of the text item
     */
    public FontTexture getFontTexture() {
        return fontTexture;
    }

    @SuppressWarnings("unused")
    public int getVerticalPadding() {
        return verticalPadding;
    }

    @SuppressWarnings("unused")
    public void setVerticalPadding(int verticalPadding) {
        this.verticalPadding = verticalPadding;
    }


    public String getText() {
        return text;
    }

    /**
     * contains the data for each line in the text
     */
    private class LineData {
        private String line;
        private int start;
        private int[] indices;
        private int yPos;

        LineData(String line, int start, int[] indices, int yPos) {
            this.line = line;
            this.start = start;
            this.indices = indices;
            this.yPos = yPos;
        }

        /**
         * moves all the vertices in the line up half the height of a line
         */
        void moveUp() {

            for(int i : indices) {

                posArr[i * VERTICES_PER_QUAD * 3 + 1] -= getLineHeight()/2;
                posArr[i * VERTICES_PER_QUAD * 3 + 4] -= getLineHeight()/2;
                posArr[i * VERTICES_PER_QUAD * 3 + 7] -= getLineHeight()/2;
                posArr[i * VERTICES_PER_QUAD * 3 + 10] -= getLineHeight()/2;
            }

            this.yPos -= getLineHeight()/2;

            Mesh mesh = new Mesh(posArr,texArr,normals,indicesArr);
            mesh.setMaterial(new Material(fontTexture.getTexture()));
            setMesh(mesh);
        }

        /**
         * moves all vertices in the line down half the height of a line
         */
        void moveDown() {
            for(int i : indices) {
                posArr[i * VERTICES_PER_QUAD * 3 + 1] += getLineHeight()/2;
                posArr[i * VERTICES_PER_QUAD * 3 + 4] += getLineHeight()/2;
                posArr[i * VERTICES_PER_QUAD * 3 + 7] += getLineHeight()/2;
                posArr[i * VERTICES_PER_QUAD * 3 + 10] += getLineHeight()/2;
            }

            this.yPos += getLineHeight()/2;

            Mesh mesh = new Mesh(posArr,texArr,normals,indicesArr);
            mesh.setMaterial(new Material(fontTexture.getTexture()));
            setMesh(mesh);
        }

        /**
         * removes all letters form the line
         */
        void removeAll() {
            int size = line.length();
            for(int i = 0;i < size;i++) {
                removeLetter(0);
            }
        }

        /**
         * removes one letter at the passed position
         *
         * @param position where the letter should be removed
         * @return the removed letter
         */
        char removeLetter(int position) {

            // line indices
            int[] indicesTemp = new int[indices.length -1];

            int index = indices[position];


            char toRemove = line.toCharArray()[position];

            FontTexture.CharData charData = fontTexture.getCharData(toRemove);

            for(int i = 0; i < indicesTemp.length;i++) {

                if(i < position) {
                    indicesTemp[i] = indices[i];
                } else {
                    indicesTemp[i] = indices[i+1];
                }
            }

            indices = indicesTemp;

            int oldWidth = getLineWidth(line);

            line = new StringBuilder(line).deleteCharAt(position).toString();


            switch (alignment) {
                case LEFT:
                    maxLineWidth = calculateMaxWidth();

                    for(int i = position;i<indices.length;i++) {
                        moveInX(indices[i],-charData.getxAdvance());
                    }

                    break;
                case CENTER:
                    int width = getLineWidth(line);
                    int diff = width - oldWidth;
                    maxLineWidth = calculateMaxWidth();
                    start += -diff/2;

                    for(int i = 0;i<indices.length;i++) {
                        if(i < position ) {
                            moveInX(indices[i], -diff/2);
                        } else {
                            moveInX(indices[i],-diff/2 - charData.getxAdvance());
                        }
                    }

                    break;
                case RIGHT:

                    maxLineWidth = calculateMaxWidth();

                    start += charData.getxAdvance();

                    for(int i = 0;i<position;i++) {
                        moveInX(indices[i],charData.getxAdvance());
                    }

                    break;
            }




            // indices
            int[] indicesArrTemp = new int[indicesArr.length - 3];



            int count = 0;

            for(int i = 0;i < indicesArr.length;i++) {

                if(indicesArr[i] >= index * VERTICES_PER_QUAD && indicesArr[i] <= index * VERTICES_PER_QUAD + 3) {
                    count=6;
                }
                if(i + count < indicesArr.length) {
                    indicesArrTemp[i] = indicesArr[i + count];
                }

            }



            indicesArr = indicesArrTemp;

            Mesh mesh = new Mesh(posArr,texArr,normals,indicesArr);
            mesh.setMaterial(new Material(fontTexture.getTexture()));
            setMesh(mesh);

            return toRemove;
        }


        /**
         * adds the passed letter to the line at the passed position
         *
         * @param letter to add
         * @param position where to add the letter
         */
        void addLetter(char letter,int position) {

            FontTexture.CharData charData = fontTexture.getCharData(letter);



            // line indices
            int[] indicesTemp = new int[indices.length + 1];

            for(int i = 0; i < indicesTemp.length;i++) {
                if(i < position) {
                    indicesTemp[i] = indices[i];
                } else if(i == position) {
                    indicesTemp[i] = nextIndex;
                } else {
                    indicesTemp[i] = indices[i-1];
                }
            }

            indices = indicesTemp;

            int oldWidth = getLineWidth(line);

            line = new StringBuilder(line).insert(position,letter).toString();

            int startX = 0;

            switch (alignment) {
                case LEFT:
                    maxLineWidth = calculateMaxWidth();
                    startX = getPosition(position);

                    for(int i = position + 1;i<indices.length;i++) {
                        moveInX(indices[i],charData.getxAdvance());
                    }

                    break;
                case CENTER:
                    int width = getLineWidth(line);
                    int diff = width - oldWidth;
                    maxLineWidth = calculateMaxWidth();
                    startX = -width/2 + getPosition(position);
                    start = -width/2;
                    for(int i = 0;i<indices.length;i++) {
                        if(i < position ) {
                            moveInX(indices[i], -diff/2);
                        } else if(i > position){
                            moveInX(indices[i],-diff/2 + charData.getxAdvance());
                        }
                    }

                    break;
                case RIGHT:
                    width = getLineWidth(line);
                    maxLineWidth = calculateMaxWidth();
                    startX = -width + getPosition(position);
                    start  -= charData.getxAdvance();
                    for(int i = position + 1;i<indices.length;i++) {
                        moveInX(indices[i],charData.getxAdvance());
                    }

                    break;
            }

            // positions
            float[] posArrTemp = posArr;
            posArr = new float[posArrTemp.length+VERTICES_PER_QUAD * 3];
            System.arraycopy(posArrTemp,0,posArr,0,posArrTemp.length);

            int yOffset = charData.getyOffset()- (VERTICAL_PADDING_ZERO - verticalPadding) /2;

            float zPosition = 0;

            posArr[nextIndex * VERTICES_PER_QUAD * 3] = charData.getxOffset() + startX;
            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 1] = this.yPos + yOffset;
            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 2] = zPosition;

            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 3] = charData.getxOffset() + startX;
            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 4] = this.yPos + yOffset + charData.getHeight();
            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 5] = zPosition;

            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 6] = charData.getxOffset() + startX + charData.getWidth();
            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 7] = this.yPos + yOffset;
            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 8] = zPosition;

            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 9] = charData.getxOffset() + startX + charData.getWidth();
            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 10] = this.yPos + yOffset + charData.getHeight();
            posArr[nextIndex * VERTICES_PER_QUAD * 3 + 11] = zPosition;

            // texture coordinates
            float[] texArrTemp = texArr;
            texArr = new float[texArrTemp.length + VERTICES_PER_QUAD * 2];
            System.arraycopy(texArrTemp,0,texArr,0,texArrTemp.length);

            texArr[nextIndex * VERTICES_PER_QUAD * 2] = charData.getX()/512.0f;
            texArr[nextIndex * VERTICES_PER_QUAD * 2 + 1] = charData.getY()/512.0f;

            texArr[nextIndex * VERTICES_PER_QUAD * 2 + 2] = charData.getX()/512.0f;
            texArr[nextIndex * VERTICES_PER_QUAD * 2 + 3] = (charData.getY() + charData.getHeight())/512.0f;

            texArr[nextIndex * VERTICES_PER_QUAD * 2 + 4] = (charData.getX() + charData.getWidth())/512.0f;
            texArr[nextIndex * VERTICES_PER_QUAD * 2 + 5] = (charData.getY())/512.0f;

            texArr[nextIndex * VERTICES_PER_QUAD * 2 + 6] = (charData.getX() + charData.getWidth())/512.0f;
            texArr[nextIndex * VERTICES_PER_QUAD * 2 + 7] = (charData.getY() + charData.getHeight())/512.0f;

            // indices
            int[] indicesArrTemp = indicesArr;
            indicesArr = new int[indicesArrTemp.length + 6];
            System.arraycopy(indicesArrTemp,0,indicesArr,0,indicesArrTemp.length);

            indicesArr[indicesArr.length - 6] = nextIndex * VERTICES_PER_QUAD;
            indicesArr[indicesArr.length - 5] = nextIndex * VERTICES_PER_QUAD + 1;
            indicesArr[indicesArr.length - 4] = nextIndex * VERTICES_PER_QUAD + 2;
            indicesArr[indicesArr.length - 3] = nextIndex * VERTICES_PER_QUAD + 3;
            indicesArr[indicesArr.length - 2] = nextIndex * VERTICES_PER_QUAD + 2;
            indicesArr[indicesArr.length - 1] = nextIndex * VERTICES_PER_QUAD + 1;

            nextIndex++;

            Mesh mesh = new Mesh(posArr,texArr,normals,indicesArr);
            mesh.setMaterial(new Material(fontTexture.getTexture()));
            setMesh(mesh);
        }

        /**
         * moves the letter at the index to the right by the given amount
         *
         * @param index of the letter
         * @param amount to move the letter
         */
        private void moveInX(int index,int amount) {
            posArr[index * VERTICES_PER_QUAD * 3] += amount;
            posArr[index * VERTICES_PER_QUAD * 3 + 3] += amount;
            posArr[index * VERTICES_PER_QUAD * 3 + 6] += amount;
            posArr[index * VERTICES_PER_QUAD * 3 + 9] += amount;
        }

        /**
         * returns the position of a letter at the passed index
         *
         * @param letter index of the letter
         * @return position of the letter (x)
         */
        int getPosition(int letter) {
            int result = START_PADDING;
            for(int i  = 0; i < letter;i++) {
                result+=fontTexture.getCharData(line.toCharArray()[i]).getxAdvance();
            }
            return result;
        }

        int getCursorPosition(float x) {
            int length = getLineWidth(line);

            if(length <= START_PADDING) {
                return 0;
            } else {
                int temp = (int) (START_PADDING + fontTexture.getCharData(line.toCharArray()[0]).getxAdvance() * 0.5);
                if(temp/(float)length > x) {
                    return 0;
                }
                for(int i = 1;i < line.length();i++) {


                    temp += fontTexture.getCharData(line.toCharArray()[i-1]).getxAdvance() * 0.5 + fontTexture.getCharData(line.toCharArray()[i]).getxAdvance() * 0.5;

                    if(temp/(float)length > x) {
                        return i;
                    }

                }
                return line.length();

            }
        }

        /**
         * returns the content of the line as String
         *
         * @return line content
         */
        @SuppressWarnings("unused")
        public String getLine() {
            return line;
        }
    }
}
