package gov.nist.appvet.tool.sigverifier.util;

import gov.nist.appvet.tool.sigverifier.Properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

public class LogMaintainer implements Runnable {
	
	public static final int WAIT_TIME = 86400000; // 24 hours
	private static final Logger log = Properties.log;
	
	@Override
	public void run() {
		log.info("Starting Log Maintainer...");
		for (;;) {
			// save Log
			saveLog();
			
			try {
				Thread.sleep(WAIT_TIME);
			} catch (final InterruptedException e) {
				log.error(e.toString());
			}
		}
	}
	
	public static synchronized void saveLog() {
		// Check if log has been saved per save frequency
		
		// Get current month
		java.util.Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int currentMonth = cal.get(Calendar.MONTH) + 1; // cal.get() is zero-based
		int currentDay = cal.get(Calendar.DAY_OF_MONTH);
		int currentYear = cal.get(Calendar.YEAR);
		String currentDayStr = null;
		if (currentDay >= 1 && currentDay <= 9) {
			currentDayStr = "0" + currentDay;
		} else {
			currentDayStr = currentDay + "";
		}
		String currentMonthStr = null;
		if (currentMonth >= 1 && currentMonth <= 9) {
			currentMonthStr = "0" + currentMonth;
		} else {
			currentMonthStr = currentMonth + "";
		}
		String currentYearStr = currentYear + "";

		//log.debug("CURRENT MONTH: " + currentMonthStr);
		//log.debug("CURRENT DAY: " + currentDayStr);
		//log.debug("CURRENT YEAR: " + currentYearStr);

		// Scan log files in /logs directory. The name format for log files is
		// MM-DD-YYYY_appvet_log.txt
		String logsDir = Properties.LOGS_DIR;

		File folder = new File(logsDir);
		if (!folder.exists()) {
			log.error("Sigverifier logs directory does not exist");
			return;
		}
		
		boolean logExists = false;
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				//log.debug("File " + listOfFiles[i].getName());
				String logName = listOfFiles[i].getName();
				if (logName.length() == 25) {
					String logMonth = logName.substring(0, 2);
					String logYear = logName.substring(6, 10);
					if (logMonth == null || logYear == null) {
						//log.debug("Found log with no month or year");
					} else if (currentMonthStr.equals(logMonth) && currentYearStr.equals(logYear)) {
						// Log already exists for current month so break
						//log.debug("logMonth: " + logMonth);
						//log.debug("logYear: " + logYear);
						//log.debug("Monthly log already exists");
						logExists = true;
						break;
					} else {
						//log.debug("logMonth: " + logMonth);
						//log.debug("logYear: " + logYear);
					}
				}
			} else if (listOfFiles[i].isDirectory()) {
				log.debug("Directory " + listOfFiles[i].getName());
			}
		}

		if (logExists) {
			return;
		} else {
			// Log does not exist for current month so copy appvet_log.txt to 
			// new log file 'MM-DD-YYYY_appvet_log.txt and CLEAR appvet_log.txt
			String destinationPath = logsDir + "/" + currentMonthStr + "-" + currentDayStr + "-" + currentYearStr + 
					"_appvet_log.txt";
			log.info("Copying active log to: " + destinationPath);
			String sourceLogPath = logsDir + "/log.txt";

			try {
				// Copy active log file to saved log file
				Files.copy(Paths.get(logsDir + "/log.txt"), new FileOutputStream(destinationPath));
				// Clear active log file
				//log.info("Clearing active log");
				PrintWriter pw = new PrintWriter(sourceLogPath);
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
