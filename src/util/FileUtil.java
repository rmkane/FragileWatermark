package util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * This class is responsible for handling common file operations.
 * 
 * @author Ryan M. Kane
 */
public class FileUtil {
	/**
	 * Opens a file in the User's default file editor.
	 * 
	 * @param file - the file to edit.
	 * @return whether the action succeeded without error.
	 */
	public static boolean editFile(File file) {
		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
				Runtime.getRuntime().exec(cmd);
			} else {
				Desktop.getDesktop().edit(file);
			}
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Opens a file in the User's default file editor.
	 * 
	 * @param filename - the filename for the file to edit.
	 * @return whether the action succeeded without error.
	 */
	public static boolean editFile(String filename) {
		return editFile(loadFile(FileUtil.class, filename));
	}
	
	/**
	 * Loads a file.
	 *
	 * @param clazz - to use correct class path.
	 * @param filename - the properties File filename.
	 * @return a Properties object.
	 */
	public static File loadFile(Class<?> clazz, String filename) {
		File file = null;
		
		// First try loading from file system.
		try {
			file = new File(filename);
		} catch (Exception e) {
		}

		try {
			if (!file.isFile() || !file.canRead()) {
				// If not found, try loading from classpath
				return new File(clazz.getResource(filename).getFile());
			}
		} catch (Exception e) {
		}

		return null;
	}
	
	/**
	 * Loads a Properties object for a given file.
	 *
	 * @param clazz - to use correct class path.
	 * @param filename - the properties File filename.
	 * @return a Properties object.
	 */
	public static Properties loadProperties(Class<?> clazz, String filename) {
		Properties props = new Properties();
		InputStream is = null;

		// First try loading from the file system.
		try {
			File f = new File(filename);
			is = new FileInputStream(f);
		} catch (Exception e) {
			is = null;
		}

		try {
			if (is == null) {
				// Try loading from classpath.
				is = clazz.getResourceAsStream(filename);
			}
			// Try loading properties from the file (if found)
			props.load(is);
		} catch (Exception e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}

		return props;
	}

	/**
	 * Saves a map of configuration properties to a properties file.
	 *
	 * @param propMap - key-value pairs of properties
	 * @param filename - the filename to save the properties to.
	 * @param description - a description of the property list.
	 */
	public static void saveProperties(Map<String, String> propMap, String filename, String description) {
		try {
			Properties props = new Properties();

			for (Map.Entry<String, String> entry : propMap.entrySet()) {
				props.setProperty(entry.getKey(), entry.getValue());
			}

			File f = new File(filename);
			OutputStream out = new FileOutputStream(f);

			props.store(out, description);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
