package de.student.SimpleVM;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for array index of defined registers.
 */
public class Register {
    // Count
    final static int COUNT = 9;
    // General purpose
    final static int R1 = 0;
    final static int R2 = 1;
    final static int R3 = 2;
    final static int R4 = 3;
    final static int R5 = 4;
    final static int R6 = 5;

    // Specialized registers
    final static int RBP = 6; // reserved
    final static int RSP = 7; // reserved

    /**
     * Instruction Pointer
     */
    final static int RIP = 8;

    // Class
    private Map<String, Integer> registerMnemonics; // 1 to 1 for Mnemonic -> Bytecode

    public Register() {
        registerMnemonics = new HashMap<String, Integer>();
        // General purpose
        registerMnemonics.put("r1", R1);
        registerMnemonics.put("r2", R2);
        registerMnemonics.put("r3", R3);
        registerMnemonics.put("r4", R4);
        registerMnemonics.put("r5", R5);
        registerMnemonics.put("r6", R6);

        // Specialized registers
        registerMnemonics.put("rbp", RBP);
        registerMnemonics.put("rsp", RSP);
        registerMnemonics.put("rip", RIP);
    }

    /**
     * Returns register index from mnemonic.
     *
     * @param mnemonic
     * @return
     */
    public int getRegisterFromMnemonic(String mnemonic) {
        int bytecode = (registerMnemonics.get(mnemonic) != null) ? registerMnemonics.get(mnemonic) : -1;

        return bytecode;
    }
}