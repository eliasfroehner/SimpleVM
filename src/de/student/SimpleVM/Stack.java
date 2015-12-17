package de.student.SimpleVM;

/**
 * Implements a basic stack.
 */
public class Stack {
    private int[] stackArray;
    private int currentElements;
    private int maxSize;

    public Stack(int size) {
        maxSize = size;
        stackArray = new int[size];
        currentElements = 0;
    }

    /**
     * Puiśh value on stack.
     *
     * @param value
     */
    public void push(int value) {
        stackArray[++currentElements] = value;
    }

    /**
     * Pop value from stack.
     *
     * @return
     */
    public int pop() {
        return stackArray[currentElements--];
    }

    /**
     * Is stack empty?
     *
     * @return
     */
    public boolean isEmpty() {
        return (currentElements == 0);
    }

    /**
     * Is stack full?
     *
     * @return
     */
    public boolean isFull() {
        return (currentElements == maxSize);
    }

    /**
     * Prints n entries of stack.
     */
    public void print() {
        for (int i = 0; i < currentElements; i++) {
            System.out.println(PrintHelper.printHexNumber(i * 4) + ": " + PrintHelper.printHexNumber(stackArray[currentElements - i]));
        }
    }
}
