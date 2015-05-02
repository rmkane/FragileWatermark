package watermark.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import watermark.core.util.FileUtil;
import watermark.core.util.GuiUtils;
import watermark.gui.AppConfig;
import watermark.gui.AppIcons;

public class EditFileDialog extends JDialog {
	private static final long serialVersionUID = 6629613323587008928L;

	private static final int PAD;
	private static final Color BORDER_COLOR;
	private static final Border PAD_BORDER;
	private static final Font FONT;
	private static final boolean LINE_WRAP;

	private static final float FONT_SIZE = 12f;
	private static final float FONT_SIZE_MIN = 10f;
	private static final float FONT_SIZE_MAX = 24f;

	static {
		PAD = 8;
		BORDER_COLOR = new Color(0xFFD7D7D7, true);
		PAD_BORDER = BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD);
		FONT = new Font("courier new", Font.PLAIN, (int) FONT_SIZE);
		LINE_WRAP = true;
	}

	private int width;
	private int height;
	private String[] reqProps;

	private JTextArea txtArea;
	private JScrollPane txtAreaScroll;
	private TextLineNumber textLineNumber;
	private JToolBar toolBar;
	private JButton saveButton;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JButton zoomDefButton;

	private String resourceName;

	public EditFileDialog(Frame frameOwner, int width, int height, String resourceName, String[] reqProps) {
		this(frameOwner, width, height);

		// Load the resource.
		this.loadFile(resourceName);
		this.reqProps = reqProps;
	}

	public EditFileDialog(Frame frameOwner, int width, int height) {
		super(frameOwner);

		this.width = width;
		this.height = height;


		this.initComponent();
		this.addChildren();
		this.setIconImage(AppIcons.getAppImage());
		this.setTitle("No file loaded");
		this.setLocationByPlatform(true);
		this.pack();
	}

	protected void initComponent() {
		toolBar = new JToolBar("Still draggable");
		saveButton = createButton(new SaveAction(), AppIcons.getSaveIcon(), "Save");
		zoomInButton = createButton(new ZoomInAction(), AppIcons.getZoomInIcon(), "Zoom In (Ctrl + Mouse Wheel Up)");
		zoomOutButton = createButton(new ZoomOutAction(), AppIcons.getZoomOutIcon(), "Zoom Out (Ctrl + Mouse Wheel Down)");
		zoomDefButton = createButton(new ZoomDefaultAction(), AppIcons.getZoomDefaultIcon(), "Zoom Default");

		txtArea = new JTextArea();
		txtAreaScroll = new JScrollPane();
		textLineNumber = new TextLineNumber(txtArea);

		txtArea.setAutoscrolls(true);
		txtArea.setPreferredSize(new Dimension(width, height));
		txtArea.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		txtArea.setFont(FONT);
		txtArea.setLineWrap(LINE_WRAP);
		txtArea.addMouseWheelListener(new ZoomMouseWheelListener());

		txtAreaScroll.setViewportView(txtArea);
		txtAreaScroll.setAutoscrolls(true);

		txtAreaScroll.setBorder(PAD_BORDER);
		txtAreaScroll.setRowHeaderView(textLineNumber);

		// Add Children
		this.setLayout(new BorderLayout());
	}

	protected void addChildren() {
		toolBar.add(saveButton);
		toolBar.add(zoomInButton);
		toolBar.add(zoomOutButton);
		toolBar.add(zoomDefButton);

		this.add(toolBar, BorderLayout.PAGE_START);
		this.add(txtAreaScroll, BorderLayout.CENTER);
	}

	public void loadFile(String resourceName) {
		if (resourceName == this.resourceName) {
			GuiUtils.showErrorMessage(String.format("The resource is already loaded: %s%n", resourceName));
			return;
		}

		this.loadResource(resourceName);
	}

	public void reloadResource() {
		loadResource(this.resourceName);
	}

	public void loadResource(String resourceName) {
		this.resourceName = resourceName;
		this.txtArea.setText(FileUtil.loadResourceFileText(resourceName));
		this.setTitle("Editing: " + resourceName);
	}

	public void launch() {
		// If not already visible, make visible and reload the file.
		if (!this.isVisible()) {
			this.reloadResource();
			this.setVisible(true);
		}

		// Request focus on text area.
		this.txtArea.requestFocusInWindow();
	}

	public void setReqProps(String[] reqProps) {
		this.reqProps = reqProps;
	}

	private JButton createButton(Action action, Icon icon, String toolTipText) {
		JButton button = new JButton(action);

		button.setIcon(icon);
		button.setToolTipText(toolTipText);
		button.setBorderPainted(false);
		button.setFocusPainted(false);

		return button;
	}

	private void handleZoomIn() {
		float size = txtArea.getFont().getSize();

		if (size < FONT_SIZE_MAX) {
			txtArea.setFont(txtArea.getFont().deriveFont(size += 1f));

			if (!zoomOutButton.isEnabled()) {
				zoomOutButton.setEnabled(true);
			}
		}

		if (size == FONT_SIZE_MAX) {
			zoomInButton.setEnabled(false);
		}
	}

	private void handleZoomOut() {
		float size = txtArea.getFont().getSize();

		if (size > FONT_SIZE_MIN) {
			txtArea.setFont(txtArea.getFont().deriveFont(size -= 1f));

			if (!zoomInButton.isEnabled()) {
				zoomInButton.setEnabled(true);
			}
		}

		if (size == FONT_SIZE_MIN) {
			zoomOutButton.setEnabled(false);
		}
	}

	private void handleZoomDefault() {
		txtArea.setFont(txtArea.getFont().deriveFont(FONT_SIZE));

		if (!zoomInButton.isEnabled()) {
			zoomInButton.setEnabled(true);
		}

		if (!zoomOutButton.isEnabled()) {
			zoomOutButton.setEnabled(true);
		}
	}

	private class SaveAction extends AbstractAction {
		private static final long serialVersionUID = -779576692147872538L;

		public SaveAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String rawText = txtArea.getText();

			if (!validateProperties(rawText)) {
				GuiUtils.showErrorMessage("Properties invalid. Please check for errors.");
				return;
			}

			if (!FileUtil.fileExists(resourceName)) {
				GuiUtils.showMessage(null, AppConfig.APP_TITLE,
						"File does not exist, creating new file.",
						JOptionPane.INFORMATION_MESSAGE);
			}

			FileUtil.writeConfig(rawText, resourceName);

			if (!FileUtil.fileExists(resourceName)) {
				GuiUtils.showErrorMessage("Save failed. Please check directory permissions.");
			} else {
				GuiUtils.showSuccessMessage("Wrote configuration to: " + resourceName);
			}
		}
	}

	public boolean validateProperties(String text) {
		try {
			Properties props = new Properties();
			InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
			props.load(stream);

			if (props == null || props.isEmpty()) {
				GuiUtils.showErrorMessage("Properties are missing!");
				return false;
			}

			for (String prop : reqProps) {
				if (!props.containsKey(prop)) {
					GuiUtils.showErrorMessage("Property is missing: " + prop);
					return false;
				}
			}

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private class ZoomInAction extends AbstractAction {
		private static final long serialVersionUID = -4077929805352920410L;

		public ZoomInAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			handleZoomIn();
		}
	}

	private class ZoomOutAction extends AbstractAction {
		private static final long serialVersionUID = 318723677338959431L;

		public ZoomOutAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			handleZoomOut();
		}
	}

	private class ZoomDefaultAction extends AbstractAction {
		private static final long serialVersionUID = 318723677338959431L;

		public ZoomDefaultAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			handleZoomDefault();
		}
	}

	private class ZoomMouseWheelListener implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.isControlDown()) {
				if (e.getWheelRotation() < 0)  {
					handleZoomIn();
				}else{
					handleZoomOut();
				}
			}
		}
	}

	/**
	 * Driver.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFrame mainFrame = new JFrame("Resource Editor");
				final JTextField txtField = new JTextField();
				JButton btn = new JButton("Open Dialog");
				String resourceName = "/resources/appconfig.properties";

				txtField.setText(resourceName);

				mainFrame.setVisible(true);
				mainFrame.setSize(300, 180);
				mainFrame.setLayout(new BorderLayout());
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.add(btn, BorderLayout.SOUTH);
				mainFrame.add(txtField, BorderLayout.NORTH);

				btn.setPreferredSize(new Dimension(0, 50));
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						String resourceName = txtField.getText().trim();
						String[] reqProps = new String[] {};
						EditFileDialog dialog = new EditFileDialog(mainFrame, 900, 500, resourceName, reqProps);

						dialog.launch();
					}
				});
			}
		});
	}
}