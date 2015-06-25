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

import org.apache.log4j.Logger;

import edu.osu.netmotifs.warswap.common.DivisionByZeroException;
import edu.osu.netmotifs.warswap.common.LoadLogger;
import static edu.osu.netmotifs.warswap.common.CONF.*;

public class JWarswap {
	private static Logger logger = LoadLogger.rLogger;

	/**
	 * The program starts warswap algorithm running using 3 arguments:
	 * InputEdgesFile, VerticesFile, OutputEdgesFile
	 * 
	 * @param args
	 */
	public void start() {

	}

	public static void main(String[] args) {
		String fanmodFileIn = args[0];
		String vFileIn = args[1];
		String eFileOut = args[2];
		String logFile = args[3];

		long t1 = System.currentTimeMillis();
		try {
			GraphDAO graphDAO = GraphDAO.getInstance();
			String tableName = "wrswapTable";
			graphDAO.createTable(tableName);

			LoadLogger.setLogger1(logFile, null, null);
			DrawRandGraphWithSwaps drawRandGraphWithSwaps = new DrawRandGraphWithSwaps(
					logger, vFileIn, eFileOut, tableName);

			drawRandGraphWithSwaps.loadGraph(fanmodFileIn);
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(TF_Color, TF_Color);
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(TF_Color, MIR_Color);
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(TF_Color, GENE_Color);
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(MIR_Color, TF_Color);
			drawRandGraphWithSwaps.sortedLayerDrawWithSwaps(MIR_Color, GENE_Color);
			drawRandGraphWithSwaps.printEdgesToFile();
			graphDAO.dropTable(tableName);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.info("WarSwap Finished in (millisecs) : "
				+ (System.currentTimeMillis() - t1));
		System.out.println("WarSwap Finished in (millisecs) : "
				+ (System.currentTimeMillis() - t1));
	}
}
