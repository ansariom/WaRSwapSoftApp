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

import static edu.osu.netmotifs.warswap.common.CONF.*;

import java.io.File;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import cern.colt.Arrays;
import edu.osu.netmotifs.warswap.common.CONF;
import edu.osu.netmotifs.warswap.common.ConvertToSubgToolFormat;
import edu.osu.netmotifs.warswap.common.CreateDirectory;
import edu.osu.netmotifs.warswap.common.Utils;
import edu.osu.netmotifs.warswap.significance.ExtractSignificanceMotifs;


public class JWarswapMultiThread implements Callable<String> {
	private static Logger logger = Logger.getLogger(JWarswapMultiThread.class);

	// private static int cores = 4;
	// TODO read this from file
	private int noOfIterations = 100;
	private int finishedJobs = 0;
	private String outDir;
	private String signOutFile;
	private int motifSize;
	private boolean done = false;
	private String status = CONF.RUNNING_STATUS;
	private String errorMsg = "";
	private String numericalVertexFile;
	private String fanFormatEdgeOut;


	public void setSelfLoops(boolean selfLoops) {
		CONF.selfLoops = selfLoops;
	}

	public boolean isDone() {
		return done;
	}

	public void setSignOutFile(String signOutFile) {
		this.signOutFile = signOutFile;
	}

	public void setMotifSize(int motifSize) {
		this.motifSize = motifSize;
	}

	public int getFinishedJobs() {
		return finishedJobs;
	}

	public void setNoOfIterations(int noOfIterations) {
		this.noOfIterations = noOfIterations;
	}

	public JWarswapMultiThread(String eFileIn, String vFileIn, String outBase,
			String netName, int motifSize) throws Exception {
		try {
			outDir = outBase;
			setRunningMode(PC_MODE);
			numericalVertexFile = vFileIn + ".txt";
			fanFormatEdgeOut = eFileIn + ".fan.txt";
			Utils.convertToNumericalVColor(vFileIn, numericalVertexFile);
			new ConvertToSubgToolFormat().convertForWarswap(eFileIn, fanFormatEdgeOut, numericalVertexFile);
			if (!createDirectories(eFileIn, numericalVertexFile, outBase, netName, fanFormatEdgeOut, motifSize))
				return;
		} catch (Exception e) {
			 logger.error(Arrays.toString(e.getStackTrace()));
			 cleanup();
			 throw e;
		}
	}

	@Override
	public String call() {
		try {
			logger.info("Number of cores = " + CONF.cores);
			long startTime = System.currentTimeMillis();
			for (int i = 0; i <= noOfIterations; i++) {
				if (FAILE_STATUS.equalsIgnoreCase(status))
					break;
				pool.submit(new WarswapTask(i));
			}

			for (int i = 0; i <= noOfIterations; i++) {
				String result = pool.take().get();
				if (!Utils.isNumeric(result)) {
					status = CONF.FAILE_STATUS;
					errorMsg = result;
					break;
				}
				finishedJobs++;
			} 
			if (FAILE_STATUS.equalsIgnoreCase(status)) {
				cleanup();
				return errorMsg;
			}
			long endTime = System.currentTimeMillis();
			logger.info("Randomization Finished in " + (endTime - startTime));
//			System.out.println("all-> " + (endTime - startTime));
			String fnmOrigOUtFile = properties.getProperty(NETWORK_NAME_KEY)
					+ FNM_EDGE_ORIG_SUFFIX + FNM_OUT_SUFFIX;
			new ExtractSignificanceMotifs(
					Integer.valueOf(properties.get(CONF.MOTIF_SIZE_KEY).toString()), properties.getProperty(FN_OUTDIR_KEY),
					fnmOrigOUtFile, signOutFile, CONF.FN_OUT_EXTENSION).extractSubGraphsInfo();
			done = true;
			cleanup();
		} catch (Exception e) {
			logger.error(e);
			cleanup();
		} catch (Throwable e) {
			logger.error(e);
			cleanup();
		} 
		return "";
	}

	private void cleanup() {
		File file = new File(fanFormatEdgeOut);
		if (file.isFile())
			file.delete();
		file = new File(numericalVertexFile);
		if (file.isFile())
			file.delete();
		CreateDirectory.deleteDir(properties.get(CONF.NET_DIR_KEY).toString());
	}

	public String getStatus() {
		return status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void shutdownApp() {
		poolExecutor.shutdown();
	}

	public static void main(String[] args) {
		try {
			new JWarswapMultiThread(args[0], args[1], args[2], args[3], 3)
					.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// new JWarswapMultiThread(args[0], args[1], args[2], args[3])
		// .startRunning();
	}

}
