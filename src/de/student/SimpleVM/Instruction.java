package de.student.SimpleVM;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported instructions.
 */
public class Instruction {
    // INTERN(OP-Codes)                         // Mnemonics

    // Parameter size
    final int NULL_PARAMETER = 0;
    final int ONE_PARAMETER = 1;
    final int TWO_PARAMETER = 2;

    // DWORD
    final static int MOV_DWORD = 1;             // MOVD REG, VAL
    final static int ADD_DWORD = 2;             // ADDD REG, VAL
    final static int SUB_DWORD = 3;             // SUBD REG, VAL
    final static int MUL_DWORD = 33;            // MULD REG, VAL
    final static int DIV_DWORD = 34;            // DIVD REG, VAL
    final static int XOR_DWORD = 4;             // XORD REG, VAL
    final static int AND_DWORD = 5;             // ANDD REG, VAL
    final static int OR_DWORD = 6;              // ORD REG, VAL
    final static int CMP_DWORD = 7;             // CMPD REG, VAL
    final static int SHL_DWORD = 51;            // SHLD REG, VAL
    final static int SHR_DWORD = 52;            // SHRD REG, VAL

    // REGISTER
    final static int MOV_REG = 8;               // MOVR REG, REG2
    final static int ADD_REG = 9;               // ADDR REG, REG2
    final static int SUB_REG = 10;              // SUBR REG, REG2
    final static int MUL_REG = 35;              // MULR REG, REG2
    final static int DIV_REG = 36;              // DIVR REG, REG2
    final static int XOR_REG = 11;              // XORR REG, REG2
    final static int AND_REG = 12;              // ANDR REG, REG2
    final static int NOT_REG = 13;              // NOTR REG
    final static int OR_REG = 14;               // ORR REG, REG2
    final static int CMP_REG = 15;              // CMPR REG, REG2
    final static int INC_REG = 16;              // INCR REG
    final static int DEC_REG = 17;              // DECR REG
    final static int SHL_REG = 53;              // SHLR REG, REG2
    final static int SHR_REG = 54;              // SHRR REG, REG2

    // JUMPS
    final static int LABEL = 18;                // LABEL:
    final static int JMP = 19;                  // JMP LABEL
    final static int JE = 20;                   // JE LABEL
    final static int JNE = 21;                  // JNE LABEL
    final static int JG = 22;                   // JG LABEL
    final static int JB = 23;                   // JB LABEL

    // MEMORY(DWORD)
    final static int WRITE_MEM_BYTE_DWORD = 24; // WMBD OFFSET, VAL
    final static int WRITE_MEM_INT_DWORD = 25;  // WMID OFFSET, VAL
    final static int READ_MEM_BYTE_DWORD = 26;  // RMBD REG(OUTPUT), OFFSET
    final static int READ_MEM_INT_DWORD = 27;   // RMID REG(OUTPUT), OFFSET

    // MEMORY(REGISTER)
    final static int WRITE_MEM_BYTE_REG = 60;   // WMBR REG(OFFSET), REG2(VAL)
    final static int WRITE_MEM_INT_REG = 61;    // WMIR REG(OFFSET), REG2(VAL)
    final static int READ_MEM_BYTE_REG = 62;    // RMBR REG(OUTPUT), REG2(OFFSET)
    final static int READ_MEM_INT_REG = 63;     // RMIR REG(OUTPUT), REG2(OFFSET)

    // STACK
    final static int PUSH_REG = 28;             // PUSH REG
    final static int POP_REG = 29;              // POP REG
    final static int CALL = 30;                 // CALL LABEL
    final static int RETN = 31;                 // RETN

    // CPU
    final static int HALT = 32;                 // HALT
    final static int INT = 80;                  // INT

    // CLASS
    private Map<Integer, Integer> instructionOffsets;
    private Map<String, Integer> instructionMnemonics; // 1 to 1 for Mnemonic -> Bytecode

    public Instruction() {
        instructionOffsets = new HashMap<>();
        instructionMnemonics = new HashMap<>();

        // Internal byte code
        // DWORD
        instructionOffsets.put(MOV_DWORD, TWO_PARAMETER);
        instructionOffsets.put(ADD_DWORD, TWO_PARAMETER);
        instructionOffsets.put(SUB_DWORD, TWO_PARAMETER);
        instructionOffsets.put(MUL_DWORD, TWO_PARAMETER);
        instructionOffsets.put(DIV_DWORD, TWO_PARAMETER);
        instructionOffsets.put(XOR_DWORD, TWO_PARAMETER);
        instructionOffsets.put(AND_DWORD, TWO_PARAMETER);
        instructionOffsets.put(OR_DWORD, TWO_PARAMETER);
        instructionOffsets.put(CMP_DWORD, TWO_PARAMETER);
        instructionOffsets.put(SHL_DWORD, TWO_PARAMETER);
        instructionOffsets.put(SHR_DWORD, TWO_PARAMETER);


        // REGISTER
        instructionOffsets.put(MOV_REG, TWO_PARAMETER);
        instructionOffsets.put(ADD_REG, TWO_PARAMETER);
        instructionOffsets.put(SUB_REG, TWO_PARAMETER);
        instructionOffsets.put(MUL_REG, TWO_PARAMETER);
        instructionOffsets.put(DIV_REG, TWO_PARAMETER);
        instructionOffsets.put(XOR_REG, TWO_PARAMETER);
        instructionOffsets.put(AND_REG, TWO_PARAMETER);
        instructionOffsets.put(SHL_REG, TWO_PARAMETER);
        instructionOffsets.put(SHR_REG, TWO_PARAMETER);
        instructionOffsets.put(CMP_REG, TWO_PARAMETER);
        instructionOffsets.put(NOT_REG, ONE_PARAMETER);
        instructionOffsets.put(INC_REG, ONE_PARAMETER);
        instructionOffsets.put(DEC_REG, ONE_PARAMETER);

        // JUMPS
        instructionOffsets.put(LABEL, ONE_PARAMETER);
        instructionOffsets.put(JMP, ONE_PARAMETER);
        instructionOffsets.put(JE, ONE_PARAMETER);
        instructionOffsets.put(JNE, ONE_PARAMETER);
        instructionOffsets.put(JG, ONE_PARAMETER);
        instructionOffsets.put(JB, ONE_PARAMETER);

        // MEMORY(DWORD)
        instructionOffsets.put(WRITE_MEM_BYTE_DWORD, TWO_PARAMETER);
        instructionOffsets.put(WRITE_MEM_INT_DWORD, TWO_PARAMETER);
        instructionOffsets.put(READ_MEM_BYTE_DWORD, TWO_PARAMETER);
        instructionOffsets.put(READ_MEM_INT_DWORD, TWO_PARAMETER);

        // MEMORY(REGISTER)
        instructionOffsets.put(WRITE_MEM_BYTE_REG, TWO_PARAMETER);
        instructionOffsets.put(WRITE_MEM_INT_REG, TWO_PARAMETER);
        instructionOffsets.put(READ_MEM_BYTE_REG, TWO_PARAMETER);
        instructionOffsets.put(READ_MEM_INT_REG, TWO_PARAMETER);

        // STACK
        instructionOffsets.put(PUSH_REG, ONE_PARAMETER);
        instructionOffsets.put(POP_REG, ONE_PARAMETER);
        instructionOffsets.put(CALL, ONE_PARAMETER);
        instructionOffsets.put(RETN, NULL_PARAMETER);

        // CPU
        instructionOffsets.put(HALT, NULL_PARAMETER);
        instructionOffsets.put(INT, ONE_PARAMETER);

        // Mnemonics
        // DWORD
        instructionMnemonics.put("movd", MOV_DWORD);
        instructionMnemonics.put("addd", ADD_DWORD);
        instructionMnemonics.put("subd", SUB_DWORD);
        instructionMnemonics.put("muld", MUL_DWORD);
        instructionMnemonics.put("divd", DIV_DWORD);
        instructionMnemonics.put("xord", XOR_DWORD);
        instructionMnemonics.put("andd", AND_DWORD);
        instructionMnemonics.put("ord", OR_DWORD);
        instructionMnemonics.put("cmpd", CMP_DWORD);
        instructionMnemonics.put("shld", SHL_DWORD);
        instructionMnemonics.put("shrd", SHR_DWORD);

        // REGISTER
        instructionMnemonics.put("movr", MOV_REG);
        instructionMnemonics.put("addr", ADD_REG);
        instructionMnemonics.put("subr", SUB_REG);
        instructionMnemonics.put("mulr", MUL_REG);
        instructionMnemonics.put("divr", DIV_REG);
        instructionMnemonics.put("xorr", XOR_REG);
        instructionMnemonics.put("andr", AND_REG);
        instructionMnemonics.put("cmpr", CMP_REG);
        instructionMnemonics.put("notr", NOT_REG);
        instructionMnemonics.put("incr", INC_REG);
        instructionMnemonics.put("decr", DEC_REG);
        instructionMnemonics.put("shlr", SHL_REG);
        instructionMnemonics.put("shrr", SHR_REG);

        // JUMPS
        // Label aliases
        instructionMnemonics.put("@", LABEL);
        instructionMnemonics.put("label", LABEL);
        instructionMnemonics.put("function", LABEL);
        instructionMnemonics.put("proc", LABEL);

        instructionMnemonics.put("jmp", JMP);
        instructionMnemonics.put("je", JE);
        instructionMnemonics.put("jne", JNE);
        instructionMnemonics.put("jg", JG);
        instructionMnemonics.put("jb", JB);

        // MEMORY(DWORD)
        instructionMnemonics.put("wmbd", WRITE_MEM_BYTE_DWORD);
        instructionMnemonics.put("wmid", WRITE_MEM_INT_DWORD);
        instructionMnemonics.put("rmbd", READ_MEM_BYTE_DWORD);
        instructionMnemonics.put("rmid", READ_MEM_INT_DWORD);

        // MEMORY(REGISTER)
        instructionMnemonics.put("wmbr", WRITE_MEM_BYTE_REG);
        instructionMnemonics.put("wmir", WRITE_MEM_INT_REG);
        instructionMnemonics.put("rmbr", READ_MEM_BYTE_REG);
        instructionMnemonics.put("rmir", READ_MEM_INT_REG);

        // STACK
        instructionMnemonics.put("push", PUSH_REG);
        instructionMnemonics.put("pop", POP_REG);
        instructionMnemonics.put("call", CALL);

        // Return aliases
        instructionMnemonics.put("retn", RETN);
        instructionMnemonics.put("end_function", RETN);
        instructionMnemonics.put("end_proc", RETN);

        // CPU
        instructionMnemonics.put("hlt", HALT);
        instructionMnemonics.put("int", INT);

    }

    /**
     * Returns the offset for CPU.
     *
     * @param cmd
     * @return
     */
    public int getInstructionOffset(int cmd) {
        return instructionOffsets.get(cmd);
    }

    /**
     * Returns byte code from mnemonic.
     *
     * @param mnemonic
     * @return
     */
    public int getInstructionFromMnemonic(String mnemonic) {
        int bytecode = (instructionMnemonics.get(mnemonic) != null) ? instructionMnemonics.get(mnemonic) : -1;

        return bytecode;
    }
}
