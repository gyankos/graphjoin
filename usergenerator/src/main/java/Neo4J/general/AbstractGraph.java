/*
 * AbstractGraph.java
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


package Neo4J.general;


import it.giacomobergami.utils.BinaryPredicate;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by vasistas on 24/02/16.
 */
public abstract class AbstractGraph<K extends AbstractNode> implements BinaryPredicate<AbstractNode,AbstractNode> {

    public abstract boolean isEdge(K src, K dst);
    public abstract boolean hasVertex(K src);
    public abstract K getVertex(K tmp);
    public abstract Collection<K> getNeighbours(K src);
    public abstract Iterator<GeneralEdge> getEdges();
    public abstract GeneralEdge addEdge(K src, K dst);
    public abstract boolean removeEdge(K src, K dst);
    public abstract K addVertex(K src);
    public abstract void addVertices(Collection<? extends K> nodes);
    public abstract K generateVertexObject(String type, Set<String> hashingScheme);
    public abstract void removeVertex(K src);
    public abstract Set<K> getVertices();

    public abstract int size();

    public abstract <T> Optional<T> startTransaction(Supplier<T> f );



    public abstract void close();

    /**
     * Converting a multilabel node into a single labelled one
     * @param src
     * @return
     */
    public static String jointype(final AbstractNode src){
        return src.getType().stream().collect(new Collector<String, StringBuilder, String>() {
            @Override
            public Supplier<StringBuilder> supplier() {
                return StringBuilder::new;
            }

            @Override
            public BiConsumer<StringBuilder, String> accumulator() {
                return StringBuilder::append;
            }

            @Override
            public BinaryOperator<StringBuilder> combiner() {
                return StringBuilder::append;
            }

            @Override
            public Function<StringBuilder, String> finisher() {
                return StringBuilder::toString;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>();
            }
        });
    }

    /**
     * Converting a multilabel node into a single labelled one
     * @param src
     * @return
     */
    public static String joinschema(final AbstractNode src){
        return src.getVertexSchema().stream().collect(new Collector<String, StringBuilder, String>() {
            @Override
            public Supplier<StringBuilder> supplier() {
                return () -> new StringBuilder();
            }

            @Override
            public BiConsumer<StringBuilder, String> accumulator() {
                return (s, s2) -> s.append(s2);
            }

            @Override
            public BinaryOperator<StringBuilder> combiner() {
                return (s, s2) -> s.append(s2);
            }

            @Override
            public Function<StringBuilder, String> finisher() {
                return x -> x.toString();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>();
            }
        });
    }

    @Override
    public boolean equals(Object o ){
        if (!(o instanceof AbstractGraph))
            return false;
        AbstractGraph right = (AbstractGraph)o;
        Set<K> l = getVertices();
        if (l.isEmpty())
            return right.getVertices().isEmpty();
        for (K v : l) {
            if (!right.hasVertex(v))
                return false;
            else {
                for (K u : getNeighbours(v)) {
                    if (!right.isEdge(v,u))
                        return false;
                }
            }
        }
        return true;
    }

    //public abstract DoubleHashVertexSet<? extends DoubleHashVertexSet,?,K> getRawVertices();


}
