package ru.pussia.endpoints.utils.structures;

import java.util.ArrayDeque;
import java.util.Deque;

public class HierarchyDeque<T> {

    private final Deque<Pair<Integer, T>> hierarchy; // spaces, last class

    public HierarchyDeque() {
        this.hierarchy = new ArrayDeque<>();
    }

    /**
     * Clears all the data in the deque
     */
    public void clear() {
        hierarchy.clear();
    }


    /**
     * Adds a pair to the end of the deque
     * @param spaces  spaces before the keyword declaration
     * @param pointer pointer to the start of the keyword
     */
    public void push(int spaces, T pointer) {
        hierarchy.addLast(new Pair<>(spaces, pointer));
    }

    /**
     * Removes the last pair from the queue while number of spaces bigger than current number of spaces
     * @param spaces number of spaces
     */
    public void popIfSpacesDecrease(int spaces) {
        while (!hierarchy.isEmpty() && hierarchy.getLast().getFirst() >= spaces) {
            hierarchy.removeLast();
        }
    }

    /**
     * Returns the last pair in the deque
     * @return last pair
     */
    public Pair<Integer, T> last() {
        return hierarchy.getLast();
    }

    /**
     * Returns true if deque is empty, otherwise false
     * @return deque fill state
     */
    public boolean isEmpty() {
        return hierarchy.isEmpty();
    }
}
