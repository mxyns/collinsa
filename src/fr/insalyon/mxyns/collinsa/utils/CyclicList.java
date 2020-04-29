package fr.insalyon.mxyns.collinsa.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Fonctionne comme une liste normale, mais permet en plus de pouvoir cycler à l'infini sur les éléments, dans les deux sens et de choisir si on autorise l'ajout de doublons ou non.
 *
 *
 * @param <E>
 */
public class CyclicList<E> extends ArrayList<E> {

    /**
     * Can have duplicates ?
     */
    public boolean duplicates = true;

    /**
     * Current position of 'iterator' in list
     */
    private int index = -1;

    /**
     * Constructor used to make an empty CyclicList
     */
    public CyclicList() {

        super();
    }

    /**
     * Constructor used to make CyclicList from a collection Default index = 0
     *
     * @param coll base collection
     * @param duplicates true if duplicates are allowed in this list
     */
    public CyclicList(Collection<E> coll, boolean duplicates) {

        super(coll);
        this.duplicates = duplicates;
    }

    /**
     * Constructor used to make CyclicList from an array Size isn't fixed. Default index = 0
     *
     * @param arr base array
     * @param duplicates true if duplicates are allowed in this list
     */
    public CyclicList(E[] arr, boolean duplicates) {

        this(Arrays.asList(arr), duplicates);
    }

    /**
     * Constructor used to make CyclicList from an array Size isn't fixed. Default index = 0
     *
     * @param duplicates true if duplicates are allowed in this list
     */
    public CyclicList(boolean duplicates) {

        super();
        this.duplicates = duplicates;
    }

    /**
     * Adds an element to the list. If duplicates aren't allowed, checks if needed adds it anyways
     *
     * @return true if element was added
     */
    @Override
    public boolean add(E toAdd) {

        if (duplicates || !contains(toAdd))
            return super.add(toAdd);

        return false;
    }

    /**
     * Get current item in list without changing index
     *
     * @return current item in list
     */
    public E current() {

        if (isEmpty())
            return null;

        return get(index > 0 ? index : 0);
    }


    /**
     * Get current index value
     *
     * @return current index value
     */
    public int getIndex() {

        return index;
    }

    /**
     * Set index to i
     *
     * @param i index
     */
    public void setIndex(int i) {

        if (i >= -1 && i < size())
            this.index = i;
    }


    /**
     * Reset index to 0
     */
    public void reset() {

        index = -1;
    }

    /**
     * Get previous item and decrement index
     */
    public E prev() {

        return get(index = ((index + size() - 1) % size()));
    }

    /**
     * Get next item and increment index
     */
    public E next() {

        return get(index = (index + 1) % size());
    }

    /**
     * Get next item without changing current index value
     *
     * @return next item in list
     */
    public E getNext() {

        return get((index + 1) % size());
    }

    /**
     * Get previous item without changing current index value
     *
     * @return previous item in list
     */
    public E getPrev() {

        return get((index + size() - 1) % size());
    }

    public boolean set(E item) {

        if (contains(item)) {
            setIndex(indexOf(item));
            return true;
        } else
            return false;
    }
}