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
package edu.osu.netmotifs.warswap.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import edu.osu.netmotifs.warswap.common.exception.EdgeFileFormatException;
import edu.osu.netmotifs.warswap.common.exception.VertexFileFormatException;


/**
 * Convert warswap's edges/vertices format to subgraph enumerator acceptable format:
 
 V1    V2    V1_color     V2_color
 */
public class ConvertToSubgToolFormat {

	private static HashMap<String, String> vertexColorHash = new HashMap<String, String>();;
	private static HashMap<String, Integer> colorHash = new HashMap<String, Integer>();
	

	public static void clearVertexHash() {
		vertexColorHash.clear();
	}
	
	/**
	 * This method reads a file in the following format from user input files:
	 for vertices user input is in the following format: 
	            V1   TF/MIR/GENE
	 * This method should convert above to the following format which is readable 
	 by warswap core:
	            V1   0/1/2
	 (slashes here mean OR) 
	 * @author mitra
	 */
	public static void readVertices(String vertexFileIn) throws VertexFileFormatException {
		clearVertexHash();
		if (!vertexColorHash.isEmpty())
			return;
		try {
			InputStream inputStream = new FileInputStream(
					new File(vertexFileIn));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("name"))
					continue;
				String vColor = line.split("\t")[1];
				colorHash.put(vColor, Integer.valueOf(vColor));
				vertexColorHash.put(line.split("\t")[0], vColor);
			}
			bufferedReader.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new VertexFileFormatException();
		}

	}

	@Deprecated
	public void convert(String edgeFileIn, String edgeFileOut, String vertexFileIn) throws VertexFileFormatException {
		readVertices(vertexFileIn);
		Map<String, String> sortedEdgeMap = new TreeMap<String, String>();
		try {
			InputStream inputStream = new FileInputStream(new File(edgeFileIn));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				String[] edgeParts = line.split("\t");
				if (edgeParts[0].equalsIgnoreCase(edgeParts[1])) {
					if (!CONF.selfLoops)
						continue;
					else {
						int nOfColors = colorHash.keySet().size();
						vertexColorHash.put(edgeParts[0], String.valueOf(nOfColors));
					}
				}
				sortedEdgeMap.put(edgeParts[0] + "\t" + edgeParts[1],
						vertexColorHash.get(edgeParts[0]) + "\t"
								+ vertexColorHash.get(edgeParts[1]));
			}
			bufferedReader.close();
			inputStream.close();
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(
					edgeFileOut)));
			Iterator<String> iterator = sortedEdgeMap.keySet().iterator();
			while (iterator.hasNext()) {
				String keyStr = (String) iterator.next();
				String v1 = keyStr.split("\t")[0];
				String v2 = keyStr.split("\t")[1];
				out.write(keyStr + "\t" + vertexColorHash.get(v1) + "\t" + vertexColorHash.get(v2));
				out.newLine();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author mitra 
	 * This method reads edges and vertices from warswap format:
	   			V1      V2             (for edges)
	   			V1      0/1/2		   (for vertices)
	 and convert it to a single file that has following format:
	  			V1      V2      V1_color        V2_color
	 */
	public static void convertToEdgVtxColorFileFormat(String edgeFileIn, String edgeVtxColorFile, String vertexFileIn) throws Exception {
		readVertices(vertexFileIn);
		Map<String, String> sortedEdgeMap = new TreeMap<String, String>();
		try {
			InputStream inputStream = new FileInputStream(new File(edgeFileIn));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				String[] edgeParts = line.split("\t");
				if (vertexColorHash.get(edgeParts[0]) == null || vertexColorHash.get(edgeParts[1]) == null)
					throw new EdgeFileFormatException("Invalid input vertex format!");
				sortedEdgeMap.put(edgeParts[0] + "\t" + edgeParts[1],
						vertexColorHash.get(edgeParts[0]) + "\t"
								+ vertexColorHash.get(edgeParts[1]));
			}
			bufferedReader.close();
			inputStream.close();
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(
					edgeVtxColorFile)));
			Iterator<String> iterator = sortedEdgeMap.keySet().iterator();
			while (iterator.hasNext()) {
				String keyStr = (String) iterator.next();
				out.write(keyStr + "\t" + sortedEdgeMap.get(keyStr));
				out.newLine();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void main(String[] args) {
		Map<String, String> sortedEdgeMap = new TreeMap<String, String>();
		sortedEdgeMap.put("1\t2", "0\t11");
		sortedEdgeMap.put("10\t1", "0\t11");
		sortedEdgeMap.put("3\t2", "0\t11");
		sortedEdgeMap.put("3\t1", "0\t11");
		sortedEdgeMap.put("1\t3", "0\t11");

		Iterator<String> it = sortedEdgeMap.keySet().iterator();
		while (it.hasNext()) {
			String string = (String) it.next();
			System.out.println(string);
		}

	}

}
