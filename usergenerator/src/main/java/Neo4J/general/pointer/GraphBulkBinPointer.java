/*
 * GraphBulkBinPointer.java
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



package Neo4J.general.pointer;

import org.apfloat.Apint;
import java.io.Serializable;

/**
 * This pointer is created when vertices are created in a bulk graph
 */
public class GraphBulkBinPointer implements Pointer, Serializable {
    public  Pointer left,right;
    public  int hash;

   /* public GraphBulkBinPointer(AbstractNode left, AbstractNode right) {
        this(left.id,right.id);
    }*/

    public GraphBulkBinPointer(Pointer left, Pointer right) {
        this.left = left;
        this.right = right;
        //this.hash = degradate().hashCode();
        int result = left.hashCode();
        hash = 31 * result + right.hashCode();
    }

    public GraphBulkBinPointer(long left, long right) {
        this(new GraphIdPointerLong(left),new GraphIdPointerLong(right));
    }

    public GraphBulkBinPointer(Apint left, Apint right) {
        this(new GraphIdPointerApint(left),new GraphIdPointerApint(right));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphBulkBinPointer)) return false;

        GraphBulkBinPointer that = (GraphBulkBinPointer) o;

        if (left != that.left) return false;
        return right == that.right;

    }


    /**
     * Transforms a bianry pointer into an unique-value pointer.
     * Uses the dovetailing function.
     * It has to be used as a key when the merged node has to be stored in a graph
     * @return
     */
    public Pointer degradate() {
        Pointer p = add(left,right);
        return add(right,div2(multiply(p,add(p,new GraphIdPointerLong(1)))));
    }

    @Override
    public PointerGeneral type() {
        return PointerGeneral.PairPointer;
    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
