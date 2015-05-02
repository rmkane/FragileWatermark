package watermark.gui;

import java.awt.Image;

import javax.swing.ImageIcon;

import watermark.gui.util.IconUtil;

public class AppIcons {
	public static final Image getAppImage() {
		return AppIcons.getAppImage(64);
	}

	public static final ImageIcon getAppIcon() {
		return AppIcons.getAppIcon(16);
	}

	public static final ImageIcon getAppLogo() {
		return AppIcons.getAppIcon(128);
	}

	public static final ImageIcon getEditIcon() {
		return IconUtil.loadResourceIcon("gear.png");
	}

	public static final ImageIcon getExitIcon() {
		return IconUtil.loadResourceIcon("door.png");
	}

	public static final ImageIcon getKeysIcon() {
		return IconUtil.loadResourceIcon("keys.png");
	}

	public static final ImageIcon getClearIcon() {
		return IconUtil.loadResourceIcon("clear.png");
	}

	public static final ImageIcon getEncodeIcon() {
		return IconUtil.loadResourceIcon("spy.png");
	}

	public static final ImageIcon getDecodeIcon() {
		return IconUtil.loadResourceIcon("decode.png");
	}

	public static final ImageIcon getSaveIcon() {
		return IconUtil.loadResourceIcon("save.png");
	}

	public static final ImageIcon getZoomInIcon() {
		return IconUtil.loadResourceIcon("zoom-in.png");
	}

	public static final ImageIcon getZoomOutIcon() {
		return IconUtil.loadResourceIcon("zoom-out.png");
	}

	public static final ImageIcon getZoomDefaultIcon() {
		return IconUtil.loadResourceIcon("zoom-default.png");
	}

	private static Image getAppImage(int size) {
		return AppIcons.getAppIcon(size).getImage();
	}

	private static ImageIcon getAppIcon(int size) {
		return IconUtil
				.loadResourceIcon(String.format("app-icon-%d.png", size));
	}
}
