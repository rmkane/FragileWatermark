package watermark.gui.controller;

import java.awt.image.BufferedImage;
import java.security.PrivateKey;
import java.security.PublicKey;

import watermark.core.cipher.KeyCipher;
import watermark.core.service.WatermarkService;

/**
 * This is the controller logic for the main view. It handle encoding and
 * decoding watermarked images. The WatermarkService is called to perform the
 * encoding and decoding.
 *
 * @author Ryan M. Kane
 *
 */
public class MainViewContoller {
	private WatermarkService watermarkService;

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
		return watermarkService.encode(cipher, key, source, watermark, blockSize);
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
		return watermarkService.decode(cipher, key, source, watermark, blockSize);
	}
}
