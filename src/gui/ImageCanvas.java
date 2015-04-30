package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import util.ImageUtil;

/**
 * This class represents a panel which handles scaling and drawing an image.
 *
 * @author Ryan M. Kane
 */
public class ImageCanvas extends JPanel {
	private static final long serialVersionUID = -646198773951859720L;

	private BufferedImage sourceImage;
	private BufferedImage scaledImage;
	private int padding;

	private boolean showAlphaTile;
	private BufferedImage alphaTileImg;

	public ImageCanvas(int padding, boolean showAlphaTile) {
		super();

		this.padding = padding;
		this.showAlphaTile = showAlphaTile;

		this.alphaTileImg = createTileImage(8, Color.LIGHT_GRAY, Color.WHITE);
	}

	private BufferedImage createTileImage(int gridSize, Color primaryColor, Color secondaryColor) {
		int tileSize = gridSize * 2;

		BufferedImage tileImg = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
		Graphics g = tileImg.getGraphics();

		g.setColor(secondaryColor);
		g.fillRect(0, 0, tileSize, tileSize);

		g.setColor(primaryColor);
		g.fillRect(0, 0, gridSize, gridSize);
		g.fillRect(gridSize, gridSize, tileSize-1, tileSize-1);

		return tileImg;
	}

	public void calculateScaledImage() {
		if (sourceImage == null) {
			return;
		}

		int panelWidth = this.getWidth();
		int panelHeight = this.getHeight();
		int maxWidth = panelWidth - this.padding * 2;
		int maxHeight = panelHeight - this.padding * 2;

		this.scaledImage = ImageUtil.scaleToFit(sourceImage, maxWidth, maxHeight, true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int panelWidth = this.getWidth();
		int panelHeight = this.getHeight();

		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		if (showAlphaTile) { 
			ImageUtil.tileImage(g, alphaTileImg, panelWidth, panelHeight);
		}
		
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, panelWidth-1, panelHeight-1);

		if (sourceImage == null) {
			return;
		}

		int imgWidth = this.scaledImage.getWidth();
		int imgHeight = this.scaledImage.getHeight();
		int xPos = (int) (panelWidth / 2.0 - imgWidth / 2.0);
		int yPos = (int) (panelHeight / 2.0 - imgHeight / 2.0);

		g.drawImage(this.scaledImage, xPos, yPos, imgWidth, imgHeight, null);
		g.dispose();
	}

	protected BufferedImage getImage() {
		return sourceImage;
	}

	protected void setImage(BufferedImage image) {
		this.sourceImage = image;
		this.calculateScaledImage();
	}

	protected int getPadding() {
		return padding;
	}

	protected void setPadding(int padding) {
		this.padding = padding;
	}
}
