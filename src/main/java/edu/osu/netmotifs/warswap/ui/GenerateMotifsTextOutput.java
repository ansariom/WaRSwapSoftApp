package edu.osu.netmotifs.warswap.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.osu.netmotifs.warswap.common.CONF;

public class GenerateMotifsTextOutput {

	public void saveAsTxtFile(String mainMotifsOutFile, String saveFile, String fileType, double zScoreCutoff,
			double pvalueCutoff) {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(mainMotifsOutFile));
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
			String seperator = "\t";
			if (fileType.equalsIgnoreCase(CONF.CSV_FILE_TYPE))
				seperator = ",";

			FileWriter writer = new FileWriter(saveFile);
			writer.write(CONF.MOTIFID_LABEL + seperator + CONF.ZSCORE_LABEL + seperator + CONF.PVALUE_LABEL + seperator
					+ CONF.STD_DEV_LABEL + "\n");

			String line = null;
			line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] parts = line.split("\t");
				String adjMtx = parts[0];

				double zscore = Double.valueOf(parts[1]);
				double pValue = Double.valueOf(parts[2]);

				if (zScoreCutoff != -1 || pvalueCutoff != -1) {
					if (zScoreCutoff != -1) {
						if (zscore < zScoreCutoff)
							continue;
					}
					if (pvalueCutoff != -1) {
						if (pValue > pvalueCutoff)
							continue;
					}
				}
				writer.write(adjMtx + seperator + parts[1] + seperator + parts[2] + seperator + parts[3] + "\n");
			}
			writer.flush();
			writer.close();
			inputStream.close();
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
