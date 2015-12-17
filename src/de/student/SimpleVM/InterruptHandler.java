package de.student.SimpleVM;

import java.io.*;

/**
 * Handles system functions.
 */
public class InterruptHandler {

    // System functions
    final static int SYS_READ_LINE = 0;
    final static int SYS_WRITE_LINE = 1;
    final static int SYS_READ_FILE = 2;
    final static int SYS_WRITE_FILE = 3;
    final static int SYS_FILE_SIZE = 4;
    final static int SYS_MEM_SIZE = 5;

    public InterruptHandler() {
        // nothing
    }

    /**
     * Reads line from cmd with prompt.
     */
    public static String sysReadLine(String prompt) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print(prompt);

        String input = br.readLine();

        return input;
    }

    /**
     * Writes line to terminal.
     */
    public static void sysWriteLine(String text) {
        System.out.println(text);
    }

    /**
     * Reads file at given position.
     *
     * @param filePath
     * @param position
     * @return
     * @throws IOException
     */
    public static byte sysReadFile(String filePath, int position) throws IOException {
        byte content;

        RandomAccessFile file = new RandomAccessFile(filePath, "r");

        file.seek(position);

        content = (byte) file.read();

        file.close();

        return content;
    }

    /**
     * Writes byte at offset.
     *
     * @param filePath
     * @param position
     * @param value
     * @throws IOException
     */
    public static void sysWriteFile(String filePath, int position, byte value) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");

        file.seek(position);

        file.writeByte(value);

        file.close();
    }

    /**
     * Returns size of file
     *
     * @param filePath
     * @return
     */
    public static int sysFileSize(String filePath) {
        File file = new File(filePath);
        return (int) file.length();
    }
}
