package CGEA;

import com.google.common.io.Files;
import it.giacomobergami.usergenerator.CreateVertexFilesFromEdges;
import it.giacomobergami.utils.Runners;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

public class DatasetSampleGenerator extends Runners {

    public static void main(String args[]) throws IOException, InterruptedException {
        Properties properties = new Properties();
        properties.load(new FileReader(args[0]));

        System.out.println("Creating the tmp file for sampling configuration");
        System.out.println("================================================");
        System.out.println(" - Generated Configuration: ");
        File graphFile = new File(properties.getProperty("DATASET_FOLDER"), properties.getProperty("DATASET_PREFIX"));
        String conf_txt_content = "graph="+graphFile.getAbsolutePath()+"\nsamples="+properties.getProperty("SAMPLES_PARAMETERS")+"\nnext="+properties.getProperty("NEXT_PARAMETERS")+"\nstarting="+properties.getProperty("STARTING_SAMPLE");
        System.out.println(conf_txt_content);
        System.out.println("================================================");
        File tempFile = File.createTempFile("conf.txt", DatasetSampleGenerator.class.getName());
        Files.asCharSink(tempFile, Charset.forName("UTF-8")).write(conf_txt_content);
        // Sampling
        Runners.run(null, null, properties.getProperty("EXTRACT_OPERANDS_FROM_DATASET"),
                                             tempFile.getAbsolutePath());
        System.out.println("================================================");
        System.out.println("================================================\n\n\n\n\n");
        tempFile.delete();

        System.out.println("Running the vertex generation for each generated operand:");
        System.out.println("================================================");
        CreateVertexFilesFromEdges.execute(new String[]{properties.getProperty("DATASET_FOLDER")}, graphFile);
        System.out.println("================================================");
        System.out.println("================================================\n\n\n\n\n");


        System.out.println("Reindexing the operands 0-(N-1) in the vertex csv and edge file list:");
        System.out.println("================================================");
        String graphPrefix = graphFile.getAbsolutePath()+"_";
        Runners.run(null, null, properties.getProperty("SAMPLES_REINDEXING"),
                                             graphPrefix,
                                             properties.getProperty("OTHER_PARAMS"),
                                             properties.getProperty("SAMPLES_PARAMETERS"),
                                             properties.getProperty("NEXT_PARAMETERS"));
        System.out.println("================================================");
        System.out.println("================================================\n\n\n\n\n");
    }

}
