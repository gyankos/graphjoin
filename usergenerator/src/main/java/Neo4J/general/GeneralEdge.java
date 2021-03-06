/*
 * GeneralEdge.java
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

/**
 * Defines an edge that could be extended for specific graph implementations
 */
public class GeneralEdge {
    public AbstractNode src, dst;

    public GeneralEdge(AbstractNode src, AbstractNode dst) {
        this.src = src;
        this.dst = dst;
    }

    public AbstractNode getSrc() {
        return src;
    }

    public AbstractNode getDst() {
        return dst;
    }

    @Override
    public String toString() {
        return src.toString()+"-->"+dst.toString();
    }

}
