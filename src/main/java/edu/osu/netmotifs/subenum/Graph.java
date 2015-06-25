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

import java.io.IOException;
import java.util.Set;

/**
 * Created by Saeed on 4/25/14.
 */
public interface Graph {
    boolean areNeighbor(int v, int w);

    public int[] getNeighbors(int v);

    public void printInfo();
    
    public String getGraphInfo();

    public int vertexCount();

    public int edgeCount();

    public Set<Integer> getVertices();

    public int getDegree(int vertex);

    public SubGraphStructure getSubGraph(int[] vertex_set);

    public long getSubGraphAsLong(int[] vertex_set);

    public boolean hasEdge(int v, int w);

    public void printToFile(String path) throws IOException;
}
