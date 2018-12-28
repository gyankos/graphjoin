/*
 * HashableTreeMap.java
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


import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by vasistas on 13/02/16.
 */
public class HashableTreeMap<K extends Comparable<K>,T extends Comparable<T>> extends TreeMap<K,T> implements Comparable<HashableTreeMap<K,T>> {

    private Comparator<K> kComparator;
    private Comparator<T> vComparator;

    public HashableTreeMap() {
        this((o1, o2) -> o1.compareTo(o2), (o1, o2) -> o1.compareTo(o2));
    }

    public HashableTreeMap(Comparator<K> kComparator, Comparator<T> vComparator) {
        super();
        this.kComparator = kComparator;
        this.vComparator = vComparator;
    }

    public HashableTreeMap(HashableTreeMap<K, T> attributes, Comparator<K> kComparator, Comparator<T> vComparator) {
        super();
        this.kComparator = kComparator;
        this.vComparator = vComparator;
        putAll(attributes);
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        ArrayList<K> elems = new ArrayList<>(keySet());
        Collections.<K>sort(elems,kComparator);
        for( K k : elems ) {
            result = result * prime + (k.hashCode() ^ get(k).hashCode());
        }
        return result;
    }

    @Override
    public int compareTo(HashableTreeMap<K,T> o) {
        int leftSize = keySet().size();
        int rightSize = o.keySet().size();
        if (leftSize==0 && rightSize==0)
            return 0;
        else if (leftSize==0)
            return -1;
        else if (rightSize==0)
            return 1;
        K leftMin = Collections.min(keySet(),kComparator);
        K rightMin = Collections.min(o.keySet(),kComparator);
        int min = (kComparator.compare(leftMin,rightMin));
        if (min!=0)
            return min;
        K leftMax = Collections.max(keySet(),kComparator);
        K rightMax = Collections.max(o.keySet(),kComparator);
        int max = kComparator.compare(leftMax,rightMax);
        if (max!=0)
            return -max;
        int setsize = leftSize - rightSize;
        if (setsize!=0)
            return setsize;
        SortedSet<K> keys = new TreeSet<>();
        keys.addAll(keySet());
        keys.addAll(o.keySet());
        for (K key : keys) {
            T l = get(key);
            T r = o.get(key);
            int cmp = Objects.compare(l,r,vComparator);
            if (cmp!=0)
                return cmp;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HashableTreeMap))
            return false;
        else return compareTo((HashableTreeMap<K,T>)o)==0;
    }

    public  static <K extends Comparable<K>,T extends Comparable<T>> Collector<Map.Entry<K,T>,HashableTreeMap<K,T>,HashableTreeMap<K,T>> collector() {
        return new Collector<Map.Entry<K, T>, HashableTreeMap<K, T>, HashableTreeMap<K, T>>() {
            @Override
            public Supplier<HashableTreeMap<K, T>> supplier() {
                return HashableTreeMap::new;
            }

            @Override
            public BiConsumer<HashableTreeMap<K, T>, Map.Entry<K, T>> accumulator() {
                return (ktHashableHashMap, ktEntry) -> ktHashableHashMap.put(ktEntry.getKey(),ktEntry.getValue());
            }

            @Override
            public BinaryOperator<HashableTreeMap<K, T>> combiner() {
                return (ktHashableHashMap, ktHashableHashMap2) -> {
                    ktHashableHashMap.putAll(ktHashableHashMap2);
                    return ktHashableHashMap;
                };
            }

            @Override
            public Function<HashableTreeMap<K, T>, HashableTreeMap<K, T>> finisher() {
                return x -> x;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>();
            }
        };
    }

    public  static <K extends Comparable<K>,T extends Comparable<T>> Collector<Map.Entry<K,T>,HashableTreeMap<String,String>,HashableTreeMap<String,String>> stringCollector() {
        return new Collector<Map.Entry<K, T>, HashableTreeMap<String, String>, HashableTreeMap<String, String>>() {
            @Override
            public Supplier<HashableTreeMap<String, String>> supplier() {
                return HashableTreeMap::new;
            }

            @Override
            public BiConsumer<HashableTreeMap<String, String>, Map.Entry<K, T>> accumulator() {
                return (ktHashableHashMap, ktEntry) -> ktHashableHashMap.put(ktEntry.getKey().toString(),ktEntry.getValue().toString());
            }

            @Override
            public BinaryOperator<HashableTreeMap<String, String>> combiner() {
                return (ktHashableHashMap, ktHashableHashMap2) -> {
                    ktHashableHashMap.putAll(ktHashableHashMap2);
                    return ktHashableHashMap;
                };
            }

            @Override
            public Function<HashableTreeMap<String, String>, HashableTreeMap<String, String>> finisher() {
                return x -> x;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>();
            }
        };
    }


}
