package example;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import util.CommonUtil;
import util.ImageUtil;

/**
 * This is only used for testing...
 *
 * @author Ryan M. Kane
 */
public class MainExample {
	public static void main(String[] args) {
		testWatermark();
		//testImageBlocks();
		//testMD5();
		//testLSB();

		testLSBZeroExport();
	}

	// In progress...
	public static void testWatermark() {
		BufferedImage[][] blocks = ImageUtil.partitionImage("snoopy.png", 8);
		BufferedImage block = blocks[1][3];
		ImageUtil.writeImage(block, "export", "snoopy8-1x3.png");
		int pixels[] = ImageUtil.getPixels(block);

		for (int pixel : pixels) {
			pixel = CommonUtil.setLSB(pixel, 1);
			System.out.printf("%s -> %s %s %d%n", CommonUtil.getPixelARGB(pixel),
					CommonUtil.getPixelBinaryARGB(pixel), CommonUtil.toBin(pixel), pixel);
		}
	}

	public static void testLSBZeroExport() {
		int blockSize = 128;
		BufferedImage img = ImageUtil.loadImage("reddit.png");
		int width = img.getWidth();
		int height = img.getHeight();
		int type = img.getType();
		BufferedImage[][] blocks = ImageUtil.partitionImage(img, blockSize);
		ImageUtil.writeBlocks(blocks, "export", "foo");

		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks[row].length; col++) {
				BufferedImage block = blocks[row][col];
				int pixels[] = ImageUtil.getPixels(block);
				int w = block.getWidth();
				int h = block.getHeight();
				for (int i = 0; i < pixels.length; i++) {
					int pixel = pixels[i];
					int x = i % w;
					int y = i / h;
					//System.out.printf("[%1$dx%2$d] [%3$3d, %3$3d]%n", x, y, w);
					block.setRGB(x, y, CommonUtil.setLSB(pixel, 1));
				}
				// block -> Xr*
			}
		}
		BufferedImage outImg = new BufferedImage(width, height, type);
		Graphics g = outImg.getGraphics();
		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks[row].length; col++) {
				BufferedImage block = blocks[row][col];
				int w = block.getWidth();
				int h = block.getHeight();
				int x = row * w;
				int y = col * h;
				System.out.printf("Drawing [%d,%d] at [%d,%d] [%dx%d]%n", row, col, x, y, w, h);
				g.drawImage(block, x, y, w, h, null);
			}
		}
		ImageUtil.writeImage(outImg, "export", "new_image.png");
	}

	public static void testImageBlocks() {
		BufferedImage[][] blocks = ImageUtil.partitionImage("snoopy.png", 16);
		ImageUtil.writeBlocks(blocks, "export", "snoopy");
	}

	public static void testMD5() {
		System.out.println("MD5 Hash: " + CommonUtil.hashStrMD5("Hello World"));
	}

	// http://stackoverflow.com/a/26616856/1762224
	public static void testLSB() {
		BufferedImage img = ImageUtil.loadImage("snoopy.png");
		int width = img.getWidth();
		int height = img.getHeight();
		int pixelArr[] = img.getRGB(0, 0, width, height, null, 0, width);

		int[] newpixel = Arrays.copyOf(pixelArr, pixelArr.length);

		String s = "abc 123";
		byte[] b = s.getBytes();

		int count = 0;

		for (int i = 0; i < b.length; i++) {
			byte current_byte = b[i];

			for (int j = 7; j >= 0; j--) {
				int lsb = (current_byte >> j) & 1;

				newpixel[count] = (pixelArr[count] & 0xfffffffe) + lsb;

				System.out.printf(lsb + ":(%d, %d)%n", pixelArr[count],
						newpixel[count]);
				count++;
			}

			System.out.println();
		}

		// Extraction sequence
		String secret = "";
		int bit = 0;

		for (int i = 0; i < b.length; i++) {
			int ascii = 0;
			for (int j = 7; j >= 0; j--) {
				ascii += (newpixel[bit] & 1) << j;
				bit++;
			}
			secret += (char) ascii;
		}

		System.out.print(secret);
	}
}