package engine.general;

import java.util.List;

public class Utils {


    /**
     * converts a list of floats int an array with equal size and content
     *
     * @param list the list of floats to convert into an array
     * @return the float array with the content of list
     */

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for(int i = 0; i < size;i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }
}
