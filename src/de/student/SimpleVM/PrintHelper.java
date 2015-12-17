package de.student.SimpleVM;

/**
 * Helps with outputting numbers.
 */
public class PrintHelper {
    /**
     * Returns number in 0xABCDEF-Format
     *
     * @param n
     * @return
     */
    public static String printHexNumber(int n) {
        return "0x" + Integer.toHexString(n).toUpperCase();
    }
}
