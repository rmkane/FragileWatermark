package watermark.gui.util;

import java.awt.Image;

import javax.swing.ImageIcon;

import watermark.core.util.ImageUtil;

public class IconUtil {
	public static Image loadResourceImage(String iconName) {
		return IconUtil.loadResourceIcon(iconName).getImage();
	}

	public static ImageIcon loadResourceIcon(String iconName) {
		return ImageUtil.loadIcon(String.format("resources/icons/%s", iconName));
	}
}
