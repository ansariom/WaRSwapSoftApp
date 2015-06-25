package edu.osu.netmotifs.warswap.common;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

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

public class LoadLogger {
	public static Logger rLogger = Logger.getRootLogger();
	public static String appenderName = "A3";
	
	public static synchronized void setLogger1(String fileName, String level, String pattern) {
		try {
//			System.out
//					.println("setlogger1(fileName,level,pattern) in LoadLogger.java ---> Called");

			if (level != null) {
//				System.out.println("setting level");
				if (level.trim().equalsIgnoreCase("info"))
					Logger.getRootLogger().setLevel(Level.INFO);
				else if (level.trim().equalsIgnoreCase("DEBUG"))
					Logger.getRootLogger().setLevel(Level.DEBUG);
				else if (level.trim().equalsIgnoreCase("ERROR"))
					Logger.getRootLogger().setLevel(Level.ERROR);
				else if (level.trim().equalsIgnoreCase("WARN"))
					Logger.getRootLogger().setLevel(Level.WARN);
				else if (level.trim().equalsIgnoreCase("FATAL"))
					Logger.getRootLogger().setLevel(Level.FATAL);
				else if (level.trim().equalsIgnoreCase("ALL"))
					Logger.getRootLogger().setLevel(Level.ALL);
				else if (level.trim().equalsIgnoreCase("OFF"))
					Logger.getRootLogger().setLevel(Level.OFF);

			}

			if (Logger.getRootLogger().getAppender(appenderName) != null) {
				RollingFileAppender r = new RollingFileAppender();
				r = (RollingFileAppender) Logger.getRootLogger().getAppender(
						appenderName);
//				System.out.println(" Appender Name  = " + r.getName());
				if (r.getFile() != null) {
//					System.out.println(" File           = " + r.getFile());
				} else {
//					System.out.println(" File           =  NULL");
				}
//				System.out.println(" Layout         = "
//						+ r.getLayout().getClass().getName());
				PatternLayout p = new PatternLayout();
				p = (PatternLayout) r.getLayout();
//				System.out.println("  ConversionPattern =  "
//						+ p.getConversionPattern());
//				System.out
//						.println("  Threshold         =  " + r.getThreshold());
				Logger.getRootLogger().removeAppender(
						Logger.getRootLogger().getAppender(appenderName));

				PatternLayout p1 = new PatternLayout();

				if (pattern != null) {
					p1.setConversionPattern(pattern);
					r.setLayout(p1);
				}

				if (fileName != null) {
					r.setFile(fileName);
//					System.out
//							.println(" setting fileName     = " + r.getFile());
				}
				r.activateOptions();
				Logger.getRootLogger().addAppender(r);
			}

//			System.out
//					.println("setlogger1(fileName,level,pattern) in LoadLogger.java ---> Exited");
			 rLogger.debug("Load log4j successfully................");
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println(" exception in setlogger1() in LoadLogger.java  "
							+ e);
		}

	}
}
