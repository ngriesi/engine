package engine.general;

import engine.graph.general.Camera;
import engine.graph.items.GameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * class containing the methods for the matrix calculations for the 2d and 3d rendering
 */
public class Transformation {

    /** determines the way the scene is visible depending of the field of view and the window size */
    private final Matrix4f projectionMatrix;

    /** the determines the way one single GameModel is viewed */
    private final Matrix4f modelViewMatrix;

    /** applies the values of the cameras in the 3d scene to the objects*/
    private final Matrix4f viewMatrix;

    /** Matrix for orthographic projection ( hud ) */
    private final Matrix4f orthoMatrix;

    /** field to temporarily store the position and rotation values of a gameItem */
    private final Matrix4f modelMatrix;

    /** Matrix for an orthographic model */
    private final Matrix4f orthoModelMatrix;

    /**
     * create a new object for every matrix
     */

    public Transformation(){
        projectionMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        orthoMatrix = new Matrix4f();
        orthoModelMatrix = new Matrix4f();
    }

    /**
     * @return the projection Matrix of the Scene
     */

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * @return the view Matrix (camera matrix)
     */

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    /**
     * Sets values of the ProjectionMatrix depending on the Parameters:
     *
     * @param fov field of View
     * @param width Window width
     * @param height Window height
     * @param zNear distance to near plane
     * @param zFar distance to far plane
     * @return ProjectionMatrix
     */

    @SuppressWarnings("UnusedReturnValue")
    public Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
        return projectionMatrix.setPerspective(fov,width/height,zNear,zFar);
    }

    /**
     * Creates a view Matrix for the World by moving the wold the opposite way of the camera
     * the View Matrix contains the opposite of the camera movement, multiplying it with the position of the objects in the scene gives the
     * impression of the camera moving
     *
     * @param camera the camera object used in the scene
     * @return the view Matrix of the scene
     */

    @SuppressWarnings("UnusedReturnValue")
    public Matrix4f updateViewMatrix(Camera camera){
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();

        // First do the Rotation so the Camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(rotation.x),new Vector3f(1,0,0)).rotate((float)Math.toRadians(rotation.y),new Vector3f(0,1,0));
        //Then do the translation
        viewMatrix.translate(-cameraPos.x,-cameraPos.y,-cameraPos.z);

        return viewMatrix;
    }

    /**
     * creates a matrix for the orthographic projection of hud elements by using the windows bounds
     * making the left top the origin by setting left and top to 0 and the right and bottom can be applied to any number
     * setting right to one means an object with the x coordinate 1 has its origin at the right edge of the window
     *
     * @param left 0
     * @param right width of the window
     * @param bottom height of the window
     * @param top 0
     * @return orthographic Matrix
     */

    public final Matrix4f getOrthoProjectionMatrix(float left, float right, float bottom, float top,float zNear,float zFar) {
        orthoMatrix.identity();
        orthoMatrix.setOrtho(left, right, bottom, top,zNear,zFar);
        return orthoMatrix;
    }

    /**
     * Creates the matrix that has to be used for the specific game item by using the transformation applied to this game item
     *
     * @param gameItem that gets rendered with this transformation
     * @param viewMatrix of the current scene/camera
     * @return model view Matrix
     */

    public Matrix4f buildModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix){
        Vector3f rotation = gameItem.getRotation();
        modelMatrix.identity().translate(gameItem.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale()).scale(gameItem.getScale3());
        modelViewMatrix.set(viewMatrix);
        return modelViewMatrix.mul(modelMatrix);
    }

    /**
     * Creates the matrix that has to be used for the specific game item by using the transformation applied to this game item
     * this Method is for the game items drawn orthographically
     *
     * @param gameItem game item that gets rendered with this transformation
     * @param orthoMatrix of the current window
     * @return orthoProjectionModelMatrix
     */

    public Matrix4f buildOrtoProjModelMatrix(GameItem gameItem, Matrix4f orthoMatrix) {
        Vector3f rotation = gameItem.getRotation();
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity().translate(gameItem.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale3().x,gameItem.getScale3().y,gameItem.getScale3().z);
        orthoModelMatrix.set(orthoMatrix);
        orthoModelMatrix.mul(modelMatrix);
        return orthoModelMatrix;
    }

}
