/*
 * Collector.java
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



package it.giacomobergami.utils.datastructures.iterators;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Collects the elements of an iterator inside
 */
public class Collector<E> {

    private Iterator<E> toit;
    private Supplier<Collection<E>> supp;

    public Collector(Iterator<E> toit, Supplier<Collection<E>> supp) {
        this.toit = toit;
        this.supp = supp;
    }

    public Collection<E> get() {
        Collection<E> toret = supp.get();
        toit.forEachRemaining(toret::add);
        return toret;
    }


}
