package it.giacomobergami.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class BenchmarkClass {

    public static String printNanoAsSecondExp(long nanos) {
        long nanoMax = 9;
        long expCount = ((long)Math.floor(Math.log10(nanos)));
        long expCountToPrint = expCount - nanoMax;
        double expNotation = ((double)nanos) / (Math.pow(10, expCount));
        long integer = ((long)Math.floor(expNotation));
        expNotation -= integer;
        long roundOff = Math.round(expNotation * 100);
        return integer+"."+roundOff+" $\\times 10^{"+expCountToPrint+"}$";
    }


    public static void playClip(File clipFile) throws IOException,
            UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        class AudioListener implements LineListener {
            private boolean done = false;
            @Override public synchronized void update(LineEvent event) {
                LineEvent.Type eventType = event.getType();
                if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
                    done = true;
                    notifyAll();
                }
            }
            public synchronized void waitUntilDone() throws InterruptedException {
                while (!done) { wait(); }
            }
        }
        AudioListener listener = new AudioListener();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile);
        try {
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(listener);
            clip.open(audioInputStream);
            try {
                clip.start();
                listener.waitUntilDone();
            } finally {
                clip.close();
            }
        } finally {
            audioInputStream.close();
        }
    }

    public static String generateEdgeFileName(Properties properties, long currentSize, long operandSeed) {
        String header = properties.getProperty("DATASET_FOLDER");
        return new File(header, properties.getProperty("DATASET_PREFIX") + "_" + currentSize + "_" + operandSeed + "_" + properties.getProperty("OTHER_PARAMS")).getAbsolutePath();
    }

    public static String generateVertexFileName(Properties properties, long currentSize, long operandSeed) {
        String header = properties.getProperty("DATASET_FOLDER");
        return new File(header, properties.getProperty("DATASET_PREFIX") + "_" + currentSize + "_" + operandSeed + "_" + properties.getProperty("OTHER_PARAMS") +"_vertices.csv").getAbsolutePath();
    }

    public static String createVertexTable(boolean isLeftOperand) {
        return "create table "+(isLeftOperand ? "l" :"r")+"v(id serial not null, sex gender, name varchar, surname varchar, dob varchar, email varchar, company varchar, residence varchar);";
    }

    public static String createEdgeTable(boolean isLeftOperand) {
        return "create table "+(isLeftOperand ? "l" :"r")+"e(src integer, dst integer);";
    }

    public static String loadOperandVertex(boolean isLeftOperand/*, String file*/) {
        return "copy "+(isLeftOperand ? "l" :"r")+"v(id,sex,name,surname,dob,email,company,residence) from STDIN DELIMITER ',' CSV HEADER;";
    }

    public static String loadOperandEdge(boolean isLeftOperand/*, String file*/) {
        return "copy "+(isLeftOperand ? "l" :"r")+"e(src,dst) from STDIN delimiter E'\\t'  CSV;";
    }

}
