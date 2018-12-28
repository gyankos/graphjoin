/*
 * GraphIdPointerLong.java
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

/**
 * Defines a basic pointer. That is, when the graph is created, this pointer defines the ordered position inside the graph
 */
public class GraphIdPointerLong implements Pointer{
    public final Long pointer;

    public GraphIdPointerLong(long pointer) {
        this.pointer = pointer;
    }

    public static Pointer anyPointer() {return new GraphIdPointerLong(-1); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphIdPointerLong)) return false;

        GraphIdPointerLong that = (GraphIdPointerLong) o;

        return pointer == that.pointer;


    }

    /***
     * TODO: Negative integer pointer are not mapped into Integers
     * @return
     */
    @Override
    public int hashCode() {
        return pointer.intValue();
    }

    @Override
    public PointerGeneral type() {
        return PointerGeneral.LongPointer;
    }

    @Override
    public String toString() { return String.valueOf(pointer); }


}
