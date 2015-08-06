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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.google.common.primitives.Ints;

import edu.osu.netmotifs.warswap.common.CONF;

/**
 * @modified and commented by Mitra Ansariola
 * 
 * This Class stores input graph within Hashsets for large graphs (more than 20,000 vertices)
 * Modifications made to the original code to support enumeration of colored graphs as follows:
 * 
 * 1- Colors of vertices should be stored
 */
public class HashGraph implements Graph {

    public List<Adjacency> table = new ArrayList<Adjacency>();
//    public Set<Integer> vertices = new HashSet<Integer>();
    private int edgeCount = 0;
    private boolean hasSelfLoop = false;
    
 // ADDED by Mitra
    private HashMap<Integer, Byte> vColorHash = new HashMap<Integer, Byte>();
    
    public static HashGraph readGraph(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        HashGraph graph = new HashGraph();

        while ((line = br.readLine()) != null) {
            if (line.isEmpty())
                continue;
            if (line.startsWith("#")) {
                System.out.printf("Skipped a line: [%s]\n", line);
                continue;
            }
            String[] tokens = line.split("\\s+");
            if (tokens.length < 2) {
                System.out.printf("Skipped a line: [%s]\n", line);
                continue;
                //throw new IOException("The input file is malformed!");
            }
            int src = Integer.parseInt(tokens[0]);
            int dest = Integer.parseInt(tokens[1]);
            byte color1 = Byte.parseByte(tokens[2]);
    		byte color2 = Byte.parseByte(tokens[3]);
    		graph.addEdge(src, dest, color1, color2);
        }
        br.close();
        graph.update();
        return graph;
    }

    public static HashGraph readStructure(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        HashGraph graph = new HashGraph();

        while ((line = br.readLine()) != null) {
            if (line.isEmpty())
                continue;
            if (line.startsWith("#")) {
                System.out.printf("Skipped a line: [%s]\n", line);
                continue;
            }
            String[] tokens = line.split("\\s+");
            if (tokens.length < 2) {
                System.out.printf("Skipped a line: [%s]\n", line);
                continue;
            }
            int src = Integer.parseInt(tokens[0]);
            int dest = Integer.parseInt(tokens[1]);
    		byte color1 = Byte.parseByte(tokens[2]);
    		byte color2 = Byte.parseByte(tokens[3]);
    		graph.addEdge(src, dest, color1, color2);
        }
        br.close();
        graph.update();
        return graph;
    }

    public static HashGraph readFromFile(String path) throws IOException {
        return readGraph(new FileReader(path));
    }

    public static HashGraph readStructureFromFile(String path) throws IOException {
        return readStructure(new FileReader(path));
    }

    public Set<Integer> getVertices() {
        return vColorHash.keySet();
    }

    public int vertexCount() {
        return vColorHash.size();
    }

    public int edgeCount() {
//        int sum = 0;
//        for (int v : vColorHash.keySet())
//            sum += getNeighbors(v).length;
//        return sum;
    	return edgeCount;
    }

    private boolean containsVertex(int vertex) {
        return vColorHash.keySet().contains(vertex);
    }

    private void addVertex(int vertex, byte color) {
        vColorHash.put(vertex, color);
        while (table.size() <= vertex)
            table.add(new Adjacency());
    }

    /**
     * Changed to store color of vertices
     * @param source
     * @param dest
     * @param color1
     * @param color2
     */
    private void addEdge(int source, int dest, byte color1, byte color2) {
    	// ADDED by Mitra : not allowed to set self-loop edges in this way
    	// Instead we  assign different colors to self loops
    	edgeCount++;
    	if (source == dest && CONF.considerSelfloop()) {
    		addVertex(source, Byte.valueOf("3"));
    		return;
    	}
    	
    	// Changed : store to vColorHash instead of vertices Hashset
        if (!containsVertex(source))
            addVertex(source, color1);
        if (!containsVertex(dest))
            addVertex(dest, color2);
        table.get(source).outSet.add(dest);
        table.get(source).allSet.add(dest);
        table.get(dest).allSet.add(source);
    }

    final public int[] getNeighbors(int vertex) {
        return table.get(vertex).allArr;
    }

    final public int getDegree(int vertex) {
        return table.get(vertex).allArr.length;
    }

    public boolean areNeighbor(int v, int w) {
    	if (v == w) return false;
        return table.get(v).allSet.contains(w);
    }

    public boolean hasEdge(int v, int w) {
    	if (v == w) return false;
        return table.get(v).outSet.contains(w);//Util.arrayContains(table.get(v).outArr,w)>=0;
    }
    
    public byte getEdge(int v, int w) {
    	if (v == w)
    		return vColorHash.get(v);
    	if (table.get(v).outSet.contains(w))
    		return 1;
    	return 0;
    }

    final public int getDegreeSum() {
        int sum = 0;
        for (int v : vColorHash.keySet())
            sum += getDegree(v);
        return sum;
    }

    final private void update() {
//        edgeCount = 0;
        for (int v : vColorHash.keySet()) {
            Adjacency adj = table.get(v);

            adj.outArr = adj.outSet.toArray();
            Arrays.sort(adj.outArr);
            adj.outSet = new IntOpenHashSet(adj.outArr.length, 0.5f);
            adj.outSet.add(adj.outArr);


            adj.allArr = adj.allSet.toArray();
            Arrays.sort(adj.allArr);
            adj.allSet = new IntOpenHashSet(adj.allArr.length, 0.5f);
            adj.allSet.add(adj.allArr);

//            edgeCount += adj.outArr.length;
        }

    }

    public void printInfo() {
    	System.out.printf("Number of vertices: %,d\n", vertexCount());
    	System.out.printf("Number of edges: %,d\n", edgeCount);
    	double degree_mean = getDegreeSum() / (double) vertexCount();
    	System.out.printf("Average degree: %f\n", degree_mean);
    	double variance = 0;
    	for (int v : vColorHash.keySet())
    		variance += (degree_mean - getDegree(v)) * (degree_mean - getDegree(v));
    	System.out.printf("STD degree: %f\n", Math.sqrt(variance / vertexCount()));
    	
    }

    public String getGraphInfo() {
    	String infoStr = "";
        infoStr += "Number of vertices: " + vertexCount() +"\n";
        infoStr += "Number of edges: " + edgeCount() + "\n";
        double degree_mean = getDegreeSum() / (double) vertexCount();
        infoStr += "Average degree: " + degree_mean + "\n";
        double variance = 0;
        for (int v : vColorHash.keySet())
            variance += (degree_mean - getDegree(v)) * (degree_mean - getDegree(v));
        infoStr += "STD degree: " + Math.sqrt(variance / vertexCount()) + "\n\n";
        return infoStr;
    }

    public void printToFile(String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        int[] vs = Ints.toArray(vColorHash.keySet());
        Arrays.sort(vs);
        for (int v : vs) {
            Adjacency adj = table.get(v);
            writer.write(v + "\t" + Arrays.toString(adj.allArr) + "\n");
        }
        writer.close();
    }

    final public SubGraphStructure getSubGraph(int[] vertex_set) {
        SubGraphStructure sub_graph = new SubGraphStructure(vertex_set.length);
        System.arraycopy(vertex_set, 0, sub_graph.nodes, 0, vertex_set.length);

        for (int i = 0; i < vertex_set.length; i++) {
            for (int j = 0; j < vertex_set.length; j++)
            	if (i == j) {
            		sub_graph.setEdgeAt(i, i, vColorHash.get(i));
            		continue;
            	}
            	else if (hasEdge(vertex_set[i], vertex_set[j]))
                    sub_graph.setEdgeAt(i, j, Byte.valueOf("1"));
        }
        return sub_graph;
    }

    final public long getSubGraphAsLong(int[] vertex_set) {
        if (vertex_set.length > 8)
            throw new IllegalStateException("SubGraph size is larger than 8: " + vertex_set.length);
        
        byte[] arr = new byte[vertex_set.length * vertex_set.length];
        int k = 0;
        for (int i = 0; i < vertex_set.length; i++) {
            for (int j = 0; j < vertex_set.length; j++) 
            	arr[k++] = getEdge(vertex_set[i], vertex_set[j]);
        }
        return ByteArray.byteArrayToLong(arr);
    }

    public class Adjacency {
        IntOpenHashSet outSet = new IntOpenHashSet();
        IntOpenHashSet allSet = new IntOpenHashSet();
        int[] allArr = new int[0];
        int[] outArr = new int[0];
    }

	@Override
	public String getSubGraphAsString(int[] vertex_set) {
		byte[] arr = new byte[vertex_set.length * vertex_set.length];
		String arrayStr = "";
		int k = 0;
		for (int i = 0; i < vertex_set.length; i++) {
			for (int j = 0; j < vertex_set.length; j++) {
				arr[k] = getEdge(vertex_set[i], vertex_set[j]);
				arrayStr += arr[k++];
			}
		}
		return arrayStr;
	}

    
}