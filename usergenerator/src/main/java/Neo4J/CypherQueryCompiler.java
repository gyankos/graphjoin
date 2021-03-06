/*
 * CypherQueryCompiler.java
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


import java.util.*;
import java.util.stream.Collectors;

public  class CypherQueryCompiler {
        final private Set<String> hashScheme;
        final private Set<String> completeSchema;
        private boolean lr;

        private static String eqRule(String src_dst, String property) {
            return src_dst+("1")+"."+property+"1="+src_dst+("2")+"."+property+"2";
        }

        private static String neqRule(String src_dst, String property) {
            return src_dst+("1")+"."+property+"1<>"+src_dst+("2")+"."+property+"2";
        }

        private static String reconstruct(String src_dst,String property) {
            return property+("1")+":"+src_dst+("1")+"."+property+"1, "+property+("2")+":"+src_dst+("2")+"."+property+"2 ";
        }

        private static String eqRule(String src_dst,Collection<String> property) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = property.iterator();
            while (it.hasNext()) {
                sb.append(eqRule(src_dst,it.next()));
                if (it.hasNext()) sb.append(" AND ");
            }
            return sb.toString();
        }

        private static String neqRule(String src_dst,Collection<String> property) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = property.iterator();
            while (it.hasNext()) {
                sb.append(neqRule(src_dst,it.next()));
                if (it.hasNext()) sb.append(" OR ");
            }
            return sb.toString();
        }

        private static String reconstruct(String src_dst,Collection<String> property) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = property.iterator();
            while (it.hasNext()) {
                sb.append(reconstruct(src_dst,it.next()));
                if (it.hasNext()) sb.append(", ");
            }
            return sb.toString();
        }

        public CypherQueryCompiler(String... args) {
            this(false,args);
        }

        public Set<String> mapLeft() {
            return lr ? hashScheme.stream().map(x->x+"1").collect(Collectors.toSet()) : hashScheme;
        }

        public Set<String> mapRight() {
            return lr ? hashScheme.stream().map(x->x+"2").collect(Collectors.toSet()) : hashScheme;
        }

        public Set<String> mapCypher() {
            HashSet<String> toret = new HashSet<>(mapLeft());
            toret.addAll(mapRight());
            return toret;
        }

        public CypherQueryCompiler(boolean lr, String commonSchema[], String... args) {
            this.lr = lr;
            Set<String> hashSet1 = new HashSet<>();
            hashSet1.addAll(Arrays.asList(args));
            this.hashScheme = hashSet1;
            this.completeSchema = new HashSet<>();
            completeSchema.addAll(Arrays.asList(commonSchema));
        }

        public String generateCypherQuery() {
            return 
		    // Conjunctive case: the only one where I create both vertices and edges
                    "MATCH (src1)-[:r]->(dst1),\n" +
                    "     (src2)-[:r]->(dst2)\n" +
                    "WHERE "+eqRule("src",hashScheme)+" AND "+eqRule("dst",hashScheme)+" AND src1.graph='L' AND src2.graph='R' AND dst1.graph='L' AND dst2.graph='R'\n" +
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID, MyGraphLabel:\"U-\"})-[:r]->(:U {id1:dst1.UID, id2:dst2.UID, MyGraphLabel:\"U-\"}) return p\n" +
                    "UNION ALL\n" +
                    // In all the other remaining cases, I just create the vertices with no edges
                    "MATCH (src1)-[:r]->(dst1), (src2)-[:r]->(dst2)\n"+
                    "WHERE "+eqRule("src",hashScheme)+" AND src1.graph='L' AND src2.graph='R' AND (("+neqRule("dst",hashScheme)+"))\n" +
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID,  MyGraphLabel:\"U-\"}) return p\n" +
                    "UNION ALL\n"+
                    "MATCH (src1)-[:r]->(u), (src2)\n"+
                    "WHERE "+eqRule("src",hashScheme)+" AND src1.graph='L' AND src2.graph='R' AND (NOT ((src2)-[:r]->()))\n" +
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID,  MyGraphLabel:\"U-\"}) return p\n"+
                    "UNION ALL\n" +
                    "MATCH (src1), (src2)-[:r]->(v)\n"+
                    "WHERE "+eqRule("src",hashScheme)+" AND src1.graph='L' AND src2.graph='R' AND (NOT ((src1)-[:r]->()))\n" +
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID,  MyGraphLabel:\"U-\"}) return p\n"+
                    "UNION ALL\n" +
                    "MATCH (src1), (src2)\n"+
                    "WHERE "+eqRule("src",hashScheme)+"src1.UID=src2.UID AND src1.graph='L' AND src2.graph='R' AND (NOT ((src2)-[:r]->())) AND (NOT ((src1)-[:r]->()))\n" +
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID,  MyGraphLabel:\"U-\"}) return p\n";
        }

	// TODO!!!!
	public String generateCypherDisjunctiveQuery() {
            return  // Preserving the conjunctive case for the disjunctive semantics
                    "MATCH (src1)-[:r]->(dst1),\n" +
                    "     (src2)-[:r]->(dst2)\n" +
                    "WHERE "+eqRule("src",hashScheme)+" AND "+eqRule("dst",hashScheme)+" AND src1.graph='L' AND src2.graph='R' AND dst1.graph='L' AND dst2.graph='R'\n" +
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID, MyGraphLabel:\"U-\"})-[:r]->(:U {id1:dst1.UID, id2:dst2.UID, MyGraphLabel:\"U-\"}) return p\n" +
                    "UNION ALL\n" +
                    // If the destination vertices do not match, then the edge has not to be drawn (merging with the last case of the conjunction, so to reduce the number of the elements within the query plan, that explodes under the disjunction)
                    "MATCH (src1), (src2)\n"+
                    "OPTIONAL MATCH (src1)-[:r]->(dst1), (src2)-[:r]->(dst2)\n"+
                    "WHERE "+eqRule("src",hashScheme)+" AND src1.graph='L' AND src2.graph='R' AND dst1.graph='L' AND dst2.graph='R' AND (("+neqRule("dst",hashScheme)+"))\n" +
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID,  MyGraphLabel:\"U-\"}) return p\n" +
                    "UNION ALL\n"+
		    // If one of the vertices has no outgoing edges, still the outgoing element needs to be linked via left edge
                    "MATCH (src1)-[:r]->(dst1), (src2)\n"+
		    "OPTIONAL MATCH (dst2)\n"+
                    "WHERE "+eqRule("src",hashScheme)+" AND "+eqRule("dst",hashScheme)+" AND src1.graph='L' AND src2.graph='R' AND dst1.graph='L' AND dst2.graph='R' AND ((NOT ((src2)-[:r]->())))\n" +
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID,  MyGraphLabel:\"U-\"})-[:r]->(:U {id1:dst1.UID, id2:dst2.UID, MyGraphLabel:\"U-\"}) return p\n"+
                    "UNION ALL\n" +
		    // If one of the vertices has no outgoing edges, still the outgoing element needs to be linked via right edge
                    "MATCH (src1), (src2)-[:r]->(dst2)\n"+
		    "OPTIONAL MATCH (dst1)\n"+
                    "WHERE "+eqRule("src",hashScheme)+" AND "+eqRule("dst",hashScheme)+" AND src1.graph='L' AND src2.graph='R' AND dst1.graph='L' AND dst2.graph='R' AND ((NOT ((src1)-[:r]->())))\n"+
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID,  MyGraphLabel:\"U-\"})-[:r]->(:U {id1:dst1.UID, id2:dst2.UID, MyGraphLabel:\"U-\"}) return p\n"+
                    "UNION ALL\n" +
 		    // Replication: in the case that the nodes have some edges, but none of them satisfy the pattern			
                    // 1)
		    "MATCH (src1)-[:r]->(dst1),\n"+
		    "(src2)-[:r]->(dst3)\n"+
		    "OPTIONAL MATCH (dst2)\n"+
		    "WITH src1, dst1, src2, COLLECT(dst3) as coll, dst2\n"+
		    "WHERE src1.graph='L' AND src2.graph='R' AND dst1.graph='L' AND dst2.graph='R' AND "+eqRule("src",hashScheme)+" AND "+eqRule("dst",hashScheme)+" AND NONE (x IN coll WHERE "+eqRule("dst",hashScheme).replaceAll("dst2","x")+")\n"+ 
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID,  MyGraphLabel:\"U-\"})-[:r]->(:U {id1:dst1.UID, id2:dst2.UID, MyGraphLabel:\"U-\"}) return p\n"+
                    "UNION ALL\n" +
		    // 2) 
		    "MATCH (src1)-[:r]->(dst3),\n"+
		    "(src2)-[:r]->(dst2)\n"+
		    "OPTIONAL MATCH (dst1)\n"+
		    "WITH src1, dst1, src2, COLLECT(dst3) as coll, dst2\n"+
		    "WHERE src1.graph='L' AND src2.graph='R' AND dst1.graph='L' AND dst2.graph='R' AND "+eqRule("src",hashScheme)+" AND "+eqRule("dst",hashScheme)+" AND NONE (x IN coll WHERE "+eqRule("dst",hashScheme).replaceAll("dst1","x")+")\n"+ 
                    "CREATE p=(:U {id1:src1.UID, id2:src2.UID,  MyGraphLabel:\"U-\"})-[:r]->(:U {id1:dst1.UID, id2:dst2.UID, MyGraphLabel:\"U-\"}) return p\n";
        }

    }

