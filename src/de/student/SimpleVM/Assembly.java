package de.student.SimpleVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static de.student.SimpleVM.Instruction.*;

// CRC32

/**
 * Helps with assembling.
 */
public class Assembly {
    private List<Integer> assembly;

    public Assembly() {
        assembly = new ArrayList<>();
    }

    /**
     * Used for returning the preprocessed output.
     *
     * @return List of commands.
     */
    public List<Integer> getAssembly() {
        this.preprocessAssembly();
        return this.assembly;
    }

    /**
     * Add assembler command.
     *
     * @param instruction
     */
    public void addAssembledInstruction(Integer... instruction) {
        for (int i = 0; i < instruction.length; i++) {
            assembly.add(instruction[i]);
        }
    }

    /**
     * Converts string label to CRC32 Integer.
     *
     * @param label
     * @return
     */
    public int labelToInt(String label) {
        int i = 0;
        int convertedInteger = 0;
        Checksum checksum = new CRC32();

        for (i = 0; i < label.length(); i += Integer.BYTES) {
            int len = (i + Integer.BYTES > label.length()) ? label.length() - i : Integer.BYTES;

            byte bytes[] = label.substring(i, i + len).getBytes();

            checksum.update(bytes, 0, bytes.length);
            convertedInteger ^= (int) checksum.getValue();
        }


        return convertedInteger;
    }

    /**
     * Overwrite assembly.
     *
     * @param asm
     */
    public void setAssembly(List<Integer> asm) {
        this.assembly = asm;
    }

    /**
     * Preprocesses code. JMP_LABELS are removed and jumps are replaced with offsets.
     */
    private void preprocessAssembly() {
        Map<Integer, Integer> labelOffsets = new HashMap<>();
        Instruction ins = new Instruction();

        // Search for LABELs
        for (int i = 0; i < assembly.size(); i++) {
            if (assembly.get(i) == LABEL) {
                if (labelOffsets.containsKey(assembly.get(i + 1))) {
                    throw new IllegalArgumentException("Labels were redefined.");
                } else {
                    labelOffsets.put(assembly.get(i + 1), i);
                }
                i += ins.getInstructionOffset(assembly.get(i));
            }
        }

        // Correct jmp offsets
        for (int i = 0; i < assembly.size(); i++) {
            int instruction = assembly.get(i);

            if (instruction == JMP || instruction == JE || instruction == JNE || instruction == JG || instruction == JB || instruction == CALL) {
                int label = assembly.get(i + 1);

                // Correct offset
                if (labelOffsets.get(label) == null) {
                    throw new IllegalArgumentException("Used label is not defined.");
                } else {
                    if (instruction == JMP || instruction == JE || instruction == JNE || instruction == JG || instruction == JB) {
                        assembly.set(i + 1, labelOffsets.get(label) - i);
                    } else if (instruction == CALL) {
                        assembly.set(i + 1, labelOffsets.get(label) - 1); // prefetching
                    }
                }
            }
            i += ins.getInstructionOffset(instruction);
        }
    }
}
