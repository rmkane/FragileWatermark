package watermark.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import watermark.core.cipher.KeyCipher;
import watermark.core.util.FileUtil;
import watermark.core.util.GuiUtils;
import watermark.core.util.ImageUtil;
import watermark.gui.AppIcons;
import watermark.gui.actions.MenuItemAction;
import watermark.gui.components.AboutPanel;
import watermark.gui.components.EditFileDialog;
import watermark.gui.components.ImagePanel;
import watermark.gui.controller.MainViewContoller;

/**
 * This class represents a view which is the main graphical interface for the
 * application.
 *
 * @author Ryan M. Kane
 */
public class MainView extends JPanel {
	private static final long serialVersionUID = 1082139773406203487L;

	// Create a file chooser
	private final JFileChooser EXPLORER = new JFileChooser("./");
	private static final String CONFIG_FILENAME = "appconfig.properties";
	public static final String DEFAULT_PRIVATE_KEY_LOC = "C:/keys/private.key";
	public static final String DEFAULT_PUBLIC_KEY_LOC = "C:/keys/public.key";
	public static final int DEFAULT_BLOCK_SIZE = 32;

	public static final String[] REQ_PROPS = new String[] {
		"privateKeyLoc", "publicKeyLoc", "blockSize"
	};

	private String appTitle;

	private MainViewContoller controller;

	private KeyCipher cipher;
	private String privateKeyLoc;
	private String publicKeyLoc;
	private int blockSize;
	private boolean scaleImage;

	private BufferedImage sourceImage;
	private BufferedImage watermarkImage;
	private BufferedImage outputImage;

	private JPanel imagesPanel;

	private ImagePanel imageSourcePanel;
	private ImagePanel imageWatermarkPanel;
	private ImagePanel imageOutputPanel;

	private JPanel buttonPanel;
	private JButton imageSrcBtn;
	private JButton watermarkImgBtn;
	private JButton exportImage;

	private AboutPanel aboutPanel;
	private EditFileDialog dialog;

	// Menu
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, helpMenu;
	private JMenuItem keyGenMenu, editConfigMenu, encodeMenu, decodeMenu, aboutMenu, clearImagesMenu, exitMenu;
	private JCheckBoxMenuItem scaleCheckMenu;

	public JMenuBar getMenu() {
		return this.menuBar;
	}

	public MainView(String appTitle) {
		super();

		this.appTitle = appTitle;
		// Image scaling is set as default.
		this.scaleImage = true;

		this.loadConfig();
		this.initComponents();
		this.addChildren();
		this.createMenu();
	}

	private void loadConfig() {
		// Load properties.
		Properties props = FileUtil.loadProperties(CONFIG_FILENAME);
		this.privateKeyLoc = props.getProperty("privateKeyLoc", DEFAULT_PRIVATE_KEY_LOC);
		this.publicKeyLoc = props.getProperty("publicKeyLoc", DEFAULT_PUBLIC_KEY_LOC);
		this.blockSize = Integer.parseInt(props.getProperty("blockSize", Integer.toString(DEFAULT_BLOCK_SIZE, 10)), 10);
	}

	@SuppressWarnings("unused")
	private void saveConfig() {
		// Save properties
		Map<String, String> propMap = new HashMap<String, String>();
		propMap.put("privateKeyLoc", this.privateKeyLoc);
		propMap.put("publicKeyLoc", this.publicKeyLoc);
		propMap.put("blockSize", Integer.toString(this.blockSize));
		FileUtil.saveProperties(propMap, CONFIG_FILENAME, "Application configurations.");
	}

	protected void initComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		imageSrcBtn = setupButton(new JButton(new LoadSourceImageAction()));
		watermarkImgBtn = setupButton(new JButton(new LoadWatermarkAction()));
		exportImage = setupButton(new JButton(new ExportOutputImageAction()));
	}

	private void createMenu() {
		menuBar = new JMenuBar();

		// Create menus.
		fileMenu = GuiUtils.createMenu("File", KeyEvent.VK_F, "");
		editMenu = GuiUtils.createMenu("Edit", KeyEvent.VK_E, "");
		helpMenu = GuiUtils.createMenu("Help", KeyEvent.VK_H, "");

		// Create menu items.
		clearImagesMenu = new JMenuItem(new ClearImagesAction());
		keyGenMenu = new JMenuItem(new KeyGenAction());
		editConfigMenu = new JMenuItem(new EditConfigAction());
		encodeMenu = new JMenuItem(new EncodeImageAction());
		decodeMenu = new JMenuItem(new DecodeImageAction());
		scaleCheckMenu = new JCheckBoxMenuItem(new ScaleImageItemAction());
		aboutMenu = new JMenuItem(new AboutAction());
		exitMenu = new JMenuItem(new ExitAction());

		// Add menu items to menus.
		fileMenu.add(clearImagesMenu);
		fileMenu.add(encodeMenu);
		fileMenu.add(decodeMenu);
		fileMenu.add(keyGenMenu);
		fileMenu.addSeparator();
		fileMenu.add(exitMenu);
		editMenu.add(editConfigMenu);
		editMenu.add(scaleCheckMenu);
		helpMenu.add(aboutMenu);

		// Add menus to menubar.
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);

		// Initialize menu properties.
		scaleCheckMenu.setSelected(this.scaleImage);
	}

	protected void setScaleImage(boolean selected) {
		this.scaleImage = selected;
		this.imageSourcePanel.setScaleImage(selected);
		this.imageWatermarkPanel.setScaleImage(selected);
		this.imageOutputPanel.setScaleImage(selected);
	}

	private void addChildren() {
		this.setLayout(new BorderLayout());

		imagesPanel = new JPanel();
		imagesPanel.setLayout(new GridLayout(1, 3));
		this.add(imagesPanel, BorderLayout.CENTER);

		imageSourcePanel = new ImagePanel("Source Image", 256, 256, 10, this.scaleImage);
		imagesPanel.add(imageSourcePanel);
		imageWatermarkPanel = new ImagePanel("Watermark Image", 256, 256, 10, this.scaleImage);
		imagesPanel.add(imageWatermarkPanel);
		imageOutputPanel = new ImagePanel("Output Image", 256, 256, 10, this.scaleImage);
		imagesPanel.add(imageOutputPanel);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		this.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(imageSrcBtn);
		buttonPanel.add(watermarkImgBtn);
		buttonPanel.add(exportImage);

		this.addComponentListener(new ResizeListener());
	}

	public void redrawImages() {
		imageSourcePanel.setImage(sourceImage);
		imageWatermarkPanel.setImage(watermarkImage);
		imageOutputPanel.setImage(outputImage);
	}

	private boolean keysExist() {
		return cipher.areKeysPresent(publicKeyLoc, privateKeyLoc);
	}

	private boolean validateInput() {
		if (!keysExist()) {
			GuiUtils.showErrorMessage("Keys do not exist! Please generate keys from menu.");
			return false;
		}

		if (sourceImage == null) {
			GuiUtils.showErrorMessage("Please load a source image!");
			return false;
		}

		if (watermarkImage == null) {
			GuiUtils.showErrorMessage("Please load a watermark image!");
			return false;
		}

		return true;
	}

	private JButton setupButton(JButton button) {
		Dimension d = button.getPreferredSize();
		d.height = 48;
		button.setPreferredSize(d);
		button.setFont(button.getFont().deriveFont(Font.BOLD).deriveFont(12f));

		return button;
	}

	private BufferedImage handleLoadImage(ImagePanel imagePanel) throws IOException {
		BufferedImage image = null;
		int returnVal = EXPLORER.showOpenDialog(MainView.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = EXPLORER.getSelectedFile();
			GuiUtils.showSuccessMessage("Opening: " + file.getName() + "...");

			// Load and draw image.
			image = ImageUtil.loadImage(file.getAbsolutePath());

			if (image == null) {
				GuiUtils.showErrorMessage("Not a valid image file.");
				throw new IOException();
			}

			imagePanel.setImage(image);

		} else {
			GuiUtils.showSuccessMessage("Open command cancelled.");
		}

		return image;
	}

	// ========================================================================
	// Accessors/Mutators
	// ========================================================================
	public void setController(MainViewContoller controller) {
		this.controller = controller;
	}

	public void setKeyCipher(KeyCipher cipher) {
		this.cipher = cipher;
	}

	public KeyCipher getKeyCipher() {
		return this.cipher;
	}

	public String getPrivateKeyLoc() {
		return privateKeyLoc;
	}

	public String getPublicKeyLoc() {
		return publicKeyLoc;
	}

	public void setApplicationTitle(String appTitle) {
		this.appTitle = appTitle;
	}

	// ========================================================================
	// Menu Actions
	// ========================================================================
	private class AboutAction extends MenuItemAction {
		private static final long serialVersionUID = -6266116884935376990L;

		public AboutAction() {
			super("About", AppIcons.getAppIcon(), KeyEvent.VK_A, "About application");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (aboutPanel == null) {
				aboutPanel = new AboutPanel();
			}

			JOptionPane.showConfirmDialog(null, aboutPanel,
					appTitle,
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
		}
	}

	private class ExitAction extends MenuItemAction {
		private static final long serialVersionUID = -2045597246972400617L;

		public ExitAction() {
			super("Exit", AppIcons.getExitIcon(), KeyEvent.VK_X, "Exit application.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int option = JOptionPane.showConfirmDialog(null,
					"Are you sure you want to exit?", "Confirm Exit",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

			if (option == JOptionPane.YES_OPTION) {
				System.exit(0);
			} else {
				//JOptionPane.showMessageDialog(null, "Action aborted.",
				//		appTitle, JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private class ClearImagesAction extends MenuItemAction {
		private static final long serialVersionUID = -474256531181431784L;

		public ClearImagesAction() {
			super("Clear Images", AppIcons.getClearIcon(), KeyEvent.VK_C,
					"Clear all images.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			imageSourcePanel.setImage(null);
			imageWatermarkPanel.setImage(null);
			imageOutputPanel.setImage(null);
		}
	}


	private class KeyGenAction extends MenuItemAction {
		private static final long serialVersionUID = -5862651506038768293L;

		public KeyGenAction() {
			super("Generate Keys", AppIcons.getKeysIcon(), KeyEvent.VK_G,
					"Generate RSA public/private key pairs.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// Check to see if keys already exist.
				if (!keysExist()) {
					// Generate new keys.
					cipher.generateKey(publicKeyLoc, privateKeyLoc);
					GuiUtils.showSuccessMessage("Generated public/private key pairs.");
				} else {
					GuiUtils.showMessage(MainView.this, "Alert",
							"Keys already exist!",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private class EditConfigAction extends MenuItemAction {
		private static final long serialVersionUID = 3875241178326699899L;

		public EditConfigAction() {
			super("Edit Config", AppIcons.getEditIcon(), KeyEvent.VK_E,
					"Edit application configuration file.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (dialog == null) {
				dialog = new EditFileDialog(null, 360, 580, CONFIG_FILENAME, REQ_PROPS);
			}

			dialog.launch();
		}
	}

	private class EncodeImageAction extends MenuItemAction {
		private static final long serialVersionUID = -7244310500703692691L;

		public EncodeImageAction() {
			super("Encode", AppIcons.getEncodeIcon(), KeyEvent.VK_E,
					"Encode source image with watermark.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!validateInput()) {
				return;
			}

			PrivateKey key = cipher.getKey(privateKeyLoc);
			BufferedImage source = ImageUtil.cloneImage(imageSourcePanel.getImage());
			BufferedImage watermark = ImageUtil.cloneImage(imageWatermarkPanel.getImage());

			outputImage = controller.handleEncode(cipher, key, source, watermark, blockSize);
			imageOutputPanel.setImage(outputImage);

			GuiUtils.showSuccessMessage("Finished encoding image.");
		}
	}

	private class DecodeImageAction extends MenuItemAction {
		private static final long serialVersionUID = -6728457739128748868L;

		public DecodeImageAction() {
			super("Decode", AppIcons.getDecodeIcon(), KeyEvent.VK_D,
					"Decode source image with watermark.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!validateInput()) {
				return;
			}

			PublicKey key = cipher.getKey(publicKeyLoc);
			BufferedImage source = ImageUtil.cloneImage(imageSourcePanel.getImage());
			BufferedImage watermark = ImageUtil.cloneImage(imageWatermarkPanel.getImage());

			outputImage = controller.handleDecode(cipher, key, source, watermark, blockSize);
			imageOutputPanel.setImage(outputImage);

			GuiUtils.showSuccessMessage("Finished decoding image.");
		}
	}

	private class ScaleImageItemAction extends MenuItemAction {
		private static final long serialVersionUID = -6897918595037333109L;

		public ScaleImageItemAction() {
			super("Scale Images", null, KeyEvent.VK_S, "Automatically adjusts scaling of the preview images.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setScaleImage(((AbstractButton) e.getSource()).isSelected());
		}
	}

	// ========================================================================
	// Button Actions
	// ========================================================================
	private class LoadSourceImageAction extends AbstractAction {
		private static final long serialVersionUID = 4874036618145563606L;

		public LoadSourceImageAction() {
			super("Choose Source Image");
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			try {
				sourceImage = handleLoadImage(imageSourcePanel);
			} catch (IOException e) {
			}
		}
	}

	private class LoadWatermarkAction extends AbstractAction {
		private static final long serialVersionUID = -1841000850569755284L;

		public LoadWatermarkAction() {
			super("Choose Watermark Image");
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			try {
				watermarkImage = handleLoadImage(imageWatermarkPanel);
			} catch (IOException e) {
			}
		}
	}

	private class ExportOutputImageAction extends AbstractAction {
		private static final long serialVersionUID = 8631472020293257344L;

		public ExportOutputImageAction() {
			super("Export Image");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (outputImage == null) {
				GuiUtils.showErrorMessage("Cannot export because the output image is null.");
				return;
			}

			int returnVal = EXPLORER.showSaveDialog(MainView.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = EXPLORER.getSelectedFile();
				GuiUtils.showSuccessMessage("Saving: " + file.getName() + "...");

				try {
					ImageIO.write(outputImage, "png", file);
					GuiUtils.showSuccessMessage("Successfully exported: " + file.getName());
				} catch (IOException e1) {
					GuiUtils.showErrorMessage("Could not save: " + file.getName());
				}
			} else {
				GuiUtils.showSuccessMessage("Save command cancelled.");
			}
		}
	}

	// ========================================================================
	// Listeners
	// ========================================================================
	private class ResizeListener implements ComponentListener {
		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			redrawImages();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}
	}
}
