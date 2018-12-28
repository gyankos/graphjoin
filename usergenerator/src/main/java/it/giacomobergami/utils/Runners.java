package it.giacomobergami.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class Runners {

    public static void run(File envDirectory, Map<String, String> exportEnv, String... args) throws IOException, InterruptedException {
        run(envDirectory, exportEnv, null, false, args);
    }

    public static void run(File envDirectory, Map<String, String> exportEnv, File outFile, boolean doAppend, String... args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(args);

        if (outFile != null && outFile.getParentFile().exists()) {
            pb = pb.redirectOutput(doAppend ? ProcessBuilder.Redirect.appendTo(outFile).file() : outFile);
        } else {
            pb = pb.inheritIO();
        }

        if (envDirectory != null && envDirectory.exists())
            pb.directory(envDirectory);

        if (exportEnv != null)
            pb.environment().putAll(exportEnv);
        Process p = pb.start();
        p.waitFor();
    }

}
