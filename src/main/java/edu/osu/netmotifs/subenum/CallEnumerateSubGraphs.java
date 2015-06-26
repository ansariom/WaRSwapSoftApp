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

import com.google.common.base.Stopwatch;

import edu.osu.netmotifs.warswap.common.CONF;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CallEnumerateSubGraphs {
	
	public CallEnumerateSubGraphs(int motifSize, String inputGraphPath, String outputPath, int noOfThreads) {

		Graph graph = null;
		Stopwatch stopwatch = Stopwatch.createStarted();
		String logForOutput = "";

        try {
        	FileWriter writer = new FileWriter(outputPath);
        	graph = HashGraph.readStructureFromFile(inputGraphPath);
            if (graph.vertexCount() < 20000) 
                graph = MatGraph.readStructureFromFile(inputGraphPath);

            logForOutput += graph.getGraphInfo();
            logForOutput += "Enumeration started at : " + Calendar.getInstance().getTime() + "\n";
            logForOutput += "Graph loaded in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " msecs.\n";
            stopwatch.reset().start();

            SMPEnumerator.setMaxCount(Long.MAX_VALUE);

            System.out.printf("Unique map size: %,d.\n", SMPEnumerator.getUniqueCap());

            System.out.printf("Label map size: %,d.\n", SignatureRepo.getCapacity());

            SMPEnumerator.setVerbose(true);

            logForOutput += "Graph's input file: " + inputGraphPath + " \nSubgraph size:" + motifSize + "\n";
            writer.write(logForOutput);
            writer.flush();
            SMPEnumerator.enumerateNonIsoInParallel(graph, motifSize, noOfThreads, stopwatch, writer);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
