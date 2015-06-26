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
package edu.osu.netmotifs.warswap.significance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.osu.netmotifs.warswap.common.CONF;
import edu.osu.netmotifs.warswap.common.Utils;

/**
 * This class computes the p-value and z-score and std of each subgraph 
 * observed in original and random networks.
 * 
 * @author mitra
 *
 */
public class ExtractSignificanceMotifs {

	private int motifSize; // for proper parsing of subgraph enumerator tool result files
	private int noOfRandNetworks = 0;
	private String subgOutDir; // input directory: random networks
	private String origFile; // input file: real network
	private String outFile;
	private String inExtention;
	private HashMap<String, List<Long>> randCountHash = new HashMap<String, List<Long>>();
	private HashMap<String, Long> origCountsHash = new HashMap<String, Long>();

	public ExtractSignificanceMotifs(int motifSize, String subgOutDir,
			String subgOrigFile, String outFile, String inExtention) {
		this.subgOutDir = subgOutDir;
		this.inExtention = inExtention;
		this.motifSize = motifSize;
		this.outFile = outFile;
		origFile = subgOrigFile;
	}

	private void parseNetworks() throws Exception {
		// Read the files in input directory
		File folder = new File(subgOutDir);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			int status = 2; // 3 means file is invalid
			if (listOfFiles[i].isFile()
					&& listOfFiles[i].getName().equalsIgnoreCase(origFile))
				status = 0; // File is real net output
			else if (listOfFiles[i].isFile()
					&& listOfFiles[i].getName().endsWith(inExtention)) {
				status = 1; // File is rand net output
				noOfRandNetworks++;
			}
			if (status == 2)
				continue;
			InputStream inputStream = new FileInputStream(new File(
					listOfFiles[i].getAbsolutePath()));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("Results")) {
					bufferedReader.readLine();
					bufferedReader.readLine();
					while ((line = bufferedReader.readLine()) != null) {
						String[] parts = line.split(",");
						String adjMtx = parts[1];
						long count = Long.valueOf(parts[2].trim());
						for (int j = 0; j < motifSize - 1; j++) {
							line = bufferedReader.readLine();
							adjMtx += line;
						}
						if (status == 0) { // This is original network
							origCountsHash.put(adjMtx, count);
						} else {           // This is random network
							
							List<Long> rcountList = null;
							
							if (randCountHash.containsKey(adjMtx)) 
								rcountList = randCountHash.get(adjMtx);
							else 
								rcountList = new ArrayList<Long>();

							rcountList.add(count);
							randCountHash.put(adjMtx, rcountList);
						}
						
						bufferedReader.readLine();
					}
				}
			}
			bufferedReader.close();
			inputStream.close();
		}

	}
	
	private String computeSignificance() {
		String outBuffer = "";
		Iterator<String> origMotifs = origCountsHash.keySet().iterator();
		while (origMotifs.hasNext()) {
			String motif = (String) origMotifs.next();
			
			double origCount = origCountsHash.get(motif);
			double mean = 0.0;
			double M2 = 0.0, variance = 0.0;
			double nrand_gt_real = 0.0;   // for p-value
			
			if (randCountHash.containsKey(motif)) {
				List<Long> randCountsList = randCountHash.get(motif);
				for (Long randCount : randCountsList)
					mean += randCount;
					
				for (Long randCount : randCountsList) {
					M2 += Math.pow((randCount - mean), 2);  // for variance computation
					if (randCount > origCount)				// for p-value calculation
						nrand_gt_real++;
				}
				variance = M2 / (noOfRandNetworks - 1);
			}
			double stdev = Math.sqrt(variance);
			String stdStr = Utils.parseToCientificNotation(stdev);
			double zScore = Double.MAX_VALUE, pValue = 0.0;
			
			if (stdev != 0) 
				zScore = (origCount - mean) / stdev;

			pValue = nrand_gt_real / (noOfRandNetworks - 1);
			String pValStr = Utils.parseToCientificNotation(pValue);
			
			String zscoreStr = Utils.parseToCientificNotation(zScore);
			if (zScore == Double.MAX_VALUE)
				zscoreStr = CONF.INFINIT;
			
			String separator = "\t";
			outBuffer += motif + separator + zscoreStr + separator + pValStr + separator + stdStr + "\n";
		}
		return outBuffer;
	}
	
	public void extractSubGraphsInfo() throws Exception {
		
		String seperator = "\t";
		StringBuffer outBuffer = new StringBuffer("AdjMatrix" + seperator + "Z-Score" + seperator + "P-Value" + seperator + "Std-Dev\n");
		
		parseNetworks();
		outBuffer.append(computeSignificance());
		
		Utils.printStrToFile(outBuffer.toString(), outFile);
	}

	public static void main(String[] args) {
		try {
			new ExtractSignificanceMotifs(3, "/home/mitra/workspace/uni-workspace/WaRSwapSoftApp/sample_inputs/output/output.rand.subg_subgraphs/", "output.ORIG.subg.out", "motifs.txt", ".subg.out").extractSubGraphsInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
