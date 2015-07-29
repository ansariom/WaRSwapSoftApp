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

import java.util.Stack;

import com.carrotsearch.hppc.LongLongOpenHashMap;

/**
 * Created by Saeed on 6/20/14.
 */
public class SubgraphEnumerator {

    public static FreqMap enumerateState(final Graph graph, SMPState init_state, int k) {
        FreqMap freqmap = new FreqMap();
        Stack<SMPState> stack = new Stack<SMPState>();
        stack.push(init_state);
        int[] foundSubGraph = new int[k];

        while (stack.size() > 0) {
            SMPState state = stack.pop();
            if (state.subgraph.length >= k)
                throw new IllegalStateException("This must never HAPPEN!!!");

            while (!state.extension.isEmpty()) {
                int w = state.extension.get(state.extension.size() - 1);
                state.extension.remove(state.extension.size() - 1);
                if (state.subgraph.length == k - 1) {
                    System.arraycopy(state.subgraph, 0, foundSubGraph, 0, k - 1);
                    foundSubGraph[k - 1] = w;//state.extension[i];
                    SubGraphStructure sub = graph.getSubGraph(foundSubGraph);
                    freqmap.add(sub.getAdjacencyArray(), 1);
                } else {
                    SMPState new_state = state.expand(w, graph);
                    if (new_state.extension.size() > 0)
                        stack.add(new_state);
                }
            }
        }
        return freqmap;
    }

    public static LongLongOpenHashMap enumerateStateHPPC(final Graph graph, SMPState init_state, int k) {
        if (k > 8)
            throw new IllegalArgumentException("k must be smaller or equal to 8.");

        LongLongOpenHashMap freqmap = new LongLongOpenHashMap(1024, 0.5f);
        Stack<SMPState> stack = new Stack<SMPState>();
        stack.push(init_state);
        int[] foundSubGraph = new int[k];

        while (stack.size() > 0) {
            SMPState state = stack.pop();
            if (state.subgraph.length >= k)
                throw new IllegalStateException("This must never HAPPEN!!!");

            while (!state.extension.isEmpty()) {
                int w = state.extension.get(state.extension.size() - 1);
                state.extension.remove(state.extension.size() - 1);
                if (state.subgraph.length == k - 1) {
                    System.arraycopy(state.subgraph, 0, foundSubGraph, 0, k - 1);
                    foundSubGraph[k - 1] = w;//state.extension[i];
                    long subl = graph.getSubGraphAsLong(foundSubGraph);
                    freqmap.putOrAdd(subl, 1, 1);
                } else {
                    SMPState new_state = state.expand(w, graph);
                    if (new_state.extension.size() > 0)
                        stack.add(new_state);
                }
            }
        }
        return freqmap;
    }

}
