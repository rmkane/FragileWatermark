package util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

	public static void writeBlocks(BufferedImage[][] blocks, String exportPath, String prefix) {
		prefix = prefix == null || prefix.isEmpty() ? "block" : prefix;

		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks[row].length; col++) {
				BufferedImage block = blocks[col][row];
				String filename = String.format("%s%d-%dx%d.png", prefix, block.getWidth(), col, row);
				writeImage(block, exportPath, filename);
			}
		}
	}

	public static void writeImage(BufferedImage img, String directory, String filename) {
		try {
			StringBuffer path = new StringBuffer();
			if (directory != null && !directory.isEmpty()) {
				path.append(directory).append('\\');
			}
			path.append(filename);
			File outputfile = new File(path.toString());
			// http://stackoverflow.com/a/2833883/1762224
			outputfile.getParentFile().mkdirs();
			ImageIO.write(img, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage[][] partitionImage(BufferedImage img, int blockSize) {
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

	public static BufferedImage[][] partitionImage(String imgFilename, int blockSize) {
		return partitionImage(ImageUtil.loadImage(imgFilename), blockSize);
	}

	public static BufferedImage recombine(BufferedImage[][] blocks) {
		BufferedImage block0 = blocks[0][0];
		int width = block0.getWidth() * blocks.length;
		int height = block0.getHeight() * blocks[0].length;
		int type = block0.getType();
		BufferedImage img = new BufferedImage(width, height, type);
		Graphics g = img.getGraphics();

		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks[row].length; col++) {
				BufferedImage block = blocks[row][col];
				int w = block.getWidth();
				int h = block.getHeight();
				int x = row * w;
				int y = col * h;
				g.drawImage(block, x, y, w, h, null);
			}
		}

		return img;
	}

	public static byte[] imageToBytes(BufferedImage img, String formatName) {
		byte[] imgBytes = null;
		ByteArrayOutputStream stream = null;

		try {
			stream = new ByteArrayOutputStream();
			ImageIO.write(img, formatName, stream);
			stream.flush();
			imgBytes = stream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return imgBytes;
	}

	public static int[] getPixels(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();

		return img.getRGB(0, 0, width, height, null, 0, width);
	}
}
