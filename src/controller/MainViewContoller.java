package controller;

import java.awt.image.BufferedImage;
import java.security.PrivateKey;
import java.security.PublicKey;

import util.BitUtil;
import util.CommonUtil;
import util.ImageUtil;
import cipher.KeyCipher;

/**
 * This is the controller logic for the main view. It handle encoding and
 * decoding watermarked images.
 *
 * @author Ryan M. Kane
 *
 */
public class MainViewContoller {
	/**
	 * Encode a watermark into an image.
	 *
	 * @param cipher - the cipher method for encoding.
	 * @param key - the public key.
	 * @param source - the image to be watermarked.
	 * @param watermark - the watermark to apply to the image.
	 * @param blockSize - image blocks pixel size.
	 * @return an image encoded with an encrypted watermark hash.
	 */
	public BufferedImage handleEncode(KeyCipher cipher, PublicKey key, BufferedImage source, BufferedImage watermark, int blockSize) {
		int imgWidth = source.getWidth();
		int imgHeight = source.getHeight();

		// Partition the image into blocks.
		BufferedImage[][] blocks = ImageUtil.partitionImage(source, blockSize);

		// Truncate the watermark to fit 16 bytes (128 bits) since the MD5 hash
		// outputs the hashed image, to be XORed, in 16 bytes.
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
				if (w == blockSize && h == blockSize) {
					int[] pixels = ImageUtil.getPixels(block);

					// Set LSB of each pixel to 0.
					BitUtil.dropLSB(pixels);
					block.setRGB(0, 0, block.getWidth(), block.getHeight(), pixels, 0, block.getWidth());

					byte[] params = new byte[] { (byte) imgWidth, (byte) imgHeight, (byte) block.getRGB(0, 0) };
					byte[] hashBytes = CommonUtil.hashMD5(params);
					byte[] xorBytes = CommonUtil.xor(hashBytes, watermarkMask);
					byte[] cipherData = cipher.encrypt(xorBytes, key);

					// Set all the pixels' LSB to the cipher data bits.
					BitUtil.setLSB(pixels, cipherData);
					block.setRGB(0, 0, w, h, pixels, 0, w);
				}
			}
		}

		return ImageUtil.recombine(blocks);
	}

	/**
	 * Decode a watermarked image.
	 *
	 * @param cipher - the cipher method for decrypting.
	 * @param key - the private key.
	 * @param source - the image that is watermarked.
	 * @param watermark - the watermark to compare to the extracted hash.
	 * @param blockSize - image blocks pixel size.
	 * @return the XORed watermark hash.
	 */
	public byte[][] handleDecode(KeyCipher cipher, PrivateKey key, BufferedImage source, BufferedImage watermark, int blockSize) {
		int imgWidth = source.getWidth();
		int imgHeight = source.getHeight();

		BufferedImage[][] blocks = ImageUtil.partitionImage(source, blockSize);

		int blockCountY = blocks.length;
		int blockCountX = blocks[0].length;

		byte[][] result = new byte[blockCountX * blockCountY][];

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
				if (w == blockSize && h == blockSize) {
					int[] pixels = ImageUtil.getPixels(block);
					byte[] lsbs = BitUtil.extractLsb(pixels, 128);

					System.out.printf("%4d. %s%n", row * blocks.length + col, CommonUtil.hexDump(lsbs, true));

					byte[] cipherData = cipher.decrypt(lsbs, key); // Decryption Error...

					// Set LSB of each pixel to 0.
					BitUtil.dropLSB(pixels);
					block.setRGB(0, 0, w, h, pixels, 0, w);

					// Expected Hash
					byte[] params = new byte[] { (byte) imgWidth, (byte) imgHeight, (byte) block.getRGB(0, 0) };
					byte[] hashBytes = CommonUtil.hashMD5(params);
					byte[] xorData = CommonUtil.xor(hashBytes, cipherData);

					//CommonUtil.hexDump(xorData, true);

					// Store the decrypted hash in the result array.
					int index = row * blockCountY + col;
					result[index] = xorData;
				}
			}
		}

		return result;
	}
}
