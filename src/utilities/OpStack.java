package utilities;

import java.util.LinkedList;
import java.util.List;

/**
 * Stack implementation that keeps track of all kinds of operations that we want to push onto stack
 * The generic type will be action in our case.
 * @param <T>
 * Referenced from Prof. Charles Theva's (RMIT University) Lecture Notes
 */
public class OpStack<T> {

    // List of all operations that has been performed so far during current session
    private List<T> operations;

    // Creates a new stack
    OpStack() {
        operations = new LinkedList<>();
    }


    /**
     * Pushes new opeation on to the stack.
     * The top element, will always represent the latest operations
     * @param item
     */
    void push(T item) {
        operations.add(operations.size(), item);
    }



    /**
     * Removes and returns the last performed operation, represented by last element of the list
     * @return
     */
    T pop() {
        if(operations.size() > 0)
            return operations.remove(operations.size()-1);
        else
            return null;
    }

    /**
     * Returns the current size of the stack
     * @return
     */
    public int size()
    {
        return operations.size();
    }

    // Clears all the operations
    void clear() {
        operations.clear();
    }
}