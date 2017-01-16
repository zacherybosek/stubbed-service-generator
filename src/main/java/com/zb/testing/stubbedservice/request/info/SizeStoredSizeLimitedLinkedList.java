package com.zb.testing.stubbedservice.request.info;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * The implementation of this class is a rolling, limited length linked list. Meaning, every time a new item is added
 * the last item in the list is removed. This class will store the total number of times it gets called,
 * but will only contain a maximum number of items.
 *
 * @param <T> The generic type this list will support
 */
public class SizeStoredSizeLimitedLinkedList<T>  implements Iterable<T> {

    private LinkedList<T> backingList = new LinkedList<>();
    private AtomicLong totalLength;

    private int itemCountLimit;

    /**
     * The constructor only supports an upper bound argument.
     *
     * @param itemCountLimit the upper bound to witch this list will be limited.
     */
    public SizeStoredSizeLimitedLinkedList(int itemCountLimit) {

        if(itemCountLimit <= 0) {
            throw new IllegalArgumentException("limit must be an integer greater than 0");
        }
        totalLength = new AtomicLong(0);
        this.itemCountLimit = itemCountLimit;
    }

    public void setListSizeLimit(int itemCountLimit) {
        this.itemCountLimit = itemCountLimit;
        while(backingList.size() > itemCountLimit) {
            backingList.remove();
        }
    }

    public int getListSizeLimit() {
        return this.itemCountLimit;
    }

    /**
     * Adds object of tpe T to the collection. If the addition of this object
     * causes the collection's size to grow past its specified limit then older
     * objects are dropped until the required size is reached.
     *
     * @param obj the object to add to the collection
     * @return the boolean result of whether teh element was successfully added.
     */
    public boolean add(T obj) {
        boolean addResult = backingList.add(obj);
        if(addResult) {
            totalLength.addAndGet(1);
        }
        if(backingList.size() > itemCountLimit) {
            backingList.remove();
        }

        return addResult;
    }

    /**
     * Gets the number of items in the list
     * TODO I think andy may have a bug regarding how accurate totalLength is in tracking number of items in the list
     * @return the list iterator
     */
    public long size() {
        return totalLength.get();
    }

    /**
     * gets an iterator for the list
     * @return the list iterator
     */
    @SuppressWarnings("unchecked")
    @Override
    public Iterator<T> iterator() {
        return ((Iterable<T>)backingList.clone()).iterator();
    }

    /**
     * Clears the backing list. Does NOT reset the count. TODO perhaps this is a clue that the totalLength does not decrement if the list shrinks
     */
    public void clear() {
        backingList.clear();
    }

}
