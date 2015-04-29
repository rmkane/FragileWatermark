package util;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class GuiUtils {
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
