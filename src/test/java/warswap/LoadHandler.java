package warswap;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoadHandler extends Thread {
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			new LoadHandler().start();
		}
	}

	public void run() {
		org.apache.log4j.Logger log = Logger.getLogger(LoadHandler.class);
		Properties props = new Properties();
		props.setProperty("log4j.appender.file",
				"org.apache.log4j.RollingFileAppender");
		props.setProperty("log4j.appender.file.maxFileSize", "100MB");
		props.setProperty("log4j.appender.file.maxBackupIndex", "100");
		props.setProperty("log4j.appender.file.File",
				"D:/log4jtesting/LoadHandler_"
						+ Thread.currentThread().getName() + ".log");
		props.setProperty("log4j.appender.file.threshold", "debug");
		props.setProperty("log4j.appender.file.layout",
				"org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.file.layout.ConversionPattern",
				"%d [%t] %-5p [%-35F : %-25M : %-6L] %-C -%m%n");
		props.setProperty("log4j.rootLogger", "DEBUG, file");
		PropertyConfigurator.configure(props);
		log.info("thread started :" + Thread.currentThread().getName());
		log.debug("run method :" + Thread.currentThread().getName());

	}
}
