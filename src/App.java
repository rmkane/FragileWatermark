import java.awt.image.BufferedImage;
import java.security.PrivateKey;
import java.security.PublicKey;

import util.CommonUtil;
import util.ImageUtil;
import cipher.PublicKeyCipher;

/**
 * Main application which encodes and decodes an image using LSB, XOR, and RSA.
 *
 * @author Ryan M. Kane
 */
public class App {
	public static final String PRIVATE_KEY_FILE = "C:/keys/private.key";
	public static final String PUBLIC_KEY_FILE = "C:/keys/public.key";

	public static final String IMAGE_PATH = "duke_stickers.png"; //"reddit.png";
	public static final String WATERMARK_PATH = "snoopy.png";

	public static void main(String[] args) {
		PublicKeyCipher cipher = new PublicKeyCipher("RSA");
		try {
			// Check to see if keys already exist.
			if (!cipher.areKeysPresent(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE)) {
				// Generate new keys.
				cipher.generateKey(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		testEncode(cipher, (PublicKey) cipher.getKey(PUBLIC_KEY_FILE));
		testDecode(cipher, (PrivateKey) cipher.getKey(PRIVATE_KEY_FILE));
	}

	private static void testEncode(final PublicKeyCipher cipher,
			final PublicKey publicKey) {

		int blockSize = 8;
		BufferedImage image = ImageUtil.cloneImage(ImageUtil.loadImage(IMAGE_PATH), BufferedImage.TYPE_INT_ARGB);
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();

		BufferedImage[][] blocks = ImageUtil.partitionImage(image, blockSize);

		BufferedImage watermarkImg = ImageUtil.loadImage(WATERMARK_PATH);
		int[] watermarkPixels = ImageUtil.getPixels(watermarkImg);
		byte[] watermarkBytes = CommonUtil.integersToBytes(watermarkPixels);
		byte[] watermarkMask = new byte[16];
		System.arraycopy(watermarkBytes, 0, watermarkMask, 0, watermarkMask.length);

		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks[row].length; col++) {
				BufferedImage block = blocks[row][col];

				// Only watermark full image blocks.
				if (block.getWidth() == blockSize && block.getHeight() == blockSize) {
					int pixels[] = ImageUtil.getPixels(block);
					int w = block.getWidth();
					int h = block.getHeight();
					for (int i = 0; i < pixels.length; i++) {
						int pixel = pixels[i];
						int x = i % w;
						int y = i / h;
						block.setRGB(x, y, CommonUtil.setLSB(pixel, 0));
					}

					byte[] params = new byte[] { (byte) imgWidth, (byte) imgHeight, (byte) block.getRGB(0, 0) };
					byte[] hashBytes = CommonUtil.hashMD5(params);
					byte[] xorBytes = CommonUtil.xor(hashBytes, watermarkMask);
					byte[] cipherData = cipher.encrypt(xorBytes, publicKey);

					//System.out.printf("%4d. %s%n", row * blocks.length + col, CommonUtil.hexDump(cipherData, true));

					for (int i = 0; i < pixels.length; i++) {
						int pixel = pixels[i];
						int x = i % w;
						int y = i / h;

						int cipherByte = cipherData[i / 8];
						int bitPos = i % 8;
						int cipherBit = (cipherByte >>> (7 - bitPos) & 1);

						block.setRGB(x, y, CommonUtil.setLSB(pixel, cipherBit));
					}
				}
			}
		}

		BufferedImage output = ImageUtil.recombine(blocks);
		ImageUtil.writeImage(output, "export", "outputImage.png");

		System.out.println("Are equal?: "
				+ ImageUtil.compareImages(image, output) + " "
				+ image.getRGB(0, 0) + " " + output.getRGB(0, 0));
	}

	private static void testDecode(final PublicKeyCipher cipher,
			final PrivateKey privateKey) {

	}
}
