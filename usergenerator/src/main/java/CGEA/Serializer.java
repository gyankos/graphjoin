package CGEA;

import it.giacomobergami.utils.Runners;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Serializer extends Runners {

    public static void main(String args[]) throws IOException, InterruptedException {
        Properties properties = new Properties();
        properties.load(new FileReader(args[0]));
        File graphFile = new File(properties.getProperty("DATASET_FOLDER"), properties.getProperty("DATASET_PREFIX"));

        File benchmark_out = null;
        boolean append = false;
        if (properties.containsKey("REDIRECT_SAMPLING_CSV")) {
            benchmark_out = new File(properties.getProperty("REDIRECT_SAMPLING_CSV"));
            append = true;
        }

        System.out.println("Serializing the operands (loading+indexing):");
        System.out.println("================================================");
        String graphPrefix = graphFile.getAbsolutePath()+"_";
        Runners.run(null, null, benchmark_out, append, properties.getProperty("SAMPLES_SERIALIZING"),
                graphPrefix,
                properties.getProperty("OTHER_PARAMS"),
                properties.getProperty("SAMPLES_PARAMETERS"),
                properties.getProperty("NEXT_PARAMETERS"),
                properties.getProperty("JOIN_ARGS_NUMBER"));
        System.out.println("================================================");
        System.out.println("================================================\n\n\n\n\n");
    }

}
