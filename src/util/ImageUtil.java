package util;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageUtil {
	public static BufferedImage loadImage(String filename) {
		URL url = null;
		try {
			ClassLoader loader = CommonUtil.class.getClassLoader();
			url = loader.getResource("resources/" + filename);
			return ImageIO.read(url);
		} catch (Exception e) {
			System.err.println("Could not load image: " + url);
		}
		return null;
	}

	public static void partitionImage(String imgFilename, int blockSize,
			String exportPath, String prefix) {
		BufferedImage[][] blocks = partitionImage(imgFilename, blockSize);

		if (exportPath == null) {
			exportPath = "";
		}

		if (prefix == null || prefix.isEmpty()) {
			prefix = "block";
		}

		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks[row].length; col++) {
				BufferedImage imgBlock = blocks[col][row];
				int xOff = col * blockSize;
				int yOff = row * blockSize;

				try {
					String filename = String.format("%s\\%s%d-%dx%d.png",
							exportPath, prefix, blockSize, xOff, yOff);
					File outputfile = new File(filename);

					// http://stackoverflow.com/a/2833883/1762224
					outputfile.getParentFile().mkdirs();

					ImageIO.write(imgBlock, "png", outputfile);
				} catch (Exception e) {

				}
			}
		}
	}

	public static BufferedImage[][] partitionImage(String imgFilename,
			int blockSize) {
		BufferedImage img = ImageUtil.loadImage(imgFilename);
		int width = img.getWidth();
		int height = img.getHeight();

		if (width % blockSize != 0 || height % blockSize != 0) {
			throw new IllegalArgumentException(
					"Block size must be divisible by width and height");
		}

		int rows = height / blockSize;
		int cols = width / blockSize;
		BufferedImage[][] blocks = new BufferedImage[rows][cols];

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				int xOff = x * blockSize;
				int yOff = y * blockSize;

				blocks[x][y] = img.getSubimage(xOff, yOff, blockSize, blockSize);
			}
		}

		return blocks;
	}
}
