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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;

import edu.osu.netmotifs.warswap.common.CONF;
import edu.osu.netmotifs.warswap.common.ConvertToSubgToolFormat;
import edu.osu.netmotifs.warswap.common.CreateDirectory;
import edu.osu.netmotifs.warswap.common.Utils;
import edu.osu.netmotifs.warswap.significance.ExtractSignificanceMotifs;
import static edu.osu.netmotifs.warswap.common.CONF.*;

/**
 * This API is provided to enable running of the warswap tool on cluster
 * specially for very large networks This module is using DRMAA API to
 * distribute jobs on any cluster type
 * 
 * @author mitra
 *
 */
public class JWarswapCluster {
	private static Logger logger = Logger.getLogger(JWarswapCluster.class);
	private static String numericalVertexFile;
	private static String fanFormatEdgeOut;
	/**
	 * 
	 * @param eFileIn
	 * @param vFileIn
	 * @param outBase
	 * @param netName
	 * @param numOfIterations
	 */
	public static void main(String[] args) {
		if (args.length != 9) {
			logger.error("Insufficient arguments!");
			return;
		}
		setRunningMode(CLUSTER_MODE);
		String workingDir = args[0];
		String bashFile = args[1];
		String edgeFileIn = args[2];
		String vtxFileIn = args[3];
		String outBaseDir = args[4];
		String networkName = args[5];
		int motifSize = 3;
		String motifsOutFile = args[8];

		String noOfIterations = "2500";
		try {
			Integer.valueOf(args[6]);
			motifSize = Integer.valueOf(args[6]);
			Integer.valueOf(args[7]);
			noOfIterations = args[7];
		} catch (Exception e) {
			logger.error(e.getStackTrace());
			return;
		}

		/** Create output directories */
		numericalVertexFile = vtxFileIn + ".txt";
		fanFormatEdgeOut = edgeFileIn + ".fan.txt";
		try {
			Utils.convertToNumericalVColor(vtxFileIn, numericalVertexFile);
			new ConvertToSubgToolFormat().convertForWarswap(edgeFileIn, fanFormatEdgeOut, numericalVertexFile);
		} catch (Exception e1) {
			logger.error(Arrays.toString(e1.getStackTrace()));
		}
		if (!createDirectories(edgeFileIn, numericalVertexFile, outBaseDir, networkName, fanFormatEdgeOut, motifSize))
			return;
		
		/** DRMAA Settings */
		SessionFactory factory = SessionFactory.getFactory();
		Session session = factory.getSession();

		try {
			session.init("");
			JobTemplate jt = session.createJobTemplate();
			jt.setWorkingDirectory(workingDir);
			jt.setRemoteCommand(workingDir + DIR_SEP + bashFile);
			jt.setNativeSpecification("-shell yes");
			List<String> argList = new ArrayList<String>();
			argList.add(edgeFileIn);
			argList.add(vtxFileIn);
			argList.add(outBaseDir);
			argList.add(networkName);
			argList.add(noOfIterations);
			argList.add(String.valueOf(motifSize));
			jt.setArgs(argList);

			int start = 1;
			int end = Integer.valueOf(noOfIterations) + 1;
			int step = 1;

			List ids = session.runBulkJobs(jt, start, end, step);
			Iterator i = ids.iterator();

			while (i.hasNext()) {
				logger.info("Your job has been submitted with id " + i.next());
			}

			session.deleteJobTemplate(jt);
			session.synchronize(
					Collections.singletonList(Session.JOB_IDS_SESSION_ALL),
					Session.TIMEOUT_WAIT_FOREVER, false);

			for (int count = start; count < end; count += step) {
				JobInfo info = session.wait(Session.JOB_IDS_SESSION_ANY,
						Session.TIMEOUT_WAIT_FOREVER);

				if (info.wasAborted()) {
					logger.warn("Job " + info.getJobId() + " never ran");
				} else if (info.hasExited()) {
					logger.warn("Job " + info.getJobId()
							+ " finished regularly with exit status "
							+ info.getExitStatus());
				} else if (info.hasSignaled()) {
					logger.warn("Job " + info.getJobId()
							+ " finished due to signal "
							+ info.getTerminatingSignal());
				} else {
					logger.warn("Job " + info.getJobId()
							+ " finished with unclear conditions");
				}

				logger.warn("Job Usage:");

				Map rmap = info.getResourceUsage();
				Iterator r = rmap.keySet().iterator();

				while (r.hasNext()) {
					String name = (String) r.next();
					String value = (String) rmap.get(name);
					logger.warn("  " + name + " = " + value);
				}
				
			}
			logger.info("ALL JOBS PROCESSED!" );
			String fnmOrigOUtFile = properties.getProperty(NETWORK_NAME_KEY)
					+ FNM_EDGE_ORIG_SUFFIX + FNM_OUT_SUFFIX;
			new ExtractSignificanceMotifs(
					Integer.valueOf(properties.get(CONF.MOTIF_SIZE_KEY).toString()), properties.getProperty(FN_OUTDIR_KEY),
					fnmOrigOUtFile, motifsOutFile, CONF.FN_OUT_EXTENSION).extractSubGraphsInfo();
			cleanup();
			session.exit();
		} catch (Exception e) {
			logger.error(Arrays.toString(e.getStackTrace()));
			cleanup();
			e.printStackTrace();
		}
	}
	
	private static void cleanup() {
		File file = new File(fanFormatEdgeOut);
		file.delete();
		file = new File(numericalVertexFile);
		file.delete();
//		CreateDirectory.deleteDir(properties.get(CONF.NET_DIR_KEY).toString());
	}
}
