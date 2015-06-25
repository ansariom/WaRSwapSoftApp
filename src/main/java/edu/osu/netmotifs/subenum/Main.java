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

import org.apache.commons.cli.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Saeed on 3/9/14.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new BasicParser();
        //System.in.read();

        // create the Options
        Options options = new Options();
        options.addOption("i", "input", true, "the input file name.");
        options.addOption("s", "size", true, "size of subgraphs to enumerate.");
        options.addOption("o", "output", true, "the output file name (default out.txt)");
        options.addOption("um", true, "the max size of unique map.");
        options.addOption("lm", true, "the max size of label map.");
        options.addOption("mc", true, "maximum subgraphs count to stop. Default is infinity i.e. enumerate all.");
        options.addOption("n", "nonisomorphic", false, "enumerate just nonisomorphic subgraphs.");
        options.addOption("c", "count", false, "count all subgraphs");
        options.addOption("f", "fast", false, "fast isomorphism detection.");
        options.addOption("r", "random", false, "do not sort initial states.");
        options.addOption("t", "threads", true, "number of threads to use");
        options.addOption("silent", false, "suppress progress report.");
        HelpFormatter formatter = new HelpFormatter();


        String input_path = "";
        String output_path = "out.txt";
        Graph graph = null;
        Stopwatch stopwatch = Stopwatch.createStarted();
        int size = 3;
        int threads = Runtime.getRuntime().availableProcessors();
        String logForOutput = "";
        FileWriter writer = new FileWriter(output_path);

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("s")) {
                size = Integer.parseInt(line.getOptionValue("s"));
                if (size < 2) {
                    System.out.println("Size of subgraphs must be greater or equal to 2.");
                    System.exit(-1);
                }
            }

            if (line.hasOption("t")) {
                threads = Integer.parseInt(line.getOptionValue("t"));
            }

            if (line.hasOption("f")) {
                SubGraphStructure.beFast = true;
            }

            if (line.hasOption("o")) {
                output_path = line.getOptionValue("o");
            }

            if (!line.hasOption("i")) {
                System.out.println("An input file must be given.");
                formatter.printHelp("subdigger", options);
                System.exit(-1);
            } else {
                input_path = line.getOptionValue("i");

                graph = HashGraph.readStructureFromFile(input_path);
                if (graph.vertexCount() < 20000) {
                    System.out.printf("Graph is small. Using adjacency matrix.\n");
                    graph = MatGraph.readStructureFromFile(input_path);
                }


                logForOutput += graph.getGraphInfo();
                logForOutput += "Enumeration started at : " + Calendar.getInstance().getTime() + "\n";
                logForOutput += "Graph loaded in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " msecs.\n";
//                graph.printInfo();
                stopwatch.reset().start();

                if (line.hasOption("mc")) {
                    SMPEnumerator.setMaxCount(Long.parseLong(line.getOptionValue("mc")));
                    System.out.printf("Subgraph count is bounded to %,d.\n", SMPEnumerator.getMaxCount());
                } else
                    SMPEnumerator.setMaxCount(Long.MAX_VALUE);


                if (line.hasOption("um")) {
                    SMPEnumerator.setUniqueCap(Integer.parseInt(line.getOptionValue("um")));
                }
                System.out.printf("Unique map size: %,d.\n", SMPEnumerator.getUniqueCap());


                if (line.hasOption("lm")) {
                    SignatureRepo.setCapacity(Integer.parseInt(line.getOptionValue("lm")));
                }
                System.out.printf("Label map size: %,d.\n", SignatureRepo.getCapacity());

                if (line.hasOption("r")) {
                    System.out.println("Using random initial states!");
                    SMPEnumerator.randomStates = true;
                }


                if (line.hasOption("silent"))
                    SMPEnumerator.setVerbose(false);
                else
                    SMPEnumerator.setVerbose(true);

                logForOutput += "Graph's input file: " + input_path + " \nSubgraph size:" + size + "\n";
                writer.write(logForOutput);
                writer.flush();
                SMPEnumerator.enumerateNonIsoInParallel(graph, size, threads, stopwatch, writer);
            }


        } catch (org.apache.commons.cli.ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            formatter.printHelp("subdigger", options);
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
