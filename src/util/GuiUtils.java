package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GuiUtils {
	public static JPanel addImagePanel(JComponent parent, String label, int height, int width) {
		JPanel imgPanel = new JPanel();
		JPanel srcPanelOuter = new JPanel();
		srcPanelOuter.setLayout(new BorderLayout());
		JLabel srcLabel = new JLabel(label, SwingConstants.CENTER);
		Font lblFont = srcLabel.getFont();
		srcLabel.setFont(lblFont.deriveFont(lblFont.getStyle() | Font.BOLD));
		imgPanel = new JPanel();
		imgPanel.setPreferredSize(new Dimension(height, width));
		imgPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		srcPanelOuter.add(srcLabel, BorderLayout.NORTH);
		srcPanelOuter.add(imgPanel, BorderLayout.CENTER);
		parent.add(srcPanelOuter, BorderLayout.NORTH);
		return imgPanel;
	}

	public static void drawImageOnPanel(JPanel panel, BufferedImage image, int padding) {
		int panelWidth = panel.getWidth();
		int panelHeight = panel.getHeight();
		int maxWidth = panelWidth - padding * 2;
		int maxHeight = panelHeight - padding * 2;
		BufferedImage scaledImage = ImageUtil.scaleToFit(image, maxWidth, maxHeight, true);
		int imgWidth = scaledImage.getWidth();
		int imgHeight = scaledImage.getHeight();
		int xPos = (int) (panelWidth / 2.0 - imgWidth / 2.0);
		int yPos = (int) (panelHeight / 2.0 - imgHeight / 2.0);
		Graphics g = panel.getGraphics();

		g.setColor(Color.BLACK);
		g.clearRect(0, 0, panelWidth, panelHeight);
		g.drawRect(0, 0, panelWidth-1, panelHeight-1);
		g.drawImage(scaledImage, xPos, yPos, imgWidth, imgHeight, null);
		g.dispose();
	}

	// =========================================================================
	// Menu Creation
	// =========================================================================
	public static JMenu createMenu(String label, int mnemonic, String description) {
		return createMenuItem(new JMenu(label), mnemonic, description, null);
	}

	public static JMenuItem createMenuItem(String label, int mnemonic, String description, ActionListener action) {
		return createMenuItem(new JMenuItem(label), mnemonic, description, action);
	}

	public static <T extends JMenuItem> T createMenuItem(T source, int mnemonic, String description, ActionListener action) {
		source.setMnemonic(mnemonic);
		source.getAccessibleContext().setAccessibleDescription(description);
		source.addActionListener(action);
		return source;
	}

	// =========================================================================
	// Alerts/Message Dialogs
	// =========================================================================
	public static void showErrorMessage(Component parentComponent, String message, String title) {
		showMessage(parentComponent, title, message, JOptionPane.ERROR_MESSAGE);
	}

	public static void showErrorMessage(Component parentComponent, String message) {
		showErrorMessage(parentComponent, message, "Error");
	}

	public static void showErrorMessage(String message) {
		showErrorMessage(null, message);
	}

	public static void showSuccessMessage(Component parentComponent, String message, String title) {
		showMessage(parentComponent, title, message, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showSuccessMessage(Component parentComponent, String message) {
		showSuccessMessage(parentComponent, message, "Success");
	}

	public static void showSuccessMessage(String message) {
		showSuccessMessage(null, message);
	}

	public static void showMessage(Component parentComponent, String title, String message, int messageType) {
		JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
	}
}
