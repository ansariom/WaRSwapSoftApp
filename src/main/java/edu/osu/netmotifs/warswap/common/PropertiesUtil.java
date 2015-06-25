package edu.osu.netmotifs.warswap.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

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

public class PropertiesUtil {
	private static Logger logger = Logger.getLogger(PropertiesUtil.class);

	public static Properties loadProperties(String fileName) {
		Properties props = new Properties();
		try {
			File file = new File(fileName);
			InputStream instream = new FileInputStream(file);
			props.load(instream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return props;
	}

	public static void saveProps(String fileName, Properties props) {
		try {
			File f = new File(fileName);
			OutputStream out = new FileOutputStream(f);
			props.store(out, "This is an optional header comment string");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	
}