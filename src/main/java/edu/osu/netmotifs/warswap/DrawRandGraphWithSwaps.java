/**
Copyright (c) 2015 Oregon State University
All Rights Reserved.

AUTHOR
  Mitra Ansariola
  
  Department of Botany and Plant Pathology 
  2082 Cordley Hall
  Oregon State University
  Corvallis, OR 97331-2902
  
  E-mail:  megrawm@science.oregonstate.edu 
  http://bpp.oregonstate.edu/

====================================================================

Permission to use, copy, modify, and distribute this software and its
documentation for educational, research and non-profit purposes, without fee,
and without a written agreement is hereby granted, provided that the above
copyright notice, this paragraph and the following three paragraphs appear in
all copies. 

Permission to incorporate this software into commercial products may be obtained
by contacting Oregon State University Office of Technology Transfer.

This software program and documentation are copyrighted by Oregon State
University. The software program and documentation are supplied "as is", without
any accompanying services from Oregon State University. OSU does not warrant
that the operation of the program will be uninterrupted or error-free. The
end-user understands that the program was developed for research purposes and is
advised not to rely exclusively on the program for any reason. 

IN NO EVENT SHALL OREGON STATE UNIVERSITY BE LIABLE TO ANY PARTY FOR DIRECT,
INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF OREGON
STATE UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. OREGON STATE
UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
AND ANY STATUTORY WARRANTY OF NON-INFRINGEMENT. THE SOFTWARE PROVIDED HEREUNDER
IS ON AN "AS IS" BASIS, AND OREGON STATE UNIVERSITY HAS NO OBLIGATIONS TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. 
 */

package edu.osu.netmotifs.warswap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.osu.netmotifs.warswap.common.DivisionByZeroException;
import edu.osu.netmotifs.warswap.common.Edge;
import edu.osu.netmotifs.warswap.common.GenerateRandom;
import edu.osu.netmotifs.warswap.common.ListReverseIndexComparatorInt;
import edu.osu.netmotifs.warswap.common.Utils;
import edu.osu.netmotifs.warswap.common.Vertex;


/** Converted code from R
 * This class is using jgraphT library for keep track of graphs generated 
 * Also is using Derby DB (memory based) as a database to load the input graph 
 * the loading graph into is much faster than parsing the input file line-by-line
 * Note: I used nio package for improving the reading IO speed but it didn't change 
 * the running time compare to standard io package.
 * 
 */
public class DrawRandGraphWithSwaps {

	// private static logger logger = Loadlogger.rlogger;

	private double factor = 6.0;
	private List<String> outBuffer = new ArrayList<String>();
	private String edgeFileOut;
	private String vertexFileIn;
	private int seed;
	private Logger logger;
	private GraphDAO graphDAO;
	private String tableName;
	private byte color1, color2;
	private List<String> addedEdgesList = new ArrayList<String>();
	private int swapCount = 0;
	HashMap<Integer, List<Integer>> graphSrcHash = new HashMap<Integer, List<Integer>>();
	HashMap<Integer, List<Integer>> graphTgtHash = new HashMap<Integer, List<Integer>>();
	HashMap<Integer, Vertex> srcVHash = new HashMap<Integer, Vertex>(); // key=index, value=vertex
	HashMap<Integer, Vertex> tgtVHash = new HashMap<Integer, Vertex>();
	HashMap<Integer, Integer> srcIndexHash = new HashMap<Integer, Integer>(); // key=label, value=index
	HashMap<Integer, Integer> tgtIndexHash = new HashMap<Integer, Integer>();



	public DrawRandGraphWithSwaps(Logger logger, String vertexFileIn,
			String edgeFileOut, String tableName) {
		swapCount = 0;
		this.tableName = tableName;
		seed = Math.abs(new Random().nextInt());
		this.logger = logger;
		this.graphDAO = GraphDAO.getInstance();
		this.edgeFileOut = edgeFileOut;
		this.vertexFileIn = vertexFileIn;
	}

	public int loadGraph(String fnmInFile) {
		int insertedRecords = 0;
		try {
			graphDAO.bulkImport(fnmInFile, tableName);
			insertedRecords = graphDAO.getAllCounts(tableName);
			logger.debug(insertedRecords + " Edges Read From input File");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return insertedRecords;
	}

	/**
	 * Create graph gLayer, containing only vertices of the two colors and only
	 * edges directed from color1 to color2 vertices
	 * 
	 * @param col1
	 * @param col2
	 * @throws Exception 
	 */
	public void sortedLayerDrawWithSwaps(byte col1, byte col2)
			throws Exception {
//		srcsTargetingSomething.clear();
		graphSrcHash.clear();
		graphTgtHash.clear();
		srcIndexHash.clear();
		srcVHash.clear();
		tgtVHash.clear();
		tgtIndexHash.clear();
		addedEdgesList.clear();
		color1 = col1;
		color2 = col2;

		long tx = System.currentTimeMillis();
//		System.out.println(" " + col1 + "  " + col2 );
//		DirectedGraph<Vertex, Edge> newGraph = new DefaultDirectedGraph<Vertex, Edge>(
//				new EdFactory());
		
		graphDAO.selectGraphsLayer(srcVHash, tgtVHash, col1, col2,
				srcIndexHash, tgtIndexHash, tableName);
		
		if (srcVHash.size() == 0)
			return;
		
		List<Integer> srcVDegList = new ArrayList<Integer>();
		List<Integer> tgtVDegList = new ArrayList<Integer>();
		for (int i = 0; i < srcVHash.size(); i++) {
			srcVDegList.add(0);
		}
		for (int i = 0; i < tgtVHash.size(); i++) {
			tgtVDegList.add(0);
		}

		List<Integer> currentTgtCapList = new ArrayList<Integer>(
				tgtIndexHash.size());

		Iterator<Integer> srcKeyItr = srcVHash.keySet().iterator();
		double m = 0.0;
		while (srcKeyItr.hasNext()) {
			Integer index = (Integer) srcKeyItr.next();
			Vertex v = srcVHash.get(index);
			int outdeg = v.getOutDegree();
			m += outdeg;
			srcVDegList.set(index, outdeg);
		}
		
		Iterator<Integer> tgtKeyItr = tgtVHash.keySet().iterator();
		while (tgtKeyItr.hasNext()) {
			Integer index = (Integer) tgtKeyItr.next();
			Vertex v = tgtVHash.get(index);
			tgtVDegList.set(index, v.getInDegree());
		}
		

		/** Factor related Calculations */
		calculateFactor(tgtVDegList, srcVDegList, m);
		double divFac = factor * m;
		if (divFac == 0) {
			throw new DivisionByZeroException();
		}

		List<Integer> tgtDegNewList = new ArrayList<Integer>();
		if (tgtVDegList.size() == 1)
			tgtDegNewList.add(tgtVDegList.get(0));
		else {
			// Draw a random sampling from targets based on their indexes -->
			// Name the result list :tgtDegNewList
			for (Integer tgtDeg : tgtVDegList) {
				tgtDegNewList.add(tgtDeg);
			}
			Collections.shuffle(tgtDegNewList);
		}
		List<Integer> currentTgtDegList = new ArrayList<Integer>();
		int[] tgtDegNewInxes = Utils.createIndexUpdateTgtDeg(tgtDegNewList, tgtVHash, currentTgtDegList);

		logger.debug("Source out degree: " + srcVDegList.toString());
		logger.debug("Target in degree: " + tgtVDegList.toString());
		logger.debug("Target in-new degree: " + tgtDegNewList.toString());

		/*
		 * Calculate Probabilities 1- Multiply source and target degrees 2-
		 * Calculate Formula in the paper
		 */
		ListReverseIndexComparatorInt comparator = new ListReverseIndexComparatorInt(
				srcVDegList);
		Integer[] srcDegIdxes = comparator.createIndexArray();
		Arrays.sort(srcDegIdxes, comparator);

		for (Integer srcIdx : srcDegIdxes) {
			Vertex srcVertex = srcVHash.get(srcIdx);
			int srcDeg = srcVertex.getOutDegree();
			if (srcDeg == 0)
				continue;
			logger.debug("Start Draw Edges for Src with Deg = " + srcVertex
					+ ", Deg = " + srcDeg + ", Idx = " + srcIdx);
			
			List<Integer> unsatTargetIdxs = new ArrayList<Integer>();
			List<Double> samplingWeightList = new ArrayList<Double>();
			computeCorroctionFac(unsatTargetIdxs, samplingWeightList,
					tgtDegNewInxes, currentTgtDegList, tgtDegNewList,
					currentTgtCapList, srcDeg, divFac);
			logger.debug("Sampling Weights = " + samplingWeightList.toString());
			int[] drawnIndexes = null;

			/*
			 * Check if the number of available unsaturated targets are enough
			 * for this source otherwise start swap edges to find required
			 * targets
			 */

			if (srcDeg > unsatTargetIdxs.size()) {
				System.out.println("StartToSwap");
//				updateGraph(newGraph);
				updateGraph_Hash();
				long ts = System.currentTimeMillis();
//				swapEdges(srcVertex, unsatTargetIdxs, samplingWeightList,
//						tgtDegNewInxes, currentTgtDegList, tgtDegNewList,
//						currentTgtCapList, srcDeg, divFac, tgtVHash, srcVHash,
//						srcIdx, srcIndexHash, tgtIndexHash, srcVDegList, newGraph);
				swapEdges(srcVertex, unsatTargetIdxs, samplingWeightList,
						tgtDegNewInxes, currentTgtDegList, tgtDegNewList,
						currentTgtCapList, srcDeg, divFac, srcIdx, srcVDegList);
				System.out
						.println("Swap: " + (System.currentTimeMillis() - ts));
			} else if (unsatTargetIdxs.size() >= 1) {
//				long t1 = System.currentTimeMillis();
				GenerateRandom generateRandom = new GenerateRandom(
						Utils.toIntArray(unsatTargetIdxs),
						Utils.toDoubleArray(samplingWeightList));
				drawnIndexes = generateRandom.nextRandList(seed);
//				System.out.println("rand = " + (System.currentTimeMillis() - t1));
				logger.debug("Drawn Indexes: " + Arrays.toString(drawnIndexes));
				for (int i = 0; i < srcDeg; i++) {
					updateTargetDegree(currentTgtDegList, drawnIndexes[i]);
					Vertex targetVertex = tgtVHash.get(drawnIndexes[i]);
					addedEdgesList.add(Edge.getEdgeStr(srcVertex, targetVertex));
				}
			}
		}
		if (swapCount == 0) {
			for (String edge : addedEdgesList) {
//				outBuffer.append(edge.split("_")[0] + "\t"
//						+ edge.split("_")[1] + "\n");				
				outBuffer.add(edge.split("_")[0] + "\t"
						+ edge.split("_")[1]);				
			}
		} else {
			updateGraph_Hash();
			Iterator<Integer> srcKeysItr = graphSrcHash.keySet().iterator();
			while (srcKeysItr.hasNext()) {
				Integer srcLabel = (Integer) srcKeysItr.next();
				List<Integer> tgtLabelList = graphSrcHash.get(srcLabel);
				for (Integer tgtLabel : tgtLabelList) {
//					outBuffer.append(srcLabel + "\t" + tgtLabel + "\n");
					outBuffer.add(srcLabel + "\t" + tgtLabel);
				}
			}
		}
//		} else {
//			updateGraph(newGraph);
//			Iterator<Edge> layerEdges = newGraph.edgeSet().iterator();
//			while (layerEdges.hasNext()) {
//				Edge edge = (Edge) layerEdges.next();
//				outBuffer.append(edge.getSourceV().getLabel() + "\t"
//						+ edge.getTargetV().getLabel() + "\n");
//			}
//		}
//		System.out.println(System.currentTimeMillis() - tx);
		logger.debug(swapCount + " Swaps Performed in Layer " + color1 + ", " + color2);
		logger.debug("Layer " + col1 + ", " + col2 + "finished");
	}

	/**
	 * Need to Perform Swaps 1- first place edges to each available target node
	 * 2- update capacity of these used target nodes 3- update corrected weights
	 * for sampling
	 * 
	 * @param newGraph
	 * @throws Exception 
	 * 
	 */
//	private void swapEdges(Vertex sourceVertex, List<Integer> unsatTargetIdxs,
//			List<Double> samplingWeightList, int[] tgtDegNewInxes,
//			List<Integer> currentTgtDegList, List<Integer> tgtDegNewList,
//			List<Integer> currentTgtCapList, int srcDeg, double divFac,
//			HashMap<Integer,Vertex> tgtVHash, HashMap<Integer,Vertex> srcVHash, Integer srcIdx,
//			HashMap<Integer,Integer> srcIndexHash,
//			HashMap<Integer,Integer> tgtIndexHash, List<Integer> srcVDegList,
//			DirectedGraph<Vertex, Edge> newGraph) throws Exception {
	private void swapEdges(Vertex sourceVertex, List<Integer> unsatTargetIdxs,
			List<Double> samplingWeightList, int[] tgtDegNewInxes,
			List<Integer> currentTgtDegList, List<Integer> tgtDegNewList,
			List<Integer> currentTgtCapList, int srcDeg, double divFac,
			Integer srcIdx, List<Integer> srcVDegList) throws Exception {
		
		logger.debug(" <<<< Starting Swap for source : " + sourceVertex + " Deg = " + sourceVertex.getOutDegree());
		for (Integer i : unsatTargetIdxs) {
			Vertex targetVertex = tgtVHash.get(i);
			logger.debug("Available unsaturated targets : " + targetVertex);
			updateNewGraph_Hash(sourceVertex, targetVertex);
//			updateNewGraph(newGraph, sourceVertex, targetVertex);
			updateTargetDegree(currentTgtDegList, i);
		}
		int remainingEdges = srcDeg - unsatTargetIdxs.size();
		logger.debug("Remaining edged to swap  = " + remainingEdges);
		unsatTargetIdxs.clear();
		samplingWeightList.clear();
		computeCorroctionFac(unsatTargetIdxs, samplingWeightList,
				tgtDegNewInxes, currentTgtDegList, tgtDegNewList,
				currentTgtCapList, srcDeg, divFac);
		logger.debug("Available unsaturated target indexes : "
				+ unsatTargetIdxs.toString());

		/*
		 * identify source nodes that have targets not already hit by the
		 * current source, and that do NOT target the drawn target node,
		 * draw one
		 */
		
		// 1- source nodes that have targets not already hit by the current source
//		List<Vertex> srcsHavingTargetsOtherThanTargetsOfCurSourceList = new ArrayList<Vertex>(
//				srcsTargetingSomething);
		Iterator<Integer> keySet = graphSrcHash.keySet().iterator();
		List<Vertex> srcsHavingTargetsOtherThanTargetsOfCurSourceList = new ArrayList<Vertex>();
		while (keySet.hasNext()) {
			Integer srcLabel = (Integer) keySet.next();
			srcsHavingTargetsOtherThanTargetsOfCurSourceList.add(srcVHash.get(srcIndexHash.get(srcLabel)));
		}
//		Iterator<Edge> targetEdgesOfCurSrcList = newGraph
//				.outgoingEdgesOf(sourceVertex).iterator();
		List<Vertex> targetsOfCurSrcList = new ArrayList<Vertex>();
		List<Integer> tgtLabelsOfCurSrcList = graphSrcHash.get(sourceVertex.getLabel()); 
		for (Integer tgtLabel : tgtLabelsOfCurSrcList) {
			Vertex target = tgtVHash.get(tgtIndexHash.get(tgtLabel));
			targetsOfCurSrcList.add(target);
//			Iterator<Edge> eTargeting = newGraph
//					.incomingEdgesOf(target).iterator();
			List<Integer> srcsOfTargetList = graphTgtHash.get(target.getLabel());
			for (Integer srcLabel : srcsOfTargetList) {
				Vertex sVtx = srcVHash.get(srcIndexHash.get(srcLabel));
				sVtx.decreaseOutCap();
				if (sVtx.getOutCap() <= 0)
					srcsHavingTargetsOtherThanTargetsOfCurSourceList.remove(sVtx);
			}
//			while (eTargeting.hasNext()) {
//				Edge te = (Edge) eTargeting.next();
//				Vertex sVtx = te.getSourceV();
//				sVtx.decreaseOutCap();
//				if (sVtx.getOutCap() <= 0)
//					srcsHavingTargetsOtherThanTargetsOfCurSourceList.remove(sVtx);
//			}
			
		}
		
//		while (targetEdgesOfCurSrcList.hasNext()) {
//			Vertex target = targetEdgesOfCurSrcList.next().getTargetV();
//			targetsOfCurSrcList.add(target);
//			Iterator<Edge> eTargeting = newGraph
//					.incomingEdgesOf(target).iterator();
//			while (eTargeting.hasNext()) {
//				Edge te = (Edge) eTargeting.next();
//				Vertex sVtx = te.getSourceV();
//				sVtx.decreaseOutCap();
//				if (sVtx.getOutCap() <= 0)
//					srcsHavingTargetsOtherThanTargetsOfCurSourceList.remove(sVtx);
//			}
//		}
		

		/*
		 * for each unplaced edge,draw a permutation of unsaturated target nodes
		 * (weighted by available capacity) traverse the permutation to find an
		 * unsaturated node that has an allowable swap
		 */
		int[] drawnIdxPerm = null;
		for (int i = 0; i < remainingEdges; i++) {
			boolean successSwap = false;
			if (unsatTargetIdxs.size() == 1) {
				drawnIdxPerm = Utils.toIntArray(unsatTargetIdxs);
			} else {
				GenerateRandom generateRandom = new GenerateRandom(
						Utils.toIntArray(unsatTargetIdxs),
						Utils.toDoubleArray(samplingWeightList));
				drawnIdxPerm = generateRandom.nextRandList(seed);
			}
			logger.debug("Drawn unsaturated indexes "
					+ Arrays.toString(drawnIdxPerm));

			// 2- find sources that don't target drawnVertex
			for (int j = 0; j < drawnIdxPerm.length; j++) {
				int drawnIdx = drawnIdxPerm[j];
				Vertex drawVertex = tgtVHash.get(drawnIdx);

				List<Vertex> sourcesNotTargetingDrawnList = new ArrayList<Vertex>(
						srcsHavingTargetsOtherThanTargetsOfCurSourceList);
				List<Integer> srcsOfTargetList = graphTgtHash.get(drawVertex.getLabel());
				for (Integer srcLabel : srcsOfTargetList) {
					Vertex srcV = srcVHash.get(srcIndexHash.get(srcLabel));
					sourcesNotTargetingDrawnList.remove(srcV);
				}
//				Iterator<Edge> edgesTargeting = newGraph.incomingEdgesOf(
//						drawVertex).iterator();
//				while (edgesTargeting.hasNext()) {
//					Edge e = (Edge) edgesTargeting.next();
//					Vertex srcV = e.getSourceV();
//					sourcesNotTargetingDrawnList.remove(srcV);
//				}

				// Intersect of 2 above source lists
				List<Vertex> allowableSourcesList = new ArrayList<Vertex>();
				List<Integer> allowableSrcIdxs = new ArrayList<Integer>();
				for (Vertex srcV : sourcesNotTargetingDrawnList) {
					if (srcV.getUsedOutCapasity() > 0) {
						allowableSourcesList.add(srcV);
						allowableSrcIdxs.add(srcIndexHash.get(srcV.getLabel()));
					}
				}

				List<Integer> sourceOutDegTmpList = new ArrayList<Integer>(
						srcVDegList);
				for (Vertex tgtOfCurSource : targetsOfCurSrcList) {
//					Iterator<Edge> sourceNamesTargetingThisTarget = newGraph
//							.incomingEdgesOf(tgtOfCurSource).iterator();
//					while (sourceNamesTargetingThisTarget.hasNext()) {
//						Vertex src = (Vertex) sourceNamesTargetingThisTarget
//								.next().getSourceV();
//						Integer sourceIdx = srcIndexHash.get(src.getLabel());
//						int prevDeg = sourceOutDegTmpList.get(sourceIdx
//								.intValue()) - 1;
//						sourceOutDegTmpList.set(sourceIdx.intValue(), prevDeg);
//					}
					List<Integer> sourceNamesTargetingThisTarget = graphTgtHash.get(tgtOfCurSource.getLabel());
					for (Integer srcLabel : sourceNamesTargetingThisTarget) {
						Integer sourceIdx = srcIndexHash.get(srcLabel);
						int prevDeg = sourceOutDegTmpList.get(sourceIdx
								.intValue()) - 1;
						sourceOutDegTmpList.set(sourceIdx.intValue(), prevDeg);
					}
				}
				List<Double> allowableSourceWeightsList = new ArrayList<Double>();
				for (Integer asIdx : allowableSrcIdxs) {
					allowableSourceWeightsList.add(Double
							.valueOf(sourceOutDegTmpList.get(asIdx)));
				}
				logger.debug("J = " + j);
				logger.debug("sourceOutDeg : " + srcVDegList.toString());
				logger.debug("sourceOutDegTmp : " + sourceOutDegTmpList.toString());
				logger.debug("allowableSources : " + allowableSourcesList.toString());
				logger.debug("allowableSourceIdxs : " + allowableSrcIdxs.toString());
				logger.debug("allowableSourceWeights: " + allowableSourceWeightsList.toString());
				if (allowableSourcesList.size() > 0) {
					// allowable swap exists
					// update capacity of drawn unsaturated target
					// node with allowable swap
					updateTargetDegree(currentTgtDegList, drawnIdx);
					unsatTargetIdxs.clear();
					samplingWeightList.clear();
					computeCorroctionFac(unsatTargetIdxs, samplingWeightList,
							tgtDegNewInxes, currentTgtDegList, tgtDegNewList,
							currentTgtCapList, srcDeg, divFac);
					// weighted choice of swap sources
					// (necessary in order to choose equally
					// among all valid swap edges)
					GenerateRandom generateRandom = new GenerateRandom(
							Utils.toIntArray(allowableSrcIdxs),
							Utils.toDoubleArray(allowableSourceWeightsList));
					int swapSrcIdx = generateRandom.nextRandList(seed)[0];

					Vertex swapSourceV = srcVHash.get(swapSrcIdx);
					// identify targets of the drawn swap source NOT
					// already targeted by current source, draw one

					List<Integer> allowableTargetIdxs = new ArrayList<Integer>();
					logger.debug("swap source: " + swapSourceV);
					List<Integer> swapSrcTargetList = graphSrcHash.get(swapSourceV.getLabel());
					for (Integer tgtLabel : swapSrcTargetList) {
						Vertex target = tgtVHash.get(tgtIndexHash.get(tgtLabel));
						if (!targetsOfCurSrcList.contains(target))
							allowableTargetIdxs.add(tgtIndexHash.get(tgtLabel));

					}
//					Iterator<Edge> swapSrcTargetEdgeList = newGraph
//							.outgoingEdgesOf(swapSourceV).iterator();

//					while (swapSrcTargetEdgeList.hasNext()) {
//						Vertex target = (Vertex) swapSrcTargetEdgeList.next()
//								.getTargetV();
//						if (!targetsOfCurSrcList.contains(target))
//							allowableTargetIdxs.add(tgtIndexHash.get(target.getLabel()));
//					}
					// Equal Probability Selection of allowable
					// targets
					logger.debug("Allowable Targets: " + allowableTargetIdxs.toString());
					generateRandom = new GenerateRandom(
							Utils.toIntArray(allowableTargetIdxs));
					int swapTargetIdx = generateRandom.nextRandList(seed)[0];
					// create new edges from swap source to drawn
					// unsaturated target node
					// and from current node to drawn target of swap
					// source
					Vertex swapTargetV = tgtVHash.get(swapTargetIdx);
//					newGraph.addEdge(swapSourceV, drawVertex);
					graphSrcHash.get(swapSourceV.getLabel()).add(drawVertex.getLabel());
					graphTgtHash.get(drawVertex.getLabel()).add(swapSourceV.getLabel());
					
//					newGraph.addEdge(sourceVertex, swapTargetV);
					graphSrcHash.get(sourceVertex.getLabel()).add(swapTargetV.getLabel());
					graphTgtHash.get(swapTargetV.getLabel()).add(sourceVertex.getLabel());
					
//					newGraph.removeEdge(swapSourceV, swapTargetV);
					graphSrcHash.get(swapSourceV.getLabel()).remove((Integer)swapTargetV.getLabel());
					graphTgtHash.get(swapTargetV.getLabel()).remove((Integer)swapSourceV.getLabel());
					
					// Update src not hitting targets of cur source
					targetsOfCurSrcList.add(swapTargetV);
					List<Integer> srcsOfNewTgtList = graphTgtHash.get(swapTargetV.getLabel());
					for (Integer srcLabel : srcsOfNewTgtList) {
						Vertex srcV = srcVHash.get(srcIndexHash.get(srcLabel));
						srcV.decreaseOutCap();
						if (srcV.getOutCap() <= 0)
							srcsHavingTargetsOtherThanTargetsOfCurSourceList.remove(srcV);
						
					}
					
//					Iterator<Edge> edgesOfNewTgtItr = newGraph.incomingEdgesOf(swapTargetV).iterator();
//					while (edgesOfNewTgtItr.hasNext()) {
//						Edge edge = (Edge) edgesOfNewTgtItr.next();
//						Vertex srcV = edge.getSourceV();
//						srcV.decreaseOutCap();
//						if (srcV.getOutCap() <= 0)
//							srcsHavingTargetsOtherThanTargetsOfCurSourceList.remove(srcV);
//					}
					successSwap = true;
					if (successSwap) {
						swapCount++;
						break;
					}
				}
			}
		}
	}

//	private void updateNewGraph(DirectedGraph<Vertex, Edge> newGraph,
//			Vertex sourceVertex, Vertex targetVertex) {
//		if (!newGraph.containsVertex(targetVertex))
//			newGraph.addVertex(targetVertex);
//		if (!newGraph.containsVertex(sourceVertex)) {
//			newGraph.addVertex(sourceVertex);
////			srcsTargetingSomething.add(sourceVertex);
//		}
//		newGraph.addEdge(sourceVertex, targetVertex);
//		sourceVertex.incrementUsedCapasity();
//	}
	
	private void updateNewGraph_Hash(Vertex sourceVertex, Vertex targetVertex) {
		List<Integer> tgtsOfSrcList = graphSrcHash.get(sourceVertex.getLabel());
		if (tgtsOfSrcList == null)
			tgtsOfSrcList = new ArrayList<Integer>();
		if (!tgtsOfSrcList.contains(targetVertex.getLabel()))
			tgtsOfSrcList.add(targetVertex.getLabel());
		
		List<Integer> srcsOftgtList = graphTgtHash.get(targetVertex.getLabel());
		if (srcsOftgtList == null)
			srcsOftgtList = new ArrayList<Integer>();
		if (!srcsOftgtList.contains(sourceVertex.getLabel())) {
			srcsOftgtList.add(sourceVertex.getLabel());
//			srcsTargetingSomething.add(sourceVertex);
		}
		
		graphSrcHash.put(sourceVertex.getLabel(), tgtsOfSrcList);
		graphTgtHash.put(targetVertex.getLabel(), srcsOftgtList);
		
		srcVHash.get(srcIndexHash.get(sourceVertex.getLabel())).incrementUsedCapasity();
	}
	
//	private void updateGraph(DirectedGraph<Vertex, Edge> newGraph) {
////		logger.debug("Edges: " + addedEdgesList.toString());
//		long t = System.currentTimeMillis();
//		for (String edge : addedEdgesList) {
////			if (!newGraph.containsVertex(edge.getTargetV()))
//			newGraph.addVertex(edge.getTargetV());
//			if (!newGraph.containsVertex(edge.getSourceV())) {
//				newGraph.addVertex(edge.getSourceV());
////				srcsTargetingSomething.add(edge.getSourceV());
//			}
////			logger.debug("add uG = " + edge.getSourceV() + ", " + edge.getTargetV());
//			newGraph.addEdge(edge.getSourceV(), edge.getTargetV());
//			
//			edge.getSourceV().incrementUsedCapasity();
//		}
//		System.out.println("s " + (System.currentTimeMillis() - t));
//		addedEdgesList.clear();
//	}
	
	
	private void updateGraph_Hash() {
//		logger.debug("Edges: " + addedEdgesList.toString());
//		long t = System.currentTimeMillis();
		for (String edge : addedEdgesList) {
			Integer srcV = Integer.valueOf(edge.split("_")[0]);
			Integer tgtV = Integer.valueOf(edge.split("_")[1]);
			List<Integer> tgtsOfSrcList = graphSrcHash.get(srcV);
			if (tgtsOfSrcList == null)
				tgtsOfSrcList = new ArrayList<Integer>();
			if (!tgtsOfSrcList.contains(tgtV))
				tgtsOfSrcList.add(Integer.valueOf(tgtV));
			
			List<Integer> srcsOftgtList = graphTgtHash.get(tgtV);
			if (srcsOftgtList == null)
				srcsOftgtList = new ArrayList<Integer>();
			if (!srcsOftgtList.contains(srcV)) {
				srcsOftgtList.add(srcV);
			}
//			if (!srcsTargetingSomething.contains(edge.getSourceV()))
//				srcsTargetingSomething.add(edge.getSourceV());
			
			graphSrcHash.put(srcV, tgtsOfSrcList);
			graphTgtHash.put(tgtV, srcsOftgtList);
			srcVHash.get(srcIndexHash.get(srcV)).incrementUsedCapasity();
		}
//		System.out.println("s " + (System.currentTimeMillis() - t));
		addedEdgesList.clear();
	}
//	private void updateNewGraph2(DirectedGraph<Vertex, Edge> newGraph,
//			Vertex sourceVertex, Vertex targetVertex) {
//		if (!newGraph.containsVertex(targetVertex))
//			newGraph.addVertex(targetVertex);
//		newGraph.addEdge(sourceVertex, targetVertex);
//	}

	/**
	 * Compute Correction Factor based on available capacities and target list
	 * 
	 * @param unsatTargetIdxs
	 * @param samplingWeightList
	 * @param tgtDegNewInxes
	 * @param currentTgtDegList
	 * @param tgtDegNewList
	 * @param currentTgtCapList
	 * @param srcDeg
	 * @param divFac
	 */
	private void computeCorroctionFac(List<Integer> unsatTargetIdxs,
			List<Double> samplingWeightList, int[] tgtDegNewInxes,
			List<Integer> currentTgtDegList, List<Integer> tgtDegNewList,
			List<Integer> currentTgtCapList, int srcDeg, double divFac) {
		currentTgtCapList.clear();
		for (Integer tgtIdx : tgtDegNewInxes) {
			int inDegCur = currentTgtDegList.get(tgtIdx);
			int inDegOrig = tgtDegNewList.get(tgtIdx);
			if (inDegCur < inDegOrig) {
				unsatTargetIdxs.add(tgtIdx);
				/** Compute Correction Factors and Sampling Weight */
				double cfact = 1 - ((srcDeg * inDegOrig) / divFac);
				int cap = inDegOrig - inDegCur;
				samplingWeightList.add(cap * cfact);
			}
			currentTgtCapList.add(inDegOrig - inDegCur);
		}
	}

	private void updateTargetDegree(List<Integer> currentTgtDegList, int index) {
		int newInDeg = currentTgtDegList.get(index) + 1;
		currentTgtDegList.set(index, newInDeg);
	}

	/**
	 * Tuning the Factor based on the degree distributions
	 * 
	 * @param tgtVDegList
	 * @param srcVDegList
	 * @param m
	 */
	public void calculateFactor(List<Integer> tgtVDegList,
			List<Integer> srcVDegList, double m) {
		Integer tgtMin = Collections.min(tgtVDegList);
		Integer tgtMax = Collections.max(tgtVDegList);
		Integer srcMax = Collections.max(srcVDegList);
		if (tgtMin == 0) {
			List<Integer> tempDegList = new ArrayList<Integer>();
			for (Integer e : tgtVDegList) {
				tempDegList.add(e);
			}

			Collections.sort(tempDegList);
			while (tempDegList.get(0) == 0) {
				tempDegList.remove(0);
			}
			tgtMin = Collections.min(tempDegList);
		}
		double facMin = (tgtMax * srcMax) / m + 0.1;

		double p1 = tgtMin * (1 - ((tgtMin * srcMax) / (facMin * m))) * 2;
		double t1 = (tgtMax * srcMax) / m;
		double t2 = (1 - (p1 / tgtMax));
		factor = (t1 / t2) + 3;
		logger.debug("#of Edges = " + m + " Min_Fac = " + facMin

		+ " Calculated Fac = " + factor);
		logger.debug("target max = " + tgtMax + ", target min = " + tgtMin
				+ ", src max = " + srcMax);
	}

	public void printEdgesToFile() {
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(new File(edgeFileOut)));
			for (String string : outBuffer) {
				out.write(string);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}
	public void clearCollections() {
		addedEdgesList.clear();
		srcVHash.clear();
		srcIndexHash.clear();
		tgtVHash.clear();
		tgtIndexHash.clear();
		graphSrcHash.clear();
		graphTgtHash.clear();
	}

}
