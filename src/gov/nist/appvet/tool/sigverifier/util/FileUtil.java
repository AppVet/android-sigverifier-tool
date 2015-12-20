/* This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 United States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 * 
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS".  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof including, but
 * not limited to, the correctness, accuracy, reliability or usefulness of
 * the software.
 * 
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement.
 */
package gov.nist.appvet.tool.sigverifier.util;

import gov.nist.appvet.tool.sigverifier.Properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.fileupload.FileItem;

/**
 * @author steveq@nist.gov
 */
public class FileUtil {
	private static final Logger log = Properties.log;

	public static boolean copyFile(File sourceFile, File destFile) {
		if (sourceFile == null || !sourceFile.exists() || destFile == null) {
			return false;
		}
		try {
			Files.copy(sourceFile.toPath(), destFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException e) {
			log.error(e.toString());
			return false;
		}
		return true;
	}

	public static boolean copyFile(String sourceFilePath,
			String destFilePath) {
		if (sourceFilePath == null || destFilePath == null) {
			return false;
		}
		File sourceFile = new File(sourceFilePath);
		if (!sourceFile.exists()) {
			return false;
		}
		File destFile = new File(destFilePath);
		try {
			Files.copy(sourceFile.toPath(), destFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException e) {
			log.error(e.toString());
			return false;
		} finally {
			sourceFile = null;
			destFile = null;
		}
		return true;
	}

	public static boolean deleteDirectory(File file) {
		if (file == null) {
			return false;
		}
		if (file.exists()) {
			for (final File f : file.listFiles()) {
				if (f.isDirectory()) {
					deleteDirectory(f);
					f.delete();
				} else {
					f.delete();
				}
			}
			return file.delete();
		}
		return false;
	}

	public static boolean deleteFile(String sourceFilePath) {
		File file = new File(sourceFilePath);
		try {
			if (file.exists()) {
				return file.delete();
			} else {
				log.error("Cannot find file '" + sourceFilePath + "' to delete");
				return false;
			}
		} finally {
			file = null;
		}
	}

	/**
	 * Remove the prepended path of the file name.
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		int lastBackSlash = filePath.lastIndexOf("\\");
		int lastForwardSlash = filePath.lastIndexOf("/");
		if (lastBackSlash == -1 && lastForwardSlash == -1)
			// No slashes found in file path
			return filePath;
		else if (lastBackSlash >= 0)
			// Back slash detected
			return filePath.substring(lastBackSlash + 1, filePath.length());
		else
			// Forward slash detected
			return filePath.substring(lastForwardSlash + 1, filePath.length());
	}

	public static boolean saveFileUpload(FileItem fileItem, String filePath) {
		try {
			if (fileItem == null) {
				log.error("File item is NULL");
				return false;
			}
			File file = new File(filePath);
			fileItem.write(file);
			log.debug("Saved " + filePath);
			return true;
		} catch (IOException e) {
			log.error(e.toString());
			return false;
		} catch (Exception e) {
			log.error(e.toString());
			return false;
		}
	}

	public static String replaceSpaceWithUnderscore(String str) {
		return str.replaceAll(" ", "_");
	}

	public static boolean saveReport(String reportContent,
			String reportFilePath) {
		PrintWriter out;
		try {
			out = new PrintWriter(reportFilePath);
			out.println(reportContent);
			out.flush();
			out.close();
			log.debug("Saved " + reportFilePath);
			return true;
		} catch (FileNotFoundException e) {
			log.error(e.toString());
			return false;
		}
	}

	private FileUtil() {
	}
}
