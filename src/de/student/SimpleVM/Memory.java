package de.student.SimpleVM;

/**
 * Memory class
 */
public class Memory {
    private byte[] space;

    public Memory(int availableSpace) {
        space = new byte[availableSpace];
    }

    // Helper

    /**
     * Sets all bytes in space to zero.
     */
    public void reset() {
        fill(0, space.length, (byte) 0);
    }

    /**
     * Writes n bytes at offset with value.
     *
     * @param offset
     * @param n
     * @param value
     */
    public void fill(int offset, int n, byte value) {
        for (int i = offset; i < offset + n; i++) {
            space[i] = value;
        }
    }

    // Main functions

    /**
     * Writes byte at offset.
     *
     * @param offset
     * @param value
     */
    public void writeByte(int offset, byte value) {
        space[offset] = value;
    }

    /**
     * Writes dword at offset.
     *
     * @param offset
     * @param value
     */
    public void writeDword(int offset, int value) {
        space[offset] = (byte) ((byte) (value >> 24) & 0xFF);
        space[offset + 1] = (byte) ((byte) (value >> 16) & 0xFF);
        space[offset + 2] = (byte) ((byte) (value >> 8) & 0xFF);
        space[offset + 3] = (byte) ((byte) (value) & 0xFF);
    }

    /**
     * Reads byte at offset.
     *
     * @param offset
     * @return
     */
    public char readByte(int offset) {
        return (char) (space[offset] & 0xFF);
    }

    /**
     * Reads dword at offset.
     *
     * @param offset
     * @return
     */
    public int readDword(int offset) {
        int result;

        result = readByte(offset) << 24;
        result += readByte(offset + 1) << 16;
        result += readByte(offset + 2) << 8;
        result += readByte(offset + 3);

        return result;
    }

    /**
     * Reads string till 0 at offset.
     *
     * @param offset
     * @return
     */
    public String readString(int offset) {
        StringBuilder result = new StringBuilder();
        int i = offset;

        while (i < space.length && this.space[i] != 0) {
            result.append((char) this.space[i]);
            i++;
        }
        return result.toString();
    }

    /**
     * Writes string at memory position.
     *
     * @param offset
     * @param string
     */
    public void writeString(int offset, String string) {
        byte[] bytes = string.getBytes();

        for (int i = 0; i < bytes.length; i++) {
            this.space[offset + i] = bytes[i];
        }
    }

    /**
     * Prints mmeory with given size.
     *
     * @param size
     */
    public void print(int size) {
        int split = 16;
        int i;
        System.out.print("      ");
        for (i = 0; i < split; i++) {
            System.out.printf("%02X ", i);
        }
        System.out.println();

        for (i = 0; i < size; i++) {
            if (i % split == 0) {
                System.out.println();
                System.out.printf("%04X: ", i);
            }
            System.out.format("%02X ", this.space[i]);
        }
        System.out.println();
    }
}
