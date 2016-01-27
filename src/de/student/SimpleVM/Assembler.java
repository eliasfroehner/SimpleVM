package de.student.SimpleVM;

import java.io.*;
import java.util.*;

import static de.student.SimpleVM.Instruction.*;

/**
 * Reads assembler file and assembles it.
 */
public class Assembler {
    private String filePath;
    private boolean containsErrors;

    private Assembly asm;
    private Map<String, Integer> constants;
    private Map<String, Integer> variableSize;

    // Directives
    final String CONST = ".const";
    final String DWORD = ".dword";
    final String BYTES = ".bytes";
    final String STRING = ".string";

    public Assembler(String path) {
        this.filePath = path;
        this.containsErrors = false;

        asm = new Assembly();
        constants = new HashMap<>();
        variableSize = new LinkedHashMap<>();
    }

    /**
     * Reads file line by line.
     */
    public boolean assemble() {
        List<Integer> assembly = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Filter whitespaces
                line = filterLine(line);
                if (!isEmptyLine(line)) {
                    if (isDirective(line)) {
                        assembly.addAll(this.parseDirective(line));
                    }
                    // Skip comments
                    else {
                        List<Integer> parsedByteCode = parseInstruction(line);
                        if (!parsedByteCode.isEmpty()) {
                            assembly.addAll(parsedByteCode);
                        }
                    }
                }
            }
            if (!this.containsErrors) {
                this.asm.setAssembly(assembly);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            System.out.println("Could not read line.");
        }
        return true;
    }

    /**
     * Returns assembled byte code. Ready for runtime.
     *
     * @return
     */
    public List<Integer> getByteCode() {
        return this.asm.getAssembly();
    }

    /**
     * Writes file to destination.
     *
     * @param dest
     */
    public void writeFile(String dest) {
        writeExecutable(dest);
    }

    /**
     * Parses directive.
     * types: constant, dword, string
     *
     * @param line
     */
    private List<Integer> parseDirective(String line) {
        List<Integer> byteCode = new ArrayList<>();
        String[] instruction = preProcessDirective(line.split(" "));

        if (instruction.length != 3) {
            printWarning("Ignored wrong instruction syntax", line);
        } else {
            String directive = instruction[0];
            String name = instruction[1];
            String value = instruction[2];

            switch (directive) {
                case CONST:
                    if (constants.containsKey(name)) {
                        printWarning("Constant already defined -> ignored", name);
                    } else {
                        if (isInteger(value)) {
                            constants.put(name, Integer.parseInt(value));
                        } else if (isHexInteger(value)) {
                            constants.put(name, parseHexInteger(value));
                        } else if (variableSize.containsKey(value)) {
                            constants.put(name, variableSize.get(value));
                        }
                    }
                    break;
                case DWORD:
                    if (variableSize.containsKey(name)) {
                        printWarning("Dword already defined -> ignored", name);
                    } else {
                        if (isInteger(value)) {
                            byteCode = this.buildAssemblerCodeDword(Integer.parseInt(value));
                        } else if (isHexInteger(value)) {
                            byteCode = this.buildAssemblerCodeDword(parseHexInteger(value));
                        }

                        variableSize.put(name, Integer.BYTES);
                    }
                    break;
                case BYTES:
                    if (variableSize.containsKey(name)) {
                        printWarning("Byte array already defined -> ignored", name);
                    } else {
                        byteCode = this.buildAssemblerCodeByteArray(value);
                        if (byteCode.size() > 0) {
                            variableSize.put(name, getByteArrayLength(value)); // +1 because 0 terminated
                        }
                    }
                    break;
                case STRING:
                    if (variableSize.containsKey(name)) {
                        printWarning("String already defined -> ignored", name);
                    } else {
                        byteCode = this.buildAssemblerCodeString(value);
                        if (byteCode.size() > 0) {
                            variableSize.put(name, getStringLength(value) + 1); // +1 because 0 terminated
                        }
                    }
                    break;
            }
        }

        return byteCode;
    }

    /**
     * Process line and converts to binary.
     *
     * @param line
     * @return
     */
    private List<Integer> parseInstruction(String line) {
        List<Integer> byteCode = new ArrayList<>();
        Instruction ins = new Instruction();
        Register reg = new Register();
        Assembly asm = new Assembly();
        String[] opCode = line.split(" ");

        int operand = ins.getInstructionFromMnemonic(opCode[0]);
        if (operand == -1) {
            printWarning("Wrong Operation code", line);
        } else {
            byteCode.add(operand);
            // Resolve label to int
            if (operand == LABEL || operand == JMP || operand == JE || operand == JNE || operand == JG || operand == JB || operand == CALL) {
                if (opCode.length != 2) {
                    printWarning("Wrong formatted label", line);
                    byteCode.clear();
                } else {
                    byteCode.add(asm.labelToInt(opCode[1]));
                }
            } else {
                for (int i = 1; i < ins.getInstructionOffset(operand) + 1; i++) {
                    String arg = opCode[i];

                    if (arg.indexOf(' ') != -1) {
                        printWarning("Wrong formatted arguments", arg);
                        byteCode.clear();
                        break;
                    } else {
                        if (isInteger(arg)) {
                            byteCode.add(Integer.parseInt(arg));
                        } else if (isHexInteger(arg)) {
                            byteCode.add(parseHexInteger(arg));
                        } else {
                            int register = reg.getRegisterFromMnemonic(arg);
                            if (register == -1) {
                                if (constants.containsKey(arg)) {
                                    byteCode.add(constants.get(arg));
                                } else if (variableSize.containsKey(arg)) {
                                    byteCode.add(this.getVariableOffset(arg));
                                } else {
                                    printWarning("Wrong register or not defined", arg);
                                    byteCode.clear();
                                }
                            } else {
                                byteCode.add(register);
                            }
                        }
                    }
                }
            }
        }

        return byteCode;
    }

    /**
     * Writes assembled file to filePath + ".sasm"
     *
     * @param destination
     */
    private void writeExecutable(String destination) {
        String assembled = "";

        // Make executable.
        for (int byteCode : asm.getAssembly()) {
            assembled = assembled.concat(Integer.toString(byteCode, 16));
            assembled = assembled.concat(";");
        }

        try {
            PrintWriter out = new PrintWriter(destination);
            out.println(assembled);
            out.close();

            System.out.println("Executable written to: " + destination);
        } catch (FileNotFoundException e) {
            printWarning("File could not be written", destination);
        }
    }

    /**
     * Writes hard coded asm code to fill space.
     *
     * @param string
     * @return
     */
    private List<Integer> buildAssemblerCodeString(String string) {
        List<Integer> byteCode = new ArrayList<>();
        if (string.startsWith("'") && string.endsWith("'")) {
            String cutString = string.substring(1, string.length() - 1);
            byte[] bytes = cutString.getBytes();
            int usedSpace = this.getUsedSpace();

            for (int i = 0; i < bytes.length; i++) {
                byteCode.add(WRITE_MEM_BYTE_DWORD);
                byteCode.add(usedSpace + i);
                byteCode.add((int) bytes[i]);
            }
        } else {
            printWarning("Wrong string format -> ' missing?", string);
        }

        return byteCode;
    }

    /**
     * Writes hard coded asm code to fill space.
     *
     * @param string
     * @return
     */
    private List<Integer> buildAssemblerCodeByteArray(String string) {
        List<Integer> byteCode = new ArrayList<>();
        String[] bytes = string.split(",");
        int usedSpace = this.getUsedSpace();
        int value = 0;
        
        for (int i = 0; i < bytes.length; i++) {
            byteCode.add(WRITE_MEM_BYTE_DWORD);
            byteCode.add(usedSpace + i);
           
            if (isInteger(bytes[i])) {
            	value = Integer.parseInt(bytes[i]);                
            } else if (isHexInteger(bytes[i])) {
            	value = parseHexInteger(bytes[i]);
            } else {
                printWarning("Invalid byte in sequence", bytes[i]);
                byteCode.clear();
                break;
            }
            
            if (value <= Byte.MAX_VALUE) {
            	byteCode.add(value);
            }
            else {
            	printWarning("Value is greater than 255 for byte array", bytes[i]);
                byteCode.clear();
                break;
            }
        }

        return byteCode;
    }

    /**
     * Writes hard coded asm code to fill space.
     *
     * @param dword
     * @return
     */
    private List<Integer> buildAssemblerCodeDword(int dword) {
        List<Integer> byteCode = new ArrayList<>();
        int usedSpace = this.getUsedSpace();

        byteCode.add(WRITE_MEM_INT_DWORD);
        byteCode.add(usedSpace);
        byteCode.add(dword);

        return byteCode;
    }

    /**
     * Rebuilds string directive.
     */
    private String[] preProcessDirective(String[] line) {
        String directive = line[0];
        String[] rebuilded = new String[3];

        if (directive.equals(STRING)) {
            rebuilded[0] = line[0]; // directive
            rebuilded[1] = line[1]; // name
            rebuilded[2] = ""; // value

            for (int i = 2; i < line.length; i++) {
                rebuilded[2] = rebuilded[2].concat(line[i]);
                // end of parts?
                if (i + 1 < line.length) {
                    rebuilded[2] = rebuilded[2].concat(" ");
                }
            }
        } else {
            rebuilded = line;
        }

        return rebuilded;
    }

    /**
     * Filter line from spaces and comments.
     */
    private String filterLine(String line) {
        String result = line.trim();
        String[] lineSplitted = line.split("#");

        if (lineSplitted.length >= 2) {
            result = lineSplitted[0].trim();
        } else if (line.startsWith("#")) {
            result = "";
        }

        return result;
    }

    /**
     * Calculates variable position in memory.
     */
    private int getVariableOffset(String varName) {
        int offset = 0;
        int i = 0;


        for (Map.Entry<String, Integer> entry : variableSize.entrySet()) {
            if (entry.getKey().equals(varName)) break;
            offset += entry.getValue();
        }

        return offset;
    }

    /**
     * Splits byte array and counts elements.
     */
    private int getStringLength(String s) {
        return s.substring(1, s.length() - 1).length();
    }

    /**
     * Splits byte array and counts elements.
     */
    private int getByteArrayLength(String s) {
        return s.split(",").length;
    }

    /**
     * Gets used space.
     *
     * @return
     */
    private int getUsedSpace() {
        int size = 0;

        for (Map.Entry<String, Integer> entry : variableSize.entrySet()) {
            size += entry.getValue();
        }

        return size;
    }

    /**
     * Prints formatted warning.
     *
     * @param type
     * @param warning
     */
    private void printWarning(String type, String warning) {
        System.out.println("[ERROR] " + type + ": " + warning);
        this.containsErrors = true;
    }

    /**
     * Checks if line is directive.
     *
     * @param line
     * @return
     */
    private boolean isEmptyLine(String line) {
        return (line.length() == 0);
    }

    /**
     * Checks if line is directive.
     *
     * @param line
     * @return
     */
    private boolean isDirective(String line) {
        return (line.charAt(0) == '.');
    }

    /**
     * Checks if argument is Integer.
     *
     * @param s
     * @return
     */
    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    /**
     * Checks if argument is Integer in Base 16.
     *
     * @param s
     * @return
     */
    private boolean isHexInteger(String s) {
        String hex = s;

        if (hex.startsWith("0x")) {
            hex = hex.substring(2, hex.length());
        }
        try {
            Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    /**
     * Checks if argument is Integer in Base 16.
     *
     * @param s
     * @return
     */
    private int parseHexInteger(String s) {
        return Integer.parseInt(s.substring(2, s.length()), 16);
    }

}
