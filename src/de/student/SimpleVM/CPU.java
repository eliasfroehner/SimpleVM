package de.student.SimpleVM;

import java.io.IOException;
import java.util.List;

import static de.student.SimpleVM.Instruction.*;
import static de.student.SimpleVM.InterruptHandler.*;
import static de.student.SimpleVM.Register.*;

public class CPU {
    /**
     * 32 bit CPU
     */
    final short BITS = 32;

    /**
     * Maximum of memory space.
     */
    final int MEMORY = 65536;

    /**
     * Bytes to print from memory.
     */
    final int MEMORY_PRINT_SIZE = 256;

    /**
     * Maximum stack size.
     */
    final short STACK = 256;

    /**
     * Registers controlled as array.
     */
    private int registers[] = new int[COUNT];

    /**
     * Flags controlled as array
     */
    private boolean flags[] = new boolean[Flag.COUNT];

    /**
     * Memory for CPU
     */
    Memory memory = new Memory(MEMORY);

    /**
     * Stack for CPU.
     */
    Stack stack = new Stack(STACK);

    /**
     * Size of OP-Codes
     */
    private int assemblySize;

    /**
     * Virtual CPU, which processes OP-Codes.
     */
    public CPU() {
        // Initialize CPU
        this.resetCPU();
    }

    // Helper

    /**
     * Print CPU fault
     */
    private void cpuError() {
        System.out.println("** FAULT **");
        System.out.println("Coredump:");
        this.dumpRegisters();
        this.dumpStack();
        this.dumpMemory();

        // interrupt execution of assembly
        this.halt();
    }

    /**
     * Dump registers
     */
    public void dumpRegisters() {
        System.out.println("** Registers **");
        System.out.println("R1: " + PrintHelper.printHexNumber(this.getRegister(R1)));
        System.out.println("R2: " + PrintHelper.printHexNumber(this.getRegister(R2)));
        System.out.println("R3: " + PrintHelper.printHexNumber(this.getRegister(R3)));
        System.out.println("R4: " + PrintHelper.printHexNumber(this.getRegister(R4)));
        System.out.println("R5: " + PrintHelper.printHexNumber(this.getRegister(R5)));
        System.out.println("R6: " + PrintHelper.printHexNumber(this.getRegister(R6)));
        // System.out.println("RBP: " + PrintHelper.printHexNumber(this.getRegister(Register.RBP)));
        // System.out.println("RSP: " + PrintHelper.printHexNumber(this.getRegister(Register.RSP)));
        System.out.println("RIP: " + PrintHelper.printHexNumber(this.getRegister(RIP)));
        System.out.println();
        System.out.println("** Flags **");
        System.out.println("Overflow: " + Boolean.toString(flags[Flag.OVERFLOW]));
        System.out.println("Underflow: " + Boolean.toString(flags[Flag.UNDERFLOW]));
        System.out.println("Greater: " + Boolean.toString(flags[Flag.GREATER]));
        System.out.println("Lower: " + Boolean.toString(flags[Flag.LOWER]));
        System.out.println("Equal: " + Boolean.toString(flags[Flag.EQUAL]));
        System.out.println();
    }

    /**
     * Dump stack
     */
    public void dumpStack() {
        System.out.println("** Stack **");
        this.stack.print();
    }

    /**
     * Dump memory
     */
    public void dumpMemory() {
        System.out.println("** Memory **");
        this.memory.print(this.MEMORY_PRINT_SIZE);
    }

    /**
     * Reset cpu
     */
    private void resetCPU() {
        // clear memory
        this.memory.reset();
        // reset registers
        this.clearRegisters();
        // reset flags
        this.clearFlags();
        // reset assembly size
        this.assemblySize = 0;
    }

    /**
     * Checks for overflow, when adding a value, and sets flag.
     *
     * @param current
     * @param value
     */
    private void setOverflow(int current, int value) {
        this.flags[Flag.OVERFLOW] = (current + value > Integer.MAX_VALUE);
    }

    /**
     * Checks for overflow, when multiplying a value, and sets flag.
     *
     * @param current
     * @param value
     */
    private void setOverflowMultiply(int current, int value) {
        this.flags[Flag.OVERFLOW] = (current * value > Integer.MAX_VALUE);
    }


    /**
     * Checks for underflow, when subtracting a value, and sets flag.
     *
     * @param current
     * @param value
     */
    private void setUnderflow(int current, int value) {
        this.flags[Flag.UNDERFLOW] = (current - value < Integer.MIN_VALUE);
    }

    /**
     * Sets given register to value.
     */
    private void setRegister(int register, int value) {
        if (this.registerInBounds(register)) this.registers[register] = value;
    }

    /**
     * Sets given flag to state.
     */
    private void setFlag(int flag, boolean value) {
        if (this.flagsInBounds(flag)) this.flags[flag] = value;
    }

    /**
     * Sets given registers to value.
     */
    private void setRegisters(int value, int... registers) {
        for (int register : registers) {
            this.setRegister(register, value);
        }
    }

    /**
     * Sets given flags to state.
     */
    private void setFlags(boolean value, int... flags) {
        for (int flag : flags) {
            this.setFlag(flag, value);
        }
    }

    /**
     * Clears all registers and sets to default.
     */
    private void clearRegisters() {
        // reset flags
        for (int i = 0; i < COUNT; i++) {
            this.setRegister(i, 0);
        }
    }

    /**
     * Clears all flags and sets to default.
     */
    private void clearFlags() {
        // reset flags
        for (int i = 0; i < Flag.COUNT; i++) {
            this.setFlag(i, false);
        }
    }

    /**
     * Returns next instruction.
     *
     * @param assembly
     * @return
     */
    private int nextInstruction(List<Integer> assembly) {
        return assembly.get(++registers[RIP]);
    }

    /**
     * Check if register number is in bounds.
     *
     * @param registers
     * @return boolean
     */
    private boolean registerInBounds(Integer... registers) {
        for (int i = 0; i < registers.length; i++) {
            if (registers[i] > COUNT || registers[i] < R1) {
                this.cpuError();
                return false;
            }
        }
        return true;
    }

    /**
     * Check if flags number is in bounds.
     *
     * @param flags
     * @return boolean
     */
    private boolean flagsInBounds(Integer... flags) {
        for (int i = 0; i < flags.length; i++) {
            if (flags[i] > Flag.COUNT || flags[i] < Flag.OVERFLOW) {
                this.cpuError();
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a value from a register
     *
     * @param register
     */
    public int getRegister(int register) {
        int register_value = 0;
        if (this.registerInBounds(register)) {
            register_value = registers[register];
        }
        return register_value;
    }

    /**
     * Returns a value from a flag
     *
     * @param flag
     */
    private boolean getFlag(int flag) {
        boolean flag_value = false;
        if (this.flagsInBounds(flag)) {
            flag_value = flags[flag];
        }
        return flag_value;
    }

    // MAIN
    public void executeProgram(List<Integer> assembly) {
        // Update assembly size
        this.assemblySize = assembly.size();

        for (registers[RIP] = 0; registers[RIP] < assembly.size(); registers[RIP]++)
            this.fetchInstruction(assembly.get(registers[RIP]), assembly);
    }

    private void fetchInstruction(Integer instruction, List<Integer> assembly) {
        switch (instruction) {
            case MOV_DWORD:
                this.movDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case ADD_DWORD:
                this.addDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case SUB_DWORD:
                this.subDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case MUL_DWORD:
                this.mulDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case DIV_DWORD:
                this.divDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case XOR_DWORD:
                this.xorDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case OR_DWORD:
                this.orDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case AND_DWORD:
                this.andDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case CMP_DWORD:
                this.cmpDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case MOV_REG:
                this.movReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case ADD_REG:
                this.addReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case SUB_REG:
                this.subReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case MUL_REG:
                this.mulReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case DIV_REG:
                this.divReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case XOR_REG:
                this.xorReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case OR_REG:
                this.orReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case AND_REG:
                this.andReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case CMP_REG:
                this.cmpReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case NOT_REG:
                this.notReg(nextInstruction(assembly));
                break;
            case INC_REG:
                this.incReg(nextInstruction(assembly));
                break;
            case DEC_REG:
                this.decReg(nextInstruction(assembly));
                break;
            case LABEL:
                incReg(RIP);
                break;
            case JMP:
                this.jmp(nextInstruction(assembly));
                break;
            case JE:
                this.je(nextInstruction(assembly));
                break;
            case JNE:
                this.jne(nextInstruction(assembly));
                break;
            case JG:
                this.jg(nextInstruction(assembly));
                break;
            case JB:
                this.jb(nextInstruction(assembly));
                break;
            case WRITE_MEM_BYTE_DWORD:
                this.writeMemoryByteDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case WRITE_MEM_INT_DWORD:
                this.writeMemoryIntDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case READ_MEM_BYTE_DWORD:
                this.readMemoryByteDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case READ_MEM_INT_DWORD:
                this.readMemoryIntDword(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case WRITE_MEM_BYTE_REG:
                this.writeMemoryByteReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case WRITE_MEM_INT_REG:
                this.writeMemoryIntReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case READ_MEM_BYTE_REG:
                this.readMemoryByteReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case READ_MEM_INT_REG:
                this.readMemoryIntReg(nextInstruction(assembly), nextInstruction(assembly));
                break;
            case PUSH_REG:
                this.pushDword(nextInstruction(assembly));
                break;
            case POP_REG:
                this.popDword(nextInstruction(assembly));
                break;
            case CALL:
                this.call(nextInstruction(assembly));
                break;
            case RETN:
                this.retn();
                break;
            case HALT:
                this.halt();
                break;
            case INT:
                this.interrupt(nextInstruction(assembly));
                break;
            default:
                this.cpuError();
        }
    }

    // Implementations
    // DWORD

    /**
     * Copies value to a register.
     * MOVD REGISTER, VALUE
     *
     * @param register
     * @param value
     */
    private void movDword(int register, int value) {
        this.setRegister(register, value);
    }

    /**
     * Adds a value to the register.
     * ADDD REGISTER, VALUE
     *
     * @param register
     * @param value
     */
    private void addDword(int register, int value) {
        this.clearFlags();
        if (this.registerInBounds(register)) {
            this.setOverflow(this.registers[register], value);
            this.registers[register] += value;
        }
    }

    /**
     * Multiply a value to the register.
     * MULD REGISTER, VALUE
     *
     * @param register
     * @param value
     */
    private void mulDword(int register, int value) {
        this.clearFlags();
        if (this.registerInBounds(register)) {
            this.setOverflowMultiply(this.registers[register], value);
            this.registers[register] *= value;
        }
    }

    /**
     * Divides a register with given value. R6 is set to modulo.
     * DIVD REGISTER, VALUE
     *
     * @param register
     * @param value
     */
    private void divDword(int register, int value) {
        this.clearFlags();
        if (this.registerInBounds(register)) {
            int modulo = this.registers[register] % value;

            this.registers[register] /= value;
            this.registers[R6] = modulo;
        }
    }

    /**
     * Subtracts a value to the register.
     * SUBD REGISTER, VALUE
     *
     * @param register
     * @param value
     */
    private void subDword(int register, int value) {
        this.clearFlags();

        if (this.registerInBounds(register)) {
            this.setUnderflow(this.registers[register], value);
            this.registers[register] -= value;
        }
    }

    /**
     * XORs a value to the register.
     * XORD REGISTER, VALUE
     *
     * @param register
     * @param value
     */
    private void xorDword(int register, int value) {
        if (this.registerInBounds(register)) this.registers[register] ^= value;
    }

    /**
     * ORs a value to the register.
     * ORD REGISTER, VALUE
     *
     * @param register
     * @param value
     */
    private void orDword(int register, int value) {
        if (this.registerInBounds(register)) this.registers[register] |= value;
    }

    /**
     * ANDs a value to the register.
     * ANDD REGISTER, VALUE
     *
     * @param register
     * @param value
     */
    private void andDword(int register, int value) {
        if (this.registerInBounds(register)) this.registers[register] &= value;
    }

    /**
     * Compares a value against a register.
     * CMPD REGISTER, VALUE
     *
     * @param register
     * @param value
     */
    private void cmpDword(int register, int value) {
        this.setFlags(false, Flag.EQUAL, Flag.GREATER, Flag.LOWER);

        if (this.registerInBounds(register)) {
            int registerValue = this.getRegister(register);

            this.generalCompareHandler(value, registerValue);
        }
    }

    // Register

    /**
     * Copies value from a register to a register.
     * MOVR REGISTER, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void movReg(int register, int register2) {
        if (this.registerInBounds(register, register2)) {
            int value = this.getRegister(register2);
            this.registers[register] = value;
        }
    }

    /**
     * Adds a value to the register.
     * ADDR REGISTER, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void addReg(int register, int register2) {
        if (this.registerInBounds(register, register2)) {
            int value = this.getRegister(register2);

            this.clearFlags();
            this.setOverflow(this.registers[register], value);
            this.registers[register] += value;
        }
    }

    /**
     * Subtracts a value to the register.
     * SUBR REGISTER, VALUE
     *
     * @param register
     * @param register2
     */
    private void subReg(int register, int register2) {
        if (this.registerInBounds(register, register2)) {
            int value = this.getRegister(register2);

            this.clearFlags();
            this.setUnderflow(this.registers[register], value);
            this.registers[register] -= value;
        }
    }

    /**
     * Multiply a register2 to the register.
     * MULR REGISTER, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void mulReg(int register, int register2) {
        this.clearFlags();
        if (this.registerInBounds(register)) {
            this.setOverflowMultiply(this.registers[register], this.getRegister(register2));
            this.registers[register] *= this.getRegister(register2);
        }
    }

    /**
     * Divides a register with given second register. R6 is set to modulo.
     * DIVR REGISTER, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void divReg(int register, int register2) {
        this.clearFlags();
        if (this.registerInBounds(register)) {
            int modulo = this.registers[register] % this.getRegister(register2);

            this.registers[register] /= this.getRegister(register2);
            this.registers[R6] = modulo;
        }
    }

    /**
     * XORs a value to the register.
     * XORR REGISTER, VALUE
     *
     * @param register
     * @param register2
     */
    private void xorReg(int register, int register2) {
        if (this.registerInBounds(register, register2)) {
            int value = this.getRegister(register2);

            this.registers[register] ^= value;
        }

    }

    /**
     * ORs a value to the register.
     * ORR REGISTER, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void orReg(int register, int register2) {
        if (this.registerInBounds(register, register2)) {
            int value = this.getRegister(register2);

            this.registers[register] |= value;
        }
    }

    /**
     * ANDs a value to the register.
     * ANDR REGISTER, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void andReg(int register, int register2) {
        if (this.registerInBounds(register, register2)) {
            int value = this.getRegister(register2);

            this.registers[register] &= value;
        }
    }

    /**
     * NOTs a value to the register.
     * NOTR REGISTER
     *
     * @param register
     */
    private void notReg(int register) {
        if (this.registerInBounds(register)) this.registers[register] = ~this.registers[register];
    }

    /**
     * Increases a register by 1.
     * INCR REGISTER
     *
     * @param register
     */
    private void incReg(int register) {
        this.setFlags(false, Flag.OVERFLOW);

        if (this.registerInBounds(register)) {
            this.setOverflow(this.registers[register], 1);
            this.registers[register]++;
        }
    }

    /**
     * Decreases a register by 1.
     * DECR REGISTER
     *
     * @param register
     */
    private void decReg(int register) {
        this.setFlags(false, Flag.UNDERFLOW);

        if (this.registerInBounds(register)) {
            this.setUnderflow(this.registers[register], 1);
            this.registers[register]--;
        }
    }

    /**
     * Compares a register against another register.
     * CMPR REGISTER, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void cmpReg(int register, int register2) {
        this.setFlags(false, Flag.EQUAL, Flag.GREATER, Flag.LOWER);

        if (this.registerInBounds(register, register2)) {
            int registerValue = this.getRegister(register);
            int register2Value = this.getRegister(register2);

            this.generalCompareHandler(registerValue, register2Value);
        }
    }

    /**
     * Checks for GREATER, EQUAL, LOWER.
     *
     * @param value1
     * @param value2
     */
    private void generalCompareHandler(int value1, int value2) {
        if (value2 == value1) {
            this.setFlag(Flag.EQUAL, true);
        } else if (value2 >= value1) {
            this.setFlag(Flag.GREATER, true);
        } else if (value2 <= value1) {
            this.setFlag(Flag.LOWER, true);
        }
    }

    /**
     * Jumps unconditionally to location.
     * JMP OFFSET
     */
    private void jmp(int offset) {
        this.generalJumpHandler(offset);
    }

    /**
     * Jumps if EQUAL Flag is true.
     * JE OFFSET
     */
    private void je(int offset) {
        if (this.getFlag(Flag.EQUAL)) this.generalJumpHandler(offset);
    }

    /**
     * Jumps if EQUAL Flag is false.
     * JNE OFFSET
     */
    private void jne(int offset) {
        if (!this.getFlag(Flag.EQUAL)) this.generalJumpHandler(offset);
    }

    /**
     * Jumps if GREATER Flag is true.
     * JG OFFSET
     */
    private void jg(int offset) {
        if (this.getFlag(Flag.GREATER)) this.generalJumpHandler(offset);
    }

    /**
     * Jumps if LOWER Flag is true.
     * JB OFFSET
     */
    private void jb(int offset) {
        if (this.getFlag(Flag.LOWER)) this.generalJumpHandler(offset);
    }

    /**
     * Corrects the Instruction Pointer
     *
     * @param jumpOffset
     */
    private void generalJumpHandler(int jumpOffset) {
        this.setRegister(RIP, this.getRegister(RIP) + jumpOffset);
    }

    // MEMORY(DWORD)

    /**
     * Writes byte at memory.
     * WMBD OFFSET, VALUE
     *
     * @param offset
     * @param value
     */
    private void writeMemoryByteDword(int offset, int value) {
        // Wrong command for dword
        if (value > Byte.MAX_VALUE) {
            this.cpuError();
        }

        this.memory.writeByte(offset, (byte) value);
    }

    /**
     * Writes dword at memory.
     * WMID OFFSET, VALUE
     *
     * @param offset
     * @param value
     */
    private void writeMemoryIntDword(int offset, int value) {
        // Wrong command
        if (value > Integer.MAX_VALUE) {
            this.cpuError();
        }

        this.memory.writeDword(offset, value);
    }

    /**
     * Reads byte from memory and stores at register.
     * RMBD REGISTER, OFFSET
     *
     * @param register
     * @param offset
     */
    private void readMemoryByteDword(int register, int offset) {
        this.setRegister(register, this.memory.readByte(offset));
    }

    /**
     * Reads dword from memory and stores at register.
     * RMID REGISTER, OFFSET
     *
     * @param register
     * @param offset
     */
    private void readMemoryIntDword(int register, int offset) {
        this.setRegister(register, this.memory.readDword(offset));
    }

    // MEMORY(REGISTER)

    /**
     * Writes byte at memory.
     * WMBR OFFSET, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void writeMemoryByteReg(int register, int register2) {
        this.writeMemoryByteDword(this.getRegister(register), this.getRegister(register2));
    }

    /**
     * Writes dword at memory.
     * WMIR OFFSET, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void writeMemoryIntReg(int register, int register2) {
        this.writeMemoryIntDword(this.getRegister(register), this.getRegister(register2));
    }

    /**
     * Reads byte from memory and stores at register.
     * RMBR REGISTER, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void readMemoryByteReg(int register, int register2) {
        this.readMemoryByteDword(register, this.getRegister(register2));
    }

    /**
     * Reads dword from memory and stores at register.
     * RMIR REGISTER, REGISTER2
     *
     * @param register
     * @param register2
     */
    private void readMemoryIntReg(int register, int register2) {
        this.readMemoryIntDword(register, this.getRegister(register2));
    }

    // Stack

    /**
     * Push value from register to stack.
     *
     * @param register PUSH REGISTER
     */
    private void pushDword(int register) {
        if (this.stack.isFull()) {
            this.cpuError();
        } else {
            this.stack.push(this.getRegister(register));
        }
    }

    /**
     * Pop value from stack to register.
     *
     * @param register POP REGISTER
     */
    private void popDword(int register) {
        this.setRegister(register, this.stack.pop());
    }

    /**
     * Pushes RIP to stack and modifies it to given address
     * CALL LABEL
     */
    private void call(int address) {
        this.stack.push(this.getRegister(RIP) - 1); // -1 because RIP is increased while fetching
        this.setRegister(RIP, address);
    }

    /**
     * Sets RIP to address from stack.
     * RETN
     */
    private void retn() {
        Instruction ins = new Instruction();
        if (this.stack.isEmpty()) {
            this.halt();
        } else {
            this.setRegister(RIP, this.stack.pop() + ins.getInstructionOffset(CALL));
        }
    }

    /**
     * Sets RIP to assemblySize and finishs execution.
     * HALT
     */
    private void halt() {
        this.setRegister(RIP, assemblySize);
    }

    /**
     * Handles sys functions.
     *
     * @param function
     */
    private void interrupt(int function) {
        String string1 = this.memory.readString(this.getRegister(R1));
        int position;

        switch (function) {
            /**
             * R1 = string
             * R2 = position in memory
             * Reads string from console.
             * Sets R1 to length of read string.
             */
            case SYS_READ_LINE:
                try {
                    String result = sysReadLine(string1);

                    this.memory.writeString(this.getRegister(R2), result);
                    this.setRegister(R1, result.length());
                } catch (IOException e) {
                    System.out.println("[IOError]: sysReadLine");
                }
                break;
            /**
             * R1 = string
             * Writes string to console.
             */
            case SYS_WRITE_LINE:
                sysWriteLine(string1);
                break;
            /**
             * R1 = filePath
             * R2 = position
             * Sets R1 to read byte.
             */
            case SYS_READ_FILE:
                position = this.getRegister(R2);
                try {
                    this.setRegister(R1, sysReadFile(string1, position));
                } catch (IOException e) {
                    System.out.println("[IOError]: sysReadFile");
                    this.cpuError();
                }
                break;
            /**
             * R1 = filePath
             * R2 = position
             * R3 = value
             */
            case SYS_WRITE_FILE:
                position = this.getRegister(R2);
                int value = this.getRegister(R3);

                try {
                    sysWriteFile(string1, position, (byte) value);
                } catch (IOException e) {
                    System.out.println("[IOError]: sysWriteFile");
                    this.cpuError();
                }
                break;
            /**
             * R1 = filePath
             * Sets R1 to file size;
             */
            case SYS_FILE_SIZE:
                this.setRegister(R1, sysFileSize(string1));
                break;

            /**
             * Sets R1 to memory size;
             */
            case SYS_MEM_SIZE:
                this.setRegister(R1, this.MEMORY);
                break;
        }
    }
}
