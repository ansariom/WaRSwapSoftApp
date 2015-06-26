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

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CONF implements Serializable {
	/**
	 * 
	 */
	private static CONF conf;

	public static CONF getInstance() {
		if (conf == null)
			conf = new CONF();
		return conf;
	}

	private static final long serialVersionUID = -1042536051405603347L;

//	public static int cores = Runtime.getRuntime().availableProcessors();
	public static int cores = 1;

	static {
		if (cores == 1)
			cores = 2;
	}
	public static ExecutorService poolExecutor = Executors
			.newFixedThreadPool(cores);
	public static CompletionService<String> pool = new ExecutorCompletionService<String>(
			poolExecutor);

	public static final String loggerName = "JOB_";
	public final static char DIR_SEP = File.separatorChar;
	public final static String U_LINE = "_";
	public static final String CLUSTER_MODE = "CLUSTER";
	public static final String PC_MODE = "PC";
	public static final String WIN_OS = "WINDOWS";
	public static final String LINUX_OS = "LINUX";
	public static final String RUNNING_MODE = PC_MODE;
	public static final String level = "INFO";

	public static final String ERROR_MSG_TYPE = "Error";
	public static final String INFO_MSG_TYPE = "INFO";
	// public static final String level = "DEBUG";
	public static byte TF_Color = 0;
	public static byte MIR_Color = 1;
	public static byte GENE_Color = 2;
	public static byte SL_Color = 3;

	private static String OSName = System.getProperty("os.name").toLowerCase();

	public static boolean isWindows() {
		return (OSName.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		return (OSName.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (OSName.indexOf("nix") >= 0 || OSName.indexOf("nux") >= 0 || OSName
				.indexOf("aix") >= 0);
	}

	public static boolean isSolaris() {
		return (OSName.indexOf("sunos") >= 0);
	}

	// public static final String OS = LINUX_OS;
	public static final String OS = WIN_OS;

	public static final String OUT_DIR_NAME = "output";
	public static final String JGRAPH_SUFFIX = ".jgraph";
	public static final String FNM_EDGE_SUFFIX = ".subg";
	public static final String FNM_OUT_SUFFIX = ".subg.out";
	public static final String LOG_SUFFIX = ".jgraph.log";
	public static final String JGRAPH_EDGE_OUT_DIR_SUFFIX = ".rand.jgraph";
	public static final String JGRAPH_LOG_OUT_DIR_SUFFIX = ".rand.jgraph_out";
	public static final String SUBENUM_OUT_DIR_SUFFIX = ".rand.subg_subgraphs";
	public static final String EDGE_VTX_COLOR_OUT_DIR_SUFFIX = ".rand.subg";
	public static final String FNM_EDGE_ORIG_SUFFIX = ".ORIG";
	public static final String NET_DIR_KEY = "networkDir";
	public static final String FN_EOUTDIR_KEY = "subenumEdgeOutDir";
	public static final String SUBENUM_OUTDIR_KEY = "subenumOutDir";
	public static final String WR_EOUTDIR_KEY = "jgraphEdgeOutDir";
	public static final String WR_OUTDIR_KEY = "jgraphOutDir";
	public static final String PREFIX_KEY = "prefix";
	public static final String EDGE_VTX_COLOR_FILE_KEY = "edggVtxColorFileIn";
	public static final String PROP_FILE_NAME = "config.properties";
	public static final String TF_STR = "TF";
	public static final String MIR_STR = "MIR";
	public static final String MOTIF_SIZE_KEY = "MOTIF_SIZE";
	public static final String GENE_STR = "GENE";

	public static Properties properties = new Properties();
	public static final String NETWORK_NAME_KEY = "networkName";
	public static final String EDGE_FILEIN_KEY = "edgeFileIn";
	public static final String VTX_FILEIN_KEY = "vtxFileIn";
	private static final String RUNNING_MODE_KEY = "runningMode";

	public static final String RUNNING_STATUS = "Running";

	public static final String FAILE_STATUS = "Failed";

	public static final String FN_OUT_EXTENSION = ".subg.out";

	public static final String INFINIT = "Inf";

	public static final String MOTIFS_OUT_DIR = "motifs_output";

	public static final String NEWLINE = "\n";

	public static boolean selfLoops = true;

	public static final String MOTIFS_OUT_FILE_NAME = "motifs.OUT";
	public static final String MOTIFS_HTML_OUT_FILE_NAME = "motifs.html";

	public static void setRunningMode(String runningMode) {
		properties.setProperty(RUNNING_MODE_KEY, runningMode);
	}

	public static String getRunningMode() {
		return (String) properties.get(RUNNING_MODE_KEY);
	}

	/**
	 * creates required directories to store intermediate files
	 * 
	 * @author mitra
	 */
	public static boolean createDirectories(String eFileIn, String vFileIn,
			String outBase, String networkName, String edgVtxColorFile,
			int motifSize) {
		properties.setProperty(EDGE_VTX_COLOR_FILE_KEY, edgVtxColorFile);
		properties.setProperty(EDGE_FILEIN_KEY, eFileIn);
		properties.setProperty(VTX_FILEIN_KEY, vFileIn);
		properties.setProperty(MOTIF_SIZE_KEY, String.valueOf(motifSize));
		properties.setProperty(NETWORK_NAME_KEY, networkName);

		String netDirectory = outBase + DIR_SEP + networkName;
		properties.setProperty(NET_DIR_KEY, netDirectory);
		CreateDirectory.createDir(netDirectory);

		String EdgVtxColorFilesOutDir = netDirectory + DIR_SEP + networkName
				+ EDGE_VTX_COLOR_OUT_DIR_SUFFIX;
		properties.setProperty(FN_EOUTDIR_KEY, EdgVtxColorFilesOutDir);
		CreateDirectory.createDir(EdgVtxColorFilesOutDir);

		String subEnumOutDir = netDirectory + DIR_SEP + networkName
				+ SUBENUM_OUT_DIR_SUFFIX;
		properties.setProperty(SUBENUM_OUTDIR_KEY, subEnumOutDir);
		CreateDirectory.createDir(subEnumOutDir);

		String jgraphEdgeOutDir = netDirectory + DIR_SEP + networkName
				+ JGRAPH_EDGE_OUT_DIR_SUFFIX;
		properties.setProperty(WR_EOUTDIR_KEY, jgraphEdgeOutDir);
		CreateDirectory.createDir(jgraphEdgeOutDir);

		String jgraphOutDir = netDirectory + DIR_SEP + networkName
				+ JGRAPH_LOG_OUT_DIR_SUFFIX;
		properties.setProperty(WR_OUTDIR_KEY, jgraphOutDir);
		CreateDirectory.createDir(jgraphOutDir);

		String prefix = networkName + ".rand.";
		properties.setProperty(PREFIX_KEY, prefix);
		PropertiesUtil.saveProps(PROP_FILE_NAME, properties);

		return true;
	}

	public static void loadProps() {
		properties = PropertiesUtil.loadProperties(PROP_FILE_NAME);
	}

}
