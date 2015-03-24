import java.awt.image.BufferedImage;
import java.util.Arrays;

import util.CommonUtil;
import util.ImageUtil;

public class MainTest {
	public static void main(String[] args) {
		//testImageBlocks();
		//testMD5();
		testLSB();
	}

	public static void testImageBlocks() {
		ImageUtil.partitionImage("snoopy.png", 16, "export", "");
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

				System.out.printf(lsb + ":(%d, %d)%n", pixelArr[count], newpixel[count]);
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