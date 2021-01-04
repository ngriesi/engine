package engine.general.blender;

import org.blender.dna.ListBase;
import org.cakelab.blender.nio.CPointer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class BlenderListIterator<T> implements Iterator<T>{

    /**
     * current element the iterator is on, the element that gets returned the next time next() is called
     */
    private T current;

    /**
     * getNext method of the classes in the iterator
     */
    private Method getNext;

    /**
     * class of the objects in the iterator
     */
    private Class<?> clazz;

    /*

     CREATING A NEW ITERATOR

     */

    /**
     * constructor checks  if the passed list element has a method called get Next and if
     * it is accessible
     *
     * @param listElement first element of the list iterator
     */
    public BlenderListIterator(T listElement) {

        try {
            if (listElement != null) {
                // get the class of the list elements
                clazz = listElement.getClass();

                // get the "getNext" method of this class
                getNext = clazz.getMethod("getNext");

                // test if this method is accessible by invoking it
                _getNext(listElement);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
            if (e instanceof IllegalArgumentException) throw (IllegalArgumentException)e;
            else throw new IllegalArgumentException(e);
        }

        // setting the current element to the first
        current = listElement;
    }

    /**
     * creating the iterator by accessing the first element of it and passing it to the
     * constructor
     *
     * @param listElementPointer pointer to the first element of the list
     * @param <T> type of the list element
     * @return new iterator
     * @throws IOException if the object cant be accessed via the pointer
     */
    public static <T> Iterator<T> create(CPointer<T> listElementPointer) throws IOException {
        // creating the object of the list element class
        T list = null;

        // check if the pointer points to something
        if (!listElementPointer.isNull()) {

            // try to get the first list object from the address the pointer points to
            list = listElementPointer.get();
        }
        // creating a new iterator with the first object
        return new BlenderListIterator<>(list);
    }

    /**
     * starting the creation of a Iterator by getting the pointer to the first object of a base
     * list
     *
     * @param listBase the iterator is created for
     * @param elemType type of elements in the list
     * @param <T> Type of the elements in the list
     * @return new Iterator for the ListBase
     * @throws IOException if the iterator cant be created
     */
    public static <T> Iterator<T> create(ListBase listBase, Class<T> elemType) throws IOException {
        CPointer<T> first = listBase.getFirst().cast(elemType);
        return create(first);
    }


    /*
     *
     * ACCESSING THE NEXT ELEMENT
     *
     */

    /**
     * checks if the current element that would be the next to be returned
     * by next() exists
     *
     * @return true if there is still at least one more object to be iterated over
     */
    @Override
    public boolean hasNext() {
        return current != null;
    }

    /**
     * returns the next element of the list of the iterator if it exists otherwise
     * it returns null
     *
     * @return the next object or null
     */
    @Override
    public T next() {
        // stores the current element
        T prev = current;

        try {
            // trying to access the next element
            current = _getNext(current);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
            throw new RuntimeException(e);
        }

        // returning the current element
        return prev;
    }

    /**
     * Method to get the next element of the list by invoking the getNext method of hte current
     * element
     *
     * @param from current element
     * @return the next element or null if it does not exist
     * @throws IllegalAccessException if the getNext method cant be accessed
     * @throws InvocationTargetException if the getNext method cant be accessed
     * @throws IOException if the object cant be created from the pointer
     */
    @SuppressWarnings("unchecked")
    private T _getNext(T from) throws IllegalAccessException, InvocationTargetException, IOException {

        // getting the pointer to an unknown class by invoking the getNext method of the current element
        CPointer<?> pointer = ((CPointer<?>) getNext.invoke(from));

        // checking if the pointer points to something
        if (!pointer.isNull()) {

            // getting the next list element object form the pointer
            return (T) pointer.cast(clazz).get();
        } else {
            return null;
        }

    }


    /**
     * unused
     *
     * @param listElement first element of the list the iterator gets created for
     * @param <T> Type of the list elements
     * @return new list iterator
     */
    public static <T> Iterator<T> create(T listElement) {
        return new BlenderListIterator<>(listElement);
    }

}
