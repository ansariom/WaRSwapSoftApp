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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import com.carrotsearch.hppc.LongLongOpenHashMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.osu.netmotifs.warswap.common.ListReverseIndexComparatorLong;
import edu.osu.netmotifs.warswap.common.Utils;

/**
 * Created by Saeed on 3/8/14.
 */
public class SignatureRepo {
    static int capacity = 40 * 1000 * 1000;
    static int motifSize = 3;
    //HashMultiset<BoolArray> labelMap = HashMultiset.create();
    FreqMap labelMap = new FreqMap();
    LongLongOpenHashMap longLabelMap = new LongLongOpenHashMap();
    FileWriter writer;
    private boolean verbose = true;
    private ReentrantLock lock = new ReentrantLock();
    private String outStream = "";

    public SignatureRepo(FileWriter writer) throws IOException {
        this.writer = writer;
    }

    public static int getCapacity() {
        return capacity;
    }

    public static void setCapacity(int capacity) {
        SignatureRepo.capacity = capacity;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

//    public void add(Multiset<BoolArray> multiset) {
//    	lock.lock();
//    	for (HashMultiset.Entry<BoolArray> entry : multiset.entrySet()) {
//    		BoolArray label = new SubGraphStructure(entry.getElement().getArray()).getOrderedForm().getAdjacencyArray();
//    		labelMap.add(label, entry.getCount());
//    	}
//    	
//    	if (isVerbose()) {
//    		System.out.printf("Added %,d new signatures. LabelMap size:%,d\n", multiset.elementSet().size(), size());
//    	}
//    	if (size() > capacity)
//    		try {
//    			flush();
//    		} catch (IOException exp) {
//    			exp.printStackTrace();
//    			System.exit(-1);
//    		}
//    	lock.unlock();
//    }
    
    /**
     * MA: Changed to byteArrays
     * @param multiset
     */
    public void add(Multiset<ByteArray> multiset) {
        lock.lock();
        for (HashMultiset.Entry<ByteArray> entry : multiset.entrySet()) {
            ByteArray label = new SubGraphStructure(entry.getElement().getArray()).getOrderedForm().getAdjacencyArray();
            labelMap.add(label, entry.getCount());
        }

        if (isVerbose()) {
            System.out.printf("Added %,d new signatures. LabelMap size:%,d\n", multiset.elementSet().size(), size());
        }
        if (size() > capacity)
            try {
                flush();
            } catch (IOException exp) {
                exp.printStackTrace();
                System.exit(-1);
            }
        lock.unlock();
    }

    /**
     * MA: Changed by Mitra to support colored graphs with color coded matrices
     * @param longMap
     * @param k
     */
    public void add(LongLongOpenHashMap longMap, int k) {
        lock.lock();
        for (int i = 0; i < longMap.keys.length; i++) {
            if (longMap.allocated[i]) {
                long key = longMap.keys[i];
                key = ByteArray.byteArrayToLong(new SubGraphStructure(ByteArray.longToByteArray(key, k * k * 2)).getOrderedForm().getAdjacencyArray().getArray());
                longLabelMap.putOrAdd(key, longMap.values[i], longMap.values[i]);
            }
        }

        if (isVerbose()) {
            System.out.printf("Added %,d new signatures. LabelMap size:%,d\n", longMap.size(), size());
        }
        if (size() > capacity)
            try {
                flush();
            } catch (IOException exp) {
                exp.printStackTrace();
                System.exit(-1);
            }
        lock.unlock();
    }


    public int size() {
        return labelMap.size() + longLabelMap.size();
    }
    
    public void writeToLog(String log) {
    	outStream += log;
	}

    /**
     * @Modified by Mitra Ansariola
     * @author Saeed
     * 
     * Modify the following code:
     * 1- Change the name of method to : writeToOutputFile
     * 2- Use adj matrix representation of each subgraph instead of long value
     * 
     * @throws IOException
     */
    public void flush() throws IOException {
        lock.lock();
        ArrayList<Long> valuesList = new ArrayList<Long>();
        ArrayList<Long> keysList = new ArrayList<Long>();
        
        for (int i = 0; i < longLabelMap.keys.length; i++)
            if (longLabelMap.allocated[i]) {
            	long subg = longLabelMap.keys[i];
            	long count = longLabelMap.values[i];
            	valuesList.add(count);
            	keysList.add(subg);
            }
        ListReverseIndexComparatorLong comparator = new ListReverseIndexComparatorLong(valuesList);
		Integer[] valuesIdxList = comparator.createIndexArray();
		Arrays.sort(valuesIdxList, comparator);

		writer.write(outStream);
		writer.write("======================================================================\n\nResults..\n");
		writer.write("subgraph Number, Adj Matrix, Frequency\n\n");
		for (int i = 0; i < valuesIdxList.length; i++) {
			writer.write(Utils.getAdjMtxOfSubgraph(keysList.get(valuesIdxList[i]), motifSize, valuesList.get(valuesIdxList[i]), i+1));
			
		}
		
		labelMap.clear();
		longLabelMap.clear();
		writer.flush();
		lock.unlock();

    }

    public void close() throws IOException {
        flush();
        writer.close();
    }
}
