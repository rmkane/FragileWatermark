package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;

import util.CommonUtil;
import util.GuiUtils;
import util.ImageUtil;

public class MainView extends JPanel {
	private static final long serialVersionUID = 1082139773406203487L;

	// Create a file chooser
	private final JFileChooser EXPLORER = new JFileChooser();
	private static final String CONFIG_FILENAME = "/resources/appconfig.properties";
	public static final String DEFAULT_PRIVATE_KEY_LOC = "C:/keys/private.key";
	public static final String DEFAULT_PUBLIC_KEY_LOC = "C:/keys/public.key";

	private String privateKeyLoc;
	private String publicKeyLoc;

	BufferedImage sourceImage;
	BufferedImage watermarkImage;
	BufferedImage outputImage;

	private JPanel imagesPanel;
	
	private ImagePanel imageSourcePanel;
	private ImagePanel imgWatermarkPanel;
	private ImagePanel imageOutputPanel;

	private JPanel buttonPanel;
	private JButton imageSrcBtn;
	private JButton watermarkImgBtn;

	// Menu
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu;
	private JMenuItem keyGenMenu;

	public JMenuBar getMenu() {
		return this.menuBar;
	}

	public MainView() {
		super();

		this.loadConfig();
		this.initComponents();
		this.addChildren();
		this.createMenu();
	}

	private void loadConfig() {
		// Load properties.
		Properties props = CommonUtil.loadProperties(this.getClass(), CONFIG_FILENAME);
		this.privateKeyLoc = props.getProperty("privateKeyLoc", DEFAULT_PRIVATE_KEY_LOC);
		this.publicKeyLoc = props.getProperty("publicKeyLoc", DEFAULT_PUBLIC_KEY_LOC);
	}

	@SuppressWarnings("unused")
	private void saveConfig() {
		// Save properties
		Map<String, String> propMap = new HashMap<String, String>();
		propMap.put("privateKeyLoc", this.privateKeyLoc);
		propMap.put("publicKeyLoc", this.publicKeyLoc);
		CommonUtil.saveProperties(propMap, CONFIG_FILENAME, "Application configurations.");
	}

	protected void initComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		imageSrcBtn = new JButton(new AbstractAction("Choose Source Image") {
			private static final long serialVersionUID = 4874036618145563606L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = EXPLORER.showOpenDialog(MainView.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = EXPLORER.getSelectedFile();
					GuiUtils.showSuccessMessage("Opening: " + file.getName() + "."
							+ '\n');
				} else {
					GuiUtils.showSuccessMessage("Open command cancelled by user." + '\n');
				}
			}
		});

		watermarkImgBtn = new JButton(new AbstractAction("Choose Watermark Image") {
			private static final long serialVersionUID = -1841000850569755284L;

			@Override
			public void actionPerformed(ActionEvent e) {
				sourceImage = ImageUtil.loadImage("reddit.png");
				watermarkImage = ImageUtil.loadImage("snoopy.png");
				outputImage = ImageUtil.loadImage("duke_stickers.png");

				redrawImages();
			}
		});
	}

	private void createMenu() {
		menuBar = new JMenuBar();
		fileMenu = GuiUtils.createMenu("File", KeyEvent.VK_F, "");
		editMenu = GuiUtils.createMenu("Edit", KeyEvent.VK_E, "");
		keyGenMenu = GuiUtils.createMenuItem("Generate Keys", KeyEvent.VK_G,
				"Generate RSA public/private key pairs.", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Foo");
			}
		});

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		fileMenu.add(keyGenMenu);
	}

	private void addChildren() {
		this.setLayout(new BorderLayout());

		imagesPanel = new JPanel();
		imagesPanel.setLayout(new GridLayout(1, 3));
		this.add(imagesPanel, BorderLayout.CENTER);

		imageSourcePanel = new ImagePanel("Source Image", 256, 256, 10);
		imagesPanel.add(imageSourcePanel);
		imgWatermarkPanel = new ImagePanel("Watermark Image", 256, 256, 10);
		imagesPanel.add(imgWatermarkPanel);
		imageOutputPanel = new ImagePanel("Output Image", 256, 256, 10);
		imagesPanel.add(imageOutputPanel);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		this.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(imageSrcBtn);
		buttonPanel.add(watermarkImgBtn);
		
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
		imgWatermarkPanel.setImage(watermarkImage);
		imageOutputPanel.setImage(outputImage);
		
		imageSourcePanel.getCanvas().calculateScaledImage();
		imgWatermarkPanel.getCanvas().calculateScaledImage();
		imageOutputPanel.getCanvas().calculateScaledImage();
		
		imageSourcePanel.repaint();
		imgWatermarkPanel.repaint();
		imageOutputPanel.repaint();
	}
}