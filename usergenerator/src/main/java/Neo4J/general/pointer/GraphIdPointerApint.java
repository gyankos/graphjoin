/*
 * GraphIdPointerApint.java
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
 * Implements a pointer which is too big to fit in a long element. It could happen on very big graph databases, or when
 *
 */
public class GraphIdPointerApint implements Pointer {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphIdPointerApint)) return false;

        GraphIdPointerApint that = (GraphIdPointerApint) o;

        return apint.equals(that.apint);

    }

    @Override
    public int hashCode() {
        return apint.hashCode();
    }

    public final Apint apint;

    public GraphIdPointerApint(Apint apint) {
        this.apint = apint;
    }

    @Override
    public PointerGeneral type() {
        return PointerGeneral.ApintPointer;
    }
}
