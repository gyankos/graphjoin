/*
 * UniqueGenerator.java
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

/**
 * Generates incrementally new ids for the vertices
 */
public class UniqueGenerator {

    private long lastIntGenerated;
    private Apint apint = new Apint(Long.MAX_VALUE);

    public UniqueGenerator() {
        this.lastIntGenerated = 0;
    }

    public Pointer next() {
        if (lastIntGenerated<Long.MAX_VALUE) {
            return new GraphIdPointerLong(lastIntGenerated++);
        } else {
            Apint toret = apint;
            apint = apint.add(Apint.ONE);
            return new GraphIdPointerApint(toret);
        }
    }

}
