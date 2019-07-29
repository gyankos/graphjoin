/*
 * AggregateSamplesInNeo4j.java
 * This file is part of databaseMappings
 *
 * Copyright (C) 2016 - Giacomo Bergami
 *
 * databaseMappings is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * databaseMappings is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with databaseMappings. If not, see <http://www.gnu.org/licenses/>.
 */

package Neo4J;


import Neo4J.general.AbstractGraph;
import Neo4J.general.AbstractNode;
import Neo4J.general.GoogleGraphVertex;
import Neo4J.general.pointer.GraphIdPointerLong;
import Neo4J.general.pointer.Pointer;
import it.giacomobergami.utils.BenchmarkClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Stores the left and right operands that have been sampled in the raw format in Neo4J
 */
public class LoadNeo4JOperands extends BenchmarkClass {

    //public static final BSONHolder4 unserializer = new BSONHolder4();
    public static HashSet<String> hashing = new HashSet<>();
    public static final Pointer ptr = new GraphIdPointerLong(0L);

    /**
     * Returns the node as a vertex
     * @param inTransaction
     * @return
     */
    public static AbstractNode getOrCreateVertex(AbstractGraph inTransaction, int side, long id, long hop, Map<String, String> attributes) {
        //BSONHolder4 bulkVertexL = unserializer.unserialize(file,left.pos);
        GoogleGraphVertex vL = new GoogleGraphVertex("User", hashing, ptr);
        vL.setAttribute("UID",(id+hop)+"");
        vL.setAttribute("graph",side==1 ? "L" : "R");
        for (Map.Entry<String, String> x : attributes.entrySet()) {
            vL.setAttribute(x.getKey(), x.getValue());
        }
        return inTransaction.addVertex(vL);
    }

    public static void delete(File file) {
        if(file.isDirectory()){
            //directory is empty, then delete it
            if(file.list().length==0){
                file.delete();
            }else{
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    delete(fileDelete);
                }
                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                }
            }
        }else{
            //if file, then delete it
            file.delete();
        }
    }

    public static CSVParser readOperator(boolean isLeft, String file, String[] header) throws IOException {
        return CSVFormat.DEFAULT.withHeader(header).parse(new FileReader(file));
    }

    public static void main(String args[]) throws IOException {
        //Paths p = Paths.getInstance();
        Properties properties = new Properties();
        properties.load(new FileReader("sampler_chain_sql.properties"));

        String[] sizes = properties.getProperty("SAMPLES_PARAMETERS").split(",");
        String[] vertexArguments = properties.getProperty("OPERAND_SCHEMA").split(",");
        String[] indexablearguments = properties.getProperty("JOIN_ARGS_NUMBER").split(",");
        String[] operandSeed = properties.getProperty("NEXT_PARAMETERS").split(",");
        long operandLeft = Long.valueOf(operandSeed[0].trim());
        long operandRight = Long.valueOf(operandSeed[1].trim());
        String[] operandLeftSchema, operandRightSchema;
        String[] operandSchema = properties.getProperty("OPERAND_SCHEMA").split(",");
        operandLeftSchema = new String[operandSchema.length];
        operandRightSchema = new String[operandSchema.length];
        for (int i = 0, operandSchemaLength = operandSchema.length; i < operandSchemaLength; i++) {
            String x = operandSchema[i];
            operandLeftSchema[i] = x+"1";
            operandRightSchema[i] = x+"2";
        }

        for (int pos=0; pos<5; pos++) {
            File torem = new File(""+(pos));
            if (torem.exists()) {
                delete(torem);
            }
            System.out.println("Storing graph i-th:"+sizes[pos]);
            int currentSize = Integer.valueOf(sizes[pos]);
            String vertexLeftFile = (generateVertexFileName(properties, currentSize, operandLeft));
            String edgeLeftFile = generateEdgeFileName(properties, currentSize, operandLeft);
            String vertexRightFile = (generateVertexFileName(properties, currentSize, operandRight));
            String edgeRIghtFile = generateEdgeFileName(properties, currentSize, operandRight);
            Map<Set<String>,Set<String>> vertexLabelToAttributesToIndex = new HashMap<>(1);

            Set<String> type = new HashSet<>(1);
            type.add("User");

            // Defining the hashing indices: this is required to show that we want also to optimize the Neo4J computation
            Set<String> hashing = new HashSet<>(indexablearguments.length*2);
            for (int j = 0; j<indexablearguments.length; j++) {
                int argPos = Integer.valueOf(indexablearguments[j]);
                hashing.add(vertexArguments[argPos]+"1");
                hashing.add(vertexArguments[argPos]+"2");
            }

            vertexLabelToAttributesToIndex.put(type,hashing);
            long t = System.currentTimeMillis();
            Neo4JGraph g = new Neo4JGraph("UID","r",false,vertexLabelToAttributesToIndex,(pos)+""); // graphs are automatically stored in the current directory, with increasing numbers
            t = System.currentTimeMillis() - t;

            long tt = System.currentTimeMillis();

            // Vertex insertion
            long max = g.startTransaction(() -> {
                long maxUID = -1;
                try {
                    for (CSVRecord record : readOperator(true, vertexLeftFile, operandLeftSchema)) {
                        HashMap<String, String> map = new HashMap<>();
                        Long id = Long.valueOf(record.get(operandLeftSchema[0]));
                        for (int i = 1; i<operandLeftSchema.length; i++) {
                            map.put(operandLeftSchema[i], record.get(operandLeftSchema[i]));
                        }
                        getOrCreateVertex(g,1, id, 0, map);
                        maxUID = Long.max(maxUID, id);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                return maxUID+1;
            }).get();
            g.startTransaction(() -> {
                try (BufferedReader br = new BufferedReader(new FileReader(edgeLeftFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] src_dst = line.split("\t");
                        g.addEdge(g.getVertex("User", src_dst[0]), g.getVertex("User", src_dst[1]));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                return 0;
            });

            g.startTransaction(() -> {
                try {
                    for (CSVRecord record : readOperator(true, vertexRightFile, operandRightSchema)) {
                        HashMap<String, String> map = new HashMap<>();
                        Long id = Long.valueOf(record.get(operandRightSchema[0]));
                        for (int i = 1; i<operandRightSchema.length; i++) {
                            map.put(operandRightSchema[i], record.get(operandRightSchema[i]));
                        }
                        getOrCreateVertex(g,0, id, max, map);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                return 0L;
            });
            g.startTransaction(() -> {
                try (BufferedReader br = new BufferedReader(new FileReader(edgeRIghtFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] src_dst = line.split("\t");
                        long src = Long.valueOf(src_dst[0])+max, dst = Long.valueOf(src_dst[1])+max;
                        g.addEdge(g.getVertex("User", src+""), g.getVertex("User", dst+""));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                return 0;
            });
            tt = System.currentTimeMillis() - tt;
            g.close();
            System.out.println(pos+" in "+tt);
        }
    }

}