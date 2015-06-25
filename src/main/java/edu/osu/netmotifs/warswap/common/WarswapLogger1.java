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

public class WarswapLogger1 {
	private BufferedWriter logger;
	private String level = "Info";

	public WarswapLogger1(String logFile, String level) {
		try {
			logger = new BufferedWriter(new FileWriter(new File(logFile)));
			this.level = level;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void info(String message) {

		try {
			logger.write(new Date() + "-INFO : " + message);
			logger.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void debug(String message) {
		if (level.equalsIgnoreCase("info"))
			return;
		try {
			logger.write(new Date() + "-DEBUG : " + message);
			logger.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void error(String message) {
		try {
			logger.write(new Date() + "-ERROR : " + message);
			logger.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void error(Throwable e) {
		try {
			logger.write(new Date() + "-ERROR : " + e.getStackTrace());
			logger.newLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void closeLogger() {
		try {
			logger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
