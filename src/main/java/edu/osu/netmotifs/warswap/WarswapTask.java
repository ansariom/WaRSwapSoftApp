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

import static edu.osu.netmotifs.warswap.common.CONF.DIR_SEP;
import static edu.osu.netmotifs.warswap.common.CONF.EDGE_FILEIN_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.EDGE_VTX_COLOR_FILE_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.FNM_EDGE_ORIG_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.FNM_EDGE_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.FNM_OUT_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.FN_EOUTDIR_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.GENE_Color;
import static edu.osu.netmotifs.warswap.common.CONF.JGRAPH_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.LOG_SUFFIX;
import static edu.osu.netmotifs.warswap.common.CONF.MIR_Color;
import static edu.osu.netmotifs.warswap.common.CONF.MOTIF_SIZE_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.NETWORK_NAME_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.OS;
import static edu.osu.netmotifs.warswap.common.CONF.PREFIX_KEY;
import static edu.osu.netmotifs.warswap.common.CONF.SUBENUM_OUTDIR_KEY;
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

import edu.osu.netmotifs.subenum.CallEnumerateSubGraphs;
import edu.osu.netmotifs.warswap.common.CONF;
import edu.osu.netmotifs.warswap.common.ConvertToSubgToolFormat;
import edu.osu.netmotifs.warswap.common.LoadLogger;
import edu.osu.netmotifs.warswap.common.ThreadLogger;

 public class WarswapTask implements Callable<String> {

	private int jobNo = 1;
	private String edgeOutFile;
	private String edgeVtxColorFile;
	private String subenumResultFile;
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
				edgeVtxColorFile = properties.getProperty(FN_EOUTDIR_KEY)
						+ DIR_SEP + properties.getProperty(NETWORK_NAME_KEY)
						+ FNM_EDGE_ORIG_SUFFIX + FNM_EDGE_SUFFIX;
				subenumResultFile = properties.getProperty(SUBENUM_OUTDIR_KEY) + DIR_SEP
						+ properties.getProperty(NETWORK_NAME_KEY)
						+ FNM_EDGE_ORIG_SUFFIX + FNM_OUT_SUFFIX;
			} else {
				edgeVtxColorFile = properties.getProperty(FN_EOUTDIR_KEY)
						+ DIR_SEP + properties.getProperty(PREFIX_KEY) + jobNo
						+ FNM_EDGE_SUFFIX;
				subenumResultFile = properties.getProperty(SUBENUM_OUTDIR_KEY) + DIR_SEP
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
			drawRandGraphWithSwaps.loadGraph(properties.getProperty(EDGE_VTX_COLOR_FILE_KEY));
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
			ConvertToSubgToolFormat.convertToEdgVtxColorFileFormat(properties.getProperty(EDGE_FILEIN_KEY), edgeVtxColorFile, properties.getProperty(VTX_FILEIN_KEY));
		} else {
			ConvertToSubgToolFormat.convertToEdgVtxColorFileFormat(edgeOutFile, edgeVtxColorFile, properties.getProperty(VTX_FILEIN_KEY));
		}
		
		// Call subenum to enumerate subgraphs and store results
		new CallEnumerateSubGraphs(motifSize, edgeVtxColorFile, subenumResultFile, 1);
//		new CallSubgCount(edgeVtxColorFile, subenumResultFile, logger, motifSize).start();
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
