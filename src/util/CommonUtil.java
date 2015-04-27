package util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class includes static methods to common methods.
 *
 * @author Ryan M. Kane
 */
public class CommonUtil {
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
	 * Returns the hexadecimal representation of a byte.
	 *
	 * @param b - the byte to format as a hexadecimal String.
	 * @return the hexadecimal representation of the given byte.
	 */
	public static String byteToHex(byte b) {
		return Integer.toString((b & 0xff) + 0x100, 16).substring(1);
	}

	/**
	 * Returns the binary representation of an integer.
	 *
	 * @param i - the integer to format as a binary String.
	 * @return the binary representation of the given integer.
	 */
	public static String toBin(int i) {
		return toBin(32, Integer.toBinaryString(i));
	}

	/**
	 * Returns the binary representation of a byte.
	 *
	 * @param b - the byte to format as a binary String.
	 * @return the binary representation of the given byte.
	 */
	public static String toBin(byte b) {
		return toBin(8, Integer.toBinaryString(b & 0xff));
	}

	/**
	 *
	 *
	 * @param size
	 * @param bitString
	 * @return
	 */
	private static String toBin(int size, String bitString) {
		return String.format(String.format("%%%ds", size), bitString).replace(' ', '0');
	}

	/**
	 * Returns a String containing a printout of all the provided bytes in
	 * hexadecimal values.
	 *
	 * @param bytes - an array of bytes to print out in hexadecimal.
	 * @param addSpace - determine if spaces should be added between each byte.
	 * @return a dump of all bytes in hexadecimal.
	 */
	public static String hexDump(byte[] bytes, boolean addSpace) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(byteToHex(bytes[i])); // sb.append(String.format("%02x", b));

			if (addSpace && i < bytes.length - 1) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}

	/**
	 * A String to represent the input byte array.
	 *
	 * @param bytes - the bytes to convert to a String.
	 * @return a String to represent the input byte array.
	 */
	public static String byteArrayToString(byte[] bytes) {
		return new String(bytes);
	}

	/**
	 * Returns a String representing an ARGB tuple of the input pixel.
	 *
	 * @param pixel - the input pixel.
	 * @return a String representing an ARGB tuple of the input pixel.
	 */
	public static String getPixelARGB(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return String.format("argb(%3d, %3d, %3d, %3d)", alpha, red, green, blue);
	}

	/**
	 * Returns a String of an ARGB tuple in binary of the input pixel.
	 *
	 * @param pixel - the input pixel.
	 * @return a String of an ARGB tuple in binary of the input pixel.
	 */
	public static String getPixelBinaryARGB(int pixel) {
		Color argb = new Color(pixel, true);
		byte a = (byte) argb.getAlpha();
		byte r = (byte) argb.getRed();
		byte g = (byte) argb.getGreen();
		byte b = (byte) argb.getBlue();

		return String.format("b[%s, %s, %s, %s]", CommonUtil.toBin(a),
				CommonUtil.toBin(r), CommonUtil.toBin(g), CommonUtil.toBin(b));
	}

	/**
	 * Converts a primitive integer array to a byte array.
	 *
	 * @param data - the input integer array to be converted to a byte array.
	 * @return a byte array after being converted from an integer array.
	 */
	public static byte[] integersToBytes(int[] data) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(data);
		return byteBuffer.array();
	}

	/**
	 * This is the older version of the method above.
	 *
	 * @deprecated
	 */
	@Deprecated
	public static byte[] integersToBytesSlow(int[] values) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			for (int i = 0; i < values.length; ++i) {
				dos.writeInt(values[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return baos.toByteArray();
	}

	/**
	 * Hashes an input String using MD5.
	 *
	 * @param input - the String to be hashed.
	 * @return a String after being hashed using MD5.
	 */
	public static String hashStrMD5(String input) {
		return CommonUtil.hexDump(hashMD5(input),  false);
	}

	/**
	 * Hashes an input byte array using MD5.
	 *
	 * @param bytes - the bytes to be hashed.
	 * @return an array of hashed bytes using MD5.
	 */
	public static byte[] hashMD5(byte[] bytes) {
		try {
			return MessageDigest.getInstance("MD5").digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Hashes an input byte array using MD5.
	 *
	 * @param input - the String to be hashed.
	 * @return a byte array after being hashed using MD5.
	 */
	public static byte[] hashMD5(String input) {
		try {
			return hashMD5(input.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * XOR's two byte arrays.
	 *
	 * @param a the first array of bytes.
	 * @param b the second array of bytes.
	 * @return an array of bytes after being XORed.
	 */
	public static byte[] xor(byte[] a, byte[] b) {
		int length = Math.max(a.length, b.length);
		byte[] result = new byte[length];

		int i = 0;
		for (byte val : a) {
			result[i] = (byte) (val ^ b[i++]);
		}

		return result;
	}
}
