/**
 * The MIT License (MIT)

Copyright (c) 2014 Saeed Shahrivari

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

@modified by Mitra Ansariola

Original code is created by Saeed Shahrivari 
@cite : 1. Shahrivari S, Jalili S. Fast Parallel All-Subgraph Enumeration Using Multicore Machines. Scientific Programming. 2015 Jun 16;2015:e901321. 
@code available at : https://github.com/shahrivari/subenum
 */
package edu.osu.netmotifs.subenum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saeed on 4/17/14.
 */
final public class FreqMap {
    public HashMap<ByteArray, Count> map = new HashMap<ByteArray, Count>();

    public void add(ByteArray arr, int occurrences) {
        Count freq = map.get(arr);
        if (freq == null)
            map.put(arr, new Count(occurrences));
        else
            freq.getAndAdd(occurrences);
    }

    public void add(ByteArray arr, long occurrences) {
        Count freq = map.get(arr);
        if (freq == null)
            map.put(arr, new Count(occurrences));
        else
            freq.getAndAdd(occurrences);
    }

    public int size() {
        return map.size();
    }

    public long totalFreq() {
        long sum = 0;
        for (Map.Entry<ByteArray, Count> e : map.entrySet())
            sum += e.getValue().get();
        return sum;
    }

    public void clear() {
        map.clear();
    }

    final public class Count {
        long value;

        Count(int value) {
            this.value = value;
        }

        Count(long value) {
            this.value = value;
        }

        public long get() {
            return value;
        }

        public long getAndAdd(int delta) {
            long result = value;
            value = result + delta;
            return result;
        }

        public long getAndAdd(long delta) {
            long result = value;
            value = result + delta;
            return result;
        }


        @Override
        public int hashCode() {
            return (int) (value ^ (value >>> 32));
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Count && ((Count) obj).value == value;
        }

        @Override
        public String toString() {
            return Long.toString(value);
        }

    }

}
