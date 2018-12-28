/*
 * AComparableSet.java
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



package it.giacomobergami.utils.datastructures.sets;

import java.util.Iterator;

/**
 * Created by vasistas on 16/04/16.
 */
public abstract class AComparableSet<E extends Comparable<E>> implements ISet<E>, Comparable<AComparableSet<E>> {

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 3;
        for( E k : this ) {
            result = result * prime + k.hashCode();
        }
        return result;
    }

    @Override
    public int compareTo(AComparableSet<E> o) {
        if (o==null)
            return 1;
        int leftSize = getSize();
        int rightSize = o.getSize();
        if (leftSize==0 && rightSize==0)
            return 0;
        else if (leftSize==0)
            return -1;
        else if (rightSize==0)
            return 1;
        E leftMin = min(this);
        E rightMin = min(o);
        int min = (leftMin.compareTo(rightMin));
        if (min!=0)
            return min;
        E leftMax = (E)max(this);
        E rightMax = (E)max(o);
        int max = leftMax.compareTo(rightMax);
        if (max!=0)
            return -max;
        return leftSize - rightSize;
    }

    public static <T extends Object & Comparable<? super T>> T min(ISet<? extends T> coll) {
        Iterator<? extends T> i = coll.iterator();
        T candidate = i.next();

        while (i.hasNext()) {
            T next = i.next();
            if (next.compareTo(candidate) < 0)
                candidate = next;
        }
        return candidate;
    }

    public static <T extends Object & Comparable<? super T>> T max(ISet<? extends T> coll) {
        Iterator<? extends T> i = coll.iterator();
        T candidate = i.next();

        while (i.hasNext()) {
            T next = i.next();
            if (next.compareTo(candidate) > 0)
                candidate = next;
        }
        return candidate;
    }

}
