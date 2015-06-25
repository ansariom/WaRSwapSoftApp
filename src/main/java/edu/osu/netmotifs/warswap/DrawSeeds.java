package edu.osu.netmotifs.warswap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

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
public class DrawSeeds {
	private static Logger logger = Logger.getLogger(DrawSeeds.class);

	/**
	 * Draw List of seeds for further use in FANMOD
	 * @param numberOfSeeds
	 * @param seedOutFile
	 */
	public void generateSeeds(int numberOfSeeds, String seedOutFile) {
		List<Integer> seedsList = new ArrayList<Integer>();
		int startingSeed = Math.abs(new Random().nextInt());
		Random random = new Random(startingSeed);
		for (int i = 0; i < numberOfSeeds; i++) {
			seedsList.add(Math.abs(random.nextInt()));
		}
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(
					seedOutFile)));
			for (Integer seed : seedsList) {
				out.write(String.valueOf(seed));
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			logger.error(e.getStackTrace());
		}

	}
}
