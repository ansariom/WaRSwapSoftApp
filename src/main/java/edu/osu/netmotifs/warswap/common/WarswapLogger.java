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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class WarswapLogger {
	private BufferedWriter loggerWriter;
	private StringBuffer logger;
	private String level = "debug";
	private String logFile;

	public WarswapLogger(String logFile, String level) {
		this.logFile = logFile;
		logger = new StringBuffer();
		this.level = level;
	}

	public void info(String message) {
		logger.append(new Date() + "-INFO : " + message + "\n");
	}

	public void debug(String message) {
		if (level.equalsIgnoreCase("info"))
			return;
		logger.append(new Date() + "-DEBUG : " + message+ "\n");
	}

	public void error(String message) {
		logger.append(new Date() + "-ERROR : " + message+ "\n");
	}

	public void error(Throwable e) {
		logger.append(new Date() + "-ERROR : " + e.getStackTrace()+ "\n");
	}

	public void closeLogger() {
		try {
			loggerWriter = new BufferedWriter(new FileWriter(new File(logFile)));
			loggerWriter.write(logger.toString());
			loggerWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
