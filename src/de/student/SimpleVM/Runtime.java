package de.student.SimpleVM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes program.
 */
public class Runtime {
    private String filePath;
    private List<Integer> assembly;

    // Constants
    final String assemblyFileEnding = ".vasm";

    public Runtime(String path) {
        this.filePath = path;
        assembly = new ArrayList<>();
    }

    /**
     * Executes file.
     */
    public void run() {
        CPU vCPU = new CPU();
        long startTime = 0;
        long estimatedTime = 0;
        boolean noErrors = true;

        if (this.filePath.endsWith(this.assemblyFileEnding)) {
            System.out.println("--> Starting live assembly!");

            Assembler asm = new Assembler(this.filePath);
            noErrors = asm.assemble();
            if (noErrors) assembly = asm.getByteCode();
        } else {
            executableToAssembly();
        }

        if (!noErrors) {
            System.out.println("--> Assembler file contains errors.");
        } else if (assembly.isEmpty()) {
            System.out.println("--> Nothing to do.");
        } else {
            System.out.println("************************************Program*************************************");
            startTime = System.currentTimeMillis();
            vCPU.executeProgram(this.assembly);
            estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("********************************************************************************");

            vCPU.dumpRegisters();
            vCPU.dumpStack();
            vCPU.dumpMemory();

            System.out.println("--> Process finished with exit code " + vCPU.getRegister(Register.R1));
            System.out.println("--> Runtime: " + estimatedTime + " ms.");
        }
    }

    /**
     * Splits executable to byteCode
     */
    private void executableToAssembly() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line = br.readLine();
            String[] byteCode = line.split(";");

            for (String code : byteCode) {
                assembly.add(Integer.parseInt(code, 16));
            }
        } catch (IOException e) {
            System.out.println("Could not read executable.");
        }
    }
}
