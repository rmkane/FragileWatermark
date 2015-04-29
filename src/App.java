import java.awt.image.BufferedImage;
import java.security.PrivateKey;
import java.security.PublicKey;

import util.BitUtil;
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

	public static final String IMAGE_PATH = "resources/duke_stickers.png"; //"reddit.png";
	public static final String WATERMARK_PATH = "resources/snoopy.png";

	private static final int BLOCK_SIZE = 8;

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

		BufferedImage originalImage = ImageUtil.cloneImage(ImageUtil.loadImage(IMAGE_PATH), BufferedImage.TYPE_INT_ARGB);
		BufferedImage watermarkImage = ImageUtil.loadImage(WATERMARK_PATH);
		BufferedImage watermarkedImage = testEncode(originalImage, watermarkImage, cipher, (PublicKey) cipher.getKey(PUBLIC_KEY_FILE));

		@SuppressWarnings("unused")
		BufferedImage unwatermarkedImage = testDecode(watermarkedImage, watermarkImage, cipher, (PrivateKey) cipher.getKey(PRIVATE_KEY_FILE));
	}

	private static BufferedImage testEncode(final BufferedImage image,
			final BufferedImage watermark, final PublicKeyCipher cipher,
			final PublicKey publicKey) {

		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();

		BufferedImage[][] blocks = ImageUtil.partitionImage(image, BLOCK_SIZE);

		int[] watermarkPixels = ImageUtil.getPixels(watermark);
		byte[] watermarkBytes = CommonUtil.integersToBytes(watermarkPixels);
		byte[] watermarkMask = new byte[16];
		System.arraycopy(watermarkBytes, 0, watermarkMask, 0, watermarkMask.length);

		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks[row].length; col++) {
				BufferedImage block = blocks[row][col];
				int w = block.getWidth();
				int h = block.getHeight();

				// Only watermark full image blocks.
				if (w == BLOCK_SIZE && h == BLOCK_SIZE) {
					int[] pixels = ImageUtil.getPixels(block);

					// Set LSB of each pixel to 0.
					BitUtil.dropLSB(pixels);
					block.setRGB(0, 0, block.getWidth(), block.getHeight(), pixels, 0, block.getWidth());

					byte[] params = new byte[] { (byte) imgWidth, (byte) imgHeight, (byte) block.getRGB(0, 0) };
					byte[] hashBytes = CommonUtil.hashMD5(params);
					byte[] xorBytes = CommonUtil.xor(hashBytes, watermarkMask);

					//System.out.printf("%4d. %s%n", row * blocks.length + col, CommonUtil.hexDump(xorBytes, true));

					byte[] cipherData = cipher.encrypt(xorBytes, publicKey);
					BitUtil.setLSB(pixels, cipherData);

					//System.out.printf("%4d. %s%n", row * blocks.length + col, CommonUtil.hexDump(cipherData, true));

					block.setRGB(0, 0, w, h, pixels, 0, w);

					//System.out.println(block);
				}
			}
		}

		BufferedImage output = ImageUtil.recombine(blocks);
		ImageUtil.writeImage(output, "export", "outputImage.png");

		System.out.println("Are equal?: "
				+ ImageUtil.compareImages(image, output) + " "
				+ image.getRGB(0, 0) + " " + output.getRGB(0, 0));

		return output;
	}

	private static BufferedImage testDecode(final BufferedImage image,
			BufferedImage watermark, final PublicKeyCipher cipher,
			final PrivateKey privateKey) {

		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();

		BufferedImage[][] blocks = ImageUtil.partitionImage(image, BLOCK_SIZE);

		int[] watermarkPixels = ImageUtil.getPixels(watermark);
		byte[] watermarkBytes = CommonUtil.integersToBytes(watermarkPixels);
		byte[] watermarkMask = new byte[16];
		System.arraycopy(watermarkBytes, 0, watermarkMask, 0, watermarkMask.length);

		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks[row].length; col++) {
				BufferedImage block = blocks[row][col];
				int w = block.getWidth();
				int h = block.getHeight();

				// Only watermark full image blocks.
				if (w == BLOCK_SIZE && h == BLOCK_SIZE) {
					int[] pixels = ImageUtil.getPixels(block);
					byte[] lsbs = BitUtil.extractLsb(pixels, 128);

					System.out.printf("%4d. %s%n", row * blocks.length + col, CommonUtil.hexDump(lsbs, true));

					byte[] cipherData = cipher.decrypt(lsbs, privateKey); // Decryption Error...

					// Set LSB of each pixel to 0.
					BitUtil.dropLSB(pixels);
					block.setRGB(0, 0, w, h, pixels, 0, w);

					// Expected Hash
					byte[] params = new byte[] { (byte) imgWidth, (byte) imgHeight, (byte) block.getRGB(0, 0) };
					byte[] hashBytes = CommonUtil.hashMD5(params);
					byte[] xorData = CommonUtil.xor(hashBytes, cipherData);

					CommonUtil.hexDump(xorData, true);
				}
			}
		}

		return null;
	}
}
