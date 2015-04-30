package watermark.core.service;

import java.awt.image.BufferedImage;
import java.security.PrivateKey;
import java.security.PublicKey;

import watermark.core.cipher.KeyCipher;

public interface WatermarkService {
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
	BufferedImage encode(KeyCipher cipher, PublicKey key, BufferedImage source, BufferedImage watermark, int blockSize);

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
	BufferedImage decode(KeyCipher cipher, PrivateKey key, BufferedImage source, BufferedImage watermark, int blockSize);
}
