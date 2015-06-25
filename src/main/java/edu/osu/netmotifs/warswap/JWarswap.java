/**
 * @author mitra
 * @Date 2/12/2014
 */
package edu.osu.netmotifs.warswap;

import org.apache.log4j.Logger;

import edu.osu.netmotifs.warswap.common.DivisionByZeroException;
import edu.osu.netmotifs.warswap.common.LoadLogger;
import static edu.osu.netmotifs.warswap.common.CONF.*;

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
