package watermark.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import watermark.gui.components.AboutPanel;
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
	private final JFileChooser EXPLORER = new JFileChooser();
	private static final String CONFIG_FILENAME = "/resources/appconfig.properties";
	public static final String DEFAULT_PRIVATE_KEY_LOC = "C:/keys/private.key";
	public static final String DEFAULT_PUBLIC_KEY_LOC = "C:/keys/public.key";
	public static final int DEFAULT_BLOCK_SIZE = 32;

	private String appTitle;
	private String appVersion;

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

	// Menu
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, helpMenu;
	private JMenuItem keyGenMenu, editConfigMenu, encodeMenu, decodeMenu, aboutMenu, clearImages;
	private JCheckBoxMenuItem scaleCheckBox;

	public JMenuBar getMenu() {
		return this.menuBar;
	}

	public MainView() {
		super();

		// Image scaling is set as default.
		this.scaleImage = true;

		this.loadConfig();
		this.initComponents();
		this.addChildren();
		this.createMenu();
	}

	private void loadConfig() {
		// Load properties.
		Properties props = FileUtil.loadProperties(this.getClass(), CONFIG_FILENAME);
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

		imageSrcBtn = setupButton(new JButton(new AbstractAction("Choose Source Image") {
			private static final long serialVersionUID = 4874036618145563606L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					sourceImage = handleLoadImage(imageSourcePanel);
				} catch (IOException e) {
				}
			}
		}));

		watermarkImgBtn = setupButton(new JButton(new AbstractAction("Choose Watermark Image") {
			private static final long serialVersionUID = -1841000850569755284L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					watermarkImage = handleLoadImage(imageWatermarkPanel);
				} catch (IOException e) {
				}
			}
		}));

		exportImage = setupButton(new JButton(new AbstractAction("Export Image") {
			private static final long serialVersionUID = 7468596761718259182L;

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
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					GuiUtils.showSuccessMessage("Save command cancelled.");
				}
			}
		}));
	}

	private JButton setupButton(JButton button) {
		Dimension d = button.getPreferredSize();
		d.height = 48;
		button.setPreferredSize(d);

		Font f = button.getFont();
		f = f.deriveFont(Font.BOLD);
		f = f.deriveFont(12f);
		button.setFont(f);

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

	private void createMenu() {
		menuBar = new JMenuBar();
		fileMenu = GuiUtils.createMenu("File", KeyEvent.VK_F, "");
		editMenu = GuiUtils.createMenu("Edit", KeyEvent.VK_E, "");
		helpMenu = GuiUtils.createMenu("Help", KeyEvent.VK_H, "");

		clearImages = GuiUtils.createMenuItem("Clear Images", KeyEvent.VK_G,
				"Generate RSA public/private key pairs.", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imageSourcePanel.setImage(null);
				imageWatermarkPanel.setImage(null);
				imageOutputPanel.setImage(null);
			}
		});

		keyGenMenu = GuiUtils.createMenuItem("Generate Keys", KeyEvent.VK_G,
				"Generate RSA public/private key pairs.", new ActionListener() {
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
		});

		editConfigMenu = GuiUtils.createMenuItem("Edit Config", KeyEvent.VK_C,
				"Edit application configuration file.", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!FileUtil.editFile(CONFIG_FILENAME)) {
					GuiUtils.showErrorMessage("Configuration file missing!");
				}
			}
		});

		encodeMenu = GuiUtils.createMenuItem("Encode", KeyEvent.VK_E,
				"Encode source image with watermark.", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!validateInput()) {
					return;
				}

				PublicKey key = cipher.getKey(publicKeyLoc);
				BufferedImage source = ImageUtil.cloneImage(imageSourcePanel.getImage());
				BufferedImage watermark = ImageUtil.cloneImage(imageWatermarkPanel.getImage());

				outputImage = controller.handleEncode(cipher, key, source, watermark, blockSize);
				imageOutputPanel.setImage(outputImage);

				GuiUtils.showSuccessMessage("Finished encoding image.");
			}
		});

		decodeMenu = GuiUtils.createMenuItem("Decode", KeyEvent.VK_E,
				"Decode source image with watermark.", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!validateInput()) {
					return;
				}

				PrivateKey key = cipher.getKey(privateKeyLoc);
				BufferedImage source = ImageUtil.cloneImage(imageSourcePanel.getImage());
				BufferedImage watermark = ImageUtil.cloneImage(imageWatermarkPanel.getImage());

				outputImage = controller.handleDecode(cipher, key, source, watermark, blockSize);
				imageOutputPanel.setImage(outputImage);

				GuiUtils.showSuccessMessage("Finished decoding image.");
			}
		});

		scaleCheckBox = GuiUtils.createCheckBoxMenuItem("Scale Images",
				KeyEvent.VK_S,
				"Automatically adjusts scaling of the preview images.",
				new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				setScaleImage(((JCheckBoxMenuItem) e.getItemSelectable()).isSelected());
			}
		});

		aboutMenu = GuiUtils.createMenuItem("About", KeyEvent.VK_A,
				"About application", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (aboutPanel == null) {
					aboutPanel = new AboutPanel(appTitle, appVersion);
				}

				JOptionPane.showConfirmDialog(null, aboutPanel,
						appTitle,
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
			}
		});

		scaleCheckBox.setSelected(this.scaleImage);

		fileMenu.add(clearImages);
		fileMenu.add(encodeMenu);
		fileMenu.add(decodeMenu);
		fileMenu.add(keyGenMenu);
		editMenu.add(editConfigMenu);
		editMenu.add(scaleCheckBox);
		helpMenu.add(aboutMenu);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);
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

		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) { }

			@Override
			public void componentResized(ComponentEvent e) {
				redrawImages();
			}

			@Override
			public void componentMoved(ComponentEvent e) { }

			@Override
			public void componentHidden(ComponentEvent e) { }
		});
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

	public void setApplicationVersion(String appVersion) {
		this.appVersion = appVersion;
	}
}
