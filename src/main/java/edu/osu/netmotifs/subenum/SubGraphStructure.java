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


import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 6/5/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubGraphStructure {
    public static boolean beFast = false;
    public int[] nodes;

    // ADDED
    public byte[] edges;
    

    public SubGraphStructure(int size) {
        nodes = new int[size];
//        edges = new boolean[size * size];
        edges = new byte[size * size];

    }
    
    /** Changed by Mitra **/
    public SubGraphStructure(byte[] adjacency) {
        if (Math.sqrt(adjacency.length) != (int) Math.sqrt(adjacency.length))
            throw new IllegalArgumentException("Adjacency size must be complete square integer!");

        int size = (int) Math.sqrt(adjacency.length);
        nodes = new int[size];
        for (int i = 0; i < size; i++)
            nodes[i] = i;
        edges = adjacency.clone();
    }

    /** Changed by Mitra **/
    public void setEdgeAt(int v, int w, byte value) {
        edges[v * nodes.length + w] = value;
    }

    /** Changed by Mitra **/
    public byte getEdgeAt(int v, int w) {
        return edges[v * nodes.length + w];
    }

    @Override
    public String toString() {
        return Arrays.toString(nodes) + "\t" + getAdjacencySignature();
    }

    /** Changed by Mitra **/
    public int getOutDegree(int v) {
        int index = SubenumUtil.arrayContains(nodes, v);
        if (index == -1)
            throw new IllegalArgumentException("The vertex is not available!");
        int out_degree = 0;
        for (int i = 0; i < nodes.length; i++)
            if (getEdgeAt(index, i) == 1)
                out_degree++;
        return out_degree;
    }

    /** Changed by Mitra **/
    public int getInDegree(int v) {
        int index = SubenumUtil.arrayContains(nodes, v);
        if (index == -1)
            throw new IllegalArgumentException("The vertex is not available!");
        int in_degree = 0;
        for (int i = 0; i < nodes.length; i++)
            if (getEdgeAt(i, index) == 1)
                in_degree++;
        return in_degree;

    }

    /**
     *  MA: Canonical labeling performs here based on a heuristic ranking method
     *  and associated long value of that label will be computed
     *  
     *  Changed by Mitra to change ranking procedure for colored vertices
     */
    final public SubGraphStructure getOrderedForm() {
        SubGraphStructure result = new SubGraphStructure(nodes.length);
        long[] ranks = new long[nodes.length];

        /** MA: Ranking the vertices based on their in/out degree
         *  Rank of vertex v = ((out-degree * motifSize) + in-degree) * (color + 1)
         *  Sort vertices based on ranked (Ascending order)
         */
        result.nodes = nodes.clone();
        if (beFast)
            for (int v = 0; v < result.nodes.length; v++)
                ranks[v] = getOutDegree(result.nodes[v]);
        else
            for (int v = 0; v < result.nodes.length; v++)
                ranks[v] = (long) ((getOutDegree(result.nodes[v]) * nodes.length + getInDegree(result.nodes[v])) + (Math.pow(10, edges[(v * nodes.length) + v])));

        SubenumUtil.rankedInsertionSort(result.nodes, ranks);
        
        /** MA: Canonical label of given subgraph */
        int[] index = new int[result.nodes.length];
        for (int i = 0; i < index.length; i++)
            index[i] = SubenumUtil.arrayContains(result.nodes, nodes[i]);

        for (int i = 0; i < nodes.length; i++)
            for (int j = 0; j < nodes.length; j++)
            	result.setEdgeAt(index[i], index[j], getEdgeAt(i, j));
        return result;
    }

    
    public ByteArray getAdjacencyArray() {
        return ByteArray.buildFrom(edges);
    }

    /**
     * MA: Changed for return values
     * @return
     */
    public String getAdjacencySignature() {
        return Arrays.toString(edges);
    }
}
