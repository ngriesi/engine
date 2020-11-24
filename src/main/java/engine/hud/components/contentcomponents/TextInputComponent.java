package engine.hud.components.contentcomponents;

import engine.general.Transformation;
import engine.graph.items.Mesh;
import engine.hud.HudShaderManager;
import engine.hud.actions.Action;
import engine.hud.assets.Quad;
import engine.hud.color.Color;
import engine.hud.mouse.MouseEvent;
import engine.hud.text.FontTexture;
import engine.render.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glStencilMask;

/**
 * class used to create a component that can get text input
 */
@SuppressWarnings("unused")
public class TextInputComponent extends TextComponent {

    /** cursor position (letters) */
    private final Vector2i cursorPosition;

    /** the mesh of the cursor bar */
    private final Quad cursor;

    /** if true the text input components only accepts numbers as input */
    @SuppressWarnings("unused")
    private boolean onlyNumbers;

    /** if false it's not possible to create a new line in the component */
    @SuppressWarnings("FieldCanBeLocal")
    private boolean allowNewLine = true;

    /** action, executed when the cursor changes position */
    private Action onCursorChanged;

    /** action, executed when text changes */
    private Action onChangedAction;

    /**
     * creates a quad for the component
     * sets default values
     *
     * @param fontTexture texture of the text
     */
    public TextInputComponent(FontTexture fontTexture) {
        super(fontTexture);
        cursorPosition = new Vector2i(0,0);
        cursor = new Quad();
        setCursorPosition();
        setText("");

        getMouseListener().addLeftButtonAction(e -> {
            if(e.getEvent()== MouseEvent.Event.CLICK_RELEASED) {
                hud.setCurrentInputFocus(TextInputComponent.this);
                hud.needsNextRendering();
            }
            return false;
        });

        getKeyListener().setKeyAction(key -> {

                switch(key) {

                    case GLFW_KEY_SPACE:
                    {
                        if(!onlyNumbers) {
                            getTextItem().addLetter(cursorPosition.y,cursorPosition.x,' ');
                            cursorPosition.x ++;
                            changed();
                        }
                    }
                    break;

                    case GLFW_KEY_LEFT:
                    {
                        if(cursorPosition.x > 0) {
                            cursorPosition.x --;
                        } else {
                            if(cursorPosition.y > 0) {
                                cursorPosition.y --;
                                cursorPosition.x = getTextItem().getLineLength(cursorPosition.y);
                            }
                        }
                    }
                    break;

                    case GLFW_KEY_RIGHT:
                    {
                        if(cursorPosition.x < getTextItem().getLineLength(cursorPosition.y)) {
                            cursorPosition.x ++;
                        } else {
                            if(cursorPosition.y < getTextItem().getLines() - 1) {
                                cursorPosition.y ++;
                                cursorPosition.x = 0;
                            }
                        }
                    }
                    break;

                    case GLFW_KEY_DOWN:
                    {
                        if(cursorPosition.y < getTextItem().getLines() - 1) {
                            cursorPosition.y ++;
                            if(getTextItem().getLineLength(cursorPosition.y) < cursorPosition.x) {
                                cursorPosition.x = getTextItem().getLineLength(cursorPosition.y);
                            }
                        }
                    }
                    break;

                    case GLFW_KEY_UP:
                    {
                        if(cursorPosition.y > 0) {
                            cursorPosition.y --;
                            if(getTextItem().getLineLength(cursorPosition.y) < cursorPosition.x) {
                                cursorPosition.x = getTextItem().getLineLength(cursorPosition.y);
                            }
                        }
                    }
                    break;

                    case GLFW_KEY_BACKSPACE:
                    {
                        if(cursorPosition.x>0) {
                            getTextItem().removeLetter(cursorPosition.y,cursorPosition.x-1);
                            cursorPosition.x--;
                            changed();
                        } else {
                            if(cursorPosition.y > 0) {
                                cursorPosition.x = getTextItem().getLineLength(cursorPosition.y-1);
                                getTextItem().removeLine(cursorPosition.y);
                                cursorPosition.y --;
                                changed();
                            }
                        }
                    }
                    break;

                    case GLFW_KEY_ENTER:
                    {
                        if(allowNewLine) {
                            getTextItem().addLine(cursorPosition.y,cursorPosition.x);
                            cursorPosition.y ++;
                            cursorPosition.x = 0;
                            changed();
                        }
                    }
                    break;

                    default:
                    {
                        if (window.isKeyPressed(key)) {

                            String temp = glfwGetKeyName(key, glfwGetKeyScancode(key));
                            if (temp != null) {
                                if (!onlyNumbers || Character.isDigit(temp.charAt(0))) {
                                    if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) {
                                        getTextItem().addLetter(cursorPosition.y, cursorPosition.x, Character.toUpperCase(temp.charAt(0)));
                                    } else {
                                        getTextItem().addLetter(cursorPosition.y, cursorPosition.x, temp.charAt(0));
                                    }
                                    cursorPosition.x++;
                                    changed();
                                }
                            }
                        }
                    }
                }

                updateBounds();

            });

    }

    /**
     * calculates and sets the cursors screen position
     * from the position in the text
     */
    private void setCursorPosition() {
        Vector2f pos = getTextItem().getCursorPosition(cursorPosition.x,cursorPosition.y);

        cursor.setPosition(getOnScreenXPosition() - getOnScreenWidth()/2 + getOnScreenWidth()*(pos.x +0.5f + getxOffset()) + super.getAutoXOffset(),getOnScreenYPosition() - getOnScreenHeight()/2 + getOnScreenHeight() * (pos.y +0.5f + getyOffset()) + super.getAutoYOffset(),cursor.getPosition().z);

        if(onCursorChanged != null) {
            onCursorChanged.execute();
        }

        super.updatePosition();
        cursor.setPosition(getOnScreenXPosition() - getOnScreenWidth()/2 + getOnScreenWidth()*(pos.x +0.5f + getxOffset()) + super.getAutoXOffset(),getOnScreenYPosition() - getOnScreenHeight()/2 + getOnScreenHeight() * (pos.y +0.5f + getyOffset()) + super.getAutoYOffset(),cursor.getPosition().z);

    }

    /**
     * updates the size of the cursor, depending on the
     * size of the text in the component
     */
    private void updateCursorSize() {
        float baseValue = getOnScreenHeight()/super.getTextItem().getLines() * (super.getTextItem().getFontTexture().getHeight()/super.getTextItem().getLineHeight());
        cursor.setScale3(baseValue * 0.03f,baseValue * 0.7f,1);
    }

    /**
     * updates bounds of the component
     */
    @Override
    public void updateBounds() {
        super.updateBounds();

        updateCursorSize();
        setCursorPosition();


    }

    /**
     * renders the cursor mesh without stencil information and calls the renderMesh
     * Method  of the textComponent (super) to render the text itself
     *
     * @param orthographic orthographic transformation
     * @param transformation transformation object
     * @param hudShaderProgram shader
     */
    public void drawCursor(Matrix4f orthographic, Transformation transformation, ShaderProgram hudShaderProgram) {


        if(hud.getCurrentInputFocus() != null && hud.getCurrentInputFocus().equals(this)) {
            Mesh mesh = cursor.getMesh();
            Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(cursor, orthographic);
            hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
            hudShaderProgram.setUniform("useTexture", 2);
            hudShaderProgram.setUniform("useColorShade", 0);
            hudShaderProgram.setUniform("colors", new Vector4f[]{cursor.getMesh().getMaterial().getAmbientColor()});
            hudShaderProgram.setUniform("transparencyMode", super.getMaskMode().ordinal());


            glStencilMask(0x0);
            mesh.render();
            glStencilMask(0xFF);
        }
    }

    /**
     * inserts the drawing of the cursor into the parent class setup shader method
     *
     * @param orthographic projection matrix for the hud
     * @param transformation transformation class for matrix calculations
     * @param shaderManager shader manager of the hud provides the shader reference
     */
    @Override
    public void setupShader(Matrix4f orthographic, Transformation transformation, HudShaderManager shaderManager) {
        super.setupShader(orthographic, transformation, shaderManager);
        drawCursor(orthographic,transformation,shaderManager.getMaskShader());
    }

    /**
     * cleans up the mesh
     */
    @Override
    public void cleanup() {
        cursor.getMesh().cleanup();
        super.cleanup();
    }

    /**
     * sets the id and the z cords for the text and the cursor
     *
     * @param id new id and z coordinate of the component
     */
    @Override
    protected void setId(int id) {
        super.setId(id);
        cursor.setPosition(cursor.getPosition().x,cursor.getPosition().y,id);
    }

    /**
     * sets a new text to the text component
     *
     * @param text new Text
     */
    @Override
    public void setText(String text) {
        super.setText(text);
        cursorPosition.y = getTextItem().getLines() - 1;
        cursorPosition.x = getTextItem().getLineLength(cursorPosition.y);
        updateCursorSize();
        setCursorPosition();
    }

    /**
     * sets the color of the text and the cursor
     *
     * @param color new color
     */
    @Override
    public void setColors(Color color) {
        super.setColors(color);
        cursor.getMesh().getMaterial().setAmbientColor(color.getVector4f());
    }



    /**
     * executes changed Action
     */
    public void changed() {
        if(onChangedAction != null) {
            onChangedAction.execute();
        }
    }

    /**
     * sets the onCursorChanged action
     *
     * @param onCursorChanged new action
     */
    public void setOnCursorChanged(Action onCursorChanged) {
        this.onCursorChanged = onCursorChanged;
    }

    /**
     * returns the cursor position in screen cords (relative to window size)
     *
     * @return cursor screen cords
     */
    public Vector2f getOnScreenCursorPosition() {
        Vector2f pos = getTextItem().getCursorPosition(cursorPosition.x,cursorPosition.y);

        Vector2f result = new Vector2f();
        result.x = getOnScreenXPosition() - getOnScreenWidth()/2 + getOnScreenWidth()*(pos.x +0.5f + getxOffset());
        result.y = getOnScreenYPosition() - getOnScreenHeight()/2 + getOnScreenHeight() * (pos.y +0.5f + getyOffset());

        return result;
    }

    /**
     * sets a new cursor position index
     *
     * @param newValues new indices
     */
    public void setCursor( Vector2i newValues) {
        cursorPosition.x = newValues.x;
        cursorPosition.y = newValues.y;
    }

    /**
     * needs rendering to remove cursor
     */
    @Override
    public void lostInputFocus() {
        super.lostInputFocus();
        hud.needsNextRendering();
    }

    public void setOnChangedAction(Action onChangedAction) {
        this.onChangedAction = onChangedAction;
    }

    public void setOnlyNumbers(boolean onlyNumbers) {
        this.onlyNumbers = onlyNumbers;
    }

    public void setAllowNewLine(boolean allowNewLine) {
        this.allowNewLine = allowNewLine;
    }
}
