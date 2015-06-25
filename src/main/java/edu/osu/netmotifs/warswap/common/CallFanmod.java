package edu.osu.netmotifs.warswap.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class CallFanmod {
	private String fanmodInFile;
	private String fanmodOutFile;
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

	
	public CallFanmod(String fanmodInFile, String fanmodOutFile, Logger logger) {
		this.fanmodInFile = fanmodInFile;
		this.fanmodOutFile = fanmodOutFile;
		this.logger = logger;
	}

	public void start() throws Exception {
		Runtime runTime = Runtime.getRuntime();
		try {
			String[] callJSStr = { "./fanmodm", motifsize, nsamps, full_enum,
					fanmodInFile, directed, colored_vertices, colored_edges,
					random_type, regard_vertex_colors, regard_edge_colors,
					reest_subgraph_num, nnets, nexchanges_per_edge,
					nexchange_attempts_per_edge, fanmodOutFile, outfile_format,
					create_dumpfile };
			Process process = runTime.exec(callJSStr);
			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				logger.info(line);
//				System.out.println(line);
			}
			process.waitFor();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw e;
		}

	}
	// $motifsize $nsamps $full_enum $infilename $directed $colored_vertices
	// $colored_edges $random_type $regard_vertex_colors $regard_edge_colors
	// $reest_subgraph_num $nnets $nexchanges_per_edge
	// $nexchange_attempts_per_edge $outfilename $outfile_format
	// $create_dumpfile

}
