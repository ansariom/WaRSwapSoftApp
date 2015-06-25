package edu.osu.netmotifs.warswap.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
 * Convert igraph edges/vertices format to Fanmod format Edges: V1 V2 Vertices:
 * V1 color
 * 
 * @author mitra
 *
 */
public class ConvertToFanmod {

	private HashMap<String, String> vertexColorHash;
	private HashMap<String, String> selfloopVerticesHash;
	private HashMap<String, Integer> colorHash = new HashMap<String, Integer>();
	private static ConvertToFanmod convertToFanmod;
	private int colorNum = 0;
	
	public ConvertToFanmod() {
		vertexColorHash = new HashMap<String, String>();
		selfloopVerticesHash = new HashMap<String, String>();
	}
	
//	public static ConvertToFanmod getInstance() {
//		if (convertToFanmod == null)
//			convertToFanmod = new ConvertToFanmod();
//		return convertToFanmod;
//	}

	public void clearVerHash() {
		vertexColorHash.clear();
	}
	
	public void readVertices(String vertexFileIn) throws VertexFileFormatException {
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
				Integer colorCode = colorHash.get(vColor);
				if (colorCode == null)
					colorCode = colorNum++;
				colorHash.put(vColor, colorCode);
				vertexColorHash.put(line.split("\t")[0], String.valueOf(colorCode));
			}
			bufferedReader.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new VertexFileFormatException();
		}

	}

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
//						selfloopVerticesHash.put(edgeParts[0], "1");
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
	
	public void convertForWarswap(String edgeFileIn, String edgeFileOut, String vertexFileIn) throws Exception {
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
					throw new EdgeFileFormatException("Vertices without color exist in edge file");
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
