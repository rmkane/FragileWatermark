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

				// Only watermark full image blocks.
				if (block.getWidth() == blockSize && block.getHeight() == blockSize) {
					handleEncodeBlock(cipher, key, block, watermarkMask, imgWidth, imgHeight);
				}
			}
		}

		return ImageUtil.recombine(blocks);
	}
	
	/**
	 * Handles encoding a watermark hash into the LSB of the designated image
	 * block. This modifies the image block in-place.
	 * 
	 * @param cipher - the cipher method for encoding.
	 * @param key - the public key.
	 * @param block - the current image block.
	 * @param watermark - the watermark mask to XOR with with block MD5 hash.
	 * @param imgWidth - the width of the whole image to be watermarked.
	 * @param imgHeight - the height of the whole image to be watermarked.
	 */
	private void handleEncodeBlock(KeyCipher cipher, PublicKey key, BufferedImage block, byte[] watermark, int imgWidth, int imgHeight) {
		int w = block.getWidth();
		int h = block.getHeight();
		int[] pixels = ImageUtil.getPixels(block);

		// Set LSB of each pixel to 0.
		BitUtil.dropLSB(pixels);
		block.setRGB(0, 0, block.getWidth(), block.getHeight(), pixels, 0, block.getWidth());

		byte[] params = new byte[] { (byte) imgWidth, (byte) imgHeight, (byte) block.getRGB(0, 0) };
		byte[] hashBytes = CommonUtil.hashMD5(params);
		byte[] xorBytes = CommonUtil.xor(hashBytes, watermark);
		byte[] cipherData = cipher.encrypt(xorBytes, key);

		// Set all the pixels' LSB to the cipher data bits.
		BitUtil.setLSB(pixels, cipherData);
		block.setRGB(0, 0, w, h, pixels, 0, w);
	}

	/**
	 * Decode a watermarked image.
	 *
	 * @param cipher - the cipher method for decoding.
	 * @param key - the private key.
	 * @param source - the image that is watermarked.
	 * @param watermark - the watermark to compare to the extracted hash.
	 * @param blockSize - image blocks pixel size.
	 * @return the XORed watermark hash.
	 */
	public BufferedImage handleDecode(KeyCipher cipher, PrivateKey key, BufferedImage source, BufferedImage watermark, int blockSize) {
		int imgWidth = source.getWidth();
		int imgHeight = source.getHeight();

		BufferedImage[][] blocks = ImageUtil.partitionImage(source, blockSize);

		int blockCountY = blocks.length;
		int blockCountX = blocks[0].length;

		int[] checksumData = new int[blockCountX * blockCountY];

		int[] watermarkPixels = ImageUtil.getPixels(watermark);
		byte[] watermarkBytes = CommonUtil.integersToBytes(watermarkPixels);
		byte[] watermarkMask = new byte[16];
		System.arraycopy(watermarkBytes, 0, watermarkMask, 0, watermarkMask.length);

		for (int row = 0; row < blocks.length; row++) {
			for (int col = 0; col < blocks[row].length; col++) {
				BufferedImage block = blocks[row][col];
				int index = row * blockCountY + col;

				// Only watermark full image blocks.
				if (block.getWidth() == blockSize && block.getHeight() == blockSize) {
					checksumData[index] = handleDecodeBlock(cipher, key, block, index, imgWidth, imgHeight);
				} else {
					checksumData[index] = 0x00000000;
				}
			}
		}

		int checksumWidth = (int) Math.ceil((double) source.getWidth() / blockSize);
		int checksumHeight = (int) Math.ceil((double) source.getHeight() / blockSize);
		
		BufferedImage checksumImage = new BufferedImage(checksumWidth, checksumHeight, BufferedImage.TYPE_INT_ARGB);
		checksumImage.setRGB(0, 0, checksumWidth, checksumHeight, checksumData, 0, checksumWidth);
		
		return checksumImage;
	}
	
	/**
	 * 
	 *@param cipher - the cipher method for decoding.
	 * @param key - the private key.
	 * @param block - the current watermarked image block.
	 * @param index - the current index for the image block.
	 * @param imgWidth - the width of the whole watermarked image.
	 * @param imgHeight - the height of the whole watermarked image.
	 * @return
	 */
	private int handleDecodeBlock(KeyCipher cipher, PrivateKey key, BufferedImage block, int index, int imgWidth, int imgHeight) {
		int w = block.getWidth();
		int h = block.getHeight();
		int[] pixels = ImageUtil.getPixels(block);
		byte[] lsbs = BitUtil.extractLsb(pixels, 128);
		
		try {
			byte[] cipherData = cipher.decrypt(lsbs, key); // Decryption Error...
			
			// Set LSB of each pixel to 0.
			BitUtil.dropLSB(pixels);
			block.setRGB(0, 0, w, h, pixels, 0, w);

			// Expected Hash
			byte[] params = new byte[] { (byte) imgWidth, (byte) imgHeight, (byte) block.getRGB(0, 0) };
			byte[] hashBytes = CommonUtil.hashMD5(params);
			byte[] xorData = CommonUtil.xor(hashBytes, cipherData);

			// Store the decrypted hash in the result array.
			return 0xFFFFFFFF;
		} catch (Exception e) {
			// Expecting a javax.crypto.BadPaddingException.
			return 0xFF000000;
		}				
	}
}
