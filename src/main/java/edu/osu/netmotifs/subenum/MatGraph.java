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
 * This Class stores input graph within a adjMatrix for graphs that have less than 20,000 vertices
 * Modifications made to the original code to support enumeration of colored graphs as follows:
 * 
 * 1- adjArr have to store color codes rather than booleans
 * 2- Some variable names and methods has changed
 */

public class MatGraph implements Graph {
    public List<Adjacency> table = new ArrayList<Adjacency>();
    private int edgeCount = 0;
    private boolean hasSelfLoop = false;

    // ADDED by Mitra
    private HashMap<Integer, Byte> vColorHash = new HashMap<Integer, Byte>();
    public byte[] adjArr;

    /**
     * @author mitra 
     * The readStructure method is modified to read colored graphs 
     * @param reader
     * @return
     * @throws IOException
     */
    public static MatGraph readColoredGraph(Reader reader) throws IOException {
    	BufferedReader br = new BufferedReader(reader);
    	String line;
    	MatGraph graph = new MatGraph();
    	
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
    
    /**
     * MA: changed to read colored graphs
     * @param reader
     * @return
     * @throws IOException
     */
    public static MatGraph readStructure(Reader reader) throws IOException {

    	BufferedReader br = new BufferedReader(reader);
        String line;
        MatGraph graph = new MatGraph();
//        Map<String, Integer> map = new HashMap<String, Integer>();
//        int last_v = 0;

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

    public static MatGraph readFromFile(String path) throws IOException {
        return readColoredGraph(new FileReader(path));
    }

    public static MatGraph readStructureFromFile(String path) throws IOException {
        return readStructure(new FileReader(path));
    }

    public int vertexCount() {
        return vColorHash.size();
    }

    public int edgeCount() {
//        int sum = 0;
//        for (int v : vColorHash.keySet())
//            sum += getOutNeighborArray(v).length;
//        return sum;
    	return edgeCount;
    }

    @Override
    public Set<Integer> getVertices() {
        return vColorHash.keySet();
    }

    private boolean containsVertex(int vertex) {
        return vColorHash.keySet().contains(vertex);
    }

    private void addVertex(int vertex, byte color) {
    	vColorHash.put(vertex, color);
        while (table.size() <= vertex)
            table.add(new Adjacency());
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

    private void addEdge(int source, int dest, byte color1, byte color2) {
    	edgeCount++;
    	if (dest == source && CONF.considerSelfloop()) {
    		addVertex(source, Byte.valueOf("3"));
    		return;
    	}
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

    final public int[] getOutNeighborArray(int vertex) {
        return table.get(vertex).outArr;
    }

    final public int getDegree(int vertex) {
        return table.get(vertex).allArr.length;
    }

    /** Changed by Mitra to handle color of vertices **/
    @Override
    public SubGraphStructure getSubGraph(int[] vertex_set) {
        SubGraphStructure sub_graph = new SubGraphStructure(vertex_set.length);
        System.arraycopy(vertex_set, 0, sub_graph.nodes, 0, vertex_set.length);

        for (int i = 0; i < vertex_set.length; i++) {
            for (int j = 0; j < vertex_set.length; j++) 
            	sub_graph.setEdgeAt(i, j, adjArr[vertex_set[i] * table.size() + vertex_set[j]]);
        }

        return sub_graph;
    }

    /**
     * MA: This method computes decimal value of corresponding adjMtrix to use later as  a code
     * for canonicalLebeling computing
     * 
     * i.e. (in original code) 001 000 110 will be represented by:
     * 0*1 + 0*2 + 1*4 + 0*8 + 0*16 + 0*32 + 1*64 + 1*128 + 0*512 = 196      
     * 
     * This method changed to code color codes in computing decimal value as follows:
     * i.e. (colored matrix) 001 010 112 represents 3 nodes with 3 colors, it 
     *      shoud be coded in two steps:
     * 1- two bits for each bit: convert above matrix to : 00 00 01, 00 01 00, 01 01 10
     * 2- Compute integer value of (1) as before: 1*32 + 1*128 + 1*2^13 + 1*2^15 + 1*2^16 
     */
    @Override
    public long getSubGraphAsLong(int[] vertex_set) {
        if (vertex_set.length > 8)
            throw new IllegalStateException("SubGraph size is larger than 8: " + vertex_set.length);

        byte[] arr = new byte[vertex_set.length * vertex_set.length];
        int k = 0;
        for (int i = 0; i < vertex_set.length; i++) {
            for (int j = 0; j < vertex_set.length; j++)
                arr[k++] = adjArr[vertex_set[i] * table.size() + vertex_set[j]];
        }
        return ByteArray.byteArrayToLong(arr);

    }
    
	@Override
	public String getSubGraphAsString(int[] vertex_set) {
			
		byte[] arr = new byte[vertex_set.length * vertex_set.length];
		String arrayStr = "";
		int k = 0;
		for (int i = 0; i < vertex_set.length; i++) {
			for (int j = 0; j < vertex_set.length; j++) {
				arr[k] = adjArr[vertex_set[i] * table.size() + vertex_set[j]];
				arrayStr += arr[k++];
			}
		}
		return arrayStr;
	}

    public boolean areNeighbor(int v, int w) {
    	if (v == w) return false;
        return (adjArr[table.size() * v + w] == 1 || adjArr[table.size() * w + v] == 1);
    }

    public boolean hasEdge(int v, int w) {
    	if (v == w) return false;
        return (adjArr[table.size() * v + w] == 1);
    }

    final public int getDegreeSum() {
        int sum = 0;
        for (int v : vColorHash.keySet())
            sum += getDegree(v);
        return sum;
    }

    /**
     * MA: This modifies @method update to store color of each vertex in adjMatrix
     * Modification on codes are commented
     * @param srcColor
     * @param targetColor
     */
    final private void update() {
//    	edgeCount = 0;
    	adjArr = new byte[table.size() * table.size()];
//    	adjArr = new boolean[table.size() * table.size()];
    	for (int v : vColorHash.keySet()) {
    		Adjacency adj = table.get(v);
    		
    		adj.outArr = adj.outSet.toArray();
    		if (vertexCount() < 10000)
    			Arrays.sort(adj.outArr);
    		for (int w : adj.outArr)
    			adjArr[v * table.size() + w] = 1;
    		
    		// ADDED by Mitra
    		adjArr[v * table.size() + v] = vColorHash.get(v);
    		
    		adj.outSet = new IntOpenHashSet(adj.outArr.length, 0.5f);
    		adj.outSet.add(adj.outArr);
    		
    		adj.allArr = adj.allSet.toArray();
    		if (vertexCount() < 10000)
    			Arrays.sort(adj.allArr);
    		adj.allSet = new IntOpenHashSet(adj.allArr.length, 0.5f);
    		adj.allSet.add(adj.allArr);
//    		edgeCount += adj.outArr.length;
    	}
    	
    }
    
    public void printInfo() {
        System.out.printf("Total vertices: %,d\n", vertexCount());
        System.out.printf("Total edges: %,d\n", edgeCount);
        double degree_mean = getDegreeSum() / (double) vertexCount();
        System.out.printf("Average degree: %f\n", degree_mean);
        double variance = 0;
        for (int v : vColorHash.keySet())
            variance += (degree_mean - getDegree(v)) * (degree_mean - getDegree(v));
        System.out.printf("STD degree: %f\n", Math.sqrt(variance / vertexCount()));
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

    public class Adjacency {
        IntOpenHashSet outSet = new IntOpenHashSet();
        IntOpenHashSet allSet = new IntOpenHashSet();
        int[] allArr = new int[0];
        int[] outArr = new int[0];
    }



}