/*
 * BenchmarkNeo4JQueryOverSamples.java
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

import org.neo4j.graphdb.Result;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 *  Performs some benchmarking over Neo4J
 */
public class BenchmarkNeo4JQueryOverSamples {

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        boolean isConjunctive = false;
        properties.load(new FileReader("sampler_chain_sql.properties"));
        String[] steps = properties.getProperty("SAMPLES_PARAMETERS").split(",");
        String[] vertexArguments = properties.getProperty("OPERAND_SCHEMA").split(",");
        String[] indexablearguments = properties.getProperty("JOIN_ARGS_NUMBER").split(",");
        String[] hashingSchema = new String[indexablearguments.length];
        for (int j = 0; j<indexablearguments.length; j++) {
            int argPos = Integer.valueOf(indexablearguments[j])+1;
            hashingSchema[j] = (vertexArguments[argPos]);
        }
        FileWriter fw = new FileWriter("join_neo4j_results.csv", true);
        CypherQueryCompiler tester = new CypherQueryCompiler(true, Arrays.copyOfRange(vertexArguments, 1, vertexArguments.length), hashingSchema);
        //System.out.println(tester.generateCypherQuery());
        //System.exit(1);

        for (int pos=1; pos<5; pos++) {
            System.out.println("Benchmarking graph join i-th:" + pos);
            Map<Set<String>, Set<String>> vertexLabelToAttributesToIndex = new HashMap<>(1);
            Set<String> type = new HashSet<>(1);
            type.add("User");
            Set<String> hashing;
            hashing = new HashSet<>(indexablearguments.length*2);
            for (int j = 0; j<indexablearguments.length; j++) {
                int argPos = Integer.valueOf(indexablearguments[j])+1;
                hashing.add(vertexArguments[argPos]+"1");
                hashing.add(vertexArguments[argPos]+"2");
            }
            vertexLabelToAttributesToIndex.put(type, hashing);
            Neo4JGraph g = new Neo4JGraph("UID", "r", false, null,pos+""); //Getting the graph
            long time_t = System.nanoTime();
            double t = g.startTransaction(()-> {
               Result r;
               System.out.println(tester.generateCypherDisjunctiveQuery());
                r = g.doGraphQuery(isConjunctive ? tester.generateCypherQuery() : tester.generateCypherDisjunctiveQuery());
                r.close();
                return 1.0;
            }).get();
            time_t = System.nanoTime() - time_t;
            System.out.println(steps[pos]+","+(((double)time_t) / 1000000.0));
            fw.write(steps[pos]+","+t+"\n");
            fw.flush();
            g.close();
        }
        System.gc();

        fw.close();
        //bgo.doGraphQuery(tester.generateCypherQuery())
    }

}
