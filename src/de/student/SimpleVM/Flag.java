package de.student.SimpleVM;

/**
 * Includes indices of flags.
 */
public abstract class Flag {
    /**
     * Count of available flags
     */
    final static int COUNT = 5;

    /**
     * Setted if overflow.
     */
    final static int OVERFLOW = 0;
    /**
     * Setted if underflow.
     */
    final static int UNDERFLOW = 1;
    /**
     * Setted if result is greater.
     */
    final static int GREATER = 2;
    /**
     * Setted if result is lower.
     */
    final static int LOWER = 3;
    /**
     * Setted if result is equal.
     */
    final static int EQUAL = 4;
}
