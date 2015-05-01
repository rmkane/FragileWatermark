package watermark.core.util;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This class includes static methods to open hyper links in the user's browser.
 *
 * @author Ryan M. Kane
 */
public class WebUtil {
	/**
	 * Opens a hyperlink in the system browser.
	 *
	 * @param uri - the URI to navigate to in a browser.
	 */
	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop()
				: null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Opens a hyperlink in the system browser.
	 *
	 * @param url - the URL to navigate to in a browser.
	 */
	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
