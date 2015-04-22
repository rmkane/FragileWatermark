import java.awt.image.BufferedImage;
import java.security.PrivateKey;
import java.security.PublicKey;

import util.CommonUtil;
import util.ImageUtil;
import cipher.PublicKeyCipher;

public class App {
	public static final String PRIVATE_KEY_FILE = "C:/keys/private.key";
	public static final String PUBLIC_KEY_FILE = "C:/keys/public.key";

	public static final String IMAGE_PATH = "reddit.png";
	public static final String WATERMARK_PATH = "snoopy.png";

	public static void main(String[] args) {
		PublicKeyCipher cipher = new PublicKeyCipher("RSA");
		try {
			if (!cipher.areKeysPresent(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE)) {
				cipher.generateKey(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		testEncode(cipher, (PrivateKey) cipher.getKey(PRIVATE_KEY_FILE));
		testDecode(cipher, (PublicKey) cipher.getKey(PUBLIC_KEY_FILE));
	}

	private static void testEncode(final PublicKeyCipher cipher,
			final PrivateKey privateKey) {

		BufferedImage image = ImageUtil.loadImage(IMAGE_PATH);
		BufferedImage[][] blocks = ImageUtil.partitionImage(image, 8);

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
					block.setRGB(x, y, CommonUtil.setLSB(pixel, 1));
				}
			}
		}

		BufferedImage out = ImageUtil.recombine(blocks);
		int width = out.getWidth();
		int height = out.getHeight();

		// Need to get the pixels in an byte array.
		byte[] imgBytes = ImageUtil.imageToBytes(out, "png");
		int pixelArr[] = out.getRGB(0, 0, width, height, null, 0, width);

		System.out.printf("%d x %d =? %d%n", width, height, width * height);
		System.out.println(imgBytes.length + " " + pixelArr.length);

		ImageUtil.writeImage(out, "export", "exampleOut.png");
	}

	private static void testDecode(final PublicKeyCipher cipher,
			final PublicKey publicKey) {

	}
}
