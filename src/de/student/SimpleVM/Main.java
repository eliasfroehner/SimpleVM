package de.student.SimpleVM;

/**
 * Simple test class.
 */
public class Main {
    final static int RUNTIME_ARGS = 1;
    final static int ASSEMBLER_ARGS = 2;

    public static void main(String[] args) {
        // Runtime or compilation
        if (args.length == RUNTIME_ARGS) {
            Runtime runtime = new Runtime(args[0]);
            runtime.run();
        } else if (args.length == ASSEMBLER_ARGS) {
            Assembler asm = new Assembler(args[0]);
            if (asm.assemble()) asm.writeFile(args[1]);
            else System.out.println("--> File contains errors!");
        } else {
            System.out.println("Usage:");
            System.out.println("->Runtime");
            System.out.println("SimpleCPU.jar <filePath>");
            System.out.println("->Assembler");
            System.out.println("SimpleCPU.jar <inputFilePath> <outputFilePath>");
        }
    }
}