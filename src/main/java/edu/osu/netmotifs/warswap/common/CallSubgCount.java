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

package edu.osu.netmotifs.warswap.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * 
 * This class calls an implementation of Nauty algorithm which is a 
 * command-line version of FANMOD (http://theinf1.informatik.uni-jena.de/motifs/ and 
 * Wernicke,S. and Rasche,F. (2006) FANMOD: a tool for fast network motif detection. Bioinformatics, 22, 1152–1153.)
 *  to enumerate subgraphs for different operating systems
 */
public class CallSubgCount {
	private String subgInFile;
	private String subgOutFile;
	private String motifsize = "3";
	private String nsamps = "0";
	private String full_enum = "1";
	private String directed = "1";
	private String colored_vertices = "1";
	private String colored_edges = "0";
	private String random_type = "0";
	private String regard_vertex_colors = "1";
	private String regard_edge_colors = "0";
	private String reest_subgraph_num = "0";
	private String nnets = "0";
	private String nexchanges_per_edge = "3";
	private String nexchange_attempts_per_edge = "3";
	private String outfile_format = "0";
	private String create_dumpfile = "0";
	private Logger logger;
	private String enum_subg_command_win = "enumerate_subg";
	private String enum_subg_command_linux = "./enumerate_subg";

	
	public CallSubgCount(String subgInFile, String subgOutFile, Logger logger, int motifSize) {
		this.subgInFile = subgInFile;
		this.subgOutFile = subgOutFile;
		this.logger = logger;
		this.motifsize = String.valueOf(motifSize);
	}

	public void start() throws Exception {
		String enum_subg_command = enum_subg_command_linux;
		Runtime runTime = Runtime.getRuntime();
		if (CONF.isWindows() && !CONF.getRunningMode().equalsIgnoreCase(CONF.CLUSTER_MODE))
			enum_subg_command = enum_subg_command_win;
		try {
			// Call FANMOD's command line version to enumerate sungraphs
			String[] callJSStr = { enum_subg_command , motifsize, nsamps, full_enum,
					subgInFile, directed, colored_vertices, colored_edges,
					random_type, regard_vertex_colors, regard_edge_colors,
					reest_subgraph_num, nnets, nexchanges_per_edge,
					nexchange_attempts_per_edge, subgOutFile, outfile_format,
					create_dumpfile };
			Process process = runTime.exec(callJSStr);
			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				logger.debug(line);
			}
			process.waitFor();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw e;
		}

	}
	

}
