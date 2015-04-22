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

		testEncode(cipher, (PublicKey) cipher.getKey(PUBLIC_KEY_FILE));
		testDecode(cipher, (PrivateKey) cipher.getKey(PRIVATE_KEY_FILE));
	}

	private static void testEncode(final PublicKeyCipher cipher,
			final PublicKey publicKey) {

		BufferedImage image = ImageUtil.loadImage(IMAGE_PATH);
		BufferedImage[][] blocks = ImageUtil.partitionImage(image, 8);

		BufferedImage watermarkImg = ImageUtil.loadImage(WATERMARK_PATH);
		int[] watermarkPixels = ImageUtil.getPixels(watermarkImg);
		byte[] watermarkBytes = CommonUtil.integersToBytes(watermarkPixels);
		byte[] watermarkMask = new byte[16];
		System.arraycopy(watermarkBytes, 0, watermarkMask, 0, watermarkMask.length);

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
					block.setRGB(x, y, CommonUtil.setLSB(pixel, 0));
				}

				int[] blockPixels = ImageUtil.getPixels(block);
				byte[] blockBytes = CommonUtil.integersToBytes(blockPixels);
				byte[] hashBytes = CommonUtil.hashMD5(blockBytes);
				byte[] xorBytes = CommonUtil.xor(hashBytes, watermarkMask);
				byte[] cipherData = cipher.encrypt(xorBytes, publicKey);

				System.out.printf("%4d. %s%n", row * blocks.length + col, CommonUtil.hexDump(cipherData, true));

				for (int i = 0; i < pixels.length; i++) {
					int pixel = pixels[i];
					int x = i % w;
					int y = i / h;

					int cipherByte = cipherData[i / 8];
					int bitPos = i % 8;
					int cipherBit = (cipherByte >>> (bitPos - 1) & 1);

					block.setRGB(x, y, CommonUtil.setLSB(pixel, cipherBit));
				}
			}
		}

		BufferedImage output = ImageUtil.recombine(blocks);
		ImageUtil.writeImage(output, "export", "outputImage.png");

		System.out.println("Are equal?: " + ImageUtil.compareImages(image, output));
	}

	private static void testDecode(final PublicKeyCipher cipher,
			final PrivateKey privateKey) {

	}
}
