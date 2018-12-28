/*
 * INeighVertex.java
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


import Neo4J.general.pointer.Pointer;
import it.giacomobergami.utils.datastructures.HashableHashSet;
import it.giacomobergami.utils.datastructures.iterators.MapIterator;

import java.util.*;

/**
 *
 */
public abstract class INeighVertex extends AbstractNode {

    public static final boolean strategy = true;
    public INeigh neigh;
    public HashableHashSet<String> type;

    public Iterator<GeneralEdge> getOutgoingEdges() {
        INeighVertex src = this;
        return new MapIterator<INeighVertex,GeneralEdge>(neigh.iterator()) {
            @Override
            public GeneralEdge apply(INeighVertex iNeighVertex) {
                return new GeneralEdge(src,iNeighVertex);
            }
        };
    }

    ///////////////
    public INeighVertex(INeighVertex v, Set<String> hashingScheme, Pointer id, boolean strategyRB) {
        super(hashingScheme,id);
        this.type = new HashableHashSet(v.type);
        //neigh = strategyRB ? new StrategyRB(this) : new StrategyStack(this);
    }

    public INeighVertex(AbstractNode v, Set<String> hashingScheme, Pointer id, boolean strategyRB) {
        super(hashingScheme, id);
        this.type = new HashableHashSet(v.getType());
        //neigh = strategyRB ? new StrategyRB(this) : new StrategyStack(this);
    }

    public INeighVertex(Set<String> hashingScheme, Pointer id, boolean strategyRB) {
        super(hashingScheme, id);
        this.type = new HashableHashSet<>();
        //neigh = strategyRB ? new StrategyRB(this) : new StrategyStack(this);
    }

    public INeighVertex(String type, Set<String> hashingScheme, Pointer id, boolean strategyRB) {
        super(hashingScheme, id);
        this.type = new HashableHashSet<>();
        this.type.add(type);
        //neigh = strategyRB ? new StrategyRB(this) : new StrategyStack(this);
    }

    public INeighVertex(Collection<String> type, Set<String> hashingScheme, Pointer id, boolean strategyRB) {
        super(hashingScheme, id);
        this.type = new HashableHashSet<>();
        this.type.addAll(type);
        //neigh = strategyRB ? new StrategyRB(this) : new StrategyStack(this);
    }
    ///////////////

    public void setTypes(Collection<String> t) {
        type.clear();
        type.addAll(t);
        updateHashes();
    }

    public boolean addNeighbour(INeighVertex av) {
        return neigh.addNeighbour(av);
    }

    @Override
    public boolean removeNeighbour(AbstractNode dst) {
        return neigh.removeNeighbour((INeighVertex)dst);
    }

    @Override
    public boolean hasNeighbour(AbstractNode dst) {
        return neigh.hasNeighbour((INeighVertex)dst);
    }

    @Override
    public HashableHashSet<String> getType() {
        return type;
    }

}
