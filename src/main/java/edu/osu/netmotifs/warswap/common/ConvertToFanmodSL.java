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

/**
 * Convert igraph edges/vertices format to Fanmod format Edges: V1 V2 Vertices:
 * V1 color
 * 
 * @author mitra
 *
 */
public class ConvertToFanmodSL {

	private HashMap<String, String> vertexColorHash;;
	private static ConvertToFanmodSL convertToFanmod;
	
	private ConvertToFanmodSL() {
		vertexColorHash = new HashMap<String, String>();
	}
	
	public static ConvertToFanmodSL getInstance() {
		if (convertToFanmod == null)
			convertToFanmod = new ConvertToFanmodSL();
		return convertToFanmod;
	}

	public void clearVerHash() {
		vertexColorHash.clear();
	}
	public void readVertices(String vertexFileIn) {
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
				vertexColorHash.put(line.split("\t")[0], line.split("\t")[1]);
			}
			bufferedReader.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void convert(String edgeFileIn, String edgeFileOut, String vertexFileIn) {
		boolean removeSelfLoops = false;
		readVertices(vertexFileIn);
		Map<String, String> sortedEdgeMap = new TreeMap<String, String>();
		try {
			InputStream inputStream = new FileInputStream(new File(edgeFileIn));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				String[] edgeParts = line.split("\t");
				if (removeSelfLoops && edgeParts[0].equalsIgnoreCase(edgeParts[1]))
					continue;
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
