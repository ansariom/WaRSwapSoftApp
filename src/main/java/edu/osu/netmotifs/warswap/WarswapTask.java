/** Copyright (C) 2015 
 * @author Mitra Ansariola 
 * 
 * This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Contact info:  megrawm@science.oregonstate.edu

 */

package edu.osu.netmotifs.warswap;

import static edu.osu.netmotifs.warswap.common.CONF.DIR_SEP;
import static edu.osu.netmotifs.warswap.common.CONF.EDGE_FILEIN_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.FNM_EDGE_ORIG_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.FNM_EDGE_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.FNM_FILEIN_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.FNM_OUT_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.FN_EOUTDIR_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.FN_OUTDIR_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.GENE_Color;
import static edu.osu.netmotifs.warswap.common.CONF.JGRAPH_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.LOG_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.MIR_Color;
import static edu.osu.netmotifs.warswap.common.CONF.MOTIF_SIZE_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.NETWORK_NAME_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.OS;
import static edu.osu.netmotifs.warswap.common.CONF.PREFIX_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.TF_Color;
import static edu.osu.netmotifs.warswap.common.CONF.VTX_FILEIN_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.WIN_OS;
import static edu.osu.netmotifs.warswap.common.CONF.WR_EOUTDIR_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.WR_OUTDIR_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.properties;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import edu.osu.netmotifs.warswap.common.CONF;
import edu.osu.netmotifs.warswap.common.CallSubgCount;
import edu.osu.netmotifs.warswap.common.ConvertToSubgToolFormat;
import edu.osu.netmotifs.warswap.common.LoadLogger;
import edu.osu.netmotifs.warswap.common.ThreadLogger;

 public class WarswapTask implements Callable<String> {

	private int jobNo = 1;
	private String edgeOutFile;
	private String fnmEdgeOutFile;
	private String fnmOutFile;
	private String logFile;
	private Logger logger;
	int motifSize = 3;
	private String loggerName = "JOB";

	public static void main(String[] args) {
		WarswapTask task = new WarswapTask(Integer.valueOf(args[0]));
		try {
			System.out.println(task.call());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WarswapTask(int jobNo) {
		try {
			CONF.loadProps();
//			this.loggerName = loggerName + jobNo + "T";
			this.jobNo = jobNo;
			edgeOutFile = properties.getProperty(WR_EOUTDIR_KEY) + DIR_SEP
					+ properties.getProperty(PREFIX_KEY) + jobNo + JGRAPH_SUFFIX;
			logFile = properties.getProperty(WR_OUTDIR_KEY) + DIR_SEP
					+ properties.getProperty(PREFIX_KEY) + jobNo + LOG_SUFFIX;
			motifSize = Integer.valueOf(properties.getProperty(MOTIF_SIZE_KEY));
	
			if (jobNo == 0) {
				fnmEdgeOutFile = properties.getProperty(FN_EOUTDIR_KEY)
						+ DIR_SEP + properties.getProperty(NETWORK_NAME_KEY)
						+ FNM_EDGE_ORIG_SUFFIX + FNM_EDGE_SUFFIX;
				fnmOutFile = properties.getProperty(FN_OUTDIR_KEY) + DIR_SEP
						+ properties.getProperty(NETWORK_NAME_KEY)
						+ FNM_EDGE_ORIG_SUFFIX + FNM_OUT_SUFFIX;
			} else {
				fnmEdgeOutFile = properties.getProperty(FN_EOUTDIR_KEY)
						+ DIR_SEP + properties.getProperty(PREFIX_KEY) + jobNo
						+ FNM_EDGE_SUFFIX;
				fnmOutFile = properties.getProperty(FN_OUTDIR_KEY) + DIR_SEP
						+ properties.getProperty(PREFIX_KEY) + jobNo
						+ FNM_OUT_SUFFIX;
			}
//			System.out.println(CONF.getRunningMode() + "------------");
			if (CONF.getRunningMode().equalsIgnoreCase(CONF.CLUSTER_MODE)) {
				LoadLogger.setLogger1(logFile, "DEBUG", null);
				logger = LoadLogger.rLogger;
			} else 
				logger = ThreadLogger.getLogger(loggerName, "debug");
		}catch(Throwable t) {
			t.printStackTrace();
		} 
	}

	@Override
	public String call() {
		long t1 = System.currentTimeMillis();
		try {
			if (jobNo == 0) {
				runSubgCount();
				return "0";
			}
			runWarswap();
			runSubgCount();
//			extractLogs();
		} catch (Exception e) {
			logger.error(Arrays.toString(e.getStackTrace()));
			return "Error : " + e.getMessage();
		} catch (Throwable e) {
			logger.error(Arrays.toString(e.getStackTrace()));
			return "Error : " + e.getMessage();
		}
		logger.debug("Job " + jobNo + " Finished in " + (System.currentTimeMillis() - t1));
		return String.valueOf(jobNo);

	}
		
	
	private void runWarswap() throws Exception {
		GraphDAO graphDAO = GraphDAO.getInstance();
		String tableName = "wrswap"+jobNo;
		
		long t1 = System.currentTimeMillis();
		try {
			graphDAO.createTable(tableName);
			DrawRandGraphWithSwaps drawRandGraphWithSwaps = new DrawRandGraphWithSwaps(
					logger, properties.getProperty(VTX_FILEIN_KEY), edgeOutFile, tableName);
			drawRandGraphWithSwaps.loadGraph(properties.getProperty(FNM_FILEIN_KEY));
//			System.out.println("ReadFile: " + (System.currentTimeMillis() - t1));
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(TF_Color, TF_Color);
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(TF_Color, MIR_Color);
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(TF_Color, GENE_Color);
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(MIR_Color, TF_Color);
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(MIR_Color, GENE_Color);
			drawRandGraphWithSwaps.printEdgesToFile();
			drawRandGraphWithSwaps.clearCollections();
			graphDAO.dropTable(tableName);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw e;
		} finally {
			logger.debug("WarSwap Finished in (millisecs) : "
					+ (System.currentTimeMillis() - t1));
		}
	}

	private void runSubgCount() throws Exception {
		if (jobNo == 0) {
			new ConvertToSubgToolFormat().convert(properties.getProperty(EDGE_FILEIN_KEY), fnmEdgeOutFile, properties.getProperty(VTX_FILEIN_KEY));
		} else {
			new ConvertToSubgToolFormat().convert(edgeOutFile, fnmEdgeOutFile, properties.getProperty(VTX_FILEIN_KEY));
		}

		long t2 = System.currentTimeMillis();
		new CallSubgCount(fnmEdgeOutFile, fnmOutFile, logger, motifSize).start();
//		System.out.println("FN-RUN -> " + jobNo + " " + (System.currentTimeMillis() - t2));
	}


	private void extractLogs() {
		String extractLogsCommand = "./extractLogs.sh";
		if (OS.equalsIgnoreCase(WIN_OS))
			extractLogsCommand  = "extractLogs.bat";
		try {
			Runtime runTime = Runtime.getRuntime();
//			String[] callJSStr = { extractLogsCommand, loggerName, logFile };
			String[] callJSStr = { extractLogsCommand, "", logFile };
			Process process = runTime.exec(callJSStr);
			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}
			process.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String call_script() throws Exception {
		try {
			Runtime runTime = Runtime.getRuntime();
			String[] callJSStr = { "./callProgram.sh", String.valueOf(jobNo) };
			Process process = runTime.exec(callJSStr);
			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}
			process.waitFor();
			// System.out.println("Exe CallJS " + jobNo + "  Finished .....");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf(jobNo);
	}

}
