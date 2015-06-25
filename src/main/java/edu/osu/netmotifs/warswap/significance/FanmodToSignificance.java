package edu.osu.netmotifs.warswap.significance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import edu.osu.netmotifs.warswap.common.Utils;

/**
 * 
 * @author mitra
 *
 */
public class FanmodToSignificance {

	private int motifSize; // for proper parsing of FANMOD results files
	private String fanmodOutDir; // input directory: random networks
	private String origFile; // input file: real network
	private String outFile;
	private String inExtention;
	private int randCount = 0;
	HashMap<String, Long> countsHash = new HashMap<String, Long>();
	HashMap<String, Byte> allIdsHash = new HashMap<String, Byte>();

	public FanmodToSignificance(int motifSize, String fanmodOutDir,
			String fanmodOrigFile, String outFile, String inExtention) {
		this.fanmodOutDir = fanmodOutDir;
		this.inExtention = inExtention;
		this.motifSize = motifSize;
		this.outFile = outFile;
		origFile = fanmodOrigFile;
	}

	public void extractSubGraphsInfo() throws Exception {
		// Read the files in input directory
		File folder = new File(fanmodOutDir);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			int status = 2; // 3 means file is invalid
			if (listOfFiles[i].isFile()
					&& listOfFiles[i].getName().equalsIgnoreCase(origFile))
				status = 0; // File is real net output
			else if (listOfFiles[i].isFile()
					&& listOfFiles[i].getName().endsWith(inExtention))
				status = 1; // File is rand net output
			if (status == 2)
				continue;
//			System.out.println("File " + listOfFiles[i].getName());
			InputStream inputStream = new FileInputStream(new File(
					listOfFiles[i].getAbsolutePath()));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			boolean finishProcess = false;
			randCount++;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("ID,Adj")) {
					bufferedReader.readLine();
					bufferedReader.readLine();
					while ((line = bufferedReader.readLine()) != null) {
//						line = bufferedReader.readLine(); // subgraph start line
//						System.out.println(line);
						SubGraph subGraph = new SubGraph();
						String[] parts = line.split(",");
						long count = Long.valueOf(parts[2].split(" ")[0].trim());
						String subGraphId = parts[0] + "_" + parts[1];
						for (int j = 0; j < motifSize - 1; j++) {
							line = bufferedReader.readLine();
							subGraphId += line.substring(1);
						}
						allIdsHash.put(subGraphId, Byte.valueOf("1"));
						if (status == 0) {
							countsHash.put(0 + "_" + subGraphId, count);
							bufferedReader.readLine();
							continue;
						}
						subGraph.setSubgId(subGraphId);
						countsHash.put(randCount + "_" + subGraphId, count);
						bufferedReader.readLine();
					}
				}
			}
			bufferedReader.close();
			inputStream.close();
		}

		Iterator<String> subGIdsItr = allIdsHash.keySet().iterator();
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuffer outBuffer = new StringBuffer();
		while (subGIdsItr.hasNext()) {
			String subgId = (String) subGIdsItr.next();

			Long realFreq = countsHash.get(0 + "_" + subgId);
			if (realFreq == null)
				realFreq = Long.valueOf(0);

			double nrand_gt_real = 0.0;
			double mean = 0.0;
			double M2 = 0.0;
			for (int j = 1; j <= randCount; j++) {
				Long randFreq = countsHash.get(j + "_" + subgId);
				if (randFreq == null)
					randFreq = Long.valueOf(0);
				double delta = randFreq - mean;
				mean += delta / j;
				M2 += delta * (randFreq - mean);
				if (randFreq > realFreq)
					nrand_gt_real++;
			}
			double variance = 0.000;
			if ((randCount - 1) > 0) {
				variance = M2 / (randCount - 1);
			}
			double stdev = Math.sqrt(variance);
			double zScore = Double.MAX_VALUE, pValue = 0.0;
			if (stdev != 0) 
				zScore = (realFreq - mean)/stdev;
			pValue = nrand_gt_real/randCount;
			String star = "";
			if (pValue <= 0.01 && zScore != Double.MAX_VALUE && zScore >= 2.0 && stdev >= 1.0)
				star = "**";
			String zscoreStr = df.format(zScore);
			if (zScore == Double.MAX_VALUE)
				zscoreStr = "Inf";
			outBuffer.append(subgId + "\t" + zscoreStr + "\t" + df.format(pValue) + "\t" + star + "\t" + df.format(stdev) + "\n");
		}
		Utils.printStrToFile(outBuffer.toString(), outFile);
	}

	public static void main(String[] args) {
		try {
			new FanmodToSignificance(3, "/home/mitra/workspace/uni-workspace/warswap/data/small20/small20.rand.fanmod_subgraphs", "small20.ORIG.fanmod.out", "", ".fanmod.out").extractSubGraphsInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
