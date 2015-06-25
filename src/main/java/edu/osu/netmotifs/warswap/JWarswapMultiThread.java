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
