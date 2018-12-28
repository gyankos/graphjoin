/*
 * HashableHashSet.java
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



package it.giacomobergami.utils.datastructures;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Created by vasistas on 13/02/16.
 */
public class HashableHashSet<T extends Comparable> extends LinkedHashSet<T> implements Comparable<HashableHashSet<T>> {

    public HashableHashSet() {
        super();
    }

    public HashableHashSet(Collection<T> elems) {
        super(elems);
    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 3;
        for( T k : this ) {
            result = result * prime + k.hashCode();
        }
        return result;
    }

    @Override
    public int compareTo(HashableHashSet<T> o) {
        if (o==null)
            return 1;
        int leftSize = size();
        int rightSize = o.size();
        if (leftSize==0 && rightSize==0)
            return 0;
        else if (leftSize==0)
            return -1;
        else if (rightSize==0)
            return 1;
        T leftMin = (T) Collections.min(this);
        T rightMin = (T)Collections.min(o);
        int min = (leftMin.compareTo(rightMin));
        if (min!=0)
            return min;
        T leftMax = (T)Collections.max(this);
        T rightMax = (T)Collections.max(o);
        int max = leftMax.compareTo(rightMax);
        if (max!=0)
            return -max;
        return leftSize - rightSize;
    }

}
