package edu.osu.netmotifs.warswap.common;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	public static int cores = Runtime.getRuntime().availableProcessors();
	
	static{
		if (cores == 1)
			cores = 2;
	}
//	 public static int cores = 1;
	public static ExecutorService poolExecutor = Executors.newFixedThreadPool(cores);
	public static CompletionService<String> pool = new ExecutorCompletionService<String>(poolExecutor); 

	public static final String loggerName = "JOB_";
	public final static char DIR_SEP = File.separatorChar;
	public final static String U_LINE = "_";
	public static final String CLUSTER_MODE = "CLUSTER";
	public static final String PC_MODE = "PC";
	public static final String WIN_OS = "WINDOWS";
	public static final String LINUX_OS = "LINUX";
	public static final String RUNNING_MODE = PC_MODE;
	public static final String level = "INFO";
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

//	public static final String OS = LINUX_OS;
	public static final String OS = WIN_OS;
	
	public static final String OUT_DIR_NAME = "output";
	public static final String JGRAPH_SUFFIX = ".jgraph";
	public static final String FNM_EDGE_SUFFIX = ".subg";
	public static final String FNM_OUT_SUFFIX = ".subg.out";
	public static final String LOG_SUFFIX = ".jgraph.log";
	public static final String JGRAPH_EDGE_OUT_DIR_SUFFIX = ".rand.jgraph";
	public static final String JGRAPH_LOG_OUT_DIR_SUFFIX = ".rand.jgraph_out";
	public static final String FNM_OUT_DIR_SUFFIX = ".rand.subg_subgraphs";
	public static final String FNM_EDGE_OUT_DIR_SUFFIX = ".rand.subg";
	public static final String FNM_EDGE_ORIG_SUFFIX = ".ORIG";
	public static final String NET_DIR_KEY = "networkDir";
	public static final String FN_EOUTDIR_KEY = "subgEdgeOutDir";
	public static final String FN_OUTDIR_KEY = "subgOutDir";
	public static final String WR_EOUTDIR_KEY = "jgraphEdgeOutDir";
	public static final String WR_OUTDIR_KEY = "jgraphOutDir";
	public static final String PREFIX_KEY = "prefix";
	public static final String FNM_FILEIN_KEY = "subgFileIn";
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

	public static boolean selfLoops = true;

	public static String MOTIFS_OUT_FILE_NAME = "motifs.OUT";

	public static void setRunningMode(String runningMode) {
		properties.setProperty(RUNNING_MODE_KEY, runningMode);
	}

	public static String getRunningMode() {
		return (String) properties.get(RUNNING_MODE_KEY);
	}

	public static boolean createDirectories(String eFileIn, String vFileIn,
			String outBase, String networkName, String fnFileIn, int motifSize) {
		properties.setProperty(FNM_FILEIN_KEY, fnFileIn);
		properties.setProperty(EDGE_FILEIN_KEY, eFileIn);
		properties.setProperty(VTX_FILEIN_KEY, vFileIn);
		properties.setProperty(MOTIF_SIZE_KEY, String.valueOf(motifSize));
		properties.setProperty(NETWORK_NAME_KEY, networkName);

		String netDirectory = outBase + DIR_SEP + networkName;
		properties.setProperty(NET_DIR_KEY, netDirectory);
		CreateDirectory.createDir(netDirectory);
		
		String fnEdgeOutDir = netDirectory + DIR_SEP + networkName
				+ FNM_EDGE_OUT_DIR_SUFFIX;
		properties.setProperty(FN_EOUTDIR_KEY, fnEdgeOutDir);
		CreateDirectory.createDir(fnEdgeOutDir);

		String fnOutDir = netDirectory + DIR_SEP + networkName
				+ FNM_OUT_DIR_SUFFIX;
		properties.setProperty(FN_OUTDIR_KEY, fnOutDir);
		CreateDirectory.createDir(fnOutDir);

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

//	public boolean createDirectories(String subgFileIn, String vFileIn,
//			String outBase, String networkName) {
//		properties.setProperty(FNM_FILEIN_KEY, subgFileIn);
//		properties.setProperty(VTX_FILEIN_KEY, vFileIn);
//		properties.setProperty(NETWORK_NAME_KEY, networkName);
//		String netDirectory = outBase + DIR_SEP + networkName;
//		properties.setProperty(NET_DIR_KEY, netDirectory);
//		if (!CreateDirectory.createDir(netDirectory))
//			return false;
//		String subgEdgeOutDir = netDirectory + DIR_SEP + networkName
//				+ FNM_EDGE_OUT_DIR_SUFFIX;
//		properties.setProperty(FN_EOUTDIR_KEY, subgEdgeOutDir);
//		if (!CreateDirectory.createDir(subgEdgeOutDir))
//			return false;
//		String subgOutDir = netDirectory + DIR_SEP + networkName
//				+ FNM_OUT_DIR_SUFFIX;
//		properties.setProperty(FN_OUTDIR_KEY, subgOutDir);
//		if (!CreateDirectory.createDir(subgOutDir))
//			return false;
//		String jgraphEdgeOutDir = netDirectory + DIR_SEP + networkName
//				+ JGRAPH_EDGE_OUT_DIR_SUFFIX;
//		properties.setProperty(WR_EOUTDIR_KEY, jgraphEdgeOutDir);
//		if (!CreateDirectory.createDir(jgraphEdgeOutDir))
//			return false;
//		String jgraphOutDir = netDirectory + DIR_SEP + networkName
//				+ JGRAPH_LOG_OUT_DIR_SUFFIX;
//		properties.setProperty(WR_OUTDIR_KEY, jgraphOutDir);
//		if (!CreateDirectory.createDir(jgraphOutDir))
//			return false;
//		String prefix = networkName + ".rand.";
//		properties.setProperty(PREFIX_KEY, prefix);
//		PropertiesUtil.saveProps(PROP_FILE_NAME, properties);
//		return true;
//	}
//
	public static void loadProps() {
//		if (RUNNING_MODE.equalsIgnoreCase(CLUSTER_MODE))
			properties = PropertiesUtil.loadProperties(PROP_FILE_NAME);
	}

}
