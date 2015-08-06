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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class CreateDirectory {
	private static Logger logger = Logger.getLogger(CreateDirectory.class);

	public static boolean createDir(String directoryPath) {
		boolean success = true;
		logger.debug("Enter path of directory to create");

		// Creating new directory in Java, if it doesn't exists
		File directory = new File(directoryPath);
		if (directory.exists()) {
			logger.debug("Directory already exists ...");
			
		} else {
			logger.debug("Directory not exists, creating now");

			success = directory.mkdir();
			if (success) {
				logger.debug("Successfully created new directory : "
						+ directoryPath);
			} else {
				logger.error("Failed to create new directory: " + directoryPath);
			}
		}
		return success;
	}

	public static boolean deleteDir(String directoryPath) {
		boolean success = true;
		// Delete directory in Java, if it doesn't exists
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			logger.debug("Directory does not exists! " + directoryPath);
		} else {
			success = directory.delete();
			try {
				FileUtils.deleteDirectory(directory);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (success) {
				logger.debug("Directory successfully deleted : "
						+ directoryPath);
			} else {
				logger.error("Failed to delete directory: " + directoryPath);
			}
		}
		return success;
	}
}
