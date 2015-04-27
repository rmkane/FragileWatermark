package example;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import util.ImageUtil;

/**
 * This is unused by the application. [Ignore]
 *
 * @author Ryan M. Kane
 *
 */
public class ImageTileExample {
	public static void main(String[] args) {
		//tileImage("mister.png", "mister_tile.png", 0xDDAA33, false, 4, 4, 20, 20, 15, 12);
		//tileImage("mister.png", "mister_tile2.png", 0xFF69A6, false, 1, 2, 100);

		tileImage("salem.jpg", "salem_tile.png", 0xDDDD88, false, 4, 4, 2, 2, 40, 34);
	}

	public static void tileImage(String inputFilename, String outputFilename,
			int bgColor, boolean transparent, int spacing, int border,
			int tileCount) {
		tileImage(inputFilename, outputFilename, bgColor, transparent, spacing,
				spacing, border, border, tileCount, tileCount);
	}

	public static void tileImage(String inputFilename, String outputFilename,
			int bgColor, boolean transparent, int spacingX, int spacingY,
			int borderX, int borderY, int tileCountX, int tileCountY) {
		BufferedImage srcImg = ImageUtil.loadImage(inputFilename);
		int imgType = srcImg.getType();
		Color fill = new Color(bgColor, transparent);
		int width = srcImg.getWidth();
		int height = srcImg.getHeight();
		int tileWidth = width / tileCountX;
		int tileHeight = height / tileCountY;
		int calcWidth = width + (spacingX * (tileCountX - 1)) + (borderX * 2);
		int calcHeight = height + (spacingY * (tileCountY - 1)) + (borderY * 2);
		int diffWidth = width - tileWidth * tileCountX;
		int diffHeight = height - tileHeight * tileCountY;
		int newWidth = calcWidth - diffWidth;
		int newHeight = calcHeight - diffHeight;
		BufferedImage outImg = new BufferedImage(newWidth, newHeight, imgType);
		Graphics g = outImg.getGraphics();

		g.setColor(fill);
		g.fillRect(0, 0, newWidth, newHeight);

		for (int row = 0; row < tileCountY; row++) {
			for (int col = 0; col < tileCountX; col++) {
				int dX = col * tileWidth;
				int dY = row * tileHeight;
				BufferedImage tile = srcImg.getSubimage(dX, dY, tileWidth, tileHeight);
				int offX = (col * (tileWidth + spacingX)) + borderX;
				int offY = (row * (tileHeight + spacingY)) + borderY;
				g.drawImage(tile, offX, offY, null);
			}
		}

		ImageUtil.writeImage(outImg, "tile", outputFilename);
	}
}
