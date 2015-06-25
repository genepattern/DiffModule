package org.genepattern.ModuleResultsDiff;

import java.io.IOException;

/**
 * GenePattern module for comparing a result file produced by a GenePattern module with a known
 * good result file. More generally, runs a diff utility to get the differences between two files.
 * 
 * Returns an error code if differences are found.
 * 
 * @author cnorman
 */
public class DiffMain {

    private final class statusCode {
        static final int cError = -2;
        static final int cSuccess = 0;
        static final int cBadArgs = 100;
        static final int cIOException = 101;
        static final int cGeneralException = 102;
    };

    /**
     * @param args - array of arguments conforming to: 'diff input_file_1 input_file_1' 
     *
     */
    public static void main(String[] args) {
        int returnCode = statusCode.cError;

        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: 'diff file1 file2'");
            returnCode = statusCode.cBadArgs;
        }

        returnCode = commandLineDiff(args); // do the actual diff

        if (returnCode == statusCode.cSuccess) {
            System.out.println("No differences found!");
        } else {
            for (String arg: args) {
                System.err.println(arg);
            }
            System.err.println("Error - diff failed with return code: "
                    + Integer.toString(returnCode));
        }
        System.exit(returnCode);
    }

    /**
     * @param args - array arguments conforming to: 'diff input_file_1 input_file_1' 
     *
     */
    private static int commandLineDiff(String[] args) {
        int exitCode = statusCode.cError;

        String actArgs[] = new String[args.length + 1];

        // to prevent execution of random programs on the server, only run diff
        if (args[0].equals("diff")) {
            return statusCode.cBadArgs;
        }
        actArgs[0] = "diff";

        for (int i = 1; i < actArgs.length; i++) {
           actArgs[i] = args[i-1];
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(actArgs).inheritIO();
            Process proc = pb.start();
            exitCode = proc.waitFor();
        }
        catch (IOException e) {
            exitCode = statusCode.cIOException;
            System.err.println("IOException: " + e.toString());
        }
        catch (Exception e) {
            exitCode = statusCode.cGeneralException;
            System.err.println("Exception: " + e.toString());
        }
        
        return exitCode;
    }
}
