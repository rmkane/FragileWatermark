package util;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {
	public static int setLSB(int value, int bit) {
		return (value & 0xFFFFFFFE) | bit;
	}

	public static String byteToHex(byte b) {
		return Integer.toString((b & 0xff) + 0x100, 16).substring(1);
	}

	public static String toBin(int i) {
		return toBin(32, Integer.toBinaryString(i));
	}

	public static String toBin(byte b) {
		return toBin(8, Integer.toBinaryString(b & 0xff));
	}

	private static String toBin(int size, String bitString) {
		return String.format(String.format("%%%ds", size), bitString)
				.replace(' ', '0');
	}

	public static String hexDump(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (byte b : bytes) {
			sb.append(byteToHex(b)); // sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	public static String byteArrayToString(byte[] bytes) {
		return new String(bytes);
	}

	public static String getPixelARGB(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return String.format("argb(%3d, %3d, %3d, %3d)", alpha, red, green,
				blue);
	}

	public static String getPixelBinaryARGB(int pixel) {
		Color argb = new Color(pixel, true);
		byte a = (byte) argb.getAlpha();
		byte r = (byte) argb.getRed();
		byte g = (byte) argb.getGreen();
		byte b = (byte) argb.getBlue();

		return String.format("b[%s, %s, %s, %s]", CommonUtil.toBin(a),
				CommonUtil.toBin(r), CommonUtil.toBin(g), CommonUtil.toBin(b));
	}

	public static String hashStrMD5(String input) {
		return CommonUtil.hexDump(hashMD5(input));
	}

	public static byte[] hashMD5(byte[] bytes) {
		try {
			return MessageDigest.getInstance("MD5").digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] hashMD5(String input) {
		try {
			return hashMD5(input.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
