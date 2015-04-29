package util;

import buffer.BitBuffer;

/**
 * The following class handles bit manipulation of bytes and integers.
 *
 * @author Ryan M. Kane
 */
public class BitUtil {
	/**
	 * Sets the least significant bit of a pixel to the specified bit.
	 *
	 * @param value - the value of the pixel.
	 * @param bit - the new LSB value of either a 0 or 1.
	 * @return the new pixel value.
	 */
	public static int setLSB(int value, int bit) {
		return (value & 0xFFFFFFFE) | bit;
	}

	/**
	 * Sets the LBS of the source pixels and sets them to the destination.
	 *
	 * @param dest - the destination of the modified pixels.
	 * @param source - the source pixels.
	 * @param data - the bytes which store bit information.
	 */
	public static void setLSB(int[] dest, int[] source, byte[] data) {
		for (int i = 0; i < source.length; i++) {
			int cipherByte = data[i / 8];
			int bitPos = i % 8;
			int cipherBit = (cipherByte >>> (7 - bitPos) & 1);

			dest[i] = setLSB(source[i], cipherBit);
		}
	}

	/**
	 * Sets the LBS of all pixels in-place.
	 *
	 * @param source - the source pixels.
	 * @param data - the bytes which store bit information.
	 */
	public static void setLSB(int[] pixels, byte[] data) {
		setLSB(pixels, pixels, data);
	}

	/**
	 * Drops the LSB of all pixels by setting it to zero.
	 *
	 * @param pixels - - the source pixels.
	 */
	public static void dropLSB(int[] pixels) {
		setLSB(pixels, pixels, new byte[pixels.length / 8]);
	}

	/**
	 * Copies and sets the LBS of all pixels in a new array of bytes.
	 *
	 * @param pixels - the source pixels.
	 * @param data - the bytes which store bit information.
	 * @return
	 */
	public static int[] setAndCopyLSB(int[] pixels, byte[] data) {
		int[] dest = new int[pixels.length];
		setLSB(dest, pixels, data);
		return dest;
	}

	/**
	 * Extracts the least significant bit of each pixels and adds it to a
	 * bit-buffer. The bit buffer is then returned as an array of bytes.
	 *
	 * @param pixels - the pixels which LSB will be extracted from.
	 * @param bitsPerByte - the number of bits to store for each byte.
	 * @return an array of bytes which are the LSB from the pixels.
	 */
	public static byte[] extractLsbDynamic(final int[] pixels, final int bitsPerByte) {
		int bufferSize = (pixels.length + bitsPerByte - 1) / bitsPerByte;
		BitBuffer buffer = new BitBuffer(bufferSize, bitsPerByte);

		for (int i = 0; i < pixels.length; i++) {
			buffer.push((byte) pixels[i]);
		}

		return buffer.getBytes();
	}

	/**
	 * Extracts the least significant bit of each pixels and adds it to a
	 * bit-buffer. The bit buffer is then returned as an array of bytes.
	 *
	 * @param pixels - the pixels which LSB will be extracted from.
	 * @return an array of bytes which are the LSB from the pixels.
	 *
	 * @see CommonUtil#extractLsbDynamic(int[], int)
	 */
	public static byte[] extractLsbDynamic(final int[] pixels) {
		return extractLsbDynamic(pixels, BitBuffer.BITS_PER_BYTE);
	}

	/**
	 * Extracts the least significant bit of each pixels and adds it to a
	 * bit-buffer. The bit buffer is then returned as an array of bytes.
	 *
	 * @param pixels - the pixels which LSB will be extracted from.
	 * @param bufferSize - the size of the buffer in bytes.
	 * @return an array of bytes which are the LSB from the pixels.
	 */
	public static byte[] extractLsb(final int[] pixels, final int bufferSize) {
		BitBuffer buffer = new BitBuffer(bufferSize);

		for (int i = 0; i < pixels.length; i++) {
			buffer.push((byte) pixels[i]);
		}

		return buffer.getBytes();
	}
}
