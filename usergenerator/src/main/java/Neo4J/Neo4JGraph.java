/*
 * Neo4JGraph.java
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
import Neo4J.general.GeneralEdge;
import it.giacomobergami.utils.datastructures.HashableHashSet;
import org.neo4j.cypher.internal.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import java.io.File;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Mapping a simple data model into a Neo4J Graph
 */
public class Neo4JGraph extends AbstractGraph<AbstractNode> {

    private static BigInteger bi = new BigInteger("0");
    private final Map<Set<String>, Set<String>> vertexLabelToAttributesToIndex;
    //private static TestGraphDatabaseFactory fact = new TestGraphDatabaseFactory();
    //private static GraphDatabaseFactory memory = new GraphDatabaseFactory();
    private GraphDatabaseService graphDb;
    private String name;
    private String ID;
    private String edgeLabel;

    public String getGlobalEdgeLabel() {
        return edgeLabel;
    }


    public Neo4JGraph(GraphDatabaseService graphDb) {
        this.ID = "UID";
        this.graphDb = graphDb;
        this.edgeLabel = "r";
        vertexLabelToAttributesToIndex = null;
    }

    public Neo4JGraph(/*File path, String name, */String vertexID, String edgeLabel, boolean inMemory) {
        this(vertexID,edgeLabel,inMemory,null);
    }

    public Neo4JGraph(String vertexID, String edgeLabel, boolean inMemory, Map<Set<String>,Set<String>> vertexLabelToAttributesToIndex) {
        this(vertexID,edgeLabel,inMemory,vertexLabelToAttributesToIndex,bi.toString());
        if (!inMemory) {
            bi = bi.add(BigInteger.ONE);
        }
    }

    public Neo4JGraph(String vertexID, String edgeLabel, boolean inMemory, Map<Set<String>,Set<String>> vertexLabelToAttributesToIndex, String path) {
        //this.name = name;
        this.ID = vertexID;
        //graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(path));

        this.edgeLabel = edgeLabel;
        //labelIndex = new HashMap<>();
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).

        if (!inMemory) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    graphDb.shutdown();
                }
            });
        }
        ArrayList<IndexDefinition> indexesCreated = new ArrayList<>();
        if (vertexLabelToAttributesToIndex!=null)
        try ( Transaction tx = graphDb.beginTx() )
        {
            Schema schema = graphDb.schema();
            for (Set<String> kk : vertexLabelToAttributesToIndex.keySet()) {
                for (String k : kk) {
                    IndexDefinition indexDefinition = schema.indexFor(DynamicLabel.label(k))
                            .on(vertexID)
                            .create();
                    indexesCreated.add(indexDefinition);
                }
            }
            tx.success();
        }
        if (vertexLabelToAttributesToIndex!=null)
        try ( Transaction ignored = graphDb.beginTx() )
        {
            Schema schema = graphDb.schema();
            for (IndexDefinition i : indexesCreated)
                schema.awaitIndexOnline( i, 10, TimeUnit.SECONDS );
        }
        this.vertexLabelToAttributesToIndex = vertexLabelToAttributesToIndex == null || vertexLabelToAttributesToIndex.isEmpty() ? null : vertexLabelToAttributesToIndex;
    }

    public Set<String> getVertexLabelHashableSchema(Set<String> label) {
        return vertexLabelToAttributesToIndex == null || vertexLabelToAttributesToIndex.get(label).isEmpty() ? null : vertexLabelToAttributesToIndex.get(label);
    }

    public Set<String> getVertexLabelHashableSchema(Node label) {
        Set <String> toret = new HashSet<>();
        for (Label k : label.getLabels()) {
            toret.add(k.name());
        }
        return getVertexLabelHashableSchema(toret);
    }

    public Neo4JGraph(/*File path, String name*/boolean inMemory) {
        this(/*path,name,*/"UID","r",inMemory);
    }

    private void clearIndex() {
        IndexManager indexManager = graphDb.index();
        try (Transaction tx = graphDb.beginTx()) {
            for (String ix : indexManager.nodeIndexNames()) {
                try {
                    Index<Node> nodeIndex = indexManager.forNodes(ix);
                    if (nodeIndex.isWriteable()) nodeIndex.delete();
                } catch (Exception e) {
                    System.err.println("Cannot delete node index " + ix);
                    e.printStackTrace();
                }
            }
            for (String ix : indexManager.relationshipIndexNames()) {
                try {
                    RelationshipIndex relationshipIndex = indexManager.forRelationships(ix);
                    if (relationshipIndex.isWriteable()) relationshipIndex.delete();
                } catch (Exception e) {
                    System.err.println("Cannot delete relationship index " + ix);
                    e.printStackTrace();
                }
            }
            tx.success();
        }
    }



    @Override
    public boolean isEdge(AbstractNode src, AbstractNode dst) {
        Node f = toRawNode(src);
        if (f != null) {
            for (Relationship k : f.getRelationships(Direction.OUTGOING)) {
                Node e = k.getEndNode();
                if (new Neo4JVertex(e,this,e.getId()).equals(dst)) {
                    return true;
                }
            }
        }
        //}
        return false;
    }


    @Override
    public AbstractNode getVertex(AbstractNode src) {
        if (src==null)
            return null;
        ResourceIterator<Node> nodes = graphDb.findNodes(DynamicLabel.label(jointype(src)), ID, src.getAttribute(ID));
        while (nodes.hasNext()) {
            Node n = nodes.next();
            Neo4JVertex tomatch = new Neo4JVertex(n,this,n.getId());
            if (tomatch.equals(src))
                return tomatch;
        }
        return null;
    }

    public AbstractNode getVertex(String labels, String src) {
        if (src==null)
            return null;
        ResourceIterator<Node> nodes = graphDb.findNodes(DynamicLabel.label(labels), ID, src);
        while (nodes.hasNext()) {
            Node n = nodes.next();
            Neo4JVertex tomatch = new Neo4JVertex(n,this,n.getId());
            if (tomatch.getAttribute(ID).equals(src))
                return tomatch;
        }
        return null;
    }


    @Override
    public boolean hasVertex(AbstractNode src) {
        return getVertex(src)!=null;
    }

    @Override
    public Iterator<GeneralEdge> getEdges() {
        return graphDb.getAllRelationships().stream().map(x->(GeneralEdge)new Neo4JEdge(this,x)).iterator();
    }

    private Node toRawNode(AbstractNode src) {
        if (src==null)
            return null;
        AbstractNode rSrc = getVertex(src);
        return rSrc!=null ? ((Neo4JVertex)rSrc).getRaw() : null;
    }


    @Override
    public HashableHashSet<AbstractNode> getNeighbours(AbstractNode src) {
        Node f = toRawNode(src);
        HashableHashSet<AbstractNode> toret = new HashableHashSet<>();
        if (f!=null) {
            for (Relationship r : f.getRelationships(Direction.OUTGOING)) {
                toret.add(new Neo4JVertex(r.getEndNode(),this,r.getEndNode().getId()));
            }
        }
        return toret;
    }

    @Override
    public GeneralEdge addEdge(AbstractNode src, AbstractNode dst) {
        Node rSrc = toRawNode(src);
        if (rSrc==null) return null;
        Node rDst = toRawNode(dst);
        if (rDst==null) return null;
        rSrc.createRelationshipTo(rDst,RelationshipType.withName(edgeLabel));
        return new Neo4JEdge(this,rSrc,rDst);
    }

    @Override
    public boolean removeEdge(AbstractNode src, AbstractNode dst) {
        Node rSrc = toRawNode(src);
        if (rSrc==null) return false;
        Node rDst = toRawNode(dst);
        if (rDst==null) return false;
        Relationship rmatch = null;
        for (Relationship r : rSrc.getRelationships(Direction.OUTGOING)) {
            if (r.getEndNode().getId()==rDst.getId()) {
                //Edge found
                rmatch = r;
                break;
            }
        }
        if (rmatch!=null) {
            rmatch.delete();
        }
        return rmatch!=null;
    }

    @Override
    public AbstractNode addVertex(AbstractNode src) {
        Node rSrc = toRawNode(src);
        if (rSrc!=null) {
            //if the obtained node has the same ID, but the label is different
            for (Label l : rSrc.getLabels()) {
                if (!l.name().equals(jointype(src))) {
                    rSrc.delete();
                    rSrc = null;
                }
            }
            //if it already exists, check if the obtained vertex is the same.
            if (rSrc!=null && new Neo4JVertex(rSrc,this,rSrc.getId()).equals(src)) {
                return src;
            }
        }
        //In this case, the node either does not exists or has been deleted previously
        Node newNode = graphDb.createNode(DynamicLabel.label(jointype(src)));
        for (String keys : src.getVertexSchema()) {
            newNode.setProperty(keys,src.getAttribute(keys));
        }
        return new Neo4JVertex(newNode,this,newNode.getId());
    }

    @Override
    public void addVertices(Collection<? extends AbstractNode> nodes) {
        for (AbstractNode n : nodes) addVertex(n);
    }

    @Override
    public AbstractNode generateVertexObject(String type, Set<String> hashingScheme) {
        throw new RuntimeException("Error: this action is not implemented (generateVertexObject)");
    }

    @Override
    public void removeVertex(AbstractNode src) {
        Node n = toRawNode(src);
        if (n!=null) n.delete();
    }

    @Override
    public HashableHashSet<AbstractNode> getVertices() {
        return graphDb.getAllNodes().stream().map(x->(AbstractNode)new Neo4JVertex(x,this)).collect(Collectors.toCollection(HashableHashSet<AbstractNode>::new));
    }

    @Override
    public int size() {
        return Math.toIntExact(graphDb.getAllNodes().stream().count()+graphDb.getAllRelationships().stream().count());
    }

    @Override
    public <T> Optional<T> startTransaction(Supplier<T> op) {
        try (Transaction t = graphDb.beginTx()) {
            T elem = op.get();
            t.success();
            return Optional.of(elem);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return Optional.empty();
        }
    }

    @Override
    public void close() {
        clearIndex();
        graphDb.shutdown();
    }

    @Override
    public boolean prop(AbstractNode left, AbstractNode right) {
        return isEdge(left,right);
    }

    public GraphDatabaseService getRaw() {
        return graphDb;
    }

    public String getVertexID() {
        return ID;
    }

    public Result doGraphQuery(String query) {
        return graphDb.execute(query);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AbstractNode key : getVertices()) {
            sb.append("Vertex: ");
            sb.append(key.toString());
            sb.append("\nNeighs: \t");
            sb.append(getNeighbours(key));
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String arg[]) {
        Neo4JGraph g = new Neo4JGraph("UID", "r", false, null, "1");
        Result s =
                g.doGraphQuery("START n=node(*) MATCH (n)-[r]->(m) RETURN n,r,m;");
        System.out.println(((ExecutionResult)s).dumpToString());
    }

}
